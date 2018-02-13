package uk.ltd.mediamagic.mobile.processes.info;

import org.mywms.model.ItemData
import org.mywms.model.StockUnit

import de.linogistix.los.location.model.LOSStorageLocation
import de.linogistix.los.location.model.LOSUnitLoad
import geb.spock.GebSpec
import groovy.util.logging.Log4j
import uk.ltd.mediamagic.los.inventory.test.ItemDataSpec
import uk.ltd.mediamagic.los.inventory.test.StockUnitSpec
import uk.ltd.mediamagic.los.inventory.test.StorageLocationSpec
import uk.ltd.mediamagic.los.inventory.test.UnitLoadSpec
import uk.ltd.mediamagic.los.inventory.test.WMSSpecBase
import uk.ltd.mediamagic.mobile.CurrentTest
import uk.ltd.mediamagic.mobile.WithAuth
import uk.ltd.mediamagic.mobile.pages.InfoPage

@Log4j
public class InfoTest extends GebSpec implements 
	WithAuth, WMSSpecBase, ItemDataSpec, UnitLoadSpec, StorageLocationSpec, StockUnitSpec {
	
	def "Get info for stock"() {
		when:
			ItemData itemData = createItemData();
			at viaLogin(new InfoPage(header: 'Info: Code'))
			
		then:
			input(itemData.number)
			at new InfoPage(header: "Info: Item")
			$(id:'Form:itemData').text() == itemData.number
	}

	def "Get info for unitload"() {
		given:
			LOSStorageLocation loc = createPickingLocation("INFO")
			LOSUnitLoad ul = createUnitLoad("info-UL", loc)
			StockUnit su = createStockUnit(ul, createItemData(), 5)

		when: "Unit load is scanned"		
			at viaLogin(new InfoPage(header: 'Info: Code'))
			input(ul.labelId)
		then: "The unit load information is displayed"
			at new InfoPage(header: "Info: Unit Load & Stock")
			$(id:'Form:unitLoadData').text() == ul.labelId
			
		when: "Unit load is clicked"
			$(id:'Form:unitLoadLabel').click()
		then: "The stock unit information is diplayed"
			at new InfoPage(header: "Info: Unit Load")
			$(id:'Form:unitLoadData').text() == su.unitLoad.labelId

		when: "Stock Units is clicked"
			$(id:'Form:contentLabel').click()
		then: "The stock unit information is diplayed"
			at new InfoPage(header: "Info: Stock")
			$(id:'Form:unitLoadData').text() == su.getUnitLoad().labelId
			$(id:'Form:itemData').text() == su.itemData.number			
	}


	def "Get info for location"() {
		when:
			LOSStorageLocation loc = createPickingLocation("INFO")
			
			at viaLogin(new InfoPage(header: 'Info: Code'))
			
		then:
			input(loc.name)
			at new InfoPage(header: "Info: Location")
			$(id:'Form:locationData').text() == loc.name
	}
	
	def "Get info for location scan code"() {
		when:
			LOSStorageLocation loc = createPickingLocation("INFO")
			
			at viaLogin(new InfoPage(header: 'Info: Code'))
			
		then:
			input(loc.scanCode)
			at new InfoPage(header: "Info: Location")
			$(id:'Form:locationData').text() == loc.name
	}
}
