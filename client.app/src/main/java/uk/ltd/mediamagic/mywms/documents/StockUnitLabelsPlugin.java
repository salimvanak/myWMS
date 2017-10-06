package uk.ltd.mediamagic.mywms.documents;

import java.util.Arrays;
import java.util.List;

import de.linogistix.los.inventory.model.StockUnitLabel;
import uk.ltd.mediamagic.flow.crud.BODTOPlugin;

public class StockUnitLabelsPlugin  extends BODTOPlugin<StockUnitLabel> {
	
	public StockUnitLabelsPlugin() {
		super(StockUnitLabel.class);
	}


	@Override
	public String getPath() {
		return "{1, _Documents} -> {1, _Stock Unit Labels}";
	}	

	@Override
	protected List<String> getTableColumns() {
		return Arrays.asList("id", "itemdataRef ", "lotRef", "itemMeasure", "date");
	}
		
}
