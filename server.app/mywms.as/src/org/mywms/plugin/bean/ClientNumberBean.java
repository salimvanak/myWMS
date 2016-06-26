/*
 * Copyright (c) 2006 by Fraunhofer IML, Dortmund.
 * All rights reserved.
 *
 * Project: myWMS
 */
package org.mywms.plugin.bean;

import javax.ejb.Stateless;

/**
 * @see org.mywms.plugin.bean.ClientNumber
 * @author Olaf Krause
 * @version $Revision: 442 $ provided by $Author: okrause $
 */
@Stateless
public class ClientNumberBean
    implements ClientNumber
{
    /**
     * @see org.mywms.plugin.bean.ClientNumber#createClientNumber()
     */
    public String createClientNumber() {
        return java.util.UUID.randomUUID().toString();
    }

}