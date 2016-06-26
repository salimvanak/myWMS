/*
 * Copyright (c) 2007 by Fraunhofer IML, Dortmund.
 * All rights reserved.
 *
 * Project: mywms.as
 */
package org.mywms.mfc;

import java.util.ResourceBundle;

/**
 * Any user defined dispatcher must implement this interface to interact
 * with the application server.
 * 
 * @author Olaf Krause
 * @version $Revision: 442 $ provided by $Author: okrause $
 */
public interface ASDispatcher {

    /**
     * Evaluate is called by the MaterialFlowControl process, whenever a
     * MFCMessage from the application server is received.
     * 
     * @param message the message to be dispatched
     */
    void evaluate(MFCMessage message);

    /**
     * Sets a resource bundle containing the current process
     * configuration.
     * 
     * @param config
     */
    void setConfiguration(ResourceBundle config);

    /**
     * Sets the current server connection.
     * 
     * @param connection the server connection
     */
    void setServerConnection(ServerConnection connection);

}
