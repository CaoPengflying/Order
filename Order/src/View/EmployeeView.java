package View;


public class EmployeeView {
		// 编号
		private String ID;
		// 姓名
		private String name;
		// 登录密码
		private String password;
		// 联系方式
		private String phone;
		// 倒班类型
		private byte workTypeID;
		private String workType;
		// 结算单位
		private short companyID;
		private String company;
		private short workshopID;
		private String workshop;
		// 工作单位
		private short departmentID;
		private String department;
		// 职工类型（角色）
		private byte role;
		// 订餐是否被锁定
		private byte lock;
		//午餐补贴剩余数量
		private short lunch;
		//晚餐补贴剩余数量
		private short dinner;
		//零点餐补贴剩余数量
		private short midnight;
		//送餐点列表
		private String placeIDs;
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
		public String getWorkType() {
			return workType;
		}
		public void setWorkType(String workType) {
			this.workType = workType;
		}
		public short getCompanyID() {
			return companyID;
		}
		public void setCompanyID(short companyID) {
			this.companyID = companyID;
		}
		public String getCompany() {
			return company;
		}
		public void setCompany(String company) {
			this.company = company;
		}
		public short getDepartmentID() {
			return departmentID;
		}
		public void setDepartmentID(short departmentID) {
			this.departmentID = departmentID;
		}
		public String getDepartment() {
			return department;
		}
		public void setDepartment(String department) {
			this.department = department;
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
		public String getPlaceIDs() {
			return placeIDs;
		}
		public void setPlaceIDs(String placeIDs) {
			this.placeIDs = placeIDs;
		}
		public byte getLock() {
			return lock;
		}
		public void setLock(byte lock) {
			this.lock = lock;
		}
		public short getWorkshopID() {
			return workshopID;
		}
		public void setWorkshopID(short workshopID) {
			this.workshopID = workshopID;
		}
		public String getWorkshop() {
			return workshop;
		}
		public void setWorkshop(String workshop) {
			this.workshop = workshop;
		}
		
		
}
