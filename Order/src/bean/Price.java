package bean;

import java.util.Date;

public class Price {
	public final static byte ALLDAY = 0; 
	public final static byte LUNCH = 1; 
	public final static byte DINNER = 2; 
	public final static byte MIDNIGHT = 3; 
	
	public final static byte NORMAL = 0; 
	public final static byte HOLIDAY = 1; 
	public final static byte EXCEPTION = 2; 
	
	private int ID;
	private String date_start;
	private String date_end;
	private float lunch_normal;
	private float lunch_holiday;
	private float lunch_exception;
	private float dinner_normal;
	private float dinner_holiday;
	private float dinner_exception;
	private float midnight_normal;
	private float midnight_holiday;
	private float midnight_exception;


	public String getDate_start() {
		return date_start;
	}
	public void setDate_start(String date_start) {
		this.date_start = date_start;
	}
	public String getDate_end() {
		return date_end;
	}
	public void setDate_end(String date_end) {
		this.date_end = date_end;
	}
	public float getLunch_normal() {
		return lunch_normal;
	}
	public void setLunch_normal(float lunch_normal) {
		this.lunch_normal = lunch_normal;
	}
	public float getLunch_holiday() {
		return lunch_holiday;
	}
	public void setLunch_holiday(float lunch_holiday) {
		this.lunch_holiday = lunch_holiday;
	}
	public float getLunch_exception() {
		return lunch_exception;
	}
	public void setLunch_exception(float lunch_exception) {
		this.lunch_exception = lunch_exception;
	}
	public float getDinner_normal() {
		return dinner_normal;
	}
	public void setDinner_normal(float dinner_normal) {
		this.dinner_normal = dinner_normal;
	}
	public float getDinner_holiday() {
		return dinner_holiday;
	}
	public void setDinner_holiday(float dinner_holiday) {
		this.dinner_holiday = dinner_holiday;
	}
	public float getDinner_exception() {
		return dinner_exception;
	}
	public void setDinner_exception(float dinner_exception) {
		this.dinner_exception = dinner_exception;
	}
	public float getMidnight_normal() {
		return midnight_normal;
	}
	public void setMidnight_normal(float midnight_normal) {
		this.midnight_normal = midnight_normal;
	}
	public float getMidnight_holiday() {
		return midnight_holiday;
	}
	public void setMidnight_holiday(float midnight_holiday) {
		this.midnight_holiday = midnight_holiday;
	}
	public float getMidnight_exception() {
		return midnight_exception;
	}
	public void setMidnight_exception(float midnight_exception) {
		this.midnight_exception = midnight_exception;
	}
	public int getID() {
		return ID;
	}
	public void setID(int iD) {
		ID = iD;
	}
	
}
