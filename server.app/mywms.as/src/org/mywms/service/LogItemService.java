/*
 * Copyright (c) 2006 by Fraunhofer IML, Dortmund.
 * All rights reserved.
 *
 * Project: myWMS
 */
package org.mywms.service;

import java.util.List;

import javax.ejb.Local;

import org.mywms.globals.LogItemType;
import org.mywms.model.Client;
import org.mywms.model.LogItem;

/**
 * This interface declares the service for the entity LogItem.
 * 
 * @author Olaf Krause
 * @version $Revision: 674 $ provided by $Author: mkrane $
 */
@Local
public interface LogItemService
    extends BasicService<LogItem>
{
    // ----------------------------------------------------------------
    // set of individual methods
    // ----------------------------------------------------------------

    /**
     * Creates a new LogItem using the specified arguments to initialize
     * the immutable properties.
     * 
     * @param client the client for who the event occured
     * @param host the host where the event occured
     * @param source the process/state when the event occured
     * @param user the user, who operated during the event
     * @param type the type of the event
     * @param message the default message to be used
     * @param messageResourceKey a resource key used for the
     *            internationalized message
     * @return the new LogItem
     */
    LogItem create(
        Client client,
        String host,
        String source,
        String user,
        LogItemType type,
        String message,
        String messageResourceKey);

    /**
     * Creates a new LogItem using the specified arguments to initialize
     * the immutable properties.
     * 
     * @param client the client for who the event occured
     * @param host the host where the event occured
     * @param source the process/state when the event occured
     * @param user the user, who operated during the event
     * @param type the type of the event
     * @param resourceBundleName the name of the resource bundle to use
     *            for internationalization
     * @param message the default message to be used
     * @param messageResourceKey a resource key used for the
     *            internationalized message
     * @return the new LogItem
     */
    LogItem create(
        Client client,
        String host,
        String source,
        String user,
        LogItemType type,
        String resourceBundleName,
        String message,
        String messageResourceKey,
        Object[] messageParameters);
    
    /**
     * Creates a new LogItem using the specified arguments to initialize
     * the immutable properties.
     * 
     * @param client the client for who the event occured
     * @param host the host where the event occured
     * @param source the process/state when the event occured
     * @param user the user, who operated during the event
     * @param type the type of the event
     * @param resourceBundleName the name of the resource bundle to use
     *            for internationalization
     * @param message the default message to be used
     * @param messageResourceKey a resource key used for the
     *            internationalized message
     * @param bundleResolver Class in the same package where bundle resides  
     * @return the new LogItem
     */
    LogItem create(
        Client client,
        String host,
        String source,
        String user,
        LogItemType type,
        String resourceBundleName,
        String message,
        String messageResourceKey,
        Object[] messageParameters,
        Class bundleResolver);

    /**
     * Creates a new LogItem using the specified arguments to initialize
     * the immutable properties.
     * 
     * @param source the process/state when the event occured
     * @param type the type of the event
     * @param message the default message to be used
     * @param messageResourceKey a resource key used for the
     *            internationalized message
     * @return the new LogItem
     */
    LogItem create(
        Client client,
        String source,
        LogItemType type,
        String message,
        String messageResourceKey);

    /**
     * Creates a new LogItem using the specified arguments to initialize
     * the immutable properties.
     * 
     * @param source the process/state when the event occured
     * @param type the type of the event
     * @param resourceBundleName the name of the resource bundle to use
     *            for internationalization
     * @param message the default message to be used
     * @param messageResourceKey a resource key used for the
     *            internationalized message
     * @return the new LogItem
     */
    LogItem create(
        Client client,
        String source,
        LogItemType type,
        String resourceBundleName,
        String message,
        String messageResourceKey);
    
    /**
     * Creates a new LogItem using the specified arguments to initialize
     * the immutable properties.
     * 
     * @param source the process/state when the event occured
     * @param type the type of the event
     * @param resourceBundleName the name of the resource bundle to use
     *            for internationalization
     * @param message the default message to be used
     * @param messageResourceKey a resource key used for the
     *            internationalized message
     * @param bundleResolver
     * @return the new LogItem
     */
    LogItem create(
        Client client,
        String source,
        LogItemType type,
        String resourceBundleName,
        String message,
        String messageResourceKey,
        Class bundleResolver);

    /**
     * Returns a list of LogItems, matching the specified parameters.
     * The value null for a parameter will not restriv the result set.
     * 
     * @param client the client to be found in the logs
     * @param host the host to be found in the logs
     * @param source the source to be found in the logs
     * @param user the user to be found in the logs
     * @param type the type to be found in the logs
     * @param limit the maximum amount of rows returned
     * @return a list of matching logs
     */
    List<LogItem> getChronologicalList(
        String client,
        String host,
        String source,
        String user,
        LogItemType type,
        int limit);

    /**
     * Returns a list of users ever logged.
     * 
     * @param client the client of the caller; maybe null
     * @return list of users
     */
    List<String> getUsers(Client client);

    /**
     * Returns a list of sources ever logged.
     * 
     * @param client the client of the caller; maybe null
     * @return list of sources
     */
    List<String> getSources(Client client);

    /**
     * Returns a list of hosts ever logged.
     * 
     * @param client the client of the caller; maybe null
     * @return list of hosts
     */
    List<String> getHosts(Client client);
}
