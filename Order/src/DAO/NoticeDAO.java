package DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import View.NoticeView;
import bean.Notice;

public class NoticeDAO {
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
	}
	/**
	 * 获取最新的公告
	 * @return
	 */
	public static Notice getLastNotice(Connection conn){
		
		PreparedStatement pst = null;
		ResultSet rs = null;
		
		Notice notice = new Notice();
		try {
			pst = conn.prepareStatement("select * from notice order by id desc limit 1");
			rs = pst.executeQuery();
			if (rs.next()) {
				notice = getNotice(rs);
			}
			rs.close();	
			pst.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return notice;
	}
	
	public static int getNotices(Connection conn, int page, int rows, int carteenID, List<NoticeView>notices){

		
		PreparedStatement pst = null;
		ResultSet rs = null;
		String sql1 = "select count(*) as totals from noticeview";
		if (carteenID != 0) {
			sql1 += String.format(" where carteenID=%d",carteenID);
		}
		int totals = 0;
		try {
			pst = conn.prepareStatement(sql1);
			rs = pst.executeQuery();
			if(rs.next()) {
				totals = rs.getInt(1);
			}
			String sql2 = "select * from noticeview order by id desc limit ?,?";
			if (carteenID != 0) {
				sql2 = String.format("select * from noticeview where carteenID=%d order by id desc limit ?,?",carteenID);
			}
			pst = conn.prepareStatement(sql2);
			pst.setInt(1, rows*(page-1));
			pst.setInt(2, rows);
			rs = pst.executeQuery();
			while (rs.next()) {
				NoticeView notice = new NoticeView();
				notice = getNoticeView(rs);
				notices.add(notice);
			}
			rs.close();	
			pst.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return totals;
	}
	
 	private static NoticeView getNoticeView(ResultSet rs) {
 		try {
 			NoticeView notice = new NoticeView();
			notice.setId(rs.getInt("id"));
			notice.setTitle(rs.getString("title"));
			notice.setContent(rs.getString("content"));
			notice.setAuthorName(rs.getString("authorName"));
			notice.setDate(rs.getString("date"));
			notice.setCarteenName(rs.getString("carteenName"));
			return notice;
		} catch (SQLException e) {
			System.out.print("从数据库中提取公告信息出错，请检查字段有无拼写错误");
			return null;
		}
	}
	public static void updateNotice(Connection conn, Notice notice){
		
		PreparedStatement pst = null;
		
		try {
			pst = conn.prepareStatement("update notice set title=?,content=?,author=?,date=? where id=?");
			pst.setString(1, notice.getTitle());
			pst.setString(2,  notice.getContent());
			pst.setString(3,  notice.getAuthor());
			pst.setString(4,  notice.getDate());
			pst.setInt(5, notice.getId());
			pst.executeUpdate();	
			pst.close();			
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
 	public static void delNotice(Connection conn, int noticeId){
 		
		PreparedStatement pst = null;
		
		try {
			pst = conn.prepareStatement("delete from notice where id=?");
			pst.setInt(1, noticeId);
			pst.executeUpdate();	
			pst.close();			
		} catch (SQLException e) {
			e.printStackTrace();
		}
 	}
 	
	private static Notice getNotice(ResultSet rs){
		try {
			Notice notice = new Notice();
			notice.setId(rs.getInt("id"));
			notice.setTitle(rs.getString("title"));
			notice.setContent(rs.getString("content"));
			notice.setAuthor(rs.getString("author"));
			notice.setDate(rs.getString("date"));
			return notice;
		} catch (SQLException e) {
			System.out.print("从数据库中提取公告信息出错，请检查字段有无拼写错误");
			return null;
		}
	}
	
	/**
	 * 添加公告
	 * @param notice 公告内容
	 */
	public static void addNotice(Connection conn, Notice notice) {
		PreparedStatement pst = null;		
		try {
			pst = conn.prepareStatement("insert into notice(title,content,author, date) values(?,?,?,?)");
			pst.setString(1, notice.getTitle());
			pst.setString(2,  notice.getContent());
			pst.setString(3,  notice.getAuthor());
			pst.setString(4,  notice.getDate());
			pst.executeUpdate();	
			pst.close();			
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
