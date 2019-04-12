package uk.ltd.mediamagic.los.inventory.test;

import org.apache.log4j.Logger
import org.junit.BeforeClass
import org.mywms.model.Area
import org.mywms.model.UnitLoadType

import de.linogistix.los.inventory.facade.StorageFacade
import de.linogistix.los.location.crud.LOSAreaCRUDRemote
import de.linogistix.los.location.crud.LOSRackCRUDRemote
import de.linogistix.los.location.crud.LOSStorageLocationCRUDRemote
import de.linogistix.los.location.crud.LOSStorageLocationTypeCRUDRemote
import de.linogistix.los.location.crud.LOSTypeCapacityConstraintCRUDRemote
import de.linogistix.los.location.model.LOSArea
import de.linogistix.los.location.model.LOSStorageLocation
import de.linogistix.los.location.model.LOSStorageLocationType
import de.linogistix.los.location.model.LOSTypeCapacityConstraint
import de.linogistix.los.location.query.LOSAreaQueryRemote
import de.linogistix.los.location.query.LOSStorageLocationQueryRemote
import de.linogistix.los.location.query.LOSStorageLocationTypeQueryRemote
import de.linogistix.los.location.query.LOSTypeCapacityConstraintQueryRemote
import de.linogistix.los.location.query.RackQueryRemote
import de.linogistix.los.location.query.UnitLoadTypeQueryRemote
import de.linogistix.los.query.exception.BusinessObjectNotFoundException
import groovy.util.logging.Log4j

trait StorageLocationSpec implements WMSSpecBase {
	private static final Logger log = Logger.getLogger(StorageLocationSpec.class)
	
	static final String PICKING_AREA_NAME = "TEST-PICKING-AREA"
	static final String STORE_AREA_NAME = "TEST-STORE-AREA"
	static final String GOODS_IN_AREA_NAME = "TEST-GOODS-IN-AREA"
	static final String GOODS_OUT_AREA_NAME = "TEST-GOODS-OUT-AREA"
	
	static final String STORAGE_LOCATION_TYPE_NAME = "TEST-ONE-PALLET-LOCATION-TYPE"
	static final String PICKING_LOCATION_TYPE_NAME = "TEST-PICKING-LOCATION-TYPE"
	static final String ONE_PALLET_LOCATION_TYPE_NAME = "TEST-PICKING-LOCATION-TYPE"
	
	static LOSStorageLocationTypeQueryRemote slTypeQuery
	static LOSStorageLocationTypeCRUDRemote slTypeCRUD
	static LOSStorageLocationQueryRemote slQuery
	static LOSStorageLocationCRUDRemote slCRUD
	static StorageFacade storageFacade
	
	static LOSTypeCapacityConstraintQueryRemote capacityQuery
	static LOSTypeCapacityConstraintCRUDRemote capacityCRUD
	static LOSAreaQueryRemote areaQuery
	static LOSAreaCRUDRemote areaCRUD
	static RackQueryRemote rackQuery
	static LOSRackCRUDRemote rackCRUD

	static UnitLoadTypeQueryRemote ulTypeQuery
	
	private def createLabel(String prefix, String tag) {
		def serialNumber = nextSeqNumber()
		return prefix + "/" + tag +"-" + serialNumber
	}

	@BeforeClass
	void setupStorageLocations() {
		ulTypeQuery = getBean(UnitLoadTypeQueryRemote.class);
    slCRUD = getBean(LOSStorageLocationCRUDRemote.class);
    storageFacade = getBean(StorageFacade.class);
    slTypeCRUD = getBean(LOSStorageLocationTypeCRUDRemote.class);
    capacityCRUD = getBean(LOSTypeCapacityConstraintCRUDRemote.class);
    areaCRUD = getBean(LOSAreaCRUDRemote.class);
    rackCRUD = getBean(LOSRackCRUDRemote.class);
    slQuery = getBean(LOSStorageLocationQueryRemote.class);
    slTypeQuery = getBean(LOSStorageLocationTypeQueryRemote.class);
    capacityQuery = getBean(LOSTypeCapacityConstraintQueryRemote.class);
    areaQuery = getBean(LOSAreaQueryRemote.class);
    rackQuery = getBean(RackQueryRemote.class);
	}
		
	def getPickingArea() {
		LOSArea area = new LOSArea();
		area.client = getClient();
		area.name = createLabel("AREA", "PICKING");
		area.useForPicking = true;
		area = create(areaCRUD, area);
		return area;
	}

	def getStorageArea() {
		LOSArea area = new LOSArea();
		area.client = getClient();
		area.name = createLabel("AREA", "STORE");
		area.useForStorage = true;
		area = create(areaCRUD, area);
		assert area != null
		return area;
	}
	
	def createOnePalletConstraint(LOSStorageLocationType slt) {
		return createConstraint(slt, ulTypeQuery.getDefaultUnitLoadType(), 100)
	}

	def createConstraint(LOSStorageLocationType slt, UnitLoadType ult, BigDecimal allocation) {
		LOSTypeCapacityConstraint constraint = new LOSTypeCapacityConstraint()
		constraint.storageLocationType = slt
		constraint.unitLoadType = ult
		constraint.allocation = allocation
		constraint = create(capacityCRUD, constraint)
	}


	def getDefaultStorageLocationType() {
		LOSStorageLocationType locationType
		locationType = new LOSStorageLocationType()
		locationType.name = createLabel("SLT", "DEFAULT")
		locationType = create(slTypeCRUD, locationType)
		createOnePalletConstraint(locationType)
				
		return locationType
	}

	def getSystemLocationType() {
		return slTypeQuery.queryById(1);
	}

	def LOSStorageLocation createPickingLocation(String tag) {
		return createStorageLocation(tag, getDefaultStorageLocationType(), getPickingArea())
	}

	def LOSStorageLocation createStorageLocation(String tag) {
		return createStorageLocation(tag, getDefaultStorageLocationType())
	}

	def LOSStorageLocation createStorageLocation(String tag, LOSStorageLocationType slt) {
		return createStorageLocation(tag, slt, getStorageArea())
	}

	def LOSStorageLocation createStorageLocation(String tag, LOSStorageLocationType slt, Area area) {
		LOSStorageLocation sl = new LOSStorageLocation()
		sl.client = getClient()
		sl.name = createLabel("SL", tag)
		sl.scanCode = "-" + sl.name + "-"
		sl.type = slt
		sl.area = area
		
		sl = create(slCRUD, sl)
		return sl
	}
}
