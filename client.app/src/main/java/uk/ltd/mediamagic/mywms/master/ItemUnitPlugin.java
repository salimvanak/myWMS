package uk.ltd.mediamagic.mywms.master;

import java.util.Arrays;
import java.util.List;

import org.mywms.model.ItemUnit;

import javafx.beans.binding.BooleanBinding;
import uk.ltd.mediamagic.flow.crud.CRUDPlugin;
import uk.ltd.mediamagic.flow.crud.SubForm;
import uk.ltd.mediamagic.mywms.common.MyWMSUserPermissions;

@SubForm(title="Main", isRequired=true, properties={"unitName", "unitType"})
@SubForm(title="Details", properties={"baseFactor", "baseUnit"})
public class ItemUnitPlugin extends CRUDPlugin<ItemUnit> {
	
	public ItemUnitPlugin() {
		super(ItemUnit.class);
		setUserPermissions(new MyWMSUserPermissions.ForMasterData());
		setDeleteAllowed(true);
	}

	@Override
	protected BooleanBinding createVisibleBinding() {
		return MyWMSUserPermissions.atLeastForeman();
	}

	@Override
	public String getPath() {
		return "{1, _Master Data} -> {1, _Inventory} -> {1, _Item Units}";
	}
	
	@Override
	protected List<String> getTableColumns() {
		return Arrays.asList("id", "unitName", "unitType");
	}

}
