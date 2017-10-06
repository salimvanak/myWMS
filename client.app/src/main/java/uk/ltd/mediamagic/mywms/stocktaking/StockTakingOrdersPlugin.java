package uk.ltd.mediamagic.mywms.stocktaking;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.Supplier;

import de.linogistix.los.query.QueryDetail;
import de.linogistix.los.query.TemplateQuery;
import de.linogistix.los.query.TemplateQueryFilter;
import de.linogistix.los.query.TemplateQueryWhereToken;
import de.linogistix.los.stocktaking.facade.LOSStocktakingFacade;
import de.linogistix.los.stocktaking.model.LOSStocktakingOrder;
import de.linogistix.los.stocktaking.model.LOSStocktakingState;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import res.R;
import uk.ltd.mediamagic.flow.crud.CRUDPlugin;
import uk.ltd.mediamagic.flow.crud.CrudTable;
import uk.ltd.mediamagic.fx.action.AC;
import uk.ltd.mediamagic.fx.action.RootCommand;
import uk.ltd.mediamagic.fx.controller.list.CellRenderer;
import uk.ltd.mediamagic.fx.data.TableKey;
import uk.ltd.mediamagic.fx.flow.ApplicationContext;
import uk.ltd.mediamagic.fx.flow.Flow;
import uk.ltd.mediamagic.fx.flow.ViewContext;
import uk.ltd.mediamagic.fx.flow.ViewContextBase;
import uk.ltd.mediamagic.mywms.common.MyWMSUserPermissions;
import uk.ltd.mediamagic.mywms.common.QueryUtils;

public class StockTakingOrdersPlugin extends CRUDPlugin<LOSStocktakingOrder> {

	private enum Action {Recount, AcceptCount, CreateOrder, RemoveOrder};
	enum StockTakingFilter {All, Open, Waiting, Processing, Finished}
	
	public StockTakingOrdersPlugin() {
		super(LOSStocktakingOrder.class);
	}
	
	@Override
	public String getPath() {
		return "{2, _Internal Orders} -> {2, _Stock Taking Orders}";
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
	protected void createAction(CrudTable<LOSStocktakingOrder> source, Flow flow, ViewContext context) {
		LOSStocktakingFacade service = context.getBean(LOSStocktakingFacade.class);
		//TODO finish this for creating orders.
//		MDialogs.create(owner)
//		
//		service.generateOrders(execute, clientId, areaId, zoneId, rackId, locationId, locationName, itemId, itemNo, invDate, enableEmptyLocations, enableFullLocations, clientModeLocations, clientModeItemData)
	}
	
	public void recount(Object source, Flow flow, ViewContext context, Collection<TableKey> key) {
		LOSStocktakingFacade service = context.getBean(LOSStocktakingFacade.class);
		withMultiSelectionTO(context, key, o -> service.recountOrder(o.getId()));
	}

	public void acceptCount(Object source, Flow flow, ViewContext context, Collection<TableKey> key) {
		LOSStocktakingFacade service = context.getBean(LOSStocktakingFacade.class);
		withMultiSelectionTO(context, key, o -> service.acceptOrder(o.getId()));
	}

	public void removeCount(Object source, Flow flow, ViewContext context, Collection<TableKey> key) {
		LOSStocktakingFacade service = context.getBean(LOSStocktakingFacade.class);
		withMultiSelectionTO(context, key, o -> service.removeOrder(o.getId()));
	}

	@Override
	protected CrudTable<LOSStocktakingOrder> getTable(ViewContextBase context) {
		CrudTable<LOSStocktakingOrder> table = super.getTable(context);
		QueryUtils.addFilter(table, StockTakingFilter.Open, () -> refresh(table, context));
		return table;
	}
	
	@Override
	public Flow createNewFlow(ApplicationContext context) {
		return super.createNewFlow(context)
				.globalWithSelection()
					.withMultiSelection(Action.Recount, this::recount)
					.withMultiSelection(Action.AcceptCount, this::acceptCount)
					.withMultiSelection(Action.RemoveOrder, this::removeCount)
				.end();
	}
	
	@Override
	protected void configureCommands(RootCommand command) {
		super.configureCommands(command);
		command.begin("actions")
			.add(AC.id(Action.AcceptCount).text("Accept Count").description("Accept the stock count and adjust unit loads."))			
			.add(AC.id(Action.Recount).text("Recount").description("Order a recount on this location."))
			.add(AC.id(Action.RemoveOrder).icon(R.icons.delete()).disable(MyWMSUserPermissions.adminUser().not()).description("Remove this storage request"))
		.end();
	}
		
}
