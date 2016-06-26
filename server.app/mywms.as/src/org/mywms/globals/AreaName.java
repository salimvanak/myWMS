/*
 * Copyright (c) 2007 by Fraunhofer IML, Dortmund.
 * All rights reserved.
 *
 * Project: myWMS
 */
package org.mywms.globals;

/**
 * Enumerates some areas, a warehous could utilze. Feel free to add more
 * areas in your special applications or to leave the ones proposed here
 * unused.
 * 
 * @author Olaf Krause
 * @version $Revision: 442 $ provided by $Author: okrause $
 */
public enum AreaName {
    GOODS_IN,
    GOODS_OUT,
    GOODS_IN_OUT,
    STORE,
    TOLL,
    QUALITY_ASSURANCE,
    REPLENISHMENT,
    QUARANTINE,
    PICKING;

    public String toString() {
        switch (this) {
        case GOODS_IN:
            return GOODS_IN_STR;
        case GOODS_OUT:
            return GOODS_OUT_STR;
        case GOODS_IN_OUT:
            return GOODS_IN_OUT_STR;
        case STORE:
            return STORE_STR;
        case TOLL:
            return TOLL_STR;
        case QUALITY_ASSURANCE:
            return QUALITY_ASSURANCE_STR;
        case REPLENISHMENT:
            return REPLENISHMENT_STR;
        case QUARANTINE:
            return QUARANTINE_STR;
        case PICKING:
            return PICKING_STR;
        default:
            return "<undefined>";
        }
    }

    public static final String GOODS_IN_STR = "Goods In";
    public static final String GOODS_OUT_STR = "Goods Out";
    public static final String GOODS_IN_OUT_STR = "Goods In/Out";
    public static final String STORE_STR = "Store";
    public static final String TOLL_STR = "Toll Store";
    public static final String QUALITY_ASSURANCE_STR = "Quality Assurance";
    public static final String REPLENISHMENT_STR = "Replenishment";
    public static final String QUARANTINE_STR = "Quarantine";
    public static final String PICKING_STR = "Picking";
}