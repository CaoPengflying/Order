package DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;


import View.EmployeeView;
import bean.Employee;

public class EmployeeDAO {
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Connection connection = DButil.getConnection();
		reset(connection);
	}
	
	/**
	 * 添加职工
	 * @param employee 职工
	 */
	public static void addEmployee(Connection conn, Employee employee){		
		PreparedStatement pst = null;		
		try {
			pst = conn.prepareStatement("insert into employee(ID,name,password,phone,workTypeID,companyID,workshopId,departmentID,placeIDs,role,lunch,dinner,midnight) values(?,?,?,?,?,?,?,?,?,?,?,?,?)");
			pst.setString(1, employee.getID());
			pst.setString(2, employee.getName());
			pst.setString(3, employee.getPassword());
			pst.setString(4, employee.getPhone());
			pst.setInt(5, employee.getWorkTypeID());
			pst.setInt(6, employee.getCompanyID());
			pst.setInt(7, employee.getWorkshopID());
			pst.setInt(8, employee.getDepartmentID());
			pst.setString(9, employee.getPlaceIDs());
			pst.setByte(10, employee.getRole());
			pst.setInt(11, employee.getLunch());
			pst.setInt(12, employee.getDinner());
			pst.setInt(13, employee.getMidnight());
			pst.executeUpdate();	
			pst.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 设置职工角色
	 * @param employee 职工
	 */
	public static void setRoles(Connection conn, Employee employee, byte roles){		
		PreparedStatement pst = null;		
		try {
			pst = conn.prepareStatement("update employee set role=? where ID = ?");
			pst.setByte(1, roles);	
			pst.setString(2, employee.getID());
			pst.executeUpdate();	
			pst.close();			
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	/**
	 * 更新职工
	 * @param employee 职工
	 */
	public static void updateEmployee(Connection conn, Employee employee){		
		PreparedStatement pst = null;		
		try {
			pst = conn.prepareStatement("update employee set name=?,password=?,phone=?,workTypeID=?,companyID=?,workshopID=?,departmentID=?,placeIDs=?,role=?,lunch=?,dinner=?,midnight=?,deleted=?,locked=? where ID = ?");
			pst.setString(1, employee.getName());
			pst.setString(2, employee.getPassword());
			pst.setString(3, employee.getPhone());
			pst.setInt(4, employee.getWorkTypeID());
			pst.setInt(5, employee.getCompanyID());
			pst.setInt(6, employee.getWorkshopID());
			pst.setInt(7, employee.getDepartmentID());
			pst.setString(8, employee.getPlaceIDs());
			pst.setByte(9, employee.getRole());
			pst.setInt(10, employee.getLunch());
			pst.setInt(11, employee.getDinner());
			pst.setInt(12, employee.getMidnight());
			pst.setBoolean(13, employee.isDeleted());
			pst.setByte(14, employee.getLock());
			pst.setString(15, employee.getID());
			pst.executeUpdate();	
			pst.close();			
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	
	public static void reset(Connection conn){
		PreparedStatement pst = null;		
		try {
			pst = conn.prepareStatement("UPDATE employee as a, worktype as b SET a.lunch = b.lunch,a.dinner=b.dinner,a.midnight=b.midnight where a.workTypeId = b.ID and a.locked=0 and a.deleted = 0");
			pst.executeUpdate();	
			pst.close();			
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 获取指定职工
	 * @param employeeID 职工号
	 * @return 返回职工信息
	 */
	public static Employee getEmployee(Connection conn, String employeeID){		
		PreparedStatement pst = null;
		ResultSet rs = null;		
		Employee employee = null;
		try {
			pst = conn.prepareStatement("select * from employee where ID = ?");
			pst.setString(1, employeeID);
			rs = pst.executeQuery();
			if (rs.next()) {
				employee = getEmployee(rs);
			}
			rs.close();	
			pst.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return employee;
	}
	
	public static List<Employee> getEmployees(Connection conn, String[] eaterIDs) {
		//形成形如“'1201','1202'”的序列
		String ids = "";
		for(int i=0; i<eaterIDs.length; i++){
			ids += "'"+eaterIDs[i]+"'";
			if(i < eaterIDs.length-1){
				ids += ",";
			}
		}
		
		List<Employee> employees = new ArrayList<Employee>();
		try {

			Statement st = conn.createStatement();
			ResultSet rs = st.executeQuery("select id,name,departmentID, lunch,dinner,midnight, locked from employee where ID in("+ids+")");
			
			while (rs.next()) {
				Employee employee = new Employee();
				employee.setID(rs.getString(1));
				employee.setName(rs.getString(2));
				employee.setDepartmentID(rs.getShort(3));
				employee.setLunch(rs.getShort(4));
				employee.setDinner(rs.getShort(5));
				employee.setMidnight(rs.getShort(6));
				employee.setLock(rs.getByte(7));
				employees.add(employee);
			}
			rs.close();	
			st.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return employees;
	}
	
	/**
	 * 获取指定职工
	 * @param employeeID 职工号
	 * @return 返回职工信息
	 */
	public static EmployeeView getEmployeeView(Connection conn, String employeeID){		
		PreparedStatement pst = null;
		ResultSet rs = null;		
		EmployeeView employee = null;
		try {
			pst = conn.prepareStatement("select * from employeeView where ID = ?");
			pst.setString(1, employeeID);
			rs = pst.executeQuery();
			if (rs.next()) {
				employee = getEmployeeView(rs);
			}
			rs.close();	
			pst.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return employee;
	}

	
	
	/**
	 * 根据ID或者姓名获取职工信息
	 * @param conn 数据库连接
	 * @param idOrName 工号或者姓名
	 * @return 职工信息集合
	 */
	public static int getEmployees(Connection conn, int companyID, int workshopID, int departmentID, int role, String idOrName, int page, int rows, List<EmployeeView> employees) {
		String where = "deleted = false";
		if(companyID != 0){
			where += " and companyID="+companyID;
		}
		if (departmentID != 0) {
			where += " and departmentID="+departmentID;
		}else if (workshopID != 0) {
			where += " and workshopID="+workshopID;
		}
		if(role != 0) {
			where += " and role="+role;
		}
		if (!idOrName.isEmpty()) {
			where += String.format(" and (ID = '%s' or name='%s')",idOrName,idOrName);
		}
		PreparedStatement pst = null;
		ResultSet rs = null;		
				
		
		String sql1 ="select count(ID) as totals from employeeView where "+where;
		String sql2 = "select * from employeeView where "+where;
		if (page != 0) {//需要分页	
			int offset = (page-1)*rows;
			sql2 += String.format(" limit %d,%d", offset,rows);
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
					EmployeeView employee = getEmployeeView(rs);
					employees.add(employee);
				}
			}
			
			rs.close();	
			pst.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return totals;
	}

	
	public static List<EmployeeView> getWorkshopMember(Connection conn, int workshopID) {		
		PreparedStatement pst = null;
		ResultSet rs = null;		
		List<EmployeeView> employees = new ArrayList<EmployeeView>();		
		try {
			pst = conn.prepareStatement("select * from employeeView where workshopID = ? and  deleted = false");
			pst.setInt(1, workshopID);
			rs = pst.executeQuery();
			while (rs.next()) {
				EmployeeView employee = getEmployeeView(rs);
				employees.add(employee);
			}
			rs.close();	
			pst.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return employees;
	}
	
	/*
	public static List<EmployeeView> getEmployeeByWrokshopIDandDepartmentID(Connection conn, int workshopID, int deaprtmentID) {		
		PreparedStatement pst = null;
		ResultSet rs = null;		
		
		String sql = String.format("select * from employeeView where workshopID = ? and  deleted = false");
		if(deaprtmentID != 0)
		{
			sql +=  " and departmentID="+deaprtmentID;
		}
		List<EmployeeView> employees = new ArrayList<EmployeeView>();		
		try {
			pst = conn.prepareStatement(sql);
			pst.setInt(1, workshopID);
			rs = pst.executeQuery();
			while (rs.next()) {
				EmployeeView employee = getEmployeeView(rs);
				employees.add(employee);
			}
			rs.close();	
			pst.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return employees;
	}*/

	public static int getEmployeeOfCompanyAndDepartment(Connection conn, int companyID,	int departmentID, int page, int rows, List<EmployeeView> employees) {
		
		PreparedStatement pst = null;
		ResultSet rs = null;
		
		int offset = (page-1)*rows;
		String sql1 = String.format("select count(ID) as totals from employeeView where companyID = ? and departmentID = ? and deleted = false");
		String sql2 = "";
		if (page == 0) {//不需要分页
			sql2 = "select * from employeeView where companyID = ? and departmentID = ? and  deleted = false";
		} else {
			sql2 = String.format("select * from employeeView where companyID = ? and departmentID = ? and  deleted = false limit %d,%d", offset,rows);
		}
		int totals = 0;
		try {
			pst = conn.prepareStatement(sql1);
			pst.setInt(1, companyID);
			pst.setInt(2, departmentID);
			rs = pst.executeQuery();
			if (rs.next()) {
				totals = rs.getInt("totals");

				pst = conn.prepareStatement(sql2);
				pst.setInt(1, companyID);
				pst.setInt(2, departmentID);
				rs = pst.executeQuery();
				while (rs.next()) {
					EmployeeView employee = getEmployeeView(rs);
					employees.add(employee);
				}
			}
			
			rs.close();	
			pst.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return totals;
	}
	
	
	private static Employee getEmployee(ResultSet rs){
		try {
			Employee employee = new Employee();
			employee.setID(rs.getString("ID"));
			employee.setName(rs.getString("name"));
			employee.setPassword(rs.getString("password"));
			employee.setPhone(rs.getString("phone"));
			employee.setCompany2(rs.getString("company2"));
			employee.setCompanyID(rs.getShort("companyID"));
			employee.setWorkshopID(rs.getShort("workshopID"));
			employee.setDepartmentID(rs.getShort("departmentID"));
			employee.setDinner(rs.getShort("dinner"));
			employee.setLunch(rs.getShort("lunch"));
			employee.setMidnight(rs.getShort("midnight"));
			employee.setWorkTypeID(rs.getByte("workTypeID"));
			employee.setRole(rs.getByte("role"));
			employee.setPlaceIDs(rs.getString("placeIDs"));
			employee.setLock(rs.getByte("locked"));
			return employee;
		} catch (SQLException e) {
			System.out.print("从数据库中提取职工信息出错，请检查字段有无拼写错误");
			return null;
		}
	}

	private static EmployeeView getEmployeeView(ResultSet rs) {
		try {
			EmployeeView employee = new EmployeeView();
			employee.setID(rs.getString("ID"));
			employee.setName(rs.getString("name"));
			employee.setPassword(rs.getString("password"));
			employee.setPhone(rs.getString("phone"));
			employee.setCompanyID(rs.getShort("companyID"));
			employee.setCompany(rs.getString("company"));
			employee.setWorkshopID(rs.getShort("workshopID"));
			employee.setWorkshop(rs.getString("workshop"));
			employee.setDepartmentID(rs.getShort("departmentID"));
			employee.setDepartment(rs.getString("department"));
			employee.setDinner(rs.getShort("dinner"));
			employee.setLunch(rs.getShort("lunch"));
			employee.setMidnight(rs.getShort("midnight"));
			employee.setWorkTypeID(rs.getByte("workTypeID"));
			employee.setWorkType(rs.getString("workType"));
			employee.setRole(rs.getByte("role"));
			employee.setPlaceIDs(rs.getString("placeIDs"));
			employee.setLock(rs.getByte("locked"));
			
			return employee;
		} catch (SQLException e) {
			System.out.print("从数据库中提取职工信息出错，请检查字段有无拼写错误");
			return null;
		}
	}

	
	public static void updateMeal(Connection conn, List<Employee> employees) {
		try {
			PreparedStatement ps = conn.prepareStatement("update employee set lunch = ? , dinner = ? , midnight = ? where ID = ?");
			for (int i = 0; i < employees.size(); i++) {
				ps.setInt(1, employees.get(i).getLunch());
				ps.setInt(2, employees.get(i).getDinner());
				ps.setInt(3, employees.get(i).getMidnight());
				ps.setString(4, employees.get(i).getID());
				ps.addBatch();
			}
			ps.executeBatch();
			ps.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	

}
