/*
 * Copyright (c) 2006,2007 by Fraunhofer IML, Dortmund.
 * All rights reserved.
 *
 * Project: myWMS
 */
import junit.framework.Test;
import junit.framework.TestSuite;

import org.mywms.facade.ClientTest;
import org.mywms.facade.DocumentTest;
import org.mywms.facade.LogTest;
import org.mywms.facade.PluginConfigTest;
import org.mywms.facade.SanityCheckTest;
import org.mywms.globals.DefaultMessageTest;

/**
 * Tests the whole project.
 *
 * @author Olaf Krause
 * @version $Revision: 543 $ provided by $Author: trautm $
 */
public class AllTests
	extends TestSuite
{
	public static void main(String[] args) {
		junit.textui.TestRunner.run(AllTests.suite());
	}

	public static Test suite() {
		TestSuite suite = new TestSuite("All tests of the myWMS project");
		//$JUnit-BEGIN$
		suite.addTestSuite(SanityCheckTest.class);
		suite.addTestSuite(ClientTest.class);
		suite.addTestSuite(DocumentTest.class);
		suite.addTestSuite(LogTest.class);
		suite.addTestSuite(PluginConfigTest.class);
        suite.addTestSuite(DefaultMessageTest.class);
		//$JUnit-END$
		return suite;
	}

}
