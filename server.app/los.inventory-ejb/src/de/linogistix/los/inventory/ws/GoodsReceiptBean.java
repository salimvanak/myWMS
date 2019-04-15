/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.inventory.ws;

import java.util.Date;

import javax.annotation.security.PermitAll;
import javax.ejb.EJB;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;

import org.apache.log4j.Logger;
//dgrys neues paket - aenderung portierung wildfly
//import org.jboss.annotation.security.SecurityDomain;
import org.jboss.ejb3.annotation.SecurityDomain;
//dgrys aenderung portierung wildfly
import org.jboss.ws.api.annotation.WebContext;
import org.mywms.facade.FacadeException;
import org.mywms.globals.ServiceExceptionKey;
import org.mywms.model.Client;
import org.mywms.model.ItemData;
import org.mywms.model.Lot;
import org.mywms.model.UnitLoadType;
import org.mywms.service.ClientService;
import org.mywms.service.EntityNotFoundException;
import org.mywms.service.ItemDataService;

import de.linogistix.los.common.exception.UnAuthorizedException;
import de.linogistix.los.inventory.businessservice.LOSGoodsReceiptComponent;
import de.linogistix.los.inventory.customization.ManageAdviceService;
import de.linogistix.los.inventory.customization.ManageReceiptService;
import de.linogistix.los.inventory.exception.InventoryException;
import de.linogistix.los.inventory.exception.InventoryExceptionKey;
import de.linogistix.los.inventory.model.LOSAdvice;
import de.linogistix.los.inventory.model.LOSGoodsReceipt;
import de.linogistix.los.inventory.model.LOSGoodsReceiptPosition;
import de.linogistix.los.inventory.query.LOSGoodsReceiptPositionQueryRemote;
import de.linogistix.los.inventory.service.LOSGoodsReceiptPositionService;
import de.linogistix.los.inventory.service.LOSGoodsReceiptService;
import de.linogistix.los.inventory.service.LOSLotService;
import de.linogistix.los.inventory.service.QueryAdviceService;
import de.linogistix.los.inventory.ws.manage_advice.UpdateAdviceRequest;
import de.linogistix.los.location.entityservice.LOSStorageLocationService;
import de.linogistix.los.location.model.LOSStorageLocation;
import de.linogistix.los.location.model.LOSUnitLoad;
import de.linogistix.los.location.service.QueryUnitLoadTypeService;
import de.linogistix.los.query.exception.BusinessObjectNotFoundException;
import de.linogistix.los.util.StringTools;
import de.linogistix.los.util.businessservice.ContextService;

@Stateless
@SecurityDomain("los-login")
@Remote(GoodsReceipt.class)
@WebService(endpointInterface = "de.linogistix.los.inventory.ws.GoodsReceipt")
@SOAPBinding(style = SOAPBinding.Style.RPC, use = SOAPBinding.Use.LITERAL)
@WebContext(contextRoot = "/webservice", authMethod = "BASIC", transportGuarantee = "NONE", secureWSDLAccess = true)
//dgrys portierung wildfly 8.2, workaround to call web service, required to log in
@PermitAll
public class GoodsReceiptBean implements GoodsReceipt {

	private static final Logger log = Logger.getLogger(GoodsReceiptBean.class);

	@EJB
	private LOSGoodsReceiptComponent goodsReceiptComponent;
	@EJB
	private LOSGoodsReceiptService goodsReceiptService;
	@EJB
	private LOSGoodsReceiptPositionService goodsReceiptPositionService;
	@EJB
	private LOSGoodsReceiptPositionQueryRemote	 goodsReceiptPositionQuery;
	@EJB
	private ClientService clientService;
	@EJB
	private LOSLotService lotService;
	@EJB
	private ItemDataService itemDataService;
	@EJB
	private LOSStorageLocationService slService;
	@EJB
	private QueryUnitLoadTypeService ulTypeService;
	@EJB
	private ContextService context;
	@EJB
	private QueryAdviceService adviceQueryService;
	@EJB
	private ManageAdviceService adviceService;
	@EJB
	private ManageReceiptService manageGrService;
	
	@Override
	public String getOpenByDeliveryNoteNumber(String deliveryNoteNumber) {
		LOSGoodsReceipt r = goodsReceiptService.getOpenByDeliveryNoteNumber(deliveryNoteNumber);
		if (r == null) 
			return "";
		else 
			return r.getGoodsReceiptNumber();
	}
	
