/*
 * Copyright (c) 2006 by Fraunhofer IML, Dortmund.
 * All rights reserved.
 *
 * Project: myWMS
 */
package org.mywms.service;

import java.util.List;

import javax.ejb.Local;

import org.mywms.model.Client;
import org.mywms.model.PluginConfiguration;

/**
 * This interface declares the service for the entity
 * PluginConfiguration. For this service it is <b>not</b> possible call
 * the <code>get(String name)</code> method successfully.
 * 
 * @author Olaf Krause
 * @version $Revision: 442 $ provided by $Author: okrause $
 */
@Local
public interface PluginConfigurationService
    extends BasicService<PluginConfiguration>
{
    // ----------------------------------------------------------------
    // set of individual methods
    // ----------------------------------------------------------------

    /**
     * Returns the current mode of the application.
     * 
     * @return the current mode
     */
    String getMode();

    /**
     * Sets the current mode of the application.
     * 
     * @param mode the new mode
     */
    void setMode(String mode);

    /**
     * Returns a list of available modes, this application can be
     * switched to.
     * 
     * @return list of modes
     */
    List<String> getModes();

    /**
     * Returns a PluginConfiguration with the specified name, using the
     * current mode of the application.
     * 
     * @param client the pursessor of the configuration; may be null
     * @param pluginName the name of the plugin
     * @param defaultClass the default class of the plugin
     * @return the PluginConfiguration object with the specified name
     */
    PluginConfiguration get(
        Client client,
        String pluginName,
        String defaultClass) throws EntityNotFoundException;

    /**
     * Returns a PluginConfiguration with the specified name under the
     * specified mode.
     * 
     * @param client the pursessor of the configuration; may be null
     * @param pluginName the name of the plugin
     * @param defaultClass the default class of the plugin
     * @param mode the application mode
     * @return the PlugunConfiguration object
     */
    PluginConfiguration get(
        Client client,
        String pluginName,
        String defaultClass,
        String mode) throws EntityNotFoundException;

    /**
     * Returns a list of plugin names, configured in the system.
     * 
     * @return a list of plugin names
     */
    List<String> getPlugins();

    /**
     * The combination of param client, pluginName and mode must be
     * unique in the system. This constraint will be checked before
     * creation.
     * 
     * @param client the client for which the plugin should be executed.
     * @param pluginName the name of the plugin.
     * @param pluginClass a stateless session bean that should be
     *            executed.
     * @param mode only execute this plugin when system is in specified
     *            mode.
     * @return a persistent instance of PluginConfiguration.
     * @throws UniqueConstraintViolatedException if the constraint
     *             described above is violated.
     * @throws NullPointerException if any of the arguments is null.
     */
    PluginConfiguration createPluginConfiguration(
        Client client,
        String pluginName,
        String pluginClass,
        String mode) throws UniqueConstraintViolatedException;

    /**
     * Returns the String, which can be used to lookup the specified
     * pattern.
     * 
     * @return the lookup path
     */
    String getLookupPath(String pluginName);

    /**
     * Returns the current lookup pattern The place, where the looked up
     * bean is inserted, is marked indicated by a question mark Example:
     * <code>myWMS/?/local</code>
     * 
     * @return the current lookup pattern
     */
    String getLookupPattern();

    /**
     * Sets the pattern used to lookup plugins. The place, where the
     * looked up bean is inserted, is marked indicated by a question
     * mark Example: <code>myWMS/?/local</code>
     * 
     * @param lookupPattern the new lookup pattern
     */
    void setLookupPattern(String lookupPattern);
}
