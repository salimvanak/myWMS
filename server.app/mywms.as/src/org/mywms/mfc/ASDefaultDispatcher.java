/*
 * Copyright (c) 2007 by Fraunhofer IML, Dortmund.
 * All rights reserved.
 *
 * Project: mywms.as
 */
package org.mywms.mfc;

import java.util.ResourceBundle;

import org.apache.log4j.Logger;

/**
 * The Default dispatcher converts incomming MFCMessage objects into
 * messages transfered to a default MFCDispatcher.
 * 
 * @author Olaf Krause
 * @version $Revision: 442 $ provided by $Author: okrause $
 */
public class ASDefaultDispatcher
    implements ASDispatcher
{
    private static final Logger log =
        Logger.getLogger(ASDefaultDispatcher.class.getName());
    private ResourceBundle config;

    @SuppressWarnings("unused")
    private ServerConnection serverConnection;

    public ASDefaultDispatcher() {
        log.info("ASDefaultDispatcher.ASDefaultDispatcher()");
    }

    /**
     * @see org.mywms.mfc.ASDispatcher#evaluate(org.mywms.mfc.MFCMessage)
     */
    public void evaluate(MFCMessage message) {
        log.info("ASDefaultDispatcher.evaluate(MFCEssage message)");
        log.info("message: " + message.toString());
    }

    /**
     * @see org.mywms.mfc.ASDispatcher#setConfiguration(java.util.ResourceBundle)
     */
    public void setConfiguration(ResourceBundle config) {
        log.info("ASDefaultDispatcher.setConfiguration()");
        this.config = config;
    }

    /**
     * @return the config
     */
    public ResourceBundle getConfig() {
        return this.config;
    }

    /**
     * @see org.mywms.mfc.ASDispatcher#setServerConnection(org.mywms.mfc.ServerConnection)
     */
    public void setServerConnection(ServerConnection connection) {
        this.serverConnection = connection;
    }
}
