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
import DAO.WorkTypeDAO;
import bean.WorkType;

public class WorkTypeServlet extends HttpServlet {

	
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		request.setCharacterEncoding("utf-8");
		response.setContentType("text/html;charset=UTF-8");
		byte mothod = Byte.parseByte(request.getParameter("method"));		
		switch (mothod) {
		case 1:
			maintainWorkType(request,response);
			break;
		case 2:
			getWorkTypes(request,response);
			break;
		default:
			break;
		}
	}
	
	
	private void getWorkTypes(HttpServletRequest request, HttpServletResponse response) throws IOException {
		//获取数据库连接，统一在这里获取连接，减少创建连接的次数
		Connection conn = DButil.getConnection();
		if (conn == null) {
			return;
		}
		
		List<WorkType> workTypes = WorkTypeDAO.getWorkTypes(conn);
		
		 //释放数据库连接
  		try {
  			conn.close();
  		} catch (SQLException e) {
  			e.printStackTrace();
  		}
		
		JSONObject json = new JSONObject();
		json.accumulate("workTypes", workTypes);
		
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
	private void maintainWorkType(HttpServletRequest request, HttpServletResponse response) throws IOException {
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
            WorkType workType = (WorkType) JSONObject.toBean(obj, WorkType.class);  
            WorkTypeDAO.addWorkType(conn,workType);
        }
        
        jsonArray = JSONArray.fromObject(deleted);  
        for (int i = 0; i < jsonArray.size(); i++) {  
            JSONObject obj = jsonArray.getJSONObject(i);  
            WorkType type = (WorkType) JSONObject.toBean(obj, WorkType.class);
            type.setDeleted(true);
            WorkTypeDAO.updateWorkType(conn,type);
        }

        jsonArray = JSONArray.fromObject(updated);  
        for (int i = 0; i < jsonArray.size(); i++) {  
            JSONObject obj = jsonArray.getJSONObject(i);  
            WorkType workType = (WorkType) JSONObject.toBean(obj, WorkType.class);  
            WorkTypeDAO.updateWorkType(conn,workType);
        }
        //释放数据库连接
  		try {
  			conn.close();
  		} catch (SQLException e) {
  			e.printStackTrace();
  		}
        
	}
}
