/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.customization;

import de.linogistix.los.common.businessservice.CommonVersion;
import de.linogistix.los.common.facade.VersionFacade;


public class VersionFacadeBean implements VersionFacade {
	
	
	public String getInfo() {
    	String commonVer = CommonVersion.getInfo(); 
    	return commonVer;
	}
	
}
