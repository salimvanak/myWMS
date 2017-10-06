package uk.ltd.mediamagic.mywms.documents;

import java.util.Arrays;
import java.util.List;

import de.linogistix.los.inventory.model.OrderReceipt;
import uk.ltd.mediamagic.flow.crud.BODTOPlugin;

public class OrderReceiptsPlugin  extends BODTOPlugin<OrderReceipt> {
	
	public OrderReceiptsPlugin() {
		super(OrderReceipt.class);
	}


	@Override
	public String getPath() {
		return "{1, _Documents} -> {1, _Order Receipts}";
	}	

	@Override
	protected List<String> getTableColumns() {
		return Arrays.asList("id", "orderNumber", "referenceNumber", "date");
	}
		
}
