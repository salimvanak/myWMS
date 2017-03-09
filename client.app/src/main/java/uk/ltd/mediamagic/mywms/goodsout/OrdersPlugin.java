package uk.ltd.mediamagic.mywms.goodsout;

import java.beans.PropertyDescriptor;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;
import java.util.logging.Level;

import org.mywms.model.User;

import de.linogistix.los.inventory.facade.LOSOrderFacade;
import de.linogistix.los.inventory.facade.LOSPickingFacade;
import de.linogistix.los.inventory.facade.OrderPositionTO;
import de.linogistix.los.inventory.model.LOSCustomerOrder;
import de.linogistix.los.inventory.model.LOSCustomerOrderPosition;
import de.linogistix.los.inventory.query.LOSCustomerOrderQueryRemote;
import de.linogistix.los.inventory.query.dto.LOSCustomerOrderTO;
import de.linogistix.los.location.model.LOSStorageLocation;
import de.linogistix.los.model.Prio;
import de.linogistix.los.model.State;
import de.linogistix.los.query.BODTO;
import de.linogistix.los.query.LOSResultList;
import de.linogistix.los.query.QueryDetail;
import de.linogistix.los.query.TemplateQuery;
import de.linogistix.los.query.TemplateQueryFilter;
import de.linogistix.los.query.TemplateQueryWhereToken;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextArea;
import javafx.scene.control.TitledPane;
import javafx.scene.control.ToggleGroup;
import javafx.util.StringConverter;
import uk.ltd.mediamagic.flow.crud.BODTOPlugin;
import uk.ltd.mediamagic.flow.crud.BODTOTable;
import uk.ltd.mediamagic.flow.crud.BasicEntityEditor;
import uk.ltd.mediamagic.flow.crud.CRUDKeyUtils;
import uk.ltd.mediamagic.flow.crud.MyWMSEditor;
import uk.ltd.mediamagic.fx.MDialogs;
import uk.ltd.mediamagic.fx.action.AC;
import uk.ltd.mediamagic.fx.action.RootCommand;
import uk.ltd.mediamagic.fx.controller.list.CellRenderer;
import uk.ltd.mediamagic.fx.controller.list.MaterialCells;
import uk.ltd.mediamagic.fx.converters.BigDecimalConverter;
import uk.ltd.mediamagic.fx.converters.DateConverter;
import uk.ltd.mediamagic.fx.converters.DateTimeConverter;
import uk.ltd.mediamagic.fx.converters.MapConverter;
import uk.ltd.mediamagic.fx.data.TableKey;
import uk.ltd.mediamagic.fx.flow.ApplicationContext;
import uk.ltd.mediamagic.fx.flow.ContextBase;
import uk.ltd.mediamagic.fx.flow.Flow;
import uk.ltd.mediamagic.fx.flow.ViewContext;
import uk.ltd.mediamagic.fx.flow.ViewContextBase;
import uk.ltd.mediamagic.fx.table.MTableViewBase;
import uk.ltd.mediamagic.fxcommon.ObservableConstant;
import uk.ltd.mediamagic.mywms.FlowUtils;
import uk.ltd.mediamagic.mywms.common.MyWMSUserPermissions;
import uk.ltd.mediamagic.mywms.common.QueryUtils;
import uk.ltd.mediamagic.mywms.goodsout.GoodsOutUtils.OpenFilter;
import uk.ltd.mediamagic.util.Closures;
import uk.ltd.mediamagic.util.DateUtils;

//@SubForm(
//		title="Main", columns=1, 
//		properties={"number", "externalNumber", "externalId", "state", "strategy", "destination", "prio"}
//	)
//@SubForm(
//		title="Delivery", columns=2, 
//		properties={"customerNumber", "customerName", "delivery", "documentUrl", "labelUrl", "dtype"}
//	)
public class OrdersPlugin  extends BODTOPlugin<LOSCustomerOrder> {

	private enum Action {FinishOrder, FinishPicking, Remove, Start, Overview, TreatOrder}
	
	
	public OrdersPlugin() {
		super(LOSCustomerOrder.class);
	}
	
	@Override
	public String getPath() {
		return "{1, _Goods out} -> {1, _Orders}";
	}
	
	@Override
	protected StringConverter<?> getConverter(PropertyDescriptor property) {
		if ("delivery".equals(property.getName())) return new DateConverter();
		else if ("prio".equals(property.getName())) return new MapConverter<Integer>(GoodsOutTypes.priority);
		else if ("state".equals(property.getName())) return new MapConverter<Integer>(GoodsOutTypes.state);
		return super.getConverter(property);
	}
	
	@Override
	public Supplier<CellRenderer<LOSCustomerOrder>> createCellFactory() {
		return MaterialCells.withDate(s -> GoodsOutUtils.getIcon(s.getState()), 
				s -> DateUtils.toLocalDate(s.getDelivery()), 
				s -> String.format("%s, %s, %s", s.toUniqueString(), s.getExternalNumber(), s.getDestination()),
				s -> String.format("%s, %s", GoodsOutTypes.state.getValue(s.getState()), s.getCustomerName()),
				null);
	}
	
