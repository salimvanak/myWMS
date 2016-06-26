/*
 * Copyright (c) 2006 by Fraunhofer IML, Dortmund.
 * All rights reserved.
 *
 * Project: myWMS
 */
package org.mywms.facade;

import org.mywms.model.PluginConfiguration;

/**
 * Transfers the data of a PluginConfiguration entity.
 * 
 * @author Olaf Krause
 * @version $Revision: 442 $ provided by $Author: okrause $
 */
public class PluginConfigTO
    extends BasicTO
{
    private static final long serialVersionUID = 1L;

    /** Holds value of property mode. */
    public String mode;
    /** Holds value of property plugin class. */
    public String pluginClass;
    /** Holds value of property plugin name. */
    public String pluginName;

    /**
     * Creates a new PluginConfigTO, using the data of the given
     * PluginConfiguration.
     * 
     * @param config the source of the data to transfer
     */
    public PluginConfigTO(PluginConfiguration config) {
        super(config);
        config.getMode();
        config.getPluginName();
        config.getPluginClass();
    }
}
