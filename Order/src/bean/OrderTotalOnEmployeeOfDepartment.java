package bean;
/**
 * 个人订单汇总
 * @author dell
 *
 */
public class OrderTotalOnEmployeeOfDepartment {
	private String employeeID;
	private String name;
	private int lunch;
	private int dinner;
	private int midnight;
	private float money;
	
	public String getEmployeeID() {
		return employeeID;
	}
	public void setEmployeeID(String employeeID) {
		this.employeeID = employeeID;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
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
	public float getMoney() {
		return money;
	}
	public void setMoney(float money) {
		this.money = money;
	}
	
}
