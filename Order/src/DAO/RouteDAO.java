package DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import bean.Employee;
import bean.Route;
import bean.Route;

public class RouteDAO {
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
	
	public static void addRoute(Connection conn, Route route){		
		PreparedStatement pst = null;		
		try {
			pst = conn.prepareStatement("insert into route(name,placeIDs,carteenID) values(?,?,?)");
			pst.setString(1, route.getName());
			pst.setString(2, route.getPlaceIDs());
			pst.setByte(3, route.getCarteenID());
			pst.executeUpdate();	
			pst.close();			
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static void delRoute(Connection conn, int routeID){		
		PreparedStatement pst = null;		
		try {
			pst = conn.prepareStatement("delete from route where ID = ?");
			pst.setInt(1, routeID);
			pst.executeUpdate();	
			pst.close();			
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static void updateRoute(Connection conn, Route route){		
		PreparedStatement pst = null;
		
		try {
			pst = conn.prepareStatement("update route set name=?,placeIDs=?,carteenID=? where ID = ?");
			pst.setString(1, route.getName());
			pst.setString(2, route.getPlaceIDs());
			pst.setByte(3, route.getCarteenID());
			pst.setInt(4, route.getID());
			pst.executeUpdate();		
			pst.close();		
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static Route getRoute(Connection conn, int routeID) {
		
		PreparedStatement pst = null;
		ResultSet rs = null;
		
		Route route = null;
		try {
			pst = conn.prepareStatement("select * from route where ID = ?");
			pst.setInt(1, routeID);
			rs = pst.executeQuery();

			while (rs.next()) {
				route = getRoute(rs);
			}
			rs.close();	
			pst.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return route;
	}

	public static List<Route> getRoutes(Connection conn, byte carteenID){		
		PreparedStatement pst = null;
		ResultSet rs = null;
		String sql = "select * from route";
		if (carteenID != 0) {
			sql += (" where carteenID="+carteenID);
		}
		List<Route> routes = new ArrayList<Route>();
		try {
			pst = conn.prepareStatement(sql);
			rs = pst.executeQuery();
			while (rs.next()) {
				Route route = getRoute(rs);
				routes.add(route);
			}
			rs.close();	
			pst.close();
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return routes;
	}	
	
	private static Route getRoute(ResultSet rs){
		try {
			Route route = new Route();
			route.setID(rs.getByte("ID"));
			route.setName(rs.getString("name"));
			route.setPlaceIDs(rs.getString("placeIDs"));
			route.setCarteenID(rs.getByte("carteenID"));
			return route;
		} catch (SQLException e) {
			System.out.print("从数据库中提取送餐线路信息出错，请检查字段有无拼写错误");
			return null;
		}
	}
}
