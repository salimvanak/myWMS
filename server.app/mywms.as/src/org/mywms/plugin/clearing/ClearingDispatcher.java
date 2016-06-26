/*
 * Copyright (c) 2008 by Fraunhofer IML, Dortmund.
 * All rights reserved.
 *
 * Project: mywms.as
 */
package org.mywms.plugin.clearing;

import org.mywms.facade.ClearingException;
import org.mywms.model.ClearingItem;

/**
 * Implements some logistics operations for the handling of the clearing
 * item.
 * 
 * @author Liyu Fu, Olaf Krause
 * @version $Revision: 487 $ provided by $Author: okrause $
 */
@javax.ejb.Local
public interface ClearingDispatcher {

    /**
     * Method for implementation of clearing item handling
     * 
     * @param item clearing item
     * @throws ClearingException if problem happens during the
     *             handling of clearing item
     */
    void clear(ClearingItem item) throws ClearingException;

}
