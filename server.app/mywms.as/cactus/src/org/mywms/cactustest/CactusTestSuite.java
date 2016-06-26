/*
 * Copyright (c) 2007 by Fraunhofer IML, Dortmund.
 * All rights reserved.
 *
 * Project: myWMS
 */
package org.mywms.cactustest;

import junit.framework.Test;
import junit.framework.TestSuite;

public class CactusTestSuite
    extends TestSuite
{

    /**
     * @param args
     */
    public static void main(String[] args) {

        junit.textui.TestRunner.run(CactusTestSuite.suite());

    }

    public static Test suite() {
        TestSuite suite = new TestSuite("All CactusTests of the myWMS project");

        suite.addTestSuite(AreaServiceTest.class);
        suite.addTestSuite(UserServiceTest.class);
        suite.addTestSuite(ItemDataServiceTest.class);
                
        suite.addTestSuite(UnitLoadTypeServiceTest.class);
        suite.addTestSuite(StockUnitServiceTest.class);
        
        suite.addTestSuite(LotServiceTest.class);
        suite.addTestSuite(ZoneServiceTest.class);

        return suite;
    }
}
