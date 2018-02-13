package uk.ltd.mediamagic.mobile.processes.picking


import static org.junit.Assert.*

import org.mywms.facade.FacadeException

import de.linogistix.mobileserver.processes.picking.PickingMobileFacade
import groovy.util.logging.Log4j
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll
import uk.ltd.mediamagic.los.inventory.test.ItemDataSpec
import uk.ltd.mediamagic.los.inventory.test.StockUnitSpec
import uk.ltd.mediamagic.los.inventory.test.StorageLocationSpec
import uk.ltd.mediamagic.los.inventory.test.UnitLoadSpec
import uk.ltd.mediamagic.los.inventory.test.WMSSpecBase

@Log4j
class PickingMobileFacadeTest extends Specification 
	implements WMSSpecBase, UnitLoadSpec, StockUnitSpec, ItemDataSpec, StorageLocationSpec {
	
	@Shared PickingMobileFacade pickingFacade;
	
	void setupSpec() {
		pickingFacade = getBean(PickingMobileFacade.class)
	}
		
	@Unroll
	def "Check location scan for #unitloads ULs, #stockunits SUs"() {
		when:
			def sl = createPickingLocation("NON-SYSTEM")
			def itemData = createItemData()
			createUnitLoadsOnLocation(sl, unitloads).each {
				createStockUnitsOnUnitLoad(it, stockunits, itemData, 10)
			}
			pickingFacade.checkLocationScan(sl.name)
		then:
			notThrown(FacadeException.class)
			
		where: 
			unitloads | stockunits 
			        0 |          0 
			        1 |          0 
			        1 |          1 
			        1 |          2 
	}
	
	@Unroll
	def "Check FAILED location scan for #unitloads ULs, #stockunits SUs"() {
		when:
			def sl = createPickingLocation("NON-SYSTEM")
			def itemData = createItemData()
			createUnitLoadsOnLocation(sl, unitloads).each {
				createStockUnitsOnUnitLoad(it, stockunits, itemData, 10)
			}
			pickingFacade.checkLocationScan(sl.name)
		then:
			def ex = thrown(FacadeException.class)
			ex.key == "LocationNotUnique"
			
		where: 
			unitloads | stockunits 
							2 |          0 
							2 |          1 
							2 |          2 
							3 |          0 
							3 |          1
							3 |          2
	}

	@Unroll
	def "Check FAILED location scan for unknown location"() {
		when:
			pickingFacade.checkLocationScan("NON-SYSTEM")
		then:
			thrown(Exception)			
	}

	@Unroll
	def "Is location count indicated for #unitloads ULs, #stockunits SUs on location type #slType"() {
		given:
			def sl = createStorageLocation("NON-SYSTEM", slType)
			def itemData = createItemData()
			createUnitLoadsOnLocation(sl, unitloads).each {
				createStockUnitsOnUnitLoad(it, stockunits, itemData, 10)
			}
		expect:
			pickingFacade.isLocationCountIndicated(sl.name) == locationCountAllowed;
		where:
			slType                          | unitloads | stockunits | locationCountAllowed 
			getDefaultStorageLocationType() |         0 |          0 | true
			getDefaultStorageLocationType() |         1 |          0 | true
			getDefaultStorageLocationType() |         1 |          1 | true
			getDefaultStorageLocationType() |         1 |          2 | false
			getDefaultStorageLocationType() |         2 |          1 | false
			getDefaultStorageLocationType() |         2 |          2 | false
			// we do not count system locations
			getSystemLocationType()         |         0 |          0 | false
			getSystemLocationType()         |         1 |          0 | false
			getSystemLocationType()         |         1 |          1 | false
			getSystemLocationType()         |         1 |          2 | false
			getSystemLocationType()         |         2 |          1 | false
			getSystemLocationType()         |         2 |          2 | false
	}

}
