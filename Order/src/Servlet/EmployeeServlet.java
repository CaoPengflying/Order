package Servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.ss.formula.functions.Now;

import com.sun.org.apache.bcel.internal.generic.NEW;

import util.ExcelUtil;
import util.ExportExcelForWorkshop;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import DAO.AdminDAO;
import DAO.DButil;
import DAO.DepartmentDAO;
import DAO.EmployeeDAO;
import DAO.OrderDAO;
import DAO.SignatureDAO;
import DAO.WorkTypeDAO;
import DAO.WorkshopDAO;
import View.EmployeeView;
import bean.Admin;
import bean.Employee;
import bean.OrderTotalOnEmployeeOfCompany;
import bean.WorkType;

public class EmployeeServlet extends HttpServlet {

	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		request.setCharacterEncoding("utf-8");
		response.setContentType("text/html;charset=UTF-8");
		byte mothod = Byte.parseByte(request.getParameter("method"));		
		switch (mothod) {
		case 1:
			maintainEmployee(request,response);
			break;
		case 3:
			getEmployees(request, response);
			break;
		case 4:
			updatePassword(request, response);
			break;
		case 6:
			updatePlace(request, response);
			break;
		case 7:
			getEmployee(request, response);
			break;
		case 11:
			exist(request, response);
			break;
		case 13:
			exit(request, response);
			break;
		case 14:
			exportExcel(request, response);
			break;
		case 16:
			setRoles(request, response);
		default:
			break;
		}
	}
	
	
	/**
	 * 维护职工信息，将职工的设为普通员工(method=16)
	 * @param request 传递 增删改的json字符串，updated
	 * @param response
	 */
	
	private void setRoles(HttpServletRequest request, HttpServletResponse response) throws IOException {
		
		String json_updated = request.getParameter("json_updated");
		byte role = Byte.parseByte(request.getParameter("role"));
		
		//获取数据库连接，统一在这里获取连接，减少创建连接的次数
		Connection conn = DButil.getConnection();
		if (conn == null) {
			return;
		}
		 if (json_updated != null) {
	        	JSONArray jsonArray = JSONArray.fromObject(json_updated);  
	            for (int i = 0; i < jsonArray.size(); i++) {  
	                JSONObject obj = jsonArray.getJSONObject(i);  
	                Employee employee = (Employee) JSONObject.toBean(obj, Employee.class);  
	                EmployeeDAO.setRoles(conn, employee, role);
	            }
			}       
		//释放数据库连接
  		try {
  			conn.close();
  		} catch (SQLException e) {
  			e.printStackTrace();
  		}
        PrintWriter out = response.getWriter();
		out.flush();
		out.close();
	}






	/**
	 * 维护职工信息，包括增删改(method=1)
	 * @param request 传递 增删改的json字符串：inserted，deleted，updated
	 * @param response
	 */
	private void maintainEmployee(HttpServletRequest request, HttpServletResponse response) throws IOException {
		
		
		String json_inserted = request.getParameter("json_inserted");
		String json_deleted = request.getParameter("json_deleted");
		String json_updated = request.getParameter("json_updated");
		String msg = "";
		
		//获取数据库连接，统一在这里获取连接，减少创建连接的次数
		Connection conn = DButil.getConnection();
		if (conn == null) {
			return;
		}
		
		if (json_inserted != null) {
			JSONArray jsonArray = JSONArray.fromObject(json_inserted);  
	        for (int i = 0; i < jsonArray.size(); i++) {  
	            JSONObject obj = jsonArray.getJSONObject(i);  
	            Employee employee = (Employee) JSONObject.toBean(obj, Employee.class);
	            
	            //如果用户不存在则添加
	            if(EmployeeDAO.getEmployee(conn, employee.getID()) == null){
		            WorkType type = WorkTypeDAO.getWorkType(conn,employee.getWorkTypeID());
		            //如果是管理员，则没有倒班类别
		            if(type != null){
			            employee.setLunch(type.getLunch());
			            employee.setDinner(type.getDinner());
			            employee.setMidnight(type.getMidnight());
		            }
		            EmployeeDAO.addEmployee(conn,employee);
	            }else {
					msg += String.format("用户号[%s]已经存在，添加失败！</br>", employee.getID());
				}
	        }
		}
		
        if (json_deleted != null) {
        	JSONArray jsonArray = JSONArray.fromObject(json_deleted);  
            for (int i = 0; i < jsonArray.size(); i++) {  
                JSONObject obj = jsonArray.getJSONObject(i);  
                Employee employee = (Employee) JSONObject.toBean(obj, Employee.class);  
                employee.setDeleted(true);
                EmployeeDAO.updateEmployee(conn,employee);
            }
		}
        
        if (json_updated != null) {
        	JSONArray jsonArray = JSONArray.fromObject(json_updated);  
            for (int i = 0; i < jsonArray.size(); i++) {  
                JSONObject obj = jsonArray.getJSONObject(i);  
                Employee employee = (Employee) JSONObject.toBean(obj, Employee.class);  
                EmployeeDAO.updateEmployee(conn,employee);
            }
		}
      //释放数据库连接
  		try {
  			conn.close();
  		} catch (SQLException e) {
  			e.printStackTrace();
  		}
        PrintWriter out = response.getWriter();
		out.print(msg);
		out.flush();
		out.close();
	}
	

	
	/**
	 * 获取录属于某公司且录属于某班组的职工信息(method=3)
	 * @param request 传递departmentID,companyID
	 * @param response 
	 */
	private void getEmployees(HttpServletRequest request, HttpServletResponse response) throws IOException{
		//获取数据库连接，统一在这里获取连接，减少创建连接的次数
		Connection conn = DButil.getConnection();
		if (conn == null) {
			return;
		}

		int page = 0;
		int rows = 0;
		boolean pagination = Boolean.parseBoolean(request.getParameter("pagination"));
		if (pagination) {
			page = Integer.parseInt(request.getParameter("page"));
			rows = Integer.parseInt(request.getParameter("rows"));
		}

		int companyID = Integer.parseInt(request.getParameter("companyID"));
		int workshopID = Integer.parseInt(request.getParameter("workshopID"));
		int departmentID = Integer.parseInt(request.getParameter("departmentID"));
		String idOrName = request.getParameter("idOrName");
		int role = Integer.parseInt(request.getParameter("role"));
		List<EmployeeView> employees = new ArrayList<EmployeeView>();
		int total = EmployeeDAO.getEmployees(conn,companyID,workshopID,departmentID,role,idOrName,page,rows,employees);
		
		JSONObject json = new JSONObject();
		json.accumulate("total", total);
		json.accumulate("rows", employees);
		
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
		//获取数据库连接，统一在这里获取连接，减少创建连接的次数
		Connection conn = DButil.getConnection();
		if (conn == null) {
			return;
		}
		
		String employeeID = request.getParameter("employeeID");
		// 原密码
		String password = request.getParameter("password");
		// 新密码
		String newPassword = request.getParameter("newPassword");

		
		JSONObject json = new JSONObject();

		if(request.getParameter("userType")==null){
			Employee employee = EmployeeDAO.getEmployee(conn,employeeID);
			if (employee.getPassword().equals(password)) {
				employee.setPassword(newPassword);
				EmployeeDAO.updateEmployee(conn,employee);
				json.accumulate("success", true);
				json.accumulate("message", "修改成功，请牢记密码");
			}else {
				json.accumulate("success", false);
				json.accumulate("message", "原密码不正确");
			}
		}else{
			Admin admin = AdminDAO.getAdmin(conn, employeeID);
			if (admin.getPassword().equals(password)) {
				admin.setPassword(newPassword);
				AdminDAO.updateAdmin(conn,admin);
				json.accumulate("success", true);
				json.accumulate("message", "修改成功，请牢记密码");
			}else {
				json.accumulate("success", false);
				json.accumulate("message", "原密码不正确");
			}
		}
		response.getWriter().println(json.toString());
	}
	

	/**
	 * 更新职工送餐点（method=6）
	 * @param request 传递employeeID,placeIDs
	 * @param response
	 */
	private void updatePlace(HttpServletRequest request, HttpServletResponse response) throws IOException{
		//获取数据库连接，统一在这里获取连接，减少创建连接的次数
		Connection conn = DButil.getConnection();
		if (conn == null) {
			return;
		}
		
		String employeeID = request.getParameter("employeeID");
		String placeIDs = request.getParameter("placeIDs");		
		
		//修改送餐点
		Employee employee = EmployeeDAO.getEmployee(conn,employeeID);		
		employee.setPlaceIDs(placeIDs);
		EmployeeDAO.updateEmployee(conn,employee);
	}
	
	/**
	 * 获取职工
	 * @param request 传递employeeID
	 * @param response
	 */
	private void getEmployee(HttpServletRequest request, HttpServletResponse response) throws IOException{
		//获取数据库连接，统一在这里获取连接，减少创建连接的次数
		Connection conn = DButil.getConnection();
		if (conn == null) {
			return;
		}
		
		String employeeID = request.getParameter("employeeID");
		Employee employee = EmployeeDAO.getEmployee(conn,employeeID);
		
		JSONObject json = new JSONObject();
		json.accumulate("employee", employee);
		
		PrintWriter out = response.getWriter();
		out.print(json);
		out.flush();
		out.close();
	}

	
	
	
	/**
	 * 判断职工号是否存在
	 * @param request 传递employeeID
	 * @param response 返回1-存在；0-不存在
	 * @throws IOException 
	 */
	private void exist(HttpServletRequest request, HttpServletResponse response) throws IOException{
		//获取数据库连接，统一在这里获取连接，减少创建连接的次数
		Connection conn = DButil.getConnection();
		if (conn == null) {
			return;
		}
		
		PrintWriter out = response.getWriter();
		
		String employeeID = request.getParameter("employeeID");
		Employee employee = EmployeeDAO.getEmployee(conn,employeeID);
		
		out.print(employee == null?0:1);
		out.flush();
		out.close();
	}
		
	
	
	private void exit(HttpServletRequest request, HttpServletResponse response)  throws ServletException, IOException{
		request.getSession().invalidate();// 销毁session所有相关信息
		PrintWriter out = response.getWriter();
		out.print("");
		out.flush();
		out.close();
	}
	
	/**
	 * 导出员工的剩余餐数目(method=14)
	 * @param request 传递 增删改的json字符串：inserted，deleted，updated
	 * @param response
	 */
	

	private void exportExcel(HttpServletRequest request,
			HttpServletResponse response) {
		// TODO Auto-generated method stub
		//获取数据库连接，统一在这里获取连接，减少创建连接的次数
		Connection conn = DButil.getConnection();
		if (conn == null) {
			return;
		}

		int workshopID = Integer.parseInt(request.getParameter("workshopID"));
		int departmentID = Integer.parseInt(request.getParameter("departmentID"));
		
		//List<EmployeeView> employees = EmployeeDAO.getEmployeeByWrokshopIDandDepartmentID(conn,workshopID,departmentID);
		List<EmployeeView> employees = new ArrayList<EmployeeView>();
		EmployeeDAO.getEmployees(conn,0,workshopID,departmentID,0,"",0,0,employees);					
						
		// JavaBean 中的字段名,Excel中的列名称 
		LinkedHashMap<String,String> fieldMap=new LinkedHashMap<String, String>();
		fieldMap.put("ID", "职工号");
		fieldMap.put("name", "姓名");
		fieldMap.put("department", "所属班组");
		fieldMap.put("lunch", "中餐剩余");
		fieldMap.put("dinner", "晚餐剩余");
		fieldMap.put("midnight", "零点餐剩余");
		        
        int []width = {16,16,60,20,20,20};
        
       //生成文件名     
        String departmentName = "";
        if(0 == departmentID){
        	departmentName = WorkshopDAO.getWorkshop(conn, workshopID).getName();
        }else {
			departmentName = DepartmentDAO.getDepartment(conn, departmentID).getName();
		}
        String fileName = String.format("%s剩余套餐统计表", departmentName);
       
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String dataRange = sdf.format(new Date());
        ExcelUtil.listToExcel(employees, fieldMap, width, fileName,dataRange, "", response);
	}
	
}
