package bean;
/**
 * 个人订单汇总
 * @author dell
 *
 */
public class OrderTotalOnEmployeeOfCompany {
	private String departmentName;
	private String employeeID;//职工号
	private String name;//姓名
	private int amountLunchNormal;//正常中餐数量
	private float moneyLunchNormal;//正常中餐餐补
	private int amountLunchHoliday;//假日中餐数量
	private float moneyLunchHoliday;//假日中餐餐补
	private int amountDinnerNormal;//正常晚餐数量
	private float moneyDinnerNormal;//正常晚餐餐补
	private int amountDinnerHoliday;//假日晚餐数量
	private float moneyDinnerHoliday;//假日晚餐餐补
	private int amountMidnightNormal;//正常零点餐数量
	private float moneyMidnightNormal;//正常零点餐餐补
	private int amountMidnightHoliday;//假日零点餐数量
	private float moneyMidnightHoliday;//假日零点餐餐补
	private float sum;//企补总额
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
	public int getAmountLunchNormal() {
		return amountLunchNormal;
	}
	public void setAmountLunchNormal(int amountLunchNormal) {
		this.amountLunchNormal = amountLunchNormal;
	}
	public float getMoneyLunchNormal() {
		return moneyLunchNormal;
	}
	public void setMoneyLunchNormal(float moneyLunchNormal) {
		this.moneyLunchNormal = moneyLunchNormal;
	}
	public int getAmountLunchHoliday() {
		return amountLunchHoliday;
	}
	public void setAmountLunchHoliday(int amountLunchHoliday) {
		this.amountLunchHoliday = amountLunchHoliday;
	}
	public float getMoneyLunchHoliday() {
		return moneyLunchHoliday;
	}
	public void setMoneyLunchHoliday(float moneyLunchHoliday) {
		this.moneyLunchHoliday = moneyLunchHoliday;
	}
	public int getAmountDinnerNormal() {
		return amountDinnerNormal;
	}
	public void setAmountDinnerNormal(int amountDinnerNormal) {
		this.amountDinnerNormal = amountDinnerNormal;
	}
	public float getMoneyDinnerNormal() {
		return moneyDinnerNormal;
	}
	public void setMoneyDinnerNormal(float moneyDinnerNormal) {
		this.moneyDinnerNormal = moneyDinnerNormal;
	}
	public int getAmountDinnerHoliday() {
		return amountDinnerHoliday;
	}
	public void setAmountDinnerHoliday(int amountDinnerHoliday) {
		this.amountDinnerHoliday = amountDinnerHoliday;
	}
	public float getMoneyDinnerHoliday() {
		return moneyDinnerHoliday;
	}
	public void setMoneyDinnerHoliday(float moneyDinnerHoliday) {
		this.moneyDinnerHoliday = moneyDinnerHoliday;
	}
	public int getAmountMidnightNormal() {
		return amountMidnightNormal;
	}
	public void setAmountMidnightNormal(int amountMidnightNormal) {
		this.amountMidnightNormal = amountMidnightNormal;
	}
	public float getMoneyMidnightNormal() {
		return moneyMidnightNormal;
	}
	public void setMoneyMidnightNormal(float moneyMidnightNormal) {
		this.moneyMidnightNormal = moneyMidnightNormal;
	}
	public int getAmountMidnightHoliday() {
		return amountMidnightHoliday;
	}
	public void setAmountMidnightHoliday(int amountMidnightHoliday) {
		this.amountMidnightHoliday = amountMidnightHoliday;
	}
	public float getMoneyMidnightHoliday() {
		return moneyMidnightHoliday;
	}
	public void setMoneyMidnightHoliday(float moneyMidnightHoliday) {
		this.moneyMidnightHoliday = moneyMidnightHoliday;
	}
	public float getSum() {
		return sum;
	}
	public void setSum(float sum) {
		this.sum = sum;
	}
	@Override
	public String toString() {
		return "OrderTotalOnEmployeeOfCompany [departmentName="+departmentName+",employeeID=" + employeeID
				+ ", name=" + name + ", amountLunchNormal=" + amountLunchNormal
				+ ", moneyLunchNormal=" + moneyLunchNormal
				+ ", amountLunchHoliday=" + amountLunchHoliday
				+ ", moneyLunchHoliday=" + moneyLunchHoliday
				+ ", amountDinnerNormal=" + amountDinnerNormal
				+ ", moneyDinnerNormal=" + moneyDinnerNormal
				+ ", amountDinnerHoliday=" + amountDinnerHoliday
				+ ", moneyDinnerHoliday=" + moneyDinnerHoliday
				+ ", amountMidnightNormal=" + amountMidnightNormal
				+ ", moneyMidnightNormal=" + moneyMidnightNormal
				+ ", amountMidnightHoliday=" + amountMidnightHoliday
				+ ", moneyMidnightHoliday=" + moneyMidnightHoliday + ", sum="
				+ sum + "]";
	}
	public String getDepartmentName() {
		return departmentName;
	}
	public void setDepartmentName(String departmentName) {
		this.departmentName = departmentName;
	}
}
