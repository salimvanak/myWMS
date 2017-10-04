package uk.ltd.mediamagic.mobile.processes.picking


import static org.junit.Assert.*

import de.linogistix.los.common.exception.LOSExceptionRB
import de.linogistix.los.example.InventoryTestTopologyRemote
import de.linogistix.los.example.LocationTestTopologyRemote
import de.linogistix.mobileserver.processes.picking.PickingMobileFacade
import spock.lang.Unroll

class PickingMobileFacadeTest extends WMSSpecBase {
	static def PickingMobileFacade pickingFacade;
	
	def setupSpec() {
		pickingFacade = beanLocator.getStateless(PickingMobileFacade.class)
	}
		
	@Unroll
	def "Check location scan for #unitloads ULs, #stockunits SUs"() {
		when:
			def sl = createStorageLocation("NON-SYSTEM", LocationTestTopologyRemote.PICKING_LOCATION_TYPE_NAME)
			createUnitLoadsWithStockUnits(sl, unitloads, stockunits, InventoryTestTopologyRemote.ITEM_A1_NUMBER, 10)
			pickingFacade.checkLocationScan(sl.name)
		then:
			notThrown(LOSExceptionRB)
			
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
			def sl = createStorageLocation("NON-SYSTEM", LocationTestTopologyRemote.PICKING_LOCATION_TYPE_NAME)
			createUnitLoadsWithStockUnits(sl, unitloads, stockunits, InventoryTestTopologyRemote.ITEM_A1_NUMBER, 10)
			pickingFacade.checkLocationScan(sl.name)
		then:
			def ex = thrown(LOSExceptionRB)
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
	def "Is location count indicated for #unitloads ULs, #stockunits SUs on location type #slTypeName"() {
		given:
			def sl = createStorageLocation("NON-SYSTEM", slTypeName)
			createUnitLoadsWithStockUnits(sl, unitloads, stockunits, InventoryTestTopologyRemote.ITEM_A1_NUMBER, 10)
		expect:
			pickingFacade.isLocationCountIndicated(sl.name) == locationCountAllowed;
		where:
			slTypeName                                            | unitloads | stockunits | locationCountAllowed 
			LocationTestTopologyRemote.PICKING_LOCATION_TYPE_NAME |         0 |          0 | true
			LocationTestTopologyRemote.PICKING_LOCATION_TYPE_NAME |         1 |          0 | true
			LocationTestTopologyRemote.PICKING_LOCATION_TYPE_NAME |         1 |          1 | true
			LocationTestTopologyRemote.PICKING_LOCATION_TYPE_NAME |         1 |          2 | false
			LocationTestTopologyRemote.PICKING_LOCATION_TYPE_NAME |         2 |          1 | false
			LocationTestTopologyRemote.PICKING_LOCATION_TYPE_NAME |         2 |          2 | false
			// we do not count system locations
			LocationTestTopologyRemote.SYSTEM_LOCATION_TYPE_NAME  |         0 |          0 | false
			LocationTestTopologyRemote.SYSTEM_LOCATION_TYPE_NAME  |         1 |          0 | false
			LocationTestTopologyRemote.SYSTEM_LOCATION_TYPE_NAME  |         1 |          1 | false
			LocationTestTopologyRemote.SYSTEM_LOCATION_TYPE_NAME  |         1 |          2 | false
			LocationTestTopologyRemote.SYSTEM_LOCATION_TYPE_NAME  |         2 |          1 | false
			LocationTestTopologyRemote.SYSTEM_LOCATION_TYPE_NAME  |         2 |          2 | false
	}

}
