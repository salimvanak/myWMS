package uk.ltd.mediamagic.mobile.pages

import static org.junit.Assert.*

import geb.Page

class StorageUnitLoadPage extends Page {
		static url = "los-mobile/faces/pages/processes/storage/scan_unitload/gui/component/CenterPanel.jsp"
	
	static at = {
		title == "LOS"
		$(id:'Form:pagetitle').text() == "Storage: Unit Load"
	}

	static content = {
		inputField { $(name:'Form:ulTextField') }
		confirmButton { $(name:'Form:forwardButton') }
		cancelButton { $(name:'Form:CancelButton') }
	}

	def input(String input) {	
		inputField << input
		confirmButton.click()
	}
}
