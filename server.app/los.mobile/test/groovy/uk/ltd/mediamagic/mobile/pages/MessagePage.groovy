package uk.ltd.mediamagic.mobile.pages
import geb.Page

class MessagePage extends Page {
	
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

  boolean checkWarningMessage(String message) {
		assert header.text() == 'Warning'
		assert messageLabel.text().contains(message)
		return true
	}

	boolean checkErrorMessage(String message) {
		assert header.text() == 'Error'
		assert messageLabel.text().contains(message)
		return true;
	}
	
	void clickYes() {
		$(id:'successForm:button1').click()
	}

	void clickNo() {
		$(id:'successForm:button2').click()
	}
}
