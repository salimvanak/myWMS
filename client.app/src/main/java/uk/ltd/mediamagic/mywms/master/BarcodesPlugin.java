package uk.ltd.mediamagic.mywms.master;

import java.util.Arrays;
import java.util.List;

import org.mywms.model.ItemDataNumber;

import uk.ltd.mediamagic.flow.crud.BODTOPlugin;
import uk.ltd.mediamagic.flow.crud.SubForm;
import uk.ltd.mediamagic.mywms.common.MyWMSUserPermissions;

@SubForm(title="Main", properties={"number", "itemData", "index", "manufacturerName"})
public class BarcodesPlugin extends BODTOPlugin<ItemDataNumber> {
	
	public BarcodesPlugin() {
		super(ItemDataNumber.class);
		setUserPermissions(new MyWMSUserPermissions.ForMasterData());
	}

	@Override
	public String getPath() {
		return "{1, _Master Data} -> {1, _Inventory} -> {1, _Barcodes}";
	}

	@Override
	protected List<String> getTableColumns() {
		return Arrays.asList("id", "number", "itemData", "index");
	}

}
