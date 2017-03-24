package bean;

import java.util.List;

/**
 * 送餐 线路
 * @author 胡浪
 *
 */
public class Route {

	//线路编号
	private byte ID;
	//线路名称
	private String name;
	//线路上的送餐点（有序）
	private String placeIDs;
	//所属食堂
	private byte carteenID;
	
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
	public String getPlaceIDs() {
		return placeIDs;
	}
	public void setPlaceIDs(String placeIDs) {
		this.placeIDs = placeIDs;
	}
	public byte getCarteenID() {
		return carteenID;
	}
	public void setCarteenID(byte carteenID) {
		this.carteenID = carteenID;
	}
	
	
}
