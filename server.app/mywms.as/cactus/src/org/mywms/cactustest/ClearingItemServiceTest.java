/*
 * Copyright (c) 2007 by Fraunhofer IML, Dortmund.
 * All rights reserved.
 *
 * Project: myWMS
 */
package org.mywms.cactustest;

import java.io.NotSerializableException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.mywms.model.ClearingItem;
import org.mywms.model.ClearingItemOption;
import org.mywms.model.ClearingItemOptionRetval;
import org.mywms.model.Client;
import org.mywms.res.BundleResolver;

/**
 * @author aelbaz
 * @version $Revision: 599 $ provided by $Author: trautm $
 */
public class ClearingItemServiceTest
    extends CactusTestInit
{
    private ClearingItem clearingItem1 = null, clearingItem11 = null,
            clearingItem2 = null;

    public void testCreateItem() throws NotSerializableException {

        Client client = clientService.getSystemClient();
        String host = "localhost";
        String source1 = "Robot1";
        String user1 = "Guest";

        String messageResourceKey1 = "CLEARING_ITEM_MESSAGE_KEY";
        String shortMessageResourceKey1 = "CLEARING_ITEM_SHORT_MESSAGE_KEY";
        String[] messageParameters1 = {
                "f_001", "f_002"
        };
        String[] shortMessageParameters1 = {
                "f_001", "f_002"
        };

        ArrayList<ClearingItemOption> optionList =
                new ArrayList<ClearingItemOption>();

        ArrayList<ClearingItemOptionRetval> retvalList =
                new ArrayList<ClearingItemOptionRetval>();

        String[] messageParameters = {
                "eins", "zwei"
        };

        ClearingItemOptionRetval retval1 = new ClearingItemOptionRetval();
        ClearingItemOptionRetval retval2 = new ClearingItemOptionRetval();

        retval1.setNameResourceKey("Date");
        retval1.setType(Date.class);
        retvalList.add(retval1);

        retval2.setNameResourceKey("Dezimal");
        retval2.setType(Integer.class);
        retvalList.add(retval2);

        ClearingItemOption options = new ClearingItemOption();
        options.setMessageResourceKey("CLEARING_ITEM_OPTION_MESSAGE_KEY_1");
        options.setMessageParameters(messageParameters);
        options.setRetvals(retvalList);

        optionList.add(options);

        String boundleName1 = "org.mywms.res.mywms-clearing";
        String boundleName2 = "org.mywms.res.mywms-clear";

        clearingItem1 =
                clearingItemService.create(
                        client,
                        host,
                        source1,
                        user1,
                        messageResourceKey1,
                        shortMessageResourceKey1,
                        boundleName1,
                        BundleResolver.class,
                        shortMessageParameters1,
                        messageParameters1,
                        optionList);

        clearingItem1.setSolution("admin", options);
        clearingItem1 = clearingItemService.merge(clearingItem1);

        assertNotNull("Das Object wurde nicht erzeugt", clearingItem1);

        String source2 = "Robot2";
        String user2 = "Guest2";
        String message2 = "Das Problem liegt am Fach2";
        String shortMessage2 = "Problem Nummer 2";
        String messageResourceKey2 = "CLEARING_ITEM_MESSAGE_";
        String shortMessageResourceKey2 = "CLEARING_ITEM_SHORT_MESSAGE_KEY";
        String[] messageParameters2 = {
                "f_001", "f_002"
        };
        String[] shortMessageParameters2 = {
                "f_001", "f_002"
        };
        clearingItem11 =
                clearingItemService.create(
                        client,
                        host,
                        source1,
                        user1,
                        messageResourceKey1,
                        shortMessageResourceKey1,
                        boundleName1,
                        BundleResolver.class,
                        shortMessageParameters1,
                        messageParameters1,
                        optionList);

        assertNotNull("Das Object wurde nicht erzeugt", clearingItem11);

        clearingItem2 =
                clearingItemService.create(
                        client,
                        host,
                        source2,
                        user2,
                        messageResourceKey2,
                        shortMessageResourceKey2,
                        boundleName2,
                        BundleResolver.class,
                        shortMessageParameters2,
                        messageParameters2,
                        optionList);
        assertNotNull("Das Object wurde nicht erzeugt", clearingItem2);

    }

    public void testGetUser() {
        List<String> listUsers = clearingItemService.getUser(null);
        assertEquals("Guest", listUsers.get(0));
    }

    public void testGetSource() {
        List<String> listSources = clearingItemService.getSources(null);
        assertEquals("Robot1", listSources.get(0));

    }

    public void testGetHosts() {
        List<String> listHosts = clearingItemService.getHosts(null);
        assertEquals("localhost", listHosts.get(0));
    }

    public void testGetChronologicalLiVoidst() {
        String client = clientService.getSystemClient().getNumber();
        List<ClearingItem> list =
                clearingItemService.getChronologicalList(
                        client,
                        "localhost",
                        "Robot1",
                        "user1",
                        3);
        assertEquals("falsche Nummer", 0, list.size());
        list =
                clearingItemService.getChronologicalList(
                        client,
                        "localhost",
                        "Robot1",
                        "Guest",
                        2);
        assertEquals("falsche Nummer", 2, list.size());
    }

    public void testGetNondealChronologicalList() {
        String client = clientService.getSystemClient().getNumber();
        List<ClearingItem> list =
                clearingItemService.getNondealChronologicalList(
                        client,
                        "localhost",
                        "Robot1",
                        "Guest",
                        5);
        assertEquals("falsche Nummer", 1, list.size());

    }

}
