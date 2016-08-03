/*
 * Copyright (c) 2006 - 2012 LinogistiX GmbH
 *
 *  www.linogistix.com
 *
 *  Project myWMS-LOS
 */
package de.linogistix.los.inventory.report;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import de.linogistix.los.util.StringTools;

/**
 * @author krane
 *
 */
public class LOSStockUnitReportTO {

	public String pos = "";

	public String itemNumber = "";
	public String itemName = "";
	public String itemUnit = "";
	public int itemScale;

	public String lotName = "";
	public String serialNumber = "";
	public BigDecimal amount = BigDecimal.ZERO;
	public Date useNotBefore;
	public Date bestBefore;

	public Date getUseNotBefore() {
		return useNotBefore;
	}

	public void setUseNotBefore(Date useNotBefore) {
		this.useNotBefore = useNotBefore;
	}

	public Date getBestBefore() {
		return bestBefore;
	}

	public void setBestBefore(Date bestBefore) {
		this.bestBefore = bestBefore;
	}

	public String getPos() {
		return pos;
	}

	public String getItemNumber() {
		return itemNumber;
	}

	public String getFormattedItemNumber() {
		if( !StringTools.isEmpty(lotName) ) {
			return itemNumber + " (" + lotName + ")";
		}
		return itemNumber;
	}

	public String getItemName() {
		return itemName;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public String getFormattedAmount() {
		if( amount != null && amount.compareTo(BigDecimal.ZERO)!=0 ) {
			return amount.toString();
		}
		return "";
	}

	public String getItemUnit() {
		return itemUnit;
	}

	public String getFormattedItemUnit() {
		if( amount != null && amount.compareTo(BigDecimal.ZERO)!=0 ) {
			return itemUnit;
		}
		return "";
	}

	public String getLotName() {
		return lotName;
	}

	public String getSerialNumber() {
		return serialNumber;
	}

	@SuppressWarnings("deprecation")
	public static Collection<LOSStockUnitReportTO> createBeanCollection() {
		ArrayList<LOSStockUnitReportTO> l = new ArrayList<>();
		LOSStockUnitReportTO a = new LOSStockUnitReportTO();
		a.amount = new BigDecimal(999);
		a.bestBefore = new Date();
		a.itemName = "This is the item description that is 60 charactors long XXX.";
		a.itemNumber = "0123456789";
		a.itemScale = 3;
		a.itemUnit = "Peices";
		a.lotName = "LOT-1234567890/20CHAR";
		a.pos = "POSITION-00000";
		a.serialNumber = "SERIAL NUMBER-20CHARS";
		a.useNotBefore = new Date(2099, 1, 1);
		l.add(a);
		return l;
	}
}
