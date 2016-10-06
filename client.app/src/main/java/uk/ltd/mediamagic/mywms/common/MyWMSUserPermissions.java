package uk.ltd.mediamagic.mywms.common;

import javafx.beans.binding.BooleanBinding;
import javafx.beans.value.ObservableBooleanValue;
import uk.ltd.mediamagic.fxcommon.ObservableConstant;
import uk.ltd.mediamagic.fxcommon.UserPermissions;
import uk.ltd.mediamagic.mywms.MyWMS;

public class MyWMSUserPermissions implements UserPermissions {

	public static final boolean isAdmin() {
		return MyWMS.hasRole("Admin");
	}

	public static final boolean isAtLeastForeman() {
		return MyWMS.hasRole("Admin", "Foreman");
	}
	
	BooleanBinding isAdmin = MyWMS.roleBinding("Admin");
	
	public MyWMSUserPermissions() {
	}

	@Override
	public ObservableBooleanValue isEditable(String colName) {
		return isAdmin;
	}
	 
	 @Override
	public ObservableBooleanValue isVisible(String colName) {
		return ObservableConstant.TRUE;
	}
	 
	public static class ForMasterData extends MyWMSUserPermissions {
		BooleanBinding isAtleastForeman = MyWMS.roleBinding("Foreman", "Admin");

		public ForMasterData() {
		}
		
		@Override
		public ObservableBooleanValue isEditable(String colName) {
			if ("created".equals(colName)) return ObservableConstant.FALSE;
			if ("modified".equals(colName)) return ObservableConstant.FALSE;
			return isAtleastForeman;
		}
	}
}
