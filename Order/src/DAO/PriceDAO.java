package DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import bean.Price;

public class PriceDAO {
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
	}

	public static void addPrice(Connection conn, Price price){		
		PreparedStatement pst = null;		
		try {
			pst = conn.prepareStatement("insert into price(date_start,date_end,lunch_normal,lunch_holiday,lunch_exception,dinner_normal,dinner_holiday,dinner_exception,midnight_normal,midnight_holiday,midnight_exception) values(?,?,?,?,?,?,?,?,?,?,?)");
			pst.setString(1, price.getDate_start());
			pst.setString(2, price.getDate_end());
			pst.setFloat(3, price.getLunch_normal());
			pst.setFloat(4, price.getLunch_holiday());
			pst.setFloat(5, price.getLunch_exception());
			pst.setFloat(6, price.getDinner_normal());
			pst.setFloat(7, price.getDinner_holiday());
			pst.setFloat(8, price.getDinner_exception());
			pst.setFloat(9, price.getMidnight_normal());
			pst.setFloat(10, price.getMidnight_holiday());
			pst.setFloat(11, price.getMidnight_exception());
			pst.executeUpdate();	
			pst.close();			
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static void delPrice(Connection conn, int ID){		
		PreparedStatement pst = null;		
		try {
			pst = conn.prepareStatement("delete from price where ID = ?");
			pst.setInt(1, ID);	
			pst.executeUpdate();	
			pst.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static void updatePrice(Connection conn, Price price){		
		PreparedStatement pst = null;		
		try {
			pst = conn.prepareStatement("update price set date_start=?,date_end=?, lunch_normal=?,lunch_holiday=?,lunch_exception=?,dinner_normal=?,dinner_holiday=?,dinner_exception=?,midnight_normal=?,midnight_holiday=?,midnight_exception=? where ID = ?");
			pst.setString(1, price.getDate_start());
			pst.setString(2, price.getDate_end());
			pst.setFloat(3, price.getLunch_normal());
			pst.setFloat(4, price.getLunch_holiday());
			pst.setFloat(5, price.getLunch_exception());
			pst.setFloat(6, price.getDinner_normal());
			pst.setFloat(7, price.getDinner_holiday());
			pst.setFloat(8, price.getDinner_exception());
			pst.setFloat(9, price.getMidnight_normal());
			pst.setFloat(10, price.getMidnight_holiday());
			pst.setFloat(11, price.getMidnight_exception());
			pst.setInt(12, price.getID());
			
			pst.executeUpdate();	
			pst.close();			
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static Price getPrice(Connection conn, int ID){
		PreparedStatement pst = null;
		ResultSet rs = null;		
		Price price = null;
		try {
			pst = conn.prepareStatement("select * from price where ID = ?");
			pst.setInt(1, ID);
			rs = pst.executeQuery();
			if (rs.next()) {
				price = getPrice(rs);
			}
			rs.close();	
			pst.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return price;
	}

	public static Price getPrice(Connection conn, String date){		
		PreparedStatement pst = null;
		ResultSet rs = null;
		
		Price price = null;
		try {
			pst = conn.prepareStatement("select * from price where date_start <= ? and date_end >= ?");
			pst.setString(1, date);
			pst.setString(2, date);
			rs = pst.executeQuery();	
			if (rs.next()) {
				price = getPrice(rs);
			}
			rs.close();	
			pst.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return price;
	}
	
	/**
	 * 获取所有价格列表
	 * @return 价格列表
	 */
	public static List<Price> getPrices(Connection conn){		
		PreparedStatement pst = null;
		ResultSet rs = null;
		
		List<Price> prices = new ArrayList<Price>();
		try {
			pst = conn.prepareStatement("select * from price order by date_start desc");
			rs = pst.executeQuery();
			while (rs.next()) {
				Price price = getPrice(rs);
				prices.add(price);
			}
			rs.close();	
			pst.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return prices;
	}
	
	/**
	 * 获取指定日期的某餐的某个种类的价格
	 * @param date 日期
	 * @param type 类别（中餐/晚餐/零点餐）
	 * @param category 种类（日常/假日/例外)
	 * @return 价格
	 */
	public static float getPrice(Connection conn, String date, byte type, byte category){
		Price price = getPrice(conn,date);
		
		if (price == null) {
			return 0;
		}
		
		switch (type) {
		case Price.LUNCH:
			switch (category) {
			case Price.NORMAL:
				return price.getLunch_normal();
			case Price.HOLIDAY:
				return price.getLunch_holiday();
			case Price.EXCEPTION:
				return price.getLunch_exception();
			default:
				break;
			}
			break;
		case Price.DINNER:
			switch (category) {
			case Price.NORMAL:
				return price.getDinner_normal();
			case Price.HOLIDAY:
				return price.getDinner_holiday();
			case Price.EXCEPTION:
				return price.getDinner_exception();
			default:
				break;
			}
			break;
		case Price.MIDNIGHT:
			switch (category) {
			case Price.NORMAL:
				return price.getMidnight_normal();
			case Price.HOLIDAY:
				return price.getMidnight_holiday();
			case Price.EXCEPTION:
				return price.getMidnight_exception();
			default:
				break;
			}
			break;
		default:
			break;
		}
		
		
		return 0;
	}
	
	/**
	 * 判断指定的价格与现有价格的日期有无重叠
	 * @param price 价格
	 * @return 是否存在重叠
	 */
	public static boolean isOverlap(Connection conn, Price price) {
		
		PreparedStatement pst = null;
		ResultSet rs = null;
		boolean overlap = false;
		try {
			pst = conn.prepareStatement("SELECT * FROM price where Id != ? and (? BETWEEN date_start and date_end or ? BETWEEN date_start and date_end)");
			pst.setInt(1, price.getID());
			pst.setString(2, price.getDate_start());
			pst.setString(3, price.getDate_end());
			rs = pst.executeQuery();
			if (rs.next()) {
				overlap = true;
			}
			rs.close();	
			pst.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return overlap;
	}
	
	private static Price getPrice(ResultSet rs){
		try {
			Price price = new Price();
			price.setID(rs.getInt("ID"));
			price.setDate_start(rs.getString("date_start"));
			price.setDate_end(rs.getString("date_end"));
			price.setLunch_normal(rs.getFloat("lunch_normal"));
			price.setLunch_holiday(rs.getFloat("lunch_holiday"));
			price.setLunch_exception(rs.getFloat("lunch_exception"));
			price.setDinner_normal(rs.getFloat("dinner_normal"));
			price.setDinner_holiday(rs.getFloat("dinner_holiday"));
			price.setDinner_exception(rs.getFloat("dinner_exception"));
			price.setMidnight_normal(rs.getFloat("midnight_normal"));
			price.setMidnight_holiday(rs.getFloat("midnight_holiday"));
			price.setMidnight_exception(rs.getFloat("midnight_exception"));
			return price;
		} catch (SQLException e) {
			System.out.print("从数据库中提取送餐点信息出错，请检查字段有无拼写错误");
			return null;
		}
	}

	
}
