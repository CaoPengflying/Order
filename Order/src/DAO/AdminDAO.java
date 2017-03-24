package DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import bean.Admin;

public class AdminDAO {
	
	
	/**
	 * 添加管理员
	 * @param admin 管理员
	 */
	public static void addAdmin(Connection conn, Admin admin){		
		PreparedStatement pst = null;		
		try {
			pst = conn.prepareStatement("insert into admin(ID,name,password,phone,permission,carteenID) values(?,?,?,?,?,?)");
			pst.setString(1, admin.getID());
			pst.setString(2, admin.getName());
			pst.setString(3, admin.getPassword());
			pst.setString(4, admin.getPhone());
			pst.setInt(5, admin.getPermission());
			pst.setInt(6, admin.getCarteenID());
			pst.executeUpdate();	
			pst.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 更新管理员
	 * @param admin 管理员
	 */
	public static void updateAdmin(Connection conn, Admin admin){		
		PreparedStatement pst = null;		
		try {
			pst = conn.prepareStatement("update admin set name=?,password=?,phone=?,carteenID=?,permission=? where ID = ?");
			pst.setString(1, admin.getName());
			pst.setString(2, admin.getPassword());
			pst.setString(3, admin.getPhone());
			pst.setInt(4, admin.getCarteenID());
			pst.setInt(5, admin.getPermission());
			pst.setString(6, admin.getID());
			pst.executeUpdate();	
			pst.close();			
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	

	/**
	 * 删除管理员
	 * @param conn 数据库连接
	 * @param id 管理员编号
	 */
	public static void delAdmin(Connection conn, String id) {
		try {
			PreparedStatement pst = conn.prepareStatement("delete from admin where ID = ?");
			pst.setString(1, id);
			pst.executeUpdate();	
			pst.close();			
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}	
	
	
	/**
	 * 获取指定管理员
	 * @param adminID 管理员号
	 * @return 返回管理员信息
	 */
	public static Admin getAdmin(Connection conn, String adminID){		
		PreparedStatement pst = null;
		ResultSet rs = null;		
		Admin admin = null;
		try {
			pst = conn.prepareStatement("select * from admin where ID = ?");
			pst.setString(1, adminID);
			rs = pst.executeQuery();
			if (rs.next()) {
				admin = getAdmin(rs);
			}
			rs.close();	
			pst.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return admin;
	}

	/**
	 * 获取管理员
	 * @return 管理员列表
	 */
	public static List<Admin> getAdimins(Connection conn){
		
		PreparedStatement pst = null;
		ResultSet rs = null;
		
		List<Admin> admins = new ArrayList<Admin>();		
		try {
			pst = conn.prepareStatement("select * from admin where role=?");
			pst.setByte(1, Admin.ROLE_ADMIN);
			rs = pst.executeQuery();
			while (rs.next()) {
				Admin admin = getAdmin(rs);
				admins.add(admin);
			}
			rs.close();	
			pst.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return admins;
	}
	
	private static Admin getAdmin(ResultSet rs){
		try {
			Admin admin = new Admin();
			admin.setID(rs.getString("ID"));
			admin.setName(rs.getString("name"));
			admin.setPassword(rs.getString("password"));
			admin.setPhone(rs.getString("phone"));
			admin.setCarteenID(rs.getInt("carteenID"));
			admin.setPermission(rs.getInt("permission"));
			admin.setRole(rs.getByte("role"));
			return admin;
		} catch (SQLException e) {
			System.out.print("从数据库中提取管理员信息出错，请检查字段有无拼写错误");
			return null;
		}
	}
}
