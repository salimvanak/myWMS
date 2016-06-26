/*
 * UserCRUDRemote.java
 *
 * Created on 14. September 2006, 06:53
 *
 * Copyright (c) 2006 LinogistiX GmbH. All rights reserved.
 *
 *<a href="http://www.linogistix.com/">browse for licence information</a>
 *
 */

package de.linogistix.los.crud;

import javax.ejb.Remote;

import org.mywms.model.Client;

import de.linogistix.los.model.LOSServiceProperty;

/**
 * CRUD operations for User entities
 * @see  BusinessObjectCRUDRemote
 * 
 * @author trautm
 *
 */
@Remote
public interface LOSServicePropertyCRUDRemote extends BusinessObjectCRUDRemote<LOSServiceProperty>{
   
	LOSServiceProperty create(Class<?> service, Client client, String key, String value, String ... subkey);
		
}
