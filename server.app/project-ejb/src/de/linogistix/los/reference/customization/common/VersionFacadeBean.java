/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.reference.customization.common;

import javax.ejb.Stateless;

import de.linogistix.los.common.businessservice.CommonVersion;
import de.linogistix.los.common.facade.VersionFacade;
import de.linogistix.los.inventory.businessservice.InventoryVersion;
import de.linogistix.los.location.businessservice.LocationVersion;
import de.linogistix.los.reference.businessservice.ProjectVersion;
import de.linogistix.los.stocktaking.businessservice.StocktakingVersion;
import de.linogistix.mobileserver.MobileVersion;

@Stateless
public class VersionFacadeBean implements VersionFacade {


	public String getInfo() {
    	
    	
    	return ProjectVersion.getInfo() + "<br>" +
    			" <li>" + CommonVersion.getInfo() + "</li>" +
    			" <li>" + InventoryVersion.getInfo() + "</li>" +
    			" <li>" + LocationVersion.getInfo() + "</li>" +
				" <li>" + StocktakingVersion.getInfo() + "</li>" +
				" <li>" + MobileVersion.getInfo() + "</li>";
	}

}
