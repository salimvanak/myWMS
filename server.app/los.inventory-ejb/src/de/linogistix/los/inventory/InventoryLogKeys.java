/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.inventory;

/**
 *
 * @author trautm
 */
public enum InventoryLogKeys {
    CREATED_STOCKUNIT,
    CREATED_ORDERREQUEST,
    CREATED_PICKINGREQUEST,
    FINISHED_PICKINGREQUEST,
    HANDLE_UNEXPECTEDNULL,
    PENDING_PICKINGREQUEST, // Waits for positions after unexpected null
    CREATED_EXTINGUISHORDER
}
