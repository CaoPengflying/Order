package bean;
/**
 * 结算单位（公司）
 * @author 胡浪
 *
 */
public class Company {
	private short ID;
	private String name;
	private String phone;
	private boolean exception;
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
	public boolean isException() {
		return exception;
	}
	public void setException(boolean exception) {
		this.exception = exception;
	}
	public boolean isDeleted() {
		return deleted;
	}
	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}
	
	
}
