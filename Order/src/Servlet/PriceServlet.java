package Servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import DAO.DButil;
import DAO.PriceDAO;
import bean.Price;

public class PriceServlet extends HttpServlet {

	public static void main(String []args){
		SimpleDateFormat sdf = new SimpleDateFormat("E M d y");
		try {
			Date date = sdf.parse("Fri Jul 22 2016");
			sdf = new SimpleDateFormat("yy-MM-dd");
			String string = sdf.format(date);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		request.setCharacterEncoding("utf-8");
		response.setContentType("text/html;charset=UTF-8");
		byte mothod = Byte.parseByte(request.getParameter("method"));		
		switch (mothod) {
		case 1:
			maintainPrice(request,response);
			break;
		case 2:
			getPrices(request,response);
		default:
			break;
		}
	}

	private void getPrices(HttpServletRequest request, HttpServletResponse response)  throws IOException{
		//获取数据库连接，统一在这里获取连接，减少创建连接的次数
		Connection conn = DButil.getConnection();
		if (conn == null) {
			return;
		}
		
		List<Price> prices = PriceDAO.getPrices(conn);
		
		 //释放数据库连接
  		try {
  			conn.close();
  		} catch (SQLException e) {
  			e.printStackTrace();
  		}
		
		JSONObject json = new JSONObject();
		json.accumulate("total", prices.size());
		json.accumulate("rows", prices);
		
		PrintWriter out = response.getWriter();
		out.print(json);
		out.flush();
		out.close();
	}

	/**
	 * 维护套餐价格，包括增删改(method=1)
	 * @param request 传递 增删改的json字符串：inserted，deleted，updated
	 * @param response
	 */
	private void maintainPrice(HttpServletRequest request, HttpServletResponse response) throws IOException {
		
		
		String inserted = request.getParameter("inserted");
		String deleted = request.getParameter("deleted");
		String updated = request.getParameter("updated");
		
		JSONArray jsonArray = JSONArray.fromObject(inserted); 
		String tips = ""; 
		//获取数据库连接，统一在这里获取连接，减少创建连接的次数
		Connection conn = DButil.getConnection();
		if (conn == null) {
			return;
		}
		
        for (int i = 0; i < jsonArray.size(); i++) {  
            JSONObject obj = jsonArray.getJSONObject(i);  
            Price price = (Price) JSONObject.toBean(obj, Price.class); 
            
            //要确保新添加的价格和原来的所有价格在日期上没有重叠
            if (!PriceDAO.isOverlap(conn,price)) {
                PriceDAO.addPrice(conn,price);
                tips += String.format("价格添加成功：【%s - %s】</br>", price.getDate_start(),price.getDate_end());
			}else{
				tips += String.format("价格添加失败：【%s - %s】，时间有重叠！</br>", price.getDate_start(),price.getDate_end());
			}
        }
        
        jsonArray = JSONArray.fromObject(deleted);  
        for (int i = 0; i < jsonArray.size(); i++) {  
            JSONObject obj = jsonArray.getJSONObject(i);  
            Price price = (Price) JSONObject.toBean(obj, Price.class);  
            PriceDAO.delPrice(conn,price.getID());
            tips += String.format("价格删除成功：【%s - %s】</br>", price.getDate_start(),price.getDate_end());
        }

        jsonArray = JSONArray.fromObject(updated);  
        for (int i = 0; i < jsonArray.size(); i++) {  
            JSONObject obj = jsonArray.getJSONObject(i);  
            Price price = (Price) JSONObject.toBean(obj, Price.class);  
           
          //要确保更新的价格和原来的所有价格在日期上没有重叠
            if (!PriceDAO.isOverlap(conn,price)) {
            	 PriceDAO.updatePrice(conn,price);
                 tips += String.format("价格修改成功：【%s - %s】</br>", price.getDate_start(),price.getDate_end());
			}else{
				tips += String.format("价格修改失败：【%s - %s】，时间有重叠！</br>", price.getDate_start(),price.getDate_end());
			}
        }
        //释放数据库连接
  		try {
  			conn.close();
  		} catch (SQLException e) {
  			e.printStackTrace();
  		}

		PrintWriter out = response.getWriter();
		out.print(tips);
		out.flush();
		out.close();
	}
}
