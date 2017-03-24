package bean;


/**
 * 公告类
 * @author 胡浪
 *
 */
public class Notice {
	private int id;
	//公告标题
	private String title;
	//公告内容
	private String content;
	//公告发布人
	private String author;
	//公告发布时间
	private String date;
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getAuthor() {
		return author;
	}
	public void setAuthor(String author) {
		this.author = author;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	
	
}
