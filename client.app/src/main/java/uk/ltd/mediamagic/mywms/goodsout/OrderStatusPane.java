package uk.ltd.mediamagic.mywms.goodsout;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

import javax.annotation.PostConstruct;

import org.mywms.model.BasicEntity;

import de.linogistix.los.inventory.model.LOSCustomerOrder;
import de.linogistix.los.inventory.model.LOSGoodsOutRequestPosition;
import de.linogistix.los.inventory.model.LOSPickingOrder;
import de.linogistix.los.inventory.model.LOSPickingPosition;
import de.linogistix.los.inventory.model.LOSPickingUnitLoad;
import de.linogistix.los.inventory.query.LOSCustomerOrderQueryRemote;
import de.linogistix.los.inventory.query.LOSGoodsOutRequestPositionQueryRemote;
import de.linogistix.los.inventory.query.LOSPickingOrderQueryRemote;
import de.linogistix.los.inventory.query.LOSPickingPositionQueryRemote;
import de.linogistix.los.inventory.query.LOSPickingUnitLoadQueryRemote;
import de.linogistix.los.inventory.query.dto.LOSCustomerOrderTO;
import de.linogistix.los.inventory.query.dto.LOSGoodsOutPositionTO;
import de.linogistix.los.model.State;
import de.linogistix.los.query.BODTO;
import de.linogistix.los.query.LOSResultList;
import de.linogistix.los.query.QueryDetail;
import de.linogistix.los.query.TemplateQuery;
import de.linogistix.los.query.TemplateQueryFilter;
import de.linogistix.los.query.TemplateQueryWhereToken;
import javafx.application.Platform;
import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.LongBinding;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.property.ListProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.transformation.FilteredList;
import javafx.scene.Node;
import javafx.scene.control.ListView;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.util.converter.LocalDateStringConverter;
import res.R;
import uk.ltd.mediamagic.annot.Worker;
import uk.ltd.mediamagic.common.utils.Strings;
import uk.ltd.mediamagic.fx.action.AC;
import uk.ltd.mediamagic.fx.binding.MBindings;
import uk.ltd.mediamagic.fx.concurrent.MExecutor;
import uk.ltd.mediamagic.fx.control.DelayedChangeListener;
import uk.ltd.mediamagic.fx.control.ProgressIndicatorBar;
import uk.ltd.mediamagic.fx.control.SimpleFormBuilder;
import uk.ltd.mediamagic.fx.control.TableKeySelectable;
import uk.ltd.mediamagic.fx.controller.ControllerCommandBase;
import uk.ltd.mediamagic.fx.controller.SelectionComposite;
import uk.ltd.mediamagic.fx.controller.list.CellWrappers;
import uk.ltd.mediamagic.fx.data.TableKey;
import uk.ltd.mediamagic.fx.flow.AutoInject;
import uk.ltd.mediamagic.fx.flow.FlowBase;
import uk.ltd.mediamagic.fx.flow.actions.Command;
import uk.ltd.mediamagic.mywms.common.Editor;
import uk.ltd.mediamagic.mywms.common.LOSResultListProperty;
import uk.ltd.mediamagic.mywms.common.ResultSetPager;
import uk.ltd.mediamagic.plugin.PluginRegistry;
import uk.ltd.mediamagic.util.DateUtils;

public class OrderStatusPane extends ControllerCommandBase implements TableKeySelectable {
	public @AutoInject LOSCustomerOrderQueryRemote orderQuery;
	
	private final LOSResultListProperty<BODTO<LOSCustomerOrder>> data = new LOSResultListProperty<>();
	private final SelectionComposite<BODTO<LOSCustomerOrder>> selection = new SelectionComposite<>();
	
	private final @Command ListView<BODTO<LOSCustomerOrder>> processable = new ListView<>();
	private final @Command ListView<BODTO<LOSCustomerOrder>> picking = new ListView<>();
	private final @Command ListView<BODTO<LOSCustomerOrder>> picked = new ListView<>();
	private final DetailsPane detail = new DetailsPane();
	
