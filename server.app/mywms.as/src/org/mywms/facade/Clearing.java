/*
 * Copyright (c) 2008 by Fraunhofer IML, Dortmund.
 * All rights reserved.
 *
 * Project: mywms.as
 */
package org.mywms.facade;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.Remote;

import org.mywms.model.ClearingItemOption;

/**
 * @author lfu, aelbaz
 */
@Remote
public interface Clearing
    extends BasicFacade
{

    /**
     * @param to
     * @throws ClearingException
     */
    public void setClearingItem(ClearingItemTO to) throws ClearingException;

    /**
     * Returns a list of sources ever logged.
     * 
     * @return list of sources
     */
    public List<String> getSources();

    /**
     * Returns a list of users ever logged.
     * 
     * @return list of users
     */
    public List<String> getUsers();

    /**
     * Returns a list of mandants ever logged.
     * 
     * @return list of mandants
     */
    public List<String> getMandants(String client) throws ClearingException;
    
    /**
     * Returns a list of ClearingTOs, matching the specified parameters.
     * The value null for a parameter will not restriv the result set.
     * 
     * @param client the client to be found in the logs
     * @param host the host to be found in the logs
     * @param source the source to be found in the logs
     * @param user the user to be found in the logs
     * @param type the type to be found in the logs
     * @param limit the maximum amount of rows returned
     * @return a list of matching logs
     */
    public List<ClearingItemTO> getChronologicalList(
        String client,
        String host,
        String source,
        String user,
        Integer limit) throws ClearingException;

    /**
     * Creates a new ClearingItem using the specified arguments to
     * initialize the immutable properties. This interface is make up
     * for testing in GUI. It will be later deleted.
     * 
     * @param client the client for who the event occured
     * @param host the host where the event occured
     * @param source the process/state when the event occured
     * @param user the user, who operated during the event
     * @param type the type of the event
     * @param message the default message to be used
     * @param messageResourceKey a resource key used for the
     *            internationalized message
     * @return the new ClearingItem
     */
    public void createClearingItem(
        String client,
        String host,
        String source,
        String user,
        String messageResourceKey,
        String shortMessageResourceKey,
        String resourceBundleName,
        Class bundleResolver,
        Object[] shortMessageParameters,
        Object[] messageParameters,
        ArrayList<ClearingItemOption> options) throws ClearingException;

}
