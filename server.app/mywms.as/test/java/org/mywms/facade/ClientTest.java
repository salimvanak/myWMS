/*
 * Copyright (c) 2006 by Fraunhofer IML, Dortmund.
 * All rights reserved.
 *
 * Project: myWMS
 */
package org.mywms.facade;


/**
 * Tests the facade Clients.
 * 
 * @author Olaf Krause
 * @version $Revision: 74 $ provided by $Author: okrause $
 */
public class ClientTest 
	extends TestInit 
{
	private Clients client;
	
	/*
	 * @see TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();
		client = beanLocator.getStateless(Clients.class);
	}

	/*
	 * @see TestCase#tearDown()
	 */
	protected void tearDown() throws Exception {
		super.tearDown();
	}
	
	public void testClient()
		throws Exception
	{
		final String NAME = Long.toString(System.currentTimeMillis());
		
		// -------------------------------------------------------------
		// check creation of a client
		client.createClient(NAME);
		
		// -------------------------------------------------------------
		// check get list
		ClientTO[] clients 
			= client.getClients();
		
		assertTrue("wrong number of elements", clients.length >= 1);
		boolean foundClient = false;
		ClientTO clientTO = null;
		for(ClientTO c : clients) {
			if(c.name.equals(NAME)) {
				foundClient = true;
				clientTO = c;
				break;
			}
		}
		assertTrue("added client was not found", foundClient);
		
		// -------------------------------------------------------------
		// check get single client
		ClientTO c 
			= client.getClient(clientTO.id);
		
		assertEquals("wrong client data", NAME, c.name );
		
		// -------------------------------------------------------------
		// check deletion
		client.removeClient(clientTO.id);
		
		// -------------------------------------------------------------
		// check deletion
		clients = client.getClients();
		foundClient = false;
		for(ClientTO c1 : clients) {
			if(c1.name.equals(NAME)) {
				foundClient = true;
				break;
			}
		}
		assertTrue("removed client was found", !foundClient);
	}
}
