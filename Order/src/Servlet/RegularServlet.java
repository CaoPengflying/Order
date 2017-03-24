package Servlet;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import DAO.DButil;
import DAO.RegularDAO;
import bean.Regular;

public class RegularServlet extends HttpServlet {

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
			updateRegular(request,response);
			break;
		default:
			break;
		}
	}

	/**
	 * 更新订餐规则
	 * @param request 传递days,lunch,dinner,midnight
	 * @param response
	 */
	private void updateRegular(HttpServletRequest request, HttpServletResponse response) throws IOException {
		
		
		int days = Integer.parseInt(request.getParameter("days"));
		String lunch = request.getParameter("lunch");
		String dinner = request.getParameter("dinner");
		String midnight = request.getParameter("midnight");
		Regular regular = new Regular();
		
		regular.setDays(days);
		regular.setLunch(lunch);
		regular.setDinner(dinner);
		regular.setMidnight(midnight);
		
		//获取数据库连接，统一在这里获取连接，减少创建连接的次数
		Connection conn = DButil.getConnection();
		if (conn == null) {
			return;
		}

		RegularDAO.updateRegular(conn,regular);
		 //释放数据库连接
  		try {
  			conn.close();
  		} catch (SQLException e) {
  			e.printStackTrace();
  		}
		
	}
}
