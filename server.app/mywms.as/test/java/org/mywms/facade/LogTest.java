/*
 * Copyright (c) 2006,2007 by Fraunhofer IML, Dortmund.
 * All rights reserved.
 *
 * Project: myWMS
 */
package org.mywms.facade;

import org.mywms.globals.LogItemType;

/**
 * Tests the facade Log.
 * 
 * @author Olaf Krause
 * @version $Revision: 634 $ provided by $Author: mkrane $
 */
public class LogTest
    extends TestInit
{

    private Log log;

    /*
     * @see TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();
        log = beanLocator.getStateless(Log.class);
    }

    /*
     * @see TestCase#tearDown()
     */
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testLog() throws Exception {
        final String CLIENT_NUMBER = "0";
        final String HOSTNAME = "l182312li738zl1kjj23i72t";
        final String SOURCE = "i12j129u312j129j";
        final String USER = "1823z.,1jh23o812z3123";
        final LogItemType TYPE = LogItemType.ERROR;
        final String MESSAGE = "q23h1j23o812njkbn";
        final String MESSAGE_KEY = "lukq2,.31uzo293";

        // write the log
        log.log(
                CLIENT_NUMBER,
                HOSTNAME,
                SOURCE,
                USER,
                TYPE,
                MESSAGE,
                MESSAGE_KEY);

        // get the log back

        LogItemTO[] logItems = log.getLogs(1);
        assertEquals("wrong number of elements", 1, logItems.length);
        assertEquals("wrong element data", HOSTNAME, logItems[0].host);
        assertEquals("wrong element data", SOURCE, logItems[0].source);
        assertEquals("wrong element data", USER, logItems[0].user);
        assertEquals("wrong element data", TYPE, logItems[0].type);
        assertEquals("wrong element data", MESSAGE, logItems[0].message);
        assertEquals(
                "wrong element data",
                MESSAGE_KEY,
                logItems[0].messageResourceKey);

        // write the logs
        log.log(CLIENT_NUMBER, HOSTNAME, SOURCE, USER, TYPE, "1", MESSAGE_KEY);
        log.log(
                CLIENT_NUMBER,
                HOSTNAME,
                SOURCE,
                USER,
                LogItemType.LOG,
                "2",
                MESSAGE_KEY);
        log.log(CLIENT_NUMBER, HOSTNAME, SOURCE, USER, TYPE, "3", MESSAGE_KEY);
        log.log(CLIENT_NUMBER, HOSTNAME, SOURCE, USER, TYPE, "4", MESSAGE_KEY);

        logItems = log.getLogs(4);
        assertEquals("wrong number of elements", 4, logItems.length);
        assertEquals("wrong element data", "4", logItems[0].message);
        assertEquals("wrong element data", "3", logItems[1].message);
        assertEquals("wrong element data", "2", logItems[2].message);
        assertEquals("wrong element data", "1", logItems[3].message);

        assertEquals("wrong element data", LogItemType.LOG, logItems[2].type);
    }
}
