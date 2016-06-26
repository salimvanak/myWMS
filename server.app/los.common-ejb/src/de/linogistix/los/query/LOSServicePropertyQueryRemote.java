/*
 * StorageLocationQueryRemote.java
 *
 * Created on 14. September 2006, 06:59
 *
 * Copyright (c) 2006 LinogistiX GmbH. All rights reserved.
 *
 *<a href="http://www.linogistix.com/">browse for licence information</a>
 *
 */

package de.linogistix.los.query;


import javax.ejb.Remote;

import org.mywms.model.Client;
import org.mywms.service.EntityNotFoundException;

import de.linogistix.los.model.LOSServiceProperty;


/**
 *
 * @author <a href="http://community.mywms.de/developer.jsp">Andreas Trautmann</a>
 */
@Remote
public interface LOSServicePropertyQueryRemote extends BusinessObjectQueryRemote<LOSServiceProperty>{ 
     
	LOSServiceProperty get(Class<?> service, Client client, String key, String ... subkey) throws EntityNotFoundException;
	
	String getValue(Class<?> service, Client client, String key, String ... subkey) throws EntityNotFoundException;

}
