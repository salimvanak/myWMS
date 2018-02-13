package uk.ltd.mediamagic.mobile.pages

import static org.junit.Assert.*

import geb.Page

class InfoPage extends Page {
	String header = 'Info: Code'
	
	static url = "los-mobile/faces/pages/processes/info/EnterCode.jsp"
	
	static at = {
		title == "LOS"
		$(id:'Form:pagetitle').text() == header
	}

	static content = {
		inputField { $(name:'Form:input1') }
		confirmButton { $(name:'Form:BUTTON_CONTINUE') }
		cancelButton { $(name:'Form:BUTTON_CANCEL') }
	}

	def input(String input) {	
		inputField << input
		confirmButton.click()
	}
}
