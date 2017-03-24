package DAO;

public class OrderForPriceAndHoliday {
	private long ID;//订单号
	private String eatDate;//用餐日期
	private byte type;//用餐类别
	public long getID() {
		return ID;
	}
	public void setID(long iD) {
		ID = iD;
	}
	public String getEatDate() {
		return eatDate;
	}
	public void setEatDate(String eatDate) {
		this.eatDate = eatDate;
	}
	public byte getType() {
		return type;
	}
	public void setType(byte type) {
		this.type = type;
	}
}
