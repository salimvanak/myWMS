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
 * This fassade declares the interface to access the clients (German:
 * Mandanten), stored in the WMS.
 * 
 * @author Olaf Krause
 * @version $Revision: 442 $ provided by $Author: okrause $
 */
@Remote
public interface Clients {

    /**
     * Creates a new Client, using the specified arguments.
     * 
     * @param name the name of the new client
     * @return the new created client
     * @throws UniqueConstraintViolatedException if the name is non
     *             unique
     * @throws ClientsException if the calling user is not allowed to
     *             create a client
     */
    ClientTO createClient(String name)
        throws ClientsException,
            UniqueConstraintViolatedException;

    /**
     * Returns the client with the specified name.
     * 
     * @param name the unique (file-)name of the client
     * @return the client
     * @throws EntityNotFoundException if the specified Client could not
     *             be found
     */
    ClientTO getClient(String name) throws EntityNotFoundException;

    /**
     * Returns the client with the specified id.
     * 
     * @param id the unique id of the client
     * @return the client
     * @throws EntityNotFoundException if the specified Client could not
     *             be found
     */
    ClientTO getClient(long id) throws EntityNotFoundException;

    /**
     * Stores a changed client back into the database.
     * 
     * @param client the client to save
     * @throws EntityNotFoundException if the specified client could not
     *             be found
     */
    void setClient(ClientTO client)
        throws EntityNotFoundException,
            VersionException;

    /**
     * Returns a list of clients.
     * 
     * @see #getClient(long)
     * @see #getClient(String)
     * @return the found clients
     */
    ClientTO[] getClients();

    /**
     * Deletes the specified client.
     * 
     * @param id the unique id of the client to delete
     * @throws EntityNotFoundException if the specified client could not
     *             be found
     * @throws ClientsException if the calling user is not allowed to
     *             remove a client
     */
    void removeClient(long id) throws ClientsException, EntityNotFoundException;
}