	private final BorderPane view = new BorderPane();
	private final GridPane hbox = new GridPane();
	private final DelayedChangeListener<BODTO<LOSCustomerOrder>> selectionChanged = new DelayedChangeListener<>(this::selectionChanged);
	private final ResultSetPager pager = new ResultSetPager();
	
	public OrderStatusPane() {
		super();
		view.setCenter(hbox);
		view.setBottom(detail);
		
		getCommands()
			.back()
			.add(AC.id(FlowBase.REFRESH_ACTION).icon(R.icons.refresh())
					.description("Refresh data")
					.action(s -> loadData()))
			.add(AC.node(pager))
		.end();
		
		pager.setPageSize(200);
		pager.startIndexProperty().bind(data.startIndexProperty());
		pager.resultSizeProperty().bind(data.resultSizeProperty());
				
		hbox.getChildren().addAll(titled("Waiting", 0, processable), titled("Picking", 1, picking), titled("Picked", 2, picked));

		@SuppressWarnings("unchecked")
		Editor<LOSCustomerOrder> editor = PluginRegistry.getPlugin(LOSCustomerOrder.class, Editor.class);
		
		processable.setCellFactory(CellWrappers.forList(editor.createTOCellFactory()));
		picking.setCellFactory(CellWrappers.forList(editor.createTOCellFactory()));
		picked.setCellFactory(CellWrappers.forList(editor.createTOCellFactory()));
		
		Predicate<BODTO<LOSCustomerOrder>> processablePred = OrderStatusPane::isProcessable;
		Predicate<BODTO<LOSCustomerOrder>> pickedPred = OrderStatusPane::isPicked;
		
		processable.setItems(data.wrappedListProperty().filtered(processablePred));
		picked.setItems(data.wrappedListProperty().filtered(pickedPred));
		picking.setItems(data.wrappedListProperty().filtered(processablePred.or(pickedPred).negate()));
				
		selection.add(processable);
		selection.add(picked);
		selection.add(picking);				
	
		FlowBase.setCommand(processable, FlowBase.TABLE_SELECT_ACTION);
		FlowBase.setCommand(picked, FlowBase.TABLE_SELECT_ACTION);
		FlowBase.setCommand(picking, FlowBase.TABLE_SELECT_ACTION);
	}
	
	@PostConstruct
	public void post() {
		getContext().autoInjectBean(detail);
		selection.selectedItemProperty().addListener(selectionChanged);
		loadData();
	}
	
	public void loadData() {
		TemplateQuery template = new TemplateQuery();
		TemplateQueryFilter filter = template.addNewFilter();
		filter.addWhereToken(new TemplateQueryWhereToken(TemplateQueryWhereToken.OPERATOR_SMALLER, "state", State.FINISHED));

		QueryDetail detail = new QueryDetail(pager.getStartIndex(), pager.getPageSize());

		setData(null);

		template.setBoClass(LOSCustomerOrder.class);
		getExecutor().call(() -> orderQuery.queryByTemplateHandles(detail,template))
			.thenAcceptAsync(this::setData, Platform::runLater);			
	}
	
	public void selectionChanged(BODTO<LOSCustomerOrder> to) {
		detail.orderTO.setValue((LOSCustomerOrderTO) to);
	}

	private Node titled(String title, int col, Node content) {
		TitledPane t = new TitledPane(title, content);
		t.setCollapsible(false);
		t.setMaxHeight(Double.MAX_VALUE);
		GridPane.setColumnIndex(t, col);
		GridPane.setFillHeight(t, true);
		GridPane.setFillWidth(t, true);
		GridPane.setHgrow(t, Priority.ALWAYS);
		GridPane.setVgrow(t, Priority.ALWAYS);
		return t;
	}
	
	@Override
	public Node getView() {
		return view;
	}
		
	@Override
	public TableKey getSelectedKey() {
		BODTO<LOSCustomerOrder> d = selection.getSelectedItem();
		return (d == null) ? null : new TableKey("id", d.getId());
	}
	
	@Override
	public Collection<TableKey> getSelectedKeys() {
		return Collections.singleton(getSelectedKey());
	}
	
