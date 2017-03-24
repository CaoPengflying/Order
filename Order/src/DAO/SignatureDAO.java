package DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import bean.Notice;

public class SignatureDAO {
	
	public static String getSignature(Connection conn){
		
		PreparedStatement pst = null;
		ResultSet rs = null;
		
		try {
			pst = conn.prepareStatement("select * from signature");
			rs = pst.executeQuery();
			if (rs.next()) {
				return rs.getString("signature");
			}
			rs.close();	
			pst.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return "";
	}
	
	public static void updateSignature(Connection conn, String signature){
		
		PreparedStatement pst = null;
		
		try {
			pst = conn.prepareStatement("update signature set signature=?");
			pst.setString(1, signature);
			pst.executeUpdate();
			pst.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
