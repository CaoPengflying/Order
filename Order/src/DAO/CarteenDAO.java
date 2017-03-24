package DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import bean.Carteen;

public class CarteenDAO {
	/**
	 * 添加管理员
	 * @param carteen 管理员
	 */
	public static void addCarteen(Connection conn, Carteen carteen){		
		PreparedStatement pst = null;		
		try {
			pst = conn.prepareStatement("insert into carteen(name) values(?)");
			pst.setString(1, carteen.getName());
			pst.executeUpdate();	
			pst.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 更新管理员
	 * @param carteen 管理员
	 */
	public static void updateCarteen(Connection conn, Carteen carteen){		
		PreparedStatement pst = null;		
		try {
			pst = conn.prepareStatement("update carteen set name=? where ID = ?");
			pst.setString(1, carteen.getName());
			pst.setInt(2, carteen.getID());
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
	public static void delCarteen(Connection conn, int id) {
		try {
			PreparedStatement pst = conn.prepareStatement("delete from carteen where ID = ?");
			pst.setInt(1, id);
			pst.executeUpdate();	
			pst.close();			
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}	
	
	
	/**
	 * 获取指定管理员
	 * @param carteenID 管理员号
	 * @return 返回管理员信息
	 */
	public static Carteen getCarteen(Connection conn, int carteenID){		
		PreparedStatement pst = null;
		ResultSet rs = null;		
		Carteen carteen = null;
		try {
			pst = conn.prepareStatement("select * from carteen where ID = ?");
			pst.setInt(1, carteenID);
			rs = pst.executeQuery();
			if (rs.next()) {
				carteen = getCarteen(rs);
			}
			rs.close();	
			pst.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return carteen;
	}

	/**
	 * 获取管理员
	 * @return 管理员列表
	 */
	public static List<Carteen> getCarteens(Connection conn){
		
		PreparedStatement pst = null;
		ResultSet rs = null;
		
		List<Carteen> carteens = new ArrayList<Carteen>();		
		try {
			pst = conn.prepareStatement("select * from carteen");
			rs = pst.executeQuery();
			while (rs.next()) {
				Carteen carteen = getCarteen(rs);
				carteens.add(carteen);
			}
			rs.close();	
			pst.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return carteens;
	}
	
	private static Carteen getCarteen(ResultSet rs){
		try {
			Carteen carteen = new Carteen();
			carteen.setID(rs.getByte("ID"));
			carteen.setName(rs.getString("name"));
			return carteen;
		} catch (SQLException e) {
			System.out.print("从数据库中提取管理员信息出错，请检查字段有无拼写错误");
			return null;
		}
	}
}
