/*
 * StorageLocationQueryBean.java
 *
 * Created on 14. September 2006, 06:53
 *
 * Copyright (c) 2006 LinogistiX GmbH. All rights reserved.
 *
 *<a href="http://www.linogistix.com/">browse for licence information</a>
 *
 */
package de.linogistix.los.query;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.mywms.model.Client;
import org.mywms.service.EntityNotFoundException;

import de.linogistix.los.model.LOSServiceProperty;
import de.linogistix.los.query.dto.LOSServicePropertyTO;
import de.linogistix.los.util.entityservice.LOSServicePropertyService;

/**
 *
 * @author <a href="http://community.mywms.de/developer.jsp">Andreas Trautmann</a>
 */
@Stateless

public class LOSServicePropertyQueryBean extends BusinessObjectQueryBean<LOSServiceProperty> implements LOSServicePropertyQueryRemote {

	@EJB
	LOSServicePropertyService service;
	
	static List<BODTOConstructorProperty> BODTOConstructorProperties = new ArrayList<BODTOConstructorProperty>();
	
	static{
		BODTOConstructorProperties.add(new BODTOConstructorProperty("id", false));
		BODTOConstructorProperties.add(new BODTOConstructorProperty("version", false));
		BODTOConstructorProperties.add(new BODTOConstructorProperty("service", false));
		BODTOConstructorProperties.add(new BODTOConstructorProperty("client.number", false));
		BODTOConstructorProperties.add(new BODTOConstructorProperty("key", false));
		BODTOConstructorProperties.add(new BODTOConstructorProperty("value", false));
		BODTOConstructorProperties.add(new BODTOConstructorProperty("subkey", false));
	}
	
	public LOSServiceProperty get(Class<?> serviceName, Client client, String key,
			String... subkey) throws EntityNotFoundException {
		
		return service.get(serviceName, client, key, subkey);
		
	}

	public String getValue(Class<?> serviceName, Client client, String key,
			String... subkey) throws EntityNotFoundException {
		return service.getValue(serviceName, client, key, subkey);
	}
	
	@Override
	public Class getBODTOClass() {
		return LOSServicePropertyTO.class;
	}
	
	@Override
	protected List<BODTOConstructorProperty> getBODTOConstructorProperties() {
		return BODTOConstructorProperties;
	}

 
}
