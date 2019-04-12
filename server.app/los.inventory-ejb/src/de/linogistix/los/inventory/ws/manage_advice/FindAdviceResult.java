/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.inventory.ws.manage_advice;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

import de.linogistix.los.inventory.model.LOSAdvice;
import de.linogistix.los.inventory.model.LOSGoodsReceiptPosition;
@XmlAccessorType(XmlAccessType.PROPERTY)
public class FindAdviceResult {
	private String adviceNumber;
	
	private String state;

	private BigDecimal receiptAmount;

	private BigDecimal notifiedAmount;

	private String itemData;

	private String itemDataName;

	private String lot = "";
	private Date bestBeforeEnd = null;

	private String client;

	private Date expectedDelivery;
	
	private List<String> grPositions = new ArrayList<>();

	public List<String> getGoodsReceiptPositions() {
		return grPositions;
	}

	public void setGoodsReceiptPositions(List<String> positions) {
		this.grPositions = positions;
	}

	public FindAdviceResult() {
	}
	
	public static FindAdviceResult forAdvice(LOSAdvice adv) {
		FindAdviceResult out = new FindAdviceResult();
		out.adviceNumber = adv.getAdviceNumber();
		out.state = adv.getAdviceState().toString();
		out.receiptAmount = adv.getReceiptAmount().setScale(adv.getItemData().getScale());
		out.notifiedAmount = adv.getNotifiedAmount().setScale(adv.getItemData().getScale());
		out.itemData = adv.getItemData().getNumber();;
		out.itemDataName = adv.getItemData().getName();;

		if (adv.getLot() != null) {
			out.lot = adv.getLot().getName();
			out.bestBeforeEnd = adv.getLot().getBestBeforeEnd();
		}
		
		out.grPositions = adv.getGrPositionList().stream()
				.map(LOSGoodsReceiptPosition::getPositionNumber)
				.collect(Collectors.toList());
		
		out.client = adv.getClient().getNumber();
		out.expectedDelivery = adv.getExpectedDelivery();
		return out;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public BigDecimal getReceiptAmount() {
		return receiptAmount;
	}

	public void setReceiptAmount(BigDecimal amount) {
		this.receiptAmount = amount;
	}

	public BigDecimal getNotifiedAmount() {
		return notifiedAmount;
	}

	public void setNotifiedAmount(BigDecimal notifiedAmount) {
		this.notifiedAmount = notifiedAmount;
	}

	public String getItemData() {
		return itemData;
	}

	public void setItemData(String itemData) {
		this.itemData = itemData;
	}

	public String getLot() {
		return lot;
	}

	public void setLot(String lot) {
		this.lot = lot;
	}
	
	public String getAdviceNumber() {
		return adviceNumber;
	}

	public void setAdviceNumber(String adviceNumber) {
		this.adviceNumber = adviceNumber;
	}

	public Date getBestBeforeEnd() {
		return bestBeforeEnd;
	}

	public void setBestBeforeEnd(Date bestBeforeEnd) {
		this.bestBeforeEnd = bestBeforeEnd;
	}

	public String getClient() {
		return client;
	}

	public void setClient(String client) {
		this.client = client;
	}

	public Date getExpectedDelivery() {
		return expectedDelivery;
	}

	public void setExpectedDelivery(Date expectedDelivery) {
		this.expectedDelivery = expectedDelivery;
	}
	
	public String getItemDataName() {
		return itemDataName;
	}
	
	public void setItemDataName(String itemDataName) {
		this.itemDataName = itemDataName;
	}
}
