package Servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import DAO.DButil;
import DAO.EmployeeDAO;
import DAO.PlaceDAO;
import DAO.RouteDAO;
import bean.Place;

public class PlaceServlet extends HttpServlet {

	
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		request.setCharacterEncoding("utf-8");
		response.setContentType("text/html;charset=UTF-8");
		byte mothod = Byte.parseByte(request.getParameter("method"));		
		switch (mothod) {
		case 1:
			maintainPlace(request,response);
			break;
		case 4:
			getAllPlaces(request, response);
			break;
		case 5:
			getEmployeePlaces(request, response);
			break;
		case 6:
			getRoutePlaces(request, response);
			break;
		case 7:
			getCarteenPlaces(request,response);
			break;
		default:
			break;
		}
	}

	


	/**
	 * 维护套餐价格，包括增删改(method=1)
	 * @param request 传递 增删改的json字符串：inserted，deleted，updated
	 * @param response
	 */
	private void maintainPlace(HttpServletRequest request, HttpServletResponse response) throws IOException {
		
		
		String inserted = request.getParameter("inserted");
		String deleted = request.getParameter("deleted");
		String updated = request.getParameter("updated");
		
		JSONArray jsonArray = JSONArray.fromObject(inserted);  
		
		//获取数据库连接，统一在这里获取连接，减少创建连接的次数
		Connection conn = DButil.getConnection();
		if (conn == null) {
			return;
		}
        for (int i = 0; i < jsonArray.size(); i++) {  
            JSONObject obj = jsonArray.getJSONObject(i);  
            Place place = (Place) JSONObject.toBean(obj, Place.class);  
            PlaceDAO.addPlace(conn,place);
        }
        
        jsonArray = JSONArray.fromObject(deleted);  
        for (int i = 0; i < jsonArray.size(); i++) {  
            JSONObject obj = jsonArray.getJSONObject(i);  
            Place place = (Place) JSONObject.toBean(obj, Place.class);  
            PlaceDAO.deletePlace(conn,place);
        }

        jsonArray = JSONArray.fromObject(updated);  
        for (int i = 0; i < jsonArray.size(); i++) {  
            JSONObject obj = jsonArray.getJSONObject(i);  
            Place place = (Place) JSONObject.toBean(obj, Place.class);  
            PlaceDAO.updatePlace(conn,place);
        }
        //释放数据库连接
  		try {
  			conn.close();
  		} catch (SQLException e) {
  			e.printStackTrace();
  		}
        
	}
	
	
	
 
	/**
	 * 获取所有送餐点列表
	 * @param request
	 * @param response
	 */
	private void getAllPlaces(HttpServletRequest request, HttpServletResponse response) throws IOException {
		

		int page = 0;
		int rows = 0;
		boolean pagination = Boolean.parseBoolean(request.getParameter("pagination"));
		
		if (pagination) {
			page = Integer.parseInt(request.getParameter("page"));
			rows = Integer.parseInt(request.getParameter("rows"));
		}
		
		List<Place> places = new ArrayList<Place>();
		//获取数据库连接，统一在这里获取连接，减少创建连接的次数
		Connection conn = DButil.getConnection();
		if (conn == null) {
			return;
		}

		int total = PlaceDAO.getPlaces(conn,page,rows,places);
		
		 //释放数据库连接
  		try {
  			conn.close();
  		} catch (SQLException e) {
  			e.printStackTrace();
  		}
		
		JSONObject json = new JSONObject();
		json.accumulate("total", total);
		json.accumulate("rows", places);
		
		PrintWriter out = response.getWriter();
		out.print(json);
		out.flush();
		out.close();
	}

	/**
	 * 获取职工送餐点列表
	 * @param request 传递 method=5,employeeID
	 * @param response 就餐列表
	 */
	private void getEmployeePlaces(HttpServletRequest request, HttpServletResponse response) throws IOException {
		String employeeID = request.getParameter("employeeID");
		
		//获取数据库连接，统一在这里获取连接，减少创建连接的次数
		Connection conn = DButil.getConnection();
		if (conn == null) {
			return;
		}
		
		String placeIDs = EmployeeDAO.getEmployee(conn,employeeID).getPlaceIDs();
		List<Place> places = PlaceDAO.getPlaces(conn,placeIDs);
		
		 //释放数据库连接
  		try {
  			conn.close();
  		} catch (SQLException e) {
  			e.printStackTrace();
  		}
		
		JSONObject json = new JSONObject();
		json.accumulate("places", places);
		
		PrintWriter out = response.getWriter();
		out.print(json);
		out.flush();
		out.close();
	}
	

	/**
	 * 获取食堂的所有就餐点列表
	 * @param request 传递 method =7,carteenID
	 * @param response
	 * @throws IOException 
	 */
	
	private void getCarteenPlaces(HttpServletRequest request,
			HttpServletResponse response) throws IOException {

				int carteenID = Integer.parseInt(request.getParameter("carteenID"));
				List<Place> places = new ArrayList<Place>();
				//获取数据库连接，统一在这里获取连接，减少创建连接的次数
				Connection conn = DButil.getConnection();
				if (conn == null) {
					return;
				}
				
				places = PlaceDAO.getPlacesOfCarteen(conn, carteenID);
				
				 //释放数据库连接
		  		try {
		  			conn.close();
		  		} catch (SQLException e) {
		  			e.printStackTrace();
		  		}
				
				JSONObject json = new JSONObject();
				json.accumulate("total", places.size());
				json.accumulate("rows", places);
				
				PrintWriter out = response.getWriter();
				out.print(json);
				out.flush();
				out.close();
	}
	
	/**
	 * 获取送餐线路送餐点列表
	 * @param request 传递 method=6,routeID
	 * @param response 就餐列表
	 */
	private void getRoutePlaces(HttpServletRequest request, HttpServletResponse response) throws IOException {
		
		
		int routeID = Integer.parseInt(request.getParameter("routeID"));
		//获取数据库连接，统一在这里获取连接，减少创建连接的次数
		Connection conn = DButil.getConnection();
		if (conn == null) {
			return;
		}
		String placeIDs = RouteDAO.getRoute(conn,routeID).getPlaceIDs();
		List<Place> places = PlaceDAO.getPlaces(conn,placeIDs);
		
		 //释放数据库连接
  		try {
  			conn.close();
  		} catch (SQLException e) {
  			e.printStackTrace();
  		}
		
		JSONObject json = new JSONObject();
		json.accumulate("places", places);
		
		PrintWriter out = response.getWriter();
		out.print(json);
		out.flush();
		out.close();
	}
}
