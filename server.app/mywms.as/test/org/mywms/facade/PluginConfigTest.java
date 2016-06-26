/*
 * Copyright (c) 2006 by Fraunhofer IML, Dortmund.
 * All rights reserved.
 *
 * Project: myWMS
 */
package org.mywms.facade;

import org.mywms.model.PluginConfiguration;

/**
 * Tests the PluginConfig facade.
 * 
 * @author Olaf Krause
 * @version $Revision: 209 $ provided by $Author: okrause $
 */
public class PluginConfigTest 
	extends TestInit 
{

	PluginConfig pluginConfig;

	/*
	 * @see TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();
		pluginConfig = beanLocator.getStateless(PluginConfig.class);
	}

	/*
	 * @see TestCase#tearDown()
	 */
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testGetSetMode()
		throws Exception
	{
		String mode = pluginConfig.getCurrentMode();
		assertNotNull("wrong mode value", mode );

		// set and check a new mode
		pluginConfig.setMode("grooving");
		
		mode = pluginConfig.getCurrentMode();
		assertEquals("wrong mode value", "grooving", mode );
		
		// set and check a new mode
		pluginConfig.setMode(PluginConfiguration.DEFAULT_MODE);
		
		mode = pluginConfig.getCurrentMode();
		assertEquals("wrong mode value", PluginConfiguration.DEFAULT_MODE, mode );
	}

	public void testGetSetLookupPattern()
		throws Exception
	{
		final String PATTERN_SAP = "sap.com/myWMS/LOCAL/?";  // SAP
		final String PATTERN_WL = "java:comp/env/ejb/myWMS/?"; // WebLogic
		String lookupPattern = pluginConfig.getLookupPattern();
		assertNotNull("wrong lookupPattern value", lookupPattern );
	
		// set and check a new pattern
		pluginConfig.setLookupPattern(PATTERN_SAP);
		lookupPattern = pluginConfig.getLookupPattern();
		assertEquals("wrong lookupPattern value", PATTERN_SAP, lookupPattern );
		
		// set and check a new pattern
		pluginConfig.setLookupPattern(PATTERN_WL);
		lookupPattern = pluginConfig.getLookupPattern();
		assertEquals("wrong lookupPattern value", PATTERN_WL, lookupPattern );
		
		// set and check a new mode
		pluginConfig.setLookupPattern(PluginConfiguration.DEFAULT_LOOKUP_PATTERN);
		lookupPattern = pluginConfig.getLookupPattern();
		assertEquals("wrong lookupPattern value", PluginConfiguration.DEFAULT_LOOKUP_PATTERN, lookupPattern );
	}
	
	/*
	 * Test method for 'org.mywms.facade.PluginConfig.getModes()'
	 */
	public void testGetModes() {
		String[] modes = pluginConfig.getModes();
		assertTrue("modes array is to small", modes.length>=1);
		boolean found = false;
		for(String mode : modes) {
			if("default".equals(mode)) {
				found = true;
				break;
			}
		}
		assertTrue("default mode is missing", found);
	}

	/*
	 * Test method for 'org.mywms.facade.PluginConfig.getCurrentMode()'
	 */
	public void testGetCurrentMode() {
		String mode = pluginConfig.getCurrentMode();
		assertNotNull("mode must not be null", mode);
		assertEquals("mode should be default mode", "default", mode);
	}
}
