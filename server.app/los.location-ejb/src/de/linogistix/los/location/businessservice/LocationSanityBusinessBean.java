/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.location.businessservice;

import java.io.NotSerializableException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.ejb.Timeout;
import javax.ejb.Timer;
import javax.ejb.TimerService;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.jboss.logging.Logger;
import org.mywms.facade.FacadeException;
import org.mywms.model.ClearingItem;
import org.mywms.model.ClearingItemOption;
import org.mywms.model.ClearingItemOptionRetval;
import org.mywms.model.Client;
import org.mywms.model.UnitLoadType;
import org.mywms.model.User;
import org.mywms.service.ClearingItemService;
import org.mywms.service.EntityNotFoundException;

import de.linogistix.los.location.entityservice.LOSAreaService;
import de.linogistix.los.location.entityservice.LOSStorageLocationService;
import de.linogistix.los.location.entityservice.LOSUnitLoadService;
import de.linogistix.los.location.model.LOSArea;
import de.linogistix.los.location.model.LOSStorageLocation;
import de.linogistix.los.location.model.LOSUnitLoad;
import de.linogistix.los.location.query.LOSAreaQueryRemote;
import de.linogistix.los.location.query.LOSStorageLocationQueryRemote;
import de.linogistix.los.location.query.LOSUnitLoadQueryRemote;
import de.linogistix.los.location.query.UnitLoadTypeQueryRemote;
import de.linogistix.los.location.res.BundleResolver;
import de.linogistix.los.location.service.QueryUnitLoadTypeService;
import de.linogistix.los.query.BODTO;
import de.linogistix.los.query.QueryDetail;
import de.linogistix.los.query.TemplateQuery;
import de.linogistix.los.query.TemplateQueryWhereToken;
import de.linogistix.los.util.businessservice.ContextService;
import de.linogistix.los.util.entityservice.LOSServicePropertyService;

@Stateless
public class LocationSanityBusinessBean implements LocationSanityBusiness{
	
	private Logger log = Logger.getLogger(LocationSanityBusinessBean.class);
	
	@EJB
	LOSStorage storage;

	@EJB
	LOSStorageLocationService slService;

	@EJB
	ContextService contextService;

	@EJB
	LOSStorageLocationQueryRemote slQuery;

	@EJB
	LOSUnitLoadQueryRemote uLoadQueryRemote;

	@EJB
	LOSStorageLocationQueryRemote slQueryRemote;
	
	@EJB
	LOSAreaQueryRemote areaQueryRemote;
	
	@EJB
	LOSAreaService areaService;
	
	@EJB
	UnitLoadTypeQueryRemote ulTypeQueryRemote;
	
	@EJB
	LOSUnitLoadService ulService;
	
	@EJB
	QueryUnitLoadTypeService ulTypeService;
	
	@EJB
	ClearingItemService clearingService;
	
	@EJB
	LOSServicePropertyService configService;
	
	@Resource
	SessionContext ctx;
	
	@PersistenceContext(unitName = "myWMS")
	private EntityManager manager;
	
	public void sanityCheck() throws FacadeException{
		sanityUnitLoadsInStore();
	}
	
