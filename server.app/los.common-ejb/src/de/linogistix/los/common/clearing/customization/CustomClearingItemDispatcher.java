/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.common.clearing.customization;

import javax.ejb.Local;

import org.mywms.facade.FacadeException;
import org.mywms.model.ClearingItem;

@Local
public interface CustomClearingItemDispatcher {

	public void dispatchClearingItem(ClearingItem clearing) throws FacadeException;
}
