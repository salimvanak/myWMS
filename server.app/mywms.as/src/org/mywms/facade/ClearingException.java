/*
 * Copyright (c) 2008 by Fraunhofer IML, Dortmund.
 * All rights reserved.
 *
 * Project: mywms.as
 */
package org.mywms.facade;

/**
 * The ClearingException is thrown by the Clearing facade.
 * 
 * @author lfu, aelbaz
 * @version $Revision: 442 $ provided by $Author: okrause $
 */
public class ClearingException
    extends Exception
{

    private static final long serialVersionUID = 1L;

    public ClearingException(String msg) {
        super(msg);
    }
}
