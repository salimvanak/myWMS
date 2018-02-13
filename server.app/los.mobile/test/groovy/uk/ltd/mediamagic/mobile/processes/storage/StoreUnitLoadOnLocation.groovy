package uk.ltd.mediamagic.mobile.processes.storage

import static org.junit.Assert.*

import org.mywms.facade.FacadeException

import de.linogistix.los.inventory.exception.InventoryException
import de.linogistix.los.location.constants.LOSStorageLocationLockState
import de.linogistix.los.location.exception.LOSLocationException
import geb.spock.GebSpec
import groovy.transform.CompileStatic
import uk.ltd.mediamagic.los.inventory.test.ItemDataSpec
import uk.ltd.mediamagic.los.inventory.test.StockUnitSpec
import uk.ltd.mediamagic.los.inventory.test.StorageLocationSpec
import uk.ltd.mediamagic.los.inventory.test.UnitLoadSpec
import uk.ltd.mediamagic.mobile.CurrentTest
import uk.ltd.mediamagic.mobile.WithAuth
import uk.ltd.mediamagic.mobile.pages.MessagePage
import uk.ltd.mediamagic.mobile.pages.StorageDestinationPage
import uk.ltd.mediamagic.mobile.pages.StorageUnitLoadPage

class StoreUnitLoadOnLocation extends GebSpec implements 
	WithAuth, StorageLocationSpec, UnitLoadSpec, StockUnitSpec, ItemDataSpec {

	def "Store a unit load"() {
		given: 
			def startLoc = createStorageLocation("LOCSTART")
			def endLoc = createStorageLocation("LOCEND")
			def ul = createUnitLoad("STORE", startLoc)
			to viaLogin(StorageUnitLoadPage)
			
		when: "Scan the unit load label"
			input(ul.labelId)
		then:
			at StorageDestinationPage
			unitLoadField == ul.labelId
			
		when: "Scan the target location $endLoc"
			input(endLoc.name)
		then: "Check the unit load is stored in the $endLoc"
			at MessagePage
			checkInfoMessage('stored successfully')
			getUnitLoad(ul.labelId).storageLocation.name == endLoc.name 
	}

	def "Store a unit load in an unknown location"() {
		given:
			def startLoc = createStorageLocation("LOCSTART")
			def endLoc = createStorageLocation("LOCEND")
			def ul = createUnitLoad("STORE", startLoc)
			to viaLogin(StorageUnitLoadPage)
			
		when: "Scan the unit load label"
			input(ul.labelId)
		then:
			at StorageDestinationPage
			unitLoadField == ul.labelId
			
		when: "Scan the target location"
			input("UNKNOWNLOCATIONFORUNITLOAD")
		then: "Check the unit load is still stored in the $startLoc"
			at MessagePage
			checkErrorMessage('Wrong Destination')
			getUnitLoad(ul.labelId).storageLocation.name == startLoc.name
	}

	def "Store a unit load to a full location"() {
		given: "A location with a unit load already present"
			def startLoc = createStorageLocation("LOCSTART")
			def endLoc = createStorageLocation("LOCEND")

			// put a unitload in the location
			def ulBlocker = createUnitLoad("STORE", startLoc)
			storageFacade.finishStorageRequest(ulBlocker.labelId, endLoc.name, false, false)

			def ul = createUnitLoad("STORE", startLoc)
			to viaLogin(StorageUnitLoadPage)
			
		when: "Scan the unit load label"
			input(ul.labelId)
		then:
			at StorageDestinationPage
			unitLoadField == ul.labelId
			
		when: "Scan the target location"
			input(endLoc.name)
		then: "Check the unit load is still stored in the $startLoc"
			at MessagePage
			checkErrorMessage('already full')
			getUnitLoad(ul.labelId).storageLocation.name == startLoc.name
	}

	def "Store a unit load in a locked location"() {
		given: "A location with a unit load already present"
			def startLoc = createStorageLocation("LOCSTART")
			def endLoc = createStorageLocation("LOCEND")		
			slCRUD.lock(endLoc, LOSStorageLocationLockState.GENERAL.getLock(), "Lock location test")

			def ul = createUnitLoad("STORE", startLoc)
			to viaLogin(StorageUnitLoadPage)
			
		when: "Scan the unit load label"
			input(ul.labelId)
		then:
			at StorageDestinationPage
			unitLoadField == ul.labelId
			
		when: "Scan the target location"
			input(endLoc.name)
		then: "Check the unit load is still stored in the $startLoc"
			at MessagePage
			checkErrorMessage('could not be stored on location')
			getUnitLoad(ul.labelId).storageLocation.name == startLoc.name
	}

}
