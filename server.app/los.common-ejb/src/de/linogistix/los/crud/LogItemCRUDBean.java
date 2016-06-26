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

import org.mywms.model.LogItem;
import org.mywms.service.BasicService;
import org.mywms.service.LogItemService;


/**
 * @author trautm
 *
 */
@Stateless
public class LogItemCRUDBean extends BusinessObjectCRUDBean<LogItem> implements LogItemCRUDRemote {

	@EJB 
	LogItemService service;
	
	@Override
	protected BasicService<LogItem> getBasicService() {
		return service;
	}
    
}
