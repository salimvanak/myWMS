package uk.ltd.mediamagic.mywms.stocktaking;

import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

import de.linogistix.los.stocktaking.model.LOSStockTaking;
import javafx.beans.binding.BooleanBinding;
import uk.ltd.mediamagic.flow.crud.CRUDPlugin;
import uk.ltd.mediamagic.fx.controller.list.CellRenderer;
import uk.ltd.mediamagic.mywms.common.MyWMSUserPermissions;

public class StockTakingPlugin extends CRUDPlugin<LOSStockTaking> {
	
	public StockTakingPlugin() {
		super(LOSStockTaking.class);
	}
	
	@Override
	protected BooleanBinding createVisibleBinding() {
		return MyWMSUserPermissions.adminUser();
	}

	@Override
	public String getPath() {
		return "{2, _Internal Orders} -> {2, _Stock Taking}";
	}
	
	@Override
	public Supplier<CellRenderer<LOSStockTaking>> createCellFactory() {
		return super.createCellFactory();
	}

	@Override
	public List<String> getTableColumns() {
		return Arrays.asList("stockTakingNumber", "stockTakingType", "started", "ended");
	}	
}
