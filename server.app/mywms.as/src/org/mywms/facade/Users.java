/*
 * Copyright (c) 2006 by Fraunhofer IML, Dortmund.
 * All rights reserved.
 *
 * Project: myWMS
 */
package org.mywms.facade;

import javax.ejb.Remote;

import org.mywms.service.EntityNotFoundException;
import org.mywms.service.UniqueConstraintViolatedException;

/**
 * This fassade declares the interface to access the users, stored in
 * the WMS.
 * 
 * @author Olaf Krause
 * @version $Revision: 442 $ provided by $Author: okrause $
 */
@Remote
public interface Users {

    /**
     * Creates a new User, using the specified arguments.
     * 
     * @param name the name of the new user
     * @return the new created user
     * @throws UniqueConstraintViolatedException if the name is non
     *             unique
     */
    UserTO createUser(String name) throws UniqueConstraintViolatedException;

    /**
     * Returns the user with the specified id.
     * 
     * @param id the unique id of the user
     * @return the user
     * @throws EntityNotFoundException if the specified user could not
     *             be found
     */
    UserTO getUser(long id) throws EntityNotFoundException;

    /**
     * Stores a changed user back into the database.
     * 
     * @param user the user to save
     * @throws EntityNotFoundException if the specified user could not
     *             be found
     */
    void setUser(UserTO user)
        throws UsersException,
            EntityNotFoundException,
            VersionException;

    /**
     * Returns a list of users.
     * 
     * @see #getUser(long)
     * @return the found users
     */
    UserTO[] getUsers();

    /**
     * Deletes the specified user.
     * 
     * @param id the unique id of the user to delete
     * @throws EntityNotFoundException if the specified user could not
     *             be found
     * @throws UsersException if a constraint was violated
     */
    void removeUser(long id) throws EntityNotFoundException, UsersException;

    /**
     * Returnes a list of all roles available in the system.
     * 
     * @return a list of all roles
     */
    String[] getAllRoles();

    /**
     * Returns a list of all clients, available for the calling user. If
     * the calling user is attached to a client, null is returned.
     * Otherwise an array of all clients is returned; maybe be an array
     * containing 0 elements, if currently no clients do exist.
     * 
     * @return a list of clients available for the user; or null
     */
    ClientTO[] getAllClients();

}
