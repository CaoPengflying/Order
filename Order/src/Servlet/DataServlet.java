package Servlet;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import DAO.DButil;
import DAO.DepartmentDAO;
import DAO.EmployeeDAO;
import DAO.OrderDAO;
import DAO.WorkTypeDAO;
import bean.Employee;
import bean.WorkType;

/**
 * 数据备份和恢复的处理逻辑
 * @author dell
 *
 */
public class DataServlet extends HttpServlet {

	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		request.setCharacterEncoding("utf-8");
		response.setContentType("text/html;charset=UTF-8");
		byte mothod = Byte.parseByte(request.getParameter("method"));
		switch (mothod) {
		case 1:
			backupData(request, response);
			break;
		case 2:
			importData(request, response);
			break;
		default:
			break;
		}
	}

	/**
	 * 数据备份（method=1）
	 * 
	 * @param request
	 *            传递data1，date2和deleteFlag
	 * @param response
	 * @throws IOException
	 */
	private void backupData(HttpServletRequest request,	HttpServletResponse response) throws IOException {
		
		String date1 = request.getParameter("date1");
		String date2 = request.getParameter("date2");
		Boolean deleteFlag = Boolean.parseBoolean(request.getParameter("deleteFlag"));
		
//		System.out.println("data1:"+date1);
//		System.out.println("data2:"+date2);
//		System.out.println("deleteFlag:"+deleteFlag);
		// 备份数据
		String filename = processBackup(date1, date2);
		//System.out.print("filename:"+filename);
		
		//获取数据库连接，统一在这里获取连接，减少创建连接的次数
		Connection conn = DButil.getConnection();
		if (conn == null) {
			return;
		}
		if (deleteFlag) {
			OrderDAO.delOrders(conn,date1, date2);
		}
		//释放数据库连接
  		try {
  			conn.close();
  		} catch (SQLException e) {
  			e.printStackTrace();
  		}
		//System.out.print("deleted");
		JSONObject json = new JSONObject();
		if (filename == null) {
			json.accumulate("success", false);
		} else {
			json.accumulate("success", true);

			String path = request.getContextPath();
			String basePath = request.getScheme() + "://"
					+ request.getServerName() + ":" + request.getServerPort()
					+ path + "/";
			String url = String.format("%sbackup/%s", basePath, filename);
			json.accumulate("url", url);
		}

		PrintWriter out = response.getWriter();
		out.print(json);
		out.flush();
		out.close();

	}

	private String processBackup(String date1, String date2) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String path = this.getServletContext().getRealPath("/backup");
		String filename = String.format("%s.sql", sdf.format(new Date()));
		String fullname = String.format("%s\\%s", path, filename);
		//System.out.println("fullname:"+fullname);
		// -t 只导出数据，而不添加CREATE TABLE 语句。
		// --add-drop-table:每个数据表创建之前添加drop数据表语句。默认为打开状态，使用--skip-add-drop-table取消选项
		// --skip-comments (取消注释)

		String sql = String.format("cmd /c mysqldump -uroot -proot order orders --skip-add-drop-table --skip-comments -t -w \"eatDate >= '%s' and eatDate <= '%s'\" > %s",	date1, date2, fullname);
		//System.out.println("sql:"+sql);
		Runtime cmd = Runtime.getRuntime();
		try {
			Process p = cmd.exec(sql);
			if (p.waitFor() == 0) {
				return filename;
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 数据导入
	 * 
	 * @param request
	 * @param response
	 * @throws IOException
	 */
	private void importData(HttpServletRequest request,	HttpServletResponse response) throws IOException {
		String sqldirFile = "";
		DiskFileItemFactory factory = new DiskFileItemFactory();
		ServletFileUpload sfu = new ServletFileUpload(factory);
		try {
			@SuppressWarnings("rawtypes")
			List fileItemList = sfu.parseRequest(request);
			@SuppressWarnings("rawtypes")
			Iterator it = fileItemList.iterator();
			if (it.hasNext()) {
				FileItem item = (FileItem) it.next();
				if (!item.isFormField()) {
					int index = item.getName().lastIndexOf("\\");
					File dir = new File(this.getServletContext().getRealPath("/backup/"));
					if (!dir.exists()) {
						dir.mkdirs();
					}
					File sqlFile = new File(dir, item.getName().substring(index + 1));
					item.write(sqlFile);
					sqldirFile = sqlFile.getAbsolutePath();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		boolean flag = processImport(sqldirFile);

		JSONObject json = new JSONObject();
		json.accumulate("success", flag);
		
		PrintWriter out = response.getWriter();
		out.print(json);
		out.flush();
		out.close();
	}

	private boolean processImport(String sqlFile) {
		String sql = "cmd /c mysql -uroot -proot order" + " < " + sqlFile;
		Runtime cmd = Runtime.getRuntime();

		try {
			Process p = cmd.exec(sql);
			return p.waitFor() == 0;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		} catch (InterruptedException e) {
			e.printStackTrace();
			return false;
		}
	}
}
