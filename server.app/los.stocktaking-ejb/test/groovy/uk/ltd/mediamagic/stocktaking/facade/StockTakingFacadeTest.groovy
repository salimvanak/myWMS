package uk.ltd.mediamagic.stocktaking.facade

import static org.junit.Assert.*

import java.util.Date

import org.junit.BeforeClass

import de.linogistix.los.stocktaking.facade.LOSStocktakingFacade
import spock.lang.Specification
import uk.ltd.mediamagic.los.inventory.test.ItemDataSpec
import uk.ltd.mediamagic.los.inventory.test.StockUnitSpec
import uk.ltd.mediamagic.los.inventory.test.StorageLocationSpec
import uk.ltd.mediamagic.los.inventory.test.UnitLoadSpec

class StockTakingFacadeTest extends Specification  implements StorageLocationSpec, UnitLoadSpec, StockUnitSpec, ItemDataSpec {

	static LOSStocktakingFacade stocktakeFacade

  @BeforeClass
  def setupBeans() {
    stocktakeFacade = getBean(LOSStocktakingFacade.class)
  }

	def "Stock take a location to empty"() {
		given:
		  def startLoc = createStorageLocation("LOCSTART")
			def endLoc = createStorageLocation("LOCEND")
			def ul = createUnitLoad("STORE", startLoc)
			def su = createStockUnit(ul, createItemData(), 5);
			
		when: "Store the unit load on destination"
			def orderCount = stocktakeFacade.generateOrders(true, null, null, null, null, startLoc.getId(), null, null, null, null, true, true)
		then:
			orderCount == 1
      ulQuery.countUnitLoadsByStorageLocation(startLoc) == 1

    when: "the location is empty"
      stocktakeFacade.processLocationEmpty(startLoc)
    then: "Then there are no more locations"
      stocktakeFacade.getNextLocation(getClient(), startLoc.getName()) == null
      ulQuery.countUnitLoadsByStorageLocation(startLoc) == 0
  }



}
