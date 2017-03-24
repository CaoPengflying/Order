package DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import bean.Place;
import bean.Workshop;

public class PlaceDAO {
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
	}
	
	public static void addPlace(Connection conn, Place place){
		PreparedStatement pst = null;		
		try {
			pst = conn.prepareStatement("insert into place(name,carteenID) values(?,?)");
			pst.setString(1, place.getName());
			pst.setInt(2, place.getCarteenID());
			pst.executeUpdate();		
			pst.close();		
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static void updatePlace(Connection conn, Place place){
		PreparedStatement pst = null;		
		try {
			pst = conn.prepareStatement("update place set name=?,carteenID=?,deleted=? where ID = ?");
			pst.setString(1, place.getName());
			pst.setInt(2, place.getCarteenID());
			pst.setBoolean(3,  place.isDeleted());
			pst.setInt(4, place.getID());
			pst.executeUpdate();		
			pst.close();		
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static void deletePlace(Connection conn, Place place){
		//加删除标记
		place.setDeleted(true);
		updatePlace(conn,place);
	}
	
	public static Place getPlace(Connection conn, int placeID){
		
		PreparedStatement pst = null;
		ResultSet rs = null;
		
		Place place = null;
		try {
			pst = conn.prepareStatement("select * from place where ID = ?");
			pst.setInt(1, placeID);
			rs = pst.executeQuery();
			if (rs.next()) {
				place = getPlace(rs);
			}
			rs.close();	
			pst.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return place;
	}
	
	public static int getPlaces(Connection conn, int page, int rows, List<Place> places){
		
		PreparedStatement pst = null;
		ResultSet rs = null;
		
		int offset = (page-1)*rows;
		String sql1 = String.format("select count(ID) as totals from place where deleted = false");
		String sql2 = "";
		if (page == 0) {//不需要分页
			sql2 = "select * from place where deleted = false";
		} else {
			sql2 = String.format("select * from place where deleted = false limit %d,%d", offset,rows);
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
					Place place = getPlace(rs);
					places.add(place);
				}
			}
			rs.close();	
			pst.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return totals;
	}

	public static List<Place> getPlaces(Connection conn, String placeIDs){		
		PreparedStatement pst = null;
		ResultSet rs = null;
		
		List<Place> places = new ArrayList<Place>();
		if (placeIDs.length() == 0) {
			return places;
		}
		try {
			//注意要按照给出的ID顺序排序
			String sql = String.format("select * from place where ID in (%s) order by instr('%s',ID)", placeIDs,placeIDs);
			pst = conn.prepareStatement(sql);
			rs = pst.executeQuery();
			while (rs.next()) {
				Place place = getPlace(rs);
				places.add(place);
			}
			rs.close();	
			pst.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return places;
	}
	

	public static List<Place> getPlacesOfCarteen(Connection conn, int id) {		
		PreparedStatement pst = null;
		ResultSet rs = null;
		
		List<Place> places = new ArrayList<Place>();
		try {
			if(id == 0) {
				pst = conn.prepareStatement("select * from place");
			}else {
				pst = conn.prepareStatement("select * from place where carteenID = ?");
				pst.setInt(1, id);
			}
			
			rs = pst.executeQuery();
			while (rs.next()) {
				Place place = getPlace(rs);
				places.add(place);
			}
			rs.close();	
			pst.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return places;
	}
	
	private static Place getPlace(ResultSet rs){
		try {
			Place place = new Place();
			place.setID(rs.getShort("ID"));
			place.setName(rs.getString("name"));
			place.setCarteenID(rs.getByte("carteenID"));
			place.setDeleted(rs.getBoolean("deleted"));
			return place;
		} catch (SQLException e) {
			System.out.print("从数据库中提取送餐点信息出错，请检查字段有无拼写错误");
			return null;
		}
	}


}
