/*
 * Copyright (c) 2006 by Fraunhofer IML, Dortmund.
 * All rights reserved.
 *
 * Project: myWMS
 */
package org.mywms.service;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.apache.log4j.Logger;
import org.mywms.globals.ServiceExceptionKey;
import org.mywms.model.Client;
import org.mywms.model.PluginConfiguration;

/**
 * This interface declares the service for the entity
 * PluginConfiguration.
 * 
 * @author Olaf Krause
 * @version $Revision: 442 $ provided by $Author: okrause $
 */
@Stateless
public class PluginConfigurationServiceBean
    extends BasicServiceBean<PluginConfiguration>
    implements PluginConfigurationService
{
    private static final Logger log =
        Logger.getLogger(PluginConfigurationServiceBean.class.getName());

    @EJB
    private ClientService clientService;

    /**
     * @see org.mywms.service.PluginConfigurationService#createPluginConfiguration(Client,
     *      String, String, String)
     */
    @SuppressWarnings("unchecked")
    public PluginConfiguration createPluginConfiguration(
        Client client,
        String pluginName,
        String pluginClass,
        String mode) throws UniqueConstraintViolatedException
    {
        Query query =
            manager.createQuery("SELECT pc FROM "
                + PluginConfiguration.class.getSimpleName()
                + " pc "
                + "WHERE pc.client=:cl AND pc.pluginName=:pName "
                + "AND pc.mode=:mo");
        query.setParameter("cl", client);
        query.setParameter("pName", pluginName);
        query.setParameter("mo", mode);

        List<PluginConfiguration> li = query.getResultList();

        if (li.size() > 0) {
            throw new UniqueConstraintViolatedException(
                ServiceExceptionKey.PLUGINCONFIGURATION_ALREADY_EXISTS);
        }

        PluginConfiguration pc = new PluginConfiguration();
        pc.setClient(client);
        pc.setPluginName(pluginName);
        pc.setPluginClass(pluginClass);
        pc.setMode(mode);

        manager.persist(pc);
        manager.flush();

        return pc;
    }

    /**
     * @see org.mywms.service.PluginConfigurationService#getMode()
     */
    @SuppressWarnings("unchecked")
    public String getMode() {
        PluginConfiguration pluginConfiguration = null;

        Query query =
            manager.createQuery("SELECT p FROM PluginConfiguration p "
                + "WHERE p.pluginName=:pluginName "
                + "AND p.mode=:mode");

        query.setParameter(
            "pluginName",
            PluginConfiguration.RESERVED_PLUGIN_MODE);
        query.setParameter("mode", PluginConfiguration.DEFAULT_MODE);

        List<PluginConfiguration> pcl = query.getResultList();
        if (pcl.size() == 0) {
            // create a new entity
            try {
                pluginConfiguration =
                    createPluginConfiguration(
                        clientService.getSystemClient(),
                        PluginConfiguration.RESERVED_PLUGIN_MODE,
                        PluginConfiguration.DEFAULT_MODE,
                        PluginConfiguration.DEFAULT_MODE);
            }
            catch (UniqueConstraintViolatedException ue) {
                log.error(ue);
            }

        }
        else {
            // use the matching entity
            pluginConfiguration = pcl.get(0);
        }

        // return the mode
        return pluginConfiguration.getPluginClass();
    }

    /**
     * @see org.mywms.service.PluginConfigurationService#setMode(java.lang.String)
     */
    @SuppressWarnings("unchecked")
    public void setMode(String mode) {
        PluginConfiguration pluginConfiguration = null;

        Query query =
            manager.createQuery("SELECT p FROM PluginConfiguration p "
                + "WHERE p.pluginName=:pluginName "
                + "AND p.mode=:mode ");

        query.setParameter(
            "pluginName",
            PluginConfiguration.RESERVED_PLUGIN_MODE);
        query.setParameter("mode", PluginConfiguration.DEFAULT_MODE);

        List<PluginConfiguration> pcl = query.getResultList();
        if (pcl.size() == 0) {
            // create a new entity
            try {
                pluginConfiguration =
                    createPluginConfiguration(
                        clientService.getSystemClient(),
                        PluginConfiguration.RESERVED_PLUGIN_MODE,
                        mode,
                        PluginConfiguration.DEFAULT_MODE);
            }
            catch (UniqueConstraintViolatedException ue) {
                log.error("unexpected error occured during mode switching:");
                log.error(ue);
            }
        }
        else {
            // use the matching entity
            pluginConfiguration = pcl.get(0);
        }

        // set the mode
        pluginConfiguration.setPluginClass(mode);
    }

    /**
     * @see org.mywms.service.PluginConfigurationService#get(org.mywms.model.Client,
     *      java.lang.String, java.lang.String)
     */
    public PluginConfiguration get(
        Client client,
        String pluginName,
        String defaultClass) throws EntityNotFoundException
    {
        Query query;
        query =
            manager.createQuery("SELECT p FROM PluginConfiguration p "
                + "WHERE p.pluginName=:pluginName "
                + "AND p.mode=:mode "
                + "AND p.client=:client");

        query.setParameter("pluginName", pluginName);
        query.setParameter("mode", getMode());
        query.setParameter("client", client);

        PluginConfiguration pluginConfiguration = null;
        try {
            pluginConfiguration = (PluginConfiguration) query.getSingleResult();
        }
        catch (NoResultException ex) {
            try {
                pluginConfiguration =
                    createPluginConfiguration(
                        client,
                        pluginName,
                        defaultClass,
                        getMode());
            }
            catch (UniqueConstraintViolatedException ue) {
                log.error(ue);
            }

        }

        return pluginConfiguration;
    }

    /**
     * @see org.mywms.service.PluginConfigurationService#get(org.mywms.model.Client,
     *      java.lang.String, java.lang.String)
     */
    public PluginConfiguration get(
        Client client,
        String pluginName,
        String defaultClass,
        String mode) throws EntityNotFoundException
    {
        Query query =
            manager.createQuery("SELECT p FROM PluginConfiguration p "
                + "WHERE p.pluginName=:pluginName "
                + "AND p.mode=:mode "
                + "AND p.client=:client");

        query.setParameter("pluginName", pluginName);
        query.setParameter("mode", mode);
        query.setParameter("client", client);

        PluginConfiguration pluginConfiguration = null;
        try {
            pluginConfiguration = (PluginConfiguration) query.getSingleResult();
        }
        catch (NoResultException ex) {
            try {
                pluginConfiguration =
                    createPluginConfiguration(
                        client,
                        pluginName,
                        defaultClass,
                        mode);
            }
            catch (UniqueConstraintViolatedException ue) {
                log.error(ue);
            }

        }

        return pluginConfiguration;
    }

    /**
     * @see org.mywms.service.PluginConfigurationService#getModes()
     */
    @SuppressWarnings("unchecked")
    public List<String> getModes() {
        Query query =
            manager.createQuery("SELECT p.mode FROM PluginConfiguration p "
                + "GROUP BY p.mode "
                + "ORDER BY p.mode");
        return (List<String>) query.getResultList();
    }

    /**
     * @see org.mywms.service.PluginConfigurationService#getPlugins()
     */
    @SuppressWarnings("unchecked")
    public List<String> getPlugins() {
        Query query =
            manager.createQuery("SELECT p.name FROM PluginConfiguration p "
                + "GROUP BY p.name "
                + "ORDER BY p.name");
        return (List<String>) query.getResultList();
    }

    /**
     * @see org.mywms.service.PluginConfigurationService#getLookupPath(java.lang.String)
     */
    public String getLookupPath(String pluginName) {
        String lookupPattern = getLookupPattern();
        log.info("getLookupPath(): lookup pattern is: " + lookupPattern);

        String lookupPath = lookupPattern.replaceFirst("\\?", pluginName);
        log.info("getLookupPath(): lookup pattern is: "
            + lookupPattern
            + "; path is: "
            + lookupPath);

        return lookupPath;
    }

    /**
     * @see org.mywms.service.PluginConfigurationService#getLookupPattern()
     */
    @SuppressWarnings("unchecked")
    public String getLookupPattern() {
        PluginConfiguration pluginConfiguration = null;

        Query query =
            manager.createQuery("SELECT p FROM PluginConfiguration p "
                + "WHERE p.pluginName=:pluginName "
                + "AND p.mode=:mode");

        query.setParameter(
            "pluginName",
            PluginConfiguration.RESERVED_PLUGIN_LOOKUP_PATTERN);
        query.setParameter("mode", PluginConfiguration.DEFAULT_MODE);

        List<PluginConfiguration> pcl = query.getResultList();
        if (pcl.size() == 0) {
            // create a new entity
            try {
                pluginConfiguration =
                    createPluginConfiguration(
                        clientService.getSystemClient(),
                        PluginConfiguration.RESERVED_PLUGIN_LOOKUP_PATTERN,
                        PluginConfiguration.DEFAULT_LOOKUP_PATTERN,
                        PluginConfiguration.DEFAULT_MODE);
            }
            catch (UniqueConstraintViolatedException ue) {
                log.error(ue);
            }

        }
        else {
            // use the matching entity
            pluginConfiguration = pcl.get(0);
        }

        log.info("getLookupPattern(): lookup pattern is: "
            + pluginConfiguration.getPluginClass());

        // return the pattern
        return pluginConfiguration.getPluginClass();
    }

    /**
     * @see org.mywms.service.PluginConfigurationService#setLookupPattern(java.lang.String)
     */
    @SuppressWarnings("unchecked")
    public void setLookupPattern(String lookupPattern) {
        PluginConfiguration pluginConfiguration = null;

        Query query =
            manager.createQuery("SELECT p FROM PluginConfiguration p "
                + "WHERE p.pluginName=:pluginName "
                + "AND p.mode=:mode ");

        query.setParameter(
            "pluginName",
            PluginConfiguration.RESERVED_PLUGIN_LOOKUP_PATTERN);
        query.setParameter("mode", PluginConfiguration.DEFAULT_MODE);

        List<PluginConfiguration> pcl = query.getResultList();
        if (pcl.size() == 0) {
            // create a new entity
            try {
                pluginConfiguration =
                    createPluginConfiguration(
                        clientService.getSystemClient(),
                        PluginConfiguration.RESERVED_PLUGIN_LOOKUP_PATTERN,
                        lookupPattern,
                        PluginConfiguration.DEFAULT_MODE);
            }
            catch (UniqueConstraintViolatedException ue) {
                log.error("unexpected error occured during mode switching:");
                log.error(ue);
            }
        }
        else {
            // use the matching entity
            pluginConfiguration = pcl.get(0);
        }

        // set the pattern
        pluginConfiguration.setPluginClass(lookupPattern);

        log.info("setLookupPattern(): new lookup pattern is: "
            + pluginConfiguration.getPluginClass());
    }
}
