/*
 * Copyright (c) 2006 by Fraunhofer IML, Dortmund.
 * All rights reserved.
 *
 * Project: myWMS
 */
package org.mywms.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

/**
 * Contains the configuration data for plugins.
 * 
 * @author Olaf Krause
 * @version $Revision: 491 $ provided by $Author: lxjordan $
 */
@Entity
@Table(name = "mywms_pluginconfiguration", uniqueConstraints = @UniqueConstraint(columnNames = {
    "client_id", "pluginName", "plugin_mode"
}))
public class PluginConfiguration
    extends BasicClientAssignedEntity
{
    private static final long serialVersionUID = 1L;

    /** The key of the default mode. */
    public static final String RESERVED_PLUGIN_MODE =
        "PluginConfiguration.Mode";
    public static final String DEFAULT_MODE = "default";

    public static final String RESERVED_PLUGIN_LOOKUP_PATTERN =
        "PluginConfiguration.LookupPattern";
    public static final String DEFAULT_LOOKUP_PATTERN = "myWMS/?/local"; // according
    // to
    // JBoss

    private String pluginName = null;
    private String pluginClass = null;
    private String mode = null;

    /**
     * @return Returns the mode.
     */
    @Column(nullable = false, name="plugin_mode")
    public String getMode() {
        return mode;
    }

    /**
     * @param mode The mode to set.
     */
    public void setMode(String mode) {
        this.mode = mode;
    }

    /**
     * @return Returns the pluginClass.
     */
    @Column(nullable = false)
    public String getPluginClass() {
        return pluginClass;
    }

    /**
     * @param pluginClass The pluginClass to set.
     */
    public void setPluginClass(String pluginClass) {
        this.pluginClass = pluginClass;
    }

    /**
     * @return Returns the pluginName.
     */
    @Column(nullable = false)
    public String getPluginName() {
        return pluginName;
    }

    /**
     * @param pluginName The pluginName to set.
     */
    public void setPluginName(String pluginName) {
        this.pluginName = pluginName;
    }
}
