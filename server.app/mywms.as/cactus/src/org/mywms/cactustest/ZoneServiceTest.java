/*
 * Copyright (c) 2007 by Fraunhofer IML, Dortmund.
 * All rights reserved.
 *
 * Project: myWMS
 */
package org.mywms.cactustest;

import javax.ejb.EJBException;

import org.mywms.model.Zone;
import org.mywms.service.EntityNotFoundException;
import org.mywms.service.UniqueConstraintViolatedException;

/**
 * Tests the apropriate fassade implementation.
 * 
 * @author Olaf Krause
 * @version $Revision: 384 $ provided by $Author: okrause $
 */
public class ZoneServiceTest 
	extends CactusTestInit 
{

	private Zone zone1 = null, zone2 = null;
	
	public void testCreateItemData() 
		throws Exception
	{
		Zone check = null;
		
		//----- Test Success with system wide unique number -----
		try {
			zone1 = zoneService.create(client1, "Dieter");
			check = zoneService.get(zone1.getId());
			assertEquals(zone1.getVersion(), check.getVersion());
		}
		catch(UniqueConstraintViolatedException ue){
			fail("Creating a Zone with name Dieter"+
				 " should not throw an UniqueConstraintViolatedException!");
		}
		catch(EntityNotFoundException ee){
			fail("The created Zone should be accessible!");
		}
		
		//----- Test UniqueConstraintException -----
		try {
			zoneService.create(client1, "Dieter");
			fail("Creating a second Zone with name Dieter"+ 
				 " should have raised an Exception!");
		}
		catch(UniqueConstraintViolatedException ue){}
		
		//----- Test NullPointerException -----
		try {
			zoneService.create(client1, null);
			fail("Creating a Zone with name==null"+ 
				 " should have thrown a NullPointerException!");
		}
		catch(EJBException ne){}
		
		try {
			zoneService.create(null, "Hans");
			fail("Creating a Zone with client==null"+ 
				 " should have raised an NullPointerException!");
		}
		catch(EJBException ne){}
		
		//----- Test success, number only unique within client namespace -----
		try {
			zone2 = zoneService.create(client2, "Dieter");
			check = zoneService.get(zone2.getId());
			assertEquals(zone2.getVersion(), check.getVersion());
		}
		catch(UniqueConstraintViolatedException ue) {
			fail("Creating another Zone with name Dieter"+
				 " for a different client"+
				 " should not throw an UniqueConstraintViolatedException!");
		}catch(EntityNotFoundException ee){
			fail("The created Zone should be accessible!");
		}
		
		//----- Test deletion -----
		zoneService.delete(zone1);
		zoneService.delete(zone2);
		
		try {
			zoneService.get(zone1.getId());
			fail("First Zone has not been deleted!");
		}
		catch(EntityNotFoundException ee) {
			zone1 = null;
		}
		
		try {
			zoneService.get(zone2.getId());
			fail("Second Zone has not been deleted!");
		}
		catch(EntityNotFoundException ee) {
			zone2 = null;
		}
	}
	
}
