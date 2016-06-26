/*
 * Copyright (c) 2007 by Fraunhofer IML, Dortmund.
 * All rights reserved.
 *
 * Project: myWMS
 */
package org.mywms.cactustest;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJBException;

import org.mywms.model.Role;
import org.mywms.model.User;
import org.mywms.service.EntityNotFoundException;
import org.mywms.service.UniqueConstraintViolatedException;

/**
 * Tests the apropriate fassade implementation.
 * 
 * @author Markus Jordan
 * @version $Revision: 550 $ provided by $Author: mjordan $
 */
public class UserServiceTest 
	extends CactusTestInit
{
	
	private User user1 = null, user2 = null, user3 = null;
	
	public void testGetUserByName() 
		throws Exception
	{
		User admin = userService.getByUsername("System Admin"); 
		
		assertNotNull(admin);
		assertEquals(admin.getName(), "System Admin");
	}
	
	public void testCreateUser() 
		throws Exception
	{
		User check = null;
		
		//----- Test Success with system wide unique name -----
		try {
			user1 = userService.create(client1, "Dieter");
			check = userService.get(user1.getId());
			assertEquals(user1.getVersion(), check.getVersion());
		}
		catch(UniqueConstraintViolatedException ue) {
			fail("Creating a User with name Dieter"+
				 " should not throw an UniqueConstraintViolatedException!");
		}
		catch(EntityNotFoundException ee) {
			fail("The created User should be accessible!");
		}
		
		//----- Test UniqueConstraintException -----
		try {
			userService.create(client2, "Dieter");
			fail("Creating a second User with name Dieter"+ 
				 " should have raised an Exception!");
		}
		catch(UniqueConstraintViolatedException ue){}
		
		//----- Test NullPointerException -----
		try {
			userService.create(client1, null);
			fail("Creating a User with name==null"+ 
				 " should have raised an NullPointerException!");
		}
		catch(EJBException ne){}
		
		try {
			userService.create(null, "Hans");
			fail("Creating a User with client==null"+ 
				 " should have raised an NullPointerException!");
		}
		catch(EJBException ne){}
		
		//----- Test deletion -----
		userService.delete(user1);
		
		try {
			userService.get(user1.getId());
			fail("First User has not been deleted!");
		}
		catch(EntityNotFoundException ee) {
			user1 = null;
		}
	}
	
	public void testGetUsersByRole() {
		ArrayList<Role> roles = new ArrayList<Role>();
		try {
			roles.add(roleService.getByName(org.mywms.globals.Role.DOCUMENT_STR));
			roles.add(roleService.getByName(org.mywms.globals.Role.INVENTORY_STR));
			roles.add(roleService.getByName(org.mywms.globals.Role.REPORT_STR));
			user1 = userService.create(client1, "Dieter");
			user1.setRoles(roles);
			user1 = userService.merge(user1);
			
			roles.add(roleService.getByName(org.mywms.globals.Role.LOGGING_STR));
			roles.add(roleService.getByName(org.mywms.globals.Role.INVENTORY_STR));
			roles.add(roleService.getByName(org.mywms.globals.Role.CLIENT_STR));
			user2 = userService.create(client2, "Peter");
			user2.setRoles(roles);
			user2 = userService.merge(user2);
			
			roles.add(roleService.getByName(org.mywms.globals.Role.DOCUMENT_STR));
			roles.add(roleService.getByName(org.mywms.globals.Role.INVENTORY_STR));
			
			user3 = userService.create(client2, "Hans");
			user3.setRoles(roles);
			user3 = userService.merge(user3);
			
		} 
		catch (UniqueConstraintViolatedException e) {
			fail("Creating test users failed because of already existing users!");
		} 
		catch (EntityNotFoundException e) {
			fail("Creating test users failed because of missing roles!");
		}
		
		//----- Test system client gets all -----
		try {
			List<User> users;
			users = userService.getListByRole(clientService.getSystemClient(), 
										 roleService.getByName(org.mywms.globals.Role.INVENTORY_STR));
			assertTrue(users.size() == 3);
			
			users = userService.getListByRole(client2, 
					 					 roleService.getByName(org.mywms.globals.Role.INVENTORY_STR));
			assertTrue(users.size() == 2);
			
		} 
		catch (EntityNotFoundException e) {
			fail("Missing role inventory!");
		}
	}

	@Override
	protected void tearDown() 
		throws Exception 
	{
		if(user1 != null) {
			userService.delete(user1);
		}
		if(user2 != null) {
			userService.delete(user2);
		}
		if(user3 != null) {
			userService.delete(user3);
		}
		super.tearDown();
	}
}
