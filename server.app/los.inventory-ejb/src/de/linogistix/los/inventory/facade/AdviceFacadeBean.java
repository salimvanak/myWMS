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
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.log4j.Logger;
import org.mywms.model.Client;
import org.mywms.model.ItemData;
import org.mywms.model.Lot;
import org.mywms.service.ClientService;
import org.mywms.service.EntityNotFoundException;
import org.mywms.service.ItemDataService;
import org.mywms.service.LotService;

import de.linogistix.los.inventory.businessservice.LOSAdviceBusiness;
import de.linogistix.los.inventory.businessservice.LOSGoodsReceiptComponent;
import de.linogistix.los.inventory.exception.InventoryException;
import de.linogistix.los.inventory.exception.InventoryExceptionKey;
import de.linogistix.los.inventory.model.LOSAdvice;
import de.linogistix.los.inventory.model.LOSAdviceState;
import de.linogistix.los.inventory.model.LOSGoodsReceipt;
import de.linogistix.los.inventory.model.LOSGoodsReceiptState;
import de.linogistix.los.inventory.service.LOSGoodsReceiptService;
import de.linogistix.los.query.BODTO;

@Stateless
public class AdviceFacadeBean implements AdviceFacade {
	Logger log = Logger.getLogger(AdviceFacadeBean.class);

	@EJB
	private LOSAdviceBusiness adviceBusiness;
	
	@EJB
	private LOSGoodsReceiptComponent grComponent;
	
	@EJB
	private LOSGoodsReceiptService grService;
	
	@EJB private ClientService clientService;
	@EJB private ItemDataService itemDataService;
	@EJB private LotService lotService;
	
	@PersistenceContext(unitName = "myWMS")
	protected EntityManager manager;
	
	public void removeAdvise(BODTO<LOSAdvice> adv) throws InventoryException {
		log.debug("removeAdvise Start advId=" + adv.getId());
		LOSAdvice adv2 = manager.find(LOSAdvice.class, adv.getId());
		
		
		// Find GoodsReceipts with this advice
		List<LOSGoodsReceipt> grList = grService.getByAdvice(adv2);
		for( LOSGoodsReceipt gr : grList ) {
			grComponent.removeAssignedAdvice(adv2, gr);
		}
		adviceBusiness.removeAdvise(adv2.getClient(), adv2);
	}

	public void finishAdvise(BODTO<LOSAdvice> adv) throws InventoryException {
		log.debug("finishAdvise Start advId=" + adv.getId());
		LOSAdvice adv2 = manager.find(LOSAdvice.class, adv.getId());
		
		// Find GoodsReceipts with this advice
		List<LOSGoodsReceipt> grList = grService.getByAdvice(adv2);
		for( LOSGoodsReceipt gr : grList ) {
			if( gr.getReceiptState() != LOSGoodsReceiptState.CANCELED &&  gr.getReceiptState() != LOSGoodsReceiptState.FINISHED ) {
				throw new InventoryException(InventoryExceptionKey.GOODS_RECEIPT_NOT_FINISHED, gr.getGoodsReceiptNumber());
			}
		}
		adv2.setAdviceState(LOSAdviceState.FINISHED);
		adv2.setFinishDate(new Date());
	}
	
	@Override
	public LOSAdvice createAdvise(BODTO<Client> c, BODTO<ItemData> item, BODTO<Lot> lotTo, 
			BigDecimal amount, boolean expireLot, Date expectedDelivery, String requestId) throws InventoryException {
		Client client;
		ItemData itemData;
		Lot lot;
		try {
			client = clientService.get(c.getId());
		} catch (EntityNotFoundException e) {
			throw new InventoryException(InventoryExceptionKey.NO_SUCH_CLIENT, c.getName());
		}
		
		try {
			itemData = itemDataService.get(item.getId());
		} catch (EntityNotFoundException e) {
			throw new InventoryException(InventoryExceptionKey.NO_SUCH_ITEMDATA, item.getName());
		}

		try {
			lot = (lotTo == null) ? null : lotService.get(lotTo.getId());
		} catch (EntityNotFoundException e) {
			throw new InventoryException(InventoryExceptionKey.NO_SUCH_ITEMDATA, item.getName());
		}
		
		adviceBusiness.goodsAdvise(client, itemData, lot, amount, expireLot, expectedDelivery, requestId);

		return null;
	}
	
}
