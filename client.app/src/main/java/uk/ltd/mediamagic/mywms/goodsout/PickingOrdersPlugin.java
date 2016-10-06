package uk.ltd.mediamagic.mywms.goodsout;

import java.beans.PropertyDescriptor;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import de.linogistix.los.inventory.model.LOSPickingOrder;
import de.linogistix.los.model.State;
import de.linogistix.los.query.BODTO;
import de.linogistix.los.query.QueryDetail;
import de.linogistix.los.query.TemplateQuery;
import de.linogistix.los.query.TemplateQueryFilter;
import de.linogistix.los.query.TemplateQueryWhereToken;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;
import javafx.util.StringConverter;
import uk.ltd.mediamagic.flow.crud.BODTOPlugin;
import uk.ltd.mediamagic.flow.crud.BODTOTable;
import uk.ltd.mediamagic.flow.crud.SubForm;
import uk.ltd.mediamagic.fx.action.AC;
import uk.ltd.mediamagic.fx.action.RootCommand;
import uk.ltd.mediamagic.fx.controller.list.MaterialListItems;
import uk.ltd.mediamagic.fx.converters.DateConverter;
import uk.ltd.mediamagic.fx.converters.MapConverter;
import uk.ltd.mediamagic.fx.flow.ApplicationContext;
import uk.ltd.mediamagic.fx.flow.ContextBase;
import uk.ltd.mediamagic.fx.flow.Flow;
import uk.ltd.mediamagic.fx.flow.ViewContextBase;
import uk.ltd.mediamagic.fxcommon.ObservableConstant;
import uk.ltd.mediamagic.mywms.common.MyWMSUserPermissions;
import uk.ltd.mediamagic.mywms.goodsout.GoodsOutUtils.OpenFilter;
import uk.ltd.mediamagic.mywms.goodsout.actions.GoodsOutFinishPickingOrder;
import uk.ltd.mediamagic.mywms.goodsout.actions.GoodsOutHaltPickingOrder;
import uk.ltd.mediamagic.mywms.goodsout.actions.GoodsOutReleasePickingOrder;
import uk.ltd.mediamagic.mywms.goodsout.actions.GoodsOutRemovePickingOrder;

@SubForm(
		title="Main", columns=1, 
		properties={"number", "customerOrderNumber", "state"}
	)
@SubForm(
		title="Picking", columns=2, 
		properties={"strategy", "destination", "operator", "prio"}
	)
public class PickingOrdersPlugin  extends BODTOPlugin<LOSPickingOrder> {

	private enum Action { Release, Halt, Properties, Finish, Remove }
	
	public PickingOrdersPlugin() {
		super(LOSPickingOrder.class);
	}
	
	@Override
	public String getPath() {
		return "{1, _Goods out} -> {1, _Picking Orders}";
	}
	
	@Override
	protected StringConverter<?> getConverter(PropertyDescriptor property) {
		if ("delivery".equals(property.getName())) return new DateConverter();
		else if ("prio".equals(property.getName())) return new MapConverter<Integer>(GoodsOutTypes.priority);
		else if ("state".equals(property.getName())) return new MapConverter<Integer>(GoodsOutTypes.state);
		return super.getConverter(property);
	}
	
	@Override
	public Callback<ListView<LOSPickingOrder>, ListCell<LOSPickingOrder>> createListCellFactory() {
		return MaterialListItems.withID(s -> GoodsOutUtils.getIcon(s.getState()), 
				s -> s.getOperator(), 
				s -> String.format("%s, %s, %s", s.toUniqueString(), s.getCustomerOrderNumber(), s.getDestination()),
				s -> String.format("%s", GoodsOutTypes.state.getValue(s.getState())),
				null);
	}
	
	@Override
	protected void refresh(BODTOTable<LOSPickingOrder> source, ViewContextBase context) {
		OpenFilter filterValue = GoodsOutUtils.getFilter(source);

		TemplateQuery template = source.createQueryTemplate();
		if (filterValue != OpenFilter.All) {
			TemplateQueryFilter filter = template.addNewFilter();
			filter.addWhereToken(new TemplateQueryWhereToken(TemplateQueryWhereToken.OPERATOR_SMALLER, "state", State.FINISHED));
		}

		QueryDetail detail = source.createQueryDetail();

		source.setItems(null);
		getListData(context, detail, template)
			.thenApplyAsync(FXCollections::observableList, Platform::runLater)
			.thenAccept(source::setItems);			

	}
	
	@Override
	public CompletableFuture<List<BODTO<LOSPickingOrder>>> 
	getListData(ContextBase context, QueryDetail detail, TemplateQuery template) {
		if (detail.getOrderBy().isEmpty()) {
			detail.addOrderByToken("created", false);
		}
		return super.getListData(context, detail, template);
	}

	@Override
	public Flow createNewFlow(ApplicationContext context) {
		Flow flow = super.createNewFlow(context);
		flow.globalWithSelection()
		.withMultiSelection(Flow.DELETE_ACTION, new GoodsOutRemovePickingOrder())
		.withMultiSelection(Action.Finish, new GoodsOutFinishPickingOrder())
		.withMultiSelection(Action.Halt, new GoodsOutHaltPickingOrder())
		.withMultiSelection(Action.Release, new GoodsOutReleasePickingOrder())
		.end();
		return flow;
	}
	
	@Override
	protected BODTOTable<LOSPickingOrder> getTable(ViewContextBase context) {
		BODTOTable<LOSPickingOrder> t = super.getTable(context);
		t.getCommands()
			.delete(ObservableConstant.TRUE, ObservableConstant.of(MyWMSUserPermissions.isAtLeastForeman()))
			.menu(RootCommand.MENU)
			.add(AC.id(Action.Release).text("Release for picking"))
			.add(AC.id(Action.Halt).text("Halt picking"))
			.add(AC.id(Action.Properties).text("Change properties"))
			.add(AC.id(Action.Finish).text("Finish"))
			.end()
		.end();
		GoodsOutUtils.addOpenFilter(t, () -> refresh(t, t.getContext()));
		return t;
	}

	@Override
	public List<String> getTableColumns() {
		return Arrays.asList("id", 
				"customerOrderNumber",	"name AS number", "clientNumber AS client.number", 
				"state", "destinationName AS destination.name", "userName AS operator.userName");
	}
	
}
