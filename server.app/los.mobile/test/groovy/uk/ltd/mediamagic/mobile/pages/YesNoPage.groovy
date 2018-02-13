package uk.ltd.mediamagic.mobile.pages
import geb.Page

class YesNoPage extends Page {
	
	static at = {
		title == "LOS"
	}
	
	static content = {
		header { $('span', id:'successForm:pagetitle') }
		messageLabel { $(id:'successForm:message') }
	}
	
  boolean checkInfoMessage(String message) {
		assert header.text() == 'Information'
		assert messageLabel.text().contains(message)
		return true
	}

	boolean checkErrorMessage(String message) {
		assert header.text() == 'Error'
		assert messageLabel.text().contains(message)
		return true;
	}

}
