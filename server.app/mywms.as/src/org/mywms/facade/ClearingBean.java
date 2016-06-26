/*
 * Copyright (c) 2008 by Fraunhofer IML, Dortmund.
 * All rights reserved.
 *
 * Project: mywms.as
 */
package org.mywms.facade;

import java.io.NotSerializableException;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;


import org.apache.log4j.Logger;
import org.mywms.model.ClearingItem;
import org.mywms.model.ClearingItemOption;
import org.mywms.model.Client;
import org.mywms.plugin.PluginFactory;
import org.mywms.plugin.clearing.ClearingDispatcher;
import org.mywms.plugin.clearing.ClearingDispatcherBean;
import org.mywms.service.ClearingItemService;
import org.mywms.service.ClientService;
import org.mywms.service.EntityNotFoundException;

/**
 * @see org.mywms.facade.Clearing
 * @author lfu, aelbaz
 */
@Stateless
public class ClearingBean
    extends BasicFacadeBean
    implements Clearing
{

    @SuppressWarnings("unused")
    private static final Logger log =
        Logger.getLogger(ClearingBean.class.getName());

    @EJB
    private ClearingItemService clearingItemService;

    @EJB
    private ClientService clientService;

    @EJB
    private PluginFactory pluginFactory;

    /**
     * @see org.mywms.facade.Clearing#setClearingItem(org.mywms.facade.ClearingItemTO)
     */
    public void setClearingItem(ClearingItemTO to) throws ClearingException {
        try {
            ClearingItem clearingItem = clearingItemService.get(to.id);
            to.merge(clearingItem);

            // resolve the plugin
            ClearingDispatcher plugin =
                (ClearingDispatcher) getClearingPlugin(
                    ClearingDispatcher.class.getSimpleName(),
                    ClearingDispatcherBean.class.getSimpleName());

            plugin.clear(clearingItem);

        }
        catch (EntityNotFoundException e) {
            throw new ClearingException("A ClearingItem with ID:"
                + to.id
                + " was not found.\n"
                + e.getMessage());
        }
        catch (VersionException e) {
            throw new ClearingException("This Clearing Item with ID:"
                + to.id
                + " has been handled.");
        }
    }

    /**
     * This method is the real working horse of the ReportsBean. It
     * instantiates a report generator and uses it to create a report.
     * 
     * @param clearingPlugin the name of the report to create
     * @param client the client to create the report for
     * @param locale the local to create the report with
     * @return the report plugin
     * @throws ReportsException if the client is invalid
     */
    private Object getClearingPlugin(
        String clearingPlugin,
        String defaultClassName) throws ClearingException
    {
        return pluginFactory.resolvePlugin(
            clientService.getSystemClient(),
            clearingPlugin,
            defaultClassName);
    }

    /**
     * @see org.mywms.facade.Clearing#getSources()
     */
    public List<String> getSources() {
        return clearingItemService.getSources(clientService.getSystemClient());
    }

    /**
     * @see org.mywms.facade.Clearing#getUsers()
     */
    public List<String> getUsers() {
        return clearingItemService.getUser(clientService.getSystemClient());
    }

    /**
     * @see org.mywms.facade.Clearing#getMandant()
     */
    public List<String> getMandants(String client) throws ClearingException {
        Client clientO;
        try {
            clientO = clientService.getByName(client);
        }
        catch (EntityNotFoundException e) {
            throw new ClearingException(e.getMessage());
        }

        List<Client> clients = clientService.getChronologicalList(clientO);
        List<String> listOfClient = new ArrayList<String>();
        for (int i = 0; i < clients.size(); i++) {
            listOfClient.add(clients.get(i).getName());
        }
        
        return listOfClient;
    }

    /**
     * @see org.mywms.facade.Clearing#createClearingItem(java.lang.String,
     *      java.lang.String, java.lang.String, java.lang.String,
     *      java.lang.String, java.lang.Object[], java.util.ArrayList)
     */
    public void createClearingItem(
        String client,
        String host,
        String source,
        String user,
        String messageResourceKey,
        String shortMessageResourceKey,
        String resourceBundleName,
        Class BundleResolver,
        Object[] shortMessageParameters,
        Object[] messageParameters,
        ArrayList<ClearingItemOption> options) throws ClearingException
    {

        Client c;
        try {
            c = clientService.getByName(client);
            clearingItemService.create(
                c,
                host,
                source,
                user,
                messageResourceKey,
                shortMessageResourceKey,
                resourceBundleName,
                BundleResolver,
                shortMessageParameters,
                messageParameters,
                options);
        }
        catch (NotSerializableException e) {
            throw new ClearingException(e.getMessage());
        }
        catch (EntityNotFoundException e) {
            throw new ClearingException(e.getMessage());
        }
    }

    /**
     * @see org.mywms.facade.Clearing#getChronologicalList(java.lang.String,
     *      java.lang.String, java.lang.String, java.lang.String, int)
     */
    public List<ClearingItemTO> getChronologicalList(
        String client,
        String host,
        String source,
        String user,
        Integer limit) throws ClearingException
    {
        try {
            Client clientName;
            List<ClearingItem> clearingItems;

            if (client != null) {
                clientName = clientService.getByName(client);
                clearingItems =
                    clearingItemService.getNondealChronologicalList(
                        clientName.getNumber(),
                        host,
                        source,
                        user,
                        limit);
            }
            else {
                clearingItems =
                    clearingItemService.getNondealChronologicalList(
                        null,
                        host,
                        source,
                        user,
                        limit);
            }

            List<ClearingItemTO> tos = new ArrayList<ClearingItemTO>();
            for (int i = 0; i < clearingItems.size(); i++) {
                tos.add(new ClearingItemTO(clearingItems.get(i)));
            }

            return tos;
        }
        catch (EntityNotFoundException e) {
            throw new ClearingException("The Client with the name "
                + client
                + " was not found.");
        }
    }

}
