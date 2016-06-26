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
 * Creates ItemData reports.
 * 
 * @author Olaf Krause
 * @version $Revision: 487 $ provided by $Author: okrause $
 */
@Local
public interface ItemDataReport {

    /**
     * Creates a report, containing all ItemDatas stored in the system.
     * If a client is specified, only item datas are returned, according
     * to the specified client. (German: Artikelstamm)
     * 
     * @param client the client the report refers to; if no client is
     *            specified (client==null) all available data is
     *            reported
     * @param locale the locale to be used to create the report
     * @return the PDF based report
     * @throws ReportsException if the specified client is invalid
     * @see org.mywms.facade.Reports#getItemDataReport(java.lang.String,
     *      java.lang.String)
     */
    public abstract byte[] createReport(Client client, String locale)
        throws ReportsException;

}