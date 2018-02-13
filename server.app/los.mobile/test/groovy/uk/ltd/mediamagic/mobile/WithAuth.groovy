package uk.ltd.mediamagic.mobile

import geb.Page
import geb.spock.GebSpec
import uk.ltd.mediamagic.mobile.pages.LoginPage

trait WithAuth {

	public <T extends Page> Class<T> viaLogin(Class<T> type) {
		browser.via(type)
		def page = browser.page(LoginPage, type)
		if (page instanceof LoginPage) {
			page.login()
		}
		browser.via(type)
		return type;
	}

	public <T extends Page> Class<T> viaLogin(T type) {
		browser.via(type)
		def page = browser.page(LoginPage, type.getClass())
		if (page instanceof LoginPage) {
			page.login()
		}
		browser.via(type)
		return type;
	}

}
