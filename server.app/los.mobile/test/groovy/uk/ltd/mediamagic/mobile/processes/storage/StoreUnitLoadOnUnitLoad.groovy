package uk.ltd.mediamagic.mobile.processes.storage

import static org.junit.Assert.*

import de.linogistix.los.location.constants.LOSUnitLoadLockState
import geb.spock.GebSpec
import uk.ltd.mediamagic.los.inventory.test.ItemDataSpec
import uk.ltd.mediamagic.los.inventory.test.StockUnitSpec
import uk.ltd.mediamagic.los.inventory.test.StorageLocationSpec
import uk.ltd.mediamagic.los.inventory.test.UnitLoadSpec
import uk.ltd.mediamagic.mobile.CurrentTest
import uk.ltd.mediamagic.mobile.WithAuth
import uk.ltd.mediamagic.mobile.pages.MessagePage
import uk.ltd.mediamagic.mobile.pages.StorageDestinationPage
import uk.ltd.mediamagic.mobile.pages.StorageUnitLoadPage

@CurrentTest
class StoreUnitLoadOnUnitLoad extends GebSpec implements 
	WithAuth, StorageLocationSpec, UnitLoadSpec, StockUnitSpec, ItemDataSpec {

	def "Combine two unit loads without 'add to unit load'"() {
		given:
			def startLoc = createStorageLocation("LOCSTART")
			def endLoc = createStorageLocation("LOCEND")
			def ul = createUnitLoad("STORE", startLoc)
			def toUL = createUnitLoad("RECEIPT", startLoc)
			def su = createStockUnit(ul, createItemData(), 5);
			to viaLogin(StorageUnitLoadPage)
			
		when: "Scan the unit load label"
			input(ul.labelId)
		then:
			at StorageDestinationPage
			unitLoadField == ul.labelId
			
		when: "Scan the target unitload"
			input(toUL.labelId)
		then: "Check the unit load is still stored in the $startLoc"
			at MessagePage
			checkWarningMessage('Do you want add?')
		when: "Confirm store on unit load"
			clickYes()
		then:
			getStockUnit(su.id).unitLoad == toUL
	}

	def "Combine two unit loads with 'add to unit load'"() {
		given:
			def startLoc = createStorageLocation("LOCSTART")
			def endLoc = createStorageLocation("LOCEND")
			def ul = createUnitLoad("STORE", startLoc)
			def toUL = createUnitLoad("RECEIPT", startLoc)
			def su = createStockUnit(ul, createItemData(), 5);
			to viaLogin(StorageUnitLoadPage)
			
		when: "Scan the unit load label"
			input(ul.labelId)
		then:
			at StorageDestinationPage
			unitLoadField == ul.labelId
			
		when: "Scan the target unitload"
			addToExistingStock.click()
			input(toUL.labelId)
		then:
			getStockUnit(su.id).unitLoad == toUL
	}

	def "Combine two unit loads without 'add to unit load' and cancel at final message"() {
		given:
			def startLoc = createStorageLocation("LOCSTART")
			def endLoc = createStorageLocation("LOCEND")
			def ul = createUnitLoad("STORE", startLoc)
			def toUL = createUnitLoad("RECEIPT", startLoc)
			def su = createStockUnit(ul, createItemData(), 5);
			to viaLogin(StorageUnitLoadPage)
			
		when: "Scan the unit load label"
			input(ul.labelId)
		then:
			at StorageDestinationPage
			unitLoadField == ul.labelId
			
		when: "Scan the target unitload"
			input(toUL.labelId)
		then: "Check the unit load is still stored in the $startLoc"
			at MessagePage
			checkWarningMessage('Do you want add?')
		when: "Confirm store on unit load"
			clickNo()
		then:
			getStockUnit(su.id).unitLoad == ul
	}
	

}
