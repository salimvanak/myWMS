package uk.ltd.mediamagic.stocktaking.facade

import static org.junit.Assert.*

import org.junit.BeforeClass

import de.linogistix.los.stocktaking.facade.LOSStocktakingFacade
import de.linogistix.los.stocktaking.model.LOSStocktakingState
import de.linogistix.los.stocktaking.exception.*
import spock.lang.Ignore
import spock.lang.Specification
import uk.ltd.mediamagic.los.inventory.test.ItemDataSpec
import uk.ltd.mediamagic.los.inventory.test.StockUnitSpec
import uk.ltd.mediamagic.los.inventory.test.StorageLocationSpec
import uk.ltd.mediamagic.los.inventory.test.UnitLoadSpec
import uk.ltd.mediamagic.stocktaking.test.StockTakingOrderSpec

class StockTakingFacadeTest extends Specification  implements StockTakingOrderSpec, StorageLocationSpec, UnitLoadSpec, StockUnitSpec, ItemDataSpec {

	def "Stock take a location to empty"() {
		given:
		  def startLoc = createStorageLocation("LOCSTART")
			def ul = createUnitLoad("STORE", startLoc)
			def su = createStockUnit(ul, createItemData(), 5);
			
		when: "Create a stocktake order for a location"
			def orderCount = createStockTakingOrders(startLoc)
		then:
			orderCount == 1
      ulQuery.countUnitLoadsByStorageLocation(startLoc) == 1

    when: "the location is empty"
      stockTakeFacade.processLocationEmpty(startLoc)
    then: "Then there are no more locations"
      stockTakeFacade.getNextLocation(getClient(), startLoc.name) == null
			ulQuery.countUnitLoadsByStorageLocation(startLoc) == 1
			def stOrder = getOrderForStorageLocation(startLoc.name)
			stOrder.size() == 1
			stOrder[0].locationName == startLoc.name
			stOrder[0].state == LOSStocktakingState.COUNTED.name()
			def stRecord = getRecordsForOrder(stOrder.get(0))
			stRecord.size() == 1
			stRecord[0].countedQuantity == 0
			stRecord[0].plannedQuantity == su.amount
			stRecord[0].unitLoad == ul.labelId
			stRecord[0].state == LOSStocktakingState.COUNTED.name()
			
    //FIXME Not sure why this does not work.
		//when: "The count is accepted"
		//	stockTakeFacade.acceptOrder(stOrder[0].id)
		//then: "The location will be empty"
		//	ulQuery.countUnitLoadsByStorageLocation(startLoc) == 0
  }

	def "Count the exact stock on location"() {
		given:
			def stockAmount = 5
			def startLoc = createStorageLocation("LOCSTART")
			def ul = createUnitLoad("STORE", startLoc)
			def su = createStockUnit(ul, createItemData(), stockAmount);
		expect: "One unit load on the location"
			ulQuery.countUnitLoadsByStorageLocation(startLoc) == 1
			
		when: "Create a stocktake order for a location"
			def orderCount = createStockTakingOrders(startLoc)
		then: "One order is created"
			orderCount == 1

		when: "Start counting"
			stockTakeFacade.processLocationStart(startLoc)
		then:
			def stOrders = getOrderForStorageLocation(startLoc.name)
      stOrders.size() == 1
			stOrders[0].with {
			  state == LOSStocktakingState.STARTED.name() 
			}

		when: "The unitload is counted"
			stockTakeFacade.processUnitloadStart(startLoc.name, ul.labelId)
			stockTakeFacade.processStockCount(su, stockAmount)
			stockTakeFacade.processLocationFinish(startLoc)
		then: "The stock take order will be automatically accepted"
			stockTakeFacade.getNextLocation(getClient(), startLoc.name) == null
			ulQuery.countUnitLoadsByStorageLocation(startLoc) == 1
			def stOrder = getOrderForStorageLocation(startLoc.name)
      stOrder.size() == 1
			stOrder[0].with {
        locationName == startLoc.name
			  state == LOSStocktakingState.FINISHED.name()
      }
     
      def stRecords = getRecordsForOrder(stOrder.get(0))
			stRecords.size() == 1
			stRecords[0].with {
        countedQuantity == su.amount
			  plannedQuantity == su.amount
			  unitLoad == ul.labelId
			  state == LOSStocktakingState.FINISHED.name()			
      }
  }

	def "An extra unit load found"() {
		given:
			def stockAmount = 5
			def startLoc = createStorageLocation("LOCSTART")
			def ul = createUnitLoad("STORE", startLoc)
      def missingItem = createItemData()
			def su = createStockUnit(ul, createItemData(), stockAmount);
      def newUnitLoadLabel = createUnitLoadLabel("STORE")
    expect: "One unit load on the location"
			ulQuery.countUnitLoadsByStorageLocation(startLoc) == 1
			
		when: "Create a stocktake order for a location"
			def orderCount = createStockTakingOrders(startLoc)
		then: "One order is created"
			orderCount == 1

		when: "Start counting"
			stockTakeFacade.processLocationStart(startLoc)
			stockTakeFacade.processUnitloadStart(startLoc.name, ul.labelId)
			stockTakeFacade.processStockCount(su, stockAmount)
      stockTakeFacade.processStockCount(getClient().name, startLoc.name, newUnitLoadLabel, 
          missingItem.number, null, null, new BigDecimal(stockAmount), ul.type.name) 
		then: "There should be a record of the count"
      def stOrders = getOrderForStorageLocation(startLoc.name)
			def record = getRecordsForOrder(stOrders[0])
			record.size() == 2;
	  	record.any {
				it.state == LOSStocktakingState.STARTED.name()
				it.unitLoad == ul.labelId
				it.plannedQuantity == stockAmount
				it.countedQuantity == stockAmount
			}
		  record.any {
				it.state == LOSStocktakingState.STARTED.name()
				it.unitLoad == newUnitLoadLabel
				it.plannedQuantity == 0
				it.countedQuantity == stockAmount
			}
	}
  
