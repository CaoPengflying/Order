package DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.fileupload.util.LimitedInputStream;

import com.sun.org.apache.bcel.internal.generic.NEW;

import View.EmployeeView;
import View.OrderView;
import View.OrderViewSimple;
import bean.Admin;
import bean.Employee;
import bean.Order;
import bean.OrderTotalOnDepartmentOfCompany;
import bean.OrderTotalOnDepartmentOfWorkshop;
import bean.OrderTotalOnEmployeeOfCompany;
import bean.OrderTotalOnEmployeeOfDepartment;
import bean.OrderTotalOnPlaceDepartmentOfPlace;
import bean.Place;
import bean.Price;
import bean.OrderTotalOnWorkshopOfCompany;

public class OrderDAO {
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Connection connection = DButil.getConnection();		
		List<EmployeeView>employees = new ArrayList<EmployeeView>();
		
		int total = EmployeeDAO.getEmployees(connection, 0, 0, 0, 0, "", 0, 0, employees);
		try {
			connection.close();
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		for (EmployeeView e:employees) {
			Connection connection2 = DButil.getConnection();
			for (int i = 1; i < 30; i++) {
				date.setDate(i);
				for (byte type = Price.DINNER; type <= Price.MIDNIGHT; type++) {					
					Order order = new Order();
					order.setDepartmentID(e.getDepartmentID());
					order.setEatDate(sdf.format(date) );
					order.setOrderDate(sdf.format(date));
					order.setEaterID(e.getID());
					order.setOrdererID("0021");
					order.setHoliday(false);
					order.setPlaceID((short)117);
					order.setType(type);
					order.setPrice(14);
					
					OrderDAO.addOrder(connection2, order);
				}				
			}
			try {
				connection2.close();
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}

	}
	
	/**
	 * 添加订单
	 * @param conn 数据库连接
	 * @param order 待添加的订单
	 */
	public static void addOrder(Connection conn, Order order){
		PreparedStatement pst = null;		
		try {
			pst = conn.prepareStatement("insert into orders(type,eaterID,ordererID,eatDate,orderDate,placeID,departmentID,price,additional,isHoliday) values(?,?,?,?,?,?,?,?,?,?)");
			pst.setByte(1, order.getType());
			pst.setString(2, order.getEaterID());
			pst.setString(3, order.getOrdererID());
			pst.setString(4, order.getEatDate());
			pst.setString(5, order.getOrderDate());
			pst.setInt(6, order.getPlaceID());
			pst.setInt(7, order.getDepartmentID());
			pst.setFloat(8, order.getPrice());
			pst.setString(9, order.getAdditional());
			pst.setBoolean(10, order.isHoliday());
			pst.executeUpdate();	
			pst.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 删除订单
	 * @param conn 数据库连接
	 * @param orderID 订单号
	 */
	public static void delOrder(Connection conn, long orderID){		
		PreparedStatement pst = null;		
		try {
			pst = conn.prepareStatement("delete from orders where ID = ?");
			pst.setLong(1,orderID);
			pst.executeUpdate();	
			pst.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 删除一段时间内的订单（数据备份时清理数据）
	 * @param conn 数据库连接
	 * @param date1 起始日期
	 * @param date2 终止日期
	 */
	public static void delOrders(Connection conn, String date1, String date2) {		
		PreparedStatement pst = null;		
		try {
			pst = conn.prepareStatement("delete from orders where eatDate between ? and ?");
			pst.setString(1,date1);
			pst.setString(2, date2);
			pst.executeUpdate();	
			pst.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 判断某人某天的某一餐是否已经订了，不能重复订
	 * @param eaterID 用餐者
	 * @param eatDate 用餐日期
	 * @param type 哪一餐
	 * @return 是否有预订
	 */
	public static boolean isExist(Connection conn, String eaterID, String eatDate, byte type) {		
		PreparedStatement pst = null;
		ResultSet rs = null;		
		try {
			pst = conn.prepareStatement("select * from Orders where eaterID = ? and eatDate = ? and type = ?");
			pst.setString(1, eaterID);
			pst.setString(2, eatDate);
			pst.setByte(3, type);
			rs = pst.executeQuery();
			if (rs.next()) {
				rs.close();				
				pst.close();
				return true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * 获取指定订单
	 * @param conn 数据库连接
	 * @param orderID 订单号
	 * @return 订单数据
	 */
	public static Order getOrder(Connection conn, long orderID) {		
		PreparedStatement pst = null;
		ResultSet rs = null;		
		try {
			pst = conn.prepareStatement("select * from Orders where ID = ?");
			pst.setLong(1, orderID);
			rs = pst.executeQuery();
			if (rs.next()) {
				Order order = getOrder(rs);
				rs.close();	
				pst.close();
				return order;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 获取指定订单视图
	 * @param conn 数据库连接
	 * @param orderID 订单号
	 * @return 订单视图
	 */
	public static OrderView getOrderView(Connection conn, long orderID) {
		PreparedStatement pst = null;
		ResultSet rs = null;		
		try {
			pst = conn.prepareStatement("select * from OrderView where ID = ?");
			pst.setLong(1, orderID);
			rs = pst.executeQuery();
			if (rs.next()) {
				OrderView order = getOrderView(conn,rs);
				rs.close();	
				pst.close();
				return order;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	
	public static int getTotalOfEmployee(Connection conn, int companyID, int workshopID, int departmentID, int carteenID, int placeID, String date1, String date2,int page, int rows, List<OrderTotalOnEmployeeOfDepartment> totals) {
		PreparedStatement pst = null;
		ResultSet rs = null;
		
		String where = String.format(" eatDate between \"%s\" and \"%s\"", date1,date2);
		if (companyID != 0) {
			where += String.format(" and companyID=%d", companyID);
		}
		if (workshopID != 0) {
			where += String.format(" and workshopID=%d", workshopID);
		}
		if (departmentID != 0) {
			where += String.format(" and departmentID=%d", departmentID);
		}
		if(carteenID != 0) {
			where += String.format(" and carteenID=%d", carteenID);
		}
		if(placeID != 0) {
			where += String.format(" and placeID=%d", placeID);
		}
		
		
		int offset = (page-1)*rows;
		String sql1 = String.format("select count(distinct eaterID) as total from orderview where "+where);
		String sql2 = "";
		if (page == 0) {//不需要分页
			sql2 = String.format("SELECT eaterID as employeeID,eaterName as name,sum(CASE type when ? then 1 else 0 end) as lunch,sum(CASE type when ? then 1 else 0 end) as dinner,sum(CASE type when ? then 1 else 0 end) as midnight,sum(price) as money FROM `orderview` where %s GROUP BY eaterID",where);
		} else {
			sql2 = String.format("SELECT eaterID as employeeID,eaterName as name,sum(CASE type when ? then 1 else 0 end) as lunch,sum(CASE type when ? then 1 else 0 end) as dinner,sum(CASE type when ? then 1 else 0 end) as midnight,sum(price) as money FROM `orderview` where %s GROUP BY eaterID limit %d,%d",where, offset,rows);
		}
		int n = 0;
		
		try {
			pst = conn.prepareStatement(sql1);
			rs = pst.executeQuery();
			if (rs.next()) {
				n = rs.getInt("total");

				pst = conn.prepareStatement(sql2);
				pst.setByte(1, Price.LUNCH);
				pst.setByte(2, Price.DINNER);
				pst.setByte(3, Price.MIDNIGHT);
				rs = pst.executeQuery();
				while (rs.next()) {
					OrderTotalOnEmployeeOfDepartment total = new OrderTotalOnEmployeeOfDepartment();
					total.setEmployeeID(rs.getString("employeeID"));
					total.setName(rs.getString("name"));
					total.setLunch(rs.getInt("lunch"));
					total.setDinner(rs.getInt("dinner"));
					total.setMidnight(rs.getInt("midnight"));
					total.setMoney(rs.getFloat("money"));
					totals.add(total);
				}
			}
			rs.close();	
			pst.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return n;
	}
	
	public static List<OrderTotalOnDepartmentOfWorkshop> getTotalOnDepartmentOfWorkshop(Connection conn, int workshopID, String date1, String date2) {
		PreparedStatement pst = null;
		ResultSet rs = null;
		
		List<OrderTotalOnDepartmentOfWorkshop> totals = new ArrayList<OrderTotalOnDepartmentOfWorkshop>();
		String sql = "SELECT departmentID,departmentName,sum(CASE type when ? then 1 else 0 end) as lunch,sum(CASE type when ? then 1 else 0 end) as dinner,sum(CASE type when ? then 1 else 0 end) as midnight FROM `orderview` where eatDate between ? and ? and workshopID=? GROUP BY departmentID";
		try {
			pst = conn.prepareStatement(sql);
			pst.setByte(1, Price.LUNCH);
			pst.setByte(2, Price.DINNER);
			pst.setByte(3, Price.MIDNIGHT);
			pst.setString(4, date1);
			pst.setString(5, date2);
			pst.setInt(6, workshopID);
			rs = pst.executeQuery();
			while (rs.next()) {
				OrderTotalOnDepartmentOfWorkshop total = new OrderTotalOnDepartmentOfWorkshop();
				total.setDepartmentID(rs.getShort("departmentID"));
				total.setDepartmentName(rs.getString("departmentName"));
				total.setLunch(rs.getInt("lunch"));
				total.setDinner(rs.getInt("dinner"));
				total.setMidnight(rs.getInt("midnight"));
				totals.add(total);
			}
			rs.close();	
			pst.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return totals;
	}
	

	public static List<OrderTotalOnPlaceDepartmentOfPlace> getTotalOfDepartment(Connection conn, String placeIDs, byte type, String date1, String date2) {
		
		PreparedStatement pst = null;
		ResultSet rs = null;
		
		List<OrderTotalOnPlaceDepartmentOfPlace> totals = new ArrayList<OrderTotalOnPlaceDepartmentOfPlace>();		
		try {
			String sql = String.format("SELECT placeID,placeName, departmentID,departmentName, COUNT(ID) as amount FROM `orderview` where type=? and placeID in(%s) and eatDate between ? and ? GROUP BY placeID,departmentID order by instr('%s',placeID)",placeIDs,placeIDs);
			pst = conn.prepareStatement(sql);
			pst.setByte(1, type);
			pst.setString(2, date1);
			pst.setString(3, date2);
			rs = pst.executeQuery();
			
			while (rs.next()) {
				OrderTotalOnPlaceDepartmentOfPlace total = new OrderTotalOnPlaceDepartmentOfPlace();
				total.setPlaceID(rs.getShort("placeID"));
				total.setPlaceName(rs.getString("placeName"));
				total.setDepartmentID(rs.getShort("departmentID"));
				total.setDepartmentName(rs.getString("departmentName"));
				total.setAmount(rs.getInt("amount"));
				if (type == Price.MIDNIGHT) {
					List<OrderView> orders = getOrders(conn,total.getPlaceID(), total.getDepartmentID(),type,date1,date2);
					String remark = getRemark(orders);
					total.setRemark(remark);
				}
				totals.add(total);
			}
			rs.close();	
			pst.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return totals;
	}

	public static List<OrderTotalOnEmployeeOfCompany> getTotalOnEmployeeOfCompany(Connection conn, int companyID, int workshopID,int departmentID, String date1, String date2, int carteenID) {
		
		PreparedStatement pst = null;
		ResultSet rs = null;
		
		List<OrderTotalOnEmployeeOfCompany> totals = new ArrayList<OrderTotalOnEmployeeOfCompany>();		
		try {
			String sql = "SELECT departmentName,eaterID as employeeID,eaterName as name,"+
						 "sum(CASE when type=? and isHoliday=false then 1 else 0 end) as amountLunchNormal,"+
						 "sum(CASE when type=? and isHoliday=false then price else 0 end) as moneyLunchNormal,"+
						 "sum(CASE when type=? and isHoliday=true then 1 else 0 end) as amountLunchHoliday,"+
						 "sum(CASE when type=? and isHoliday=true then price else 0 end) as moneyLunchHoliday,"+
						 "sum(CASE when type=? and isHoliday=false then 1 else 0 end) as amountDinnerNormal,"+
						 "sum(CASE when type=? and isHoliday=false then price else 0 end) as moneyDinnerNormal,"+
						 "sum(CASE when type=? and isHoliday=true then 1 else 0 end) as amountDinnerHoliday,"+
						 "sum(CASE when type=? and isHoliday=true then price else 0 end) as moneyDinnerHoliday,"+
						 "sum(CASE when type=? and isHoliday=false then 1 else 0 end) as amountmidnightNormal," +
						 "sum(CASE when type=? and isHoliday=false then price else 0 end) as moneyMidnightNormal,"+
						 "sum(CASE when type=? and isHoliday=true then 1 else 0 end) as amountMidnightHoliday,"+
						 "sum(CASE when type=? and isHoliday=true then price else 0 end) as moneyMidnightHoliday ";
			if(carteenID != 0) {
				sql += "FROM `orderview` where companyID=? and eatDate between ? and ? and placeID in (select ID from place where carteenID = ?)";
			}else {
				sql += "FROM `orderview` where companyID=? and eatDate between ? and ?";
			}
			if(workshopID != 0) {
				sql += String.format(" and workshopID=%d", workshopID);
			}
			if(departmentID != 0) {
				sql += String.format(" and departmentID=%d", departmentID);
			}
			sql += " GROUP BY eaterID";
			pst = conn.prepareStatement(sql);
			pst.setByte(1, Price.LUNCH);
			pst.setByte(2, Price.LUNCH);
			pst.setByte(3, Price.LUNCH);
			pst.setByte(4, Price.LUNCH);
			pst.setByte(5, Price.DINNER);
			pst.setByte(6, Price.DINNER);
			pst.setByte(7, Price.DINNER);
			pst.setByte(8, Price.DINNER);
			pst.setByte(9, Price.MIDNIGHT);
			pst.setByte(10, Price.MIDNIGHT);
			pst.setByte(11, Price.MIDNIGHT);
			pst.setByte(12, Price.MIDNIGHT);
			pst.setInt(13, companyID);
			pst.setString(14, date1);
			pst.setString(15, date2);
			if(carteenID != 0) {
				pst.setInt(16, carteenID);
			}
			rs = pst.executeQuery();

			while (rs.next()) {
				OrderTotalOnEmployeeOfCompany total = new OrderTotalOnEmployeeOfCompany();
				total.setDepartmentName(rs.getString("departmentName"));
				total.setEmployeeID(rs.getString("employeeID"));
				total.setName(rs.getString("name"));
				total.setAmountLunchNormal(rs.getInt("amountLunchNormal"));
				total.setMoneyLunchNormal(rs.getFloat("moneyLunchNormal"));
				total.setAmountLunchHoliday(rs.getInt("amountLunchHoliday"));
				total.setMoneyLunchHoliday(rs.getFloat("moneyLunchHoliday"));
				total.setAmountDinnerNormal(rs.getInt("amountDinnerNormal"));
				total.setMoneyDinnerNormal(rs.getFloat("moneyDinnerNormal"));
				total.setAmountDinnerHoliday(rs.getInt("amountDinnerHoliday"));
				total.setMoneyDinnerHoliday(rs.getFloat("moneyDinnerHoliday"));
				total.setAmountMidnightNormal(rs.getInt("amountMidnightNormal"));
				total.setMoneyMidnightNormal(rs.getFloat("moneyMidnightNormal"));
				total.setAmountMidnightHoliday(rs.getInt("amountMidnightHoliday"));
				total.setMoneyMidnightHoliday(rs.getFloat("moneyMidnightHoliday"));
				
				//计算企补总额
				float sum = 0;
				sum += total.getMoneyDinnerHoliday()+total.getMoneyDinnerNormal();
				sum += total.getMoneyLunchHoliday()+total.getMoneyLunchNormal();
				sum += total.getMoneyMidnightHoliday()+total.getMoneyMidnightNormal();
				total.setSum(sum);
				
				totals.add(total);
			}
			rs.close();	
			pst.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return totals;
	}

	public static List<OrderTotalOnDepartmentOfCompany> getTotalOnDepartmentOfCompany(Connection conn, int companyID, int workshopID, String date1, String date2, int carteenID) {
		
		PreparedStatement pst = null;
		ResultSet rs = null;
		
		List<OrderTotalOnDepartmentOfCompany> totals = new ArrayList<OrderTotalOnDepartmentOfCompany>();		
		try {
			String sql = "SELECT departmentID,departmentName,"+
						 "sum(CASE when type=? and isHoliday=false then 1 else 0 end) as amountLunchNormal,"+
						 "sum(CASE when type=? and isHoliday=false then price else 0 end) as moneyLunchNormal,"+
						 "sum(CASE when type=? and isHoliday=true then 1 else 0 end) as amountLunchHoliday,"+
						 "sum(CASE when type=? and isHoliday=true then price else 0 end) as moneyLunchHoliday,"+
						 "sum(CASE when type=? and isHoliday=false then 1 else 0 end) as amountDinnerNormal,"+
						 "sum(CASE when type=? and isHoliday=false then price else 0 end) as moneyDinnerNormal,"+
						 "sum(CASE when type=? and isHoliday=true then 1 else 0 end) as amountDinnerHoliday,"+
						 "sum(CASE when type=? and isHoliday=true then price else 0 end) as moneyDinnerHoliday,"+
						 "sum(CASE when type=? and isHoliday=false then 1 else 0 end) as amountmidnightNormal," +
						 "sum(CASE when type=? and isHoliday=false then price else 0 end) as moneyMidnightNormal,"+
						 "sum(CASE when type=? and isHoliday=true then 1 else 0 end) as amountMidnightHoliday,"+
						 "sum(CASE when type=? and isHoliday=true then price else 0 end) as moneyMidnightHoliday ";
			if(carteenID != 0) {
				sql +=  "FROM `orderview` where companyID=? and eatDate between ? and ? and placeID in (select ID from place where carteenID = ?)";
			}else {
				sql += "FROM `orderview` where companyID=? and eatDate between ? and ? ";
			}
			if(workshopID != 0) {
				sql += String.format(" and workshopID=%d", workshopID);
			}
			
			sql += " GROUP BY departmentID";
			pst = conn.prepareStatement(sql);
			pst.setByte(1, Price.LUNCH);
			pst.setByte(2, Price.LUNCH);
			pst.setByte(3, Price.LUNCH);
			pst.setByte(4, Price.LUNCH);
			pst.setByte(5, Price.DINNER);
			pst.setByte(6, Price.DINNER);
			pst.setByte(7, Price.DINNER);
			pst.setByte(8, Price.DINNER);
			pst.setByte(9, Price.MIDNIGHT);
			pst.setByte(10, Price.MIDNIGHT);
			pst.setByte(11, Price.MIDNIGHT);
			pst.setByte(12, Price.MIDNIGHT);
			pst.setInt(13, companyID);
			pst.setString(14, date1);
			pst.setString(15, date2);
			if(carteenID != 0) {
				pst.setInt(16, carteenID);
			}
			rs = pst.executeQuery();

			while (rs.next()) {
				OrderTotalOnDepartmentOfCompany total = new OrderTotalOnDepartmentOfCompany();
				total.setDepartmentID(rs.getShort("departmentID"));
				total.setDepartmentName(rs.getString("departmentName"));
				total.setAmountLunchNormal(rs.getInt("amountLunchNormal"));
				total.setMoneyLunchNormal(rs.getFloat("moneyLunchNormal"));
				total.setAmountLunchHoliday(rs.getInt("amountLunchHoliday"));
				total.setMoneyLunchHoliday(rs.getFloat("moneyLunchHoliday"));
				total.setAmountDinnerNormal(rs.getInt("amountDinnerNormal"));
				total.setMoneyDinnerNormal(rs.getFloat("moneyDinnerNormal"));
				total.setAmountDinnerHoliday(rs.getInt("amountDinnerHoliday"));
				total.setMoneyDinnerHoliday(rs.getFloat("moneyDinnerHoliday"));
				total.setAmountMidnightNormal(rs.getInt("amountMidnightNormal"));
				total.setMoneyMidnightNormal(rs.getFloat("moneyMidnightNormal"));
				total.setAmountMidnightHoliday(rs.getInt("amountMidnightHoliday"));
				total.setMoneyMidnightHoliday(rs.getFloat("moneyMidnightHoliday"));
				
				//计算企补总额
				float sum = 0;
				sum += total.getMoneyDinnerHoliday()+total.getMoneyDinnerNormal();
				sum += total.getMoneyLunchHoliday()+total.getMoneyLunchNormal();
				sum += total.getMoneyMidnightHoliday()+total.getMoneyMidnightNormal();
				total.setSum(sum);
				
				totals.add(total);
			}
			rs.close();	
			pst.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return totals;
	}
public static List<OrderTotalOnWorkshopOfCompany> getTotalOnWorkshopOfCompany(Connection conn, int companyID, String date1, String date2, int carteenID) {
		
		PreparedStatement pst = null;
		ResultSet rs = null;
		
		List<OrderTotalOnWorkshopOfCompany> totals = new ArrayList<OrderTotalOnWorkshopOfCompany>();		
		try {
			String sql = "SELECT workshopID,workshopName,"+
						 "sum(CASE when type=? and isHoliday=false then 1 else 0 end) as amountLunchNormal,"+
						 "sum(CASE when type=? and isHoliday=false then price else 0 end) as moneyLunchNormal,"+
						 "sum(CASE when type=? and isHoliday=true then 1 else 0 end) as amountLunchHoliday,"+
						 "sum(CASE when type=? and isHoliday=true then price else 0 end) as moneyLunchHoliday,"+
						 "sum(CASE when type=? and isHoliday=false then 1 else 0 end) as amountDinnerNormal,"+
						 "sum(CASE when type=? and isHoliday=false then price else 0 end) as moneyDinnerNormal,"+
						 "sum(CASE when type=? and isHoliday=true then 1 else 0 end) as amountDinnerHoliday,"+
						 "sum(CASE when type=? and isHoliday=true then price else 0 end) as moneyDinnerHoliday,"+
						 "sum(CASE when type=? and isHoliday=false then 1 else 0 end) as amountmidnightNormal," +
						 "sum(CASE when type=? and isHoliday=false then price else 0 end) as moneyMidnightNormal,"+
						 "sum(CASE when type=? and isHoliday=true then 1 else 0 end) as amountMidnightHoliday,"+
						 "sum(CASE when type=? and isHoliday=true then price else 0 end) as moneyMidnightHoliday ";
				if(carteenID != 0) {
					sql += "FROM `orderview` where companyID=? and eatDate between ? and ? and placeID in (select ID from place where carteenID = ?) GROUP BY workshopID ";
				}else {
					sql += "FROM `orderview` where companyID=? and eatDate between ? and ?  GROUP BY workshopID ";
				}	 
			
			pst = conn.prepareStatement(sql);
			pst.setByte(1, Price.LUNCH);
			pst.setByte(2, Price.LUNCH);
			pst.setByte(3, Price.LUNCH);
			pst.setByte(4, Price.LUNCH);
			pst.setByte(5, Price.DINNER);
			pst.setByte(6, Price.DINNER);
			pst.setByte(7, Price.DINNER);
			pst.setByte(8, Price.DINNER);
			pst.setByte(9, Price.MIDNIGHT);
			pst.setByte(10, Price.MIDNIGHT);
			pst.setByte(11, Price.MIDNIGHT);
			pst.setByte(12, Price.MIDNIGHT);
			pst.setInt(13, companyID);
			pst.setString(14, date1);
			pst.setString(15, date2);
			if(carteenID != 0) {
				pst.setInt(16, carteenID);
			}
			rs = pst.executeQuery();

			while (rs.next()) {
				OrderTotalOnWorkshopOfCompany total = new OrderTotalOnWorkshopOfCompany();
				total.setWorkshopID(rs.getShort("workshopID"));
				total.setWorkshopName(rs.getString("workshopName"));
				total.setAmountLunchNormal(rs.getInt("amountLunchNormal"));
				total.setMoneyLunchNormal(rs.getFloat("moneyLunchNormal"));
				total.setAmountLunchHoliday(rs.getInt("amountLunchHoliday"));
				total.setMoneyLunchHoliday(rs.getFloat("moneyLunchHoliday"));
				total.setAmountDinnerNormal(rs.getInt("amountDinnerNormal"));
				total.setMoneyDinnerNormal(rs.getFloat("moneyDinnerNormal"));
				total.setAmountDinnerHoliday(rs.getInt("amountDinnerHoliday"));
				total.setMoneyDinnerHoliday(rs.getFloat("moneyDinnerHoliday"));
				total.setAmountMidnightNormal(rs.getInt("amountMidnightNormal"));
				total.setMoneyMidnightNormal(rs.getFloat("moneyMidnightNormal"));
				total.setAmountMidnightHoliday(rs.getInt("amountMidnightHoliday"));
				total.setMoneyMidnightHoliday(rs.getFloat("moneyMidnightHoliday"));
				
				//计算企补总额
				float sum = 0;
				sum += total.getMoneyDinnerHoliday()+total.getMoneyDinnerNormal();
				sum += total.getMoneyLunchHoliday()+total.getMoneyLunchNormal();
				sum += total.getMoneyMidnightHoliday()+total.getMoneyMidnightNormal();
				total.setSum(sum);
				
				totals.add(total);
			}
			rs.close();	
			pst.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return totals;
	}
	/**
	 * 从订单集合中找出A餐、B餐、C餐各多少（字符串）
	 * @param orders
	 * @return
	 */
	private static String getRemark(List<OrderView> orders) {
		int midnightA = 0;
		int midnightB = 0;
		int midnightC = 0;
		for(OrderView order : orders){
			if (order.getAdditional().equals("A")) {
				midnightA++;
				continue;
			}
			if (order.getAdditional().equals("B")) {
				midnightB++;
				continue;
			}
			if (order.getAdditional().equals("C")) {
				midnightC++;
				continue;
			}
		}
		String remark = "";
		if (midnightA >= 0) {
			remark += String.format("%d,", midnightA);
		}
		if (midnightB >= 0) {
			remark += String.format("%d,", midnightB);
		}
		if (midnightC >= 0) {
			remark += String.format("%d", midnightC);
		}
		return remark;
	}

	/**
	 * 获取职工们一段时间内的中餐/午餐/零点餐的订单
	 * @param employeeIDs 职工号序列，以逗号隔开
	 * @param type 中餐/午餐/零点餐
	 * @param date1 起始日期
	 * @param date2 终止日期
	 * @return 订单集合
	 */
	public static List<OrderView> getOrders(Connection conn, String employeeIDs, byte type, String date1, String date2){
		PreparedStatement pst = null;
		ResultSet rs = null;
		
		//先处理成带单引号的字符串序列，否则在sql语句中可能会存在问题
		String []ids = employeeIDs.split(",");
		String str = "";
		for(int i=0; i<ids.length; i++){
			str += "'"+ids[i]+"'";
			if(i < ids.length-1){
				str += ",";
			}
		}
		
		List<OrderView> orders = new ArrayList<OrderView>();		
		try {
			if (type == Price.ALLDAY) {
				String sql = String.format("select * from OrderView where eaterID in (%s) and eatDate between ? and ?",str);
				pst = conn.prepareStatement(sql);
				pst.setString(1, date1);
				pst.setString(2, date2);
			}else{
				String sql = String.format("select * from OrderView where eaterID in (%s) and type = ? and eatDate between ? and ?",str);
				pst = conn.prepareStatement(sql);
				pst.setByte(1, type);
				pst.setString(2, date1);
				pst.setString(3, date2);
			}
			rs = pst.executeQuery();
			
			while (rs.next()) {
				OrderView order = getOrderView(conn,rs);
				orders.add(order);
			}
			rs.close();	
			pst.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return orders;
	}
	
	public static List<OrderViewSimple> getOrders(Connection conn, String employeeIDs, byte type, String date1, String date2, int carteenID){
		//先处理成带单引号的字符串序列，否则在sql语句中可能会存在问题
		String []ids = employeeIDs.split(",");
		String str = "";
		for(int i=0; i<ids.length; i++){
			str += "'"+ids[i]+"'";
			if(i < ids.length-1){
				str += ",";
			}
		}
		
		List<OrderViewSimple> orders = new ArrayList<OrderViewSimple>();	
		String sql = String.format("select * from OrderViewsimple where eaterID in (%s) and eatDate between '%s' and '%s'",str,date1,date2);
		if(carteenID != 0) {
			sql +=String.format(" and placeID in (select ID from place where carteenID = %d)",carteenID);
		}
		if(type != Price.ALLDAY){
			sql += (" and type = "+type);
		}
		try {
			Statement st = conn.createStatement();
			ResultSet rs = st.executeQuery(sql);			
			while (rs.next()) {
				OrderViewSimple order = getOrderViewSimple(conn,rs);
				orders.add(order);
			}
			rs.close();	
			st.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return orders;
	}
	
	//获取视图orderviewsimple中的数据
	private static OrderViewSimple getOrderViewSimple(Connection conn,
			ResultSet rs) {
		try {
			OrderViewSimple order = new OrderViewSimple();
			order.setID(rs.getInt("ID"));
			order.setType(rs.getByte("type"));
			order.setEaterID(rs.getString("eaterID"));
			order.setOrdererID(rs.getString("ordererID"));
			order.setEaterName(rs.getString("eaterName"));
			order.setOrdererName(rs.getString("ordererName"));
			order.setEatDate(rs.getDate("eatDate").toString());
			order.setOrderDate(rs.getDate("orderDate").toString());
			order.setPlaceID(rs.getShort("placeID"));
			order.setPlaceName(rs.getString("placeName"));
			order.setDepartmentName(rs.getString("departmentName"));
			order.setPrice(rs.getFloat("price"));
			order.setAdditional(rs.getString("additional"));
			
			//如果是订餐人是管理员，那么orderName应该为空，此时应从管理员表中去获取
			if(order.getOrdererName() == null){
				Admin admin = AdminDAO.getAdmin(conn, order.getOrdererID());
				order.setOrdererName(admin.getName());
			}
			return order;
		} catch (SQLException e) {
			System.out.print("从数据库中订单视图信息出错，请检查字段有无拼写错误");
			return null;
		}
	}

	/**
	 * 获取指定部门、送餐点的订单视图集合（getTotalOfDepartment方法内部使用）
	 * @param conn
	 * @param placeID
	 * @param departmentID
	 * @param type
	 * @param date1
	 * @param date2
	 * @return
	 */
	private static List<OrderView> getOrders(Connection conn, short placeID, int departmentID, byte type, String date1, String date2) {
		
		PreparedStatement pst = null;
		ResultSet rs = null;
		
		List<OrderView> orders = new ArrayList<OrderView>();		
		try {
			pst = conn.prepareStatement("select * from orderView where placeID = ? and departmentID = ? and type = ? and eatDate between ? and ?");
			pst.setInt(1, placeID);
			pst.setInt(2, departmentID);
			pst.setByte(3, type);
			pst.setString(4, date1);
			pst.setString(5, date2);
			
			rs = pst.executeQuery();

			while (rs.next()) {
				OrderView order = getOrderView(conn,rs);
				orders.add(order);
			}
			rs.close();	
			pst.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return orders;
	}

	/**
	 * 获取一段时间内的某餐订单，食堂备餐需要
	 * @param type 中餐、晚餐、零点餐
	 * @param date1 起始日期
	 * @param date2 终止日期
	 * @param carteenID  
	 * @return 订单集合
	 */
	public List<OrderView> getOrders(Connection conn, String date1, String date2, byte type, int carteenID){		
		PreparedStatement pst = null;
		ResultSet rs = null;
		
		List<OrderView> orders = new ArrayList<OrderView>();		
		try {
			String sql = "select * from OrderView where type = ? and eatDate between ? and ? and placeID in (select ID from place where carteenID = ?)";
			pst = conn.prepareStatement(sql);
			pst.setByte(1, type);
			pst.setString(2, date1);
			pst.setString(3, date2);
			pst.setInt(4, carteenID);

			rs = pst.executeQuery();
			while (rs.next()) {
				OrderView order = getOrderView(conn,rs);
				orders.add(order);
			}
			rs.close();	
			pst.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return orders;
	}
	
	/**
	 * 获取订单视图集合（备餐用）
	 * @param date1
	 * @param date2
	 * @param type
	 * @param additional
	 * @param carteenID:食堂编号
	 * @return
	 */
	public static List<OrderView> getOrders(Connection conn, String date1, String date2, byte type, String additional, int carteenID) {
		PreparedStatement pst = null;
		ResultSet rs = null;
		
		List<OrderView> orders = new ArrayList<OrderView>();		
		try {
			String sql = "";
			if (type == Price.MIDNIGHT) {
				if(carteenID != 0) {
					sql = "select * from OrderView where type = ? and additional=? and eatDate between ? and ? and placeID in (select ID from place where carteenID = ?)";
				}else {
					sql = "select * from OrderView where type = ? and additional=? and eatDate between ? and ? ";
				}
					pst = conn.prepareStatement(sql);
				pst.setByte(1, type);
				pst.setString(2, additional);
				pst.setString(3, date1);
				pst.setString(4, date2);
				if (carteenID != 0) {
				pst.setInt(5, carteenID);
				}
			}else{
				if(carteenID != 0) {
					sql = "select * from OrderView where type = ? and eatDate between ? and ? and placeID in (select ID from place where carteenID = ?)";
				}else {
					sql = "select * from OrderView where type = ? and eatDate between ? and ? ";
				}
				pst = conn.prepareStatement(sql);
				pst.setByte(1, type);
				pst.setString(2, date1);
				pst.setString(3, date2);
				if(carteenID != 0) {
					pst.setInt(4, carteenID);
				}
			}
			rs = pst.executeQuery();
			while (rs.next()) {
				OrderView order = getOrderView(conn,rs);
				orders.add(order);
			}
			rs.close();	
			pst.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return orders;
	}

	/**
	 * 获取指定公司和班组的订餐明细
	 * @param companyID
	 * @param departmentID
	 * @param date1
	 * @param date2
	 * @param carteenID2 
	 * @return
	 */
	public static List<OrderView> getOrdersOfDepartment(Connection conn,  int departmentID, String date1, String date2, byte type, int carteenID) {
		
		PreparedStatement pst = null;
		ResultSet rs = null;
		
		List<OrderView> orders = new ArrayList<OrderView>();		
		try {
			String sql = "select * from OrderView where departmentID = ? and eatDate between ? and ? ";
			if (type != 0) {
				sql += String.format(" and type=%d", type);
			}
			if(carteenID != 0) {
				sql += String.format(" and placeID in (select ID from place where carteenID = %d)",carteenID);
			}
			pst = conn.prepareStatement(sql);
			pst.setInt(1, departmentID);
			pst.setString(2, date1);
			pst.setString(3, date2);
			rs = pst.executeQuery();

			while (rs.next()) {
				OrderView order = getOrderView(conn,rs);
				orders.add(order);
			}
			rs.close();	
			pst.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return orders;
	}
	
public static List<OrderView> getOrdersOfWorkshop(Connection conn, int workshopID, String date1, String date2,int carteenID, byte type) {
		
		PreparedStatement pst = null;
		ResultSet rs = null;
		
		List<OrderView> orders = new ArrayList<OrderView>();		
		try {
			String sql = "select * from OrderView where workshopID = ? and eatDate between ? and ? ";

			if(carteenID != 0) {
				sql += String.format(" and placeID in (select ID from place where carteenID = %d)",carteenID);
			}
			if (type != 0) {
				sql += String.format(" and type= %d",type);
			}
			pst = conn.prepareStatement(sql);
			pst.setInt(1, workshopID);
			pst.setString(2, date1);
			pst.setString(3, date2);
			rs = pst.executeQuery();

			while (rs.next()) {
				OrderView order = getOrderView(conn,rs);
				orders.add(order);
			}
			rs.close();	
			pst.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return orders;
	}

	private static List<OrderForPriceAndHoliday> getOrdersOfCompany(Connection conn, int companyID, int workshhopID, int departmentID, String date1, String date2) {
		
		PreparedStatement pst = null;
		ResultSet rs = null;
		
		List<OrderForPriceAndHoliday> orders = new ArrayList<OrderForPriceAndHoliday>();		
		try {
			String sql = String.format("select ID,eatDate,type from OrderView where companyID = %d",companyID);
			if(workshhopID != 0) {
				sql += String.format(" and workshopID = %d", workshhopID);
			}
			if(departmentID != 0) {
				sql += String.format(" and departmentID=%d", departmentID);
			}
			sql += String.format(" and eatDate between '%s' and '%s' ORDER BY eatDate,type", date1,date2);
			pst = conn.prepareStatement(sql);

			rs = pst.executeQuery();

			while (rs.next()) {
				OrderForPriceAndHoliday order = getOrderForPriceAndHoliday(conn,rs);
				orders.add(order);
			}
			rs.close();	
			pst.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return orders;
	}
	
	private static OrderForPriceAndHoliday getOrderForPriceAndHoliday(Connection conn,ResultSet rs) {
		try {
			OrderForPriceAndHoliday order = new OrderForPriceAndHoliday();
			order.setID(rs.getInt("ID"));
			order.setType(rs.getByte("type"));
			order.setEatDate(rs.getDate("eatDate").toString());
			return order;
		} catch (SQLException e) {
			System.out.print("从数据库中订单信息出错，请检查字段有无拼写错误");
			return null;
		}
	}

	/**
	 * 生成价格和假日信息（同时时需要）
	 * @param companyID
	 * @param date1
	 * @param date2
	 */
	public static void generatePriceAndHoliday(Connection conn, int companyID, int workshopID, int departmentID, String date1, String date2) {
		//获取订单列表
		//List<OrderView> orders = getOrdersOfCompany(conn,companyID, date1, date2);
		List<OrderForPriceAndHoliday> orders = getOrdersOfCompany(conn, companyID, workshopID, departmentID, date1, date2);
		//计算价格，根据套餐类别、是否为假日、是否录属于例外的公司来计算
		boolean isException = CompanyDAO.getCompany(conn,companyID).isException();
		
		String d = "";
		byte type = -1;
		float price = 0;
		boolean isHoliday = false;
		String idString = "";
		for(OrderForPriceAndHoliday order:orders){
			if(!d.equals(order.getEatDate()) || type!=order.getType()){
				//执行之前的数据修改
				if(idString.length()>0){
					idString = idString.substring(0,idString.length()-1);
					OrderDAO.updatePriceAndHoliday(conn,idString,price,isHoliday);
					
					idString = "";
				}
				
				d = order.getEatDate();
				type = order.getType();
				isHoliday = HolidayDAO.isHoliday(conn,d,type);
				byte category = isException?Price.EXCEPTION:(isHoliday?Price.HOLIDAY:Price.NORMAL);
				price = PriceDAO.getPrice(conn,d,type, category);
				
				
			}	
			idString += order.getID()+",";
		}
		
		//执行最后一批数据的修改
		if(idString.length()>0){
			idString = idString.substring(0,idString.length()-1);
			OrderDAO.updatePriceAndHoliday(conn,idString,price,isHoliday);
		}
		
	}

	
	/**
	 * 批量修改订单的价格和假日信息
	 * @param conn 数据库连接
	 * @param ids 待修改的订单id序列
	 * @param price 价格
	 * @param isHoliday 是否为假日
	 */
	private static void updatePriceAndHoliday(Connection conn, String ids,	float price, boolean isHoliday) {
		PreparedStatement pst = null;
		
		try {
			pst = conn.prepareStatement(String.format("update orders set price=?,isHoliday=? where ID in(%s)",ids));
			pst.setFloat(1,price);
			pst.setBoolean(2, isHoliday);
			pst.executeUpdate();	
			pst.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private static OrderView getOrderView(Connection conn, ResultSet rs) {
		try {
			OrderView order = new OrderView();
			order.setID(rs.getInt("ID"));
			order.setType(rs.getByte("type"));
			order.setEaterID(rs.getString("eaterID"));
			order.setOrdererID(rs.getString("ordererID"));
			order.setEaterName(rs.getString("eaterName"));
			order.setOrdererName(rs.getString("ordererName"));
			order.setEatDate(rs.getDate("eatDate").toString());
			order.setOrderDate(rs.getDate("orderDate").toString());
			order.setPlaceID(rs.getShort("placeID"));
			order.setPlaceName(rs.getString("placeName"));
			//order.setDepartmentID(rs.getInt("departmentID"));
			order.setDepartmentName(rs.getString("departmentName"));
			//order.setWorkshopId(rs.getInt("workshopId"));
			//order.setWorkshopName(rs.getString("workshopName"));
			//order.setCompanyID(rs.getInt("companyID"));
			order.setPrice(rs.getFloat("price"));
			order.setAdditional(rs.getString("additional"));
			//order.setHoliday(rs.getBoolean("isHoliday"));
			
			//如果是订餐人是管理员，那么orderName应该为空，此时应从管理员表中去获取
			if(order.getOrdererName() == null){
				Admin admin = AdminDAO.getAdmin(conn, order.getOrdererID());
				order.setOrdererName(admin.getName());
			}
			return order;
		} catch (SQLException e) {
			System.out.print("从数据库中订单视图信息出错，请检查字段有无拼写错误");
			return null;
		}
	}

	private static Order getOrder(ResultSet rs) {
		try {
			Order order = new Order();
			order.setID(rs.getInt("ID"));
			order.setType(rs.getByte("type"));
			order.setEaterID(rs.getString("eaterID"));
			order.setOrdererID(rs.getString("ordererID"));
			order.setEatDate(rs.getDate("eatDate").toString());
			order.setOrderDate(rs.getDate("orderDate").toString());
			order.setPlaceID(rs.getShort("placeID"));
			order.setDepartmentID(rs.getShort("departmentID"));
			order.setPrice(rs.getFloat("price"));
			order.setAdditional(rs.getString("additional"));
			order.setHoliday(rs.getBoolean("isHoliday"));
			return order;
		} catch (SQLException e) {
			System.out.print("从数据库中订单信息出错，请检查字段有无拼写错误");
			return null;
		}
	}

	public static int getOrders(Connection conn, String date1,String date2, int companyID, int workshopID, int departmentID, short placeID, int carteenID, int type, String idOrName, List<OrderView>orders, int page, int rows) {
		String where = String.format("eatDate between '%s' and '%s'", date1,date2);
		if (type != 0) {
			where += " and type="+type;
		}
		if (companyID != 0) {
			where += " and companyID="+companyID;
		}
		if (workshopID != 0) {
			where += " and workshopID="+workshopID;
		}
		if (departmentID != 0) {
			where += " and departmentID="+departmentID;
		}
		if(placeID != 0 ) {
			where += " and placeID="+placeID;
		}
		if(carteenID != 0) {
			where += " and carteenID="+carteenID;
		}
		if (!idOrName.isEmpty()) {
			where += String.format(" and (eaterID = '%s' or eaterName='%s')",idOrName,idOrName);
		}
		PreparedStatement pst = null;
		ResultSet rs = null;		
				
		int totals = 0;
		String sql1 = "select count(*) as totals from orderView where "+where;
		where += String.format(" limit %d,%d", rows*(page-1),rows);
		String sql = "select * from orderView where "+where;
		
		try {
			pst = conn.prepareStatement(sql1);
			rs = pst.executeQuery();
			if(rs.next()) {
				totals = rs.getInt(1);
			}
			pst = conn.prepareStatement(sql);
			rs = pst.executeQuery();
			
			while (rs.next()) {
				OrderView order = getOrderView(conn, rs);
				orders.add(order);
			}
			
			rs.close();	
			pst.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return totals;
	}

	public static void addOrders(Connection conn, List<Order> orders) {
		try {
			PreparedStatement pst = null;
			pst = conn.prepareStatement("insert into orders(type,eaterID,ordererID,eatDate,orderDate,placeID,departmentID,price,additional,isHoliday) values(?,?,?,?,?,?,?,?,?,?)");
			for (int i= 0; i < orders.size(); i++) {
				pst.setByte(1, orders.get(i).getType());
				pst.setString(2, orders.get(i).getEaterID());
				pst.setString(3, orders.get(i).getOrdererID());
				pst.setString(4, orders.get(i).getEatDate());
				pst.setString(5, orders.get(i).getOrderDate());
				pst.setInt(6, orders.get(i).getPlaceID());
				pst.setInt(7, orders.get(i).getDepartmentID());
				pst.setFloat(8, orders.get(i).getPrice());
				pst.setString(9, orders.get(i).getAdditional());
				pst.setBoolean(10, orders.get(i).isHoliday());
				pst.addBatch();
			}
			pst.executeBatch();
			pst.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static void deleteOrders(Connection conn, List<OrderView> successDel) {
		PreparedStatement pst = null;		
		try {
			pst = conn.prepareStatement("delete from orders where ID = ?");
			for (int i = 0; i < successDel.size(); i++) {
				pst.setLong(1,successDel.get(i).getID());
				pst.addBatch();
			}
			pst.executeBatch();
			pst.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
}
