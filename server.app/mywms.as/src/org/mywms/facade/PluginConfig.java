/*
 * Copyright (c) 2006 by Fraunhofer IML, Dortmund.
 * All rights reserved.
 *
 * Project: myWMS
 */
package org.mywms.facade;

import javax.ejb.Remote;

/**
 * This fassade declares the interface to operate the configuration of
 * plugins. Per default the mode called "default" is allways available.
 * 
 * @author Olaf Krause
 * @version $Revision: 442 $ provided by $Author: okrause $
 */
@Remote
public interface PluginConfig {
    /**
     * Returns a list of all configured plugins.
     * 
     * @return a list of all configured plugins
     * @throws PluginConfigException
     */
    String[] getPlugins() throws PluginConfigException;

    /**
     * Returns the available modes, the application can be switched to.
     * 
     * @return the available modes
     */
    String[] getModes();

    /**
     * Returns the current mode, the application is switched to.
     * 
     * @return the current mode
     */
    String getCurrentMode();

    /**
     * Switches to the specified mode, if available.
     * 
     * @param mode
     * @throws PluginConfigException if the mode is not available
     */
    void setMode(String mode) throws PluginConfigException;

    /**
     * Sets a classname for the specified plugin and the specified
     * plugin. If the mode matches the current mode, the change will
     * affect the system behaviour instandly.
     * 
     * @param clientNumber the pursessor of the configuration; maybe
     *            null
     * @param mode the mode to be manipulated
     * @param plugin
     * @param pluginClassname
     * @throws PluginConfigException
     */
    void setPlugin(
        String clientNumber,
        String mode,
        String plugin,
        String pluginClassname) throws PluginConfigException;

    /**
     * Returns the classname stored for the specified plugin in the
     * specified mode.
     * 
     * @param clientNumber the pursessor of the configuration; maybe
     *            null
     * @param mode the mode to be used to search for the plugin
     * @param plugin the plugin identifier
     * @return the classname of the plugin
     * @throws PluginConfigException if the mode or the plugin is
     *             invalid
     */
    String getPlugin(String clientNumber, String mode, String plugin)
        throws PluginConfigException;

    /**
     * Sets the new lookup pattern, used to resolve local plugins. The
     * location, where the requested bean is inserted, is identified by
     * a questionmark (?). Example: <code>/myWMS/?/local</code>
     * 
     * @param lookupPattern
     * @throws PluginConfigException
     */
    void setLookupPattern(String lookupPattern) throws PluginConfigException;

    /**
     * Returns the current lookup pattern, used to resolve local
     * plugins. The location, where the requested bean is inserted, is
     * identified by a questionmark (?). Example:
     * <code>/myWMS/?/local</code>
     * 
     * @return the lookup pattern
     * @throws PluginConfigException
     */
    String getLookupPattern() throws PluginConfigException;
}
