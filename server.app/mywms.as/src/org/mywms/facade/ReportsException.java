/*
 * Copyright (c) 2006 by Fraunhofer IML, Dortmund.
 * All rights reserved.
 *
 * Project: myWMS
 */
package org.mywms.facade;

import org.mywms.globals.DefaultMessage;

/**
 * This exception is thrown by the Reports facade.
 * 
 * @see org.mywms.facade.PluginConfig
 * @author Olaf Krause
 * @version $Revision: 443 $ provided by $Author: okrause $
 */
public class ReportsException
    extends FacadeException
{

    private static final long serialVersionUID = 1L;

    /**
     * @param msg
     * @param resourcekey
     * @param parameters
     */
    public ReportsException(
        String msg,
        DefaultMessage resourcekey,
        Object[] parameters)
    {
        super(msg, resourcekey.toString(), parameters);
    }

    /**
     * @param t
     * @param msg
     * @param resourcekey
     * @param parameters
     */
    public ReportsException(
        Throwable t,
        String msg,
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
    public ReportsException(
        String msg,
        DefaultMessage resourcekey,
        Object[] parameters,
        String bundleName)
    {
        super(msg, resourcekey.toString(), parameters, bundleName);
    }

    /**
     * @param t
     * @param msg
     * @param resourcekey
     * @param parameters
     * @param bundleName
     */
    public ReportsException(
        Throwable t,
        String msg,
        DefaultMessage resourcekey,
        Object[] parameters,
        String bundleName)
    {
        super(t, resourcekey.toString(), parameters, bundleName);
    }

}
