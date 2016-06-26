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

import org.mywms.model.StockUnit;
import org.mywms.service.ConstraintViolatedException;
import org.mywms.service.EntityNotFoundException;
import org.mywms.service.StockUnitInfoTO;

/**
 * Tests the apropriate fassade implementation.
 * 
 * @author Markus Jordan
 * @version $Revision: 550 $ provided by $Author: mjordan $
 */
public class StockUnitServiceTest
    extends CactusTestInit
{
    private StockUnit stock1 = null;

    private StockUnit stock2 = null;

    private StockUnit stock3 = null;

    public void testCreateStockUnit() throws Exception {
        StockUnit check = null;

        // ----- Test Success -----
        try {
            stock1 = stockUnitService.create(client1, ul1, item1, new BigDecimal(5));
            check = stockUnitService.get(stock1.getId());
            assertEquals(stock1.getVersion(), check.getVersion());
        }
        catch (EntityNotFoundException ee) {
            fail("The created StockUnit should be accessible!");
        }

        // ----- Test NullPointerException -----
        try {
            stockUnitService.create(null, ul1, item1, new BigDecimal(5));
            fail("Creating a StockUnit with client==null"
                    + " should have raised an NullPointerException!");
        }
        catch (EJBException ne) {
            // OK - we expect this
        }

        try {
            stockUnitService.create(client1, null, item1, new BigDecimal(5));
            fail("Creating a StockUnit with unitLoad==null"
                    + " should have raised an NullPointerException!");
        }
        catch (EJBException ne) {
            // OK - we expect this
        }

        try {
            stockUnitService.create(client1, ul1, null, new BigDecimal(5));
            fail("Creating a StockUnit with itemData==null"
                    + " should have raised an NullPointerException!");
        }
        catch (EJBException ne) {
            // OK - we expect this
        }

        // ----- Test BusinessException -----
        try {
            stockUnitService.create(client2, ul1, item3, new BigDecimal(5));
            fail("Creating a StockUnit for client2 on an UnitLoad assigned to client1"
                    + " should have raised an BusinessException!");
        }
        catch (EJBException ne) {
            // OK - we expect this
        }

        try {
            stockUnitService.create(client2, ul3, item1, new BigDecimal(5));
            fail("Creating a StockUnit for client2 with an ItemData assigned to client1"
                    + " should have raised an BusinessException!");
        }
        catch (EJBException ne) {
            // OK - we expect this
        }

        // ----- Test deletion -----
        stockUnitService.delete(stock1);

        try {
            stockUnitService.get(stock1.getId());
            fail("First StockUnit has not been deleted!");
        }
        catch (EntityNotFoundException ee) {
            // OK - we expect this
            stock1 = null;
        }
    }

    public void testGetStockUnitsByUnitLoad()
            throws ConstraintViolatedException
    {
        stock1 = stockUnitService.create(client1, ul1, item1, new BigDecimal(5));
        stock2 = stockUnitService.create(client1, ul1, item1, new BigDecimal(5));
        stock3 = stockUnitService.create(client1, ul2, item1, new BigDecimal(5));

        List<StockUnit> suList = stockUnitService.getListByUnitLoad(ul1);

        assertTrue("Wrong size of returned list", suList.size() == 2);
        boolean stock1Returned = false, stock2Returned = false;
        for (StockUnit su : suList) {
            if (su.getId().longValue() == stock1.getId().longValue()) {
                stock1Returned = true;
            }
            else if (su.getId().longValue() == stock2.getId().longValue()) {
                stock2Returned = true;
            }
        }
        assertTrue(stock1Returned);
        assertTrue(stock2Returned);

        stockUnitService.delete(stock1);
        stockUnitService.delete(stock2);
        stockUnitService.delete(stock3);
    }

    public void testGetStockUnitByItemDataByDate() throws Exception {

        // erzeuge StockUnits
        stock1 = stockUnitService.create(client1, ul2, item1, new BigDecimal(5));
        wa1t(100);
        System.out.println(stock1.getCreated().getTime());
        stock2 = stockUnitService.create(client1, ul1, item1, new BigDecimal(5));
        wa1t(100);
        System.out.println(stock2.getCreated().getTime());

        stock1.setUnitLoad(ul1);
        stock1 = stockUnitService.merge(stock1);
        wa1t(100);
        System.out.println(stock1.getCreated().getTime());
        // finde die StockUnits
        List<StockUnit> stList =
                stockUnitService.getListByItemDataOrderByDate(item1);

        assertTrue("Wrong size of returned List", stList.size() == 2);
        boolean stock1Returned = false, stock2Returned = false;

        // teste ob die StockUnit in die richtige Position ist
        if (stList.get(0).getId().longValue() == stock1.getId().longValue()) {
            stock1Returned = true;
        }
        // teste ob die StockUnit in die richtige Position ist
        if (stList.get(1).getId().longValue() == stock2.getId().longValue()) {
            stock2Returned = true;
        }
        assertTrue("Die StockUnit sind falsch geordnet", stock1Returned);
        assertTrue("Die StockUnit sind falsch geordnet", stock2Returned);

        stockUnitService.delete(stock1);
        stockUnitService.delete(stock2);

    }

    public void testGetStockUnitByItemDataByAvailableAmount() throws Exception {
        // erzeuge StockUnit stock1 an UL1
        stock1 = stockUnitService.create(client1, ul1, item1, new BigDecimal(5));
        wa1t(100);
        // erzeuge StockUnit stock2 an UL1
        stock2 = stockUnitService.create(client1, ul2, item1, new BigDecimal(5));
        wa1t(100);
        stock3 = stockUnitService.create(client1, ul4, item1, new BigDecimal(5));

        stock1.setAmount(new BigDecimal(16));
        stock1.addReservedAmount(new BigDecimal(4));
        stock1 = stockUnitService.merge(stock1);

        stock2.setAmount(new BigDecimal(8));
        stock2.addReservedAmount(new BigDecimal(4));
        stock2 = stockUnitService.merge(stock2);

        stock3.setAmount(new BigDecimal(12));
        stock3.addReservedAmount(new BigDecimal(4));
        stock3 = stockUnitService.merge(stock3);

        //  
        List<StockUnit> stList;
        stList =
                stockUnitService.getListByItemDataOrderByAvailableAmount(item1);
        System.out.println(stList.size());

        assertTrue("Wrong size of returned List", stList.size() == 3);
        boolean stock1Returned = false, stock2Returned = false, stock3Returned =
                false;
        if (stock2.getId().longValue() == stList.get(0).getId().longValue()) {
            stock2Returned = true;
        }
        if (stock3.getId().longValue() == stList.get(1).getId().longValue()) {
            stock3Returned = true;
        }
        if (stock1.getId().longValue() == stList.get(2).getId().longValue()) {
            stock1Returned = true;
        }

        assertTrue("wrong position of the stock1", stock1Returned);
        assertTrue("wrong position of the stock2", stock2Returned);
        assertTrue("wrong position of the stock3", stock3Returned);

        stock1Returned = false;
        stock2Returned = false;
        stock3Returned = false;

        stock1.addReservedAmount(new BigDecimal(8));
        stock1 = stockUnitService.merge(stock1);

        stock3.addReservedAmount(new BigDecimal(5));
        stock3 = stockUnitService.merge(stock3);

        stList =
                stockUnitService.getListByItemDataOrderByAvailableAmount(item1);

        if (stock3.getId().longValue() == stList.get(0).getId().longValue()) {
            stock2Returned = true;
        }
        if (stock1.getId().longValue() == stList.get(1).getId().longValue()) {
            stock3Returned = true;
        }
        if (stock2.getId().longValue() == stList.get(2).getId().longValue()) {
            stock1Returned = true;
        }

        stockUnitService.delete(stock1);
        stockUnitService.delete(stock2);
        stockUnitService.delete(stock3);
    }

    public void testGetStockUnitsByStorageLocation() throws Exception {
        // FIND: Stock on SL1 and UL1
        stock1 = stockUnitService.create(client1, ul1, item1, new BigDecimal(8));

        // FIND: same unitload, different stock
        stock2 = stockUnitService.create(client1, ul1, item2, new BigDecimal(8));

        // FIND: same storageLocation, different unitload
//        UnitLoad utest =
//                unitLoadService.create(client1, ulType1, "Ul-test", sl1);
//        stock3 = stockUnitService.create(client1, utest, item1, new BigDecimal(8));

        // NOT: different unitload, different storagelocation(SL4)
        StockUnit stock4 = stockUnitService.create(client1, ul4, item1, new BigDecimal(8));

//        sl1 = storageLocationService.get(sl1.getId());

//        List<StockUnit> suList = stockUnitService.getListByStorageLocation(sl1);
//        assertTrue("Wrong size of returned List " + suList.size(), suList
//                .size() == 3);
//        boolean stock1Returned = false, stock2Returned = false, stock3Returned =
//                false;
//
//        for (StockUnit su : suList) {
//            if (su.getId().longValue() == stock1.getId().longValue()) {
//                stock1Returned = true;
//            }
//            else if (su.getId().longValue() == stock2.getId().longValue()) {
//                stock2Returned = true;
//            }
//            else if (su.getId().longValue() == stock3.getId().longValue()) {
//                stock3Returned = true;
//            }
//        }
//        assertTrue("Stock1 was not returned", stock1Returned);
//        assertTrue("Stock2 was not returned", stock2Returned);
//        assertTrue("Stock3 was not returned", stock3Returned);
//
//        suList = stockUnitService.getListByStorageLocation(sl2);
//        assertTrue("Wrong size of returned List " + suList.size(), suList
//                .size() == 0);

        stockUnitService.delete(stock1);
        stockUnitService.delete(stock2);
        stockUnitService.delete(stock3);
        stockUnitService.delete(stock4);
    }

    public void testAmounts() throws ConstraintViolatedException {
        StockUnitInfoTO infoTO;

        stock1 = stockUnitService.create(client1, ul1, item1, new BigDecimal(1));
        stock2 = stockUnitService.create(client1, ul1, item1, new BigDecimal(1));
        stock3 = stockUnitService.create(client1, ul2, item1, new BigDecimal(1));

        assertEquals("wrong stock", 3, stockUnitService.getStock(item1));
        assertEquals("wrong available stock", 3, stockUnitService
                .getAvailableStock(item1));
        assertEquals("wrong reserved stock", 0, stockUnitService
                .getReservedStock(item1));
        assertEquals("wrong count", 3, stockUnitService.getCount(item1));

        infoTO = stockUnitService.getInfo(item1);
        assertEquals("wrong stock", 3, infoTO.stock);
        assertEquals("wrong available stock", 3, infoTO.availableStock);
        assertEquals("wrong reserved stock", 0, infoTO.reservedStock);
        assertEquals("wrong count", 3, infoTO.count);

        stock1.addReservedAmount(new BigDecimal(1));
        stock3.setAmount(new BigDecimal(5));
        stock3.addReservedAmount(new BigDecimal(2));

        stock1 = stockUnitService.merge(stock1);
        stock3 = stockUnitService.merge(stock3);

        assertEquals("wrong stock", 7, stockUnitService.getStock(item1));
        assertEquals("wrong available stock", 4, stockUnitService
                .getAvailableStock(item1));
        assertEquals("wrong reserved stock", 3, stockUnitService
                .getReservedStock(item1));
        assertEquals("wrong count", 3, stockUnitService.getCount(item1));

        infoTO = stockUnitService.getInfo(item1);
        assertEquals("wrong stock", 7, infoTO.stock);
        assertEquals("wrong available stock", 4, infoTO.availableStock);
        assertEquals("wrong reserved stock", 3, infoTO.reservedStock);
        assertEquals("wrong count", 3, infoTO.count);

        stockUnitService.delete(stock1);
        assertEquals("wrong count", 2, stockUnitService.getCount(item1));
        stockUnitService.delete(stock2);
        assertEquals("wrong count", 1, stockUnitService.getCount(item1));
        stockUnitService.delete(stock3);

        assertEquals("stock should be 0", 0, stockUnitService.getStock(item1));
        assertEquals("stock should be 0", 0, stockUnitService
                .getAvailableStock(item1));
        assertEquals("reserved stock should be 0", 0, stockUnitService
                .getReservedStock(item1));
        assertEquals("wrong count", 0, stockUnitService.getCount(item1));

        infoTO = stockUnitService.getInfo(item1);
        assertEquals("wrong stock", 0, infoTO.stock);
        assertEquals("wrong available stock", 0, infoTO.availableStock);
        assertEquals("wrong reserved stock", 0, infoTO.reservedStock);
        assertEquals("wrong count", 0, infoTO.count);
    }

    public void testGetOrderedLists() throws Exception {
        stock1 = stockUnitService.create(client1, ul1, item1, new BigDecimal(1));
        wa1t(200);
        stock2 = stockUnitService.create(client1, ul1, item1, new BigDecimal(1));
        wa1t(200);
        stock3 = stockUnitService.create(client1, ul2, item1, new BigDecimal(1));

        // prepare contents
        stock1.setAmount(new BigDecimal(5));
        stock1.addReservedAmount(new BigDecimal(1));
        // available := 4
        stock1 = stockUnitService.merge(stock1);

        stock2.setAmount(new BigDecimal(8));
        stock2.addReservedAmount(new BigDecimal(5));
        // available := 3
        stock2 = stockUnitService.merge(stock2);

        stock3.setAmount(new BigDecimal(6));
        stock3.addReservedAmount(new BigDecimal(5));
        // available := 1
        stock3 = stockUnitService.merge(stock3);

        // check list by date
        List<StockUnit> stockUnits;
        stockUnits = stockUnitService.getListByItemDataOrderByDate(item1);
        assertEquals("wrong number of elements", 3, stockUnits.size());
        assertEquals("wrong element", stock1.getId(), stockUnits.get(0).getId());
        assertEquals("wrong element", stock2.getId(), stockUnits.get(1).getId());
        assertEquals("wrong element", stock3.getId(), stockUnits.get(2).getId());

        // check list by available amount
        stockUnits =
                stockUnitService.getListByItemDataOrderByAvailableAmount(item1);
        assertEquals("wrong number of elements", 3, stockUnits.size());
        assertEquals("wrong element", stock3.getId(), stockUnits.get(0).getId());
        assertEquals("wrong element", stock2.getId(), stockUnits.get(1).getId());
        assertEquals("wrong element", stock1.getId(), stockUnits.get(2).getId());
    }

    private synchronized void wa1t(long ms) {
        try {
            this.wait(ms);
        }
        catch (Exception ex) {
            // must not happen
        }
    }
}
