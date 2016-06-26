/*
 * Copyright (c) 2006,2007 by Fraunhofer IML, Dortmund.
 * All rights reserved.
 *
 * Project: myWMS
 */
package org.mywms.mfc;

/**
 * Contains the current location of the unit load. Several responses can
 * be delivered during a move.
 * 
 * @author Olaf Krause
 * @version $Revision: 442 $ provided by $Author: okrause $
 */
public class MFCMoveRequestResponse
    implements MFCMessage
{
    private static final long serialVersionUID = 1L;

    long requestId;

    String currentLocation;
}
