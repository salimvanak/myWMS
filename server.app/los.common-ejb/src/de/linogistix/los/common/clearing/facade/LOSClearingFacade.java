/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.common.clearing.facade;

import java.util.List;

import javax.ejb.Remote;

import org.mywms.facade.BasicFacade;
import org.mywms.facade.FacadeException;
import org.mywms.model.ClearingItem;
import org.mywms.model.ClearingItemOption;

import de.linogistix.los.common.clearing.exception.LOSClearingException;

@Remote
public interface LOSClearingFacade extends BasicFacade {

	/**
	 * Returns a list of all unresolved ClearingItems associated with callers client,
	 * sorted by creation date descending.
	 * 
	 * @return
	 */
	public List<ClearingItem> getUnresolvedClearingItemList();		
	
	/**
	 * Set the solution for a clearing item.
	 * 
	 * @param ci
	 * @param solution
	 * @throws FacadeException 
	 */
	public void setClearingSolution(ClearingItem ci, ClearingItemOption solution) throws LOSClearingException, FacadeException;
}
