/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.inventory.facade;

import java.math.BigDecimal;
import java.util.Date;

import javax.ejb.Remote;

import org.mywms.model.Client;
import org.mywms.model.ItemData;
import org.mywms.model.Lot;

import de.linogistix.los.inventory.exception.InventoryException;
import de.linogistix.los.inventory.model.LOSAdvice;
import de.linogistix.los.query.BODTO;

@Remote
public interface AdviceFacade {
	/**
	 * Creates a new {@link LOSAdvice}
	 * @param c
	 * @param lot
	 * @param amount
	 * @param expireLot
	 * @param expectedDelivery
	 * @param requestId unique id
	 * @return requestID of created LOSAdvice
	 * @throws InventoryException 
	 */
	public abstract LOSAdvice createAdvise(BODTO<Client> c, BODTO<ItemData> item, BODTO<Lot> lot, 
			BigDecimal amount, boolean expireLot, Date expectedDelivery, String requestId) throws InventoryException;

	public void removeAdvise(BODTO<LOSAdvice> adv) throws InventoryException;

	public void finishAdvise(BODTO<LOSAdvice> adv) throws InventoryException;

}