	@Override
	public GoodsReceiptPositionTO getGoodsReceiptPosition(String positionNumber) {
		GoodsReceiptPositionTO to = new GoodsReceiptPositionTO();
		LOSGoodsReceiptPosition p = goodsReceiptPositionService.getByNumber(positionNumber);
		to.setAdvice(p.getRelatedAdvice().getAdviceNumber());
		to.setAmount(p.getAmount());
		to.setItemDataNumber(p.getItemData());
		to.setLock(p.getLock());
		to.setLotName(p.getLot());
		to.setUnitLoadLabelId(p.getUnitLoad());
		if (!StringTools.isEmpty(p.getLot())) {
			try {
				ItemData itemData = resolveItemData(p.getClient(), p.getItemData());
				Lot lot = resolveLot(p.getClient(), itemData, p.getLot());
				to.setBestBeforeEnd(lot.getBestBeforeEnd());
				to.setNotUseBefore(lot.getUseNotBefore());
			}
			catch (InventoryException e) {
				log.error("getGoodsReceiptPosition " + e.getMessage(), e);
			}
			catch (FacadeException e) {
				log.error("getGoodsReceiptPosition " + e.getMessage(), e);
			}
		}
		return to;
	}
	
	public void create(
			@WebParam(name = "client") String client, 
			@WebParam(name = "storageLocation") String storageLocation, 
			@WebParam(name = "licencePlate") String licencePlate, 
			@WebParam(name = "driver") 	String driver, 
			@WebParam(name = "deliveryNoteNumber") String deliveryNoteNumber,
			@WebParam(name = "forwarder") String forwarder,
			@WebParam(name = "positions") GoodsReceiptPositionTO[] positions) throws InventoryException, FacadeException {
		
		log.info("create");
		try {
			Client usersClient = context.getCallersUser().getClient();
			Client c = clientService.getByNumber(client);

			if ((!usersClient.isSystemClient()) && (!usersClient.equals(c))) {
				throw new InventoryException(
						InventoryExceptionKey.CLIENT_MISMATCH, usersClient
								.getNumber());
			}

			LOSGoodsReceipt goodsReceipt;
			goodsReceipt = goodsReceiptComponent.createGoodsReceipt(c,
					licencePlate, driver, forwarder, deliveryNoteNumber,
					new Date());

			LOSStorageLocation sl = slService.getByName(storageLocation);
			if (sl == null){
				throw new BusinessObjectNotFoundException(storageLocation);
			}

			for (GoodsReceiptPositionTO position : positions) {
				log.info("Retrieving Lot for " + c.getNumber() + "/"
						+ position.getLotName() + "/"
						+ position.getItemDataNumber());
				
				
				ItemData itemData = resolveItemData(c, position.getItemDataNumber());
				Lot lot = resolveLot(c, itemData, position.getLotName());
				UnitLoadType type = resolveUnitLoadType(c, position.getUnitLoadType());
				
				LOSUnitLoad ul = goodsReceiptComponent.getOrCreateUnitLoad(c,
						sl, type, position.getUnitLoadLabelId());
				
				LOSGoodsReceiptPosition pos = goodsReceiptComponent
						.createGoodsReceiptPosition(c, goodsReceipt,
								deliveryNoteNumber, position.getAmount());
				
				goodsReceiptComponent.receiveStock(pos, lot, itemData, position.getAmount(), ul, null);
				
				LOSAdvice adv = resolveAdvice(position.getAdvice());
				
				if (adv != null) {
					goodsReceiptComponent.assignAdvice(adv, pos);
				} else {
					if (itemData.isAdviceMandatory()) {
						throw new InventoryException(
								InventoryExceptionKey.ADVICE_MANDATORY,
								itemData.getNumber());
					}
				}
				
				manageGrService.onGoodsReceiptPositionCollected(pos);
			}
		} catch (EntityNotFoundException e) {
			log.error(e.getMessage(), e);
			throw new InventoryException(
					InventoryExceptionKey.CREATE_GOODSRECEIPT, "");
		}
		

	}

