/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.util.entityservice;

import javax.ejb.Local;

import org.mywms.model.Client;
import org.mywms.service.BasicService;
import org.mywms.service.EntityNotFoundException;

import de.linogistix.los.model.LOSServiceProperty;

@Local
public interface LOSServicePropertyService extends BasicService<LOSServiceProperty>{

	LOSServiceProperty get(Class<?> service, Client client, String key, String ... subkey) throws EntityNotFoundException;

	LOSServiceProperty create(Class<?> service, Client client, String key, String value, String ... subkey);
	
	String getValue(Class<?> service, Client client, String key, String ... subkey) throws EntityNotFoundException;
}
