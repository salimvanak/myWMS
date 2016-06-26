/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.common.clearing.facade;

import java.util.List;

import javax.annotation.security.PermitAll;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.jboss.logging.Logger;
import org.mywms.facade.BasicFacadeBean;
import org.mywms.facade.FacadeException;
import org.mywms.model.ClearingItem;
import org.mywms.model.ClearingItemOption;
import org.mywms.service.ClearingItemService;
import org.mywms.service.EntityNotFoundException;

import de.linogistix.los.common.clearing.customization.CustomClearingItemDispatcher;
import de.linogistix.los.common.clearing.exception.LOSClearingException;
import de.linogistix.los.common.clearing.exception.LOSClearingExceptionKey;
import de.linogistix.los.query.ClearingItemQueryRemote;

@Stateless
@PermitAll
public class LOSClearingFacadeBean 
					extends BasicFacadeBean 
					implements LOSClearingFacade 
{	
	
	private static final Logger log = Logger.getLogger(LOSClearingFacadeBean.class);
	
	@EJB
	private ClearingItemService clearingItemService;
	
	@EJB
	private CustomClearingItemDispatcher clDispatcher;
	
	@EJB
	private ClearingItemQueryRemote clearingQuery;
	
	@PersistenceContext(unitName = "myWMS")
    protected EntityManager manager;
	
	public List<ClearingItem> getUnresolvedClearingItemList() {
		
		return clearingQuery.getUnresolvedClearingItemList();
	}

	public void setClearingSolution(ClearingItem ci, ClearingItemOption solution) throws LOSClearingException, FacadeException
	{
		
		try {
			ClearingItem clItem = (ClearingItem) clearingItemService.get(ci.getId());
			
			clItem.setSolution(getCallersUsername(), solution);
			
			clDispatcher.dispatchClearingItem(clItem);
			
		} catch (EntityNotFoundException e) {
			log.error(e.getMessage(), e);
			throw new LOSClearingException(
					LOSClearingExceptionKey.NO_CLEARING_ITEM_FOR_ID, 
					new Object[]{ci.getId()});
		} 
	}

}
