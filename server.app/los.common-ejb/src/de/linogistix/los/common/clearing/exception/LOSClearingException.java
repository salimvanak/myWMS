/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.common.clearing.exception;

import javax.ejb.ApplicationException;

import org.mywms.facade.FacadeException;

@ApplicationException(rollback=true)
public class LOSClearingException extends FacadeException {

	private static final long serialVersionUID = 1L;

	private static String resourceBundle = "de.linogistix.los.res.Bundle";
	
	private LOSClearingExceptionKey clearingExceptionKey;
	
	public LOSClearingException(LOSClearingExceptionKey key, Object[] parameters){
		super("", key.name(), parameters, resourceBundle);
		setBundleResolver(de.linogistix.los.res.BundleResolver.class);
		clearingExceptionKey = key;
	}

	public LOSClearingExceptionKey getClearingExceptionKey() {
		return clearingExceptionKey;
	}
}
