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

import org.mywms.model.Client;
import org.mywms.service.BasicService;

import de.linogistix.los.model.LOSServiceProperty;
import de.linogistix.los.util.entityservice.LOSServicePropertyService;

/**
 * @author trautm
 *
 */
@Stateless
public class LOSServicePropertyCRUDBean extends BusinessObjectCRUDBean<LOSServiceProperty> implements LOSServicePropertyCRUDRemote {

    @EJB
    LOSServicePropertyService service;

    @Override
    protected BasicService<LOSServiceProperty> getBasicService() {
        return service;
    }
    
    public LOSServiceProperty create(Class<?> serviceName, Client client,
			String key, String value, String ... sub) {
    	return service.create(serviceName, client, key, value, sub);
		
	}
    

}
