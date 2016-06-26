/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.inventory.facade;

import javax.ejb.Remote;

import org.mywms.facade.FacadeException;

@Remote
public interface InventoryCleanupFacade {

	void cleanup() throws FacadeException;
}
