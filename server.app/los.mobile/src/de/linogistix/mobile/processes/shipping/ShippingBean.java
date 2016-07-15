/*
 * Copyright (c) 2006 - 2013 LinogistiX GmbH
 * 
 * www.linogistix.com
 * 
 * Project: myWMS-LOS
*/
package de.linogistix.mobile.processes.shipping;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import org.apache.log4j.Logger;
import org.mywms.facade.FacadeException;

import de.linogistix.los.inventory.facade.LOSGoodsOutFacade;
import de.linogistix.los.inventory.facade.LOSGoodsOutTO;
import de.linogistix.los.inventory.model.LOSCustomerOrder;
import de.linogistix.los.inventory.query.dto.LOSGoodsOutRequestTO;
import de.linogistix.los.inventory.service.LOSCustomerOrderService;
import de.linogistix.los.inventory.service.LOSGoodsOutRequestService;
import de.linogistix.los.inventory.service.dto.GoodsReceiptTO;
import de.linogistix.los.util.StringTools;
import de.linogistix.mobile.common.gui.bean.BasicDialogBean;
import de.linogistix.mobile.common.system.JSFHelper;

/**
 *
 * @author krane
 */
public class ShippingBean extends BasicDialogBean {
	private static final Logger log = Logger.getLogger(ShippingBean.class);

	protected LOSGoodsOutFacade goFacade;
	protected LOSGoodsOutTO currentOrderTO = null;
	protected String inputUnitLoadLabel = "";
	protected String searchOrderList = "";
	protected List<SelectItem> orderList = null; 
	
	public ShippingBean() {
		super();
		goFacade = super.getStateless(LOSGoodsOutFacade.class);
	}
	
	public String getNavigationKey() {
		return ShippingNavigation.SHIPPING_SELECT_ORDER.name();
	}
	
	public String getTitle() {
		return resolve("Shipping");
	}

	/** 
	 * reset all variables to default values
	 */
	protected void init() {
		inputUnitLoadLabel = "";
		searchOrderList = "";
		currentOrderTO = null;
		orderList = null;
	}
	
	private LOSGoodsOutTO findGoodsOutOrder(String searchStr) throws FacadeException {
		LOSGoodsOutTO order = goFacade.load(searchOrderList);
		return order;
	}
	

	public String processSelectOrder() {
		orderList = null;
		if (!StringTools.isEmpty(searchOrderList)) {
    		try {
				currentOrderTO = findGoodsOutOrder(searchOrderList);
				searchOrderList = "";
			} catch (FacadeException e) {
				searchOrderList = "";
				log.error("processSelectOrder While loading order "+searchOrderList,e);
				JSFHelper.getInstance().message(e.getLocalizedMessage(getLocale()));
				return "";
			}
		}
		if( currentOrderTO == null ) {
			JSFHelper.getInstance().message(resolve("MsgOrderNotFound"));
			return "";
		}
		
		try {
			goFacade.start(currentOrderTO.getOrderId());
		} 
		catch (FacadeException e) {
			JSFHelper.getInstance().message(e.getLocalizedMessage(getLocale()));
			return "";
		}
		
		return ShippingNavigation.SHIPPING_SCAN_UNITLOAD.toString();
	}
	

	public String processCancelSelectOrder() {
		init();
		return ShippingNavigation.SHIPPING_BACK_TO_MENU.toString();
	}

	
	public String processShowInfo() {
		return ShippingNavigation.SHIPPING_SHOW_INFO.toString();
	}


	public String processEnterUnitLoad() {
		if( currentOrderTO == null ) {
			JSFHelper.getInstance().message(resolve("MsgOrderNotLoaded"));
			return ShippingNavigation.SHIPPING_SELECT_ORDER.toString();
		}
		
		if( inputUnitLoadLabel == null ) {
			JSFHelper.getInstance().message(resolve("MsgEnterUnitLoad"));
			return ShippingNavigation.SHIPPING_SCAN_UNITLOAD.toString();
		}
		inputUnitLoadLabel = inputUnitLoadLabel.trim();
		if( inputUnitLoadLabel.length() == 0 ) {
			JSFHelper.getInstance().message(resolve("MsgEnterUnitLoad"));
			return ShippingNavigation.SHIPPING_SCAN_UNITLOAD.toString();
		}
		
		try {
			goFacade.finishPosition(inputUnitLoadLabel, currentOrderTO.getOrderId());
		} catch (FacadeException e) {
			JSFHelper.getInstance().message(e.getLocalizedMessage(getLocale()));
			return "";
		}
		
		inputUnitLoadLabel = "";
		
		try {
			currentOrderTO = goFacade.getOrderInfo(currentOrderTO.getOrderId());
			if( currentOrderTO.isFinished() ) {
				goFacade.finish(currentOrderTO.getOrderId());
				return ShippingNavigation.SHIPPING_SHOW_SUMMARY.toString();
			}
		} catch (FacadeException e) {
			JSFHelper.getInstance().message(e.getLocalizedMessage(getLocale()));
			return "";
		}
		
		return ShippingNavigation.SHIPPING_SCAN_UNITLOAD.toString();
	}
	
