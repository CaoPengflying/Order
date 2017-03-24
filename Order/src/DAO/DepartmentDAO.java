package DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import View.DepartmentView;
import bean.Department;
import bean.Department;
import bean.Department;

public class DepartmentDAO {
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
	
	public static void addDepartment(Connection conn, Department department){		
		PreparedStatement pst = null;		
		try {
			pst = conn.prepareStatement("insert into department(ID,name,workshopID,phone) values(?,?,?,?)");
			pst.setInt(1, department.getID());
			pst.setString(2, department.getName());
			pst.setInt(3, department.getWorkshopID());
			pst.setString(4, department.getPhone());
			pst.executeUpdate();		
			pst.close();		
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	
	public static void updateDepartment(Connection conn, Department department){		
		PreparedStatement pst = null;		
		try {
			pst = conn.prepareStatement("update department set name=?,workshopID=?,phone=?,deleted=? where ID = ?");
			pst.setString(1, department.getName());
			pst.setInt(2, department.getWorkshopID());
			pst.setString(3, department.getPhone());
			pst.setBoolean(4, department.isDeleted());
			pst.setInt(5, department.getID());
			pst.executeUpdate();		
			pst.close();		
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static Department getDepartment(Connection conn, int departmentID){		
		PreparedStatement pst = null;
		ResultSet rs = null;		
		Department department = null;
		try {
			pst = conn.prepareStatement("select * from department where ID = ?");
			pst.setInt(1, departmentID);
			rs = pst.executeQuery();
			if (rs.next()) {
				department = getDepartment(rs);
			}
			rs.close();	
			pst.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return department;
	}

	
	
	/**
	 * 获取车间的班组
	 * @param workshopID 车间号
	 * @return 班组列表
	 */
	public static List<DepartmentView> getDepartments(Connection conn, short workshopID){		
		PreparedStatement pst = null;
		ResultSet rs = null;		
		List<DepartmentView> departments = new ArrayList<DepartmentView>();
		try {
			pst = conn.prepareStatement("select * from departmentView where workshopID = ? and deleted = false");
			pst.setInt(1, (int)workshopID);
			rs = pst.executeQuery();
			while (rs.next()) {
				DepartmentView department = getDepartmentView(rs);
				departments.add(department);
			}
			rs.close();	
			pst.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return departments;
	}
	
	
	private static Department getDepartment(ResultSet rs){
		try {
			Department department = new Department();
			department.setID(rs.getShort("ID"));
			department.setName(rs.getString("name"));
			department.setPhone(rs.getString("phone"));
			department.setWorkshopID(rs.getShort("workshopID"));
			department.setDeleted(rs.getBoolean("deleted"));
			return department;
		} catch (SQLException e) {
			System.out.print("从数据库中提取部门信息出错，请检查字段有无拼写错误");
			return null;
		}
	}

	private static DepartmentView getDepartmentView(ResultSet rs) {
		try {
			DepartmentView department = new DepartmentView();
			department.setID(rs.getShort("ID"));
			department.setName(rs.getString("name"));
			department.setPhone(rs.getString("phone"));
			department.setWorkshopID(rs.getShort("workshopID"));
			department.setWorkshopName(rs.getString("workshopName"));
			department.setDeleted(rs.getBoolean("deleted"));
			return department;
		} catch (SQLException e) {
			System.out.print("从数据库中提取部门信息出错，请检查字段有无拼写错误");
			return null;
		}
	}
}
