/*
 * Copyright (c) 2006 by Fraunhofer IML, Dortmund.
 * All rights reserved.
 *
 * Project: myWMS
 */
package org.mywms.plugin.report;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.mywms.facade.ReportsException;
import org.mywms.model.Client;
import org.mywms.model.ItemData;
import org.mywms.service.ItemDataService;

/**
 * Creates a report, containing all ItemDatas stored in the system. If a
 * client is specified, only item datas are returned, according to the
 * specified client. (German: Artikelstamm)
 * 
 * @see org.mywms.facade.Reports#getItemDataReport(String, String)
 * @author Olaf Krause
 * @version $Revision: 442 $ provided by $Author: okrause $
 */
@Stateless
public class ItemDataReportBean
    implements ItemDataReport
{

    @EJB
    private ItemDataService itemDataService;

    /**
     * @see org.mywms.plugin.report.ItemDataReport#createReport(org.mywms.model.Client,
     *      java.lang.String)
     */
    public byte[] createReport(Client client, String locale)
        throws ReportsException
    {
        List<ItemData> itemDatas =
            itemDataService.getList(client, 0, new String[] {
                "name", "number"
            });

        StringBuffer strb = new StringBuffer();

        strb.append("<ItemDatas>\n");

        for (ItemData itemData: itemDatas) {
            strb.append("  <ItemData>\n");
            strb.append("    <number>").append(itemData.getNumber()).append(
                "</number>\n");
            strb.append("    <name>").append(itemData.getName()).append(
                "</name>\n");
            strb.append("    <created>").append(itemData.getCreated()).append(
                "</created>\n");
            strb.append("    <modified>")
                .append(itemData.getModified())
                .append("</modified>\n");
            strb.append("    <safetyStock>")
                .append(itemData.getSafetyStock())
                .append("</safetyStock>\n");
            strb.append("  </ItemData>\n");
        }
        strb.append("</ItemDatas>\n");

        return strb.toString().getBytes();
    }

}
