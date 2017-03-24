package View;

public class OrderViewSimple {
	private long ID;//订单号
	private byte type;//用餐类别
	private String eaterID;//用餐人工号
	private String eaterName;//用餐人姓名
	private String ordererID;//下单人工号
	private String ordererName;//下单人姓名
	private String eatDate;//用餐日期
	private String orderDate;//订餐日期
	private short placeID;//送餐点编号
	private String placeName;//送餐点名称
	private String departmentName;//结算单位名称
	private short workshopId;//车间编号
	private float price;//价格
	private String additional;//零点餐补充说明：A、B、C
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
	public String getEaterName() {
		return eaterName;
	}
	public void setEaterName(String eaterName) {
		this.eaterName = eaterName;
	}
	public String getOrdererID() {
		return ordererID;
	}
	public void setOrdererID(String ordererID) {
		this.ordererID = ordererID;
	}
	public String getOrdererName() {
		return ordererName;
	}
	public void setOrdererName(String ordererName) {
		this.ordererName = ordererName;
	}
	public String getEatDate() {
		return eatDate;
	}
	public void setEatDate(String eatDate) {
		this.eatDate = eatDate;
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
	public String getPlaceName() {
		return placeName;
	}
	public void setPlaceName(String placeName) {
		this.placeName = placeName;
	}
	public String getDepartmentName() {
		return departmentName;
	}
	public void setDepartmentName(String departmentName) {
		this.departmentName = departmentName;
	}
	public short getWorkshopId() {
		return workshopId;
	}
	public void setWorkshopId(short workshopId) {
		this.workshopId = workshopId;
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
}
