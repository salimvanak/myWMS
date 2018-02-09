package uk.ltd.mediamagic.mywms.stocktaking;

import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

import de.linogistix.los.query.QueryDetail;
import de.linogistix.los.query.TemplateQuery;
import de.linogistix.los.query.TemplateQueryFilter;
import de.linogistix.los.query.TemplateQueryWhereToken;
import de.linogistix.los.stocktaking.model.LOSStocktakingRecord;
import de.linogistix.los.stocktaking.model.LOSStocktakingState;
import javafx.application.Platform;
import javafx.scene.Node;
import uk.ltd.mediamagic.common.utils.Strings;
import uk.ltd.mediamagic.flow.crud.CRUDPlugin;
import uk.ltd.mediamagic.flow.crud.CrudTable;
import uk.ltd.mediamagic.fx.AwesomeIcon;
import uk.ltd.mediamagic.fx.controller.list.CellRenderer;
import uk.ltd.mediamagic.fx.controller.list.MaterialCells;
import uk.ltd.mediamagic.fx.flow.ViewContextBase;
import uk.ltd.mediamagic.mywms.common.QueryUtils;
import uk.ltd.mediamagic.mywms.stocktaking.StockTakingOrdersPlugin.StockTakingFilter;

public class StockTakingRecordsPlugin extends CRUDPlugin<LOSStocktakingRecord> {

	public StockTakingRecordsPlugin() {
		super(LOSStocktakingRecord.class);
	}
	
	@Override
	public String getPath() {
		return "{2, _Internal Orders} -> {2, _Stock Taking Records}";
	}
	
	private Node getIcon(LOSStocktakingRecord r) {
		if (r.getCountedQuantity() == null) return new AwesomeIcon(AwesomeIcon.question_circle);
		else if (r.getPlannedQuantity() == null) return new AwesomeIcon(AwesomeIcon.question_circle);
		else if (r.getPlannedQuantity().compareTo(r.getCountedQuantity()) == 0) return new AwesomeIcon(AwesomeIcon.check_circle);
		else return new AwesomeIcon(AwesomeIcon.warning);
	}
	
	@Override
	public Supplier<CellRenderer<LOSStocktakingRecord>> createCellFactory() {
		return MaterialCells.withID((LOSStocktakingRecord s) -> 
				getIcon(s), 
				LOSStocktakingRecord::getId, 
				s -> {
					if (Strings.isEmpty(s.getUnitLoadLabel())) {
						return "No unit load";
					}
					else {
						return Strings.format("{0}, {1}", s.getUnitLoadLabel(), s.getItemNo());
					}
				},
				s -> {
					if (s.getLotNo() != null) {
						return Strings.format("Lot: {0}", s.getLotNo());
					}
					else {						
						return String.format("No lot information"); 						
					}
				},
				s -> Strings.format("Planned Count {0}, Actual Count {1}", s.getPlannedQuantity(), s.getCountedQuantity()));
	}

	@Override
	public List<String> getTableColumns() {
		return Arrays.asList("created", "locationName", "itemNo", "plannedQuantity", "countedQuantity", "state");
	}
	
	@Override
	protected void refresh(CrudTable<LOSStocktakingRecord> source, ViewContextBase context) {
		TemplateQuery template = source.createQueryTemplate();
		QueryDetail detail = source.createQueryDetail();
		source.clearTable();
		
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
			.thenAcceptAsync(source::setLOSResultList, Platform::runLater);					
	}
	
	@Override
	protected CrudTable<LOSStocktakingRecord> getTable(ViewContextBase context) {
		CrudTable<LOSStocktakingRecord> table = super.getTable(context);
		QueryUtils.addFilter(table, StockTakingFilter.Open, () -> refresh(table, context));
		return table;
	}
		

}
