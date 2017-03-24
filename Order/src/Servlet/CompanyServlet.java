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

import DAO.DButil;
import DAO.WorkshopDAO;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import DAO.CompanyDAO;
import bean.Company;

public class CompanyServlet extends HttpServlet {

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
			getCompanys(request,response);
			break;
		case 2:
			maintainCompany(request,response);
			break;
		default:
			break;
		}
	}
	
	/**
	 * 获取所有公司列表
	 * @param request 
	 * @param response
	 */
	private void getCompanys(HttpServletRequest request, HttpServletResponse response)throws IOException {
		//获取数据库连接，统一在这里获取连接，减少创建连接的次数
		Connection conn = DButil.getConnection();
		if (conn == null) {
			return;
		}
		List<Company> companys = CompanyDAO.getCompanys(conn);
		
		//释放数据库连接
  		try {
  			conn.close();
  		} catch (SQLException e) {
  			e.printStackTrace();
  		}
		
		JSONObject json = new JSONObject();
		json.accumulate("total", companys.size());
		json.accumulate("rows", companys);
		
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
	private void maintainCompany(HttpServletRequest request, HttpServletResponse response)throws IOException {
		
		String inserted = request.getParameter("inserted");
		String deleted = request.getParameter("deleted");
		String updated = request.getParameter("updated");		
		
		//获取数据库连接，统一在这里获取连接，减少创建连接的次数
		Connection conn = DButil.getConnection();
		if (conn == null) {
			return;
		}
		
		JSONArray jsonArray = JSONArray.fromObject(inserted);  
        for (int i = 0; i < jsonArray.size(); i++) {  
            JSONObject obj = jsonArray.getJSONObject(i);  
            Company company = (Company) JSONObject.toBean(obj, Company.class);  
            CompanyDAO.addCompany(conn,company);
        }
        
        jsonArray = JSONArray.fromObject(deleted);  
        for (int i = 0; i < jsonArray.size(); i++) {  
            JSONObject obj = jsonArray.getJSONObject(i);  
            Company company = (Company) JSONObject.toBean(obj, Company.class); 
            int totals = WorkshopDAO.getWorkShopsByCompanyID(conn,company.getID());
            if(totals <= 0) {
	            company.setDeleted(true);
	            CompanyDAO.updateCompany(conn,company);
            }
        }

        jsonArray = JSONArray.fromObject(updated);  
        for (int i = 0; i < jsonArray.size(); i++) {  
            JSONObject obj = jsonArray.getJSONObject(i);  
            Company company = (Company) JSONObject.toBean(obj, Company.class);  
            CompanyDAO.updateCompany(conn,company);
        }
        
      //释放数据库连接
  		try {
  			conn.close();
  		} catch (SQLException e) {
  			e.printStackTrace();
  		}
	}
}
