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
 * @see org.mywms.plugin.report.WarehouseStatusReport
 * @author Olaf Krause
 * @version $Revision: 442 $ provided by $Author: okrause $
 */
@Stateless
public class WarehouseStatusReportBean
    implements WarehouseStatusReport
{

    /**
     * @see org.mywms.plugin.report.WarehouseStatusReport#createReport(org.mywms.model.Client,
     *      java.lang.String)
     */
    public byte[] createReport(Client client, String locale)
        throws ReportsException
    {
        // TODO Auto-generated method stub
        return null;
    }
}
