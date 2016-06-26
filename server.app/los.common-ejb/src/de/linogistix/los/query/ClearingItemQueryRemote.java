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


import java.util.List;
import javax.ejb.Remote;

import org.mywms.model.ClearingItem;


/**
 *
 * @author <a href="http://community.mywms.de/developer.jsp">Andreas Trautmann</a>
 */
@Remote
public interface ClearingItemQueryRemote extends BusinessObjectQueryRemote<ClearingItem>{ 
  
    public List<ClearingItem> getUnresolvedClearingItemList();
    
}
