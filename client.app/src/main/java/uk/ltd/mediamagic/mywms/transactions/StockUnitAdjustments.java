package uk.ltd.mediamagic.mywms.transactions;

import java.util.function.Consumer;
import java.util.function.Function;

import de.linogistix.los.inventory.model.LOSStockUnitRecordType;
import de.linogistix.los.query.TemplateQuery;
import de.linogistix.los.query.TemplateQuery.LogicalOperator;
import de.linogistix.los.query.TemplateQueryFilter;
import de.linogistix.los.query.TemplateQueryWhereToken;
import javafx.scene.Node;
import javafx.scene.Parent;
import uk.ltd.mediamagic.fx.flow.ApplicationContext;
import uk.ltd.mediamagic.mywms.MyWMSMainMenuPlugin;

public class StockUnitAdjustments extends MyWMSMainMenuPlugin {

	@Override
	public String getPath() {
		return "{1, _Logs} -> {2, _Stock Unit Adjustments}";
	}
	
	@Override
	public void handle(ApplicationContext context, Parent source, Function<Node, Runnable> showNode) {
		Consumer<TemplateQuery> filter = t -> {
				t.setLogicalOperator(LogicalOperator.OR);
				TemplateQueryFilter f = t.addNewFilter();
				f.addWhereToken(new TemplateQueryWhereToken(TemplateQueryWhereToken.OPERATOR_LIKE, "activityCode", "IMAN"));
				
			
				TemplateQueryFilter f2 = t.addNewFilter();
				f2.addWhereToken(new TemplateQueryWhereToken(TemplateQueryWhereToken.OPERATOR_LIKE, "activityCode", "PICK"));
				f2.addWhereToken(new TemplateQueryWhereToken(TemplateQueryWhereToken.OPERATOR_EQUAL, "type", LOSStockUnitRecordType.STOCK_ALTERED));
			};

		StockUnitLogPlugin p = new StockUnitLogPlugin("Stock Adjustments", filter);
		p.handle(context, source, showNode);
	}
}