	public static final boolean isProcessable(BODTO<LOSCustomerOrder> d) {
		LOSCustomerOrderTO to = (LOSCustomerOrderTO) d;
		return to.getState() < State.STARTED;
	}

	public static final boolean isPicked(BODTO<LOSCustomerOrder> d) {
		LOSCustomerOrderTO to = (LOSCustomerOrderTO) d;
		return to.getState() >= State.PICKED;
	}

	public final LOSResultListProperty<BODTO<LOSCustomerOrder>> dataProperty() {
		return this.data;
	}
	

	public final LOSResultList<BODTO<LOSCustomerOrder>> getData() {
		return this.dataProperty().get();
	}
	

	public final  void setData(final LOSResultList<BODTO<LOSCustomerOrder>> data) {
		this.dataProperty().set(data);
	}
	
	static class DetailsPane extends BorderPane {
		@AutoInject public LOSCustomerOrderQueryRemote orderQuery;
		@AutoInject public LOSPickingOrderQueryRemote pickQuery;
		@AutoInject public LOSPickingPositionQueryRemote pickItemsQuery;
		@AutoInject public LOSPickingUnitLoadQueryRemote pickUnitLoadQuery;
		@AutoInject public LOSGoodsOutRequestPositionQueryRemote goodsOutQuery;
		@AutoInject public MExecutor exec;
		
		final private ObjectProperty<LOSCustomerOrderTO> orderTO = new SimpleObjectProperty<>();
		
		final private ObjectProperty<LOSCustomerOrder> order = new SimpleObjectProperty<>();
		final private ListProperty<BODTO<LOSPickingOrder>> pick = new SimpleListProperty<>();
		final private ListProperty<LOSPickingPosition> pickItems = new SimpleListProperty<>();
		final private ListProperty<BODTO<LOSPickingUnitLoad>> unitLoads = new SimpleListProperty<>();
		final private ListProperty<BODTO<LOSGoodsOutRequestPosition>> goodsOut = new SimpleListProperty<>();
		
		final private FilteredList<LOSPickingPosition> pickedItems = pickItems.filtered(p -> p.getState() >= State.PICKED);
		final private ObjectBinding<LOSPickingPosition> lastPick = Bindings.createObjectBinding(this::lastPick, pickedItems);
		final private ObjectBinding<Duration> pickTime = Bindings.createObjectBinding(this::pickTime, pickedItems);
		final private LongBinding pickedCount = Bindings.createLongBinding(this::pickedCount, pickedItems);

		final private ObjectBinding<Duration> minsPerPick = Bindings.createObjectBinding(this::minutesPerPick, pickTime, pickedCount);
		final private ObjectBinding<Duration> timeToGo = Bindings.createObjectBinding(this::timeToGo, minsPerPick, pickedCount);

		final private ListView<BODTO<LOSPickingOrder>> picksView = new ListView<>();
		final private ListView<BODTO<LOSPickingUnitLoad>> unitLoadsView = new ListView<>();
		final private ListView<BODTO<LOSGoodsOutRequestPosition>> goodsOutView = new ListView<>();
		
