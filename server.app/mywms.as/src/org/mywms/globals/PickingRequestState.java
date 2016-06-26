/*
 * Copyright (c) 2006 by Fraunhofer IML, Dortmund.
 * All rights reserved.
 *
 * Project: myWMS
 */
package org.mywms.globals;

/**
 * Represents the state of a PickingRequest.
 * 
 * @author Olaf Krause
 * @version $Revision: 442 $ provided by $Author: okrause $
 */
public enum PickingRequestState {
    RAW, // freshly created
    ACCEPTED, // a user has accepted to fullfill the request
    PICKING, // a user picks the requested ItemDatas
    PICKED, // all positions are picked - finalization is pending
    PICKED_PARTIAL, // the request is partial finished
    FINISHED, // the request is finished completly
    FINISHED_PARTIAL, // request is finished partially
    FAILED
    // the request is failed completly
}