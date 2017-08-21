package uk.ltd.mediamagic.los.reference.ws;

import java.util.Collections;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import de.linogistix.los.inventory.model.LOSCustomerOrder;
import de.linogistix.los.inventory.model.LOSPickingOrder;

@XmlAccessorType(XmlAccessType.FIELD)             
@XmlType(
		name = "OrderInfo",
		namespace="http://uk.ltd.mediamagic/connector/wms/orders" 
)
public class OrderInfo {
	String orderNumber;
	String customerNumber = null;
	String customerName = null;
	String externalNumber = null;
	String externalId = null;

	int state;
	int priority;
	int totalPositions = -1;
	int pickedPositions = -1;
		
	String destinationLocation = null;
	
	List<String> goodsOutNumbers;
	List<PickUnitLoad> unitLoads;
	List<PickingOrder> pickingOrders;
	
	public OrderInfo(LOSCustomerOrder order) {
		this();
		this.orderNumber = order.getNumber();
		this.priority = order.getPrio();
		this.state = order.getState();
		this.customerNumber = order.getCustomerNumber();
		this.customerName = order.getCustomerName();
		this.externalNumber = order.getExternalNumber();
		this.externalId = order.getExternalId();
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
	
	public String getDestinationLocation() {
		return destinationLocation;
	}

	public void setDestinationLocation(String destinationLocation) {
		this.destinationLocation = destinationLocation;
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
	
	public String getCustomerNumber() {
		return customerNumber;
	}

	public void setCustomerNumber(String customerNumber) {
		this.customerNumber = customerNumber;
	}

	public String getCustomerName() {
		return customerName;
	}

	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}

	public String getExternalNumber() {
		return externalNumber;
	}

	public void setExternalNumber(String externalNumber) {
		this.externalNumber = externalNumber;
	}

	public String getExternalId() {
		return externalId;
	}

	public void setExternalId(String externalId) {
		this.externalId = externalId;
	}

	public List<PickingOrder> getPickingOrders() {
		return pickingOrders;
	}

	public void setPickingOrders(List<PickingOrder> pickingOrders) {
		this.pickingOrders = pickingOrders;
	}

	public static class PickingOrder {
		private String number;
		private String clientNumber;
		private String customerOrderNumber;
		private int state;
		private int numPos;
		private int prio;
	  private String userName;
	  private String destinationName;
		
		public PickingOrder(LOSPickingOrder pick) {
			this.number = pick.getNumber();
			this.clientNumber= pick.getClient().getNumber();
			this.customerOrderNumber = pick.getCustomerOrderNumber();
			this.state = pick.getState();
			this.numPos=pick.getPositions().size();
			this.prio = pick.getPrio();
			this.userName = (pick.getOperator() == null) ? "" : pick.getOperator().getName();
			this.destinationName = pick.getDestination().getName();
		}

		public String getNumber() {
			return number;
		}

		public void setNumber(String number) {
			this.number = number;
		}

		public String getClientNumber() {
			return clientNumber;
		}

		public void setClientNumber(String clientNumber) {
			this.clientNumber = clientNumber;
		}

		public String getCustomerOrderNumber() {
			return customerOrderNumber;
		}

		public void setCustomerOrderNumber(String customerOrderNumber) {
			this.customerOrderNumber = customerOrderNumber;
		}

		public int getState() {
			return state;
		}

		public void setState(int state) {
			this.state = state;
		}

		public int getNumPos() {
			return numPos;
		}

		public void setNumPos(int numPos) {
			this.numPos = numPos;
		}

		public int getPrio() {
			return prio;
		}

		public void setPrio(int prio) {
			this.prio = prio;
		}

		public String getUserName() {
			return userName;
		}

		public void setUserName(String userName) {
			this.userName = userName;
		}

		public String getDestinationName() {
			return destinationName;
		}

		public void setDestinationName(String destinationName) {
			this.destinationName = destinationName;
		}
	}
	
	public static class PickUnitLoad {
		String labelId;
		String location;
		String pickedBy;
		
		public PickUnitLoad(String labelId, String pickedBy, String location) {
			super();
			this.labelId = labelId;
			this.pickedBy = pickedBy;
			this.location = location;
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
		public String getLocation() {
			return location;
		}
		public void setLocation(String location) {
			this.location = location;
		}
	}
}
