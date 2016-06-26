/*
 * Copyright (c) 2006 by Fraunhofer IML, Dortmund.
 * All rights reserved.
 *
 * Project: myWMS
 */
package org.mywms.plugin;

import javax.ejb.Local;

import org.mywms.model.Client;

/**
 * The PluginFactory resolves plugins according to the current
 * configuration. Well known myWMS plugins are:
 * <ul>
 * <li> ItemDataNumberPlugin
 * <li> UnitLoadIdPlugin
 * <li> RequestNumberPlugin
 * </ul>
 * 
 * @author <a href="http://community.mywms.de/developer.jsp">Olaf Krause</a>
 * @version $Revision: 442 $ provided by $Author: okrause $
 */
@Local
public interface PluginFactory {
    /**
     * Resolves the plugin with the specified name.
     * 
     * @param client the client pursessing the plugin; maybe null
     * @param pluginName the name of the plugin to resolve
     * @param defaultClassName the default class name, implementing the
     *            plugin
     * @return the plugin
     * @throws PluginException if a plugin could not be resolved
     */
    Object resolvePlugin(
        Client client,
        String pluginName,
        String defaultClassName) throws PluginException;
}
