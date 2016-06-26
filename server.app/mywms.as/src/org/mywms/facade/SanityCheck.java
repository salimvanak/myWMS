/*
 * Copyright (c) 2006 by Fraunhofer IML, Dortmund.
 * All rights reserved.
 *
 * Project: myWMS
 */
package org.mywms.facade;

import javax.ejb.Remote;

/**
 * This fassade declares methods to check the myWMS installation for
 * sanity. For now the sanity checks should be safe in any installation.
 * However - some special code added to your application may require
 * special sanity checks. So use the check with care in customized
 * installations. The sanity check adds rows to the database, required
 * by the myWMS system. For example, it adds missing admin and guest
 * users and it adds missing roles. Please refer to the implementation
 * (SanityCheckBean) to read about the required privileges for calling
 * the check.
 * 
 * @author Olaf Krause
 * @version $Revision: 442 $ provided by $Author: okrause $
 */
@Remote
public interface SanityCheck {

    /**
     * Runs the sanity check and returns a string based report.
     * 
     * @return the report string
     */
    String check();

}
