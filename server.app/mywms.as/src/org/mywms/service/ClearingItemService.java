/*
 * Copyright (c) 2008 by Fraunhofer IML, Dortmund.
 * All rights reserved.
 *
 * Project: mywms.as
 */
package org.mywms.service;

import java.io.NotSerializableException;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.Local;
import javax.persistence.Query;

import org.mywms.model.ClearingItem;
import org.mywms.model.ClearingItemOption;
import org.mywms.model.Client;

/**
 * This interface declares the service for the entity ClearingItem.
 * 
 * @author Olaf Krause
 * @version $Revision: 720 $ provided by $Author: mkrane $
 */
@Local
public interface ClearingItemService
    extends BasicService<ClearingItem>
{
    // ----------------------------------------------------------------
    // set of individual methods
    // ----------------------------------------------------------------

    /**
     * Creates a new ClearingItem using the specified arguments to
     * initialize the immutable properties.
     */
	@SuppressWarnings("rawtypes")
	public ClearingItem create(
			Client client, String host, String source, String user,
			String messageResourceKey, String shortMessageResourceKey,
			String resourceBundleName, Class bundleResolver, 
			Object[] shortMessageParameters,
			Object[] messageParameters, ArrayList<ClearingItemOption> options)
			throws NotSerializableException ;
	
    /**
     * Returns a list of ClearingItems, matching the specified
     * parameters. The value null for a parameter will not restriv the
     * result set.
     * 
     * @param client the client to be found in the logs
     * @param host the host to be found in the logs
     * @param source the source to be found in the logs
     * @param user the user to be found in the logs
     * @param type the type to be found in the logs
     * @param limit the maximum amount of rows returned
     * @return a list of matching logs
     */
    List<ClearingItem> getChronologicalList(
        String client,
        String host,
        String source,
        String user,
        int limit);

    /**
     * Returns a list of ClearingItems, which was not dealt(Solution is
     * null), according to the specified parameters. The value null for
     * a parameter will not restriv the result set.
     * 
     * @param client the client to be found in the logs
     * @param host the host to be found in the logs
     * @param source the source to be found in the logs
     * @param user the user to be found in the logs
     * @param type the type to be found in the logs
     * @param limit the maximum amount of rows returned
     * @return a list of matching logs
     */
    List<ClearingItem> getNondealChronologicalList(
        String client,
        String host,
        String source,
        String user,
        int limit);

    /**
     * Returns a list of sources ever logged.
     * 
     * @param client the client of the caller; maybe null
     * @return list of sources
     */
    List<String> getSources(Client client);

    /**
     * Returns a list of hosts ever logged.
     * 
     * @param client the client of the caller; maybe null
     * @return list of hosts
     */
    List<String> getHosts(Client client);

    /**
     * Returns a list of users ever logged.
     * 
     * @param client the client of the caller; maybe null
     * @return list of users
     */
    List<String> getUser(Client client);

}
