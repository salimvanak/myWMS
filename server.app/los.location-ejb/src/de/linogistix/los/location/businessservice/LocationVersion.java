/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.location.businessservice;

import java.util.Locale;

import org.mywms.util.BundleHelper;

import de.linogistix.los.location.res.BundleResolver;


public class LocationVersion {
	
	private static final String module = "myWMS-LOS Location";
	private static String version = "1.8.0";
	private static final String resourceBundle = "de.linogistix.los.location.res.VersionBundle";
	
    
    public static String getInfo() {
    	return "<b>" + module + " " + version + "</b> , Build " + getBuild();
    }
    
    public static String getModule() {
    	return module; 
    }
    
    public static String getVersion() {
    	return version; 
    }
    
    public static String getBuild() {
        String date = getProperty("build.date");
        String time = getProperty("build.time");
        
        return date + time;
    }
    
	private static String getProperty( String key ) {
       String prop = BundleHelper.resolve(key, key, new Object[]{}, resourceBundle, BundleResolver.class, Locale.getDefault());
       return prop == null ? "" : prop.trim();
	}
}
