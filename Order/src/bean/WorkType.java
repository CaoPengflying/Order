package bean;
/**
 * 职工倒班类型
 * @author 胡浪
 *不同倒班类型餐补数量不同
 */
public class WorkType {
	//倒班类型编号
	private byte ID;
	//倒班类型名称
	private String name;
	//中餐补贴标准 （餐补数量）
	private short lunch;
	//晚餐补贴标准
	private short dinner;
	//零点餐补贴标准
	private short midnight;
	//删除标记
	private boolean deleted;
	
	public byte getID() {
		return ID;
	}
	public void setID(byte iD) {
		ID = iD;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
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
	public boolean isDeleted() {
		return deleted;
	}
	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}
}
