/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.location.facade;

import javax.ejb.Remote;

import org.mywms.facade.FacadeException;

@Remote
public interface LocationSanityCheckFacade {

	void sanityCheck() throws FacadeException;
	
	void assignDummyTypeToPicklocations() throws FacadeException;

	//-----------------------------------------------------------
	
	public void createCronJob() ;
	
	public void cancelCronJob() throws Exception ;
	
	public String statusCronJob();
}
