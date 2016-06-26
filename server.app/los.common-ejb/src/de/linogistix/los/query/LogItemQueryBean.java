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

import javax.ejb.Stateless;
import javax.persistence.Query;

import org.mywms.globals.LogItemType;
import org.mywms.model.Client;
import org.mywms.model.LogItem;

/**
 *
 * @author <a href="http://community.mywms.de/developer.jsp">Andreas Trautmann</a>
 */
@Stateless

//public class LogItemQueryBean extends BusinessObjectQueryBean<Client> implements LogItemQueryRemote{
public class LogItemQueryBean extends BusinessObjectQueryBean<LogItem> implements LogItemQueryRemote {

    @SuppressWarnings("unchecked")
	public List<LogItem> queryRecent(int amount, List<LogItemType> typeList) {
        //Instead of table name you can use the Entityname which mapped on this table.
        //Don't use real tablenames. Use Entity column names which mapped to the real tablenames instead. 
        Query query;
        Client c;
        c = getCallersUser().getClient();       
        if (typeList.size() == 2) {
            if (c.isSystemClient()) {
                query = manager.createQuery(
                        "SELECT li FROM " + LogItem.class.getSimpleName() + " li " +
                        " WHERE li.type=:t1 OR li.type=:t2" +
                        " ORDER BY li.created DESC");
            } else {
                query = manager.createQuery(
                        "SELECT li FROM " + LogItem.class.getSimpleName() + " li " +
                        " WHERE li.type=:t1 OR li.type=:t2" +
                        " AND li.client=:client" +
                        " ORDER BY li.created DESC");
                query.setParameter("client", c);
            }
            query.setParameter("t1", typeList.get(0));
            query.setParameter("t2", typeList.get(1));
            query.setMaxResults(amount);
            return query.getResultList();                        
        } else if (typeList.size() == 1) {
            if (c.isSystemClient()) {
                query = manager.createQuery(
                        "SELECT li FROM " + LogItem.class.getSimpleName() + " li " +
                        " WHERE li.type=:t" +
                        " ORDER BY li.created DESC");
            } else {
                query = manager.createQuery(
                        "SELECT li FROM " + LogItem.class.getSimpleName() + " li " +
                        " WHERE li.type=:t" +
                        " AND li.client=:client" +
                        " ORDER BY li.created DESC");
                query.setParameter("client", c);
            }
            query.setParameter("t", typeList.get(0));
            query.setMaxResults(amount);
            return query.getResultList();            
        }
        return new ArrayList();
    }

  @Override
  public String getUniqueNameProp() {
    return "id";
  }

    
}
