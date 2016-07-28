package uk.ltd.mediamagic.los.reference.ws;

import java.math.BigDecimal;
import java.util.Date;

public class StockUnitResult {
	public void setLot(String lot) {
		this.lot = lot;
	}

	public void setUnitLoad(String unitLoad) {
		this.unitLoad = unitLoad;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public void setBestBefore(Date bestBefore) {
		this.bestBefore = bestBefore;
	}

	final String stockUnit;
	final String itemNumber;
	private String lot;
	private String unitLoad;
	private String location;
	private Date bestBefore;
	
	private BigDecimal inStock;
	private BigDecimal available;
	private BigDecimal reserved;
	private BigDecimal locked;
	private BigDecimal advised;
	
	public StockUnitResult(String stockUnit, String itemNumber) {
		super();
		this.itemNumber = itemNumber;
		this.stockUnit = stockUnit;
	}

	public BigDecimal getInStock() {
		return inStock;
	}

	public void setInStock(BigDecimal inStock) {
		this.inStock = inStock;
	}

	public BigDecimal getAvailable() {
		return available;
	}

	public void setAvailable(BigDecimal available) {
		this.available = available;
	}

	public BigDecimal getReserved() {
		return reserved;
	}

	public void setReserved(BigDecimal reserved) {
		this.reserved = reserved;
	}

	public BigDecimal getLocked() {
		return locked;
	}

	public void setLocked(BigDecimal locked) {
		this.locked = locked;
	}

	public BigDecimal getAdvised() {
		return advised;
	}

	public void setAdvised(BigDecimal advised) {
		this.advised = advised;
	}

	public String getStockUnit() {
		return stockUnit;
	}

	public String getItemNumber() {
		return itemNumber;
	}

	public String getLot() {
		return lot;
	}

	public String getUnitLoad() {
		return unitLoad;
	}

	public String getLocation() {
		return location;
	}

	public Date getBestBefore() {
		return bestBefore;
	}
	
}
