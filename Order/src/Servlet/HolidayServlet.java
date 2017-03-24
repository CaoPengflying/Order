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
import DAO.DButil;
import DAO.HolidayDAO;
import bean.Holiday;

public class HolidayServlet extends HttpServlet {


	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		request.setCharacterEncoding("utf-8");
		response.setContentType("text/html;charset=UTF-8");
		byte mothod = Byte.parseByte(request.getParameter("method"));		
		switch (mothod) {
		case 1:
			getHolidays(request,response);
			break;
		case 2:
			maintainHoliday(request,response);
			break;
		default:
			break;
		}
	}
	
	/**
	 * 获取所有节假日列表(method=1)
	 * @param request
	 * @param response
	 */
	private void getHolidays(HttpServletRequest request, HttpServletResponse response) throws IOException {
		//获取数据库连接，统一在这里获取连接，减少创建连接的次数
		Connection conn = DButil.getConnection();
		if (conn == null) {
			return;
		}
		
		List<Holiday> holidays = HolidayDAO.getHolidays(conn);
		
		 //释放数据库连接
  		try {
  			conn.close();
  		} catch (SQLException e) {
  			e.printStackTrace();
  		}
		
		JSONObject json = new JSONObject();
		json.accumulate("total", holidays.size());
		json.accumulate("rows", holidays);
		
		PrintWriter out = response.getWriter();
		out.print(json);
		out.flush();
		out.close();
	}
	
	/**
	 * 节假日维护，包括增删改(method=2)
	 * @param request 传递month,days
	 * @param response
	 */
	private void maintainHoliday(HttpServletRequest request, HttpServletResponse response) throws IOException {
		
		
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
            Holiday holiday = (Holiday) JSONObject.toBean(obj, Holiday.class);  
            HolidayDAO.addHoliday(conn,holiday);
        }
        
        jsonArray = JSONArray.fromObject(deleted);  
        for (int i = 0; i < jsonArray.size(); i++) {  
            JSONObject obj = jsonArray.getJSONObject(i);  
            Holiday holiday = (Holiday) JSONObject.toBean(obj, Holiday.class); 
            HolidayDAO.delHoliday(conn,holiday.getDate());
        }

        jsonArray = JSONArray.fromObject(updated);  
        for (int i = 0; i < jsonArray.size(); i++) {  
            JSONObject obj = jsonArray.getJSONObject(i);  
            Holiday holiday = (Holiday) JSONObject.toBean(obj, Holiday.class);  
            HolidayDAO.updateHoliday(conn,holiday);
        }
        
        //释放数据库连接
  		try {
  			conn.close();
  		} catch (SQLException e) {
  			e.printStackTrace();
  		}
	}
	
}
