/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.inventory.ws;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.annotation.security.PermitAll;
import javax.ejb.EJB;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.ejb.Timer;
import javax.ejb.TimerService;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.log4j.Logger;
//dgrys comment - aenderung portierung wildfly
import org.jboss.ws.api.annotation.WebContext;
import org.mywms.model.ItemData;
import org.mywms.model.Lot;
import org.mywms.service.EntityNotFoundException;

import de.linogistix.los.inventory.query.ItemDataQueryRemote;
import de.linogistix.los.inventory.query.LotQueryRemote;
import de.linogistix.los.query.QueryDetail;
import de.linogistix.los.query.exception.BusinessObjectQueryException;
import de.linogistix.los.util.businessservice.ContextService;
import de.linogistix.los.util.entityservice.LOSServicePropertyService;

@Stateless 
@Remote(ERPQueryInventory.class)
@WebService
@SOAPBinding(style = SOAPBinding.Style.RPC, use = SOAPBinding.Use.LITERAL)
@WebContext(contextRoot = "/webservice", authMethod = "BASIC", transportGuarantee = "NONE", secureWSDLAccess = true)
//dgrys portierung wildfly 8.2, workaround to call web service, required to log in
@PermitAll
public class ERPQueryInventoryBean implements ERPQueryInventory {

	private static final Logger log = Logger.getLogger(ERPQueryInventoryBean.class);
	@EJB
	ContextService context;

	@EJB
	LOSServicePropertyService configService;

	@Resource
    TimerService timerService;

	@EJB
	ItemDataQueryRemote itemQuery;
	
	@EJB
	LotQueryRemote lotQuery;
	
	@PersistenceContext(unitName = "myWMS")
	protected EntityManager manager;
	
//	private static final String PROXY_URL = "http://dev-lager/soap/index.php?wsdl";
	
//	@WebServiceRef(wsdlLocation=PROXY_URL)
	ERPQueryInventory proxy;
	
	public ERPBatchDataTO[] getBatchData(
			@WebParam(name="clientref") String clientref, 
			@WebParam(name="batchRef") String batchRef) {
		
		return proxy.getBatchData(clientref, batchRef);
		
	}
	
	public ERPItemDataTO[] getItemData(
			@WebParam(name="clientref") String clientref,
			@WebParam(name="articleRef") String articleRef) {
		
		return proxy.getItemData(clientref, articleRef);
	}
	
	public void createCronJob() {
		
		Long intervall = 3600 * 1000L; 

		String value;
		try {
			value = configService
					.getValue(ERPQueryInventory.class, context.getCallersUser()
							.getClient(), TIME_OUT_KEY);
			if (value != null) {
				try {
					intervall = Long.parseLong(value);
				} catch (NumberFormatException ex) {
					log.warn(ex.getMessage());
					intervall = 3600 * 1000L;
				}
			}
		} catch (EntityNotFoundException e) {
			// TODO Auto-generated catch block
			log.error(e.getMessage(), e);
		}

		cancelCronJob();

		timerService.createTimer(new Date(), intervall,	ERPQueryInventory.TIME_OUT_INFO);

	}

	@SuppressWarnings("unchecked")
	public void cancelCronJob() {
		for (Timer timer : (Collection<Timer>) timerService.getTimers()) {
			if (timer.getInfo() instanceof String) {
				if (((String) timer.getInfo())
						.equals(TIME_OUT_INFO)) {
					timer.cancel();
					log.info("Cancel timer: " + timer.toString());
					return;
				}
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	public String statusCronJob() {
		String stat = null;
		for (Timer timer : (Collection<Timer>) timerService.getTimers()) {
			if (timer.getInfo() instanceof String) {
				if (((String) timer.getInfo())
						.equals(ERPQueryInventory.TIME_OUT_INFO)) {
					log.info(" " + timer.toString());
					stat = "Running. Remaining: " + timer.getTimeRemaining();
					return stat;
				}
			}
		}
		
		return "Not running";
	}

//	@Timeout
//	@XmlTransient
//	public void timeout(Timer timer) {
//		try {
//			
//			log.info("Timeout: " + timer.getInfo());
//			updateItemData();
//			
//		} catch (Throwable e) {
//			log.error(e.getMessage(), e);
//		}
//	}

	public void update(){
		updateItemData();
		updateLot();
	}
	public void updateItemData() {
		
		List<ItemData> items;
		try {
			items = itemQuery.queryAll(new QueryDetail(0,Integer.MAX_VALUE));
		} catch (BusinessObjectQueryException e) {
			log.error(e.getMessage());
			return;
		}
		int i = 0;
		
		for (ItemData idat : items){
			ERPItemDataTO[] itos = getItemData(idat.getClient().getNumber(), idat.getNumber());
			for (ERPItemDataTO to : itos){
				i++;
				idat = manager.find(ItemData.class, idat.getId());
				idat.setDescription(to.description);
				idat.setSafetyStock(to.minimumStock);
				if (i % 30 == 0){
					manager.flush();
					manager.clear();
				}
			}
		}
		
	}
	

	public void updateLot() {
		
		List<Lot> lots;
		try {
			lots = lotQuery.queryAll(new QueryDetail(0,Integer.MAX_VALUE));
		} catch (BusinessObjectQueryException e) {
			log.error(e.getMessage());
			return;
		}
		int i = 0;
		
		for (Lot lot : lots){
			ERPBatchDataTO[] itos = getBatchData(lot.getClient().getNumber(), lot.getName());
			for (ERPBatchDataTO to : itos){
				i++;
				lot = manager.find(Lot.class,  lot.getId());
				lot.setAdditionalContent(to.description);
				if (i % 30 == 0){
					manager.flush();
					manager.clear();
				}
			}
		}
	}

}
