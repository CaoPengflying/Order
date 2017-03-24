package View;

public class WorkshopView {

	//部门编号
	private short ID;
	//部门名称
	private String name;	
	//部门联系电话
	private String phone;
	//所属公司
	private short companyID;
	//所属公司名
	private String companyName;
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
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public short getCompanyID() {
		return companyID;
	}
	public void setCompanyID(short companyID) {
		this.companyID = companyID;
	}
	public String getCompanyName() {
		return companyName;
	}
	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}
	public boolean isDeleted() {
		return deleted;
	}
	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}
	
	
}
