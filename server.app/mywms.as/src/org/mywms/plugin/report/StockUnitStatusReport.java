/**
 * 
 */
package org.mywms.plugin.report;

import javax.ejb.Local;

import org.mywms.facade.ReportsException;
import org.mywms.model.Client;

/**
 * Returns a report, containing the StockUnits, ordered by ItemData and
 * the StorageLocations (and UnitLoads) containing the StockUnits.
 * (German: bestandsbezogener Lagerspiegel)
 * 
 * @see org.mywms.facade.Reports#getStockUnitStatusReport(String,
 *      String)
 * @author Olaf Krause
 * @version $Revision: 487 $ provided by $Author: okrause $
 */
@Local
public interface StockUnitStatusReport {

    /**
     * Returns a report, containing the StockUnits, ordered by ItemData
     * and the StorageLocations (and UnitLoads) containing the
     * StockUnits. (German: bestandsbezogener Lagerspiegel)
     * 
     * @param client the client the report refers to; if no client is
     *            specified (client==null) all available data is
     *            reported
     * @param locale the locale to be used to create the report
     * @return the PDF based report
     * @throws ReportsException if the specified client is invalid
     * @see org.mywms.facade.Reports#getStockUnitStatusReport(String,
     *      String)
     */
    public abstract byte[] createReport(Client client, String locale)
        throws ReportsException;

}