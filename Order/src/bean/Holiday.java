package bean;

import java.util.Date;

/**
 * 节假日
 * @author Administrator
 *
 */
public class Holiday {

	private String date;
	private boolean lunch;
	private boolean dinner;
	private boolean midnight;
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public boolean isLunch() {
		return lunch;
	}
	public void setLunch(boolean lunch) {
		this.lunch = lunch;
	}
	public boolean isDinner() {
		return dinner;
	}
	public void setDinner(boolean dinner) {
		this.dinner = dinner;
	}
	public boolean isMidnight() {
		return midnight;
	}
	public void setMidnight(boolean midnight) {
		this.midnight = midnight;
	}
}
