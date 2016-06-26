/*
 * Copyright (c) 2006 by Fraunhofer IML, Dortmund.
 * All rights reserved.
 *
 * Project: myWMS
 */
package org.mywms.facade;

/**
 * This exception is thrown by the Document facade.
 * 
 * @see org.mywms.facade.PluginConfig
 * @author Olaf Krause
 * @version $Revision: 442 $ provided by $Author: okrause $
 */
public class DocumentException
    extends FacadeException
{

    private static final long serialVersionUID = 1L;

    /**
     * @param msg
     * @param resourcekey
     * @param parameters
     */
    public DocumentException(String msg, String resourcekey, Object[] parameters)
    {
        super(msg, resourcekey, parameters);
    }

    /**
     * @param t
     * @param msg
     * @param resourcekey
     * @param parameters
     */
    public DocumentException(
        Throwable t,
        String msg,
        String resourcekey,
        Object[] parameters)
    {
        super(t, resourcekey, parameters);
    }

    /**
     * @param msg
     * @param resourcekey
     * @param parameters
     * @param bundleName
     */
    public DocumentException(
        String msg,
        String resourcekey,
        Object[] parameters,
        String bundleName)
    {
        super(msg, resourcekey, parameters, bundleName);
    }

    /**
     * @param t
     * @param msg
     * @param resourcekey
     * @param parameters
     * @param bundleName
     */
    public DocumentException(
        Throwable t,
        String msg,
        String resourcekey,
        Object[] parameters,
        String bundleName)
    {
        super(t, resourcekey, parameters, bundleName);
    }

}
