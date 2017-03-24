package bean;
/**
 * 职工工作单位(部门)
 * @author 胡浪
 *
 */
public class Department {

	//部门编号
	private short ID;
	//部门名称
	private String name;
	//上级部门
	private short workshopID;
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
