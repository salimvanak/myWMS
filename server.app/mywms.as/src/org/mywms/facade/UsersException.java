/*
 * Copyright (c) 2006 by Fraunhofer IML, Dortmund.
 * All rights reserved.
 *
 * Project: myWMS
 */
package org.mywms.facade;

import org.mywms.globals.DefaultMessage;

/**
 * The UsersException is thrown by the Users facade.
 * 
 * @author Olaf Krause
 * @version $Revision: 443 $ provided by $Author: okrause $
 */
public class UsersException
    extends FacadeException
{

    private static final long serialVersionUID = 1L;

    /**
     * @param msg
     * @param resourcekey
     * @param parameters
     */
    public UsersException(
        String msg,
        DefaultMessage resourcekey,
        Object[] parameters)
    {
        super(msg, resourcekey.toString(), parameters);
    }

    /**
     * @param t
     * @param resourcekey
     * @param parameters
     */
    public UsersException(
        Throwable t,
        DefaultMessage resourcekey,
        Object[] parameters)
    {
        super(t, resourcekey.toString(), parameters);
    }

    /**
     * @param msg
     * @param resourcekey
     * @param parameters
     * @param bundleName
     */
    public UsersException(
        String msg,
        DefaultMessage resourcekey,
        Object[] parameters,
        String bundleName)
    {
        super(msg, resourcekey.toString(), parameters, bundleName);
    }

    /**
     * @param t
     * @param resourcekey
     * @param parameters
     * @param bundleName
     */
    public UsersException(
        Throwable t,
        DefaultMessage resourcekey,
        Object[] parameters,
        String bundleName)
    {
        super(t, resourcekey.toString(), parameters, bundleName);
    }

}
