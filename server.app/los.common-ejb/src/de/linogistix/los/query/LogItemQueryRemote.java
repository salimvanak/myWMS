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

import org.mywms.globals.LogItemType;
import org.mywms.model.LogItem;

@Remote
public interface LogItemQueryRemote extends BusinessObjectQueryRemote<LogItem> {

    /**
     * Returns a List of {@link LogItem}.
     * 
     * The <code>amount</code> latest LogItems are delivered.  
     * 
     * @param amount LogItems amount to deliver
     * @param typeList LogItems type (LOG, ERROR)
     * @return
     */
    public List<LogItem> queryRecent(int amount, List<LogItemType> typeList);
}
