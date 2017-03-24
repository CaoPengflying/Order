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
import DAO.CarteenDAO;
import DAO.DButil;
import bean.Carteen;

public class CarteenServlet extends HttpServlet {

	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		request.setCharacterEncoding("utf-8");
		response.setContentType("text/html;charset=UTF-8");
		byte mothod = Byte.parseByte(request.getParameter("method"));		
		switch (mothod) {
		case 1:
			maintainCarteen(request,response);
			break;
		case 2:
			getCarteens(request, response);
			break;
		}
	}
	
	/**
	 * 维护套餐价格，包括增删改(method=1)
	 * @param request 传递 增删改的json字符串：inserted，deleted，updated
	 * @param response
	 */
	private void maintainCarteen(HttpServletRequest request, HttpServletResponse response) throws IOException {
		
		
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
	            Carteen carteen = (Carteen) JSONObject.toBean(obj, Carteen.class);
	            CarteenDAO.addCarteen(conn,carteen);
	        }
		}
		
        if (json_deleted != null) {
        	JSONArray jsonArray = JSONArray.fromObject(json_deleted);  
            for (int i = 0; i < jsonArray.size(); i++) {  
                JSONObject obj = jsonArray.getJSONObject(i);  
                Carteen carteen = (Carteen) JSONObject.toBean(obj, Carteen.class); 
                CarteenDAO.delCarteen(conn,carteen.getID());
            }
		}
        
        if (json_updated != null) {
        	JSONArray jsonArray = JSONArray.fromObject(json_updated);  
            for (int i = 0; i < jsonArray.size(); i++) {  
                JSONObject obj = jsonArray.getJSONObject(i);  
                Carteen carteen = (Carteen) JSONObject.toBean(obj, Carteen.class);  
                CarteenDAO.updateCarteen(conn,carteen);
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
	 */
	private void getCarteens(HttpServletRequest request, HttpServletResponse response) throws IOException{
		//获取数据库连接，统一在这里获取连接，减少创建连接的次数
		Connection conn = DButil.getConnection();
		if (conn == null) {
			return;
		}
		
		List<Carteen> carteens = CarteenDAO.getCarteens(conn);
		
		//释放数据库连接
  		try {
  			conn.close();
  		} catch (SQLException e) {
  			e.printStackTrace();
  		}
		
		JSONObject json = new JSONObject();
		json.accumulate("rows", carteens);
		
		PrintWriter out = response.getWriter();
		out.print(json);
		out.flush();
		out.close();
	}
}
