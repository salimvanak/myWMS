/*
 * Copyright (c) 2006 by Fraunhofer IML, Dortmund.
 * All rights reserved.
 *
 * Project: myWMS
 */
package org.mywms.facade;

import org.mywms.ejb.BeanLocator;


/**
 * Tests the facade SanityCheck.
 * 
 * @author Olaf Krause
 * @version $Revision: 196 $ provided by $Author: okrause $
 */
public class SanityCheckTest 
	extends TestInit 
{
	public static void main(String[] args) {
		junit.textui.TestRunner.run(SanityCheckTest.class);
	}
	
	/*
	 * @see TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		// super.setUp(); -- no authentification
	}

	/*
	 * @see TestCase#tearDown()
	 */
	protected void tearDown() throws Exception {
		// super.tearDown();
	}
	
	public void testCheck()
		throws Exception
	{
		BeanLocator beanLocator = new BeanLocator();
		SanityCheck sanityCheck = beanLocator.getStateless(SanityCheck.class);
		sanityCheck.check();

		String response = sanityCheck.check();
		
		assertNotNull(response);
		
		System.out.println(response);
	}
}
