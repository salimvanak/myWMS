package uk.ltd.mediamagic.mywms.system;

import java.util.Arrays;
import java.util.List;

import org.mywms.model.Role;

import javafx.beans.binding.BooleanBinding;
import uk.ltd.mediamagic.flow.crud.CRUDPlugin;
import uk.ltd.mediamagic.mywms.common.MyWMSUserPermissions;

public class RolesPlugin  extends CRUDPlugin<Role> {
	
	public RolesPlugin() {
		super(Role.class);
		setUserPermissions(new MyWMSUserPermissions.ForSystemData());
	}

	@Override
	protected BooleanBinding createVisibleBinding() {
		return MyWMSUserPermissions.adminUser();
	}

	@Override
	public String getPath() {
		return "{1, _System} -> {1, User _Roles}";
	}

	@Override
	protected List<String> getTableColumns() {
		return Arrays.asList("id", "name", "description");
	}

}
