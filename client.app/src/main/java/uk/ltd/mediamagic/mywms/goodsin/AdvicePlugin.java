package uk.ltd.mediamagic.mywms.goodsin;

import java.beans.PropertyDescriptor;
import java.util.Arrays;
import java.util.List;

import de.linogistix.los.inventory.model.LOSAdvice;
import de.linogistix.los.inventory.query.dto.LOSAdviceTO;
import de.linogistix.los.query.BODTO;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.cell.TextFieldListCell;
import javafx.util.Callback;
import javafx.util.StringConverter;
import uk.ltd.mediamagic.flow.crud.BODTOPlugin;
import uk.ltd.mediamagic.flow.crud.SubForm;
import uk.ltd.mediamagic.fx.AwesomeIcon;
import uk.ltd.mediamagic.fx.controller.list.MaterialListItems;
import uk.ltd.mediamagic.fx.converters.DateConverter;
import uk.ltd.mediamagic.fx.converters.ToStringConverter;
import uk.ltd.mediamagic.util.DateUtils;

@SubForm(
		title="Main", columns=1, 
		properties={"adviceNumber", "externalAdviceNumber", "adviceState", "itemData", "notifiedAmount", "deliveryDate"}
	)
@SubForm(
		title="Lot", columns=2, 
		properties={"lot", "expireBatch"}
	)
@SubForm(
		title="Arrival", columns=2, 
		properties={"receiptAmount", "diffAmount"}
	)

public class AdvicePlugin  extends BODTOPlugin<LOSAdvice> {

	public AdvicePlugin() {
		super(LOSAdvice.class);
	}
	
	@Override
	public String getPath() {
		return "{1, _Goods in} -> {1, _Advice}";
	}
	
	@Override
	protected StringConverter<?> getConverter(PropertyDescriptor property) {
		if ("expectedDelivery".equals(property.getName())) return new DateConverter();
		return super.getConverter(property);
	}
	
	@Override
	public Callback<ListView<BODTO<LOSAdvice>>, ListCell<BODTO<LOSAdvice>>> createTOListCellFactory() {
		return TextFieldListCell.forListView(ToStringConverter.of(i -> {
			LOSAdviceTO to = (LOSAdviceTO) i;
			return String.format("%s, %s x %f", to.getName(), to.getItemData(), to.getNotifiedAmount());
		}));
	}
	
	@Override
	public Callback<ListView<LOSAdvice>, ListCell<LOSAdvice>> createListCellFactory() {
		return MaterialListItems.withDate(s -> (s.getLock() == 0) ? new AwesomeIcon(AwesomeIcon.unlock) : new AwesomeIcon(AwesomeIcon.lock), 
				s -> DateUtils.toLocalDateTime(s.getExpectedDelivery()), 
				s -> String.format("%s, %s, %s", s.toUniqueString(), s.getItemData().getNumber(), s.getItemData().getName()),
				s -> {
					if (s.getLot() != null) {
						return String.format("%s, %s -> %s", s.getLot().getName(), s.getLot().getUseNotBefore(), s.getLot().getBestBeforeEnd()); 						
					}
					else {						
						return String.format("No lot information"); 						
					}
				},
				s -> String.format("Expected %f, Receipt %f", s.getNotifiedAmount(), s.getReceiptAmount()));
	}
	
	@Override
	public List<String> getTableColumns() {
		return Arrays.asList("id", 
				"name AS adviceNumber",	"client AS client.number", 
				"itemData AS itemData.number", "itemDataName AS itemData.name", 
				"lot AS lot.name", 
				"notifiedAmount", "receiptAmount", "expectedDelivery", "state");
	}
	
}
