package uk.ltd.mediamagic.mobile.pages;

import geb.Page;

public class MainMenuPage extends Page {
	static url = "los-mobile/faces/pages/processes/controller/MainMenu.jsp"
	
	static at = {
		title == "LOS"
		$(id:'Form:pagetitle').text().startsWith("Homepage")
	}

	def selectItem(String name) {
		$('input.commandButton', value:name).click();
	}
}
