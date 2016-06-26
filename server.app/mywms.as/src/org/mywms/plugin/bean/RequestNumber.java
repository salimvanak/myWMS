/*
 * Copyright (c) 2006 by Fraunhofer IML, Dortmund.
 * All rights reserved.
 *
 * Project: myWMS
 */
package org.mywms.plugin.bean;

import javax.ejb.Local;

/**
 * Creates a new unique request number.
 * 
 * @author Olaf Krause
 * @version $Revision: 487 $ provided by $Author: okrause $
 */
@Local
public interface RequestNumber {

    /**
     * Creates a new unique request number.
     * 
     * @return the new unique request number
     */
    String createRequestNumber();

}