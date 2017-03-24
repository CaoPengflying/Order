package Servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import DAO.AdminDAO;
import DAO.DButil;
import DAO.EmployeeDAO;
import bean.Admin;
import bean.Employee;

public class LoginServlet extends HttpServlet {

	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		request.setCharacterEncoding("utf-8");
		response.setContentType("text/html;charset=UTF-8");

		PrintWriter out = response.getWriter();

		String userId = request.getParameter("userId");
		String password = request.getParameter("password");
		byte role = Byte.parseByte(request.getParameter("role"));
		
		// 获取数据库连接，统一在这里获取连接，减少创建连接的次数
		Connection conn = DButil.getConnection();
		if (conn == null) {
			return;
		}
		
		JSONObject json = new JSONObject();//创建json对象		
		switch (role) {
		case 0:
			Employee employee = EmployeeDAO.getEmployee(conn, userId);
			if (employee == null || !employee.getPassword().equals(password)) {
				json.accumulate("success", false);
				json.accumulate("msg", "用户名或密码错误");
			} else {
				request.getSession().setAttribute("user", employee);//保存登录职工至session
				json.accumulate("success", true);
				json.accumulate("role", employee.getRole());
				json.accumulate("msg", "登录成功");
			}
			break;
		case 1:
			Admin admin = AdminDAO.getAdmin(conn, userId);
			if (admin == null || !admin.getPassword().equals(password)) {
				json.accumulate("success", false);
				json.accumulate("msg", "用户名或密码错误");
			} else {
				request.getSession().setAttribute("user", admin);//保存管理员至session
				json.accumulate("success", true);
				json.accumulate("role", admin.getRole());
				json.accumulate("msg", "登录成功");
			}
			break;
		default:
			json.accumulate("success", false);
			json.accumulate("msg", "用户类型错误");
		}
		
		 //释放数据库连接
  		try {
  			conn.close();
  		} catch (SQLException e) {
  			e.printStackTrace();
  		}
		
		out.print(json);
		out.flush();
		out.close();
	}

}
