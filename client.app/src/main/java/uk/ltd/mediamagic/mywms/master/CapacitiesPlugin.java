package uk.ltd.mediamagic.mywms.master;

import java.util.Arrays;
import java.util.List;

import de.linogistix.los.location.model.LOSStorageLocationType;
import uk.ltd.mediamagic.flow.crud.BODTOPlugin;
import uk.ltd.mediamagic.flow.crud.SubForm;
import uk.ltd.mediamagic.mywms.common.MyWMSUserPermissions;

@SubForm(
		title="Main", 
		properties={"locationType", "unitLoadType","type", "allocation", "orderIndex"}
	)
@SubForm(
		title="Measurment", columns=2, 
		properties={"height", "width", "depth", "weight"}
	)
public class CapacitiesPlugin extends BODTOPlugin<LOSStorageLocationType> {
	
	public CapacitiesPlugin() {
		super(LOSStorageLocationType.class);
		setUserPermissions(new MyWMSUserPermissions.ForMasterData());
	}

	@Override
	public String getPath() {
		return "{1, _Master Data} -> {1, _Location} -> {1, _Capacities}";
	}

	@Override
	protected List<String> getTableColumns() {
		return Arrays.asList("id", "name", "locationType", "unitLoadType", "allocation");
	}

}
