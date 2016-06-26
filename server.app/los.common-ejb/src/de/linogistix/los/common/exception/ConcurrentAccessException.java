/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.common.exception;

import javax.ejb.ApplicationException;

/**
 * This Exception is thrown if two user try to access the same resource or data at the same time.
 * It will cause a rollback of the transaction.
 * 
 * @author Jordan
 *
 */
@ApplicationException(rollback=true)
@Deprecated
public class ConcurrentAccessException extends Exception {

	private static final long serialVersionUID = 1L;
}