  def "Recounting the same unit load before the count is finished"() {
		given:
			def stockAmount = 5
			def startLoc = createStorageLocation("LOCSTART")
			def ul = createUnitLoad("STORE", startLoc)
			def su = createStockUnit(ul, createItemData(), stockAmount);
		expect: "One unit load on the location"
			ulQuery.countUnitLoadsByStorageLocation(startLoc) == 1
			
		when: "Create a stocktake order for a location"
			def orderCount = createStockTakingOrders(startLoc)
		then: "One order is created"
			orderCount == 1

		when: "Start counting"
			stockTakeFacade.processLocationStart(startLoc)
		then: "The order should change state to started"
			def stOrders = getOrderForStorageLocation(startLoc.name)
			stOrders.size() == 1
			stOrders[0].with {
				locationName == startLoc.name
				unitLoadLabel == ul.labelId
				state == LOSStocktakingState.STARTED.name()
			}

		when: "The unit load is started"
			stockTakeFacade.processUnitloadStart(startLoc.name, ul.labelId)
			stockTakeFacade.processStockCount(su, stockAmount)
		then: "There should be a record of the count"
			def record = getRecordsForOrder(stOrders[0])
			record.size() == 1;
			record[0].with {
				state == LOSStocktakingState.STARTED.name()
				unitLoad == ul.labelId
				plannedQuantity == stockAmount
				countedQuantity == stockAmount
			}
		
		when: "The unit load is started again"
			stockTakeFacade.processUnitloadStart(startLoc.name, ul.labelId)
		then: "The previous count is removed"
			getRecordsForOrder(stOrders[0]).isEmpty();
	}

	def "An extra unit in a different location"() {
		given:
			def stockAmount = 5
			def startLoc = createStorageLocation("LOCSTART")
			def endLoc = createStorageLocation("LOCEND")
			def ul = createUnitLoad("STORE", startLoc)
      def missingItem = createItemData()
			def su = createStockUnit(ul, createItemData(), stockAmount);
      def newUnitLoadLabel = createUnitLoadLabel("STORE")
    expect: "One unit load on the location"
			ulQuery.countUnitLoadsByStorageLocation(startLoc) == 1
			
		when: "Create a stocktake order for a location"
			def orderCount = createStockTakingOrders(startLoc)
		then: "One order is created"
			orderCount == 1

		when: "Start counting and count a new unitload in another location"
			stockTakeFacade.processLocationStart(startLoc)
      stockTakeFacade.processStockCount(getClient().name, endLoc.name, newUnitLoadLabel, 
          missingItem.number, null, null, new BigDecimal(stockAmount), ul.type.name) 
		then: "Expection because the new location is not started"
      LOSStockTakingException ex = thrown()
      ex.stockTakingExceptionKey == LOSStockTakingExceptionKey.NO_ACTIVE_STOCKTAKING_ORDER
	}

	def "If we scan the wrong unitload"() {
		given:
		  def startLoc = createStorageLocation("LOCSTART")
			def ul = createUnitLoad("STORE", startLoc)
		  def otherLoc = createStorageLocation("LOC")
			def ulWrong = createUnitLoad("STORE", otherLoc)
			def su = createStockUnit(ul, createItemData(), 5);
			def suWrong = createStockUnit(ulWrong, createItemData(), 5);

		when: "Create a stocktake order for a location"
			def orderCount = createStockTakingOrders(startLoc)
		then:
			orderCount == 1
      ulQuery.countUnitLoadsByStorageLocation(startLoc) == 1

    when: "when the wrong unit load is scanned"
      stockTakeFacade.processLocationStart(startLoc)
      stockTakeFacade.processUnitloadStart(otherLoc.name, ulWrong.labelId)
    then: "An exception is thrown"
      LOSStockTakingException ex = thrown()
      ex.stockTakingExceptionKey == LOSStockTakingExceptionKey.NO_ACTIVE_STOCKTAKING_ORDER
 
    when: "we the wrong unit load is scanned"
      stockTakeFacade.processUnitloadStart(startLoc.name, ulWrong.labelId)
      stockTakeFacade.processStockCount(suWrong, 6)
    then: "An exception is thrown"
      LOSStockTakingException ex2 = thrown()
      ex2.stockTakingExceptionKey == LOSStockTakingExceptionKey.NO_ACTIVE_STOCKTAKING_ORDER
  }


}
