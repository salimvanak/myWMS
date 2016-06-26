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
import org.mywms.globals.DefaultMessage;
import org.mywms.globals.ServiceExceptionKey;
import org.mywms.model.Client;
import org.mywms.model.Role;
import org.mywms.model.User;
import org.mywms.service.ClientService;
import org.mywms.service.ConstraintViolatedException;
import org.mywms.service.EntityNotFoundException;
import org.mywms.service.RoleService;
import org.mywms.service.UniqueConstraintViolatedException;
import org.mywms.service.UserService;

/**
 * @see org.mywms.facade.Clients
 * @author Olaf Krause
 * @version $Revision: 739 $ provided by $Author: mkrane $
 */
@Stateless
@PermitAll
public class UsersBean
    extends BasicFacadeBean
    implements Users
{
    private static final Logger log =
        Logger.getLogger(BasicFacadeBean.class.getName());

    @EJB
    UserService userService;

    @EJB
    RoleService roleService;

    @EJB
    ClientService clientService;

    /**
     * @see org.mywms.facade.Users#createUser(java.lang.String)
     */
    public UserTO createUser(String name)
        throws UniqueConstraintViolatedException
    {
        log.info("createUser() called for user " + name);
        User user = userService.create(getCallersClient(), name);

        UserTO userTO = new UserTO(user);
        return userTO;
    }

    /**
     * @see org.mywms.facade.Users#getUser(long)
     */
    public UserTO getUser(long id) throws EntityNotFoundException {
        Client cc = getCallersClient();

        User user = userService.get(id);

        // check, if the user is allowed to get the user data
        if (cc.isSystemClient()
            || (!cc.isSystemClient() && cc == user.getClient()))
        {
            UserTO userTO = new UserTO(user);
            return userTO;
        }
        else {
            throw new EntityNotFoundException(
                ServiceExceptionKey.NO_ENTITY_WITH_ID);
        }
    }

    /**
     * @see org.mywms.facade.Users#getUsers()
     */
    public UserTO[] getUsers() {
        Client cc = getCallersClient();

        List<User> users = userService.getList(cc);

        int n = users.size();
        UserTO[] userTOs = new UserTO[n];
        for (int i = 0; i < n; i++) {
            userTOs[i] = new UserTO(users.get(i));
        }

        return userTOs;
    }

    /**
     * @see org.mywms.facade.Users#removeUser(long)
     */
    public void removeUser(long id)
        throws EntityNotFoundException,
            UsersException
    {
        log.info("removeUser() called for user " + id);
        Client cc = getCallersClient();

        User user = userService.get(id);

        // check, if the user is allowed to get the user data
        if (cc.isSystemClient()
            || (!cc.isSystemClient() && cc == user.getClient()))
        {
            try {
                userService.delete(user);
            }
            catch (ConstraintViolatedException e) {
                throw new UsersException(
                    "Removing the specified user failed, because a "
                        + "constraint was violated.",
                    DefaultMessage.Users_removeConstraintViolated,
                    new Object[0]);
            }
        }
        else {
            throw new EntityNotFoundException(
                ServiceExceptionKey.NO_ENTITY_WITH_ID);
        }
    }

    /**
     * @see org.mywms.facade.Users#setUser(org.mywms.facade.UserTO)
     */
    public void setUser(UserTO userTO)
        throws UsersException,
            EntityNotFoundException,
            VersionException
    {
        log.info("setUser() called for user "
            + userTO.id
            + " ("
            + userTO.name
            + ")");
        Client cc = getCallersClient();

        User user = userService.get(userTO.id);

        // check, if the user is allowed to change the user data
        if (cc.isSystemClient() // OK - superuser can change clientes
            || (!cc.isSystemClient() && cc == user.getClient() // check,
            // if
            // edited
            // user's
            // client
            // match
            // callers
            // client
            && cc.getNumber().equals(userTO.clientNumber))) // check, if
        // client is
        // unchanged
        {
            userTO.merge(
                user,
                roleService.getList(clientService.getSystemClient()));

            if (!cc.isSystemClient()) {
                // whatever the TO did contain - overwrite the client
                // with the
                // special client of the caller
                user.setClient(cc);
            }
            else { // => cc is the system client.
                // set the client to what the calling user has choosen
                if (userTO.clientNumber != null) {
                    Client client =
                        clientService.getByNumber(userTO.clientNumber);
                    user.setClient(client);
                }
                else {
                    throw new NullPointerException();
                }
            }
        }
        else {
            throw new UsersException(
                "You are not allowed to change the users client setting.",
                DefaultMessage.Users_clientChangeNotAllowed,
                new Object[0]);
        }
    }

    /**
     * @see org.mywms.facade.Users#getAllRoles()
     */
    public String[] getAllRoles() {
        List<Role> roles = roleService.getList(clientService.getSystemClient());

        int n = roles.size();
        String[] roleStrs = new String[n];
        for (int i = 0; i < n; i++) {
            roleStrs[i] = roles.get(i).getName();
        }

        return roleStrs;
    }

    /**
     * @see org.mywms.facade.Users#getAllClients()
     */
    public ClientTO[] getAllClients() {
        if (!getCallersClient().isSystemClient()) {
            return null;
        }

        // return a list of all available clients
        return getClients();
    }
}
