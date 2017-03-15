/*
 * Copyright (c) 2006 by Fraunhofer IML, Dortmund.
 * All rights reserved.
 *
 * Project: myWMS
 */
package org.mywms.facade;

import java.io.NotSerializableException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.mywms.model.ClearingItemOption;
import org.mywms.model.ClearingItemOptionRetval;
import org.mywms.res.BundleResolver;

/**
 * Tests the facade Clearing
 * 
 * @author aelbaz
 * @version $Revision: 734 $ provided by $Author: dgrys $
 */
public class ClearingTest
    extends TestInit
{

    private Clearing clearing;

    /**
     * @see org.mywms.facade.TestInit#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();
        clearing = beanLocator.getStateless(Clearing.class);

    }

    /**
     * @see junit.framework.TestCase#tearDown()
     */
    protected void tearDown() throws Exception {
        super.tearDown();

    }

    public void testClearing() {

        final String HOST = "localhost";
        final String SOURCE1 = "Roboter1";
        final String USER = "Guest";
        final String MESSAGE1 = "Das Problem liegt am Fach1";
        final String SHORTMESSAGE1 = "am Fach1";

        final String SOURCE2 = "Roboter2";
        final String MESSAGE2 = "Das Problem liegt am Fach2";
        final String SHORTMESSAGE2 = "am Fach2";

        final String MESSAGERESOUCEKE = "CLEARING_ITEM_MESSAGE_KEY";
        final String SHORTMESSAGERESOUCEKE = "CLEARING_ITEM_SHORT_MESSAGE_KEY";
        final String[] messageParameters = {
            "f_001", "f_002"
        };
        String[] shortMessageParameters = {
            "f_001", "f_002"
        };

        ArrayList<ClearingItemOption> optionList =
            new ArrayList<ClearingItemOption>();

        ArrayList<ClearingItemOptionRetval> retvalList =
            new ArrayList<ClearingItemOptionRetval>();

        String[] messageParameter = {
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
        try {
            options.setMessageParameters(messageParameter);
        }
        catch (NotSerializableException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        options.setRetvals(retvalList);

        optionList.add(options);

        String resourceBundleName1 = "mywms-clearing";
        String resourceBundleName2 = "mywms";
        // -------------------------------------------------------------
        // check creation of a clearingItem
        try {
            clearing.createClearingItem(
            		"System-Client",
                HOST,
                SOURCE1,
                USER,
                MESSAGERESOUCEKE,
                SHORTMESSAGERESOUCEKE,
                resourceBundleName1,
                BundleResolver.class,
                shortMessageParameters,
                messageParameters,
                optionList);
        }
        catch (ClearingException e) {
            fail(e.getMessage());
        }

        // check creation of a clearingItem with the same source
        try {
            clearing.createClearingItem(
                "System-Client",
                HOST,
                SOURCE1,
                USER,
                MESSAGERESOUCEKE,
                SHORTMESSAGERESOUCEKE,
                resourceBundleName1,
                BundleResolver.class,
                shortMessageParameters,
                messageParameters,
                optionList);
        }
        catch (ClearingException e) {
            fail(e.getMessage());
        }

        // -------------------------------------------------------------

        // -------------------------------------------------------------
        // check get list
        try {
            List<ClearingItemTO> cleaList =
                clearing.getChronologicalList(null, HOST, SOURCE1, USER, 3);
            assertEquals("wrong number of elements", 2, cleaList.size());
            assertEquals("wrong host", HOST, cleaList.get(0).host);
            assertEquals("wrong source", SOURCE1, cleaList.get(0).source);
            assertEquals("wrong user", USER, cleaList.get(0).user);

        }
        catch (ClearingException e) {
            fail(e.toString());
        }
        // -------------------------------------------------------------

        // check creation of a second clearinItem with other source
        try {
            clearing.createClearingItem(
            		"System-Client",
                HOST,
                SOURCE2,
                USER,
                MESSAGERESOUCEKE,
                SHORTMESSAGERESOUCEKE,
                resourceBundleName2,
                BundleResolver.class,
                shortMessageParameters,
                messageParameters,
                optionList);
        }
        catch (ClearingException e) {
            fail(e.getMessage());
        }
        // check get list
        try {
            List<ClearingItemTO> cleaList =
                clearing.getChronologicalList(null, HOST, SOURCE2, USER, 2);
            assertEquals("wrong number of elements", 1, cleaList.size());
            assertEquals("wrong host", HOST, cleaList.get(0).host);
            assertEquals("wrong source", SOURCE2, cleaList.get(0).source);
            assertEquals("wrong user", USER, cleaList.get(0).user);
            Object[] parameters = cleaList.get(0).options.get(0).getMessageParameters();
            for(Object o: parameters){
                System.out.println(((String)o).toString());
            }

        }
        catch (ClearingException e) {
            fail(e.toString());
        }
        // -------------------------------------------------------------

        // -------------------------------------------------------------
        // check get mandants
        String[] mandants = clearing.getClientNames();
        assertEquals("wrong number of elements", 1, mandants.length);

        // -------------------------------------------------------------

        // -------------------------------------------------------------
        // check get users
        List<String> users = clearing.getUsers();
        assertEquals("wrong number of elements", 1, users.size());
        assertEquals("wrong source", USER, users.get(0));
        // -------------------------------------------------------------

        // -------------------------------------------------------------
        // check get sources
        List<String> sources = clearing.getSources();
        assertEquals("wrong number of elements", 2, sources.size());
        assertEquals("wrong source", SOURCE1, sources.get(0));
        assertEquals("wrong source", SOURCE2, sources.get(1));
        // -------------------------------------------------------------

        // -------------------------------------------------------------
        // check set of clearingItem
        try {
            List<ClearingItemTO> cleaList =
                clearing.getChronologicalList(null, HOST, SOURCE2, USER, 2);
            ClearingItemTO clearingItemTO = cleaList.get(0);
            assertNotNull("element is null", clearingItemTO);

            clearingItemTO.source = SOURCE1;
            clearing.setClearingItem(clearingItemTO);

            assertEquals("wrong source", SOURCE1, clearingItemTO.source);
        }
        catch (ClearingException e) {
            fail(e.toString());
        }

        // -------------------------------------------------------------

    }
}
