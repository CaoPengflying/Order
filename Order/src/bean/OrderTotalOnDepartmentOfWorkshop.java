package bean;
/**
 * 个人订单汇总
 * @author dell
 *
 */
public class OrderTotalOnDepartmentOfWorkshop {
	private short departmentID;//班组号
	private String departmentName;//班组名
	private int lunch;
	private int dinner;
	private int midnight;
	
	
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
	public int getLunch() {
		return lunch;
	}
	public void setLunch(int lunch) {
		this.lunch = lunch;
	}
	public int getDinner() {
		return dinner;
	}
	public void setDinner(int dinner) {
		this.dinner = dinner;
	}
	public int getMidnight() {
		return midnight;
	}
	public void setMidnight(int midnight) {
		this.midnight = midnight;
	}
	
}
