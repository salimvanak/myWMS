/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.mywms.model;

/**
 *
 * @author trautm
 */
public enum PickingWithdrawalType {
    
    /**
     * Default: Pick from identified StockUnit 
     */
    UNORDERED_FROM_STOCKUNIT,
    /**
     * Take whole UnitLoad
     */
    TAKE_UNITLOAD
    
}
