/*
 * UserCRUDBean.java
 *
 * Created on 20.02.2007, 18:37:29
 *
 * Copyright (c) 2006/2007 LinogistiX GmbH. All rights reserved.
 *
 * <a href="http://www.linogistix.com/">browse for licence information</a>
 *
 */

package de.linogistix.los.crud;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.mywms.facade.FacadeException;
import org.mywms.model.ClearingItem;
import org.mywms.model.ClearingItemOption;
import org.mywms.service.BasicService;
import org.mywms.service.ClearingItemService;

import de.linogistix.los.query.exception.BusinessObjectNotFoundException;
import de.linogistix.los.runtime.BusinessObjectSecurityException;


/**
 * @author trautm
 *
 */
@Stateless
public class ClearingItemCRUDBean extends BusinessObjectCRUDBean<ClearingItem> implements ClearingItemCRUDRemote {

	@EJB 
	ClearingItemService service;
	
	@Override
	protected BasicService<ClearingItem> getBasicService() {
		return service;
	}
        
	public void setClearingSolution(ClearingItem ci, ClearingItemOption solution) {        
        try {
//            ci.setSolution(ci.getSolver(), ci.getSolution());
            update(ci);
        } catch (BusinessObjectNotFoundException ex) {
//            Logger.getLogger(ClearingItemCRUDBean.class.getName()).log(Level.SEVERE, null, ex);
//Clearing konnte nicht gefunden werden. Es wird als erledigt angesehen.            
        } catch (BusinessObjectModifiedException ex) {
//            Logger.getLogger(ClearingItemCRUDBean.class.getName()).log(Level.SEVERE, null, ex);
//Schief gelaufen. Nochmal probieren ?            
        } catch (BusinessObjectMergeException ex) {
//            Logger.getLogger(ClearingItemCRUDBean.class.getName()).log(Level.SEVERE, null, ex);
//Schief gelaufen. Nochmal probieren ?            
        } catch (BusinessObjectSecurityException ex) {
//            Logger.getLogger(ClearingItemCRUDBean.class.getName()).log(Level.SEVERE, null, ex);
//Sie duerfen
        } catch (FacadeException e) {
//
		}

	}
        
}
