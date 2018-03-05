package uk.ltd.mediamagic.mywms.system;

import java.util.Arrays;
import java.util.List;

import de.linogistix.los.model.LOSSystemProperty;
import javafx.beans.binding.BooleanBinding;
import uk.ltd.mediamagic.flow.crud.BODTOPlugin;
import uk.ltd.mediamagic.mywms.common.MyWMSUserPermissions;

public class PropertiesPlugin  extends BODTOPlugin<LOSSystemProperty> {
	
	public PropertiesPlugin() {
		super(LOSSystemProperty.class);
		setUserPermissions(new MyWMSUserPermissions.ForSystemData());
	}

	@Override
	protected BooleanBinding createVisibleBinding() {
		return MyWMSUserPermissions.adminUser();
	}

	@Override
	public String getPath() {
		return "{1, _System} -> {1, _Properties}";
	}

	@Override
	protected List<String> getTableColumns() {
		return Arrays.asList("id", "clientNumber AS client.number", "key", "value", "workstation");
	}

}
