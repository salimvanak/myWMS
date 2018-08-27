package uk.ltd.mediamagic.stocktaking.facade

import static org.junit.Assert.*

import java.util.Date

import de.linogistix.los.stocktaking.facade.LOSStocktakingFacade
import spock.lang.Specification
import uk.ltd.mediamagic.los.inventory.test.ItemDataSpec
import uk.ltd.mediamagic.los.inventory.test.StockUnitSpec
import uk.ltd.mediamagic.los.inventory.test.StorageLocationSpec
import uk.ltd.mediamagic.los.inventory.test.UnitLoadSpec

class StockTakingFacadeTest extends Specification  implements StorageLocationSpec, UnitLoadSpec, StockUnitSpec, ItemDataSpec {

	static LOSStocktakingFacade stocktakeFacade;
	
	def "Create a stock taking order"() {
		given:
			def startLoc = createStorageLocation("LOCSTART")
			def endLoc = createStorageLocation("LOCEND")
			def ul = createUnitLoad("STORE", startLoc)
			def su = createStockUnit(ul, createItemData(), 5);
			
		when: "Store the unit load on destination"
			orderCount = stocktakeFacade.generateOrders(true, null, null, null, null, sl.getId(), null, null, null, null, true, true)
		then:
			orderCount == 1
	}


}
