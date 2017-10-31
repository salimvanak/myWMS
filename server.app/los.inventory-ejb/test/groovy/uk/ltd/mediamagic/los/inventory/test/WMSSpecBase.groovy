package uk.ltd.mediamagic.mobile

import org.apache.log4j.Level
import org.junit.After
import org.junit.AfterClass
import org.junit.BeforeClass
import org.mywms.facade.MyWMSSpecification
import org.mywms.globals.SerialNoRecordType
import org.mywms.model.StockUnit

import de.linogistix.los.crud.ClientCRUDRemote
import de.linogistix.los.example.CommonTestTopologyRemote
import de.linogistix.los.example.InventoryTestTopologyRemote
import de.linogistix.los.example.LocationTestTopologyRemote
import de.linogistix.los.inventory.crud.ItemDataCRUDRemote
import de.linogistix.los.inventory.crud.StockUnitCRUDRemote
import de.linogistix.los.inventory.query.ItemDataQueryRemote
import de.linogistix.los.inventory.query.StockUnitQueryRemote
import de.linogistix.los.inventory.query.dto.StockUnitTO
import de.linogistix.los.location.crud.LOSAreaCRUDRemote
import de.linogistix.los.location.crud.LOSRackCRUDRemote
import de.linogistix.los.location.crud.LOSStorageLocationCRUDRemote
import de.linogistix.los.location.crud.LOSStorageLocationTypeCRUDRemote
import de.linogistix.los.location.crud.LOSTypeCapacityConstraintCRUDRemote
import de.linogistix.los.location.crud.UnitLoadCRUDRemote
import de.linogistix.los.location.crud.UnitLoadTypeCRUDRemote
import de.linogistix.los.location.model.LOSStorageLocation
import de.linogistix.los.location.model.LOSStorageLocationType
import de.linogistix.los.location.model.LOSUnitLoad
import de.linogistix.los.location.query.LOSAreaQueryRemote
import de.linogistix.los.location.query.LOSStorageLocationQueryRemote
import de.linogistix.los.location.query.LOSStorageLocationTypeQueryRemote
import de.linogistix.los.location.query.LOSTypeCapacityConstraintQueryRemote
import de.linogistix.los.location.query.RackQueryRemote
import de.linogistix.los.location.query.UnitLoadQueryRemote
import de.linogistix.los.location.query.UnitLoadTypeQueryRemote
import de.linogistix.los.location.query.dto.StorageLocationTO
import de.linogistix.los.location.query.dto.UnitLoadTO
import de.linogistix.los.query.ClientQueryRemote
import groovy.util.logging.Log4j
import uk.ltd.mediamagic.los.inventory.test.TopologyHelper

@Log4j
trait WMSSpecBase extends MyWMSSpecification {
	static def ClientCRUDRemote clService;
	static def ClientQueryRemote clQuery;
	
	static def LOSStorageLocationTypeQueryRemote slTypeQuery;
	static def LOSStorageLocationTypeCRUDRemote slTypeService;
	static def LOSStorageLocationQueryRemote slQuery;
	static def LOSStorageLocationCRUDRemote slService;

	static def UnitLoadTypeQueryRemote ulTypeQuery;
	static def UnitLoadTypeCRUDRemote ulTypeService;
	static def UnitLoadQueryRemote ulQuery;
	static def UnitLoadCRUDRemote ulService;
	
	static def StockUnitQueryRemote suQuery;
	static def StockUnitCRUDRemote suService;
	
	static def LOSTypeCapacityConstraintQueryRemote capacityQuery;
	static def LOSTypeCapacityConstraintCRUDRemote capacityService;
	static def LOSAreaQueryRemote areaQuery;
	static def LOSAreaCRUDRemote areaService;
	static def RackQueryRemote rackQuery;
	static def LOSRackCRUDRemote rackService;
	
	static def ItemDataCRUDRemote itemService;
	static def ItemDataQueryRemote itemQuery;
	
	static def LocationTestTopologyRemote locTopology;
	static def InventoryTestTopologyRemote invTopology;
	static def CommonTestTopologyRemote commonTopology;

	def serialNumber = 1;
	
	def ulList = []
	def suList = []
	def slList = []
	
	@BeforeClass
	void setupTestTopology() {
		log.setLevel(Level.INFO)
    clService = getBean(ClientCRUDRemote.class);
  	locTopology = getBean(LocationTestTopologyRemote.class, "los.location-comp");
    invTopology = getBean(InventoryTestTopologyRemote.class, "los.inventory-comp");
    commonTopology = getBean(CommonTestTopologyRemote.class, "los.common-comp");
				
    slService = getBean(LOSStorageLocationCRUDRemote.class);
    ulService = getBean(UnitLoadCRUDRemote.class);
    suService = getBean(StockUnitCRUDRemote.class);
    ulTypeService = getBean(UnitLoadTypeCRUDRemote.class);
    slTypeService = getBean(LOSStorageLocationTypeCRUDRemote.class);
    capacityService = getBean(LOSTypeCapacityConstraintCRUDRemote.class);
    areaService = getBean(LOSAreaCRUDRemote.class);
    rackService = getBean(LOSRackCRUDRemote.class);
		itemService = getBean(ItemDataCRUDRemote.class);
		
    clQuery = getBean(ClientQueryRemote.class);
    slQuery = getBean(LOSStorageLocationQueryRemote.class);
    ulQuery = getBean(UnitLoadQueryRemote.class);
    suQuery = getBean(StockUnitQueryRemote.class);
    ulTypeQuery = getBean(UnitLoadTypeQueryRemote.class);
    slTypeQuery = getBean(LOSStorageLocationTypeQueryRemote.class);
    capacityQuery = getBean(LOSTypeCapacityConstraintQueryRemote.class);
    areaQuery = getBean(LOSAreaQueryRemote.class);
    rackQuery = getBean(RackQueryRemote.class);
    itemQuery = getBean(ItemDataQueryRemote.class);
	}
	
	@AfterClass
	void cleanTestTopology() {
	}
		
	@After
	void cleanupTopology() {
		log.info "Deleting stock units " + suList
		base.suService.delete(suList)
		log.info "Deleting unitloads " + ulList
		base.ulService.delete(ulList)
		log.info "Deleting storage locations " + ulList
		base.slService.delete(slList)
	}
	
	private def createLabel(String type, String tag) {
		serialNumber = serialNumber + 1
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
