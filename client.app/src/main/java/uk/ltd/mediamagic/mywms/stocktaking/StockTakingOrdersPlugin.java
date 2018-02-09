package uk.ltd.mediamagic.mywms.stocktaking;

import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.function.Supplier;

import org.mywms.model.Area;
import org.mywms.model.Client;
import org.mywms.model.ItemData;
import org.mywms.model.Zone;

import de.linogistix.los.location.model.LOSRack;
import de.linogistix.los.location.model.LOSStorageLocation;
import de.linogistix.los.query.QueryDetail;
import de.linogistix.los.query.TemplateQuery;
import de.linogistix.los.query.TemplateQueryFilter;
import de.linogistix.los.query.TemplateQueryWhereToken;
import de.linogistix.los.stocktaking.facade.LOSStocktakingFacade;
import de.linogistix.los.stocktaking.model.LOSStocktakingOrder;
import de.linogistix.los.stocktaking.model.LOSStocktakingState;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import res.R;
import uk.ltd.mediamagic.common.utils.Strings;
import uk.ltd.mediamagic.flow.crud.BasicEntityEditor;
import uk.ltd.mediamagic.flow.crud.CRUDPlugin;
import uk.ltd.mediamagic.flow.crud.CrudTable;
import uk.ltd.mediamagic.fx.MDialogs;
import uk.ltd.mediamagic.fx.action.AC;
import uk.ltd.mediamagic.fx.action.RootCommand;
import uk.ltd.mediamagic.fx.control.FormBuilder;
import uk.ltd.mediamagic.fx.controller.list.CellRenderer;
import uk.ltd.mediamagic.fx.data.TableKey;
import uk.ltd.mediamagic.fx.flow.ApplicationContext;
import uk.ltd.mediamagic.fx.flow.FXErrors;
import uk.ltd.mediamagic.fx.flow.Flow;
import uk.ltd.mediamagic.fx.flow.ViewContext;
import uk.ltd.mediamagic.fx.flow.ViewContextBase;
import uk.ltd.mediamagic.fx.helpers.ComboBoxes;
import uk.ltd.mediamagic.mywms.common.MyWMSUserPermissions;
import uk.ltd.mediamagic.mywms.common.QueryUtils;
import uk.ltd.mediamagic.mywms.master.StorageLocationPlugin;

public class StockTakingOrdersPlugin extends CRUDPlugin<LOSStocktakingOrder> {

	private enum Action {Recount, AcceptCount, CreateOrder, RemoveOrder};
	enum StockTakingFilter {All, Open, Waiting, Processing, Finished};
	
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
		source.clearTable();
		
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
			.thenAcceptAsync(source::setLOSResultList, Platform::runLater);			
	}
	
	protected void createOrdersAction(Object obj, Flow flow, ViewContext context) {
		Node parent = context.getRootNode();
		LOSStocktakingFacade service = context.getBean(LOSStocktakingFacade.class);
		
		BasicEntityEditor<Client> client = BasicEntityEditor.create(context, Client.class);
		BasicEntityEditor<Area> area = BasicEntityEditor.create(context, Area.class);
		BasicEntityEditor<Zone> zone = BasicEntityEditor.create(context, Zone.class);
		BasicEntityEditor<LOSRack> rack = BasicEntityEditor.create(context, LOSRack.class);
		BasicEntityEditor<LOSStorageLocation> location = BasicEntityEditor.create(context, LOSStorageLocation.class);		
		BasicEntityEditor<ItemData> item = BasicEntityEditor.create(context, ItemData.class);
		
		TextField itemName = new TextField();
		TextField locationName = new TextField();
		
		ComboBox<StorageLocationPlugin.AllocationFilter> allocation = ComboBoxes.createComboForEnum(StorageLocationPlugin.AllocationFilter.All_Locations);
		
		DatePicker stockCheckDate = new DatePicker();
		BooleanProperty lastStockTakeEnabled = new SimpleBooleanProperty(false);
		stockCheckDate.disableProperty().bind(lastStockTakeEnabled.not());
		
		FormBuilder form = new FormBuilder();
		form.row().label("Client").fieldNode(client).end();
		form.row().label("Area").fieldNode(area).end();
		form.row().label("Zone").fieldNode(zone).end();
		form.row().label("Rack").fieldNode(rack).end();
		form.row().label("Location").fieldNode(location).label("Name").fieldNode(locationName).end();
		form.row().label("Item").fieldNode(item).label("Name").fieldNode(itemName).end();
		form.row().label("Last stock take").checkbox().bind(lastStockTakeEnabled).fieldNode(stockCheckDate).colSpan(2).end();
		form.row().label("Allocation").fieldNode(allocation).end();
		
		boolean ok = MDialogs.create(parent).content(form).showOkCancel();
		if (!ok) return;
		
		Long clientId = (client.getValue() == null) ? null : client.getValue().getId(); 
		Long areaId = (area.getValue() == null) ? null : area.getValue().getId();
		Long zoneId = (zone.getValue() == null) ? null : zone.getValue().getId();
		Long rackId = (rack.getValue() == null) ? null : rack.getValue().getId();
		Long locationId = (location.getValue() == null) ? null : location.getValue().getId();
		Long itemId = (item.getValue() == null) ? null : item.getValue().getId();
		Date stockCheck = (!lastStockTakeEnabled.get()) ? null : java.sql.Date.valueOf(stockCheckDate.getValue());
		
		String locationNameStr = (Strings.isEmpty(locationName.getText()) ? null : locationName.getText());
		String itemNo = (Strings.isEmpty(itemName.getText()) ? null : itemName.getText());
		boolean enableEmptyLocations = allocation.getValue() != StorageLocationPlugin.AllocationFilter.Locations_with_stock;
		boolean enableFullLocations = allocation.getValue() != StorageLocationPlugin.AllocationFilter.Empty_locations;
		
		int count = context.getExecutor().executeAndWait(parent, p -> {
			return service.generateOrders(false, clientId, areaId, zoneId, rackId, locationId, locationNameStr, itemId, itemNo, 
					stockCheck, enableEmptyLocations, enableFullLocations);			
		});
		
		if (count <= 0) {
			FXErrors.error(parent, "No matching locations found.");
		}
		else {
			boolean yes = MDialogs.create(parent, "Create stock taking orders")
			.masthead(count + " locations found")
			.message("Create stock taking orders")
			.showYesNo();
			if (yes) {
				int count2 = context.getExecutor().executeAndWait(parent, p -> {
					return service.generateOrders(true, clientId, areaId, zoneId, rackId, locationId, locationNameStr, itemId, itemNo, 
							stockCheck, enableEmptyLocations, enableFullLocations);			
				});				
				FXErrors.info(parent, count2 + " stock take orders created.");
				
			}
		}
				
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
		table.getCommands()
			.add(AC.id(Action.CreateOrder).text("Create Orders").description("Create stock taking order for one or more locations"))
		.end();
		return table;
	}
	
	@Override
	public Flow createNewFlow(ApplicationContext context) {
		return super.createNewFlow(context)
				.globalWithSelection()
					.withMultiSelection(Action.Recount, this::recount)
					.withMultiSelection(Action.AcceptCount, this::acceptCount)
					.withMultiSelection(Action.RemoveOrder, this::removeCount)
				.end()
				.global()
					.action(Action.CreateOrder, this::createOrdersAction)
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
