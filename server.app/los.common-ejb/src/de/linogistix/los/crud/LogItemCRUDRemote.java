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

import org.mywms.model.LogItem;

/**
 * CRUD operations for {link LogItem} entities
 * @see  BusinessObjectCRUDRemote
 * 
 * @author trautm
 *
 */
@Remote
public interface LogItemCRUDRemote extends BusinessObjectCRUDRemote<LogItem>{

}