	public void sanityUnitLoadsInStore() throws FacadeException{
		
		log.info("Starting location sanity check: UnitLoadsInStore");
		
//		List<LOSArea> areas = areaService.getByType(LOSAreaType.STORE);
		List<LOSArea> areas = areaService.getForStorage();
		
		for (LOSArea area : areas){
		
			QueryDetail d = new QueryDetail(0, Integer.MAX_VALUE);
			TemplateQueryWhereToken t = new TemplateQueryWhereToken(TemplateQueryWhereToken.OPERATOR_EQUAL, "area", area);
			TemplateQuery query = new TemplateQuery();
			query.addWhereToken(t);
			query.setBoClass(LOSStorageLocation.class);
			
			List<BODTO<LOSStorageLocation>> sls = slQueryRemote.queryByTemplateHandles(d, query );
			
			for (BODTO<LOSStorageLocation> to : sls){

				UnitLoadType dummy = ulTypeService.getPickLocationUnitLoadType();
			
				LOSStorageLocation sl = manager.find(LOSStorageLocation.class, to.getId());
	
				if ( sl.getCurrentTypeCapacityConstraint() != null &&
						sl.getCurrentTypeCapacityConstraint().getUnitLoadType().equals(dummy)){
					log.debug("IGNORE TYPE " + sl.getCurrentTypeCapacityConstraint().toDescriptiveString());
					continue;
				}
							
				if (sl.getUnitLoads().isEmpty()){
					continue;
				}
				
				List<Long> ids = new ArrayList<Long>();
				
				for (LOSUnitLoad ul : sl.getUnitLoads()){
					ids.add(ul.getId());
				}
				
				for (Long id : ids){
					
					LOSUnitLoad ul ;
					ul = manager.find(LOSUnitLoad.class, id);
					
					if (ul.getType().equals(dummy)){
						log.warn("UNEXPECTED TYPE: " + ul.toDescriptiveString());
						continue;
					}
					
					if (ul.getStockUnitList().size() == 0){
						log.error("FOUND EMPTY UNITLOAD: " + ul.toDescriptiveString());
						clearEmptyUnitLoad(ul);
						continue;
					}
					
				}
			}
		}
	}

	public void clearEmptyUnitLoad(LOSUnitLoad ul) {
		User u = contextService.getCallersUser();
		Client c = ul.getClient();
		
		String host;
		try {
			host = InetAddress.getLocalHost().getHostName();
		} catch (UnknownHostException e) {
			host = "?";
		}
		
		String source = CLEARING_EMPTYUL_SOURCEREF + "_" + ul.getLabelId();

		List<ClearingItem> l = clearingService.getNondealChronologicalList(null, null, source, null, -1);
		
		if (l != null && l.size() > 0){
			// clearing already exists!
			return;
		}
		
		String resourceBundleName = "de.linogistix.los.location.res.Bundle";
		String[] shortMessageParameters = new String[]{ul.getLabelId()};
		String[] messageParameters = new String[]{ul.getLabelId(), ul.getStorageLocation().getName()};
		
		ArrayList<ClearingItemOption> options = new ArrayList<ClearingItemOption>();
		ClearingItemOption sendToNirwana = new ClearingItemOption();
		sendToNirwana.setMessageResourceKey(CLEARING_EMPTYUL_SENDNIRWANA);
		options.add(sendToNirwana);
		
		ClearingItemOption cancel = new ClearingItemOption();
		cancel.setMessageResourceKey(CLEARING_EMPTYUL_CANCEL);
		options.add(cancel);

		try {
			ClearingItem item = clearingService.create(
					c,host, source, u.getName(),
					CLEARING_EMPTYUL_MSG, CLEARING_EMPTYUL_SHORT, resourceBundleName, 
					BundleResolver.class, shortMessageParameters, messageParameters, options);
			item.getPropertyMap().put(CLEARING_EMPTYUL_PARAMUL, ul.getId().toString());
		} catch (NotSerializableException e) {
			log.error(e.getMessage());
		}
		
	}

