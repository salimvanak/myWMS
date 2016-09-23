package uk.ltd.mediamagic.mywms.master;

import java.util.Arrays;
import java.util.List;

import de.linogistix.los.location.model.LOSLocationCluster;
import uk.ltd.mediamagic.flow.crud.CRUDPlugin;
import uk.ltd.mediamagic.flow.crud.SubForm;
import uk.ltd.mediamagic.mywms.common.MyWMSUserPermissions;

@SubForm(title="Main", properties={"name", "level"})
public class LocationClusterPlugin extends CRUDPlugin<LOSLocationCluster> {
	
	public LocationClusterPlugin() {
		super(LOSLocationCluster.class);
		setUserPermissions(new MyWMSUserPermissions.ForMasterData());
	}

	@Override
	public String getPath() {
		return "{1, _Master Data} -> {1, _Location} -> {2, _Location Clusters}";
	}

	@Override
	protected List<String> getTableColumns() {
		return Arrays.asList("id", "name");
	}

}
