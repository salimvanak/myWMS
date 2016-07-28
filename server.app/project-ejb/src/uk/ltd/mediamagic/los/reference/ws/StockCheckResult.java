package uk.ltd.mediamagic.los.reference.ws;

import java.math.BigDecimal;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import uk.ltd.mediamagic.los.reference.ws.MagicStockQueryBean.StockCheckResultType;
@XmlAccessorType(XmlAccessType.FIELD)             
@XmlType(
		name = "StockCheckResult",
		namespace="http://uk.ltd.mediamagic/connector/wms/orders" 
)
public class StockCheckResult {
	final String itemNumber;
	final BigDecimal amountAvailable;
	final StockCheckResultType type;
	
	public StockCheckResult(String itemNumber, BigDecimal amountAvailable, StockCheckResultType type) {
		super();
		this.itemNumber = itemNumber;
		this.amountAvailable = amountAvailable;
		this.type = type;
	}

	public String getItemNumber() {
		return itemNumber;
	}
	
	public BigDecimal getAmountAvailable() {
		return amountAvailable;
	}
	
	public StockCheckResultType getType() {
		return type;
	}

}