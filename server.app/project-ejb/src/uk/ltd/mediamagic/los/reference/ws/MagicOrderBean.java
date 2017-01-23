/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package uk.ltd.mediamagic.los.reference.ws;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import javax.annotation.security.PermitAll;
import javax.ejb.EJB;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.apache.log4j.Logger;
import org.jboss.ejb3.annotation.SecurityDomain;
import org.jboss.ws.api.annotation.WebContext;
import org.mywms.facade.BasicFacadeBean;
import org.mywms.facade.FacadeException;
import org.mywms.model.Client;
import org.mywms.model.ItemData;
import org.mywms.model.Lot;
import org.mywms.model.User;
import org.mywms.service.ClientService;
import org.mywms.service.EntityNotFoundException;
import org.mywms.service.ItemDataService;
import org.mywms.service.UserService;

import de.linogistix.los.inventory.businessservice.LOSOrderBusiness;
import de.linogistix.los.inventory.businessservice.LOSPickingPosGenerator;
import de.linogistix.los.inventory.businessservice.LOSPickingStockService;
import de.linogistix.los.inventory.exception.InventoryException;
import de.linogistix.los.inventory.exception.InventoryExceptionKey;
import de.linogistix.los.inventory.facade.LOSOrderFacade;
import de.linogistix.los.inventory.facade.OrderPositionTO;
import de.linogistix.los.inventory.model.LOSCustomerOrder;
import de.linogistix.los.inventory.model.LOSCustomerOrderPosition;
import de.linogistix.los.inventory.model.LOSGoodsOutRequest;
import de.linogistix.los.inventory.model.LOSPickingOrder;
import de.linogistix.los.inventory.model.LOSPickingPosition;
import de.linogistix.los.inventory.model.LOSPickingUnitLoad;
import de.linogistix.los.inventory.service.LOSCustomerOrderService;
import de.linogistix.los.inventory.service.LOSGoodsOutRequestService;
import de.linogistix.los.inventory.service.LOSOrderStrategyService;
import de.linogistix.los.inventory.service.LOSPickingOrderService;
import de.linogistix.los.inventory.service.QueryLotService;
import de.linogistix.los.model.State;
import de.linogistix.los.util.StringTools;
import de.linogistix.los.util.businessservice.ContextService;
import uk.ltd.mediamagic.los.reference.ws.OrderInfo.PickUnitLoad;

/**
 * A Webservice for ordering items from stock
 * 
 * @see de.linogistix.los.inventory.MagicOrder.Order
 * 
 * @author trautm
 *
 */

@Stateless
@SecurityDomain("los-login")
@Remote(MagicOrder.class)
@WebService(endpointInterface = "uk.ltd.mediamagic.los.reference.ws.MagicOrder")
@SOAPBinding(style = SOAPBinding.Style.RPC, use = SOAPBinding.Use.LITERAL)
@WebContext(contextRoot = "/automagic-mywms",  authMethod="BASIC", transportGuarantee="NONE", secureWSDLAccess=true)
@PermitAll
public class MagicOrderBean extends BasicFacadeBean implements MagicOrder {

    Logger log = Logger.getLogger(MagicOrderBean.class);
   
    @EJB LOSOrderFacade orderFacade;
	@EJB LOSCustomerOrderService orderService;
	@EJB
	LOSGoodsOutRequestService goodsOutService;
	@EJB
	private LOSPickingPosGenerator pickingPosGenerator;
	@EJB
	private LOSPickingStockService pickingStockService;
	@EJB
	private ContextService contextService;
	@EJB
	private ClientService clientService;
	@EJB
	private LOSOrderStrategyService orderStratService;
	@EJB
	private ItemDataService itemService;
	@EJB
	private QueryLotService lotService;
	@EJB
	private LOSOrderBusiness pickingBusiness;
	@EJB
	LOSPickingOrderService pickingService;
	@EJB
	UserService userService;

	@PersistenceContext(unitName = "myWMS")
    protected EntityManager manager;


