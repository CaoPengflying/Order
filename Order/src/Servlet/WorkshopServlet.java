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
import DAO.DepartmentDAO;
import DAO.WorkshopDAO;
import View.DepartmentView;
import View.WorkshopView;
import bean.Workshop;

public class WorkshopServlet extends HttpServlet {

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
			getWorkshops(request, response);
			break;
		case 2:
			maintainWorkshop(request,response);
			break;
		default:
			break;
		}
	}

	/**
	 * 获取车间列表
	 * @param request
	 * @param response
	 */
	private void getWorkshops(HttpServletRequest request, HttpServletResponse response) throws IOException {

		
		int page = 0;
		int rows = 0;
		boolean pagination = Boolean.parseBoolean(request.getParameter("pagination"));
		if (pagination) {
			page = Integer.parseInt(request.getParameter("page"));
			rows = Integer.parseInt(request.getParameter("rows"));
		}
		Short companyID = Short.parseShort(request.getParameter("companyID"));
		
		List<WorkshopView> workshops = new ArrayList<WorkshopView>();
		
		//获取数据库连接，统一在这里获取连接，减少创建连接的次数
		Connection conn = DButil.getConnection();
		if (conn == null) {
			return;
		}
		
		int total = WorkshopDAO.getWorkShops(conn,page,rows,workshops,companyID);
		
		 //释放数据库连接
  		try {
  			conn.close();
  		} catch (SQLException e) {
  			e.printStackTrace();
  		}
		
		
		JSONObject json = new JSONObject();
		json.accumulate("total", total);
		json.accumulate("rows", workshops);
		
		PrintWriter out = response.getWriter();
		out.print(json);
		out.flush();
		out.close();
	}
	
	/**
	 * 维护车间信息，包括增删改
	 * @param request 传递inserted,deleted,updated
	 * @param response
	 */
	private void maintainWorkshop(HttpServletRequest request, HttpServletResponse response)throws IOException {
		//获取数据库连接，统一在这里获取连接，减少创建连接的次数
		Connection conn = DButil.getConnection();
		if (conn == null) {
			return;
		}
		
		String inserted = request.getParameter("inserted");
		String deleted = request.getParameter("deleted");
		String updated = request.getParameter("updated");
		
		JSONArray jsonArray = JSONArray.fromObject(inserted);  
        for (int i = 0; i < jsonArray.size(); i++) {  
            JSONObject obj = jsonArray.getJSONObject(i);  
            Workshop workshop = (Workshop) JSONObject.toBean(obj, Workshop.class);  
            WorkshopDAO.addWorkshop(conn,workshop);
        }
        
        jsonArray = JSONArray.fromObject(deleted);  
        for (int i = 0; i < jsonArray.size(); i++) {  
            JSONObject obj = jsonArray.getJSONObject(i);  
            Workshop workshop = (Workshop) JSONObject.toBean(obj, Workshop.class); 
            List<DepartmentView>departments = DepartmentDAO.getDepartments(conn, workshop.getID());
            if(departments.size() <= 0) {
	            workshop.setDeleted(true);
	            WorkshopDAO.updateWorkshop(conn,workshop);
            }
        }

        jsonArray = JSONArray.fromObject(updated);  
        for (int i = 0; i < jsonArray.size(); i++) {  
            JSONObject obj = jsonArray.getJSONObject(i);  
            Workshop workshop = (Workshop) JSONObject.toBean(obj, Workshop.class);  
            WorkshopDAO.updateWorkshop(conn,workshop);
        }
	}
}
