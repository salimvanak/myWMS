package uk.ltd.mediamagic.mywms.common;

import java.util.function.BooleanSupplier;

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
		return MyWMS.hasRole("Foreman", "Operator");
	}

	public static final BooleanBinding forRoles(String...roles) {
		return MyWMS.roleBinding(roles);
	}

	public static final BooleanBinding atLeastForemanUser() {
		return forRoles("Foreman", "Operator");
	}

	public static final BooleanBinding adminUser() {
		return forRoles("Admin", "Foreman");
	}

	BooleanBinding isAdmin = MyWMS.roleBinding("Admin");
	
	public MyWMSUserPermissions() {
	}

	@Override
	public ObservableBooleanValue isEditable(String colName) {
		if ("created".equals(colName)) return ObservableConstant.FALSE;
		if ("modified".equals(colName)) return ObservableConstant.FALSE;
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

	public static class ForSystemData extends MyWMSUserPermissions {
		BooleanBinding isAdmin = MyWMS.roleBinding("Admin");

		public ForSystemData() {
		}
		
		@Override
		public ObservableBooleanValue isEditable(String colName) {
			if ("created".equals(colName)) return ObservableConstant.FALSE;
			if ("modified".equals(colName)) return ObservableConstant.FALSE;
			return isAdmin;
		}
	}

	public static class ForLockedWhen implements UserPermissions {
		private final BooleanSupplier isLocked; 
		private final UserPermissions permissions;
		public ForLockedWhen(UserPermissions permissions, BooleanSupplier isLocked) {
			super();
			this.isLocked = isLocked;
			this.permissions = permissions;
		}
		
		@Override
		public ObservableBooleanValue isVisible(String colName) {
			return permissions.isVisible(colName);
		}
		
		@Override
		public ObservableBooleanValue isEditable(String colName) {
			if ("created".equals(colName)) return ObservableConstant.FALSE;
			if ("modified".equals(colName)) return ObservableConstant.FALSE;
			if (isLocked.getAsBoolean()) return ObservableConstant.FALSE;
			return permissions.isEditable(colName);
		}
	}

}
