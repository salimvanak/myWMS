package uk.ltd.mediamagic.mywms.common;

import javafx.beans.binding.BooleanBinding;
import javafx.beans.value.ObservableBooleanValue;
import uk.ltd.mediamagic.fxcommon.ObservableConstant;
import uk.ltd.mediamagic.fxcommon.UserPermissions;
import uk.ltd.mediamagic.mywms.MyWMS;

public class MyWMSUserPermissions implements UserPermissions {
	
	BooleanBinding isAdmin = MyWMS.hasRole("Admin");
	
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
		BooleanBinding isAtleastForeman = MyWMS.hasRole("Foreman", "Admin");

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
