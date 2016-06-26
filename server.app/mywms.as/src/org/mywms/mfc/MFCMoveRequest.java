/*
 * Copyright (c) 2006,2007 by Fraunhofer IML, Dortmund.
 * All rights reserved.
 *
 * Project: myWMS
 */
package org.mywms.mfc;

/**
 * Contains the information which unit load should be moved from a
 * source to a destination location.
 * 
 * @author Olaf Krause
 * @version $Revision: 442 $ provided by $Author: okrause $
 */
public class MFCMoveRequest
    implements MFCMessage
{
    private static final long serialVersionUID = 1L;

    long requestId;

    String sourceLocation;
    String destinationLocation;
    String unitLoadLabelId;
}
