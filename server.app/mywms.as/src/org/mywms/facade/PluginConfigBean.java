/*
 * Copyright (c) 2006 by Fraunhofer IML, Dortmund.
 * All rights reserved.
 *
 * Project: myWMS
 */
package org.mywms.facade;

import java.util.List;

import javax.annotation.security.PermitAll;
import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.log4j.Logger;
import org.mywms.globals.DefaultMessage;
import org.mywms.model.Client;
import org.mywms.model.PluginConfiguration;
import org.mywms.service.ClientService;
import org.mywms.service.EntityNotFoundException;
import org.mywms.service.PluginConfigurationService;
import org.mywms.service.UniqueConstraintViolatedException;

/**
 * This fassade declares the interface to operate the configuration of
 * plugins. Per default the mode called "default" is allways available.
 * 
 * @author Olaf Krause
 * @version $Revision: 740 $ provided by $Author: mkrane $
 */
@Stateless
@PermitAll
public class PluginConfigBean
    implements PluginConfig
{
    @SuppressWarnings("unused")
    private static final Logger log =
        Logger.getLogger(PluginConfigBean.class.getName());

    @EJB
    private PluginConfigurationService pluginConfigurationService;

    @EJB
    private ClientService clientService;

    /**
     * @see org.mywms.facade.PluginConfig#getModes()
     */
    public String[] getModes() {
        List<String> modes = pluginConfigurationService.getModes();
        return (String[]) modes.toArray(new String[modes.size()]);
    }

    /**
     * @see org.mywms.facade.PluginConfig#getCurrentMode()
     */
    public String getCurrentMode() {
        return pluginConfigurationService.getMode();
    }

    /**
     * @see org.mywms.facade.PluginConfig#setMode(java.lang.String)
     */
    public void setMode(String mode) throws PluginConfigException {
        pluginConfigurationService.setMode(mode);
    }

    /**
     * @see org.mywms.facade.PluginConfig#setPlugin(java.lang.String,
     *      java.lang.String, java.lang.String, java.lang.String)
     */
    public void setPlugin(
        String clientNumber,
        String mode,
        String pluginName,
        String pluginClassname) throws PluginConfigException
    {
        PluginConfiguration config = null;
        Client client;

        // try to resolve the client
        if (clientNumber != null) {
            client = clientService.getByNumber(clientNumber);
            
            if(client == null){
            	throw new PluginConfigException(
                        "the specified client could not be found",
                        DefaultMessage.PluginConfig_ClientNotFound,
                        new Object[0]);
            }
        }
        else {
            throw new PluginConfigException(
                "the specified client could not be found",
                DefaultMessage.PluginConfig_ClientNotFound,
                new Object[0]);
        }
        

        // try to resolve the config entity
        try {
            // load existing entity
            config = pluginConfigurationService.get(client, pluginName, mode);
        }
        catch (EntityNotFoundException ex) {
            // create new entity
            try {
                config =
                    pluginConfigurationService.createPluginConfiguration(
                        client,
                        pluginName,
                        pluginClassname,
                        mode);
            }
            catch (UniqueConstraintViolatedException ue) {
                // this exception must not be thrown, because the
                // specified
                // entity was not found before
                throw new PluginConfigException(
                    "the specified plugin could not be set",
                    DefaultMessage.PluginConfig_pluginNotSet,
                    new Object[0]);
            }

            // config.setMode(mode);
            // config.setPluginName(pluginName);
            // config.setClient(client);
        }

        config.setPluginName(pluginClassname);
    }

    /**
     * @see org.mywms.facade.PluginConfig#getPlugin(java.lang.String,
     *      java.lang.String, java.lang.String)
     */
    public String getPlugin(String clientNumber, String mode, String plugin)
        throws PluginConfigException
    {
        PluginConfiguration config;
        try {
            Client client;
            if (clientNumber != null) {
                client = clientService.getByNumber(clientNumber);
            }
            else {
                throw new PluginConfigException(
                    "the specified client could not be found",
                    DefaultMessage.PluginConfig_ClientNotFound,
                    new Object[0]);
            }
            // load existing entity
            config = pluginConfigurationService.get(client, plugin, mode);
            return config.getPluginClass();
        }
        catch (EntityNotFoundException ex) {
            throw new PluginConfigException(
                "the specified plugin could not be located",
                DefaultMessage.PluginConfig_getPluginFailed,
                new Object[0]);
        }
    }

    /**
     * @see org.mywms.facade.PluginConfig#getPlugins()
     */
    public String[] getPlugins() throws PluginConfigException {
        return pluginConfigurationService.getPlugins().toArray(new String[0]);
    }

    /**
     * @see org.mywms.facade.PluginConfig#getLookupPattern()
     */
    public String getLookupPattern() throws PluginConfigException {
        return pluginConfigurationService.getLookupPattern();
    }

    /**
     * @see org.mywms.facade.PluginConfig#setLookupPattern(java.lang.String)
     */
    public void setLookupPattern(String lookupPattern)
        throws PluginConfigException
    {
        if (lookupPattern.indexOf("?") < 0) {
            throw new PluginConfigException(
                "the pattern is missing the bean name indicator",
                DefaultMessage.PluginConfig_indicatorNotFound,
                new Object[0]);
        }

        pluginConfigurationService.setLookupPattern(lookupPattern);
    }
}
