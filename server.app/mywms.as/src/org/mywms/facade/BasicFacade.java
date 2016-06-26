/*
 * Copyright (c) 2006 by Fraunhofer IML, Dortmund.
 * All rights reserved.
 *
 * Project: myWMS
 */
package org.mywms.facade;

import javax.ejb.Remote;

/**
 * This fassade declares the interface to access informations nearly
 * every facade requires.
 * 
 * @author Olaf Krause
 * @version $Revision: 442 $ provided by $Author: okrause $
 */
@Remote
public interface BasicFacade {

    /**
     * Returns a list of clients.
     * 
     * @see #getClientNames()
     * @return the found clients
     */
    ClientTO[] getClients();

    /**
     * Returns a list of client names.
     * 
     * @see #getClients()
     * @return the found client names
     */
    String[] getClientNames();

}
