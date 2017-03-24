package bean;
/**
 * 送餐点
 * @author 胡浪
 *
 */
public class Place {

	//送餐点编号
	private short ID;
	//送餐点名称
	private String name;
	//对应的食堂
	private byte carteenID;
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
	public boolean isDeleted() {
		return deleted;
	}
	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}
	public byte getCarteenID() {
		return carteenID;
	}
	public void setCarteenID(byte carteenID) {
		this.carteenID = carteenID;
	}	
	
	
}
