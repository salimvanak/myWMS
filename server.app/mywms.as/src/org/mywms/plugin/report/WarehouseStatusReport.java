/*
 * Copyright (c) 2006 by Fraunhofer IML, Dortmund.
 * All rights reserved.
 *
 * Project: myWMS
 */
package org.mywms.plugin.report;

import javax.ejb.Local;

import org.mywms.facade.ReportsException;
import org.mywms.model.Client;

/**
 * /** Creates a report, containing the StorageLocations and the
 * StockUnits (and UnitLoads) stored onto them. (German: ortsbezogener
 * Lagerspiegel)
 * 
 * @author Olaf Krause
 * @version $Revision: 487 $ provided by $Author: okrause $
 */
@Local
public interface WarehouseStatusReport {

    /**
     * Creates a report, containing the StorageLocations and the
     * StockUnits (and UnitLoads) stored onto them. (German:
     * ortsbezogener Lagerspiegel)
     * 
     * @param client the client the report refers to; if no client is
     *            specified (client==null) all available data is
     *            reported
     * @param locale the locale to be used to create the report
     * @return the PDF based report
     * @throws ReportsException if the specified client is invalid
     * @see org.mywms.facade.Reports#getWarehouseStatusReport(String,
     *      String)
     */
    public abstract byte[] createReport(Client client, String locale)
        throws ReportsException;

}