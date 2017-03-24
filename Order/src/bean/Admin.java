package bean;

import java.util.List;

/**
 * 
 * @author 胡浪
 *         <p>
 *         职工类:
 *         <p>
 * 
 */
public class Admin {
	public final static byte ROLE_ADMIN = 0;//管理员
	public final static byte ROLE_SUPERADMIN = 1;//超级管理员
	
	public final static int POWER_COMPANY = 1;//公司管理权限
	public final static int POWER_WORKSHOP = 2;//车间管理权限
	public final static int POWER_GROUP = 4;//班组管理权限
	public final static int POWER_EMPLOYEE = 8;//职工管理权限
	public final static int POWER_WORKTYPE = 16;//倒班管理权限
	public final static int POWER_HOLIDAY = 32;//假日管理权限
	public final static int POWER_PRICE = 64;//价格权限
	public final static int POWER_PREPARE = 128;//备餐权限
	public final static int POWER_DISTRIBUTION = 256;//送餐权限
	public final static int POWER_ROUTE = 512;//线路管理权限
	public final static int POWER_REGULAR = 1024;//订餐规则制定权限
	public final static int POWER_NOTICE = 2048;//发布公告权限
	public final static int POWER_TOTAL = 4096;//统计权限
	public final static int POWER_DATA = 8192;//数据管理权限
	public final static int POWER_HISTORY = 16384;//历史订单查询权限
	public final static int POWER_ORDER = 32768;//代为订餐权限
	public final static int POWER_CARTEEN = 65536;//食堂管理权限
	public final static int POWER_SEARCH = 131072;//订单查询权限
	
	// 编号
	private String ID;
	// 姓名
	private String name;
	// 登录密码
	private String password;
	// 联系方式
	private String phone;
	//权限
	private int permission;
	// 所属食堂
	private int carteenID;
	//角色
	private byte role;
	
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
	public int getCarteenID() {
		return carteenID;
	}
	public void setCarteenID(int carteenID) {
		this.carteenID = carteenID;
	}
	public int getPermission() {
		return permission;
	}
	public void setPermission(int permission) {
		this.permission = permission;
	}
	public byte getRole() {
		return role;
	}
	public void setRole(byte role) {
		this.role = role;
	}
	
}
