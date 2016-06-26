/*
 * Copyright (c) 2007 by Fraunhofer IML, Dortmund.
 * All rights reserved.
 *
 * Project: myWMS
 */
package org.mywms.cactustest;

import javax.ejb.EJBException;

import org.mywms.model.UnitLoadType;
import org.mywms.service.EntityNotFoundException;
import org.mywms.service.UniqueConstraintViolatedException;

/**
 * Tests the apropriate fassade implementation.
 * 
 * @author Markus Jordan
 * @version $Revision: 602 $ provided by $Author: mkrane $
 */
public class UnitLoadTypeServiceTest 
	extends CactusTestInit 
{

	private UnitLoadType ult1 = null, ult2 = null;
	
	public void testCreateUnitLoadType() 
		throws Exception
	{
		UnitLoadType check = null;
		
		//----- Test Success with system wide unique name -----
		try {
			ult1 = unitLoadTypeService.create("Dieter");
			check = unitLoadTypeService.get(ult1.getId());
			assertEquals(ult1.getVersion(), check.getVersion());
		}
		catch(UniqueConstraintViolatedException ue) {
			fail("Creating an UnitLoadType with name Dieter"+
				 " should not throw an UniqueConstraintViolatedException!");
		}
		catch(EntityNotFoundException ee) {
			fail("The created UnitLoadType should be accessible!");
		}
		
		//----- Test UniqueConstraintException -----
		try {
			unitLoadTypeService.create("Dieter");
			fail("Creating a second UnitLoadType with name Dieter"+ 
				 " should have raised an Exception!");
		}
		catch(UniqueConstraintViolatedException ue){}
		
		//----- Test NullPointerException -----
		try {
			unitLoadTypeService.create(null);
			fail("Creating a UnitLoadType with name==null"+ 
				 " should have raised an NullPointerException!");
		}
		catch(EJBException ne){}
		
		
		
		//----- Test deletion -----
		unitLoadTypeService.delete(ult1);
		unitLoadTypeService.delete(ult2);
		
		try {
			unitLoadTypeService.get(ult1.getId());
			fail("First UnitLoadType has not been deleted!");
		}
		catch(EntityNotFoundException ee) {
			ult1 = null;
		}
		
		try {
			unitLoadTypeService.get(ult2.getId());
			fail("Second UnitLoadType has not been deleted!");
		}
		catch(EntityNotFoundException ee) {
			ult2 = null;
		}
	}	
	
	public void testGetByName() {
		try {
			unitLoadTypeService.getByName("Palette");
		} 
		catch (EntityNotFoundException e) {
			fail("UnitLoadType Palette should be accessible!");
		}
		
	}
}
