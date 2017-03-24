package DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.Date;

import bean.Price;
import bean.Regular;

public class RegularDAO {
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
	}
	public static void updateRegular(Connection conn, Regular regular){		
		PreparedStatement pst = null;		
		try {
			pst = conn.prepareStatement("update regular set days=?,lunchTime=?,dinnerTime=?,midnightTime=?");
			pst.setInt(1, regular.getDays());
			pst.setString(2,  regular.getLunch());
			pst.setString(3,  regular.getDinner());
			pst.setString(4,  regular.getMidnight());
			pst.executeUpdate();	
			pst.close();			
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static Regular getRegular(Connection conn){
		
		PreparedStatement pst = null;
		ResultSet rs = null;
		
		Regular regular = null;
		try {
			pst = conn.prepareStatement("select * from regular");
			rs = pst.executeQuery();
			if (rs.next()) {
				regular = getRegular(rs);
			}
			rs.close();	
			pst.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return regular;
	}
	
	/**
	 * 判断某日某餐订餐是否截止
	 * @param conn 数据库连接
	 * @param date 日期
	 * @param type 零点餐/午餐/晚餐
	 * @return 是否截止
	 */
	public static boolean isExpire(Connection conn, String date, byte type) {		
		PreparedStatement pst = null;
		ResultSet rs = null;
		
		String sql = "";
		switch (type) {
		case Price.LUNCH://截止的条件是日期已过，或者是同一天但时间过了
			sql = "select (DATEDIFF(?,CURDATE())<0) or (DATEDIFF(?,CURDATE())=0 and TIMEDIFF(lunchTime, curtime())<0) as expire from regular";
			break;
		case Price.DINNER://晚餐应该在前一天指定时间之前才能订餐
			sql = "select (DATEDIFF(?,CURDATE())<1) or (DATEDIFF(?,CURDATE())=1 and TIMEDIFF(dinnerTime, curtime())<0) as expire from regular";
			break;
		case Price.MIDNIGHT://零点餐应该在前两天指定时间之前才能订餐
			sql = "select (DATEDIFF(?,CURDATE())<2) or (DATEDIFF(?,CURDATE())=2 and TIMEDIFF(midnightTime, curtime())<0) as expire from regular";
			break;
		default:
			break;
		}
		try {
			pst = conn.prepareStatement(sql);
			pst.setString(1, date);
			pst.setString(2, date);
			rs = pst.executeQuery();
			if (rs.next()) {
				return rs.getBoolean("expire");
			}
			rs.close();	
			pst.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return false;
	}
	
	private static Regular getRegular(ResultSet rs){
		try {
			Regular regular = new Regular();
			regular.setDays(rs.getInt("days"));
			regular.setLunch(rs.getString("lunchTime"));
			regular.setDinner(rs.getString("dinnerTime"));
			regular.setMidnight(rs.getString("midnightTime"));
			return regular;
		} catch (SQLException e) {
			System.out.print("从数据库中提取订餐规则信息出错，请检查字段有无拼写错误");
			return null;
		}
	}

}