  @WebMethod
  @Deprecated
  public String createAndStartOrder1 (
          @WebParam(name = "clientRef") String clientRef,     
          @WebParam(name = "orderRef") String orderRef,       
          @WebParam(name = "positions") OrderPositionTO[] positions,  
          @WebParam(name = "documentUrl") String documentUrl,
          @WebParam(name = "labelUrl") String labelUrl,  
          @WebParam(name = "destination") String destination) 
  throws FacadeException {
  	log.info("order");
  	LOSCustomerOrder order = orderFacade.order(clientRef, orderRef, positions, documentUrl, labelUrl, destination, null, new Date(), LOSCustomerOrder.PRIO_DEFAULT, true, true, null);
  	return (order != null) ? order.getNumber() : null;
  }

  @Deprecated
  @WebMethod
  public String createOrder1 (
          @WebParam(name = "clientRef") String clientRef,     
          @WebParam(name = "orderRef") String orderRef,       
          @WebParam(name = "positions") OrderPositionTO[] positions,  
          @WebParam(name = "documentUrl") String documentUrl,
          @WebParam(name = "labelUrl") String labelUrl,  
          @WebParam(name = "destination") String destination) 
  throws FacadeException {
  	log.info("order");
  	LOSCustomerOrder order = orderFacade.order(clientRef, orderRef, positions, documentUrl, labelUrl, destination, null, new Date(), LOSCustomerOrder.PRIO_DEFAULT, false, true, null);
  	return (order != null) ? order.getNumber() : null;
  }

	
    /* (non-Javadoc)
     * @see de.linogistix.los.inventory.connector.OrderRemote#order(java.lang.String, java.lang.String, java.lang.String[], byte[], byte[])
     */
    @WebMethod
    public String createAndStartOrder (
            @WebParam(name = "clientRef") String clientRef,     
            @WebParam(name = "orderRef") String orderRef,       
      			@WebParam(name = "customerNumber") String customerNumber,
      			@WebParam(name = "customerName") String customerName,
            @WebParam(name = "positions") OrderPositionTO[] positions,  
            @WebParam(name = "documentUrl") String documentUrl,
            @WebParam(name = "labelUrl") String labelUrl,  
            @WebParam(name = "destination") String destination) 
    throws FacadeException {
    	log.info("order");
    	LOSCustomerOrder order = orderFacade.order(clientRef, orderRef, positions, documentUrl, labelUrl, destination, null, new Date(), LOSCustomerOrder.PRIO_DEFAULT, true, true, null);
    	order.setCustomerNumber(customerNumber);
    	order.setCustomerName(customerName);
    	return (order != null) ? order.getNumber() : null;
    }

    /* (non-Javadoc)
     * @see de.linogistix.los.inventory.connector.OrderRemote#order(java.lang.String, java.lang.String, java.lang.String[], byte[], byte[])
     */
    @WebMethod
    public String createOrder (
            @WebParam(name = "clientRef") String clientRef,     
            @WebParam(name = "orderRef") String orderRef,       
      			@WebParam(name = "customerNumber") String customerNumber,
      			@WebParam(name = "customerName") String customerName,
            @WebParam(name = "positions") OrderPositionTO[] positions,  
            @WebParam(name = "documentUrl") String documentUrl,
            @WebParam(name = "labelUrl") String labelUrl,  
            @WebParam(name = "destination") String destination) 
    throws FacadeException {
    	log.info("order");
    	LOSCustomerOrder order = orderFacade.order(clientRef, orderRef, positions, documentUrl, labelUrl, destination, null, new Date(), LOSCustomerOrder.PRIO_DEFAULT, false, true, null);
    	order.setCustomerNumber(customerNumber);
    	order.setCustomerName(customerName);
    	return (order != null) ? order.getNumber() : null;
    }
    
