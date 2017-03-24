package DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import bean.Company;

public class CompanyDAO {
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
	public static void addCompany(Connection conn, Company company){
		PreparedStatement pst = null;
		
		try {
			pst = conn.prepareStatement("insert into company(name,phone,exception) values(?,?,?)");
			pst.setString(1, company.getName());
			pst.setString(2, company.getPhone());
			pst.setBoolean(3, company.isException());
			pst.executeUpdate();		
			pst.close();		
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static void updateCompany(Connection conn, Company company){
		
		PreparedStatement pst = null;
		
		try {
			pst = conn.prepareStatement("update company set name=?,phone=?,exception=?,deleted=? where ID = ?");
			pst.setString(1, company.getName());
			pst.setString(2, company.getPhone());
			pst.setBoolean(3, company.isException());
			pst.setBoolean(4, company.isDeleted());
			pst.setInt(5, company.getID());
			pst.executeUpdate();		
			pst.close();		
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static Company getCompany(Connection conn, int companyID){
		
		PreparedStatement pst = null;
		ResultSet rs = null;
		
		Company company = null;
		try {
			pst = conn.prepareStatement("select * from company where ID = ?");
			pst.setInt(1, companyID);
			rs = pst.executeQuery();
			if (rs.next()) {
				company = getCompany(rs);
			}
			rs.close();	
			pst.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return company;
	}

	public static List<Company> getCompanys(Connection conn){
		
		PreparedStatement pst = null;
		ResultSet rs = null;
		
		List<Company> companys = new ArrayList<Company>();
		try {
			pst = conn.prepareStatement("select * from company where deleted = false");
			rs = pst.executeQuery();	
			while (rs.next()) {
				Company company = getCompany(rs);
				companys.add(company);
			}
			rs.close();	
			pst.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return companys;
	}
	
	
	private static Company getCompany(ResultSet rs){
		try {
			Company company = new Company();
			company.setID(rs.getShort("ID"));
			company.setName(rs.getString("name"));
			company.setPhone(rs.getString("phone"));
			company.setException(rs.getBoolean("exception"));
			company.setDeleted(rs.getBoolean("deleted"));
			return company;
		} catch (SQLException e) {
			System.out.print("从数据库中提取公司信息出错，请检查字段有无拼写错误");
			return null;
		}
	}
}
