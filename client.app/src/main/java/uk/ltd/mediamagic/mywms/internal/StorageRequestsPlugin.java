package uk.ltd.mediamagic.mywms.internal;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import de.linogistix.los.inventory.facade.StorageFacade;
import de.linogistix.los.inventory.model.LOSStorageRequest;
import de.linogistix.los.inventory.model.LOSStorageRequestState;
import de.linogistix.los.query.QueryDetail;
import de.linogistix.los.query.TemplateQuery;
import de.linogistix.los.query.TemplateQueryFilter;
import de.linogistix.los.query.TemplateQueryWhereToken;
import javafx.application.Platform;
import javafx.scene.control.SelectionMode;
import res.R;
import uk.ltd.mediamagic.flow.crud.BODTOPlugin;
import uk.ltd.mediamagic.flow.crud.BODTOTable;
import uk.ltd.mediamagic.fx.AwesomeIcon;
import uk.ltd.mediamagic.fx.action.AC;
import uk.ltd.mediamagic.fx.action.RootCommand;
import uk.ltd.mediamagic.fx.data.TableKey;
import uk.ltd.mediamagic.fx.flow.ApplicationContext;
import uk.ltd.mediamagic.fx.flow.Flow;
import uk.ltd.mediamagic.fx.flow.ViewContext;
import uk.ltd.mediamagic.fx.flow.ViewContextBase;
import uk.ltd.mediamagic.mywms.common.MyWMSUserPermissions;
import uk.ltd.mediamagic.mywms.common.QueryUtils;
import uk.ltd.mediamagic.mywms.common.QueryUtils.OpenAllFilter;

public class StorageRequestsPlugin  extends BODTOPlugin<LOSStorageRequest> {
	

	private enum Action {	CANCEL, REMOVE }

	public StorageRequestsPlugin() {
		super(LOSStorageRequest.class);
	}

	@Override
	public String getPath() {
		return "{1, _Internal Orders} -> {1, _Storage Requests}";
	}

	@Override
	protected	void refresh(BODTOTable<LOSStorageRequest> source, ViewContextBase context) {
		OpenAllFilter filterValue = QueryUtils.getFilter(source, OpenAllFilter.Open);
		TemplateQuery template = source.createQueryTemplate();
		TemplateQueryFilter filter = template.addNewFilter();
		switch (filterValue) {
			case Open: 
				filter.addWhereToken(QueryUtils.or(new TemplateQueryWhereToken(TemplateQueryWhereToken.OPERATOR_EQUAL, "requestState", "state1", LOSStorageRequestState.RAW)));
				filter.addWhereToken(QueryUtils.or(new TemplateQueryWhereToken(TemplateQueryWhereToken.OPERATOR_EQUAL, "requestState", "state2", LOSStorageRequestState.PROCESSING)));
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

	/**
	 * Generate the table layout for table selectors.
	 * The method should be overridden when the table layout needs to be customised.
	 * @param context the context for preparing the controller.
	 * @return the table layout.
	 */
	protected BODTOTable<LOSStorageRequest> getTable(ViewContextBase context) {
		BODTOTable<LOSStorageRequest> table = super.getTable(context);
		table.getTable().getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		Runnable refreshData = () -> refresh(table, context);
		QueryUtils.addFilter(table, QueryUtils.OpenAllFilter.Open, refreshData);
		return table;
	}

	@Override
	protected List<String> getTableColumns() {
		return Arrays.asList("id", "unitLoadLabel AS unitLoad.labelId", "requestStateName AS requestState", "destinationName AS destination.name");
	}
	
	@Override
	protected void configureCommands(RootCommand command) {
		super.configureCommands(command);
		command.add(AC.id(Action.REMOVE).icon(R.icons.delete()).disable(MyWMSUserPermissions.adminUser().not()).description("Remove this storage request"));
		command.add(AC.id(Action.CANCEL).icon(AwesomeIcon.ban).description("Cancel storage request"));
	}
	
	@Override
	public Flow createNewFlow(ApplicationContext context) {
		return super.createNewFlow(context)
			.with(BODTOTable.class)
				.withMultiSelection(Action.CANCEL, this::cancelRequest)
				.withMultiSelection(Action.REMOVE, this::removeRequest)
			.end();
	}

	public void cancelRequest(Object obj, Flow flow, ViewContext context, Collection<TableKey> sel) {
		StorageFacade facade = context.getBean(StorageFacade.class);
		withMultiSelectionTO(context, sel, o -> facade.cancelStorageRequest(o));
	}

	public void removeRequest(Object obj, Flow flow, ViewContext context, Collection<TableKey> sel) {
		StorageFacade facade = context.getBean(StorageFacade.class);
		withMultiSelectionTO(context, sel, o -> facade.removeStorageRequest(o));
	}
}
