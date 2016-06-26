/*
 * Copyright (c) 2007 by Fraunhofer IML, Dortmund.
 * All rights reserved.
 *
 * Project: mywms.as
 */
package org.mywms.mfc;

import java.util.ResourceBundle;

/**
 * This interface is at least a tagging interface, to introduce
 * connectors to controllers into the MaterialFlowControl frame. A
 * MFCDispatcher should connect to its controller device and then
 * operate its communication.
 * 
 * @author Olaf Krause
 * @version $Revision: 442 $ provided by $Author: okrause $
 */
public interface MFCDispatcher {
    /**
     * Sets a resource bundle containing the current process
     * configuration.
     * 
     * @param config
     */
    void setConfiguration(ResourceBundle config);
}
