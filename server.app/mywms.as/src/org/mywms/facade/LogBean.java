/*
 * Copyright (c) 2006 by Fraunhofer IML, Dortmund.
 * All rights reserved.
 *
 * Project: myWMS
 */
package org.mywms.facade;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.security.PermitAll;
import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.mywms.globals.LogItemType;
import org.mywms.model.Client;
import org.mywms.model.LogItem;
import org.mywms.service.ClientService;
import org.mywms.service.LogItemService;

/**
 * This fassade declares the interface to access the operational log.
 * 
 * @author Olaf Krause
 * @version $Revision: 740 $ provided by $Author: mkrane $
 */
@Stateless
@PermitAll
public class LogBean
    extends BasicFacadeBean
    implements Log
{
    // @SuppressWarnings("unused")
    // private static final Logger log =
    // Logger.getLogger(LogBean.class.getName());

    @EJB
    private LogItemService logItemService;

    @EJB
    private ClientService clientService;

    /**
     * @see org.mywms.facade.Log#log(String, String, String,
     *      org.mywms.globals.LogItemType, String, String)
     */
    @PermitAll
    public void log(
        String host,
        String source,
        String user,
        LogItemType type,
        String message,
        String messageResourceKey)
    {
        log(null, host, source, user, type, message, messageResourceKey);
    }

    /**
     * @see org.mywms.facade.Log#log(String, String, String, String,
     *      org.mywms.globals.LogItemType, String, String)
     */
    @PermitAll
    public void log(
        String client,
        String host,
        String source,
        String user,
        LogItemType type,
        String message,
        String messageResourceKey)
    {
        org.mywms.model.Client clientEntity;
        if (client != null) {
            try {
                clientEntity = clientService.getByName(client);
            }
            catch (Exception ex) {
                // could not locate the specified client
                clientEntity = clientService.getSystemClient();
            }
        }
        else {
            clientEntity = clientService.getSystemClient();
        }

        logItemService.create(
            clientEntity,
            host,
            source,
            user,
            type,
            message,
            messageResourceKey);
    }

    /**
     * @see org.mywms.facade.Log#getLogs(int)
     */
    public LogItemTO[] getLogs(int limit) {
        List<LogItem> logItems =
            logItemService.getChronologicalList(getCallersClient(), limit);
        int n;
        n = logItems.size();
        List<LogItemTO> logItemTOs = new ArrayList<LogItemTO>(n);
        for (int i = 0; i < n; i++) {
            logItemTOs.add(new LogItemTO(logItems.get(i)));
        }
        return (LogItemTO[]) logItemTOs.toArray(new LogItemTO[n]);
    }

    /**
     * @see org.mywms.facade.Log#getLogs(java.lang.String,
     *      java.lang.String, java.lang.String, java.lang.String,
     *      org.mywms.globals.LogItemType, int)
     */
    public LogItemTO[] getLogs(
        String client,
        String host,
        String source,
        String user,
        LogItemType type,
        int limit)
    {
        Client clientEntity = getCallersClient();
        // if the caller may not see another client,
        // limit the shown logs to the preselected client
        if (!clientEntity.isSystemClient()) {
            client = clientEntity.getNumber();
        }

        List<LogItem> logItems =
            logItemService.getChronologicalList(
                client,
                host,
                source,
                user,
                type,
                limit);
        int n;
        n = logItems.size();
        List<LogItemTO> logItemTOs = new ArrayList<LogItemTO>(n);
        for (int i = 0; i < n; i++) {
            logItemTOs.add(new LogItemTO(logItems.get(i)));
        }
        return (LogItemTO[]) logItemTOs.toArray(new LogItemTO[n]);
    }

    /**
     * @see org.mywms.facade.Log#getHosts()
     */
    public String[] getHosts() {
        List<String> list = logItemService.getHosts(getCallersClient());
        return list.toArray(new String[0]);
    }

    /**
     * @see org.mywms.facade.Log#getSources()
     */
    public String[] getSources() {
        List<String> list = logItemService.getSources(getCallersClient());
        return list.toArray(new String[0]);
    }

    /**
     * @see org.mywms.facade.Log#getUsers()
     */
    public String[] getUsers() {
        List<String> list = logItemService.getUsers(getCallersClient());
        return list.toArray(new String[0]);
    }

}
