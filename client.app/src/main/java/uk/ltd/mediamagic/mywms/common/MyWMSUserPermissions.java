package uk.ltd.mediamagic.mywms.common;

import java.util.function.BooleanSupplier;

import javafx.beans.binding.BooleanBinding;
import javafx.beans.value.ObservableBooleanValue;
import uk.ltd.mediamagic.fxcommon.ObservableConstant;
import uk.ltd.mediamagic.fxcommon.UserPermissions;
import uk.ltd.mediamagic.mywms.MyWMS;

public class MyWMSUserPermissions implements UserPermissions {

	public static final boolean isAtLeastInventory() {
		return MyWMS.hasRole("Admin", "Foreman", "Operator", "Inventory");
	}
	
	public static final boolean isAtLeastForeman() {
		return MyWMS.hasRole("Admin", "Foreman");
	}

	public static final boolean isAdmin() {
		return MyWMS.hasRole("Admin");
	}

	public static final BooleanBinding forRoles(String...roles) {
		return MyWMS.roleBinding(roles);
	}

	public static final BooleanBinding atLeastOperator() {
		return forRoles("Admin", "Foreman", "Inventory", "Operator");
	}

	public static final BooleanBinding atLeastInventory() {
		return forRoles("Admin", "Foreman", "Inventory");
	}

	public static final BooleanBinding atLeastForeman() {
		return forRoles("Admin", "Foreman");
	}

	public static final BooleanBinding adminUser() {
		return forRoles("Admin");
	}

	protected final BooleanBinding isAdmin = adminUser();
	
	public MyWMSUserPermissions() {
	}

	public ObservableBooleanValue isDeleteAllowed() {
		return ObservableConstant.FALSE;		
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
		public ForMasterData() {
		}

		public ObservableBooleanValue isDeleteAllowed() {
			return isAdmin;		
		}
		
		@Override
		public ObservableBooleanValue isEditable(String colName) {
			if ("created".equals(colName)) return ObservableConstant.FALSE;
			if ("modified".equals(colName)) return ObservableConstant.FALSE;
			return isAdmin;
		}
	}

	public static class ForSystemData extends MyWMSUserPermissions {
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
		private final BooleanBinding isAdmin = MyWMS.roleBinding("Admin");

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
			if (isAdmin.get()) return permissions.isEditable(colName);
			if (isLocked.getAsBoolean()) return ObservableConstant.FALSE;
			return permissions.isEditable(colName);
		}
	}

}
