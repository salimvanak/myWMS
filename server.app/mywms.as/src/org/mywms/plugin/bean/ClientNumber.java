/*
 * Copyright (c) 2006 by Fraunhofer IML, Dortmund.
 * All rights reserved.
 *
 * Project: myWMS
 */
package org.mywms.plugin.bean;

import javax.ejb.Local;

/**
 * Creates a new client number.
 * 
 * @author Olaf Krause
 * @version $Revision: 487 $ provided by $Author: okrause $
 */
@Local
public interface ClientNumber {

    /**
     * Creates a new unique client number.
     * 
     * @return the new unique client number
     */
    String createClientNumber();

}