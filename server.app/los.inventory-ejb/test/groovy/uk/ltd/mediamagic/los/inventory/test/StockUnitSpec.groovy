package uk.ltd.mediamagic.los.inventory.test;

import org.apache.log4j.Logger
import org.junit.BeforeClass
import org.mywms.model.ItemData
import org.mywms.model.StockUnit

import de.linogistix.los.inventory.crud.StockUnitCRUDRemote
import de.linogistix.los.inventory.query.StockUnitQueryRemote
import de.linogistix.los.location.model.LOSUnitLoad

trait StockUnitSpec extends WMSSpecBase {
	private static final Logger log = Logger.getLogger(StockUnitSpec.class)
	static StockUnitQueryRemote suQuery;
	static StockUnitCRUDRemote suService;

	@BeforeClass
	void setupStockUnits() {
		suQuery = getBean(StockUnitQueryRemote.class);
		suService = getBean(StockUnitCRUDRemote.class);
	}

	def StockUnit createStockUnit(LOSUnitLoad ul, ItemData itemData, BigDecimal qty) {
		def su = new StockUnit()
		su.client = getClient()
		su.amount = qty
		su.unitLoad = ul
		su.itemData = itemData
		su = create(suService, su)
		return su
	}
	
	/**
	 * Create a number of stock units on at the given unit load.
	 * @param ul the unit load to assign to the stock units
	 * @param itemData the item data 
	 * @param qty the quantity on each stock unit
	 */
	def createStockUnitsOnUnitLoad(LOSUnitLoad ul, int stockunits, ItemData itemData, BigDecimal qty) {
		stockunits.times { slNo ->
			createStockUnit(ul, itemData, qty)
		}
	}

	def StockUnit getStockUnit(long id) {
		return suQuery.queryById(id)
	}

}
