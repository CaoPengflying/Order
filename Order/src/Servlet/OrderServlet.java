package Servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import util.ExcelUtil;
import DAO.CarteenDAO;
import DAO.DButil;
import DAO.EmployeeDAO;
import DAO.HolidayDAO;
import DAO.OrderDAO;
import DAO.PlaceDAO;
import DAO.RegularDAO;
import DAO.SignatureDAO;
import View.OrderView;
import View.OrderViewSimple;
import bean.Carteen;
import bean.Employee;
import bean.Order;
import bean.OrderTotalOnDepartmentOfCompany;
import bean.OrderTotalOnDepartmentOfWorkshop;
import bean.OrderTotalOnEmployeeOfCompany;
import bean.OrderTotalOnEmployeeOfDepartment;
import bean.OrderTotalOnPlaceDepartmentOfPlace;
import bean.OrderTotalOnWorkshopOfCompany;
import bean.Place;
import bean.Price;

/**
 * 处理订单逻辑
 * @author Administrator
 *
 */
public class OrderServlet extends HttpServlet {

	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		request.setCharacterEncoding("utf-8");
		response.setContentType("text/html;charset=UTF-8");
		byte mothod = Byte.parseByte(request.getParameter("method"));		
		switch (mothod) {
		case 1:
			addOrder(request,response);
			break;
		case 2:
			delOrder(request, response);
			break;
		case 3:
			getOrdersOfEmployee(request, response);
			break;
		case 4:
			getTotalOfEmployee(request, response);
			break;
		case 5:
			getTotalOnEmployeeOfCompany(request, response);
			break;
		case 6:
			getTotalOnDepartmentOfWorkshop(request, response);
			break;
		case 7:
			getOrdersForPrepare(request, response);
			break;
		case 8:
			getTotalOfRoute(request,response);
			break;
		case 9:
			getTotalOnDepartmentOfCompany(request, response);
			break;
		case 10:
			exportEXcelOnDepartment(request, response);
			break;
		case 11:
			exportExcelOnEmployee(request, response);
			break;
		case 12:
			getOrdersOfDepartment(request, response);
			break;
		case 13:
			getFutureOrders(request,response);
			break;
		case 14:
			delSingleOrder(request, response);
			break;
		case 15:
			getTotalOnWorkshopOfCompany(request, response);
			break;
		case 16:
			exportExcelOnWorkshop(request, response);
			break;
		case 17:
			getOrdersOfWorkshop(request, response);
			break;
		case 18:
			SearchOrders(request, response);
			break;
			}
		
	}



	private void getOrdersOfWorkshop(HttpServletRequest request, HttpServletResponse response) throws IOException {
		
		
		int workshopID = Integer.parseInt(request.getParameter("workshopID"));
		String date1 = request.getParameter("date1");
		String date2 = request.getParameter("date2");
		int carteenID = Integer.parseInt(request.getParameter("carteenID"));
		byte type = Byte.parseByte(request.getParameter("type"));
		
		//获取数据库连接，统一在这里获取连接，减少创建连接的次数
		Connection conn = DButil.getConnection();
		if (conn == null) {
			return;
		}
		List<OrderView> orders = OrderDAO.getOrdersOfWorkshop(conn, workshopID, date1, date2,carteenID,type);
		 //释放数据库连接
  		try {
  			conn.close();
  		} catch (SQLException e) {
  			e.printStackTrace();
  		}
		
		JSONObject json = new JSONObject();
		json.accumulate("total", orders.size());
		json.accumulate("rows", orders);
		
		PrintWriter out = response.getWriter();
		out.print(json);
		out.flush();
		out.close();
	}



	private void exportExcelOnWorkshop(HttpServletRequest request, HttpServletResponse response) {
		
			
			int companyID = Integer.parseInt(request.getParameter("companyID"));
			String companyName = request.getParameter("companyName");
			String date1 = request.getParameter("date1");
			String date2 = request.getParameter("date2");
			int carteenID = Integer.parseInt(request.getParameter("carteenID"));
			//获取数据库连接，统一在这里获取连接，减少创建连接的次数
			Connection conn = DButil.getConnection();
			if (conn == null) {
				return;
			}
			
			List<OrderTotalOnWorkshopOfCompany> totals = OrderDAO.getTotalOnWorkshopOfCompany(conn,companyID, date1, date2, carteenID);			
					
			LinkedHashMap<String,String> fieldMap=new LinkedHashMap<String, String>();
			// JavaBean 中的字段名,Excel中的列名称 
			fieldMap.put("workshopID", "单位编码");
			fieldMap.put("workshopName", "单位名称");
			fieldMap.put("amountLunchNormal", "中餐数");
			fieldMap.put("moneyLunchNormal", "中餐企补");
			fieldMap.put("amountLunchHoliday", "假日中餐数");
			fieldMap.put("moneyLunchHoliday", "假日中餐企补");
			fieldMap.put("amountDinnerNormal", "晚餐数");
			fieldMap.put("moneyDinnerNormal", "晚餐企补");
			fieldMap.put("amountDinnerHoliday", "假日晚餐数");
			fieldMap.put("moneyDinnerHoliday", "假日晚餐企补");
			fieldMap.put("amountMidnightNormal", "零点餐数");
			fieldMap.put("moneyMidnightNormal", "零点餐企补");
			fieldMap.put("amountMidnightHoliday", "假日零点餐数");
			fieldMap.put("moneyMidnightHoliday", "假日零点餐企补");
			fieldMap.put("sum", "企补总额");
			
			int []width = {9,22,7,6,7,8,7,6,7,8,6,7,7,9,9};

			//生成表的标题和日期范围
	        String title = companyName+"结算统计表";
	        String dateRange = String.format("%s至%s", date1,date2);
	        String signature = SignatureDAO.getSignature(conn);
	        //释放数据库连接
	  		try {
	  			conn.close();
	  		} catch (SQLException e) {
	  			e.printStackTrace();
	  		}
	        
			ExcelUtil.listToExcel(totals, fieldMap,width, title,dateRange, signature, response);
		}
		



	private void getTotalOnWorkshopOfCompany(HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		
				
				int companyID = Integer.parseInt(request.getParameter("companyID"));
				String date1 = request.getParameter("date1");
				String date2 = request.getParameter("date2");
				int carteenID = Integer.parseInt(request.getParameter("carteenID"));
				int workshopID = Integer.parseInt(request.getParameter("workshopID"));
				int departmentID = Integer.parseInt(request.getParameter("departmentID"));
				//获取数据库连接，统一在这里获取连接，减少创建连接的次数
				Connection conn = DButil.getConnection();
				if (conn == null) {
					return;
				}
				
				//统计时生成价格和假日信息
				OrderDAO.generatePriceAndHoliday(conn,companyID,workshopID, departmentID, date1, date2);
				//汇总统计
				List<OrderTotalOnWorkshopOfCompany> totals = OrderDAO.getTotalOnWorkshopOfCompany(conn,companyID, date1, date2,carteenID);			
				
				 //释放数据库连接
		  		try {
		  			conn.close();
		  		} catch (SQLException e) {
		  			e.printStackTrace();
		  		}
				
				OrderTotalOnWorkshopOfCompany footer = new OrderTotalOnWorkshopOfCompany();
				for(OrderTotalOnWorkshopOfCompany total:totals){
					footer.setAmountDinnerHoliday(footer.getAmountDinnerHoliday()+total.getAmountDinnerHoliday());
					footer.setAmountDinnerNormal(footer.getAmountDinnerNormal()+total.getAmountDinnerNormal());
					footer.setAmountLunchHoliday(footer.getAmountLunchHoliday()+total.getAmountLunchHoliday());
					footer.setAmountLunchNormal(footer.getAmountLunchNormal()+total.getAmountLunchNormal());
					footer.setAmountMidnightHoliday(footer.getAmountMidnightHoliday()+total.getAmountMidnightHoliday());
					footer.setAmountMidnightNormal(footer.getAmountMidnightNormal()+total.getAmountMidnightNormal());
					footer.setMoneyDinnerHoliday(footer.getMoneyDinnerHoliday()+total.getMoneyDinnerHoliday());
					footer.setMoneyDinnerNormal(footer.getMoneyDinnerNormal()+total.getMoneyDinnerNormal());
					footer.setMoneyLunchHoliday(footer.getMoneyLunchHoliday()+total.getMoneyLunchHoliday());
					footer.setMoneyLunchNormal(footer.getMoneyLunchNormal()+total.getMoneyLunchNormal());
					footer.setMoneyMidnightHoliday(footer.getMoneyMidnightHoliday()+total.getMoneyMidnightHoliday());
					footer.setMoneyMidnightNormal(footer.getMoneyMidnightNormal()+total.getMoneyMidnightNormal());
					footer.setSum(footer.getSum()+total.getSum());
				}
				footer.setWorkshopName("合计");
				List<OrderTotalOnWorkshopOfCompany> footers = new ArrayList<OrderTotalOnWorkshopOfCompany>();
				footers.add(footer);
				
				JSONObject json = new JSONObject();
				json.accumulate("total", totals.size());
				json.accumulate("rows", totals);
				json.accumulate("footer", footers);
				
				
				
				PrintWriter out = response.getWriter();
				out.print(json);
				out.flush();
				out.close();
			}
			
			private void getTotalOnDepartmentOfWorkshop(HttpServletRequest request,	HttpServletResponse response) throws IOException {
				
				
				int workshopID = Integer.parseInt(request.getParameter("workshopID"));
				String date1 = request.getParameter("date1");
				String date2 = request.getParameter("date2");
				
				//获取数据库连接，统一在这里获取连接，减少创建连接的次数
				Connection conn = DButil.getConnection();
				if (conn == null) {
					return;
				}
				
				//汇总统计
				List<OrderTotalOnDepartmentOfWorkshop> totals = OrderDAO.getTotalOnDepartmentOfWorkshop(conn,workshopID, date1, date2);			
				
				 //释放数据库连接
		  		try {
		  			conn.close();
		  		} catch (SQLException e) {
		  			e.printStackTrace();
		  		}
				
				OrderTotalOnDepartmentOfWorkshop footer = new OrderTotalOnDepartmentOfWorkshop();
				for(OrderTotalOnDepartmentOfWorkshop total:totals){
					footer.setDinner(footer.getDinner()+total.getDinner());
					footer.setLunch(footer.getLunch()+total.getLunch());
					footer.setMidnight(footer.getMidnight()+total.getMidnight());
				}
				footer.setDepartmentName("合计");
				List<OrderTotalOnDepartmentOfWorkshop> footers = new ArrayList<OrderTotalOnDepartmentOfWorkshop>();
				footers.add(footer);
				
				JSONObject json = new JSONObject();
				json.accumulate("total", totals.size());
				json.accumulate("rows", totals);
				json.accumulate("footer", footers);
				
				PrintWriter out = response.getWriter();
				out.print(json);
				out.flush();
				out.close();
			}



	/**
	 * @param request
	 * @param response
	 * 个人订单按职工(有职工号,职工姓名)汇总
	 */
	private void exportExcelOnEmployee(HttpServletRequest request,
			HttpServletResponse response) {
		
		
		//获取参数
		int companyID = Integer.parseInt(request.getParameter("companyID"));
		String companyName = request.getParameter("companyName");
		String date1 = request.getParameter("date1");
		String date2 = request.getParameter("date2");
		int carteenID = Integer.parseInt(request.getParameter("carteenID"));
		int workshopID = Integer.parseInt(request.getParameter("workshopID"));
		int departmentID = Integer.parseInt(request.getParameter("departmentID"));
		//获取数据库连接，统一在这里获取连接，减少创建连接的次数
		Connection conn = DButil.getConnection();
		if (conn == null) {
			return;
		}
		
		//从数据库中获取统计数据
		List<OrderTotalOnEmployeeOfCompany> totals = OrderDAO.getTotalOnEmployeeOfCompany(conn,companyID,workshopID,departmentID, date1, date2, carteenID);			
						
		// JavaBean 中的字段名,Excel中的列名称 
		LinkedHashMap<String,String> fieldMap=new LinkedHashMap<String, String>();
		fieldMap.put("departmentName", "单位名称");
		fieldMap.put("employeeID", "职工号");
		fieldMap.put("name", "姓名");
		fieldMap.put("amountLunchNormal", "中餐数");
		fieldMap.put("moneyLunchNormal", "中餐企补");
		fieldMap.put("amountLunchHoliday", "假日中餐数");
		fieldMap.put("moneyLunchHoliday", "假日中餐企补");
		fieldMap.put("amountDinnerNormal", "晚餐数");
        fieldMap.put("moneyDinnerNormal", "晚餐企补");
        fieldMap.put("amountDinnerHoliday", "假日晚餐数");
        fieldMap.put("moneyDinnerHoliday", "假日晚餐企补");
        fieldMap.put("amountMidnightNormal", "零点餐数");
        fieldMap.put("moneyMidnightNormal", "零点餐企补");
        fieldMap.put("amountMidnightHoliday", "假日零点餐数");
        fieldMap.put("moneyMidnightHoliday", "假日零点餐企补");
        fieldMap.put("sum", "企补总额");
		        
        int []width = {21,8,7,6,6,7,7,6,6,7,7,6,7,8};
        
        //生成表的标题和日期范围
        String title = companyName+"结算明细统计表";
        String dateRange = String.format("%s至%s", date1,date2);
        String signature = SignatureDAO.getSignature(conn);
        
        //释放数据库连接
  		try {
  			conn.close();
  		} catch (SQLException e) {
  			e.printStackTrace();
  		}
        
        ExcelUtil.listToExcel(totals, fieldMap, width, title,dateRange, signature, response);
	}
	
	/**
	 * @param request
	 * @param response
	 * 个人订单按班组(不带职工号,姓名)汇总
	 */
	private void exportEXcelOnDepartment(HttpServletRequest request,HttpServletResponse response) {
		
		
		int companyID = Integer.parseInt(request.getParameter("companyID"));
		String companyName = request.getParameter("companyName");
		String date1 = request.getParameter("date1");
		String date2 = request.getParameter("date2");
		int carteenID = Integer.parseInt(request.getParameter("carteenID"));
		int workshopID = Integer.parseInt(request.getParameter("workshopID"));
		//获取数据库连接，统一在这里获取连接，减少创建连接的次数
		Connection conn = DButil.getConnection();
		if (conn == null) {
			return;
		}
		
		
		List<OrderTotalOnDepartmentOfCompany> totals = OrderDAO.getTotalOnDepartmentOfCompany(conn,companyID,workshopID, date1, date2, carteenID);			
				
		LinkedHashMap<String,String> fieldMap=new LinkedHashMap<String, String>();
		// JavaBean 中的字段名,Excel中的列名称 
		fieldMap.put("departmentID", "单位编码");
		fieldMap.put("departmentName", "单位名称");
		fieldMap.put("amountLunchNormal", "中餐数");
		fieldMap.put("moneyLunchNormal", "中餐企补");
		fieldMap.put("amountLunchHoliday", "假日中餐数");
		fieldMap.put("moneyLunchHoliday", "假日中餐企补");
		fieldMap.put("amountDinnerNormal", "晚餐数");
		fieldMap.put("moneyDinnerNormal", "晚餐企补");
		fieldMap.put("amountDinnerHoliday", "假日晚餐数");
		fieldMap.put("moneyDinnerHoliday", "假日晚餐企补");
		fieldMap.put("amountMidnightNormal", "零点餐数");
		fieldMap.put("moneyMidnightNormal", "零点餐企补");
		fieldMap.put("amountMidnightHoliday", "假日零点餐数");
		fieldMap.put("moneyMidnightHoliday", "假日零点餐企补");
		fieldMap.put("sum", "企补总额");
		
		int []width = {9,22,7,6,7,8,7,6,7,8,6,7,7,9,9};

		//生成表的标题和日期范围
        String title = companyName+"结算统计表";
        String dateRange = String.format("%s至%s", date1,date2);
        String signature = SignatureDAO.getSignature(conn);
        
        
        //释放数据库连接
  		try {
  			conn.close();
  		} catch (SQLException e) {
  			e.printStackTrace();
  		}
        
		ExcelUtil.listToExcel(totals, fieldMap,width, title,dateRange, signature, response);
	}
	/**
	 * 添加订单
	 * @param request 传递eaterID,eatDate,ordererID,type,placeIDs
	 * @param response 返回每个订单的执行反馈信息
	 */
	private void addOrder(HttpServletRequest request, HttpServletResponse response)throws IOException {
		
		
		//获取传递的参数
		String []eaterIDs = request.getParameter("eaterIDs").split(",");
		String ordererID = request.getParameter("ordererID");
		byte type = Byte.parseByte(request.getParameter("type"));	
		String additional = request.getParameter("additional");
		String []placeIDs = request.getParameter("placeIDs").split(",");
		String eatDate = request.getParameter("eatDate");
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String orderDate = sdf.format(new Date());
		String tips = "";	
		String placeID = request.getParameter("placeID");
		
		Connection conn = DButil.getConnection();
		if (conn == null) {
			return ;
		}
		//如果订餐时间截止，则不能订
		if(RegularDAO.isExpire(conn,eatDate,type)){
			tips += String.format("订餐失败！【%s，%s】，订餐时间已截止</br>", eatDate,Order.getTypeString(type));
		}else {
			List<Employee> employees = EmployeeDAO.getEmployees(conn,eaterIDs);
			List<Order> orders = new ArrayList<Order>();
			
			//逐个添加每一条订单，同时把添加订单的结果记录下来以用于向前端反馈
			for(int i=0; i<employees.size(); i++){	
				Employee e = employees.get(i);
				
				if(e.getLock() == 0) {
					switch (type) {
					case Price.LUNCH:
						if((e.getLunch()  > 0 || e.getDinner() > 0)) {
							if (OrderDAO.isExist(conn,e.getID(),eatDate,type)) {			
								tips += String.format("订餐失败！【%s，%s】，不能重复订餐</br>", e.getName(),Order.getTypeString(type));
							}else if(placeID.equals("0")){
								pushOrder(orders,e,eatDate, ordererID, orderDate, type, additional, Short.parseShort(placeIDs[i]));
								}else {
									pushOrder(orders,e,eatDate, ordererID, orderDate, type, additional, Short.parseShort(placeID));
								}
						}else {
							tips += String.format("订餐失败！【%s，%s】，餐数已用完</br>", e.getName(),Order.getTypeString(type));
						}
						break;
					case Price.DINNER:
						if(e.getLunch()  > 0 || e.getDinner() > 0) {
							if (OrderDAO.isExist(conn,e.getID(),eatDate,type)) {			
								tips += String.format("订餐失败！【%s，%s】，不能重复订餐</br>", e.getName(),Order.getTypeString(type));
							}else if(placeID.equals("0")){
								pushOrder(orders,e,eatDate, ordererID, orderDate, type, additional, Short.parseShort(placeIDs[i]));
								}else {
									pushOrder(orders,e,eatDate, ordererID, orderDate, type, additional, Short.parseShort(placeID));
								}
						}else {
							tips += String.format("订餐失败！【%s，%s】，餐数已用完</br>", e.getName(),Order.getTypeString(type));
						}
						break;
					case Price.MIDNIGHT:
						if(e.getMidnight() > 0) {
							if (OrderDAO.isExist(conn,e.getID(),eatDate,type)) {			
								tips += String.format("订餐失败！【%s，%s】，不能重复订餐</br>", e.getName(),Order.getTypeString(type));
							}else if(placeID.equals("0")){
								pushOrder(orders,e,eatDate, ordererID, orderDate, type, additional, Short.parseShort(placeIDs[i]));
								}else {
									pushOrder(orders,e,eatDate, ordererID, orderDate, type, additional, Short.parseShort(placeID));
								}
						}else {
							tips += String.format("订餐失败！【%s，%s】，餐数已用完</br>", e.getName(),Order.getTypeString(type));
						}
						break;
					}
				}
				else {
					tips += String.format("订餐失败！【%s】，已锁定</br>", e.getName());
				}
			}
			tips = String.format("成功订餐【%d】份</br>%s",orders.size(),tips);
			OrderDAO.addOrders(conn,orders);
			EmployeeDAO.updateMeal(conn,employees);
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



	private void pushOrder(List<Order> orders, Employee employee, String eatDate,String ordererID, String orderDate, byte type, String additional,	short placeID) {
		Order order = new Order();
		order.setEatDate(eatDate);//设置用餐日期
		order.setEaterID(employee.getID());//设置用餐人
		order.setOrderDate(orderDate);//设置订餐日期
		order.setOrdererID(ordererID);//设置订餐人
		order.setPlaceID(placeID);//设置送餐点
		order.setType(type);//设置套餐类别
		order.setDepartmentID(employee.getDepartmentID());//设置结算单位为现在的工作班组
		if (order.getType() == Price.MIDNIGHT) {
			order.setAdditional(additional);//设置零点餐附属信息
		}
		
		orders.add(order);
		deductMeal(employee, type);
	}

	
	/**
	 * 扣除套餐数
	 * @param employee
	 * @param type
	 */
	private void deductMeal(Employee employee, byte type){
		switch (type) {
		case Price.LUNCH:
			if (employee.getLunch() <= 0) {
				employee.setDinner((short) (employee.getDinner()-1));
			}else{
				employee.setLunch((short) (employee.getLunch()-1));
			}
			break;
		case Price.DINNER:
			if (employee.getDinner() <= 0) {
				employee.setLunch((short) (employee.getLunch()-1));
			}else{
				employee.setDinner((short) (employee.getDinner()-1));
			}
			break;
		case Price.MIDNIGHT:
			employee.setMidnight((short) (employee.getMidnight()-1));
		}
	}

	/**
	 * 增加套餐数
	 * @param employee
	 * @param type
	 */
	private void addMeal(Employee employee, byte type) {
		//修改餐补数
				switch (type) {
				case Price.LUNCH:
					employee.setLunch((short) (employee.getLunch()+1));
					break;
				case Price.DINNER:
					employee.setDinner((short) (employee.getDinner()+1));
					break;
				case Price.MIDNIGHT:
					employee.setMidnight((short) (employee.getMidnight()+1));
					break;

				default:
					break;
				}
	}
	
	/**
	 * 删除订单（批量）
	 * @param request 传递orderIDs
	 * @param response
	 * @throws SQLException 
	 */
	private void delOrder(HttpServletRequest request, HttpServletResponse response)throws IOException {
		
		
		String json_deleted = request.getParameter("json_deleted");
		String operator = request.getParameter("operator");
        
		JSONArray jsonArray = JSONArray.fromObject(json_deleted); 
		List<OrderView>orders = (List<OrderView>)jsonArray.toList(jsonArray, OrderView.class);
		String []eaterIDs = new String[orders.size()];
		for(int i=0; i<orders.size(); i++){
			eaterIDs[i] = orders.get(i).getEaterID();
		}
		String tips = "";
		//获取数据库连接，统一在这里获取连接，减少创建连接的次数
		Connection conn = DButil.getConnection();
		if (conn == null) {
			return;
		}
		
		List<Employee>employees = EmployeeDAO.getEmployees(conn, eaterIDs);
		
		List<OrderView>successDel = new ArrayList<OrderView>();
		
		//如果订餐时间截止，则不能订
		if(RegularDAO.isExpire(conn,orders.get(0).getEatDate(),orders.get(0).getType())){
			tips = "退订失败！退餐时间以截止！</br>";
		}else{
			for (int i = 0; i < orders.size(); i++) {
				OrderView order = orders.get(i);
				//只有订餐人或者本人才能取消
				if (!order.getOrdererID().equals(operator) && !order.getEaterID().equals(operator)) {
					tips += String.format("退订失败！【%s，%s，%s】，只有订餐人或者本人才能取消</br>", order.getEatDate(),order.getEaterName(),Order.getTypeString(order.getType()));
				}else {
					successDel.add(order);
					addMeal(employees.get(i),order.getType());
				}
			}
		}
		
		OrderDAO.deleteOrders(conn,successDel);
		EmployeeDAO.updateMeal(conn,employees);
		
		  //释放数据库连接
  		try {
  			conn.close();
  		} catch (SQLException e) {
  			e.printStackTrace();
  		}
		
		tips = String.format("成功退订【%d】份</br>%s",successDel.size(),tips);
		
		
		PrintWriter out = response.getWriter();
		out.print(tips);
		out.flush();
		out.close();

	}
	
	/**
	 * 删除订单(单个）
	 * @param request 传递orderIDs
	 * @param response
	 */
	private void delSingleOrder(HttpServletRequest request, HttpServletResponse response)throws IOException {

		
		long orderID = Long.parseLong(request.getParameter("orderID"));
		String operator = request.getParameter("operator");
        String tip = "";
		//获取数据库连接，统一在这里获取连接，减少创建连接的次数
		Connection conn = DButil.getConnection();
		if (conn == null) {
			return;
		}
        
		OrderView order = OrderDAO.getOrderView(conn, orderID);
		//如果订餐时间截止，则不能订
		if(RegularDAO.isExpire(conn,order.getEatDate(),order.getType())){
			tip = String.format("退订失败！【%s，%s，%s】，订餐时间已截止</br>", order.getEatDate(),order.getEaterName(),Order.getTypeString(order.getType()));
		}
		
		//只有订餐人或者本人才能取消
		if (!order.getOrdererID().equals(operator) && !order.getEaterID().equals(operator)) {
			tip = String.format("退订失败！【%s，%s，%s】，只有订餐人或者本人才能取消</br>", order.getEatDate(),order.getEaterName(),Order.getTypeString(order.getType()));
		}
		
		//退订
		OrderDAO.delOrder(conn,order.getID());
		
		//修改餐补数
		Employee employee = EmployeeDAO.getEmployee(conn,order.getEaterID());
		switch (order.getType()) {
		case Price.LUNCH:
			employee.setLunch((short) (employee.getLunch()+1));
			break;
		case Price.DINNER:
			employee.setDinner((short) (employee.getDinner()+1));
			break;
		case Price.MIDNIGHT:
			employee.setMidnight((short) (employee.getMidnight()+1));
			break;

		default:
			break;
		}
		EmployeeDAO.updateEmployee(conn,employee);
		  //释放数据库连接
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		tip = String.format("退订成功！【%s，%s，%s】</br>", order.getEatDate(),order.getEaterName(),Order.getTypeString(order.getType()));    
		
		PrintWriter out = response.getWriter();
		out.print(tip);
		out.flush();
		out.close();
	}
	
	




	/**
	 * 获取个人订单列表
	 * @param request 传递employeeID,type,date1,date2
	 * @param response
	 */
	private void getOrdersOfEmployee(HttpServletRequest request, HttpServletResponse response)throws IOException {
		
		
		String employeeIDs = request.getParameter("employeeIDs");
		byte type = Byte.parseByte(request.getParameter("type"));		
		String date1 = request.getParameter("date1");
		String	date2 = request.getParameter("date2");
		int carteenID = Integer.parseInt(request.getParameter("carteenID")); 
					
		//获取数据库连接，统一在这里获取连接，减少创建连接的次数
		Connection conn = DButil.getConnection();
		if (conn == null) {
			return;
		}

		List<OrderViewSimple> orders = OrderDAO.getOrders(conn,employeeIDs, type, date1, date2,carteenID);
		
		  //释放数据库连接
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		JSONObject json = new JSONObject();
		json.accumulate("total", orders.size());
		json.accumulate("rows", orders);
		
		PrintWriter out = response.getWriter();
		out.print(json);
		out.flush();
		out.close();
	}
	
	/**
	 * 获取个人订单列表
	 * @param request 传递employeeID,type,date1,date2
	 * @param response
	 */
	private void getOrdersOfDepartment(HttpServletRequest request, HttpServletResponse response)throws IOException {
		
		
		int departmentID = Integer.parseInt(request.getParameter("departmentID"));
		String date1 = request.getParameter("date1");
		String date2 = request.getParameter("date2");
		byte type = Byte.parseByte(request.getParameter("type"));
		int carteenID = Integer.parseInt(request.getParameter("carteenID"));
		
		//获取数据库连接，统一在这里获取连接，减少创建连接的次数
		Connection conn = DButil.getConnection();
		if (conn == null) {
			return;
		}
		
		List<OrderView> orders = OrderDAO.getOrdersOfDepartment(conn, departmentID, date1, date2, type, carteenID);
		
		  //释放数据库连接
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		JSONObject json = new JSONObject();
		json.accumulate("total", orders.size());
		json.accumulate("rows", orders);
		
		PrintWriter out = response.getWriter();
		out.print(json);
		out.flush();
		out.close();
	}
	
	/**
	 * 获取一段时期内的某餐订单列表,以便提供备餐明细(method=7)
	 * @param request 传递type,additional,date1,date2
	 * @param response
	 */
	private void getOrdersForPrepare(HttpServletRequest request, HttpServletResponse response)throws IOException {
		
		
		byte type = Byte.parseByte(request.getParameter("type"));
		String additional = request.getParameter("additional");
		String date1 = request.getParameter("date1");
		String	date2 = request.getParameter("date2");
		int carteenID = Integer.parseInt(request.getParameter("carteenID"));
		
		//获取数据库连接，统一在这里获取连接，减少创建连接的次数
		Connection conn = DButil.getConnection();
		if (conn == null) {
			return;
		}
		
		List<OrderView> orders = OrderDAO.getOrders(conn,date1, date2, type, additional,carteenID);
		
		  //释放数据库连接
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		JSONObject json = new JSONObject();
		json.accumulate("total", orders.size());
		json.accumulate("rows", orders);
		
		PrintWriter out = response.getWriter();
		out.print(json);
		out.flush();
		out.close();
	}
	
	/**
	 * 获取职工订单列表
	 * @param request 传递departmentID,date1,date2
	 * @param response 返回该部门成员在指定时段内的用餐汇总数据(工号，姓名，中餐，晚餐，零点餐）
	 */
	private void getTotalOfEmployee(HttpServletRequest request, HttpServletResponse response)throws IOException {
		
		
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
		String date1 = request.getParameter("date1");
		String date2 = request.getParameter("date2");
		int carteenID = Integer.parseInt(request.getParameter("carteenID"));
		int placeID = Integer.parseInt(request.getParameter("placeID"));
		
		List<OrderTotalOnEmployeeOfDepartment> totals = new ArrayList<OrderTotalOnEmployeeOfDepartment>();
		
		//获取数据库连接，统一在这里获取连接，减少创建连接的次数
		Connection conn = DButil.getConnection();
		if (conn == null) {
			return;
		}
		
		int total = OrderDAO.getTotalOfEmployee(conn,companyID,workshopID,departmentID, carteenID,placeID,date1, date2,page,rows,totals);
		
		  //释放数据库连接
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		JSONObject json = new JSONObject();
		json.accumulate("total", total);
		json.accumulate("rows", totals);
		
		PrintWriter out = response.getWriter();
		out.print(json);
		out.flush();
		out.close();
	}
	
	/**
	 * 获取班组订单列表
	 * @param request 传递placeIDs，date，type
	 * @param response 返回送餐路线某天某餐的用餐汇总数据(送餐点号，送餐点名，班组号，班组名，份数，备注【零点餐需要说明各有哪些】）
	 */
	private void getTotalOfRoute(HttpServletRequest request, HttpServletResponse response)throws IOException {
		
		
		String placeIDs = request.getParameter("placeIDs");
		String date = request.getParameter("date");
		byte type = Byte.parseByte(request.getParameter("type"));
		
		//获取数据库连接，统一在这里获取连接，减少创建连接的次数
		Connection conn = DButil.getConnection();
		if (conn == null) {
			return;
		}
		
		List<OrderTotalOnPlaceDepartmentOfPlace> totals = OrderDAO.getTotalOfDepartment(conn,placeIDs,type,date,date);			
		
		//计算总数
		int sum = 0;
		for(OrderTotalOnPlaceDepartmentOfPlace total:totals){
			sum += total.getAmount();
		}		
		OrderTotalOnPlaceDepartmentOfPlace footer = new OrderTotalOnPlaceDepartmentOfPlace();
		footer.setDepartmentName("总计");
		footer.setAmount(sum);
		
		//释放数据库连接
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		JSONObject json = new JSONObject();
		json.accumulate("total", totals.size());
		json.accumulate("rows", totals);
		json.accumulate("footer", footer);
		
		PrintWriter out = response.getWriter();
		out.print(json);
		out.flush();
		out.close();
	}
	
	/**
	 * 获取公司基于职工汇总的订单列表(mothod=5)
	 * @param request 传递companyID,date1,date2carteenID
	 * @param response
	 */
	private void getTotalOnEmployeeOfCompany(HttpServletRequest request, HttpServletResponse response)throws IOException {

		
		int companyID = Integer.parseInt(request.getParameter("companyID"));
		String date1 = request.getParameter("date1");
		String date2 = request.getParameter("date2");
		int carteenID = Integer.parseInt(request.getParameter("carteenID"));
		int workshopID = Integer.parseInt(request.getParameter("workshopID"));
		int departmentID = Integer.parseInt(request.getParameter("departmentID"));
		//获取数据库连接，统一在这里获取连接，减少创建连接的次数
		Connection conn = DButil.getConnection();
		if (conn == null) {
			return;
		}
		
		//统计时生成价格和假日信息
		OrderDAO.generatePriceAndHoliday(conn, companyID, workshopID, departmentID, date1, date2);
		
		//汇总统计
		List<OrderTotalOnEmployeeOfCompany> totals = OrderDAO.getTotalOnEmployeeOfCompany(conn,companyID,workshopID,departmentID, date1, date2,carteenID);			
		
		  //释放数据库连接
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		OrderTotalOnEmployeeOfCompany footer = new OrderTotalOnEmployeeOfCompany();
		for(OrderTotalOnEmployeeOfCompany total:totals){
			footer.setAmountDinnerHoliday(footer.getAmountDinnerHoliday()+total.getAmountDinnerHoliday());
			footer.setAmountDinnerNormal(footer.getAmountDinnerNormal()+total.getAmountDinnerNormal());
			footer.setAmountLunchHoliday(footer.getAmountLunchHoliday()+total.getAmountLunchHoliday());
			footer.setAmountLunchNormal(footer.getAmountLunchNormal()+total.getAmountLunchNormal());
			footer.setAmountMidnightHoliday(footer.getAmountMidnightHoliday()+total.getAmountMidnightHoliday());
			footer.setAmountMidnightNormal(footer.getAmountMidnightNormal()+total.getAmountMidnightNormal());
			footer.setMoneyDinnerHoliday(footer.getMoneyDinnerHoliday()+total.getMoneyDinnerHoliday());
			footer.setMoneyDinnerNormal(footer.getMoneyDinnerNormal()+total.getMoneyDinnerNormal());
			footer.setMoneyLunchHoliday(footer.getMoneyLunchHoliday()+total.getMoneyLunchHoliday());
			footer.setMoneyLunchNormal(footer.getMoneyLunchNormal()+total.getMoneyLunchNormal());
			footer.setMoneyMidnightHoliday(footer.getMoneyMidnightHoliday()+total.getMoneyMidnightHoliday());
			footer.setMoneyMidnightNormal(footer.getMoneyMidnightNormal()+total.getMoneyMidnightNormal());
			footer.setSum(footer.getSum()+total.getSum());
		}
		footer.setName("合计");
		List<OrderTotalOnEmployeeOfCompany> footers = new ArrayList<OrderTotalOnEmployeeOfCompany>();
		footers.add(footer);

		JSONObject json = new JSONObject();
		json.accumulate("total", totals.size());
		json.accumulate("rows", totals);
		json.accumulate("footer", footers);
		
		
		
		PrintWriter out = response.getWriter();
		out.print(json);
		
		out.flush();
		out.close();
	}
	
	/**
	 * 获取公司基于职工汇总的订单列表(mothod=9)
	 * @param request 传递companyID,date1,date2
	 * @param response
	 */
	private void getTotalOnDepartmentOfCompany(HttpServletRequest request, HttpServletResponse response)throws IOException {
		
		
		int companyID = Integer.parseInt(request.getParameter("companyID"));
		String date1 = request.getParameter("date1");
		String date2 = request.getParameter("date2");
		int carteenID = Integer.parseInt(request.getParameter("carteenID"));
		int workshopID = Integer.parseInt(request.getParameter("workshopID"));
		int departmentID = Integer.parseInt(request.getParameter("departmentID"));
		//获取数据库连接，统一在这里获取连接，减少创建连接的次数
		Connection conn = DButil.getConnection();
		if (conn == null) {
			return;
		}
		
		//统计时生成价格和假日信息
		OrderDAO.generatePriceAndHoliday(conn,companyID,workshopID,departmentID, date1, date2);
		//汇总统计
		List<OrderTotalOnDepartmentOfCompany> totals = OrderDAO.getTotalOnDepartmentOfCompany(conn,companyID,workshopID,date1, date2,carteenID);			
		
		  //释放数据库连接
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		OrderTotalOnDepartmentOfCompany footer = new OrderTotalOnDepartmentOfCompany();
		for(OrderTotalOnDepartmentOfCompany total:totals){
			footer.setAmountDinnerHoliday(footer.getAmountDinnerHoliday()+total.getAmountDinnerHoliday());
			footer.setAmountDinnerNormal(footer.getAmountDinnerNormal()+total.getAmountDinnerNormal());
			footer.setAmountLunchHoliday(footer.getAmountLunchHoliday()+total.getAmountLunchHoliday());
			footer.setAmountLunchNormal(footer.getAmountLunchNormal()+total.getAmountLunchNormal());
			footer.setAmountMidnightHoliday(footer.getAmountMidnightHoliday()+total.getAmountMidnightHoliday());
			footer.setAmountMidnightNormal(footer.getAmountMidnightNormal()+total.getAmountMidnightNormal());
			footer.setMoneyDinnerHoliday(footer.getMoneyDinnerHoliday()+total.getMoneyDinnerHoliday());
			footer.setMoneyDinnerNormal(footer.getMoneyDinnerNormal()+total.getMoneyDinnerNormal());
			footer.setMoneyLunchHoliday(footer.getMoneyLunchHoliday()+total.getMoneyLunchHoliday());
			footer.setMoneyLunchNormal(footer.getMoneyLunchNormal()+total.getMoneyLunchNormal());
			footer.setMoneyMidnightHoliday(footer.getMoneyMidnightHoliday()+total.getMoneyMidnightHoliday());
			footer.setMoneyMidnightNormal(footer.getMoneyMidnightNormal()+total.getMoneyMidnightNormal());
			footer.setSum(footer.getSum()+total.getSum());
		}
		footer.setDepartmentName("合计");
		List<OrderTotalOnDepartmentOfCompany> footers = new ArrayList<OrderTotalOnDepartmentOfCompany>();
		footers.add(footer);
		
		JSONObject json = new JSONObject();
		json.accumulate("total", totals.size());
		json.accumulate("rows", totals);
		json.accumulate("footer", footers);
		
		
		
		PrintWriter out = response.getWriter();
		out.print(json);
		out.flush();
		out.close();
	}
	

	private void getFutureOrders(HttpServletRequest request,HttpServletResponse response) throws IOException {
		
				PrintWriter out = response.getWriter();
				
				//获取或计算相关参数
				String employeeID = request.getParameter("employeeID");	
				
				//获取数据库连接，统一在这里获取连接，减少创建连接的次数
				Connection conn = DButil.getConnection();
				if (conn == null) {
					return;
				}
				
				int days = RegularDAO.getRegular(conn).getDays();//获取可预订天数
				Date day1 = new Date();
				Date day2 = new Date();
				day2.setDate(day1.getDate()+days);
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				String date1 = sdf.format(day1);
				String date2 = sdf.format(day2);
				
				//获取订餐期限内的订餐记录
				List<OrderView> orders = OrderDAO.getOrders(conn, employeeID, Price.ALLDAY, date1, date2);
				
				int columns = 3;//每行3列
				int rows = (int) Math.ceil(days*1.0/columns);
				
				Date day = new Date();
				int n = 1;
				for(int row=0; row<rows; row++){
					out.print("<table>");
					out.print("<tr>");
					for(int column=0; column<columns; column++){
						String date = sdf.format(day);
						OrderView order = new OrderView();
						out.print("<td>");
						out.print("<table class='table_order' style='width:300px;' cellspacing='0' cellpadding='5'>");
						out.print("<tr>");
						out.print("<th colspan=3 align='center'>");
						out.print(date);
						out.print("</th>");
						out.print("</tr>");
						
						//零点餐
						byte status = getOrderStatus(conn, orders,date,Price.MIDNIGHT,order);
						String msg = "";
						String op = "";
						switch (status) {
						case 0:
							msg = "未订";
							op = "已截止";
							break;
						case 1:
							msg = "未订";
							op = String.format("<input type='button' style='color:#0000FF;' value='A餐' onclick=\"addOrder('%s',%d,'%s')\"/> &nbsp;",date,Price.MIDNIGHT,"A");
							op += String.format("<input type='button' style='color:#0000FF;' value='B餐' onclick=\"addOrder('%s',%d,'%s')\" /> &nbsp;",date,Price.MIDNIGHT,"B");
							op += String.format("<input type='button' style='color:#0000FF;' value='C餐' onclick=\"addOrder('%s',%d,'%s')\" />",date,Price.MIDNIGHT,"C");
							break;
						case 2:
							msg = "已订"+order.getAdditional()+"餐";
							op = "已截止";
							break;
						case 3:
							msg = "已订"+order.getAdditional()+"餐";
							op = String.format("<input type='button' style='color:#FF0000;' value='取消' onclick=\"delOrder(%d)\" />",order.getID());
							break;

						default:
							break;
						}
						out.print("<tr>");					
						out.print("<td width=40>");
						out.print("零点餐");
						if (HolidayDAO.isHoliday(conn, date, Price.MIDNIGHT)) {
							out.print("<br/><span style='color:red'>[假日]</span>");
						}
						out.print("</td>");
						out.print("<td width=45>");
						out.print(msg);
						out.print("</td>");
						out.print("<td>");
						out.print(op);
						out.print("</td>");									
						out.print("</tr>");
						
						//中餐
						status = getOrderStatus(conn, orders,date,Price.LUNCH,order);
						switch (status) {
						case 0:
							msg = "未订";
							op = "已截止";
							break;
						case 1:
							msg = "未订";
							op = String.format("<input type='button' style='color:#0000FF;' value='预订' onclick=\"addOrder('%s',%d,'%s')\" />",date,Price.LUNCH,"");
							break;
						case 2:
							msg = "已订";
							op = "已截止";
							break;
						case 3:
							msg = "已订";
							op = String.format("<input type='button' style='color:#FF0000;' value='取消' onclick=\"delOrder(%d)\" />",order.getID());
							break;

						default:
							break;
						}
						out.print("<tr>");					
						out.print("<td>");
						out.print("中餐");
						if (HolidayDAO.isHoliday(conn, date, Price.LUNCH)) {
							out.print("<br/><span style='color:red'>[假日]</span>");
						}
						out.print("</td>");
						out.print("<td>");
						out.print(msg);
						out.print("</td>");
						out.print("<td>");
						out.print(op);
						out.print("</td>");									
						out.print("</tr>");
						
						//晚餐
						status = getOrderStatus(conn, orders,date,Price.DINNER,order);
						switch (status) {
						case 0:
							msg = "未订";
							op = "已截止";
							break;
						case 1:
							msg = "未订";
							op = String.format("<input type='button' style='color:#0000FF;' value='预订' onclick=\"addOrder('%s',%d,'%s')\" />",date,Price.DINNER,"");
							break;
						case 2:
							msg = "已订";
							op = "已截止";
							break;
						case 3:
							msg = "已订";
							op = String.format("<input type='button' style='color:#FF0000;' value='取消' onclick=\"delOrder(%d)\" />",order.getID());
							break;

						default:
							break;
						}
						out.print("<tr>");					
						out.print("<td>");
						out.print("晚餐");
						if (HolidayDAO.isHoliday(conn, date, Price.DINNER)) {
							out.print("<br/><span style='color:red'>[假日]</span>");
						}
					
						out.print("</td>");
						out.print("<td>");
						out.print(msg);
						out.print("</td>");
						out.print("<td>");
						out.print(op);
						out.print("</td>");									
						out.print("</tr>");
						
						out.print("</table>");
						out.print("</td>");
						
						day.setDate(day.getDate()+1);//下一天
						n++;
						if (n>days) {
							break;
						}
					}
					out.print("</tr>");
					out.print("</table>");
				}
				  //释放数据库连接
				try {
					conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
				out.flush();
				out.close();
	}


	/**
	 * 获取某一天某餐的状态
	 * @param conn 数据库连接
	 * @param orders 待查找的订单集合
	 * @param date 日期
	 * @param type 零点餐/中餐/晚餐
	 * @param order 如果已订则返回订单
	 * @return 0：未订已截止；1：未订未截止；2：已订已截止；3：已订未截止
	 */
	private byte getOrderStatus(Connection conn, List<OrderView> orders, String date, byte type, OrderView order) {
		for (OrderView o : orders) {
			if (o.getEatDate().equals(date) && o.getType()==type) {
				order.setID(o.getID());
				order.setAdditional(o.getAdditional());
				if(RegularDAO.isExpire(conn,date,type)){
					return 2;
				}else {
					return 3;
				}
			}
		}
		if(RegularDAO.isExpire(conn,date,type)){
			return 0;
		}else {
			return 1;
		}
	}

	//查询订单
	private void SearchOrders(HttpServletRequest request, HttpServletResponse response)  throws IOException{
		
		int page = 0;
		int rows = 0;
		boolean pagination = Boolean.parseBoolean(request.getParameter("pagination"));
		if (pagination) {
			page = Integer.parseInt(request.getParameter("page"));
			rows = Integer.parseInt(request.getParameter("rows"));
		}
				
		String date1 = request.getParameter("date1");
		String date2 = request.getParameter("date2");
		int companyID = Integer.parseInt(request.getParameter("companyID"));
		int workshopID = Integer.parseInt(request.getParameter("workshopID"));
		int departmentID = Integer.parseInt(request.getParameter("departmentID"));
		String idOrName = request.getParameter("idOrName");
		int type = Integer.parseInt(request.getParameter("type"));	
		short placeID = Short.parseShort(request.getParameter("placeID"));
		int carteenID = Integer.parseInt(request.getParameter("carteenID"));
		
		//获取数据库连接，统一在这里获取连接，减少创建连接的次数
		Connection conn = DButil.getConnection();
		if (conn == null) {
			return;
		}
		List<OrderView> orders = new ArrayList<OrderView>();
		int totals= OrderDAO.getOrders(conn,date1,date2, companyID, workshopID, departmentID,placeID,carteenID, type, idOrName,orders,page,rows);
		
		  //释放数据库连接
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		JSONObject json = new JSONObject();
		json.accumulate("total", totals);
		json.accumulate("rows", orders);
		
		PrintWriter out = response.getWriter();
		out.print(json);
		out.flush();
		out.close();
	}
}
