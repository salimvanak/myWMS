package de.linogistix.los.inventory.businessservice;

import java.io.Serializable;
import java.math.BigDecimal;

public class PickingStockUnitTO implements Serializable {
	private static final long serialVersionUID = 1L;

	public long stockId;
	public boolean opened = true;
	public long locationId;
	public boolean fix = false;
	public String locationName;
	public long unitLoadId;
	public String unitLoadLabel;
	public long lotId=0;

	public boolean useForPick = true;
	public boolean useForTransport = true;

	public BigDecimal amount;
	public BigDecimal amountReserved;
	public BigDecimal amountAvailable;

	public PickingStockUnitTO(long id, BigDecimal amount, BigDecimal amountReserved, boolean useForPick, boolean useForTransport, long locationId, String locationName, long unitLoadId, String unitLoadLabel, boolean opened) {
		this.stockId = id;
		this.locationId = locationId;
		this.locationName = locationName;
		this.amount = amount;
		this.amountReserved = amountReserved;
		this.amountAvailable = (amountReserved == null ? amount : amount.subtract(amountReserved));
		this.unitLoadId = unitLoadId;
		this.unitLoadLabel = unitLoadLabel;
		this.opened = opened;
		this.useForPick = useForPick;
		this.useForTransport = useForTransport;
	}


	public String toString() {
		return "id="+stockId+", amount="+amount+", location="+locationName;
	}



}