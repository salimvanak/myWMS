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
 * @author Olaf Krause
 * @version $Revision: 442 $ provided by $Author: okrause $
 */
public class MFCDefaultDispatcher
    implements MFCDispatcher
{
    private static final Logger log =
        Logger.getLogger(MFCDefaultDispatcher.class.getName());
    private ResourceBundle config;

    public MFCDefaultDispatcher() {
        log.info("MFCDefaultDispatcher()");
    }

    /**
     * @see org.mywms.mfc.MFCDispatcher#setConfiguration(java.util.ResourceBundle)
     */
    public void setConfiguration(ResourceBundle config) {
        log.info("setConfiguration()");
        this.config = config;
    }

    /**
     * @return the config
     */
    public ResourceBundle getConfig() {
        return this.config;
    }
}
