/*
 * Copyright (c) 2006 by Fraunhofer IML, Dortmund.
 * All rights reserved.
 *
 * Project: myWMS
 */
package org.mywms.facade;

/**
 * The ClientsException is thrown by the Clients facade.
 * 
 * @author Olaf Krause
 * @version $Revision: 442 $ provided by $Author: okrause $
 */
public class ClientsException
    extends FacadeException
{

    private static final long serialVersionUID = 1L;

    /**
     * @param msg
     * @param resourcekey
     * @param parameters
     */
    public ClientsException(String msg, String resourcekey, Object[] parameters)
    {
        super(msg, resourcekey, parameters);
    }

    /**
     * @param t
     * @param resourcekey
     * @param parameters
     */
    public ClientsException(Throwable t, String resourcekey, Object[] parameters)
    {
        super(t, resourcekey, parameters);
    }

    /**
     * @param msg
     * @param resourcekey
     * @param parameters
     * @param bundleName
     */
    public ClientsException(
        String msg,
        String resourcekey,
        Object[] parameters,
        String bundleName)
    {
        super(msg, resourcekey, parameters, bundleName);
    }

    /**
     * @param t
     * @param resourcekey
     * @param parameters
     * @param bundleName
     */
    public ClientsException(
        Throwable t,
        String resourcekey,
        Object[] parameters,
        String bundleName)
    {
        super(t, resourcekey, parameters, bundleName);
    }

}
