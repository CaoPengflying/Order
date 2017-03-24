package bean;


/**
 * 
 * @author 胡浪
 *         <p>
 *         职工类:
 *         <p>
 * 
 */
public class Employee {
	public final static byte ROLE_COMMON = 1;//普遍职工
	public final static byte ROLE_GROUP = 2;//班组管理员
	public final static byte ROLE_WORKSHOP = 3;//车间管理员
	// 编号
	private String ID;
	// 姓名
	private String name;
	// 登录密码
	private String password;
	// 联系方式
	private String phone;
	//人事关系所属公司
	private String company2;
	// 倒班类型
	private byte workTypeID;
	// 结算单位
	private short companyID;
	//车间
	private short workshopID;
	// 工作单位
	private short departmentID;	
	//送餐点
	private String placeIDs;
	// 职工类型（角色）
	private byte role;
	//午餐补贴剩余数量
	private short lunch;
	//晚餐补贴剩余数量
	private short dinner;
	//零点餐补贴剩余数量
	private short midnight;
	// 订餐是否被锁定
	private byte lock;
	//删除标记
	private boolean deleted;
	public String getID() {
		return ID;
	}
	public void setID(String iD) {
		ID = iD;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public byte getWorkTypeID() {
		return workTypeID;
	}
	public void setWorkTypeID(byte workTypeID) {
		this.workTypeID = workTypeID;
	}
	public short getCompanyID() {
		return companyID;
	}
	public void setCompanyID(short companyID) {
		this.companyID = companyID;
	}
	public short getDepartmentID() {
		return departmentID;
	}
	public void setDepartmentID(short departmentID) {
		this.departmentID = departmentID;
	}
	public String getPlaceIDs() {
		return placeIDs;
	}
	public void setPlaceIDs(String placeIDs) {
		this.placeIDs = placeIDs;
	}
	public byte getRole() {
		return role;
	}
	public void setRole(byte role) {
		this.role = role;
	}
	public short getLunch() {
		return lunch;
	}
	public void setLunch(short lunch) {
		this.lunch = lunch;
	}
	public short getDinner() {
		return dinner;
	}
	public void setDinner(short dinner) {
		this.dinner = dinner;
	}
	public short getMidnight() {
		return midnight;
	}
	public void setMidnight(short midnight) {
		this.midnight = midnight;
	}
	public byte getLock() {
		return lock;
	}
	public void setLock(byte lock) {
		this.lock = lock;
	}
	public boolean isDeleted() {
		return deleted;
	}
	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}
	public short getWorkshopID() {
		return workshopID;
	}
	public void setWorkshopID(short workshopID) {
		this.workshopID = workshopID;
	}
	public String getCompany2() {
		return company2;
	}
	public void setCompany2(String company2) {
		this.company2 = company2;
	}	
	
	
}
