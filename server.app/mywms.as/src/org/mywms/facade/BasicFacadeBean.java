/*
 * Copyright (c) 2006 by Fraunhofer IML, Dortmund.
 * All rights reserved.
 *
 * Project: myWMS
 */
package org.mywms.facade;

import java.security.Principal;
import java.util.List;

import javax.ejb.EJB;
import javax.annotation.Resource;
import javax.ejb.EJBContext;
import javax.ejb.Stateless;

import org.apache.log4j.Logger;
import org.mywms.model.Client;
import org.mywms.model.User;
import org.mywms.service.ClientService;
import org.mywms.service.EntityNotFoundException;
import org.mywms.service.UserService;

/**
 * @see org.mywms.facade.Clients
 * @author Olaf Krause
 * @version $Revision: 635 $ provided by $Author: mjordan $
 */
@Stateless
public class BasicFacadeBean
    implements BasicFacade
{
    @SuppressWarnings("unused")
    private static final Logger log =
        Logger.getLogger(BasicFacadeBean.class.getName());

    @EJB
    private ClientService clientService;

    @Resource
    EJBContext context;

    @EJB
    UserService userService;

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
     * @see org.mywms.facade.BasicFacade#getClientNames()
     */
    public String[] getClientNames() {
        List<org.mywms.model.Client> clients =
            clientService.getList((Client) null, 0, new String[] {
                "name"
            });
        int n = clients.size();
        String[] clientNames = new String[n];
        for (int i = 0; i < n; i++) {
            clientNames[i] = clients.get(i).getName();
        }
        return clientNames;
    }

    /**
     * Returns the client, the current caller belongs to. If the user
     * does not belong to a single client (for example if the user is an
     * administrator) this method will return null.
     * 
     * @return the client
     */
    protected Client getCallersClient() {
        Principal principal = context.getCallerPrincipal();
        if (principal.getName() == null) {
            return null;
        }

        try {
            User user = userService.getByUsername(principal.getName());
            return user.getClient();
        }
        catch (EntityNotFoundException ex) {
            return null;
        }
    }

    /**
     * Returns the username of the user, currently operating a thread.
     * 
     * @return the username
     */
    protected String getCallersUsername() {
        Principal principal = context.getCallerPrincipal();
        if (principal.getName() == null) {
            return "<undefined>";
        }
        else {
            return principal.getName();
        }
    }
}
