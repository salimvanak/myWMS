package uk.ltd.mediamagic.mobile.pages
import geb.Page

class LoginPage extends Page {

	static url = "los-mobile/faces/login.jsp";
	
	static at = {
		title == "LOS"
		$('span', id:'pagetitle').text() == 'Login'
	}
	
	static content = {
		password { $('input', name:'j_password2') }
		username { $('input', name:'j_username2') }
		loginButton { $('input', type:'submit') }
	}
	
	void login(String usernameStr, String passwordStr) {
		username.value(usernameStr);
		password.value(passwordStr);
		loginButton.click();
	}

	void login() {
		String usernameStr = System.getProperty('mywms.username', 'admin');
		String passwordStr = System.getProperty('mywms.password', 'admin');
		username.value(usernameStr);
		password.value(passwordStr);
		loginButton.click();
	}

}
