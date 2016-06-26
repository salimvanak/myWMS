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
 * @version $Revision: 331 $ provided by $Author: aelbaz $
 */
@Stateless
public class RequestSimpleNumberBean
    implements RequestNumber
{
    private static long currentTimeMillis = System.currentTimeMillis();

    /**
     * @see org.mywms.plugin.bean.RequestNumber#createRequestNumber()
     */
    public String createRequestNumber() {
        synchronized (this.getClass()) {
            long newCurrentTimeMillis = System.currentTimeMillis();
            if (newCurrentTimeMillis <= currentTimeMillis) {
                currentTimeMillis++;
            }
            else {
                currentTimeMillis = newCurrentTimeMillis;
            }
            return Long.toString(currentTimeMillis);
        }
    }
}