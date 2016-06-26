/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.location.businessservice;

import javax.ejb.Local;
import javax.ejb.Timer;

import org.mywms.facade.FacadeException;

import de.linogistix.los.location.model.LOSStorageLocation;
import de.linogistix.los.location.model.LOSUnitLoad;

@Local
public interface LocationSanityBusiness {
	
	String CLEARING_EMPTYUL_SOURCEREF = "CLEARING_EMPTYUL_SOURCEREF";
	
	String CLEARING_EMPTYUL_SENDNIRWANA = "CLEARING_EMPTYUL_SENDNIRWANA";
	
	String CLEARING_EMPTYUL_CANCEL = "CLEARING_EMPTYUL_CANCEL";
	
	String CLEARING_EMPTYUL_MSG = "CLEARING_EMPTYUL_MSG";
	
	String CLEARING_EMPTYUL_SHORT = "CLEARING_EMPTYUL_SHORT";
	
	String CLEARING_EMPTYUL_PARAMUL = "CLEARING_EMPTYUL_PARAMUL";
	
	String CLEARING_CAPACITYEXH_SOURCEREF = "CLEARING_CAPACITYEXH_SOURCEREF";

	String CLEARING_CAPACITYEXH_SENDNIRWANA = "CLEARING_CAPACITYEXH_SENDNIRWANA";
	
	String CLEARING_CAPACITYEXH_ULLIST = "CLEARING_CAPACITYEXH_ULLIST";
	
	String CLEARING_CAPACITYEXH_CANCEL = "CLEARING_CAPACITYEXH_CANCEL";
	
	String CLEARING_CAPACITYEXH_MSG = "CLEARING_CAPACITYEXH_MSG";
	
	String CLEARING_CAPACITYEXH_SHORT = "CLEARING_CAPACITYEXH_SHORT";
	
	String CLEARING_CAPACITYEXH_PARAMSL = "CLEARING_CAPACITYEXH_PARAMSL";

	String TIME_OUT_INFO = "TIME_OUT_INFO";

	String TIME_OUT_KEY = "TIME_OUT_KEY";
	
	//-------------------------------------------------------------------
	
	void sanityCheck() throws FacadeException;
	
	void sanityUnitLoadsInStore() throws FacadeException;

	void clearEmptyUnitLoad(LOSUnitLoad ul) ;

	void clearCapacityExhausted(LOSStorageLocation sl);
	
	//---------------------------------------------------------------
	
	void createCronJob();
	
	void cancelCronJob() ;
	
	Timer getTimer() ;
	
	String statusCronJob();
	
	public void timeout(Timer timer) ;
	
}
