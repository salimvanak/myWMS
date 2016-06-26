/*
 * Copyright (c) 2006 by Fraunhofer IML, Dortmund.
 * All rights reserved.
 *
 * Project: myWMS
 */
package org.mywms.facade;

import javax.ejb.Remote;

import org.mywms.globals.LogItemType;

/**
 * This fassade declares the interface to access the operational log.
 * 
 * @author Olaf Krause
 * @version $Revision: 481 $ provided by $Author: okrause $
 */
@Remote
public interface Log
    extends BasicFacade
{

    /**
     * Logs a message to the myWMS system log. This log is not intended
     * to support software debugging, but to log operational events. The
     * log facade is used by clients as well as by other facades.
     * 
     * @param client the client to log for
     * @param host the name of the host where the log has been created
     * @param source the source of the event (for example the name/step
     *            of the process)
     * @param type the type of the log
     * @param message the describing default message stored for this log
     * @param messageResourceKey a resource key to internationalize the
     *            message
     */
    void log(
        String client,
        String host,
        String source,
        String user,
        LogItemType type,
        String message,
        String messageResourceKey);

    /**
     * Logs a message to the myWMS system log. This log is not intended
     * to support software debugging, but to log operational events. The
     * log facade is used by clients as well as by other facades.
     * 
     * @param host the name of the host where the log has been created
     * @param source the source of the event (for example the name/step
     *            of the process)
     * @param type the type of the log
     * @param message the describing default message stored for this log
     * @param messageResourceKey a resource key to internationalize the
     *            message
     */
    void log(
        String host,
        String source,
        String user,
        LogItemType type,
        String message,
        String messageResourceKey);

    /**
     * Returns a limited number of log items. The items are sorted
     * descending by creation date.
     * 
     * @param limit the maximum number of items to return; 0 if no limit
     *            applies
     * @return <i>limit</i> ot less log items
     */
    LogItemTO[] getLogs(int limit);

    /**
     * Returns a limited number of log items of the specified type. The
     * items are sorted descending by creation date.
     * 
     * @param client the client to find in logs
     * @param host the host to find in logs
     * @param source the source to find in logs
     * @param user the user to find in logs
     * @param type the type to be displayed
     * @param limit the maximum number of items to return; 0 if no limit
     *            applies
     * @return <i>limit</i> ot less log items
     */
    LogItemTO[] getLogs(
        String client,
        String host,
        String source,
        String user,
        LogItemType type,
        int limit);

    /**
     * Returns an array of all hosts ever logged.
     * 
     * @return an array of hosts
     */
    String[] getHosts();

    /**
     * Returns an array of all users ever logged.
     * 
     * @return an array of users
     */
    String[] getUsers();

    /**
     * Returns an array of sources ever logged.
     * 
     * @return an array of sources
     */
    String[] getSources();
}
