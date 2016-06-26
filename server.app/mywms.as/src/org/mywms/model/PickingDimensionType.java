/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.mywms.model;

/**
 * Dimension of movement during picking
 *  
 * @author trautm
 */
public enum PickingDimensionType {
    /**
     * On the ground (block storage)
     */
    ONE_DIMENSIONAL,
    /**
     * within rack (e.g. High Bay Rack/ ASRS)
     */
    TWO_DIMENSIONAL,
    /**
     * free (e.g. crane)
     */
    THREE_DIMENSIONAL
}
