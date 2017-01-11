package uk.ltd.mediamagic.mywms.inventory;

import java.beans.PropertyDescriptor;
import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

import org.mywms.model.Lot;

import de.linogistix.los.inventory.query.dto.LotTO;
import de.linogistix.los.inventory.service.LotLockState;
import de.linogistix.los.query.BODTO;
import javafx.util.StringConverter;
import uk.ltd.mediamagic.flow.crud.BODTOPlugin;
import uk.ltd.mediamagic.flow.crud.SubForm;
import uk.ltd.mediamagic.fx.action.AC;
import uk.ltd.mediamagic.fx.action.RootCommand;
import uk.ltd.mediamagic.fx.controller.list.CellRenderer;
import uk.ltd.mediamagic.fx.controller.list.TextRenderer;
import uk.ltd.mediamagic.fx.converters.DateConverter;
import uk.ltd.mediamagic.fx.converters.ToStringConverter;
import uk.ltd.mediamagic.fx.flow.ApplicationContext;
import uk.ltd.mediamagic.fx.flow.Flow;
import uk.ltd.mediamagic.mywms.common.LockStateConverter;
import uk.ltd.mediamagic.mywms.transactions.StockUnitRecordAction;

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
	public Supplier<CellRenderer<BODTO<Lot>>> createTOCellFactory() {
		return TextRenderer.of(ToStringConverter.of(i -> {
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
	
	@Override
	public Flow createNewFlow(ApplicationContext context) {
		return super.createNewFlow(context)
				.globalWithSelection()
					.withSelection("transaction_log", StockUnitRecordAction.forLot())
				.end();
	}
	
	@Override
	protected void configureCommands(RootCommand command) {
		super.configureCommands(command);
		command
		.begin(RootCommand.MENU) 
			.add(AC.id("transaction_log").text("Stock Unit Log"))
		.end();
	}
	
}
