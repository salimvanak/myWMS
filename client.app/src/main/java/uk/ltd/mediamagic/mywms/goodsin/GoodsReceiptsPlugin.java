package uk.ltd.mediamagic.mywms.goodsin;

import java.beans.PropertyDescriptor;
import java.util.Arrays;
import java.util.List;

import de.linogistix.los.inventory.model.LOSGoodsReceipt;
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
import uk.ltd.mediamagic.util.DateUtils;

@SubForm(
		title="Main", columns=1, 
		properties={"goodsReceiptNumber", "referenceNo", "receiptState", "goodsInLocation", "operator"}
	)
@SubForm(
		title="Delivery", columns=2, 
		properties={"forwarder", "deliveryNoteNumber", "receiptDate", "deliverer", "driverName", "licencePlate"}
	)
public class GoodsReceiptsPlugin  extends BODTOPlugin<LOSGoodsReceipt> {

	public GoodsReceiptsPlugin() {
		super(LOSGoodsReceipt.class);
	}
	
	@Override
	public String getPath() {
		return "{1, _Goods in} -> {1, _Goods Receipts}";
	}
	
	@Override
	protected StringConverter<?> getConverter(PropertyDescriptor property) {
		if ("receiptDate".equals(property.getName())) return new DateConverter();
		return super.getConverter(property);
	}
	
	@Override
	public Callback<ListView<LOSGoodsReceipt>, ListCell<LOSGoodsReceipt>> createListCellFactory() {
		return MaterialListItems.withDate(GoodsReceiptsPlugin::getIcon, 
				s -> DateUtils.toLocalDateTime(s.getReceiptDate()), 
				s -> String.format("%s, %s, %s", s.toUniqueString(), s.getReferenceNo(), s.getDeliveryNoteNumber()),
				s -> String.format("%s, %s", s.getLicencePlate(), s.getDriverName()),
				s -> String.format("%s", s.getReceiptState()));
	}
	
	private static Node getIcon(LOSGoodsReceipt d) {
		if (d == null) return null;
		switch (d.getReceiptState()) {
		case RAW: return R.svgPaths.goodsWaiting();
		case ACCEPTED: return R.svgPaths.goodsArrived();
		case TRANSFER: return R.svgPaths.goodsTransfer();
		case CANCELED: return R.svgPaths.cancelled();
		case FINISHED: return R.svgPaths.goodsStored();
		}
		return R.svgPaths.unknown();
	}
	
	@Override
	public List<String> getTableColumns() {
		return Arrays.asList("id", 
				"name AS goodsReceiptNumber",	"clientNumber AS client.number", 
				"deliveryNoteNumber", "receiptDate", "receiptState");
	}
	
}
