package bean;

import java.sql.Time;

/**
 * 订餐规则
 * @author 胡浪
 *
 */
public class Regular {

	//最大可预订天数
	private int days;
	/**午餐截止时间,09:10:00*/
	private String lunch;
	//晚餐截止时间
	private String dinner;
	//零点餐截止时间
	private String midnight;
	public int getDays() {
		return days;
	}
	public void setDays(int days) {
		this.days = days;
	}
	public String getLunch() {
		return lunch;
	}
	public void setLunch(String lunch) {
		this.lunch = lunch;
	}
	public String getDinner() {
		return dinner;
	}
	public void setDinner(String dinner) {
		this.dinner = dinner;
	}
	public String getMidnight() {
		return midnight;
	}
	public void setMidnight(String midnight) {
		this.midnight = midnight;
	}
	
	
}
