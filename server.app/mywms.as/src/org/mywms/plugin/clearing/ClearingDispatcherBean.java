/*
 * Copyright (c) 2008 by Fraunhofer IML, Dortmund.
 * All rights reserved.
 *
 * Project: mywms.as
 */
package org.mywms.plugin.clearing;

import javax.ejb.Stateless;

import org.mywms.facade.ClearingException;
import org.mywms.model.ClearingItem;

/**
 * @see org.mywms.component.Equipment
 * @author Liyu Fu, Olaf Krause
 * @version $Revision: 484 $ provided by $Author: lfu $
 */

@Stateless
public class ClearingDispatcherBean
    implements ClearingDispatcher
{

    /* (non-Javadoc)
     * @see org.mywms.plugin.clearing.ClearingDispatcher#clear(org.mywms.model.ClearingItem)
     */
    public void clear(ClearingItem item) throws ClearingException {
        
    }

}
