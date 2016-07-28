package uk.ltd.mediamagic.los.reference.ws;

import java.util.Collections;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)             
@XmlType(
		name = "OrderInfo",
		namespace="http://uk.ltd.mediamagic/connector/wms/orders" 
)
public class OrderInfo {
	String orderNumber;
	int state;
	int priority;
	int totalPositions;
	int pickedPositions;
	List<String> goodsOutNumbers;
	List<PickUnitLoad> unitLoads;
		
	public OrderInfo(String orderNumber, int totalPositions, int pickedPositions) {
		super();
		this.orderNumber = orderNumber;
		this.totalPositions = totalPositions;
		this.pickedPositions = pickedPositions;
		goodsOutNumbers = Collections.emptyList();
		unitLoads = Collections.emptyList();
	}
	
	public OrderInfo() {
		orderNumber = "";
		state = 0;
		totalPositions = 0;
		pickedPositions = 0;
		goodsOutNumbers = Collections.emptyList();
		unitLoads = Collections.emptyList();
	}
	
	public int getPriority() {
		return priority;
	}

	public void setPriority(int priority) {
		this.priority = priority;
	}
	
	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}
	
	public List<String> getGoodsOutNumbers() {
		return goodsOutNumbers;
	}

	public void setGoodsOutNumbers(List<String> goodsOutNumbers) {
		this.goodsOutNumbers = goodsOutNumbers;
	}

	public List<PickUnitLoad> getUnitLoads() {
		return unitLoads;
	}

	public void setUnitLoads(List<PickUnitLoad> unitLoads) {
		this.unitLoads = unitLoads;
	}

	public String getOrderNumber() {
		return orderNumber;
	}
	public void setOrderNumber(String orderNumber) {
		this.orderNumber = orderNumber;
	}
	public int getTotalPositions() {
		return totalPositions;
	}
	public void setTotalPositions(int totalPositions) {
		this.totalPositions = totalPositions;
	}
	public int getPickedPositions() {
		return pickedPositions;
	}
	public void setPickedPositions(int pickedPositions) {
		this.pickedPositions = pickedPositions;
	}
	
	public static class PickUnitLoad {
		String labelId;
		String state;
		String pickedBy;
		
		public PickUnitLoad(String labelId, String pickedBy, String state) {
			super();
			this.labelId = labelId;
			this.pickedBy = pickedBy;
			this.state = state;
		}
		
		public String getPickedBy() {
			return pickedBy;
		}

		public void setPickedBy(String pickedBy) {
			this.pickedBy = pickedBy;
		}



		public String getLabelId() {
			return labelId;
		}
		public void setLabelId(String labelId) {
			this.labelId = labelId;
		}
		public String getState() {
			return state;
		}
		public void setState(String state) {
			this.state = state;
		}
	}
}
