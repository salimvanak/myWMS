package uk.ltd.mediamagic.mywms.stocktaking;

import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

import de.linogistix.los.query.QueryDetail;
import de.linogistix.los.query.TemplateQuery;
import de.linogistix.los.query.TemplateQueryFilter;
import de.linogistix.los.query.TemplateQueryWhereToken;
import de.linogistix.los.stocktaking.model.LOSStocktakingOrder;
import de.linogistix.los.stocktaking.model.LOSStocktakingState;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import uk.ltd.mediamagic.flow.crud.CRUDPlugin;
import uk.ltd.mediamagic.flow.crud.CrudTable;
import uk.ltd.mediamagic.fx.controller.list.CellRenderer;
import uk.ltd.mediamagic.fx.flow.ViewContextBase;
import uk.ltd.mediamagic.mywms.common.QueryUtils;

public class StockTakingOrdersPlugin extends CRUDPlugin<LOSStocktakingOrder> {

	enum StockTakingFilter {All, Open, Waiting, Processing, Finished}
	
	public StockTakingOrdersPlugin() {
		super(LOSStocktakingOrder.class);
	}
	
	@Override
	public String getPath() {
		return "{1, _Logs} -> {2, _Stock Taking Orders}";
	}
	
	@Override
	public Supplier<CellRenderer<LOSStocktakingOrder>> createCellFactory() {
		return super.createCellFactory();
	}

	@Override
	public List<String> getTableColumns() {
		return Arrays.asList("created", "locationName", "areaName", "operatorName", "countingDate", "state");
	}
	
	@Override
	protected void refresh(CrudTable<LOSStocktakingOrder> source, ViewContextBase context) {
		TemplateQuery template = source.createQueryTemplate();
		QueryDetail detail = source.createQueryDetail();
		source.setItems(null);
		
		StockTakingFilter filterValue = QueryUtils.getFilter(source, StockTakingFilter.Open);
		TemplateQueryFilter filter = template.addNewFilter();
		switch (filterValue) {
		case Open:
			filter.addWhereToken(new TemplateQueryWhereToken(TemplateQueryWhereToken.OPERATOR_NOT_EQUAL, "state", LOSStocktakingState.CANCELLED));
			filter.addWhereToken(new TemplateQueryWhereToken(TemplateQueryWhereToken.OPERATOR_NOT_EQUAL, "state", LOSStocktakingState.FINISHED));
			break;
		case Waiting:			
			filter.addWhereToken(QueryUtils.or(new TemplateQueryWhereToken(TemplateQueryWhereToken.OPERATOR_EQUAL, "state", "state1", LOSStocktakingState.CREATED)));
			filter.addWhereToken(QueryUtils.or(new TemplateQueryWhereToken(TemplateQueryWhereToken.OPERATOR_EQUAL, "state", "state2", LOSStocktakingState.FREE)));
			break;
		case Processing:			
			filter.addWhereToken(QueryUtils.or(new TemplateQueryWhereToken(TemplateQueryWhereToken.OPERATOR_EQUAL, "state", "state1", LOSStocktakingState.STARTED)));
			filter.addWhereToken(QueryUtils.or(new TemplateQueryWhereToken(TemplateQueryWhereToken.OPERATOR_EQUAL, "state", "state2", LOSStocktakingState.COUNTED)));
			break;
		case Finished:			
			filter.addWhereToken(QueryUtils.or(new TemplateQueryWhereToken(TemplateQueryWhereToken.OPERATOR_EQUAL, "state", "state1", LOSStocktakingState.CANCELLED)));
			filter.addWhereToken(QueryUtils.or(new TemplateQueryWhereToken(TemplateQueryWhereToken.OPERATOR_EQUAL, "state", "state2", LOSStocktakingState.FINISHED)));
			break;
		case All: default:
		}
		
		getListData(context, detail, template)
			.thenApplyAsync(FXCollections::observableList, Platform::runLater)
			.thenAccept(source::setItems);					
	}
	
	@Override
	protected CrudTable<LOSStocktakingOrder> getTable(ViewContextBase context) {
		CrudTable<LOSStocktakingOrder> table = super.getTable(context);
		QueryUtils.addFilter(table, StockTakingFilter.Open, () -> refresh(table, context));
		return table;
	}
		
}
