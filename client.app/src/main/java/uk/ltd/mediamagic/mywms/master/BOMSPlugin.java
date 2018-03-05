package uk.ltd.mediamagic.mywms.master;

import java.util.Arrays;
import java.util.List;

import de.linogistix.los.inventory.model.LOSBom;
import javafx.beans.binding.BooleanBinding;
import uk.ltd.mediamagic.flow.crud.BODTOPlugin;
import uk.ltd.mediamagic.flow.crud.SubForm;
import uk.ltd.mediamagic.mywms.common.MyWMSUserPermissions;

@SubForm(title="Main", properties={"master", "child", "amount", "index"})
public class BOMSPlugin extends BODTOPlugin<LOSBom> {
	
	public BOMSPlugin() {
		super(LOSBom.class);
		setUserPermissions(new MyWMSUserPermissions.ForMasterData());
	}

	@Override
	protected BooleanBinding createVisibleBinding() {
		return MyWMSUserPermissions.atLeastInventory();
	}

	@Override
	public String getPath() {
		return "{1, _Master Data} -> {1, _Inventory} -> {1, _Bill of materials}";
	}

	@Override
	protected List<String> getTableColumns() {
		return Arrays.asList("id", 
				"parentNumber AS parent.number", "parentName  AS parent.name", 
				"childNumber AS child.number", "childName AS child.name", 
				"amount");
	}

}