    @WebMethod
    public List<OrderPositionTO> pickedPositions(@WebParam( name="orderNumber") String orderNumber) throws FacadeException {
    	LOSCustomerOrder order = orderService.getByNumber(orderNumber);
    	if (order == null) {
			throw new InventoryException(InventoryExceptionKey.NO_PICKREQUEST, "The order " + orderNumber + " cannot be found.");    		    		
    	}
    	if (order.getState() == State.CANCELED) {
			throw new InventoryException(InventoryExceptionKey.ORDER_ALREADY_FINISHED, "The order has been cancelled.");    		
    	}
    	if (order.getState() < State.PICKED) {
			throw new InventoryException(InventoryExceptionKey.ORDER_NOT_FINISHED, "The order has not been finished yet.");    		
    	}
    	List<LOSCustomerOrderPosition> ps = order.getPositions();
    	List<OrderPositionTO> out = new ArrayList<>();
    	for(LOSCustomerOrderPosition p : ps) {
    		if (p.getState() < State.PICKED) {
    			throw new InventoryException(InventoryExceptionKey.CUSTOM_TEXT, "Order position not finished: " + p.getItemData().getNumber() + " " + p.getItemData().getName());
    		}
//    		else if (p.getState() == State.CANCELED) {
//    			OrderPositionTO pos = new OrderPositionTO(p.getClient().getName(), p.getLot().getName(), p.getItemData().getNumber(), BigDecimal.ZERO);
//    			out.add(pos);
//    		}
    		else {
    			String lotName = (p.getLot() == null) ? null : p.getLot().getName();
    			OrderPositionTO pos = new OrderPositionTO(p.getClient().getName(), lotName, p.getItemData().getNumber(), p.getAmountPicked());
    			out.add(pos);
    		}
    	}
    	
    	return out;
    }    
    
    @WebMethod
    public OrderInfo getOrderInfo(@WebParam( name="orderNumber") String orderNumber) throws FacadeException {
    	if (StringTools.isEmpty(orderNumber)) return new OrderInfo();
    	LOSCustomerOrder order = orderService.getByNumber(orderNumber);
    	if (order == null) return new OrderInfo();
    	return getOrderInfo(order, true);
    }

