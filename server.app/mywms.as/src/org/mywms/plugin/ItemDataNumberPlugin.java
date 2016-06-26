/*
 * Copyright (c) 2006 by Fraunhofer IML, Dortmund.
 * All rights reserved.
 *
 * Project: myWMS
 */
package org.mywms.plugin;

import javax.ejb.Local;

/**
 * @author Olaf Krause
 * @version $Revision: 634 $ provided by $Author: mkrane $
 */
@Local
public interface ItemDataNumberPlugin {
    /**
     * Returns a new, unique item number for a new ItemData object.
     * 
     * @return the new item number
     */
    String getItemNumber();
}
