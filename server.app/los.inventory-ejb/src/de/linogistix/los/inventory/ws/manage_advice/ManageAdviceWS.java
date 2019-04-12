/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.inventory.ws.manage_advice;

import javax.ejb.Remote;
import javax.jws.WebMethod;
import javax.jws.WebService;

@Remote
@WebService
public interface ManageAdviceWS {

	@WebMethod
	String adviceUnitLoad(AdviceUnitLoadRequest req) throws ManageAdviceWSFault;
	
	@WebMethod
	AdviceUnitLoadResponse getAdviceForUnitLoad(String unitLoadId) throws ManageAdviceWSFault;
	
	/**
	 * sets the give advice to finished.
	 * @param adviceNumber the advice number
	 * @throws ManageAdviceWSFault
	 */
	@WebMethod
	CommitAdviceResponse commitUnitLoadAdvice(String adviceNumber) throws ManageAdviceWSFault;
	
	@WebMethod
	void rejectUnitLoadAdvice(RejectAdviceRequest req) throws ManageAdviceWSFault;
	
	/**
	 * Create or update a LOSAdvice.
	 * The Operation will create a new advice, if it is not known. Otherwise it will be updated.
	 * Update is only allowed, if the advice is not used. 
	 * After assigning it to a goods receipt or receiving material on it, an update will be rejected.   
	 * To update, the advice is searched by one of the fields adviceNumber, externalId, externalAdviceNumber
	 * 
	 * @param UpdateAdviceRequest, a data object containing the parameters
	 * @return The LOS Advice Number
	 * @throws ManageAdviceWSFault if for some goes wrong. See also {@link ManageAdviceErrorCodes}
	 */
	@WebMethod
	String updateAdvice(UpdateAdviceRequest updateReq) throws ManageAdviceWSFault;
	
	/**
	 * delete a LOSAdvice.
	 * The advice is searched by one of the fields adviceNumber, externalId, externalAdviceNumber
	 * 
	 * @param UpdateAdviceRequest, a data object containing the parameters
	 * @return The LOS Advice Number
	 * @throws ManageAdviceWSFault if for some goes wrong. See also {@link ManageAdviceErrorCodes}
	 */
	@WebMethod
	void deleteAdvice( DeleteAdviceRequest data ) throws ManageAdviceWSFault;
	
	/**
	 * Fetches the advice.
	 * The advice is searched by one of: adviceNumber, externalId, externalAdviceNumber
	 * in order
	 * 
	 * The lot number returned is the lot assigned to the advice,  in the case 
	 * there is no lot on the advice, the lot on the first GoodsReceiptPosition.
	 * 
	 * @param clientNumber the client number, null for the system client
	 * @param id the number to search for.
	 * @return LOSAdviceTO, representation of the advice
	 * @throws ManageAdviceWSFault if for some goes wrong. See also {@link ManageAdviceErrorCodes}
	 */
	@WebMethod
	FindAdviceResult findAdvice(String clientNumber, String id) throws ManageAdviceWSFault;
}
