package View;

public class DepartmentView {
	//部门编号
	private short ID;
	//部门名称
	private String name;
	//上级部门
	private short workshopID;
	private String workshopName;
	private String placeName;
	//部门联系电话
	private String phone;
	//删除标记
	private boolean deleted;
	public short getID() {
		return ID;
	}
	public void setID(short iD) {
		ID = iD;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public short getWorkshopID() {
		return workshopID;
	}
	public void setWorkshopID(short workshopID) {
		this.workshopID = workshopID;
	}
	public String getWorkshopName() {
		return workshopName;
	}
	public void setWorkshopName(String workshopName) {
		this.workshopName = workshopName;
	}
	public String getPlaceName() {
		return placeName;
	}
	public void setPlaceName(String placeName) {
		this.placeName = placeName;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public boolean isDeleted() {
		return deleted;
	}
	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}
	
}
