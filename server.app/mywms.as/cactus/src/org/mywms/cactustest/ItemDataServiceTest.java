/*
 * Copyright (c) 2007 by Fraunhofer IML, Dortmund.
 * All rights reserved.
 *
 * Project: myWMS
 */
package org.mywms.cactustest;

import java.math.BigDecimal;
import java.util.List;

import javax.ejb.EJBException;

import org.mywms.model.ItemData;
import org.mywms.model.StockUnit;
import org.mywms.service.EntityNotFoundException;
import org.mywms.service.UniqueConstraintViolatedException;

/**
 * Tests the apropriate fassade implementation.
 * 
 * @author Markus Jordan
 * @version $Revision: 565 $ provided by $Author: mjordan $
 */
public class ItemDataServiceTest 
	extends CactusTestInit 
{

	private ItemData item1 = null, item2 = null;
	
	public void testCreateItemData() 
		throws Exception
	{
		ItemData check = null;
		
		//----- Test Success with system wide unique number -----
		try {
			item1 = itemDataService.create(client1, "Dieter");
			check = itemDataService.get(item1.getId());
			assertEquals(item1.getVersion(), check.getVersion());
		}
		catch(UniqueConstraintViolatedException ue){
			fail("Creating a ItemData with number Dieter"+
				 " should not throw an UniqueConstraintViolatedException!");
		}
		catch(EntityNotFoundException ee){
			fail("The created ItemData should be accessible!");
		}
		
		//----- Test UniqueConstraintException -----
		try {
			itemDataService.create(client1, "Dieter");
			fail("Creating a second ItemData with number Dieter"+ 
				 " should have raised an Exception!");
		}
		catch(UniqueConstraintViolatedException ue){}
		
		//----- Test NullPointerException -----
		try {
			itemDataService.create(client1, null);
			fail("Creating a ItemData with number==null"+ 
				 " should have raised an NullPointerException!");
		}
		catch(EJBException ne){}
		
		try {
			itemDataService.create(null, "Hans");
			fail("Creating a ItemData with client==null"+ 
				 " should have raised an NullPointerException!");
		}
		catch(EJBException ne){}
		
		//----- Test success, number only unique within client namespace -----
		try {
			item2 = itemDataService.create(client2, "Dieter");
			check = itemDataService.get(item2.getId());
			assertEquals(item2.getVersion(), check.getVersion());
		}
		catch(UniqueConstraintViolatedException ue) {
			fail("Creating another ItemData with name Dieter"+
				 " for a different client"+
				 " should not throw an UniqueConstraintViolatedException!");
		}catch(EntityNotFoundException ee){
			fail("The created ItemData should be accessible!");
		}
		
		//----- Test deletion -----
		itemDataService.delete(item1);
		itemDataService.delete(item2);
		
		try {
			itemDataService.get(item1.getId());
			fail("First ItemData has not been deleted!");
		}
		catch(EntityNotFoundException ee) {
			item1 = null;
		}
		
		try {
			itemDataService.get(item2.getId());
			fail("Second ItemData has not been deleted!");
		}
		catch(EntityNotFoundException ee) {
			item2 = null;
		}
	}
	
	public void testGetListByNameFragment() 	
		throws Exception
	{
		// Should be found for client1 and fragment 'scho' 
		ItemData i1 = itemDataService.create(client1, "1");
		i1.setName("Milchschokolade");
		i1 = itemDataService.merge(i1);
		
		// Should be found for client1 and fragment 'scho'
		ItemData i2 = itemDataService.create(client1, "2");
		i2.setName("Schokolade");
		i2 = itemDataService.merge(i2);
		
		// Should not be found for client1 and fragment 'scho'
		ItemData i3 = itemDataService.create(client1, "3");
		i3.setName("Mischware");
		i3 = itemDataService.merge(i3);
		
		// Should not be found for client1 and fragment 'scho'
		// but for system client
		ItemData i4 = itemDataService.create(client2, "4");
		i4.setName("Milchschokolade");
		i4 = itemDataService.merge(i4);
		
		List<ItemData> res = itemDataService.getListByNameFragment(client1, "scho");
		
		assertTrue(res.size() == 2);
		assertEquals("Milchschokolade", res.get(0).getName());
		assertEquals("Schokolade", res.get(1).getName());
		
		res = itemDataService.getListByNameFragment(clientService.getSystemClient(), "scho");
		
		assertTrue(res.size() == 3);
		assertEquals("Milchschokolade", res.get(0).getName());
		assertEquals("Milchschokolade", res.get(1).getName());
		assertEquals("Schokolade", res.get(2).getName());
	}
	
	public void testGetByNumber() {
		
		ItemData item = itemDataService.getByItemNumber(client1, "I-c1-1");
		
		if(item == null){
			fail("Item I-c1-1 should be accessible!");
		}
		
		item = itemDataService.getByItemNumber(client2, "I-c1-1");
		
		if(item != null){
			fail("Item I-c1-1 should not be accessible for client 2!");
		}
		
	}
	
	public void testSafetyStock()
		throws UniqueConstraintViolatedException
	{
		List<ItemData> underflows;

		// check for empty store without any safety stock constraint
		underflows
			= itemDataService.getListSafetyStockUnderflow(client1);
		
		assertEquals("wrong safety stock report", 0, underflows.size());
	
		// create another item data without constraint 
		ItemData i2 = itemDataService.create(client1, "2");
		i2.setName("Schokolade");
		i2 = itemDataService.merge(i2);
		
		// check for empty store without any safety stock constraint
		underflows
			= itemDataService.getListSafetyStockUnderflow(client1);
		
		assertEquals("wrong safety stock report", 0, underflows.size());

		// create an item data with safety stock constraint
		ItemData i1 = itemDataService.create(client1, "1");
		i1.setName("Milchschokolade");
		i1.setSafetyStock(9);
		i1 = itemDataService.merge(i1);

		// check for empty store with safety stock constraint
		underflows 
			= itemDataService.getListSafetyStockUnderflow(client1);		
		
		assertEquals("wrong safety stock report", 1, underflows.size());
		assertEquals("wrong item data in safety stock report", i1.getId(), underflows.get(0).getId());		

		// create a stock unit - under the limit of safety stock
		StockUnit stockUnit1 = stockUnitService.create(client1, ul1, i1, new BigDecimal(1));
		stockUnit1.setAmount(new BigDecimal(10));
		stockUnit1.setReservedAmount(new BigDecimal(2));
		// available = 8
		stockUnitService.merge(stockUnit1);

		// check for available amount less then safety stock  
		underflows 
			= itemDataService.getListSafetyStockUnderflow(client1);

//		System.out.println("# stock............: "+stockUnitService.getStock(i1));
//		System.out.println("# available stock..: "+stockUnitService.getAvailableStock(i1));
//		System.out.println("# safety stock.....: "+i1.getSafetyStock());

		assertEquals("wrong safety stock report", 1, underflows.size());
		assertEquals("wrong item data in safety stock report", i1.getId(), underflows.get(0).getId());		
		
		underflows 
			= itemDataService.getListSafetyStockUnderflow(client2);

		assertEquals("wrong safety stock report", 0, underflows.size());
		
		// add a stock unit - safetyLimit is not underflown
		StockUnit stockUnit2 = stockUnitService.create(client1, ul1, i1, new BigDecimal(1));
		stockUnit2.setAmount(new BigDecimal(2));
		// available = 8+2=10
		stockUnitService.merge(stockUnit2);

		underflows 
			= itemDataService.getListSafetyStockUnderflow(client1);

		assertEquals("wrong safety stock report", 0, underflows.size());

//		System.out.println("# stock............: "+stockUnitService.getStock(i1));
//		System.out.println("# available stock..: "+stockUnitService.getAvailableStock(i1));
//		System.out.println("# safety stock.....: "+i1.getSafetyStock());
		
	}
}
