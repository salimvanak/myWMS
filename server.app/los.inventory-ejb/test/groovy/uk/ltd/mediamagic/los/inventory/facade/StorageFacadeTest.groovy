package uk.ltd.mediamagic.los.inventory.facade

import static org.junit.Assert.*

import de.linogistix.los.inventory.exception.InventoryException
import de.linogistix.los.inventory.exception.InventoryExceptionKey
import de.linogistix.los.location.constants.LOSStorageLocationLockState
import de.linogistix.los.location.constants.LOSUnitLoadLockState
import de.linogistix.los.location.exception.LOSLocationException
import de.linogistix.los.location.exception.LOSLocationExceptionKey
import spock.lang.Specification
import uk.ltd.mediamagic.los.inventory.test.ItemDataSpec
import uk.ltd.mediamagic.los.inventory.test.StockUnitSpec
import uk.ltd.mediamagic.los.inventory.test.StorageLocationSpec
import uk.ltd.mediamagic.los.inventory.test.UnitLoadSpec

class StorageFacadeTest extends Specification implements StorageLocationSpec, UnitLoadSpec, StockUnitSpec, ItemDataSpec {

	def "Store a unit load"() {
		given:
			def startLoc = createStorageLocation("LOCSTART")
			def endLoc = createStorageLocation("LOCEND")
			def ul = createUnitLoad("STORE", startLoc)
			
		when: "Store the unit load on destination"
			storageFacade.finishStorageRequest(ul.labelId, endLoc.name, false, true)
		then:
			getUnitLoad(ul.labelId).storageLocation.name == endLoc.name
	}

	def "Store a unit load in an unknown place"() {
		given:
			def startLoc = createStorageLocation("LOCSTART")
			def ul = createUnitLoad("STORE", startLoc)
			
		when: "Store the unit load on destination"
			storageFacade.finishStorageRequest(ul.labelId, "UNKNOWNUNITLOAD/LOCATION", false, true)
		then:
			def ex = thrown(InventoryException)
			ex.inventoryExceptionKey == InventoryExceptionKey.STORAGE_WRONG_LOCATION_NOT_ALLOWED
			getUnitLoad(ul.labelId).storageLocation.name == startLoc.name
	}

	def "Store a unit load with the location locked #lockState"() {
		given:
			def startLoc = createStorageLocation("LOCSTART")
			def endLoc = createStorageLocation("LOCEND")
			def ul = createUnitLoad("STORE", startLoc)
			def toUL = createUnitLoad("RECEIPT", startLoc)
			def su = createStockUnit(ul, createItemData(), 5);
			
		when: "Store with a locked destination unit load"
			slCRUD.lock(endLoc, lockState.getLock(), "Locked for testing")
			storageFacade.finishStorageRequest(ul.labelId, endLoc.name, true, true)
		then: "Refuse to combine unit loads"
			def ex = thrown(LOSLocationException)
			ex.locationExceptionKey == LOSLocationExceptionKey.STORAGELOCATION_LOCKED
			getStockUnit(su.id).unitLoad == ul

		where:
			lockState << [LOSStorageLocationLockState.GENERAL, LOSStorageLocationLockState.CLEARING,
										 LOSStorageLocationLockState.GOING_TO_DELETE]					
	}
	
	def "Combine two unit loads"() {
		given: "we don't care about the lock state of the storage"
			def startLoc = createStorageLocation("LOCSTART")
			def endLoc = createStorageLocation("LOCEND")
			def ul = createUnitLoad("STORE", startLoc)
			def toUL = createUnitLoad("RECEIPT", endLoc)
			def su = createStockUnit(ul, createItemData(), 5);

			slCRUD.lock(endLoc, lockState.getLock(), "Locked for testing")
			areaCRUD.lock(endLoc.area, lockState.getLock(), "Locked for testing")
			//rackCRUD.lock(endLoc.area, lockState.getLock(), "Locked for testing")
			//zoneCRUD.lock(endLoc.area, lockState.getLock(), "Locked for testing")

		when: "Transfer stock units to destination"
			storageFacade.finishStorageRequest(ul.labelId, toUL.labelId, true, true)
		then:
			getStockUnit(su.id).unitLoad == toUL
			
		where:
			lockState << [LOSStorageLocationLockState.NOT_LOCKED, 
										LOSStorageLocationLockState.GENERAL, LOSStorageLocationLockState.CLEARING, 
										LOSStorageLocationLockState.GOING_TO_DELETE, LOSStorageLocationLockState.RETRIEVAL]
	}

	def "Combine two unit loads by scanning location"() {
		given: 
			def startLoc = createStorageLocation("LOCSTART")
			def endLoc = createStorageLocation("LOCEND")
			def ul = createUnitLoad("STORE", startLoc)
			def toUL = createUnitLoad("RECEIPT", endLoc)
			def su = createStockUnit(ul, createItemData(), 5);
			
		when: "Transfer stock units to destination"
			storageFacade.finishStorageRequest(ul.labelId, endLoc.name, true, true)
		then:
			getStockUnit(su.id).unitLoad == toUL
			
		where:
			scanLocation << [true, false, false, false, false, false]
	}

