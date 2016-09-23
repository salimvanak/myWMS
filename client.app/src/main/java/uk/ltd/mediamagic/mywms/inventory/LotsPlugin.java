package uk.ltd.mediamagic.mywms.inventory;

import java.beans.PropertyDescriptor;
import java.util.Arrays;
import java.util.List;

import org.mywms.model.Lot;

import de.linogistix.los.inventory.query.dto.LotTO;
import de.linogistix.los.inventory.service.LotLockState;
import de.linogistix.los.query.BODTO;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.cell.TextFieldListCell;
import javafx.util.Callback;
import javafx.util.StringConverter;
import uk.ltd.mediamagic.flow.crud.BODTOPlugin;
import uk.ltd.mediamagic.flow.crud.SubForm;
import uk.ltd.mediamagic.fx.converters.DateConverter;
import uk.ltd.mediamagic.fx.converters.ToStringConverter;
import uk.ltd.mediamagic.mywms.common.LockStateConverter;

@SubForm(
		title="Main", columns=1, 
		properties={"name", "itemData", "date", "useNotBefore", "bestBeforeEnd"}
	)
@SubForm(
		title="Measurment", columns=2, 
		properties={"height", "width", "depth", "weight"}
	)
@SubForm(
		title="Hidden", columns=0, 
		properties={"age", "code", "locked", "version", "volume"}
	)
public class LotsPlugin extends BODTOPlugin<Lot> {

	public LotsPlugin() {
		super(Lot.class);
	}
	
	@Override
	public String getPath() {
		return "{1, _Inventory} -> {1, _Lots}";
	}
	
	@Override
	public Callback<ListView<BODTO<Lot>>, ListCell<BODTO<Lot>>> createTOListCellFactory() {
		return TextFieldListCell.forListView(ToStringConverter.of(i -> {
			LotTO to = (LotTO) i;
			return String.format("%s, %s -- %s", to.getName(), to.getUseNotBefore(), to.getBestBeforeEnd());
		}));
	}

	@Override
	protected StringConverter<?> getConverter(PropertyDescriptor property) {
		if ("lock".equals(property.getName())) {
			return new LockStateConverter<>(LotLockState.class);
		}
		else if ("bestBeforeEnd".equals(property.getName())) {
			return new DateConverter();
		}
		else if ("useNotBefore".equals(property.getName())) {
			return new DateConverter();
		}
		else {
			return super.getConverter(property);			
		}
	}

	@Override
	public List<String> getTableColumns() {
		return Arrays.asList("name", "itemData", "useNotBefore", "bestBeforeEnd", "lock");
	}

}
