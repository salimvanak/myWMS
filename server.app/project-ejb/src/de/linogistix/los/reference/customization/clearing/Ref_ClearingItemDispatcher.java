/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.reference.customization.clearing;

import javax.ejb.Stateless;

import org.mywms.model.ClearingItem;

import de.linogistix.los.common.clearing.customization.CustomClearingItemDispatcher;
import de.linogistix.los.common.clearing.exception.LOSClearingException;

@Stateless
public class Ref_ClearingItemDispatcher implements CustomClearingItemDispatcher {

	public void dispatchClearingItem(ClearingItem clearing)
			throws LOSClearingException {
		// TODO No clearing to be done yet

	}

}