	@Override
	public Supplier<CellRenderer<BODTO<LOSCustomerOrder>>> createTOCellFactory() {
		return MaterialCells.withDate(s -> GoodsOutUtils.getIcon(((LOSCustomerOrderTO)s).getState()), 
				s -> null, 
				s -> String.format("%s, %s, %s", ((LOSCustomerOrderTO)s).getName(), ((LOSCustomerOrderTO)s).getExternalNumber(), ((LOSCustomerOrderTO)s).getDestinationName()),
				s -> String.format("%s, %s", GoodsOutTypes.state.getValue(((LOSCustomerOrderTO)s).getState()), ((LOSCustomerOrderTO)s).getCustomerName()),
				null);
	}

	@Override
	protected void refresh(BODTOTable<LOSCustomerOrder> source, ViewContextBase context) {
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
	public CompletableFuture<LOSResultList<BODTO<LOSCustomerOrder>>> getListData(ContextBase context, QueryDetail detail, TemplateQuery template) {
		if (detail.getOrderBy().isEmpty()) {
			detail.addOrderByToken("created", false);
		}
		return super.getListData(context, detail, template);
	}

	@Override
	public List<String> getTableColumns() {
		return Arrays.asList("id", 
				"name AS number",	"clientNumber AS client.number", 
				"customerNumber", "customerName", "externalNumber", "delivery", "state");
	}
	
	private void finishOrder(Object source, Flow flow, ViewContext context, TableKey key) {
		boolean ok = MDialogs.create(context.getRootNode(), "Finish Order")
				.message("This will finish the order.\nAll outstanding picks will be canceled.")
			.showYesNo();
		
		if (!ok) return; // user canceled
				
		LOSOrderFacade facade = context.getBean(LOSOrderFacade.class);
		long id = key.get("id");
		context.getExecutor().run(() -> {
			facade.finishOrder(id);
		})
		.thenAcceptAsync(x -> flow.executeCommand(Flow.REFRESH_ACTION), Platform::runLater);
	}

	private void finishPicking(Object source, Flow flow, ViewContext context, TableKey key) {
		boolean ok = MDialogs.create(context.getRootNode(), "Finish Order")
				.message("This will finish the order.\nAll outstanding picklists will be marked as picked.")
			.showYesNo();
		
		if (!ok) return; // user canceled
				
		LOSOrderFacade facade = context.getBean(LOSOrderFacade.class);
		LOSCustomerOrderQueryRemote crud = context.getBean(LOSCustomerOrderQueryRemote.class);
		long id = key.get("id");
		context.getExecutor().run(() -> {
			List<BODTO<LOSCustomerOrder>> orders = crud.queryHandlesById(Collections.singletonList(id), new QueryDetail(0, 1));
			facade.processOrderPickedFinish(orders);
		})
		.thenAcceptAsync(x -> flow.executeCommand(Flow.REFRESH_ACTION), Platform::runLater);
	}

	private void removeOrder(Object source, Flow flow, ViewContext context, TableKey key) {
		boolean ok = MDialogs.create(context.getRootNode(), "Remove Order")
				.message("This will delete order.\nAll outstanding picklists will also be deleted.")
			.showYesNo();
		
		if (!ok) return; // user canceled
				
		LOSOrderFacade facade = context.getBean(LOSOrderFacade.class);
		long id = key.get("id");
		context.getExecutor().run(() -> {
			facade.removeOrder(id);
		})
		.thenAcceptAsync(x -> flow.executeCommand(Flow.REFRESH_ACTION), Platform::runLater);
	}

	private void startOrder(Object source, Flow flow, ViewContext context, TableKey key) {
		TextArea commentField = new TextArea();
		ComboBox<Integer> prioField = QueryUtils.priorityCombo();
		BasicEntityEditor<LOSStorageLocation> destinationField = new BasicEntityEditor<>();
		BasicEntityEditor<User> userField = new BasicEntityEditor<>();
		CheckBox releaseOrder = new CheckBox("Release picking order");
		RadioButton useStrategy = new RadioButton("Use picking strategy");
		RadioButton createOnePerCustomer = new RadioButton("Create on pick per customer");
		RadioButton createOne = new RadioButton("Create only one picking order");

		ToggleGroup tg = new ToggleGroup();
		createOnePerCustomer.setToggleGroup(tg);
		createOne.setToggleGroup(tg);
		useStrategy.setToggleGroup(tg);
		
		releaseOrder.setSelected(true);
		useStrategy.setSelected(true);
		userField.configure(context, User.class);
		destinationField.configure(context, LOSStorageLocation.class);
		
		boolean ok = MDialogs.create(context.getRootNode(), "Lock Stock Unit")
			.input("Priority", prioField)
			.input("Destination", destinationField)
			.input("User", userField)
			.input("", releaseOrder)
			.input("", useStrategy)
			.input("", createOnePerCustomer)
			.input("", createOne)
			.input(new TitledPane("Comment", commentField))
			.showOkCancel();

		if (!ok) return; // user canceled
				
		boolean useSingleOrderService = createOne.isSelected();
		boolean useStratOrderService = useStrategy.isSelected();
		int prio = prioField.getValue();
		String destinationName = Closures.guardedValue(
				destinationField.getValue(), LOSStorageLocation::getName, null);
		boolean setProcessable = releaseOrder.isSelected();
		String userName = Closures.guardedValue(userField.getValue(), User::getName, null);
		LOSPickingFacade facade = context.getBean(LOSPickingFacade.class);
		String comment = commentField.getText();
		
		long id = key.get("id");
		context.getExecutor().run(() -> {
			facade.createOrders(id, true, useSingleOrderService, useStratOrderService, prio, destinationName, setProcessable, userName, comment);
		})
		.thenAcceptAsync(x -> flow.executeCommand(Flow.REFRESH_ACTION), Platform::runLater);
	}

	private void treatOrder(Object source, Flow flow, ViewContext context, TableKey key) {
		LOSCustomerOrderQueryRemote query = context.getBean(LOSCustomerOrderQueryRemote.class);
		TreatOrderController c = new TreatOrderController(getBeanInfo(), this::getConverter);
		context.autoInjectBean(c);
		
		c.getExecutor().apply(query::queryById, key.get("id")).thenSetUI(c.dataProperty());
		
		FlowUtils.showNext(flow, context, TreatOrderController.class, c);
	}

	private void overview(Object source, Flow flow, ViewContext context) {
		OrderStatusPane pane = new OrderStatusPane();
		context.autoInjectBean(pane);
		FlowUtils.showNext(flow, context, OrderStatusPane.class, pane);
	}

	private void createOrder(Object source, Flow flow, ViewContext context) {
		LOSOrderFacade facade = context.getBean(LOSOrderFacade.class);
		LOSCustomerOrder order = context.getExecutor().executeAndWait(context.getRootNode(), p -> {
			return facade.order(null, null, new OrderPositionTO[0], null, null, null, null, null, Prio.NORMAL, false, false, null);
		});
		
		MyWMSEditor<?> editor = getEditor(context, CRUDKeyUtils.createKey(order));
		FlowUtils.showNext(flow, context, MyWMSEditor.class, editor);
	}
	
	@Override
	protected MyWMSEditor<LOSCustomerOrder> getEditor(ContextBase context, TableKey key) {
		Long id = key.get("id");
		if (id == null) {
			log.log(Level.INFO, "id is null so cannot generate controller");
			return null;
		}

		CustomerOrderController editor = new CustomerOrderController(getBeanInfo(), this::getConverter);
		context.autoInjectBean(editor);
		initialiseEditController(context, key, editor);
			
		return editor;
	}
	
	@Override
	public Flow createNewFlow(ApplicationContext context) {
		Flow flow = super.createNewFlow(context);
		flow
		.global()
			.action(Action.Overview, this::overview)
			.action(Flow.CREATE_ACTION, this::createOrder)
		.end()
		.globalWithSelection()
			.withSelection(Flow.DELETE_ACTION, this::removeOrder)
			.withSelection(Action.FinishOrder, this::finishOrder)
			.withSelection(Action.FinishPicking, this::finishPicking)
			.withSelection(Action.Start, this::startOrder)
			.withSelection(Action.TreatOrder, this::treatOrder)
		.end()
		.with(OrderStatusPane.class)
			.withSelection(Flow.EDIT_ACTION, this::getEditor)
			.alias(Flow.TABLE_SELECT_ACTION, Flow.EDIT_ACTION)
		.end();
		return flow;
	}
	
	/**
	 * This is the flow action that is used to move to the editing state.
	 * 
	 * @param source the source state for this transation
	 * @param flow the control flow
	 * @param context the new context for the target state
	 * @param key selected item from the previous state.
	 */
	public void getEditor(OrderStatusPane source, Flow flow, ViewContext context, TableKey key) {
		MyWMSEditor<LOSCustomerOrder> controller = getEditor(context, key);
		context.setActiveBean(MyWMSEditor.class, controller);
		flow.next(context);
	}

	
	@Override
	protected void configureCommands(RootCommand command) {
		super.configureCommands(command);
		command.begin(RootCommand.MENU)
			.add(AC.id(Action.TreatOrder).text("Treat Order"))
		.end();
	}
		
	@Override
	protected BODTOTable<LOSCustomerOrder> getTable(ViewContextBase context) {
		BODTOTable<LOSCustomerOrder> t = super.getTable(context);
		t.getCommands()
			.delete(ObservableConstant.TRUE, ObservableConstant.of(MyWMSUserPermissions.isAtLeastForeman()))
			.menu(RootCommand.MENU)
			.add(AC.id(Action.Start).text("Start"))
			.add(AC.id(Action.FinishPicking).text("Finish Picking"))
			.add(AC.id(Action.FinishOrder).text("Finish Order"))
			.end()
			.add(AC.id(Action.Overview).text("Overview"))
		.end();
		GoodsOutUtils.addOpenFilter(t, () -> refresh(t, t.getContext()));
		return t;
	}
	
}
