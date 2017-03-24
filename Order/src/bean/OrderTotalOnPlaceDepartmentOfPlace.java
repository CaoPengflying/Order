/**
 * 
 */
package bean;

/**
 * 班组汇总
 * @author dell
 *
 */
public class OrderTotalOnPlaceDepartmentOfPlace {
	private short placeID;
	private String placeName;
	private short departmentID;
	private String departmentName;
	private int amount;
	private String remark;
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
	public short getDepartmentID() {
		return departmentID;
	}
	public void setDepartmentID(short departmentID) {
		this.departmentID = departmentID;
	}
	public String getDepartmentName() {
		return departmentName;
	}
	public void setDepartmentName(String departmentName) {
		this.departmentName = departmentName;
	}
	public int getAmount() {
		return amount;
	}
	public void setAmount(int amount) {
		this.amount = amount;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	
}
