/*
 * Copyright (c) 2009-2012 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.inventory.query;


import java.util.List;

import javax.ejb.Remote;

import org.mywms.model.Client;

import de.linogistix.los.inventory.model.LOSPickingPosition;
import de.linogistix.los.query.BusinessObjectQueryRemote;


/**
 * @author krane
 *
 */
@Remote
public interface LOSPickingPositionQueryRemote extends BusinessObjectQueryRemote<LOSPickingPosition>{ 

	public List<LOSPickingPosition> queryAll( Client client );

	/**
	 * Returns a list of picking orders associated with the given customerOrderNumber
	 * @param customerOrderNumber the customer order number
	 * @return a list of LOSPickingOrders
	 */
	public List<LOSPickingPosition> getByCustomerOrder(String customerOrderNumber);

	/**
	 * Returns a list of picking orders associated with the given customerOrderNumberPositions
	 * @param customerOrderNumber the unique identity of the customer order position.
	 * @return a list of LOSPickingOrders
	 */
//	public List<LOSPickingPosition> getByCustomerOrderPos(String customerOrderNumberPos);

}
