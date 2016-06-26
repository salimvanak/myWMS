/*
 * Copyright (c) 2006 by Fraunhofer IML, Dortmund.
 * All rights reserved.
 *
 * Project: myWMS
 */
package org.mywms.facade;

import javax.ejb.ApplicationException;

/**
 * VersionException is thrown if data is merged into an entity which has
 * been modified in the mean time.
 * 
 * @author Olaf Krause
 * @version $Revision: 442 $ provided by $Author: okrause $
 */
@ApplicationException
public class VersionException
    extends RuntimeException
{
    private static final long serialVersionUID = 1L;
}
