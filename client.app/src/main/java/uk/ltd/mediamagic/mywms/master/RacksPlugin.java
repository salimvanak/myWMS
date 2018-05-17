package uk.ltd.mediamagic.mywms.master;

import java.util.Arrays;
import java.util.List;

import de.linogistix.los.location.model.LOSRack;
import javafx.beans.binding.BooleanBinding;
import uk.ltd.mediamagic.flow.crud.BODTOPlugin;
import uk.ltd.mediamagic.flow.crud.SubForm;
import uk.ltd.mediamagic.mywms.common.MyWMSUserPermissions;

@SubForm(title="Main", isRequired=true, properties={"client", "name", "aisle"})
public class RacksPlugin extends BODTOPlugin<LOSRack> {
	
	public RacksPlugin() {
		super(LOSRack.class);
		setUserPermissions(new MyWMSUserPermissions.ForMasterData());
	}

	@Override
	protected BooleanBinding createVisibleBinding() {
		return MyWMSUserPermissions.atLeastForeman();
	}

	@Override
	public String getPath() {
		return "{1, _Master Data} -> {1, _Location} -> {3, _Racks}";
	}

	@Override
	protected List<String> getTableColumns() {
		return Arrays.asList("id", "name", "aisle");
	}

}
