/*
 * Copyright (c) 2008 by Fraunhofer IML, Dortmund.
 * All rights reserved.
 *
 * Project: mywms.as
 */
package org.mywms.service;

import java.io.NotSerializableException;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.Query;

import org.apache.log4j.Logger;
import org.mywms.model.ClearingItem;
import org.mywms.model.ClearingItemOption;
import org.mywms.model.Client;

/**
 * This class implements the service for the entity ClearingItem.
 * 
 * @author Olaf Krause
 * @version $Revision: 597 $ provided by $Author: trautm $
 */
@Stateless
public class ClearingItemServiceBean
    extends BasicServiceBean<ClearingItem>
    implements ClearingItemService
{

    @SuppressWarnings("unused")
    private static final Logger log =
        Logger.getLogger(ClearingItemServiceBean.class.getName());

    public ClearingItem create(
			Client client, String host, String source, String user,
			String messageResourceKey, String shortMessageResourceKey,
			String resourceBundleName, Class bundleResolver, 
			Object[] shortMessageParameters,
			Object[] messageParameters, ArrayList<ClearingItemOption> options)
			throws NotSerializableException 
	{
		ClearingItem clearingItem = new ClearingItem();

        clearingItem.setClient(client);
        clearingItem.setHost(host);
        clearingItem.setSource(source);
        clearingItem.setUser(user);
        clearingItem.setMessageResourceKey(messageResourceKey);
        clearingItem.setShortMessageResourceKey(shortMessageResourceKey);
        clearingItem.setMessageParameters(messageParameters);
        clearingItem.setShortMessageParameters(shortMessageParameters);
        clearingItem.setResourceBundleName(resourceBundleName);
        clearingItem.setBundleResolver(bundleResolver);
        clearingItem.setOptions(options);

        manager.persist(clearingItem);
        manager.flush();

        return clearingItem;
	}

    /**
     * @see org.mywms.service.ClearingItemService#getChronologicalList(java.lang.String,
     *      java.lang.String, java.lang.String, java.lang.String, int)
     */
    @SuppressWarnings("unchecked")
    public List<ClearingItem> getChronologicalList(
        String clientNumber,
        String host,
        String source,
        String user,
        int limit)
    {
        StringBuffer queryStr = new StringBuffer();
        queryStr.append("SELECT ci FROM ").append(
            ClearingItem.class.getSimpleName()).append(" ci ");

        // filter client
        boolean whereClauseUsed = false;
        if (clientNumber != null) {
            queryStr.append(whereClauseUsed ? "AND" : "WHERE");
            whereClauseUsed = true;
            queryStr.append(" ci.client.number=:client ");
        }

        // filter host
        if (host != null) {
            queryStr.append(whereClauseUsed ? "AND" : "WHERE");
            whereClauseUsed = true;
            queryStr.append(" ci.host=:host ");
        }

        // filter source
        if (source != null) {
            queryStr.append(whereClauseUsed ? "AND" : "WHERE");
            whereClauseUsed = true;
            queryStr.append(" ci.source=:source ");
        }

        // filter user
        if (user != null) {
            queryStr.append(whereClauseUsed ? "AND" : "WHERE");
            whereClauseUsed = true;
            queryStr.append(" ci.user=:user ");
        }

        // sort the resultset
        queryStr.append("ORDER BY ci.created DESC, ci.id DESC");

        // create the query
        Query query = manager.createQuery(queryStr.toString());

        if (clientNumber != null) {
            query.setParameter("client", clientNumber);
        }
        if (host != null) {
            query.setParameter("host", host);
        }
        if (source != null) {
            query.setParameter("source", source);
        }
        if (user != null) {
            query.setParameter("user", user);
        }
        if (limit > 0) {
            query.setMaxResults(limit);
        }

        return (List<ClearingItem>) query.getResultList();
    }

    /**
     * @see org.mywms.service.ClearingItemService#getHosts(org.mywms.model.Client)
     */
    @SuppressWarnings("unchecked")
    public List<String> getHosts(Client client) {
        if (client == null) {
            Query query =
                manager.createQuery("SELECT ci.host FROM "
                    + ClearingItem.class.getSimpleName()
                    + " ci "
                    + "GROUP BY ci.host ORDER BY ci.host");

            List<String> list = (List<String>) query.getResultList();

            return list;
        }
        else {
            Query query =
                manager.createQuery("SELECT ci.host FROM "
                    + ClearingItem.class.getSimpleName()
                    + " ci "
                    + " WHERE ci.client=:client "
                    + "GROUP BY ci.host ORDER BY ci.host");

            query.setParameter("client", client);

            List<String> list = (List<String>) query.getResultList();

            return list;
        }
    }

    /**
     * @see org.mywms.service.ClearingItemService#getSources(org.mywms.model.Client)
     */
    @SuppressWarnings("unchecked")
    public List<String> getSources(Client client) {
        if (client == null) {
            Query query =
                manager.createQuery("SELECT ci.source FROM "
                    + ClearingItem.class.getSimpleName()
                    + " ci "
                    + "GROUP BY ci.source ORDER BY ci.source");

            List<String> list = (List<String>) query.getResultList();

            return list;
        }
        else {
            Query query =
                manager.createQuery("SELECT ci.source FROM "
                    + ClearingItem.class.getSimpleName()
                    + " ci "
                    + " WHERE client=:client "
                    + "GROUP BY ci.source ORDER BY ci.source");

            query.setParameter("client", client);
            List<String> list = (List<String>) query.getResultList();

            return list;
        }
    }

    /**
     * @see org.mywms.service.ClearingItemService#getUser(org.mywms.model.Client)
     */
    @SuppressWarnings("unchecked")
    public List<String> getUser(Client client) {
        if (client == null) {
            Query query =
                manager.createQuery("SELECT ci.user FROM "
                    + ClearingItem.class.getSimpleName()
                    + " ci "
                    + "GROUP BY ci.user ORDER BY ci.user");

            List<String> list = (List<String>) query.getResultList();
            return list;
        }
        else {
            Query query =
                manager.createQuery("SELECT ci.user FROM "
                    + ClearingItem.class.getSimpleName()
                    + " ci "
                    + "WHERE ci.client=:client "
                    + "GROUP BY ci.user ORDER BY ci.user");

            query.setParameter("client", client);
            List<String> list = (List<String>) query.getResultList();

            return list;
        }
    }
    public List<ClearingItem> getNondealChronologicalList(
        String client,
        String host,
        String source,
        String user,
        int limit)
    {
        List<ClearingItem> items =
            getChronologicalList(client, host, source, user, limit);
        List<ClearingItem> retList = new ArrayList<ClearingItem>();

        for (int i = 0; i < items.size(); i++) {
            ClearingItem item = items.get(i);
            if (item.getSolution() == null)
                retList.add(item);
        }

        return retList;
    }

}
