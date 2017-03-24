package bean;


/**
 * 订单类
 * 
 * @author 胡浪
 * 
 */
public class Order {
	// 订单编号
	private long ID;
	// 套餐类型
	private byte type;
	// 用餐人编号
	private String eaterID;
	// 用餐时间
	private String eatDate;
	// 下单人编号
	private String ordererID;
	//结算班组代码
	private short departmentID;
	// 下单时间
	private String orderDate;
	// 送餐点编号
	private short placeID;
	//价格
	private float price;
	//零点餐类型
	private String additional;
	//是否是节假日
	private boolean isHoliday;
	public long getID() {
		return ID;
	}
	public void setID(long iD) {
		ID = iD;
	}
	public byte getType() {
		return type;
	}
	public void setType(byte type) {
		this.type = type;
	}
	public String getEaterID() {
		return eaterID;
	}
	public void setEaterID(String eaterID) {
		this.eaterID = eaterID;
	}
	public String getEatDate() {
		return eatDate;
	}
	public void setEatDate(String eatDate) {
		this.eatDate = eatDate;
	}
	public String getOrdererID() {
		return ordererID;
	}
	public void setOrdererID(String ordererID) {
		this.ordererID = ordererID;
	}
	public String getOrderDate() {
		return orderDate;
	}
	public void setOrderDate(String orderDate) {
		this.orderDate = orderDate;
	}
	public short getPlaceID() {
		return placeID;
	}
	public void setPlaceID(short placeID) {
		this.placeID = placeID;
	}
	public float getPrice() {
		return price;
	}
	public void setPrice(float price) {
		this.price = price;
	}
	
	public String getAdditional() {
		return additional;
	}
	public void setAdditional(String additional) {
		this.additional = additional;
	}
	public static String getTypeString(byte type) {
		switch (type) {
		case Price.LUNCH:
			return "中餐";
		case Price.DINNER:
			return "晚餐";
		case Price.MIDNIGHT:
			return "零点餐";
		default:
			break;
		}
		return "";
	}
	public boolean isHoliday() {
		return isHoliday;
	}
	public void setHoliday(boolean isHoliday) {
		this.isHoliday = isHoliday;
	}
	public short getDepartmentID() {
		return departmentID;
	}
	public void setDepartmentID(short departmentID) {
		this.departmentID = departmentID;
	}
}
