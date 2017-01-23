package uk.ltd.mediamagic.los.reference.ws;

import java.math.BigDecimal;
import java.sql.Date;

import de.linogistix.los.inventory.model.LOSPickingPosition;

public class LotTraceTO {
	private String lotName;
	private String item_nr;
	
	private String customerNumber;
	private String customerName;
	private String customerOrderNumber;
	private String externalOrderNumber;
	private String orderPosition;

	private java.util.Date bestBeforeEnd;
	
	private BigDecimal amount;
	private BigDecimal amountPicked;

	private String pickFromLocation;
	private String pickFromUnitLoad;
	private String to_unitload;

	private String pickedBy;

	private	int pickingPositionState;
	private Long pickingPositionId;

	public LotTraceTO() {
		super();
	}

	public LotTraceTO(String lotName, String item_nr, String customerOrderNumber, String externalOrderNumber,
			String orderPosition, Date bestBeforeEnd, BigDecimal amount, BigDecimal amountPicked, String pickFromLocation,
			String pickFromUnitLoad, String to_unitload, String pickedBy, int pickingPositionState,
			Long pickingPositionId) {
		super();
		this.lotName = lotName;
		this.item_nr = item_nr;
		this.setCustomerNumber(null);
		this.customerName = null;
		this.customerOrderNumber = customerOrderNumber;
		this.externalOrderNumber = externalOrderNumber;
		this.orderPosition = orderPosition;
		this.bestBeforeEnd = bestBeforeEnd;
		this.amount = amount;
		this.amountPicked = amountPicked;
		this.pickFromLocation = pickFromLocation;
		this.pickFromUnitLoad = pickFromUnitLoad;
		this.to_unitload = to_unitload;
		this.pickedBy = pickedBy;
		this.pickingPositionState = pickingPositionState;
		this.pickingPositionId = pickingPositionId;
	}

	public LotTraceTO(LOSPickingPosition pp) {
		super();
		this.lotName = pp.getLotPicked().getName();
		this.item_nr = pp.getItemData().getNumber();
		this.customerName = pp.getCustomerOrderPosition().getOrder().getCustomerName();
		this.customerNumber = pp.getCustomerOrderPosition().getOrder().getCustomerNumber();
		this.customerOrderNumber = pp.getCustomerOrderPosition().getOrder().getNumber();
		this.externalOrderNumber = pp.getCustomerOrderPosition().getOrder().getExternalNumber();
		this.orderPosition = pp.getCustomerOrderPosition().getNumber();
		this.bestBeforeEnd = pp.getLotPicked().getBestBeforeEnd();
		this.amount = pp.getAmount();
		this.amountPicked = pp.getAmountPicked();
		this.pickFromLocation = pp.getPickFromLocationName();
		this.pickFromUnitLoad = pp.getPickFromUnitLoadLabel();
		this.to_unitload = pp.getPickToUnitLoad().getUnitLoad().getLabelId();
		this.pickedBy = pp.getPickingOrder().getOperator().getName();
		this.pickingPositionState = pp.getState();
		this.pickingPositionId = pp.getId();
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

	public String getLotName() {
		return lotName;
	}
	
	public void setLotName(String lotName) {
		this.lotName = lotName;
	}
	
	public String getItem_nr() {
		return item_nr;
	}
	
	public void setItem_nr(String item_nr) {
		this.item_nr = item_nr;
	}
	
	public String getCustomerOrderNumber() {
		return customerOrderNumber;
	}
	
	public void setCustomerOrderNumber(String customerOrderNumber) {
		this.customerOrderNumber = customerOrderNumber;
	}
	
	public String getExternalOrderNumber() {
		return externalOrderNumber;
	}
	
	public void setExternalOrderNumber(String externalOrderNumber) {
		this.externalOrderNumber = externalOrderNumber;
	}
	
	public String getOrderPosition() {
		return orderPosition;
	}
	
	public void setOrderPosition(String orderPosition) {
		this.orderPosition = orderPosition;
	}
	
	public java.util.Date getBestBeforeEnd() {
		return bestBeforeEnd;
	}
	
	public void setBestBeforeEnd(java.util.Date bestBeforeEnd) {
		this.bestBeforeEnd = bestBeforeEnd;
	}
	
	public BigDecimal getAmount() {
		return amount;
	}
	
	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}
	
	public BigDecimal getAmountPicked() {
		return amountPicked;
	}
	
	public void setAmountPicked(BigDecimal amountPicked) {
		this.amountPicked = amountPicked;
	}
	
	public String getPickFromLocation() {
		return pickFromLocation;
	}
	
	public void setPickFromLocation(String pickFromLocation) {
		this.pickFromLocation = pickFromLocation;
	}
	
	public String getPickFromUnitLoad() {
		return pickFromUnitLoad;
	}
	
	public void setPickFromUnitLoad(String pickFromUnitLoad) {
		this.pickFromUnitLoad = pickFromUnitLoad;
	}
	
	public String getTo_unitload() {
		return to_unitload;
	}
	
	public void setTo_unitload(String to_unitload) {
		this.to_unitload = to_unitload;
	}
	
	public String getPickedBy() {
		return pickedBy;
	}
	
	public void setPickedBy(String pickedBy) {
		this.pickedBy = pickedBy;
	}
	
	public int getPickingPositionState() {
		return pickingPositionState;
	}
	
	public void setPickingPositionState(int pickingPositionState) {
		this.pickingPositionState = pickingPositionState;
	}
	
	public Long getPickingPositionId() {
		return pickingPositionId;
	}

	public void setPickingPositionId(Long pickingPositionId) {
		this.pickingPositionId = pickingPositionId;
	}
}
