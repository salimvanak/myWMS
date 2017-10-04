package uk.ltd.mediamagic.mobile.processes.picking

import org.apache.log4j.Level
import org.mywms.facade.MyWMSSpecification
import org.mywms.model.StockUnit

import de.linogistix.los.example.CommonTestTopologyRemote
import de.linogistix.los.example.LocationTestTopologyRemote
import de.linogistix.los.inventory.query.dto.StockUnitTO
import de.linogistix.los.location.model.LOSStorageLocation
import de.linogistix.los.location.model.LOSStorageLocationType
import de.linogistix.los.location.model.LOSUnitLoad
import de.linogistix.los.location.query.dto.StorageLocationTO
import de.linogistix.los.location.query.dto.UnitLoadTO
import groovy.util.logging.Log4j
import uk.ltd.mediamagic.los.inventory.test.TopologyHelper

@Log4j
class WMSSpecBase extends MyWMSSpecification {
	
	static def TopologyHelper base;

	def serialNumber = 1;
	
	def ulList = []
	def suList = []
	def slList = []
	
	def setupSpec() {
		log.setLevel(Level.INFO)
		if (base == null) {
			base = new TopologyHelper(beanLocator)
			base.createTestTopology()
		}
	}
	
	def cleanSpec() {
		if (base != null) {
			base.removeTestTopology()
			base = null;
		}
	}
	
	def setup() {
		
	}
	
	def cleanup() {
		System.out.println "************** deleting ul " + ulList
		log.info "Deleting stock units " + suList
		base.suService.delete(suList)
		log.info "Deleting unitloads " + ulList
		base.ulService.delete(ulList)
		log.info "Deleting storage locations " + ulList
		base.slService.delete(slList)
	}
	
	private def createLabel(String type, String tag) {
		serialNumber ++
		return type + "/" + tag +"-" + serialNumber
	}
	
	/**
	 * Create a number of unit loads at the give location with a number of stock units.
	 * @param sl the location to store the unit loads
	 * @param unitloads the number of unit loads
	 * @param stockunits the number of stock units
	 */
	def createUnitLoadsWithStockUnits(LOSStorageLocation sl, int unitloads, int stockunits, String itemNr, BigDecimal qty) {
		unitloads.times({
			def ul = createUnitload("COUNT", sl);
			stockunits.times({ slNo ->
				createStockUnit(ul, itemNr, qty)
			})
		})
	}

	def LOSStorageLocation createStorageLocation(String tag, String locationTypeName) {
		def locationType = base.slTypeQuery.queryByIdentity(locationTypeName)
		assert locationType != null
		def area = base.areaQuery.queryByIdentity(LocationTestTopologyRemote.PICKING_AREA_NAME);
		assert area != null
		
		LOSStorageLocation sl = new LOSStorageLocation()
		sl.client = base.clQuery.queryByIdentity(CommonTestTopologyRemote.TESTCLIENT_NUMBER)
		sl.name = createLabel("SL", tag)
		sl.type = locationType
		sl.area = area
		
		sl = base.slService.create(sl)
		slList << new StorageLocationTO(sl)
		return sl
	}
		
	def LOSUnitLoad createUnitload(String tag, String locationName) {
		return createUnitload(tag, base.slQuery.queryByIdentity(locationName))
	}
	
	def LOSUnitLoad createUnitload(String tag, LOSStorageLocation location) {
		def ul = new LOSUnitLoad()
		ul.client = base.clQuery.queryByIdentity(CommonTestTopologyRemote.TESTCLIENT_NUMBER)
		ul.labelId = createLabel("UL", tag)
		ul.storageLocation = location
		ul.type = base.ulTypeQuery.getDefaultUnitLoadType()
		ul = base.ulService.create(ul)
		assert ul.id != null
		ulList << new UnitLoadTO(ul)
		log.info "Creating unit load " + ul.labelId + "(" + ul.id + ")"
		return ul
	}
	
	
	def StockUnit createStockUnit(LOSUnitLoad ul, String itemNr, BigDecimal qty) {
		def su = new StockUnit();
		su.client = base.clQuery.queryByIdentity(CommonTestTopologyRemote.TESTCLIENT_NUMBER)
		su.amount = qty
		su.unitLoad = ul
		su.itemData = base.itemQuery.queryByIdentity(itemNr);		
		su = base.suService.create(su)
		assert su.id != null
		suList << new StockUnitTO(su)
		return su;
	}
}
