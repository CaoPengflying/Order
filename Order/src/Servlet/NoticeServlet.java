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
import DAO.NoticeDAO;
import View.NoticeView;
import bean.Notice;

public class NoticeServlet extends HttpServlet {

	
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		request.setCharacterEncoding("utf-8");
		response.setContentType("text/html;charset=UTF-8");
		byte mothod = Byte.parseByte(request.getParameter("method"));		
		switch (mothod) {
		case 1:
			getNotices(request, response);
			break;
		case 2:
			maintainNotice(request,response);
			break;
		}
		
	}



	private void getNotices(HttpServletRequest request,	HttpServletResponse response) throws IOException {
		int page = 0;
		int rows = 0;
		page = Integer.parseInt(request.getParameter("page"));
		rows = Integer.parseInt(request.getParameter("rows"));
		int carteenID = Integer.parseInt(request.getParameter("carteenID"));
		//获取数据库连接，统一在这里获取连接，减少创建连接的次数
		Connection conn = DButil.getConnection();
		if (conn == null) {
			return;
		}
		List<NoticeView> notices = new ArrayList<NoticeView>();
		int totals= NoticeDAO.getNotices(conn,page,rows,carteenID,notices);
		
		 //释放数据库连接
  		try {
  			conn.close();
  		} catch (SQLException e) {
  			e.printStackTrace();
  		}
		
		JSONObject json = new JSONObject();
		json.accumulate("total", totals);
		json.accumulate("rows", notices);
		
		PrintWriter out = response.getWriter();
		out.print(json);
		out.flush();
		out.close();
	}



	@SuppressWarnings("static-access")
	private void maintainNotice(HttpServletRequest request,	HttpServletResponse response) {
		
		
		String inserted = request.getParameter("inserted");
		String deleted = request.getParameter("deleted");
		String updated = request.getParameter("updated");
		
		
		//获取数据库连接，统一在这里获取连接，减少创建连接的次数
		Connection conn = DButil.getConnection();
		if (conn == null) {
			return;
		}
		if(inserted != null) {
            JSONObject obj = new JSONObject().fromObject(inserted);
            Notice notice = (Notice) JSONObject.toBean(obj, Notice.class);  
            NoticeDAO.addNotice(conn,notice);
        }
		if(deleted != null) {
            JSONObject obj = new JSONObject().fromObject(deleted);
            Notice notice = (Notice) JSONObject.toBean(obj, Notice.class); 
            NoticeDAO.delNotice(conn,notice.getId());
        }
		if(updated != null) {
            JSONObject obj = new JSONObject().fromObject(updated); 
            Notice notice = (Notice) JSONObject.toBean(obj, Notice.class);  
            NoticeDAO.updateNotice(conn,notice);
        }
        //释放数据库连接
  		try {
  			conn.close();
  		} catch (SQLException e) {
  			e.printStackTrace();
  		}
	}

}
