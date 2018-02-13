package uk.ltd.mediamagic.los.inventory.test

import org.apache.log4j.Logger
import org.junit.BeforeClass
import org.mywms.model.ItemData
import org.mywms.model.ItemUnit

import de.linogistix.los.inventory.crud.ItemDataCRUDRemote
import de.linogistix.los.inventory.crud.ItemUnitCRUDRemote
import de.linogistix.los.inventory.query.ItemDataQueryRemote
import de.linogistix.los.inventory.query.ItemUnitQueryRemote
import groovy.transform.CompileDynamic
import groovy.transform.CompileStatic
import groovy.util.logging.Log4j

@Log4j
@CompileStatic
trait ItemDataSpec extends WMSSpecBase {
	private static final Logger log = Logger.getLogger(ItemDataSpec.class)
	
	static final String PCS_UNIT = "PCS";
	
	static ItemDataCRUDRemote itemService
	static ItemDataQueryRemote itemQuery
	static ItemUnitCRUDRemote itemUnitService
	static ItemUnitQueryRemote itemUnitQuery
	
	@CompileDynamic 
	@BeforeClass
	void setupItemDataSpec() {
		itemService = getBean(ItemDataCRUDRemote.class)
		itemQuery = getBean(ItemDataQueryRemote.class)
		itemUnitService = getBean(ItemUnitCRUDRemote.class)
		itemUnitQuery = getBean(ItemUnitQueryRemote.class)
	}
	
	ItemUnit createItemUnit() {
		itemUnitQuery.getDefault();
	}

	def createItemData() {
		ItemData item = new ItemData()
		item.number = "TESTITEM-" + nextSeqNumber()
		item.description = "Item data for testing"
		item.name = "Item data for testing"
		item.handlingUnit = createItemUnit();
		item.client = getClient()
		item = create(itemService, item)
		return item;
	}	
}
