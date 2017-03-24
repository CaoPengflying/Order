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

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import DAO.AdminDAO;
import DAO.DButil;
import bean.Admin;

public class AdminServlet extends HttpServlet {

	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		request.setCharacterEncoding("utf-8");
		response.setContentType("text/html;charset=UTF-8");
		byte mothod = Byte.parseByte(request.getParameter("method"));		
		switch (mothod) {
		case 1:
			maintainAdmin(request,response);
			break;
		case 2:
			getAdmins(request, response);
			break;
		case 3:
			updatePassword(request, response);
			break;
		}
	}
	
	/**
	 * 维护套餐价格，包括增删改(method=1)
	 * @param request 传递 增删改的json字符串：inserted，deleted，updated
	 * @param response
	 * @throws SQLException 
	 */
	private void maintainAdmin(HttpServletRequest request, HttpServletResponse response) throws IOException {
		
		String json_inserted = request.getParameter("json_inserted");
		String json_deleted = request.getParameter("json_deleted");
		String json_updated = request.getParameter("json_updated");
		
		//获取数据库连接，统一在这里获取连接，减少创建连接的次数
		Connection conn = DButil.getConnection();
		if (conn == null) {
			return;
		}
		if (json_inserted != null) {
			JSONArray jsonArray = JSONArray.fromObject(json_inserted);  
	        for (int i = 0; i < jsonArray.size(); i++) {  
	            JSONObject obj = jsonArray.getJSONObject(i);  
	            Admin admin = (Admin) JSONObject.toBean(obj, Admin.class);
	            AdminDAO.addAdmin(conn,admin);
	        }
		}
		
        if (json_deleted != null) {
        	JSONArray jsonArray = JSONArray.fromObject(json_deleted);  
            for (int i = 0; i < jsonArray.size(); i++) {  
                JSONObject obj = jsonArray.getJSONObject(i);  
                Admin admin = (Admin) JSONObject.toBean(obj, Admin.class); 
                AdminDAO.delAdmin(conn,admin.getID());
            }
		}
        
        if (json_updated != null) {
        	JSONArray jsonArray = JSONArray.fromObject(json_updated);  
            for (int i = 0; i < jsonArray.size(); i++) {  
                JSONObject obj = jsonArray.getJSONObject(i);  
                Admin admin = (Admin) JSONObject.toBean(obj, Admin.class);  
                AdminDAO.updateAdmin(conn,admin);
            }
		}
      //释放数据库连接
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 获取管理员列表
	 * @param request
	 * @param response
	 * @throws SQLException 
	 */
	private void getAdmins(HttpServletRequest request, HttpServletResponse response) throws IOException{
		//获取数据库连接，统一在这里获取连接，减少创建连接的次数
		Connection conn = DButil.getConnection();
		if (conn == null) {
			return;
		}
		
		List<Admin> admins = AdminDAO.getAdimins(conn);
		//释放数据库连接
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		JSONObject json = new JSONObject();
		json.accumulate("admins", admins);
		
		PrintWriter out = response.getWriter();
		out.print(json);
		out.flush();
		out.close();
	}

	/**
	 * 修改密码
	 * @param request 传递employeeID，password
	 * @param response
	 * @throws IOException
	 */
	private void updatePassword(HttpServletRequest request, HttpServletResponse response) throws IOException{
		String userID = request.getParameter("userID");
		// 原密码
		String password = request.getParameter("password");
		// 新密码
		String newPassword = request.getParameter("newPassword");
		
		JSONObject json = new JSONObject();

		//获取数据库连接，统一在这里获取连接，减少创建连接的次数
		Connection conn = DButil.getConnection();
		if (conn == null) {
			return;
		}
		
		Admin admin = AdminDAO.getAdmin(conn,userID);
		if (admin.getPassword().equals(password)) {
			admin.setPassword(newPassword);
			AdminDAO.updateAdmin(conn,admin);
			json.accumulate("success", true);
			json.accumulate("message", "修改成功，请牢记密码");
		}else {
			json.accumulate("success", false);
			json.accumulate("message", "原密码不正确");
		}
		//释放数据库连接
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		response.getWriter().println(json.toString());
	}	
}
