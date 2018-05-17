package uk.ltd.mediamagic.mywms.master;

import java.util.Arrays;
import java.util.List;

import de.linogistix.los.location.model.LOSWorkingAreaPosition;
import javafx.beans.binding.BooleanBinding;
import uk.ltd.mediamagic.flow.crud.BODTOPlugin;
import uk.ltd.mediamagic.flow.crud.SubForm;
import uk.ltd.mediamagic.mywms.common.MyWMSUserPermissions;

@SubForm(title="Main", isRequired=true, properties={"name", "workingArea", "cluster"})
public class WorkAreasPositionsPlugin extends BODTOPlugin<LOSWorkingAreaPosition> {
	
	public WorkAreasPositionsPlugin() {
		super(LOSWorkingAreaPosition.class);
		setUserPermissions(new MyWMSUserPermissions.ForMasterData());
	}
	
	@Override
	protected BooleanBinding createVisibleBinding() {
		return MyWMSUserPermissions.atLeastForeman();
	}

	@Override
	public String getPath() {
		return "{1, _Master Data} -> {1, _Location} -> {3, _Working area positions}";
	}

	@Override
	protected List<String> getTableColumns() {
		return Arrays.asList("id", "name", "workingArea", "cluster");
	}

}