	public String processCancelUnitLoad() {
		if( currentOrderTO == null ) {
			JSFHelper.getInstance().message(resolve("MsgOrderNotLoaded"));
			return ShippingNavigation.SHIPPING_SELECT_ORDER.toString();
		}
		
		try {
			goFacade.cancel(currentOrderTO.getOrderId());
			return ShippingNavigation.SHIPPING_SHOW_SUMMARY.toString();
		} catch (FacadeException e) {
			JSFHelper.getInstance().message(e.getLocalizedMessage(getLocale()));
			return ShippingNavigation.SHIPPING_SELECT_ORDER.toString();
		}

	}
	
	public String processShowUnitLoad() {
		return ShippingNavigation.SHIPPING_SCAN_UNITLOAD.toString();
	}
	
	
    public List<SelectItem> getOrderList() {
    	
		if( orderList == null ) {
			orderList = new ArrayList<SelectItem>();

			List<LOSGoodsOutRequestTO> outRequestList = goFacade.getRaw();
			if (outRequestList != null && (outRequestList.isEmpty() == false)) {
				for (LOSGoodsOutRequestTO order : outRequestList) {
					String displayname = order.getNumber();
					String customerOrderNumber = order.getCustomerOrderNumber();
					if( !StringTools.isEmpty(customerOrderNumber) ) {
						displayname += " ("+customerOrderNumber+")";
					}
					orderList.add(new SelectItem(order.getNumber(), displayname));
				}
			}
		}

    	
		return orderList;
    }


    public void selectedOrderChanged(ValueChangeEvent vce) {
		currentOrderTO = null;
    	try {
        	String orderNumber = null;
    		orderNumber = (String)vce.getNewValue();
    		log.info("Selected order: " + orderNumber);
    		currentOrderTO = goFacade.load(orderNumber);
    		searchOrderList = "";
    	}
    	catch( Exception e) {
    		log.error(e.getMessage(), e);
    	}
    }
    
	public String processSummaryFinish() {
		init();
		return ShippingNavigation.SHIPPING_SELECT_ORDER.toString();
	}
	
	public String processInfoFinish() {
		return ShippingNavigation.SHIPPING_SCAN_UNITLOAD.toString();
	}
	
	public String getOrderNumber() {
		return currentOrderTO == null ? "" : currentOrderTO.getOrderNumber();
	}
	
	public void setOrderNumber(String orderNumber) {
	}
	
	public String getComment() {
		return currentOrderTO == null ? "" : currentOrderTO.getComment();
	}

	public String getNextUnitLoadLabel() {
		return currentOrderTO == null ? "" : currentOrderTO.getNextUnitLoadLabelId();
	}
	
	public String getNextLocation() {
		return currentOrderTO == null ? "" : currentOrderTO.getNextLocationName();
	}
	
	public String getNumPosDone() {
		return currentOrderTO == null ? "" : Long.valueOf(currentOrderTO.getNumPosDone()).toString();
	}

	public String getNumPosOpen() {
		return currentOrderTO == null ? "" : Long.valueOf(currentOrderTO.getNumPosOpen()).toString();
	}
	
	public String getNumPos() {
		if( currentOrderTO == null ) {
			return "";
		}
		long numPos = currentOrderTO.getNumPosDone() + currentOrderTO.getNumPosOpen(); 
		return Long.valueOf(numPos).toString();
	}

	public String getInputUnitLoadLabel() {
		return inputUnitLoadLabel;
	}

	public void setInputUnitLoadLabel(String inputUnitLoadLabel) {
		this.inputUnitLoadLabel = inputUnitLoadLabel;
	}

	public String getSearchOrderList() {
		return searchOrderList;
	}

	public void setSearchOrderList(String searchOrderList) {
		this.searchOrderList = searchOrderList;
	}

	protected ResourceBundle getResourceBundle() {
		ResourceBundle bundle;
		Locale loc;
		loc = getUIViewRoot().getLocale();
		bundle = ResourceBundle.getBundle("de.linogistix.mobile.processes.shipping.ShippingBundle", loc);
		return bundle;
	}

}