	public void clearCapacityExhausted(LOSStorageLocation sl) {
		User u = contextService.getCallersUser();
		Client c = sl.getClient();
		
		String host;
		try {
			host = InetAddress.getLocalHost().getHostName();
		} catch (UnknownHostException e) {
			host = "?";
		}
		
		String source = CLEARING_CAPACITYEXH_SOURCEREF + "_" + sl.getName();

		List<ClearingItem> l = clearingService.getNondealChronologicalList(null, null, source, null, -1);
		
		if (l != null && l.size() > 0){
			// clearing already exists!
			return;
		}
		
		String resourceBundleName = "de.linogistix.los.location.res.Bundle";
		String[] shortMessageParameters = new String[]{sl.getName()};
		String[] messageParameters = new String[]{sl.getName()};
		
		ArrayList<ClearingItemOption> options = new ArrayList<ClearingItemOption>();
		
		ClearingItemOption sendToNirwana = new ClearingItemOption();
		sendToNirwana.setMessageResourceKey(CLEARING_CAPACITYEXH_SENDNIRWANA);
		
		ClearingItemOptionRetval retval = new ClearingItemOptionRetval();
		String[] uls = new String[sl.getUnitLoads().size()];
		int i=0;
		for (LOSUnitLoad ul : sl.getUnitLoads()){
			uls[i++] = ul.getLabelId();
		}
		retval.setNameResourceKey(CLEARING_CAPACITYEXH_ULLIST);
		retval.setType(String[].class);
		
		try {
			retval.setRetval(uls);
			ArrayList<ClearingItemOptionRetval> retvals = new ArrayList<ClearingItemOptionRetval>();
			retvals.add(retval);
			sendToNirwana.setRetvals(retvals);
			options.add(sendToNirwana);
		} catch (NotSerializableException e1) {
			log.error(e1.getMessage(), e1);
		}

		ClearingItemOption cancel = new ClearingItemOption();
		cancel.setMessageResourceKey(CLEARING_CAPACITYEXH_CANCEL);
		options.add(cancel);

		try {
			ClearingItem item = clearingService.create(
					c,host, source, u.getName(),
					CLEARING_CAPACITYEXH_MSG, CLEARING_CAPACITYEXH_SHORT, resourceBundleName, 
					BundleResolver.class, shortMessageParameters, messageParameters, options);
			item.getPropertyMap().put(CLEARING_CAPACITYEXH_PARAMSL, sl.getId().toString());
		} catch (NotSerializableException e) {
			log.error(e.getMessage());
		}
		
	}
	
	//---------------------------------------------------------------
	
	public void createCronJob() {
		
		Long intervall = 10 * 1000L;

		String value;
		try {
			value = configService
					.getValue(LocationSanityBusiness.class, contextService.getCallersUser()
							.getClient(), LocationSanityBusiness.TIME_OUT_KEY);
			if (value != null) {
				try {
					intervall = Long.parseLong(value);
				} catch (NumberFormatException ex) {
					log.warn(ex.getMessage());
					intervall = 10 * 1000L;
				}
			}
		} catch (EntityNotFoundException e) {
			log.error(e.getMessage(), e);
		}

		if (getTimer() != null){
			cancelCronJob();
		}
		TimerService timerService = ctx.getTimerService();
	    timerService.createTimer(new Date(System.currentTimeMillis() + intervall), intervall,
	    		LocationSanityBusiness.TIME_OUT_INFO);

	}

	@SuppressWarnings("unchecked")
	public void cancelCronJob() {
		TimerService timerService = ctx.getTimerService();
		for (Timer timer : (Collection<Timer>) timerService.getTimers()) {
			if (timer.getInfo() instanceof String) {
				if (((String) timer.getInfo())
						.equals(LocationSanityBusiness.TIME_OUT_INFO)) {
					timer.cancel();
					log.info("Cancel timer: " + timer.toString());
					return;
				}
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	public Timer getTimer() {
		TimerService timerService = ctx.getTimerService();
		for (Timer timer : (Collection<Timer>) timerService.getTimers()) {
			if (timer.getInfo() instanceof String) {
				if (((String) timer.getInfo())
						.equals(LocationSanityBusiness.TIME_OUT_INFO)) {
					log.info(" " + timer.toString());
					return timer;
				}
			}
		}
		
		return null;
	}
	
	public String statusCronJob() {
		String stat = null;
		Timer timer = getTimer();
		if (timer != null){
			log.info(" " + timer.toString());
			stat = "Running. Remaining: " + timer.getTimeRemaining();
			return stat;
		}
		
		return "Not running";
	}

	@Timeout
	public void timeout(Timer timer) {
		try {
			log.info("create replenishment if needed");
			sanityCheck();
		} catch (Throwable e) {
			log.error(e.getMessage(), e);
		}
	}
	

}