		@SuppressWarnings("unchecked")
		public DetailsPane() {
			ProgressIndicatorBar progress = new ProgressIndicatorBar();
			progress.progressProperty().bind(MBindings.get(order, this::getOrderProgress));
			
			picksView.setItems(pick);
			unitLoadsView.setItems(unitLoads);
			goodsOutView.setItems(goodsOut);
			
			unitLoadsView.setCellFactory(
					CellWrappers.forList(PluginRegistry.getPlugin(LOSPickingUnitLoad.class, Editor.class).createTOCellFactory()));
			picksView.setCellFactory(
					CellWrappers.forList(PluginRegistry.getPlugin(LOSPickingOrder.class, Editor.class).createTOCellFactory()));
			goodsOutView.setCellFactory(
					CellWrappers.forList(PluginRegistry.getPlugin(LOSGoodsOutRequestPosition.class, Editor.class).createTOCellFactory()));
			
			TitledPane picksTP = new TitledPane("Picks", picksView);
			TitledPane unitloadsTP = new TitledPane("Unit loads", unitLoadsView);
			TitledPane goodsOutTP = new TitledPane("Goods out positions", goodsOutView);

			Bindings.bindBidirectional(picksTP.expandedProperty(), unitloadsTP.expandedProperty());
			Bindings.bindBidirectional(picksTP.expandedProperty(), goodsOutTP.expandedProperty());
			
			SimpleFormBuilder form = new SimpleFormBuilder();
			form.row()
			.label("Number")
			.field(MBindings.get(order, LOSCustomerOrder::getNumber))
			.label("Pick Time")
			.field(Bindings.createStringBinding(this::pickTimeFormat, pickTime, timeToGo))
			.label("Last Pick")
			.field(MBindings.get(lastPick, LOSPickingPosition::getPickFromLocationName))
		.end();
			form.row()
			.label("External Number")
			.field(MBindings.get(order, LOSCustomerOrder::getExternalNumber))
			.label("Mins/Pick")
			.field(Bindings.createStringBinding(this::pickRateFormat, minsPerPick))
			.label("Destination")
			.field(Bindings.selectString(order, "destination", "name"))
		.end();
			form.row()
			.label("Progress")
			.fieldNode(progress)
			.label("To pick")
			.field(Bindings.createStringBinding(this::pickQtyFormat, pickedCount))
			.label("Delivery")
			.boundField(new LocalDateStringConverter(), MBindings.get(order, o -> DateUtils.toLocalDate(o.getDelivery())))
		.end();
		form.row()
		.fieldNode(picksTP).colSpan(2)
		.fieldNode(unitloadsTP).colSpan(2)
		.fieldNode(goodsOutTP).colSpan(2)
		.end();
			
			setCenter(form);
			
			orderTO.addListener(this::selectionChanged);
		}
		
		@PostConstruct
		public void post() {
			
		}

		public String pickTimeFormat() {
			Duration pt = pickTime.get();
			Duration ttg = timeToGo.get();
			if (pt == null) pt = Duration.ZERO;
			if (ttg == null) ttg = Duration.ZERO;
			return String.format("%02d:%02d + %02d:%02d", 
					pt.toMinutes(), pt.minusMinutes(pt.toMinutes()).getSeconds(),
					ttg.toMinutes(), ttg.minusMinutes(ttg.toMinutes()).getSeconds()
					);
		}

		public String pickRateFormat() {
			Duration pt = minsPerPick.get();
			if (pt == null) pt = Duration.ZERO;
			return String.format("%02d:%02d", 
					pt.toMinutes(), pt.minusMinutes(pt.toMinutes()).getSeconds()
					);
		}

		public String pickQtyFormat() {
			return Strings.format("{0} of {1}", 
					pickedCount.get(), pickItems.size()
					);
		}
		
		public LOSPickingPosition lastPick() {
			return pickItems.stream()
					.reduce((a,b) -> (a.getModified().getTime() >= b.getModified().getTime()) ? a : b)
					.orElse(null);
		}
		
		public Duration minutesPerPick() {
			Duration pt = pickTime.get();
			long pc = pickedCount.get();
			if (pt == null) return null;
			if (pt.isZero()) return null;
			if (pc == 0) return null;
			return pt.dividedBy(pickedCount.get());
		}

		public Duration timeToGo() {
			long picksRemaining = pickItems.size() - pickedCount.get();
			if (picksRemaining <= 0) return Duration.ZERO; 
			Duration pt = minsPerPick.get();
			if (pt == null) return null;
			return pt.multipliedBy(picksRemaining);
		}

		public Duration pickTime() {
			LocalDateTime min = pickItems.stream()
					.map(BasicEntity::getModified)
					.reduce((a,b) -> (a.getTime() >= b.getTime()) ? a : b)
					.map(DateUtils::toLocalDateTime)
					.orElse(null);
			LocalDateTime max = pickItems.stream()
					.map(BasicEntity::getModified)
					.reduce((a,b) -> (a.getTime() <= b.getTime()) ? a : b)
					.map(DateUtils::toLocalDateTime)
					.orElse(null);
						
			if ((max != null) && (min != null)) {
				return Duration.between(max, min);
			}
			else {
				return Duration.ZERO;
			}
		}