	public String createAdvices(
			@WebParam(name = "client") String client, 
			@WebParam(name = "storageLocation") String storageLocation, 
			@WebParam(name = "licencePlate") String licencePlate, 
			@WebParam(name = "driver") 	String driver, 
			@WebParam(name = "deliveryNoteNumber") String deliveryNoteNumber,
			@WebParam(name = "forwarder") String forwarder,
			@WebParam(name = "positions") UpdateAdviceRequest[] positions) throws InventoryException, FacadeException {

		final String logStr = "createAdvices ";
		log.info(logStr);
		Client usersClient = context.getCallersUser().getClient();
		Client c = clientService.getByNumber(client);
		
		if ((!usersClient.isSystemClient()) && (!usersClient.equals(c))) {
			log.error(logStr+"User client does not match");
			throw new InventoryException(
					InventoryExceptionKey.CLIENT_MISMATCH, usersClient.getNumber());
		}


		LOSStorageLocation sl = slService.getByName(storageLocation);
		if (sl == null) {
			log.error(logStr+"Storage location " + storageLocation + " not found");
			throw new BusinessObjectNotFoundException(storageLocation);
		}

		LOSGoodsReceipt goodsReceipt = goodsReceiptComponent.createGoodsReceipt(
				c, licencePlate, driver, forwarder, deliveryNoteNumber,	new Date());
		goodsReceipt.setGoodsInLocation(sl);

		for (UpdateAdviceRequest position : positions) {
			log.info(logStr+"Retrieving Lot for " + c.getNumber() + "/"
					+ position.getLotNumber() + "/"
					+ position.getItemNumber());


			ItemData itemData = resolveItemData(c, position.getItemNumber());
			Lot lot;
			if (StringTools.isEmpty(position.getLotNumber())) {
				lot = null;
			}
			else { 
				lot = resolveLot(c, itemData, position.getLotNumber());
			}

			if(position.getNotifiedAmount() == null){
				log.error(logStr+"Nofified amount must not be null. Abort");
				throw new InventoryException( 
						InventoryExceptionKey.ADVICE_CANNOT_BE_ACCEPTED, 
						"Nofified amount must not be null");
			}

			String advNumber = adviceService.getNewAdviceNumber();				
			LOSAdvice adv;
			try {
				adv = adviceService.createAdvice(c, advNumber, itemData, position.getNotifiedAmount());
				adv.setAdditionalContent(position.getAdditionalContent());
				adv.setExpectedDelivery(position.getExpectedDelivery());
				adv.setExternalAdviceNumber(position.getExternalAdviceNumber());
				adv.setExternalId(position.getExternalId());
				adv.setLot(lot);

				log.info(logStr+"Inserted advice="+adv.getAdviceNumber());
			} catch ( Throwable e ) {
				// It is unbelieveable what exceptions are thrown
				log.error(logStr+"Something went wrong when creating the advice: "+e.getMessage());
				throw new InventoryException(
						InventoryExceptionKey.ADVICE_CANNOT_BE_ACCEPTED, e.getMessage());
			}

			goodsReceiptComponent.assignAdvice(adv, goodsReceipt);				
		}
		return goodsReceipt.getGoodsReceiptNumber();
	}

	private Lot resolveLot(Client c, ItemData idat, String lotName) throws FacadeException{
		Lot lot = null;
		try {
			lot = lotService.getByNameAndItemData(c, lotName, idat.getNumber());
		} catch (EntityNotFoundException ex) {
			log.warn(ex.getMessage() + ": " + lotName);
			throw new InventoryException(InventoryExceptionKey.LOT_MANDATORY, idat.getNumber());
		}
		
		return lot;
	}
	
	private ItemData resolveItemData(Client c, String number) throws InventoryException{
		
		ItemData itemData =  itemDataService.getByItemNumber(c, number);
		
		if(itemData == null){
			log.error("--- ITEM NUMBER NOT FOUND : "	+ number);
			throw new InventoryException(
					InventoryExceptionKey.ITEMDATA_NOT_FOUND, number);
		}
		
		return itemData;
	}
	
	private UnitLoadType resolveUnitLoadType(Client c, String name) throws BusinessObjectNotFoundException{
		UnitLoadType type = null;
		type = ulTypeService.getByName(name);
		if( type == null ) {
			log.error("Could not find Unitload type: " + name );
			type = ulTypeService.getDefaultUnitLoadType();
			if (type == null) {
				throw new BusinessObjectNotFoundException(name);
			}
		}
		
		return type;
	}
	
	private LOSAdvice resolveAdvice(String advice) throws EntityNotFoundException{
		try {
			return adviceQueryService.getByAdviceNumber(advice);
		} catch (UnAuthorizedException e) {
			throw new EntityNotFoundException(ServiceExceptionKey.LOGIN_FAILED);
		}
	}
	
	
	public String[] getSuitableAdvice(String client, String itemData, String Lot) {
		return new String[0];
	}

}
