package uk.ltd.mediamagic.mobile.pages

import static org.junit.Assert.*

import geb.Page

class StorageDestinationPage extends Page {	
	static at = {
		title == "LOS"
		$(id:'Form:pagetitle').text() == "Storage: Destination"
	}

	static content = {
		inputField { $(name:'Form:storageLocationTextField') }

		unitLoadField { $(name:'Form:unitLoadTextField') }
		targetLocationLabel {name:'Form:storageLocationLabelMessage' }
		addToExistingStock { $(name:'Form:j_id_jsp_140142968_11') }
		confirmButton { $(name:'Form:finishedButton') }
		cancelButton { $(name:'Form:cancelButton') }
	}

	def input(String input) {	
		inputField << input
		confirmButton.click()
	}
}