	def "Fail if there are two unit loads on destination and we ask to combine unit loads with a location scan"() {
		given:
			def startLoc = createStorageLocation("LOCSTART")
			def endLoc = createStorageLocation("LOCEND")
			def ul = createUnitLoad("STORE", startLoc)
			def toUL = createUnitLoad("RECEIPT", endLoc)
			def toUL2 = createUnitLoad("RECEIPT", endLoc)
			def su = createStockUnit(ul, createItemData(), 5);
			
		when: "Transfer stock units to destination"
			storageFacade.finishStorageRequest(ul.labelId, endLoc.name, true, true)
		then:
			def ex = thrown(InventoryException)
			ex.inventoryExceptionKey == InventoryExceptionKey.MUST_SCAN_STOCKUNIT
			getStockUnit(su.id).unitLoad == ul
			
		where:
			scanLocation << [true, false, false, false, false, false]
	}

	def "Fail to combine two unit loads with add to stock false"() {
		given: "we don't care about the lock state of the storage"
			def startLoc = createStorageLocation("LOCSTART")
			def endLoc = createStorageLocation("LOCEND")
			def ul = createUnitLoad("STORE", startLoc)
			def toUL = createUnitLoad("RECEIPT", startLoc)
			def su = createStockUnit(ul, createItemData(), 5);
				
		when: "Store the unit load on destination"
			storageFacade.finishStorageRequest(ul.labelId, toUL.labelId, false, true)
		then:
			def ex = thrown(InventoryException)
			ex.inventoryExceptionKey == InventoryExceptionKey.STORAGE_ADD_TO_EXISTING
			getStockUnit(su.id).unitLoad == ul
	}

	def "Fail if unit load shares the same name as a storage location"() {
		given:
			def startLoc = createStorageLocation("LOCSTART")
			def endLoc = createStorageLocation("LOCEND")
			def ul = createUnitLoad("STORE", startLoc)
			def toUL = createUnitLoad("RECEIPT", startLoc)
			def su = createStockUnit(ul, createItemData(), 5);
			toUL.labelId = endLoc.name
			ulCRUD.update(toUL)
		when: "Store the unit load on destination"
			storageFacade.finishStorageRequest(ul.labelId, toUL.labelId, true, true)
		then:
			def ex = thrown(InventoryException)
			ex.inventoryExceptionKey == InventoryExceptionKey.AMBIGUOUS_SCAN
			getStockUnit(su.id).unitLoad == ul
	}

	def "Fail if combining two unit loads with target unit load locked"() {
		given:
			def startLoc = createStorageLocation("LOCSTART")
			def endLoc = createStorageLocation("LOCEND")
			def ul = createUnitLoad("STORE", startLoc)
			def toUL = createUnitLoad("RECEIPT", startLoc)
			def su = createStockUnit(ul, createItemData(), 5);
			
		when: "Store with a locked destination unit load"
			ulCRUD.lock(toUL, lockState.getLock(), "Locked for testing")
			storageFacade.finishStorageRequest(ul.labelId, toUL.labelId, true, true)
		then: "Refuse to combine unit loads" 
			def ex = thrown(InventoryException)
			ex.inventoryExceptionKey == InventoryExceptionKey.STOCKUNIT_TRANSFER_NOT_ALLOWED
			getStockUnit(su.id).unitLoad == ul

		where:
			lockState << [LOSUnitLoadLockState.GENERAL, LOSUnitLoadLockState.CLEARING, 
										 LOSUnitLoadLockState.GOING_TO_DELETE, LOSUnitLoadLockState.QUALITY_CHECK, 
										 LOSUnitLoadLockState.RETRIEVAL, LOSUnitLoadLockState.SHIPPED, 
										 LOSUnitLoadLockState.STORAGE, LOSUnitLoadLockState.NOT_FOUND]
											
	}

	/**
	 * I think this is a bug, surely we should refuse to move any Locked unit load
	 * However the current behaviour is like this so I will differ changing behaviour until 
	 * a later date
	 */
	def "(?Fail if?) combining two unit loads with source unit load locked"() {
		given:
			def startLoc = createStorageLocation("LOCSTART")
			def endLoc = createStorageLocation("LOCEND")
			def ul = createUnitLoad("STORE", startLoc)
			def toUL = createUnitLoad("RECEIPT", startLoc)
			def su = createStockUnit(ul, createItemData(), 5);
			
		when: "Store with a locked destination unit load"
			ulCRUD.lock(ul, lockState.getLock(), "Locked for testing")
			storageFacade.finishStorageRequest(ul.labelId, toUL.labelId, true, true)
		then: "Refuse to combine unit loads"
//			def ex = thrown(InventoryException)
//			ex.inventoryExceptionKey == InventoryExceptionKey.STOCKUNIT_TRANSFER_NOT_ALLOWED
//			getStockUnit(su.id).unitLoad == ul
				getStockUnit(su.id).unitLoad == toUL		

		where:
			lockState << [LOSUnitLoadLockState.GENERAL, LOSUnitLoadLockState.CLEARING,
										 LOSUnitLoadLockState.GOING_TO_DELETE, LOSUnitLoadLockState.QUALITY_CHECK,
										 LOSUnitLoadLockState.RETRIEVAL, LOSUnitLoadLockState.SHIPPED,
										 LOSUnitLoadLockState.STORAGE, LOSUnitLoadLockState.NOT_FOUND]
											
	}


}
