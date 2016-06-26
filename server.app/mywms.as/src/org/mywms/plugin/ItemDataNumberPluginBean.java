/*
 * Copyright (c) 2006 by Fraunhofer IML, Dortmund.
 * All rights reserved.
 *
 * Project: myWMS
 */
package org.mywms.plugin;

/**
 * This class is a dump implementation to create unique item numbers. It
 * starts usinge the current time and then increments this counter call
 * by call.
 * 
 * @author Olaf Krause
 * @version $Revision: 442 $ provided by $Author: okrause $
 */
public class ItemDataNumberPluginBean
    implements ItemDataNumberPlugin
{

    static long counter = System.currentTimeMillis();

    /**
     * @see org.mywms.plugin.ItemDataNumberPlugin#getItemNumber()
     */
    public String getItemNumber() {
        synchronized (ItemDataNumberPluginBean.class) {
            return Long.toString(counter++);
        }
    }

}
