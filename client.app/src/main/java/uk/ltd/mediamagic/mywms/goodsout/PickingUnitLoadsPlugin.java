package uk.ltd.mediamagic.mywms.goodsout;

import java.beans.PropertyDescriptor;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.function.Supplier;

import de.linogistix.los.inventory.model.LOSPickingUnitLoad;
import de.linogistix.los.inventory.query.dto.LOSPickingUnitLoadTO;
import de.linogistix.los.model.State;
import de.linogistix.los.query.BODTO;
import de.linogistix.los.query.LOSResultList;
import de.linogistix.los.query.QueryDetail;
import de.linogistix.los.query.TemplateQuery;
import de.linogistix.los.query.TemplateQueryFilter;
import de.linogistix.los.query.TemplateQueryWhereToken;
import javafx.application.Platform;
import javafx.util.StringConverter;
import uk.ltd.mediamagic.flow.crud.BODTOPlugin;
import uk.ltd.mediamagic.flow.crud.BODTOTable;
import uk.ltd.mediamagic.flow.crud.MyWMSEditor;
import uk.ltd.mediamagic.flow.crud.SubForm;
import uk.ltd.mediamagic.fx.action.AC;
import uk.ltd.mediamagic.fx.action.RootCommand;
import uk.ltd.mediamagic.fx.controller.list.CellRenderer;
import uk.ltd.mediamagic.fx.controller.list.MaterialCells;
import uk.ltd.mediamagic.fx.converters.MapConverter;
import uk.ltd.mediamagic.fx.data.TableKey;
import uk.ltd.mediamagic.fx.flow.ApplicationContext;
import uk.ltd.mediamagic.fx.flow.ContextBase;
import uk.ltd.mediamagic.fx.flow.Flow;
import uk.ltd.mediamagic.fx.flow.ViewContextBase;
import uk.ltd.mediamagic.mywms.goodsout.GoodsOutUtils.OpenFilter;
import uk.ltd.mediamagic.mywms.goodsout.actions.GoodsOutCreateShippingOrder;
import uk.ltd.mediamagic.mywms.goodsout.actions.GoodsOutFinishPickingUnitLoad;
import uk.ltd.mediamagic.mywms.goodsout.actions.GoodsOutPrintPickingUnitLoad;
import uk.ltd.mediamagic.util.Closures;

@SubForm(
		title="Main", columns=1, 
		properties={"unitLoad", "customerOrderNumber", "pickingOrder", "state", "positionIndex"}
	)
public class PickingUnitLoadsPlugin  extends BODTOPlugin<LOSPickingUnitLoad> {
 
	private enum Actions {Print, Finish, ShippingOrder}
	
	
	public PickingUnitLoadsPlugin() {
		super(LOSPickingUnitLoad.class);
	}
	
	@Override
	public String getPath() {
		return "{1, _Goods out} -> {1, _Picking unit loads}";
	}
	
	@Override
	protected StringConverter<?> getConverter(PropertyDescriptor property) {
		if ("state".equals(property.getName())) return new MapConverter<Integer>(GoodsOutTypes.state);
		return super.getConverter(property);
	}
	
	@Override
	public Supplier<CellRenderer<LOSPickingUnitLoad>> createCellFactory() {
		return MaterialCells.withID(s -> GoodsOutUtils.getIcon(s.getState()), 
				s -> s.getUnitLoad().toUniqueString(), 
				s -> String.format("%s (%s)", s.getPickingOrder().toUniqueString(), s.getClient()),
				s -> String.format("%s", s.getCustomerOrderNumber()),
				s -> String.format("%s", s.getUnitLoad().getStorageLocation()));
	}
		
	@Override
	public Supplier<CellRenderer<BODTO<LOSPickingUnitLoad>>> createTOCellFactory() {
		Function<BODTO<LOSPickingUnitLoad>, LOSPickingUnitLoadTO> cast = Closures.cast(LOSPickingUnitLoadTO.class);
		return MaterialCells.withID(cast.andThen(s -> GoodsOutUtils.getIcon(s.getState())), 
				cast.andThen(s -> s.getLabel()), 
				cast.andThen(s -> String.format("%s (%s)", s.getPickingOrderNumber(), s.getClientNumber())),
				cast.andThen(s -> String.format("%s", s.getCustomerOrderNumber())),
				cast.andThen(s -> String.format("%s", s.getLocationName())));
	}

	@Override
	public CompletableFuture<LOSResultList<BODTO<LOSPickingUnitLoad>>> 
	getListData(ContextBase context, QueryDetail detail, TemplateQuery template) {
		if (detail.getOrderBy().isEmpty()) {
			detail.addOrderByToken("created", false);
		}
		return super.getListData(context, detail, template);
	}

	@Override
	protected void refresh(BODTOTable<LOSPickingUnitLoad> source, ViewContextBase context) {
		OpenFilter filterValue = GoodsOutUtils.getFilter(source);

		TemplateQuery template = source.createQueryTemplate();
		if (filterValue != OpenFilter.All) {
			TemplateQueryFilter filter = template.addNewFilter();
			filter.addWhereToken(new TemplateQueryWhereToken(TemplateQueryWhereToken.OPERATOR_SMALLER, "state", State.FINISHED));
		}

		QueryDetail detail = source.createQueryDetail();

		source.clearTable();
		getListData(context, detail, template)
			.thenAcceptAsync(source::setLOSResultList, Platform::runLater);			

	}
	
	@Override
	public Flow createNewFlow(ApplicationContext context) {
		Flow flow = super.createNewFlow(context);
		flow
		.globalWithSelection()
			.withMultiSelection(Actions.Finish, new GoodsOutFinishPickingUnitLoad())
			.withMultiSelection(Actions.Print, new GoodsOutPrintPickingUnitLoad())
			.withMultiSelection(Actions.ShippingOrder, new GoodsOutCreateShippingOrder())
		.end();
		
		return flow;
	}
	
	@Override
	protected MyWMSEditor<LOSPickingUnitLoad> getEditor(ContextBase context, TableKey key) {
		MyWMSEditor<LOSPickingUnitLoad> editor = super.getEditor(context, key);
		editor.getCommands()
			.menu(RootCommand.MENU_PRINT)
				.add(AC.id(Actions.Print).text("Unit load label"))
			.end()
			.menu(RootCommand.MENU)
				.add(AC.id(Actions.Finish).text("Finish unit load"))
				.add(AC.id(Actions.ShippingOrder).text("Create shipping order"))
			.end()
		.end();
		return editor;
	}
	
	@Override
	protected BODTOTable<LOSPickingUnitLoad> getTable(ViewContextBase context) {
		BODTOTable<LOSPickingUnitLoad> t = super.getTable(context);
		t.getCommands()
			.menu(RootCommand.MENU_PRINT)
				.add(AC.id(Actions.Print).text("Unit load label"))
			.end()
			.menu(RootCommand.MENU)
				.add(AC.id(Actions.Finish).text("Finish unit load"))
				.add(AC.id(Actions.ShippingOrder).text("Create shipping order"))
			.end()
		.end();
		GoodsOutUtils.addOpenFilter(t, () -> refresh(t, t.getContext()));
		return t;
	}

	
	@Override
	public List<String> getTableColumns() {
		return Arrays.asList("id", 
				"name AS unitload.labelId",	"clientNumber AS client.number", 
				"customerOrderNumber", "pickingOrderNumber AS pickingOrder.number", 
				"locationName AS unitLoad.storageLocation", "state");
	}
	
}
