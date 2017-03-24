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
import DAO.EmployeeDAO;
import DAO.WorkshopDAO;
import View.DepartmentView;
import View.EmployeeView;
import bean.Department;
import bean.Workshop;

public class DepartmentServlet extends HttpServlet {

	
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		request.setCharacterEncoding("utf-8");
		response.setContentType("text/html;charset=UTF-8");
		byte mothod = Byte.parseByte(request.getParameter("method"));		
		switch (mothod) {
		case 1:
			getDepartments(request,response);
			break;
		case 3:
			maintainDepartment(request,response);
			break;
		default:
			break;
		}
	}

	
	
	/**
	 * 获取车间所属班组列表
	 * @param request 传递车间号
	 * @param response
	 */
	private void getDepartments(HttpServletRequest request, HttpServletResponse response) throws IOException {
		
		
		short workshopID = Short.parseShort(request.getParameter("workshopID"));
		//获取数据库连接，统一在这里获取连接，减少创建连接的次数
		Connection conn = DButil.getConnection();
		if (conn == null) {
			return;
		}
		
		List<DepartmentView> departments = DepartmentDAO.getDepartments(conn,workshopID);
		
		//释放数据库连接
  		try {
  			conn.close();
  		} catch (SQLException e) {
  			e.printStackTrace();
  		}
		
		JSONObject json = new JSONObject();
		json.accumulate("total", departments.size());
		json.accumulate("rows", departments);
		
		PrintWriter out = response.getWriter();
		out.print(json);
		out.flush();
		out.close();
	}
	
	/**
	 * 维护公司信息，包括增删改
	 * @param request 传递name,phone,exception
	 * @param response
	 */
	private void maintainDepartment(HttpServletRequest request, HttpServletResponse response)throws IOException {
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
            Department department = (Department) JSONObject.toBean(obj, Department.class);  
            DepartmentDAO.addDepartment(conn,department);
        }
        
        jsonArray = JSONArray.fromObject(deleted);  
        for (int i = 0; i < jsonArray.size(); i++) {  
        	
            JSONObject obj = jsonArray.getJSONObject(i);  
            Department department = (Department) JSONObject.toBean(obj, Department.class);
            List<EmployeeView>employees = new ArrayList<EmployeeView>();
            EmployeeDAO.getEmployees(conn, 0, 0, department.getID(), 0, "", 0, 0, employees);
            if(employees.size() <= 0){
	            department.setDeleted(true);
	            DepartmentDAO.updateDepartment(conn,department);
            }
        }

        jsonArray = JSONArray.fromObject(updated);  
        for (int i = 0; i < jsonArray.size(); i++) {  
            JSONObject obj = jsonArray.getJSONObject(i);  
            Department department = (Department) JSONObject.toBean(obj, Department.class);  
            DepartmentDAO.updateDepartment(conn,department);
        }
	}
}
