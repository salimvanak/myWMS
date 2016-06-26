/*
 * Copyright (c) 2006 by Fraunhofer IML, Dortmund.
 * All rights reserved.
 *
 * Project: myWMS
 */
package org.mywms.facade;

import java.util.List;

import javax.annotation.security.PermitAll;
import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.log4j.Logger;
import org.mywms.globals.LogItemType;
import org.mywms.globals.ServiceExceptionKey;
import org.mywms.model.Client;
import org.mywms.plugin.PluginFactory;
import org.mywms.plugin.bean.ClientNumber;
import org.mywms.plugin.bean.ClientNumberBean;
import org.mywms.service.ClientService;
import org.mywms.service.ConstraintViolatedException;
import org.mywms.service.EntityNotFoundException;
import org.mywms.service.LogItemService;
import org.mywms.service.UniqueConstraintViolatedException;

/**
 * @see org.mywms.facade.Clients
 * @author Olaf Krause
 * @version $Revision: 740 $ provided by $Author: mkrane $
 */
@Stateless
@PermitAll
public class ClientsBean
    extends BasicFacadeBean
    implements Clients
{
    @SuppressWarnings("unused")
    private static final Logger log =
        Logger.getLogger(ClientsBean.class.getName());

    @EJB
    private ClientService clientService;

    @EJB
    private PluginFactory pluginFactory;

    @EJB
    private LogItemService logService;

    /**
     * @see org.mywms.facade.Clients#getClient(java.lang.String)
     */
    public ClientTO getClient(String name) throws EntityNotFoundException {
        org.mywms.model.Client client = clientService.getByName(name);
        ClientTO clientTO = new ClientTO(client);
        return clientTO;
    }

    /**
     * @see org.mywms.facade.Clients#getClient(long)
     */
    public ClientTO getClient(long id) throws EntityNotFoundException {
        org.mywms.model.Client client = clientService.get(id);
        ClientTO clientTO = new ClientTO(client);
        return clientTO;
    }

    /**
     * @see org.mywms.facade.Clients#setClient(org.mywms.facade.ClientTO)
     */
    public void setClient(ClientTO clientTO)
        throws EntityNotFoundException,
            VersionException
    {
        Client cc = getCallersClient();
        if (!cc.isSystemClient()) {
            // if the user is attached to a client, only the client
            // itself
            // may be changed
            if (!cc.getNumber().equals(clientTO.number)) {
                throw new EntityNotFoundException(
                    ServiceExceptionKey.NO_CLIENT_WITH_NUMBER);
            }
        }

        org.mywms.model.Client client = clientService.get(clientTO.id);
        clientTO.merge(client);

        logService.create(
            cc,
            Thread.currentThread().getName(),
            "ClientsBean",
            getCallersUsername(),
            LogItemType.LOG,
            "Client '" + client.getNumber() + "' has been updated.",
            ClientsBean.class.getName() + ".setClient()");
    }

    /**
     * @see org.mywms.facade.Clients#getClients()
     */
    public ClientTO[] getClients() {
        Client cc = getCallersClient();

        if (!cc.isSystemClient()) {
            return new ClientTO[] {
                new ClientTO(cc)
            };
        }

        List<org.mywms.model.Client> clients =
            clientService.getList(cc, 0, new String[] {
                "name"
            });

        int n = clients.size();
        ClientTO[] clientTOs = new ClientTO[n];
        for (int i = 0; i < n; i++) {
            clientTOs[i] = new ClientTO(clients.get(i));
        }
        return clientTOs;
    }

    /**
     * @see org.mywms.facade.Clients#removeClient(long)
     */
    public void removeClient(long id)
        throws ClientsException,
            EntityNotFoundException
    {
        Client cc = getCallersClient();
        if (!cc.isSystemClient()) {
            throw new ClientsException(
                "You are not allowed to remove a client.",
                "org.mywms.facade.Clients.removeNotAllowed",
                new Object[0]);
        }

        org.mywms.model.Client client = clientService.get(id);

        if (client.isSystemClient()) {
            throw new ClientsException(
                "You are not allowed to remove the root client.",
                "org.mywms.facade.Clients.removeNotAllowed",
                new Object[0]);
        }

        logService.create(cc, "ClientsBean", LogItemType.LOG, "Client "
            + client.getNumber()
            + " removed", ClientsBean.class.getName() + ".removeClient()");

        logService.create(
            cc,
            Thread.currentThread().getName(),
            "ClientsBean",
            getCallersUsername(),
            LogItemType.LOG,
            "Client '" + client.getNumber() + "' has been removed.",
            ClientsBean.class.getName() + ".removeClient()");

        try {
            clientService.delete(client);
        }
        catch (ConstraintViolatedException e) {
            throw new ClientsException(
                "Deleting the client violates a constraint: " + e.getMessage(),
                "org.mywms.facade.Clients.removeViolatedConstraint",
                new Object[0]);
        }
    }

    /**
     * @see org.mywms.facade.Clients#createClient(java.lang.String)
     */
    public ClientTO createClient(String name)
        throws ClientsException,
            UniqueConstraintViolatedException
    {
        Client cc = getCallersClient();
        if (!cc.isSystemClient()) {
            throw new ClientsException(
                "You are not allowed to create a new client.",
                "org.mywms.facade.Clients.createNotAllowed",
                new Object[0]);
        }

        // crate a unique number
        ClientNumber plugin =
            (ClientNumber) pluginFactory.resolvePlugin(
                cc,
                "ClientNumber",
                ClientNumberBean.class.getSimpleName());

        org.mywms.model.Client client =
            this.clientService.create(name, plugin.createClientNumber(), plugin.createClientNumber());

        logService.create(
            cc,
            Thread.currentThread().getName(),
            "ClientsBean",
            getCallersUsername(),
            LogItemType.LOG,
            "Client '" + client.getNumber() + "' has been created.",
            ClientsBean.class.getName() + ".createClient()");

        return new ClientTO(client);
    }
}
