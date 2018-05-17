package uk.ltd.mediamagic.mywms.master;

import java.util.Arrays;
import java.util.List;

import de.linogistix.los.location.model.LOSStorageLocationType;
import javafx.beans.binding.BooleanBinding;
import uk.ltd.mediamagic.flow.crud.CRUDPlugin;
import uk.ltd.mediamagic.flow.crud.SubForm;
import uk.ltd.mediamagic.mywms.common.MyWMSUserPermissions;

@SubForm(title="Main", isRequired=true, properties={"name"})
@SubForm(
		title="Measurment", columns=2, 
		properties={"height", "width", "depth", "weight"}
	)
public class StorageLocationTypesPlugin extends CRUDPlugin<LOSStorageLocationType> {
	
	public StorageLocationTypesPlugin() {
		super(LOSStorageLocationType.class);
		setUserPermissions(new MyWMSUserPermissions.ForMasterData());
	}

	@Override
	public String getPath() {
		return "{1, _Master Data} -> {1, _Location} -> {2, _Storage location types}";
	}

	@Override
	protected BooleanBinding createVisibleBinding() {
		return MyWMSUserPermissions.atLeastForeman();
	}

	@Override
	protected List<String> getTableColumns() {
		return Arrays.asList("id", "name");
	}

}
