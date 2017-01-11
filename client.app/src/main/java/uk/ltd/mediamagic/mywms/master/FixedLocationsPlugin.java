package uk.ltd.mediamagic.mywms.master;

import java.util.Arrays;
import java.util.List;

import de.linogistix.los.location.model.LOSFixedLocationAssignment;
import uk.ltd.mediamagic.flow.crud.BODTOPlugin;
import uk.ltd.mediamagic.flow.crud.SubForm;
import uk.ltd.mediamagic.mywms.common.MyWMSUserPermissions;

@SubForm(title="Main", properties={"name", "assignedLocation", "itemData", "desiredAmount"})
public class FixedLocationsPlugin extends BODTOPlugin<LOSFixedLocationAssignment> {
	
	public FixedLocationsPlugin() {
		super(LOSFixedLocationAssignment.class);
		setUserPermissions(new MyWMSUserPermissions.ForMasterData());
	}

	@Override
	public String getPath() {
		return "{1, _Master Data} -> {1, _Location} -> {1, _Fixed locations}";
	}

	@Override
	protected List<String> getTableColumns() {
		return Arrays.asList("id", "name AS itemData.number", "storageLocation AS assignedLocation.name", "itemDataName AS itemData.name", "amount as desiredAmount");
	}

}
