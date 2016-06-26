/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.location.facade;

import junit.framework.TestCase;

import org.mywms.facade.FacadeException;

import de.linogistix.los.test.TestUtilities;

public class LocationSanityCheckFacadeBeanTest extends TestCase {

	LocationSanityCheckFacade bean;

	protected void setUp() throws Exception {
		super.setUp();
		this.bean = TestUtilities.beanLocator
				.getStateless(LocationSanityCheckFacade.class);
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	// public void testAssignDummyTypeToPicklocations(){
	//		
	// String labelId = "T1-1-1-1";
	// LOSStorageLocation sl;
	// LOSUnitLoad ul;
	//		
	// try{
	// ManageInventoryFacade manageInv =
	// TestUtilities.beanLocator.getStateless(ManageInventoryFacade.class);
	// LOSUnitLoadQueryRemote ulQueryRemote =
	// TestUtilities.beanLocator.getStateless(LOSUnitLoadQueryRemote.class);
	// StockUnitQueryRemote suQueryRemote =
	// TestUtilities.beanLocator.getStateless(StockUnitQueryRemote.class);
	// int amount;
	// ul = ulQueryRemote.queryByIdentity(labelId);
	// if (ul.getStockUnitList().isEmpty()){
	// manageInv.createStockUnitOnStorageLocation(TopologyBean.TESTCLIENT_NUMBER,
	// labelId, TopologyBean.ITEM_A1_NUMBER, TopologyBean.LOT_N1_A1_NAME, 23,
	// labelId);
	// }
	// ul = ulQueryRemote.queryByIdentity(labelId);
	//
	// StockUnit su = ul.getStockUnitList().get(0);
	// manageInv.createStockUnitOnStorageLocation(TopologyBean.TESTCLIENT_NUMBER,
	// TopologyBean.SL_WE_NAME, su.getItemData().getNumber(),
	// su.getLot().getName(), 32,"testAssignDummyTypeToPicklocations" );
	//			
	// // provoke error : 2 sus on one sl
	// LOSUnitLoadQueryRemote slQueryRemote =
	// TestUtilities.beanLocator.getStateless(LOSUnitLoadQueryRemote.class);
	// LOSUnitLoadCRUDRemote ulCRUDRemote =
	// TestUtilities.beanLocator.getStateless(LOSUnitLoadCRUDRemote.class);
	//			
	// UnitLoadTypeQueryRemote ulTypeQuery =
	// TestUtilities.beanLocator.getStateless(UnitLoadTypeQueryRemote.class);
	// UnitLoadType palette =
	// ulTypeQuery.queryByIdentity(TopologyBean.PALETTE_NAME);
	// UnitLoadType dummy =
	// ulTypeQuery.queryByIdentity(TopologyBean.DUMMY_KOMM_ULTYPE_NAME);
	//
	// ul = ulQueryRemote.queryById(ul.getId());
	// ul.setType(palette);
	// ulCRUDRemote.update(ul);
	//			
	// ul = ulQueryRemote.queryById(ul.getId());
	// assertTrue(ul.getType().equals(palette));
	//			
	// bean.assignDummyTypeToPicklocations();
	//			
	// ul = ulQueryRemote.queryById(ul.getId());
	// assertTrue(ul.getType().equals(dummy));
	//
	//			
	// } catch (Throwable t){
	// log.error(t.getMessage(), t);
	// fail(t.getMessage());
	// }
	// }

	// public void testCallAssignDummyTypeToPicklocations() throws
	// FacadeException{
	// bean.assignDummyTypeToPicklocations();
	// }

	// public void testCallRemoveEmptyUnitLoads() throws FacadeException{
	// bean.removeEmptyUnitLoads();
	// }

	public void testCallRemoveEmptyUnitLoads() throws FacadeException {
		bean.sanityCheck();
	}
}
