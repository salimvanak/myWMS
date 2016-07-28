package uk.ltd.mediamagic.los.reference.ws;

import java.math.BigDecimal;
import java.util.Date;

public class LotCheckResult {
	final String itemNumber;
	final String name;
	final Date bestBefore;
	
	private BigDecimal inStock;
	private BigDecimal available;
	private BigDecimal reserved;
	private BigDecimal locked;
	private BigDecimal advised;
	
	public LotCheckResult(String itemNumber, String name, Date bestBefore) {
		super();
		this.itemNumber = itemNumber;
		this.name = name;
		this.bestBefore = bestBefore;
	}
	public String getItemNumber() {
		return itemNumber;
	}
	public String getName() {
		return name;
	}
	public Date getBestBefore() {
		return bestBefore;
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
}
