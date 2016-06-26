/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.common.exception;

import javax.ejb.ApplicationException;

import org.mywms.facade.FacadeException;

@ApplicationException(rollback=true)
public class LOSEventException extends FacadeException {
	
	private static final long serialVersionUID = 1L;

	public static final String KEY = "BusinessException.LOSEventException";
	public LOSEventException(){
		super("LOSEventException", KEY, new Object[0]);
		setBundleResolver(de.linogistix.los.res.BundleResolver.class);
	}
	
	public LOSEventException(String message, String key, String param){
		super(message, key, new Object[]{param});
		setBundleResolver(de.linogistix.los.res.BundleResolver.class);
	}
}
