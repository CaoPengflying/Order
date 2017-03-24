package DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import View.WorkshopView;
import bean.Workshop;

public class WorkshopDAO {
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
	public static void addWorkshop(Connection conn, Workshop workshop){
		
		PreparedStatement pst = null;
		
		try {
			pst = conn.prepareStatement("insert into workshop(name,phone,companyID) values(?,?,?)");
			pst.setString(1, workshop.getName());
			pst.setString(2, workshop.getPhone());
			pst.setShort(3, workshop.getCompanyID());
			pst.executeUpdate();		
			pst.close();		
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	
	public static void updateWorkshop(Connection conn, Workshop workshop){		
		PreparedStatement pst = null;		
		try {
			pst = conn.prepareStatement("update workshop set name=?,phone=?,companyID=?,deleted=? where ID = ?");
			pst.setString(1, workshop.getName());
			pst.setString(2, workshop.getPhone());
			pst.setShort(3, workshop.getCompanyID());
			pst.setBoolean(4, workshop.isDeleted());
			pst.setInt(5, workshop.getID());
			pst.executeUpdate();		
			pst.close();		
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static WorkshopView getWorkshop(Connection conn, int workshopID){
		
		PreparedStatement pst = null;
		ResultSet rs = null;
		
		WorkshopView workshop = null;
		try {
			pst = conn.prepareStatement("select * from workshopView where ID = ?");
			pst.setInt(1, workshopID);
			rs = pst.executeQuery();
			if (rs.next()) {
				workshop = getWorkshop(rs);
			}
			rs.close();	
			pst.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return workshop;
	}

	public static int getWorkShops(Connection conn, int page, int rows, List<WorkshopView> workshops, Short companyID) {		
		PreparedStatement pst = null;
		ResultSet rs = null;
		
		int offset = (page-1)*rows;
		
		String sql1 = "select count(ID) as totals from workshopview where deleted = false";
		if(companyID != 0){
			sql1 += String.format(" and companyID=%d", companyID);
		}
		String sql2 = "select * from workshopview where deleted = false";
		if(companyID != 0){
			sql2 += String.format(" and companyID=%d", companyID);
		}
		if (page != 0) {//需要分页			
			sql2 += String.format(" limit %d,%d", offset, rows);
		}
		int totals = 0;
		
		try {
			pst = conn.prepareStatement(sql1);
			rs = pst.executeQuery();
			if (rs.next()) {
				totals = rs.getInt("totals");

				pst = conn.prepareStatement(sql2);
				rs = pst.executeQuery();
				while (rs.next()) {
					WorkshopView workshop = getWorkshop(rs);
					workshops.add(workshop);
				}
			}
			rs.close();	
			pst.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return totals;
	}
	
	
	private static WorkshopView getWorkshop(ResultSet rs){
		try {
			WorkshopView workshop = new WorkshopView();
			workshop.setID(rs.getShort("ID"));
			workshop.setName(rs.getString("name"));
			workshop.setPhone(rs.getString("phone"));
			workshop.setCompanyID(rs.getShort("companyID"));
			workshop.setDeleted(rs.getBoolean("deleted"));
			workshop.setCompanyName(rs.getString("companyName"));
			return workshop;
		} catch (SQLException e) {
			System.out.print("从数据库中提取车间信息出错，请检查字段有无拼写错误");
			return null;
		}
	}
	public static int getWorkShopsByCompanyID(Connection conn, short companyID) {
		PreparedStatement pst = null;
		ResultSet rs = null;
		int totals = 0;
		try {
			pst = conn.prepareStatement("select count(ID) as totals from workshop where companyID = ?");
			pst.setShort(1, companyID);
			rs = pst.executeQuery();
			if(rs.next()) {
				totals = rs.getInt("totals");
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("获取车间的数量失败");
			return totals;
			
		}
		return totals;
	}

}
