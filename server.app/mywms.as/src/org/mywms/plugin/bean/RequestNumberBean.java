/*
 * Copyright (c) 2006 by Fraunhofer IML, Dortmund.
 * All rights reserved.
 *
 * Project: myWMS
 */
package org.mywms.plugin.bean;

import javax.ejb.Stateless;

/**
 * @see org.mywms.plugin.bean.RequestNumber
 * @author Olaf Krause
 * @version $Revision: 329 $ provided by $Author: okrause $
 */
@Stateless
public class RequestNumberBean
    implements RequestNumber
{
    /**
     * @see org.mywms.plugin.bean.RequestNumber#createRequestNumber()
     */
    public String createRequestNumber() {
        return java.util.UUID.randomUUID().toString();
    }

}
