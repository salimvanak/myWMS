/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.mywms.model;

/**
 * Static: No Movement of supply StockUnit before picking
 * Dynamic: Movement of supply StockUnit before picking
 * Centralized: Picking at defined points
 * Decentralized: Picking everywhere within the warehouse
 * 
 * STATIC_DECENTRALIZED describes well-known "Mann zur Ware".
 * 
 * @author trautm
 */
public enum PickingSupplyType {
    STATIC_CENTRALIZED,
    STATIC_DECENTRALIZED,
    DYNAMIC_CENTRALIZED,
    DYNAMIC_DECENTRALIZED,
}
