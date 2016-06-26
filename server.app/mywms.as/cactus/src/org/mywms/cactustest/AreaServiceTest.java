/*
 * Copyright (c) 2007 by Fraunhofer IML, Dortmund.
 * All rights reserved.
 *
 * Project: myWMS
 */
package org.mywms.cactustest;

import javax.ejb.EJBException;

import org.mywms.globals.AreaName;
import org.mywms.model.Area;
import org.mywms.service.EntityNotFoundException;
import org.mywms.service.UniqueConstraintViolatedException;

/**
 * Tests the apropriate fassade implementation.
 * 
 * @author Markus Jordan
 * @version $Revision: 311 $ provided by $Author: lxjordan $
 */
public class AreaServiceTest 
	extends CactusTestInit 
{

	private Area au1 = null, au2 = null;
	
	public void testCreateArea() throws Exception{
		Area check = null;
		
//		----- Test Success with system wide unique name -----
		try {
			au1 = areaService.create(client1, "Dieter");
			check = areaService.get(au1.getId());
			assertEquals(au1.getVersion(), check.getVersion());
		}
		catch(UniqueConstraintViolatedException ue) {
			fail("Creating a Area with name Dieter"+
				 " should not throw an UniqueConstraintViolatedException!");
		}
		catch(EntityNotFoundException ee) {
			fail("The created Area should be accessible!");
		}
		
		//----- Test UniqueConstraintException -----
		try {
			areaService.create(client1, "Dieter");
			fail("Creating a second Area with name Dieter"+ 
				 " should have raised an Exception!");
		}
		catch(UniqueConstraintViolatedException ue){}
		
		//----- Test NullPointerException -----
		try {
			areaService.create(client1, null);
			fail("Creating a Area with name==null"+ 
				 " should have raised an NullPointerException!");
		}
		catch(EJBException ne){}
		
		try {
			areaService.create(null, "Hans");
			fail("Creating a Area with client==null"+ 
				 " should have raised an NullPointerException!");
		}
		catch(EJBException ne){}
		
		//----- Test success, name only unique within client namespace -----
		try {
			au2 = areaService.create(client2, "Dieter");
			check = areaService.get(au2.getId());
			assertEquals(au2.getVersion(), check.getVersion());
		}
		catch(UniqueConstraintViolatedException ue) {
			fail("Creating another Area with name Dieter"+
				 " for a different client"+
				 " should not throw an UniqueConstraintViolatedException!");
		}
		catch(EntityNotFoundException ee) {
			fail("The created Area should be accessible!");
		}
		
		//----- Test deletion -----
		areaService.delete(au1);
		areaService.delete(au2);
		
		try {
			areaService.get(au1.getId());
			fail("First Area has not been deleted!");
		}
		catch(EntityNotFoundException ee) {
			au1 = null;
		}
		
		try {
			areaService.get(au2.getId());
			fail("Second Area has not been deleted!");
		}
		catch(EntityNotFoundException ee) {
			au2 = null;
		}
	}
	
	public void testGetByName(){
		//----- Test success ----
		try {
			areaService.getByName(client1, AreaName.GOODS_IN_STR);
		} catch (EntityNotFoundException e) {
			fail("There should be one area GoodsIn for client 1");
		}
		//----- Test wrong client ----
		try {
			areaService.getByName(client2, AreaName.PICKING_STR);
			fail("Searching for area Picking of client 2"
				 +" should have raised an Exception");
		} catch (EntityNotFoundException e) {
		}
	}
}
