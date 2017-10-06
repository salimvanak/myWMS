package uk.ltd.mediamagic.mywms.documents;

import java.util.Arrays;
import java.util.List;

import de.linogistix.los.inventory.pick.model.PickReceipt;
import uk.ltd.mediamagic.flow.crud.BODTOPlugin;

public class PickingReceiptsPlugin  extends BODTOPlugin<PickReceipt> {
	
	public PickingReceiptsPlugin() {
		super(PickReceipt.class);
	}


	@Override
	public String getPath() {
		return "{1, _Documents} -> {1, _Picking Receipts}";
	}	

	@Override
	protected List<String> getTableColumns() {
		return Arrays.asList("id", "orderNumber", "pickNumber", "labelID", "date");
	}
		
}
