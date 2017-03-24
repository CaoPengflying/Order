package Servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;
import DAO.DButil;
import DAO.RouteDAO;
import bean.Route;

public class RouteServlet extends HttpServlet {

	/**
	 * The doPost method of the servlet. <br>
	 *
	 * This method is called when a form has its tag value method equals to post.
	 * 
	 * @param request the request send by the client to the server
	 * @param response the response send by the server to the client
	 * @throws ServletException if an error occurred
	 * @throws IOException if an error occurred
	 */
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		request.setCharacterEncoding("utf-8");
		response.setContentType("text/html;charset=UTF-8");
		byte mothod = Byte.parseByte(request.getParameter("method"));		
		switch (mothod) {
		case 1:
			addRoute(request,response);
			break;
		case 2:
			delRoute(request,response);
			break;
		case 3:
			updateRoute(request,response);
			break;
		case 4:
			getRoutes(request, response);
			break;
		}
	}
	
	/**
	 * 添加送餐线路
	 * @param request 传递name
	 * @param response
	 */
	private void addRoute(HttpServletRequest request, HttpServletResponse response)throws IOException {
		
		
		String routeString = request.getParameter("route");
		
		//一次只能 
		JSONObject obj = JSONObject.fromObject(routeString);  
        Route route = (Route) JSONObject.toBean(obj, Route.class);  
            
      //获取数据库连接，统一在这里获取连接，减少创建连接的次数
  		Connection conn = DButil.getConnection();
  		if (conn == null) {
  			return;
  		}
        
        RouteDAO.addRoute(conn,route);   
        
        //释放数据库连接
  		try {
  			conn.close();
  		} catch (SQLException e) {
  			e.printStackTrace();
  		}
	}
	
	/**
	 * 删除送餐线路
	 * @param request 传递routeID
	 * @param response
	 */
	private void delRoute(HttpServletRequest request, HttpServletResponse response)throws IOException {
		
		
		String json_deleted = request.getParameter("route");
		JSONObject obj = JSONObject.fromObject(json_deleted);  
        
		Route route = (Route) JSONObject.toBean(obj, Route.class);  
		//获取数据库连接，统一在这里获取连接，减少创建连接的次数
		Connection conn = DButil.getConnection();
		if (conn == null) {
			return;
		}
		
        RouteDAO.delRoute(conn,route.getID()); 
        
      //释放数据库连接
  		try {
  			conn.close();
  		} catch (SQLException e) {
  			e.printStackTrace();
  		}
       
	}
	
	/**
	 * 更新送餐线路
	 * @param request 传递routeID,name
	 * @param response
	 */
	private void updateRoute(HttpServletRequest request, HttpServletResponse response)throws IOException {
		
		
		String routeString = request.getParameter("route");
		
		//一次只能 
		JSONObject obj = JSONObject.fromObject(routeString);  
        Route route = (Route) JSONObject.toBean(obj, Route.class);  
            
      //获取数据库连接，统一在这里获取连接，减少创建连接的次数
		Connection conn = DButil.getConnection();
		if (conn == null) {
			return;
		}
        
        RouteDAO.updateRoute(conn,route);    
        
        //释放数据库连接
  		try {
  			conn.close();
  		} catch (SQLException e) {
  			e.printStackTrace();
  		}
		
	}

	
	/**
	 * 获取所有送餐线路列表
	 * @param request
	 * @param response
	 */
	private void getRoutes(HttpServletRequest request, HttpServletResponse response)throws IOException {
		//获取数据库连接，统一在这里获取连接，减少创建连接的次数
		Connection conn = DButil.getConnection();
		if (conn == null) {
			return;
		}
		byte carteenID = Byte.parseByte(request.getParameter("carteenID"));
		List<Route> routes = RouteDAO.getRoutes(conn,carteenID);
		
		//释放数据库连接
  		try {
  			conn.close();
  		} catch (SQLException e) {
  			e.printStackTrace();
  		}
		
		
		JSONObject json = new JSONObject();
		json.accumulate("routes", routes);
		
		PrintWriter out = response.getWriter();
		out.print(json);
		out.flush();
		out.close();
	}
}
