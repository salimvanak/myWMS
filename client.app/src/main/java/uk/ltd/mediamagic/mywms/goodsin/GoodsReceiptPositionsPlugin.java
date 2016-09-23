package uk.ltd.mediamagic.mywms.goodsin;

import java.beans.PropertyDescriptor;
import java.util.Arrays;
import java.util.List;

import de.linogistix.los.inventory.model.LOSGoodsReceiptPosition;
import javafx.scene.Node;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;
import javafx.util.StringConverter;
import res.R;
import uk.ltd.mediamagic.flow.crud.BODTOPlugin;
import uk.ltd.mediamagic.flow.crud.SubForm;
import uk.ltd.mediamagic.fx.controller.list.MaterialListItems;
import uk.ltd.mediamagic.fx.converters.DateConverter;

@SubForm(
		title="Main", columns=1, 
		properties={
				"positionNumber", "goodsReceipt", "advice", "type", "itemData", 
				"lot", "amount", "qaLock", "qaFault", "unitLoad", "stockUnit"
			}
	)

public class GoodsReceiptPositionsPlugin  extends BODTOPlugin<LOSGoodsReceiptPosition> {

	public GoodsReceiptPositionsPlugin() {
		super(LOSGoodsReceiptPosition.class);
	}
	
	@Override
	public String getPath() {
		return "{1, _Goods in} -> {1, _Goods Receipt Positions}";
	}
	
	@Override
	protected StringConverter<?> getConverter(PropertyDescriptor property) {
		if ("receiptDate".equals(property.getName())) return new DateConverter();
		return super.getConverter(property);
	}
	
	@Override
	public Callback<ListView<LOSGoodsReceiptPosition>, ListCell<LOSGoodsReceiptPosition>> createListCellFactory() {
		return MaterialListItems.withID(s -> R.svgPaths.goodsWaiting(), 
				LOSGoodsReceiptPosition::getPositionNumber, 
				s -> String.format("Item: %s, x %f", s.getItemData(), s.getAmount()),
				s -> String.format("Lot: %s", s.getLot()),
				null);
	}
	
	@Override
	public List<String> getTableColumns() {
		return Arrays.asList("id", 
				"name AS positionNumber",	"itemData", "lot", "amount", "qaLock","unitLoad");
	}
	
}
