/*
 * Copyright (c) 2006 by Fraunhofer IML, Dortmund.
 * All rights reserved.
 *
 * Project: myWMS
 */
package org.mywms.plugin;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.log4j.Logger;
import org.mywms.model.Client;
import org.mywms.model.PluginConfiguration;
import org.mywms.service.EntityNotFoundException;
import org.mywms.service.PluginConfigurationService;

/**
 * @see org.mywms.plugin.PluginFactory
 * @author <a href="http://community.mywms.de/developer.jsp">Olaf Krause</a>
 * @version $Revision: 442 $ provided by $Author: okrause $
 */
@Stateless
public class PluginFactoryBean
    implements PluginFactory
{
    private static final Logger log =
        Logger.getLogger(PluginFactoryBean.class.getName());

    @EJB
    PluginConfigurationService pluginService;

    /**
     * @see org.mywms.plugin.PluginFactory#resolvePlugin(Client, String,
     *      String)
     */
    public Object resolvePlugin(
        Client client,
        String pluginName,
        String defaultClassName) throws PluginException
    {
        try {
            // resolve classname
            PluginConfiguration config;
            config = pluginService.get(client, pluginName, defaultClassName);

            String pluginClassname = config.getPluginClass();

            // create a new class
            Context initialContext = new InitialContext();
            String lookupPath = pluginService.getLookupPath(pluginClassname);

            log.info("resolvePlugin(): try to lookup " + lookupPath);

            Object obj = initialContext.lookup(lookupPath);

            return obj;
        }
        catch (NamingException e) {
            throw new PluginException(e);
        }
        catch (EntityNotFoundException e) {
            throw new PluginException(e);
        }
    }
}