		public long pickedCount() {
			return pickedItems.size();
		}
		
		protected double getOrderProgress(LOSCustomerOrder o) {
			if (o.getState() >= State.PICKED) return 1;
			int total = o.getPositions().size();
			long picked = o.getPositions().stream().filter(p -> p.getState() >= State.PICKED).count();
			return ((double) picked) / total;
		}
		
		@Worker
		private TemplateQuery getPicks(String orderNumber) {
			Objects.requireNonNull(orderNumber);
			TemplateQuery template = new TemplateQuery();
			template.setBoClass(LOSPickingOrder.class);
			template.addWhereToken(new TemplateQueryWhereToken(
					TemplateQueryWhereToken.OPERATOR_EQUAL, "customerOrderNumber", orderNumber));
			return template;
		}
		
		@Worker
		private List<BODTO<LOSPickingUnitLoad>> getUnitLoads(String orderNumber) throws Exception {			
			TemplateQuery template = new TemplateQuery();
			template.setBoClass(LOSPickingUnitLoad.class);
			template.addWhereToken(new TemplateQueryWhereToken(
					TemplateQueryWhereToken.OPERATOR_EQUAL, "customerOrderNumber", orderNumber));
			List<BODTO<LOSPickingUnitLoad>> loads = pickUnitLoadQuery.queryByTemplateHandles(new QueryDetail(0, Integer.MAX_VALUE), template);
			loads.sort(Comparator.comparing(e -> e.getName() ));			
			return loads;
		}

		@Worker
		private List<BODTO<LOSGoodsOutRequestPosition>> getGoodsOutPositions(String orderNumber) throws Exception {			
			TemplateQuery template = new TemplateQuery();
			template.setBoClass(LOSGoodsOutRequestPosition.class);
			template.addWhereToken(new TemplateQueryWhereToken(
					TemplateQueryWhereToken.OPERATOR_EQUAL, "goodsOutRequest.customerOrder.number", orderNumber));
			List<BODTO<LOSGoodsOutRequestPosition>> loads = goodsOutQuery.queryByTemplateHandles(new QueryDetail(0, Integer.MAX_VALUE), template);
			
			loads.sort(Comparator.comparing(e -> ((LOSGoodsOutPositionTO)e).getUnitLoadLabel()));			
			
			return loads;
		}

		@Worker
		private List<LOSPickingPosition> getPickPositions(String orderNumber) throws Exception {			
			TemplateQuery template = new TemplateQuery();
			template.setBoClass(LOSPickingPosition.class);
			template.addWhereToken(new TemplateQueryWhereToken(
					TemplateQueryWhereToken.OPERATOR_EQUAL, "pickingOrder.customerOrderNumber", orderNumber));
			List<LOSPickingPosition> picks = pickItemsQuery.queryByTemplate(new QueryDetail(0, Integer.MAX_VALUE, "modified", false), template);			
			return picks;
		}

		public void selectionChanged(Observable o, LOSCustomerOrderTO oldID, LOSCustomerOrderTO newID) {
			if (newID == null) {
				order.set(null);
			}
			else {
				exec.call(() -> orderQuery.queryById(newID.getId())).thenAcceptUI(order::set);
				exec.call(
						() -> pickQuery.queryByTemplateHandles(new QueryDetail(0, Integer.MAX_VALUE), getPicks(newID.getNumber())))
					.thenAcceptUI(l -> pick.set(FXCollections.observableList(l)));
				exec.call(() -> getPickPositions(newID.getNumber())) 
					.thenAcceptUI(l -> pickItems.set(FXCollections.observableList(l)));
				exec.call(() -> getUnitLoads(newID.getNumber())) 
					.thenAcceptUI(l -> unitLoads.set(FXCollections.observableList(l)));
				exec.call(() -> getGoodsOutPositions(newID.getNumber())) 
					.thenAcceptUI(l -> goodsOut.set(FXCollections.observableList(l)));
			}
		}
	}
}