    private OrderInfo getOrderInfo(LOSCustomerOrder order, boolean full) throws FacadeException {
    	if (order == null) return null;;
    	int pickedCount = 0;
    	
   		List<LOSCustomerOrderPosition> ps = order.getPositions();
   		for(LOSCustomerOrderPosition p : ps) {
   			if (p.getState() >= State.PICKED) {
   				pickedCount ++;
   			}
   		}
     	    	
    	OrderInfo oi = new OrderInfo(order.getNumber(), order.getPositions().size(), pickedCount);
    	oi.setPriority(order.getPrio());
    	oi.setState(order.getState());

    	if (full) {
    		List<PickUnitLoad> unitLoads = new ArrayList<>();
    		List<LOSPickingOrder> pos = pickingService.getByCustomerOrder(order);
    		for (LOSPickingOrder po : pos) {
    			for (LOSPickingUnitLoad ul : po.getUnitLoads()) {
    				String unitLoad = ul.getUnitLoad().getLabelId();
    				int state = po.getState();
    				String stateStr = (state <= State.PICKED) ? "Picked" : "Finished";
    				String pickedBy = (po.getOperator() == null) ? "Unknown" : po.getOperator().toUniqueString(); 
    				unitLoads.add(new PickUnitLoad(unitLoad, pickedBy, stateStr));    			
    			}
    			po.getUnitLoads();
    		}
    		
    		List<LOSGoodsOutRequest> goodsOutRequests = goodsOutService.getByCustomerOrder(order);    	
    		List<String> goodsOutNumbers = new ArrayList<>();
    		for (LOSGoodsOutRequest go : goodsOutRequests) {
    			goodsOutNumbers.add(go.getNumber());
    		}

    		oi.setUnitLoads(unitLoads);
        	oi.setGoodsOutNumbers(goodsOutNumbers);
    	}
    	
    	return oi;
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<OrderInfo> getOpenOrders() throws FacadeException {
    	
		StringBuilder b = new StringBuilder();	
		b.append("SELECT co FROM ");
		b.append(LOSCustomerOrder.class.getSimpleName()).append(" co ");
		b.append(" JOIN co.positions");
		b.append(" WHERE co.state < :statefinished ");
	
		Query query = manager.createQuery(b.toString());
		query.setParameter("statefinished", State.FINISHED);

		try{
			ArrayList<OrderInfo> out = new ArrayList<>();
			List<LOSCustomerOrder> list = query.getResultList();
			for(LOSCustomerOrder o : list) {
				OrderInfo oi = getOrderInfo(o, false);
				if (oi != null) out.add(oi);
			}
			return out;
		} 
		catch (NoResultException e) {
			return Collections.emptyList();
		}
    }

    @Override
    public void reserveOrder(String orderNumber, String userName) throws FacadeException {
    	final String logStr = "reserveOrder "; 
    	List<LOSPickingOrder> pickOrders = pickingService.getByCustomerOrderNumber(orderNumber);
    	for(LOSPickingOrder p : pickOrders) {
    		if (p.getState() >= State.STARTED) {
    			log.error(logStr + "Order is already started. => Cannot reserve.");
    			throw new InventoryException(InventoryExceptionKey.PICK_ALREADY_STARTED, p.getNumber());
    		}
    	}
    	
    	User user;
		try {
			user = userService.getByUsername(userName);
		} catch (EntityNotFoundException e) {
			log.error(logStr + "User " + userName + " cannot be found.");
			throw new FacadeException("User " + userName + " cannot be found", e.getKey(), new Object[] {userName});
		}
    	
    	for(LOSPickingOrder p : pickOrders) {
    		pickingBusiness.reservePickingOrder(p, user, false);
    	}
    }
    
    @Override
    public void cancelOrder(String orderNumber) throws FacadeException {
		LOSCustomerOrder order = orderService.getByNumber(orderNumber);
		if( order == null ) {
			String msg = "Customer order does not exist. number="+orderNumber;
			log.error("cancelOrder "+msg);
			throw new InventoryException(InventoryExceptionKey.CUSTOM_TEXT, msg);
		}
		orderFacade.removeOrder(order.getId());
		//order.addAdditionalContent("Order cancelled");
    }

    @Override
    public void setOrderPriority(String orderNumber, int prio) throws FacadeException {
		LOSCustomerOrder order = orderService.getByNumber(orderNumber);
		if( order == null ) {
			String msg = "Customer order does not exist. number="+orderNumber;
			log.error("setOrderPriority "+msg);
			throw new InventoryException(InventoryExceptionKey.CUSTOM_TEXT, msg);
		}
		orderFacade.changeOrderPrio(order.getId(), prio);
		
    }

    @Override
    public List<String> getGoodsOutLocations() throws FacadeException {
    	return orderFacade.getGoodsOutLocations();
    }
    
		public List<LotTraceTO> traceLot(String clientName, String itemNumber, String lotName) throws FacadeException, EntityNotFoundException {
    	Client client;
    	if (clientName == null || clientName.length() == 0) {
    		client = getCallersClient();
    	}
    	else {
    		client = clientService.getByName(clientName);
    	}
    	ItemData itemData = itemService.getByItemNumber(client, itemNumber);
    	Lot lot = lotService.getByNameAndItemData(lotName, itemData);
    	if (lot == null) throw new InventoryException(InventoryExceptionKey.NO_LOT_WITH_NAME, lotName);

    	StringBuilder b = new StringBuilder();
  		b.append("SELECT NEW ").append(LotTraceTO.class.getName()).append("(pp)")
  			.append(" FROM ")
  			.append(LOSPickingPosition.class.getSimpleName()).append(" pp")
  			.append(" JOIN pp.customerOrderPosition")
  			.append(" JOIN pp.customerOrderPosition.order")
  			.append(" JOIN pp.pickingOrder")
  			.append(" JOIN pp.lotPicked")
  			.append(" JOIN pp.pickingOrder.operator")
  			.append(" JOIN pp.pickToUnitLoad.unitLoad")
  			.append(" WHERE pp.client = :client AND pp.lotPicked=:lot AND pp.itemData=:itemData");

  		
  		Objects.requireNonNull(manager);
  		TypedQuery<LotTraceTO> q = manager.createQuery(new String(b), LotTraceTO.class);
  		q.setParameter("client", client);
  		q.setParameter("lot", lot);
  		q.setParameter("itemData", itemData);
  		
 			return (List<LotTraceTO>) q.getResultList();
    }
}
