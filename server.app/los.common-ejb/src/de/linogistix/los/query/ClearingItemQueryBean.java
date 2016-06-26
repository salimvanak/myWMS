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

import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.mywms.model.ClearingItem;
import org.mywms.model.Client;

/**
 *
 * @author <a href="http://community.mywms.de/developer.jsp">Andreas Trautmann</a>
 */
@Stateless
public class ClearingItemQueryBean extends BusinessObjectQueryBean<ClearingItem> implements ClearingItemQueryRemote {

    @PersistenceContext(unitName = "myWMS")
    protected EntityManager manager;

    @SuppressWarnings("unchecked")
    public List<ClearingItem> getUnresolvedClearingItemList() {

    	Client client = getCallersUser().getClient();
    	
		StringBuffer b = new StringBuffer();
		b.append("SELECT ci FROM "
				+ ClearingItem.class.getSimpleName()
				+ " ci "
				+ " WHERE ci.solution IS NULL ");
		
		if ( ! client.isSystemClient()){
			b.append(" AND ci.client=:cl ");
		}
		
		b.append(" ORDER BY ci.created");
		
		Query query = manager.createQuery(new String(b));
		
		if ( ! client.isSystemClient()){
			query.setParameter("cl", client);
		}
		
		return query.getResultList();
    }
}
