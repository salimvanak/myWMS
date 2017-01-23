/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package uk.ltd.mediamagic.los.reference.ws;

import java.util.List;

import javax.ejb.Remote;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;

import org.mywms.facade.FacadeException;
import org.mywms.service.EntityNotFoundException;

import de.linogistix.los.inventory.facade.OrderPositionTO;

/**
 * A Facade for ordering items from stock
 * @author trautm
 *
 */
@Remote
@WebService
@SOAPBinding(style = SOAPBinding.Style.RPC, use = SOAPBinding.Use.LITERAL)
public interface MagicOrder {
	
	/**
	 * creates a new Order for retrieving items from stock.
	 * 
	 * @param clientRef a reference to the client
	 * @param orderRef a reference to the order
	 * @param articleRefs a list of article references
	 * @param document an url to the document to be printed with the order
	 * @param label an url to the label to be printed with the order
	 * @return true if order has been created
	 */
	@WebMethod
	@Deprecated
	String createAndStartOrder1(
			@WebParam( name="clientRef") String clientRef, 
			@WebParam( name="orderRef") String orderRef,
			@WebParam( name="positions") OrderPositionTO[] positions,
			@WebParam( name="documentUrl") String documentUrl, 
			@WebParam( name="labelUrl") String labelUrl,
            @WebParam( name="destination") String destination) throws FacadeException;

	@WebMethod
	@Deprecated
	String createOrder1(
			@WebParam( name="clientRef") String clientRef, 
			@WebParam( name="orderRef") String orderRef,
			@WebParam( name="positions") OrderPositionTO[] positions,
			@WebParam( name="documentUrl") String documentUrl, 
			@WebParam( name="labelUrl") String labelUrl,
            @WebParam( name="destination") String destination) throws FacadeException;

	
	/**
	 * creates a new Order for retrieving items from stock.
	 * 
	 * @param clientRef a reference to the client
	 * @param orderRef a reference to the order
	 * @param articleRefs a list of article references
	 * @param document an url to the document to be printed with the order
	 * @param label an url to the label to be printed with the order
	 * @return true if order has been created
	 */
	@WebMethod
	String createAndStartOrder(
			@WebParam( name="clientRef") String clientRef, 
			@WebParam( name="orderRef") String orderRef,
			@WebParam( name="customerNumber") String customerNumber,
			@WebParam( name="customerName") String customerName,
			@WebParam( name="positions") OrderPositionTO[] positions,
			@WebParam( name="documentUrl") String documentUrl, 
			@WebParam( name="labelUrl") String labelUrl,
            @WebParam( name="destination") String destination) throws FacadeException;

	@WebMethod
	String createOrder(
			@WebParam( name="clientRef") String clientRef, 
			@WebParam( name="orderRef") String orderRef,
			@WebParam( name="customerNumber") String customerNumber,
			@WebParam( name="customerName") String customerName,
			@WebParam( name="positions") OrderPositionTO[] positions,
			@WebParam( name="documentUrl") String documentUrl, 
			@WebParam( name="labelUrl") String labelUrl,
            @WebParam( name="destination") String destination) throws FacadeException;

	
	/**
	 * the order positions associated with the given order.
	 * @param orderNumber the order number
	 * @return a list of order positions
	 * @throws FacadeException if a error occurs.
	 */
    @WebMethod
    public List<OrderPositionTO> pickedPositions(@WebParam( name="orderNumber") String orderNumber) throws FacadeException;

    /**
     * Gets the status info for the order number
     * @param orderNumber the order number
     * @return the info 
     * @throws FacadeException if an error occurs.
     */
    @WebMethod
    public OrderInfo getOrderInfo(@WebParam( name="orderNumber") String orderNumber) throws FacadeException;

    /**
     * Gets the status info for all the open orders
     * @param orderNumber the order number
     * @return the info 
     * @throws FacadeException if an error occurs.
     */
    @WebMethod
    public List<OrderInfo> getOpenOrders() throws FacadeException;

    /**
     * Reserved all the picks on the given order of a particular user.
     * @param orderNumber the order number
     * @param userName the user to reserve for.
     * @throws FacadeException an error occurred or if the order has already been reserved.
     */
    @WebMethod
    public void reserveOrder(@WebParam( name="orderNumber") String orderNumber, @WebParam( name="userName") String userName) throws FacadeException;

    /**
     * Removes an order.  The order will only be removed if it has not been started yet.
     * @param orderNumber the order number to remove
     * @throws FacadeException an error occurred or if the order cannot be removed.
     */
    @WebMethod
    public void cancelOrder(@WebParam( name="orderNumber") String orderNumber) throws FacadeException;

    /**
     * Sets the order priority, if pick have been generated their priority is changed also.
     * @param orderNumber
     * @param prio
     * @throws FacadeException
     */
    public void setOrderPriority(@WebParam( name="orderNumber") String orderNumber, @WebParam( name="priority")int prio) throws FacadeException;

    /**
     * Returns a list of goods out locations suitable for the destination of an order    
     * @return a list of location names.
     * @throws FacadeException an error occurred or if no locations were found.
     */
    @WebMethod
    public List<String> getGoodsOutLocations() throws FacadeException;    

    /**
     * Returns a list of LotTraceTO object that contain data about how picking order positions 
     * for the given lot.
     * @param clientName the client name
     * @param itemNumber the item number
     * @param lotName the lotName
     * @return a list of LotTraceTO objects
     * @throws FacadeException
     * @throws EntityNotFoundException if the item number client or lot cannot be found.
     */
    @WebMethod
		public List<LotTraceTO> traceLot(String clientName, String itemNumber, String lotName) throws FacadeException, EntityNotFoundException;

}
