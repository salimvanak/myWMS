/*
 * Copyright (c) 2006 by Fraunhofer IML, Dortmund.
 * All rights reserved.
 *
 * Project: myWMS
 */
package org.mywms.plugin.report;

import javax.ejb.Stateless;

import org.mywms.facade.ReportsException;
import org.mywms.model.Client;

/**
 * @see org.mywms.facade.Reports#getStockUnitStatusReport(String,
 *      String)
 * @author Olaf Krause
 * @version $Revision: 442 $ provided by $Author: okrause $
 */
@Stateless
public class StockUnitStatusReportBean
    implements StockUnitStatusReport
{

    /**
     * @see org.mywms.plugin.report.StockUnitStatusReport#createReport(org.mywms.model.Client,
     *      java.lang.String)
     */
    public byte[] createReport(Client client, String locale)
        throws ReportsException
    {
        // TODO Auto-generated method stub
        return null;
    }

}
