package Servlet;


import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import DAO.DButil;
import DAO.EmployeeDAO;

public class ResetServlet extends HttpServlet {
	private MyThread1 myThread1;

	/**
	 * Destruction of the servlet. <br>
	 */
	public void destroy() {
		if (myThread1 != null && myThread1.isInterrupted()) {  
		            myThread1.interrupt();  
		 }  

	}

	/**
	 * Initialization of the servlet. <br>
	 *
	 * @throws ServletException if an error occurs
	 */
	public void init() throws ServletException {
		 String str = null;  
		        if (str == null && myThread1 == null) {  
		            myThread1 = new MyThread1();  
		            myThread1.start(); // servlet 上下文初始化时启动 socket  
		        }  

	}

}

class MyThread1 extends Thread {	  
		private int year;//记录上一次获取的年份，如果年份改变了，则重置餐补数
    public void run() {  
		year = new Date().getYear();;
        while (!this.isInterrupted()) {// 线程未中断执行循环  
           try {  
               Thread.sleep(3600000);  //每小时判断一次
           } catch (InterruptedException e) {  
                e.printStackTrace();  
            }  
           
         //获取数据库连接，统一在这里获取连接，减少创建连接的次数
	   		Connection conn = DButil.getConnection();
	   		if (conn == null) {
	   			continue;
	   		}

            int year1 = new Date().getYear();
            if (year1 >= year) {
				year = year1;
				EmployeeDAO.reset(conn);
			}
            //释放数据库连接
      		try {
      			conn.close();
      		} catch (SQLException e) {
      			e.printStackTrace();
      		}
        }  
    }  
}  
