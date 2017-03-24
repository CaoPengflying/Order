package DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import bean.Holiday;
import bean.Price;

public class HolidayDAO {
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
	}
	
	public static void addHoliday(Connection conn, Holiday holiday){		
		PreparedStatement pst = null;		
		try {
			pst = conn.prepareStatement("insert into holiday(date,lunch,dinner,midnight) values(?,?,?,?)");
			pst.setString(1, holiday.getDate());
			pst.setBoolean(2, holiday.isLunch());
			pst.setBoolean(3, holiday.isDinner());
			pst.setBoolean(4, holiday.isMidnight());
			pst.executeUpdate();	
			pst.close();			
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static void delHoliday(Connection conn, String date){		
		PreparedStatement pst = null;		
		try {
			pst = conn.prepareStatement("delete from holiday where date = ?");
			pst.setString(1,date);
			pst.executeUpdate();		
			pst.close();		
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static void updateHoliday(Connection conn, Holiday holiday){		
		PreparedStatement pst = null;		
		try {
			pst = conn.prepareStatement("update holiday set lunch=?,dinner=?,midnight=? where date = ?");
			pst.setBoolean(1, holiday.isLunch());
			pst.setBoolean(2, holiday.isDinner());
			pst.setBoolean(3, holiday.isMidnight());
			pst.setString(4, holiday.getDate());
			pst.executeUpdate();	
			pst.close();			
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static List<Holiday> getHolidays(Connection conn){		
		PreparedStatement pst = null;
		ResultSet rs = null;		
		List<Holiday> holidays = new ArrayList<Holiday>();
		try {
			pst = conn.prepareStatement("select * from holiday");
			rs = pst.executeQuery();
			while (rs.next()) {
				Holiday holiday = getHoliday(rs);
				holidays.add(holiday);
			}
			rs.close();	
			pst.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return holidays;
	}

	public static boolean isHoliday(Connection conn, String eatDate, byte type){		
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			pst = conn.prepareStatement("select * from holiday where date=?");
			pst.setString(1, eatDate);
			rs = pst.executeQuery();	
			if (rs.next()) {
				Holiday holiday = getHoliday(rs);
				switch (type) {
				case Price.LUNCH:
					return holiday.isLunch();
				case Price.DINNER:
					return holiday.isDinner();
				case Price.MIDNIGHT:
					return holiday.isMidnight();
				}
				return false;
			}
			rs.close();	
			pst.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	private static Holiday getHoliday(ResultSet rs){
		try {
			Holiday holiday = new Holiday();
			holiday.setDate(rs.getString(1));
			holiday.setLunch(rs.getBoolean(2));
			holiday.setDinner(rs.getBoolean(3));
			holiday.setMidnight(rs.getBoolean(4));
			return holiday;
		} catch (SQLException e) {
			System.out.print("从数据库中提取节假日信息出错，请检查字段有无拼写错误");
			return null;
		}
	}
}
