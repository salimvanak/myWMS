package uk.ltd.mediamagic.mywms.system;

import java.util.Arrays;
import java.util.List;

import org.mywms.model.Client;

import javafx.beans.binding.BooleanBinding;
import uk.ltd.mediamagic.flow.crud.CRUDPlugin;
import uk.ltd.mediamagic.mywms.common.MyWMSUserPermissions;

public class ClientsPlugin  extends CRUDPlugin<Client> {
	
	public ClientsPlugin() {
		super(Client.class);
		setUserPermissions(new MyWMSUserPermissions.ForSystemData());
	}

	@Override
	protected BooleanBinding createVisibleBinding() {
		return MyWMSUserPermissions.adminUser();
	}

	@Override
	public String getPath() {
		return "{1, _System} -> {1, _Clients}";
	}

	@Override
	protected List<String> getTableColumns() {
		return Arrays.asList("id", "number", "code", "name");
	}

}
