/*
 * Copyright (c) 2007 by Fraunhofer IML, Dortmund.
 * All rights reserved.
 *
 * Project: myWMS
 */
package org.mywms.cactustest;

import java.util.List;

import org.apache.log4j.Logger;
import org.mywms.model.ItemData;
import org.mywms.model.Lot;

/**
 * Tests the apropriate fassade implementation.
 * 
 * @author Olaf Krause
 * @version $Revision: 379 $ provided by $Author: okrause $
 */
public class LotServiceTest 
	extends CactusTestInit 
{
    @SuppressWarnings("unused")
    private static final Logger log = Logger.getLogger(LotServiceTest.class.getName());
    
	private ItemData id1;
	private ItemData id2;
	
	public void testCreateGetLot() 
		throws Exception
	{
		id1 = itemDataService.create(clientService.getSystemClient(), "4711_LotServiceTest_1");
		id2 = itemDataService.create(clientService.getSystemClient(), "4711_LotServiceTest_2");
		
		Lot lot1Id1 = lotService.create(clientService.getSystemClient(), id1, "Lot1Id1");
		Lot lot2Id1 = lotService.create(clientService.getSystemClient(), id1, "Lot2Id1");
		
		Lot lot1Id2 = lotService.create(clientService.getSystemClient(), id2, "Lot1Id2");
		Lot lot2Id2 = lotService.create(clientService.getSystemClient(), id2, "Lot2Id2");

		List<Lot> lots = lotService.getListByItemData(id1);
		assertEquals("wrong number of lots found", 2, lots.size());
		assertEquals("wrong lot found", lot1Id1.getId(), lots.get(0).getId());
		assertEquals("wrong lot found", lot2Id1.getId(), lots.get(1).getId());
		
		lots = lotService.getListByItemData(id2);
		assertEquals("wrong number of lots found", 2, lots.size());
		assertEquals("wrong lot found", lot1Id2.getId(), lots.get(0).getId());
		assertEquals("wrong lot found", lot2Id2.getId(), lots.get(1).getId());
		
		lotService.delete(lot1Id1);
		lotService.delete(lot2Id1);
		
		lotService.delete(lot1Id2);
		lotService.delete(lot2Id2);
	}
	
}
