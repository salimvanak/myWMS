/*
 * Copyright (c) 2006 by Fraunhofer IML, Dortmund.
 * All rights reserved.
 *
 * Project: myWMS
 */
package org.mywms.service;

import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.Query;

import org.mywms.globals.LogItemType;
import org.mywms.model.Client;
import org.mywms.model.LogItem;

/**
 * This class implements the service for the entity LogItem.
 * 
 * @author Olaf Krause
 * @version $Revision: 674 $ provided by $Author: mkrane $
 */
@Stateless
public class LogItemServiceBean
    extends BasicServiceBean<LogItem>
    implements LogItemService
{

    /**
     * @see org.mywms.service.LogItemService#create(Client, String,
     *      String, String, LogItemType, String, String)
     */
    public LogItem create(
        Client client,
        String host,
        String source,
        String user,
        LogItemType type,
        String message,
        String messageResourceKey)
    {
        LogItem logItem = new LogItem();

        logItem.setClient(client);
        logItem.setHost(host);
        logItem.setSource(source);
        logItem.setUser(user);
        logItem.setType(type);
        logItem.setMessage(message);
        logItem.setMessageResourceKey(messageResourceKey);

        manager.persist(logItem);
        manager.flush();

        return logItem;
    }

    /**
     * @see org.mywms.service.LogItemService#create(org.mywms.model.Client,
     *      java.lang.String, java.lang.String, java.lang.String,
     *      org.mywms.globals.LogItemType, java.lang.String,
     *      java.lang.String, java.lang.String)
     */
    public LogItem create(
        Client client,
        String host,
        String source,
        String user,
        LogItemType type,
        String resourceBundleName,
        String message,
        String messageResourceKey,
        Object[] messageParameters)
    {
        LogItem logItem = new LogItem();

        logItem.setClient(client);
        logItem.setHost(host);
        logItem.setSource(source);
        logItem.setUser(user);
        logItem.setType(type);
        logItem.setResourceBundleName(resourceBundleName);
        logItem.setMessage(message);
        logItem.setMessageResourceKey(messageResourceKey);
        logItem.setMessageParameters(messageParameters);

        manager.persist(logItem);
        manager.flush();

        return logItem;
    }
    
    public LogItem create(
            Client client,
            String host,
            String source,
            String user,
            LogItemType type,
            String resourceBundleName,
            String message,
            String messageResourceKey,
            Object[] messageParameters,
            Class bundleResolver)
    {
    	LogItem logItem = create(client, host, source, user, type, resourceBundleName, message, messageResourceKey, messageParameters);
    	logItem.setBundleResolver(bundleResolver);
    	return logItem;
    }
        
    

    /**
     * @see org.mywms.service.LogItemService#create(String, LogItemType,
     *      String, String)
     */
    public LogItem create(
        Client client,
        String source,
        LogItemType type,
        String message,
        String messageResourceKey)
    {
        LogItem logItem = new LogItem();

        logItem.setClient(client);
        logItem.setSource(source);
        logItem.setType(type);
        logItem.setMessage(message);
        logItem.setMessageResourceKey(messageResourceKey);

        manager.persist(logItem);

        return logItem;
    }

    /**
     * @see org.mywms.service.LogItemService#create(org.mywms.model.Client,
     *      java.lang.String, org.mywms.globals.LogItemType,
     *      java.lang.String, java.lang.String, java.lang.String)
     */
    public LogItem create(
        Client client,
        String source,
        LogItemType type,
        String resourceBundleName,
        String message,
        String messageResourceKey)
    {
        LogItem logItem = new LogItem();

        logItem.setClient(client);
        logItem.setSource(source);
        logItem.setType(type);
        logItem.setResourceBundleName(resourceBundleName);
        logItem.setMessage(message);
        logItem.setMessageResourceKey(messageResourceKey);

        manager.persist(logItem);

        return logItem;
    }
    
    public LogItem create(
            Client client,
            String source,
            LogItemType type,
            String resourceBundleName,
            String message,
            String messageResourceKey,
            Class bundleResolver)
        {
            LogItem logItem = create(client, source, type, resourceBundleName, message, messageResourceKey);
            logItem.setBundleResolver(bundleResolver);
            return logItem;
        }


    /**
     * @see org.mywms.service.LogItemService#getChronologicalList(String,
     *      String, String, String, LogItemType, int)
     */
    @SuppressWarnings("unchecked")
    public List<LogItem> getChronologicalList(LogItemType type, int limit) {
        if (type == null) {
            return getChronologicalList((Client) null, limit);
        }

        Query query =
            manager.createQuery("SELECT li FROM "
                + LogItem.class.getSimpleName()
                + " li "
                + "WHERE li.type_=:type "
                + "ORDER BY li.created DESC, li.id DESC");

        query.setParameter("type", type.name());

        if (limit > 0) {
            query.setMaxResults(limit);
        }

        return (List<LogItem>) query.getResultList();
    }

    /**
     * @see org.mywms.service.LogItemService#getChronologicalList(String,
     *      String, String, String, LogItemType, int)
     */
    @SuppressWarnings("unchecked")
    public List<LogItem> getChronologicalList(
        String clientNumber,
        String host,
        String source,
        String user,
        LogItemType type,
        int limit)
    {
        StringBuffer queryStr = new StringBuffer();
        queryStr.append("SELECT li FROM ")
            .append(LogItem.class.getSimpleName())
            .append(" li ");

        // filter client
        boolean whereClauseUsed = false;
        if (clientNumber != null) {
            queryStr.append(whereClauseUsed ? "AND" : "WHERE");
            whereClauseUsed = true;
            queryStr.append(" li.client.number=:client ");
        }

        // filter host
        if (host != null) {
            queryStr.append(whereClauseUsed ? "AND" : "WHERE");
            whereClauseUsed = true;
            queryStr.append(" li.host=:host ");
        }

        // filter source
        if (source != null) {
            queryStr.append(whereClauseUsed ? "AND" : "WHERE");
            whereClauseUsed = true;
            queryStr.append(" li.source=:source ");
        }

        // filter user
        if (user != null) {
            queryStr.append(whereClauseUsed ? "AND" : "WHERE");
            whereClauseUsed = true;
            queryStr.append(" li.user=:user ");
        }

        // filter type
        if (type != null) {
            queryStr.append(whereClauseUsed ? "AND" : "WHERE");
            whereClauseUsed = true;
            queryStr.append(" li.type_=:type ");
        }

        // sort the resultset
        queryStr.append("ORDER BY li.created DESC, li.id DESC");

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
        if (type != null) {
            query.setParameter("type", type.name());
        }
        if (limit > 0) {
            query.setMaxResults(limit);
        }

        return (List<LogItem>) query.getResultList();
    }

    /**
     * @see org.mywms.service.LogItemService#getHosts(Client)
     */
    @SuppressWarnings("unchecked")
    public List<String> getHosts(Client client) {
        if (client == null) {
            Query query =
                manager.createQuery("SELECT li.host FROM "
                    + LogItem.class.getSimpleName()
                    + " li "
                    + "GROUP BY li.host ORDER BY li.host");

            List<String> list = (List<String>) query.getResultList();

            return list;
        }
        else {
            Query query =
                manager.createQuery("SELECT li.host FROM "
                    + LogItem.class.getSimpleName()
                    + " li "
                    + " WHERE li.client=:client "
                    + "GROUP BY li.host ORDER BY li.host");

            query.setParameter("client", client);

            List<String> list = (List<String>) query.getResultList();

            return list;
        }
    }

    /**
     * @see org.mywms.service.LogItemService#getSources(Client)
     */
    @SuppressWarnings("unchecked")
    public List<String> getSources(Client client) {
        if (client == null) {
            Query query =
                manager.createQuery("SELECT li.source FROM "
                    + LogItem.class.getSimpleName()
                    + " li "
                    + "GROUP BY li.source ORDER BY li.source");

            List<String> list = (List<String>) query.getResultList();

            return list;
        }
        else {
            Query query =
                manager.createQuery("SELECT li.source FROM "
                    + LogItem.class.getSimpleName()
                    + " li "
                    + " WHERE client=:client "
                    + "GROUP BY li.source ORDER BY li.source");

            query.setParameter("client", client);
            List<String> list = (List<String>) query.getResultList();

            return list;
        }
    }

    /**
     * @see org.mywms.service.LogItemService#getUsers(Client)
     */
    @SuppressWarnings("unchecked")
    public List<String> getUsers(Client client) {
        if (client == null) {
            Query query =
                manager.createQuery("SELECT li.user FROM "
                    + LogItem.class.getSimpleName()
                    + " li "
                    + "GROUP BY li.user ORDER BY li.user");

            List<String> list = (List<String>) query.getResultList();

            return list;
        }
        else {
            Query query =
                manager.createQuery("SELECT li.user FROM "
                    + LogItem.class.getSimpleName()
                    + " li "
                    + " WHERE client=:client "
                    + "GROUP BY li.user ORDER BY li.user");

            query.setParameter("client", client);
            List<String> list = (List<String>) query.getResultList();

            return list;
        }
    }
}
