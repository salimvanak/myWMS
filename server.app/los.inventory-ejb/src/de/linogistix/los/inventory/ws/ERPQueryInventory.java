/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.inventory.ws;


import javax.ejb.Remote;
import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;

/**
 * Contract for retrieving and managing ItemData from an ERP system
 * 
 * @author trautm
 *
 */
@Remote
@WebService
@SOAPBinding(style = SOAPBinding.Style.RPC, use = SOAPBinding.Use.LITERAL)
public interface ERPQueryInventory extends java.rmi.Remote {

	String TIME_OUT_INFO = "update item data from erp";
	public final static String TIME_OUT_KEY = "timeout millis";

	/**
	 * Returns information an ItemData/article known in the ERP system
	 * 
	 * @param clientRef a reference to the client
	 * @param articleRef a reference to the article
	 */
	@WebMethod
	ERPItemDataTO[] getItemData(String clientref, String articleRef);
	
	/**
	 * Returns Batch information from ERP
	 */
	@WebMethod
	ERPBatchDataTO[] getBatchData(String clientref, String batchRef);
	
	public void createCronJob();
	
	public void cancelCronJob();
	
	public String statusCronJob();
	
//	public void timeout(Timer timer);
	
	public void updateItemData();
	
	public void updateLot() ;
}
