package uk.ltd.mediamagic.mywms.stocktaking;

import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

import de.linogistix.los.query.BODTO;
import de.linogistix.los.query.QueryDetail;
import de.linogistix.los.query.TemplateQuery;
import de.linogistix.los.query.TemplateQueryFilter;
import de.linogistix.los.query.TemplateQueryWhereToken;
import de.linogistix.los.stocktaking.model.LOSStocktakingRecord;
import de.linogistix.los.stocktaking.model.LOSStocktakingState;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import uk.ltd.mediamagic.flow.crud.CRUDPlugin;
import uk.ltd.mediamagic.flow.crud.CrudTable;
import uk.ltd.mediamagic.fx.controller.list.CellRenderer;
import uk.ltd.mediamagic.fx.controller.list.TextRenderer;
import uk.ltd.mediamagic.fx.converters.ToStringConverter;
import uk.ltd.mediamagic.fx.flow.ViewContextBase;
import uk.ltd.mediamagic.mywms.common.QueryUtils;
import uk.ltd.mediamagic.mywms.stocktaking.StockTakingOrdersPlugin.StockTakingFilter;

public class StockTakingRecordsPlugin extends CRUDPlugin<LOSStocktakingRecord> {

	public StockTakingRecordsPlugin() {
		super(LOSStocktakingRecord.class);
	}
	
	@Override
	public String getPath() {
		return "{1, _Logs} -> {2, _Stock Taking Records}";
	}
	
	@Override
	public Supplier<CellRenderer<BODTO<LOSStocktakingRecord>>> createTOCellFactory() {
		return TextRenderer.of(ToStringConverter.of(i -> i.toString()));
	}

	@Override
	public List<String> getTableColumns() {
		return Arrays.asList("created", "locationName", "itemNo", "plannedQuantity", "countedQuantity", "state");
	}
	
	@Override
	protected void refresh(CrudTable<LOSStocktakingRecord> source, ViewContextBase context) {
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
			filter.addWhereToken(QueryUtils.or(new TemplateQueryWhereToken(TemplateQueryWhereToken.OPERATOR_EQUAL, "state", LOSStocktakingState.CREATED)));
			filter.addWhereToken(QueryUtils.or(new TemplateQueryWhereToken(TemplateQueryWhereToken.OPERATOR_EQUAL, "state", LOSStocktakingState.FREE)));
			break;
		case Processing:			
			filter.addWhereToken(QueryUtils.or(new TemplateQueryWhereToken(TemplateQueryWhereToken.OPERATOR_EQUAL, "state", LOSStocktakingState.STARTED)));
			filter.addWhereToken(QueryUtils.or(new TemplateQueryWhereToken(TemplateQueryWhereToken.OPERATOR_EQUAL, "state", LOSStocktakingState.COUNTED)));
			break;
		case Finished:			
			filter.addWhereToken(QueryUtils.or(new TemplateQueryWhereToken(TemplateQueryWhereToken.OPERATOR_EQUAL, "state", LOSStocktakingState.CANCELLED)));
			filter.addWhereToken(QueryUtils.or(new TemplateQueryWhereToken(TemplateQueryWhereToken.OPERATOR_EQUAL, "state", LOSStocktakingState.FINISHED)));
		case All: default:
		}
		
		getListData(context, detail, template)
			.thenApplyAsync(FXCollections::observableList, Platform::runLater)
			.thenAccept(source::setItems);					
	}
	
	@Override
	protected CrudTable<LOSStocktakingRecord> getTable(ViewContextBase context) {
		CrudTable<LOSStocktakingRecord> table = super.getTable(context);
		QueryUtils.addFilter(table, StockTakingFilter.Open, () -> refresh(table, context));
		return table;
	}
		

}
