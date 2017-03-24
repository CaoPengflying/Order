package DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import bean.Employee;
import bean.WorkType;

public class WorkTypeDAO {
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
	}
	public static void addWorkType(Connection conn, WorkType type){
		
		PreparedStatement pst = null;
		
		try {
			pst = conn.prepareStatement("insert into workType(name,lunch,dinner,midnight) values(?,?,?,?)");
			pst.setString(1, type.getName());
			pst.setInt(2, type.getLunch());
			pst.setInt(3, type.getDinner());
			pst.setInt(4, type.getMidnight());
			pst.executeUpdate();	
			pst.close();			
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static WorkType getWorkType(Connection conn, int typeID){
		
		PreparedStatement pst = null;
		ResultSet rs = null;
		
		WorkType type = null;
		try {
			pst = conn.prepareStatement("select * from workType where ID = ?");
			pst.setInt(1, typeID);
			rs = pst.executeQuery();	
			if (rs.next()) {
				type = getWorkType(rs);
			}
			rs.close();	
			pst.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return type;
	}
	
	public static void updateWorkType(Connection conn, WorkType type){		
		PreparedStatement pst = null;
		
		try {
			pst = conn.prepareStatement("update workType set name=?,lunch=?,dinner=?,midnight=?,deleted=? where ID = ?");
			pst.setString(1, type.getName());
			pst.setInt(2, type.getLunch());
			pst.setInt(3, type.getDinner());
			pst.setInt(4, type.getMidnight());
			pst.setBoolean(5, type.isDeleted());
			pst.setInt(6, type.getID());
			pst.executeUpdate();	
			pst.close();			
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static List<WorkType> getWorkTypes(Connection conn){		
		PreparedStatement pst = null;
		ResultSet rs = null;
		
		List<WorkType> types = new ArrayList<WorkType>();
		try {
			pst = conn.prepareStatement("select * from workType where deleted = false");
			rs = pst.executeQuery();
			while (rs.next()) {
				WorkType type = getWorkType(rs);
				types.add(type);
			}
			rs.close();	
			pst.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return types;
	}
	
	public static void setNumbers(Connection conn, Employee employee){
		WorkType type = getWorkType(conn,employee.getWorkTypeID());
		
		if (type == null) {
			return;
		}

		employee.setLunch(type.getLunch());
		employee.setDinner(type.getDinner());
		employee.setMidnight(type.getMidnight());
	}
	
	private static WorkType getWorkType(ResultSet rs){
		try {
			WorkType type = new WorkType();
			type.setID(rs.getByte("ID"));
			type.setName(rs.getString("name"));
			type.setLunch(rs.getShort("lunch"));
			type.setDinner(rs.getShort("dinner"));
			type.setMidnight(rs.getShort("midnight"));
			type.setDeleted(rs.getBoolean("deleted"));
			return type;
		} catch (SQLException e) {
			System.out.print("从数据库中提取倒班信息出错，请检查字段有无拼写错误");
			return null;
		}
	}
}
