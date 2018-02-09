package uk.ltd.mediamagic.mywms.internal;

import java.beans.PropertyDescriptor;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import de.linogistix.los.inventory.facade.LOSReplenishFacade;
import de.linogistix.los.inventory.model.LOSReplenishOrder;
import de.linogistix.los.model.State;
import de.linogistix.los.query.QueryDetail;
import de.linogistix.los.query.TemplateQuery;
import de.linogistix.los.query.TemplateQueryFilter;
import de.linogistix.los.query.TemplateQueryWhereToken;
import javafx.application.Platform;
import javafx.scene.control.SelectionMode;
import javafx.util.StringConverter;
import res.R;
import uk.ltd.mediamagic.flow.crud.BODTOPlugin;
import uk.ltd.mediamagic.flow.crud.BODTOTable;
import uk.ltd.mediamagic.fx.AwesomeIcon;
import uk.ltd.mediamagic.fx.action.AC;
import uk.ltd.mediamagic.fx.action.RootCommand;
import uk.ltd.mediamagic.fx.converters.MapConverter;
import uk.ltd.mediamagic.fx.data.TableKey;
import uk.ltd.mediamagic.fx.flow.ApplicationContext;
import uk.ltd.mediamagic.fx.flow.Flow;
import uk.ltd.mediamagic.fx.flow.ViewContext;
import uk.ltd.mediamagic.fx.flow.ViewContextBase;
import uk.ltd.mediamagic.mywms.common.MyWMSUserPermissions;
import uk.ltd.mediamagic.mywms.common.QueryUtils;
import uk.ltd.mediamagic.mywms.common.QueryUtils.OpenAllFilter;
import uk.ltd.mediamagic.mywms.goodsout.GoodsOutTypes;

public class ReplenishPlugin  extends BODTOPlugin<LOSReplenishOrder> {
	
	private enum Action {	REMOVE, CANCEL }

	public ReplenishPlugin() {
		super(LOSReplenishOrder.class);
	}


	@Override
	public String getPath() {
		return "{1, _Internal Orders} -> {1, _Replenish Orders}";
	}

	@Override
	protected	void refresh(BODTOTable<LOSReplenishOrder> source, ViewContextBase context) {
		OpenAllFilter filterValue = QueryUtils.getFilter(source, OpenAllFilter.Open);
		TemplateQuery template = source.createQueryTemplate();
		TemplateQueryFilter filter = template.addNewFilter();
		switch (filterValue) {
			case Open: 
				filter.addWhereToken(new TemplateQueryWhereToken(TemplateQueryWhereToken.OPERATOR_BEFORE, "state", State.PICKED));
				break;
			case All:
			default:
		}
		
		QueryDetail detail = source.createQueryDetail();
		detail.addOrderByToken("created", false);
		source.clearTable();
		getListData(context, detail, template)
			.thenAcceptAsync(source::setLOSResultList, Platform::runLater);			
	}
	
	@Override
	protected StringConverter<?> getConverter(PropertyDescriptor property) {
		if ("state".equals(property.getName())) return new MapConverter<Integer>(GoodsOutTypes.state);
		else if ("prio".equals(property.getName())) return new MapConverter<Integer>(GoodsOutTypes.priority);
		return super.getConverter(property);
	}

	@Override
	protected void configureCommands(RootCommand command) {
		super.configureCommands(command);
		command.add(AC.id(Action.REMOVE).icon(R.icons.delete()).disable(MyWMSUserPermissions.adminUser().not()).description("Remove this replenish order"));
		command.add(AC.id(Action.CANCEL).icon(AwesomeIcon.ban).description("Cancel replenish order"));
	}

	/**
	 * Generate the table layout for table selectors.
	 * The method should be overridden when the table layout needs to be customised.
	 * @param context the context for preparing the controller.
	 * @return the table layout.
	 */
	protected BODTOTable<LOSReplenishOrder> getTable(ViewContextBase context) {
		BODTOTable<LOSReplenishOrder> table = super.getTable(context);
		table.getTable().getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		Runnable refreshData = () -> refresh(table, context);
		QueryUtils.addFilter(table, QueryUtils.OpenAllFilter.Open, refreshData);
		return table;
	}

	@Override
	protected List<String> getTableColumns() {
		return Arrays.asList("id", "number", "destinationLocationName AS destination.name", "itemDataNumber AS itemData.itemNr", "state");
	}
	
	@Override
	public Flow createNewFlow(ApplicationContext context) {
		return super.createNewFlow(context)
			.globalWithSelection()
				.withMultiSelection(Action.REMOVE, this::removeRequest)
				.withMultiSelection(Action.CANCEL, this::cancelRequest)
			.end();
	}
	

	public void cancelRequest(Object obj, Flow flow, ViewContext context, Collection<TableKey> sel) {
		LOSReplenishFacade facade = context.getBean(LOSReplenishFacade.class);
		withMultiSelectionTO(context, sel, o -> facade.finishOrder(o.getName()));
	}

	public void removeRequest(Object obj, Flow flow, ViewContext context, Collection<TableKey> sel) {
		LOSReplenishFacade facade = context.getBean(LOSReplenishFacade.class);
		withMultiSelectionTO(context, sel, o -> facade.removeOrder(o.getName()));
	}
}
