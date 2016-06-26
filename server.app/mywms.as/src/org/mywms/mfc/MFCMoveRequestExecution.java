/*
 * Copyright (c) 2006,2007 by Fraunhofer IML, Dortmund.
 * All rights reserved.
 *
 * Project: myWMS
 */
package org.mywms.mfc;

/**
 * The execution of a move request confirms, that the UnitLoad has
 * reached its destination.
 * 
 * @author Olaf Krause
 * @version $Revision: 442 $ provided by $Author: okrause $
 */
public class MFCMoveRequestExecution
    implements MFCMessage
{
    private static final long serialVersionUID = 1L;

    long requestId;
}
