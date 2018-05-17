package uk.ltd.mediamagic.mywms.goodsout;

import java.beans.BeanInfo;
import java.beans.PropertyDescriptor;
import java.math.BigDecimal;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.function.Supplier;

import javax.annotation.PostConstruct;

import org.mywms.model.Client;
import org.mywms.model.ItemData;
import org.mywms.model.Lot;

import de.linogistix.los.inventory.model.LOSCustomerOrder;
import de.linogistix.los.inventory.model.LOSPickingOrder;
import de.linogistix.los.inventory.model.LOSPickingPosition;
import de.linogistix.los.inventory.pick.facade.CreatePickRequestPositionTO;
import de.linogistix.los.inventory.query.LotQueryRemote;
import de.linogistix.los.inventory.query.dto.ItemDataTO;
import de.linogistix.los.inventory.query.dto.LOSCustomerOrderPositionTO;
import de.linogistix.los.inventory.query.dto.LOSOrderStockUnitTO;
import de.linogistix.los.location.model.LOSStorageLocation;
import de.linogistix.los.location.query.LOSStorageLocationQueryRemote;
import de.linogistix.los.model.State;
import de.linogistix.los.query.BODTO;
import de.linogistix.los.query.QueryDetail;
import javafx.application.Platform;
import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.LongProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.collections.FXCollections;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextFormatter;
import javafx.util.StringConverter;
import res.R;
import uk.ltd.mediamagic.common.utils.Strings;
import uk.ltd.mediamagic.flow.crud.BasicEntityEditor;
import uk.ltd.mediamagic.flow.crud.CRUDKeyUtils;
import uk.ltd.mediamagic.fx.MDialogs;
import uk.ltd.mediamagic.fx.action.AC;
import uk.ltd.mediamagic.fx.binding.BigDecimalBinding;
import uk.ltd.mediamagic.fx.binding.MBindings;
import uk.ltd.mediamagic.fx.control.CommandLink;
import uk.ltd.mediamagic.fx.controller.list.CellRenderer;
import uk.ltd.mediamagic.fx.controller.list.CellWrappers;
import uk.ltd.mediamagic.fx.controller.list.MaterialCells;
import uk.ltd.mediamagic.fx.converters.BigDecimalConverter;
import uk.ltd.mediamagic.fx.converters.DateTimeConverter;
import uk.ltd.mediamagic.fx.converters.Filters;
import uk.ltd.mediamagic.fx.converters.ToStringConverter;
import uk.ltd.mediamagic.fx.data.TableKey;
import uk.ltd.mediamagic.fx.flow.AutoInject;
import uk.ltd.mediamagic.fx.flow.FXErrors;
import uk.ltd.mediamagic.fx.flow.FXMLController;
import uk.ltd.mediamagic.fx.flow.Flow;
import uk.ltd.mediamagic.fx.table.MTableViewBase;
import uk.ltd.mediamagic.mywms.FlowUtils;
import uk.ltd.mediamagic.mywms.common.Editor;
import uk.ltd.mediamagic.mywms.goodsout.TreatOrderModel.TreatOrderPosition;
import uk.ltd.mediamagic.plugin.PluginRegistry;
import uk.ltd.mediamagic.util.Closures;
@FXMLController("TreadOrder.fxml")
public class TreatOrderController extends GoodsOutEditController<LOSCustomerOrder> implements Initializable {

	@AutoInject public LotQueryRemote lotQuery;
	@AutoInject public LOSStorageLocationQueryRemote locationQuery;
	
	private @FXML TreatOrderModel model = new TreatOrderModel(dataProperty());
	
	private @FXML ListView<TreatOrderPosition> orderPositions;
	private @FXML ComboBox<LOSPickingOrder> pickingOrders;
	private @FXML ListView<LOSPickingPosition> pickingPositionsForOrderPos;
	private @FXML ListView<LOSPickingPosition> pickingPositionsForPick;
	
	private @FXML MTableViewBase<LOSOrderStockUnitTO> stockUnits;

	private @FXML BasicEntityEditor<ItemData> itemData;
	private @FXML BasicEntityEditor<Lot> lot;

	private @FXML BasicEntityEditor<LOSStorageLocation> field_destination;
	private @FXML ComboBox<Integer> field_prio;
	
	private @FXML Button loadStocks;
	private @FXML Button createNewPick;
	private @FXML Button assignPicks;
	private @FXML Button assignPartialPicks;
	private @FXML Button removePicks;
	
	private LongProperty defaultPickOrder = new SimpleLongProperty();
	private BooleanProperty stockUnitsLoading = new SimpleBooleanProperty();
	
	public TreatOrderController(BeanInfo beanInfo, Function<PropertyDescriptor, StringConverter<?>> getConveryer) {
		super(beanInfo, getConveryer);
		
		getCommands()
			.add(AC.idText("Start Picking").action(e -> {
				if (!MDialogs.create(getView(), "Start Picking")
					.message("Release all picking orders?")
					.showYesNo()) return;
				
				if (model != null) {
					model.startPicking(getView());
					getData().setState(State.PROCESSABLE);
					FlowUtils.executeCommand(getContext(), Flow.SAVE_ACTION);
				}
			}))
		.end();
	}
	
	@PostConstruct
	public void post() {		
		getContext().autoInjectBean(model);
	}
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		field_destination.configure(getContext(), LOSStorageLocation.class);
		field_destination.setFetchCompleteions(s -> this.getStorageLocations(s));
		
		itemData.configure(getContext(), ItemData.class);
		lot.configure(getContext(), Lot.class);

		orderPositions.setCellFactory(CellWrappers.forList(this.createCellFactory()));
		orderPositions.setItems(model.orderPositionsProperty());
		
		stockUnits.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		stockUnits.setColumns(
				stockUnits.column().title("Stock Unit").valueFactory(LOSOrderStockUnitTO::getName).show(),
				stockUnits.column().title("Lot").valueFactory(LOSOrderStockUnitTO::getLot).show(),
				stockUnits.column(DateTimeConverter.forDate()).title("Not use before").valueFactory(Closures.nullsTo(o -> o.getLotEntity().getUseNotBefore(), null)).hide(),
				stockUnits.column(DateTimeConverter.forDate()).title("Best Before").valueFactory(Closures.nullsTo(o -> o.getLotEntity().getBestBeforeEnd(), null)).hide(),
				stockUnits.column().title("Unit Load").valueFactory(LOSOrderStockUnitTO::getUnitLoad).show(),
				stockUnits.column().title("Location").valueFactory(LOSOrderStockUnitTO::getStorageLocation).show(),
				stockUnits.column(new BigDecimalConverter()).title("Amount Available").valueFactory(LOSOrderStockUnitTO::getAvailableAmount).show()
				);
		
		pickingOrders.setConverter(ToStringConverter.of(LOSPickingOrder::getNumber));
		pickingOrders.setItems(model.pickingOrdersProperty());
		model.pickingOrdersProperty().addListener((v,o,n) -> setSelectedPick());
		defaultPickOrder.addListener((v,o,n) -> setSelectedPick());


		@SuppressWarnings("unchecked")
		Editor<LOSPickingPosition> pickingPositionEditor = PluginRegistry.getPlugin(LOSPickingPosition.class, Editor.class);
		pickingPositionsForOrderPos.setCellFactory(CellWrappers.forList(pickingPositionEditor.createCellFactory()));
		pickingPositionsForPick.setCellFactory(CellWrappers.forList(pickingPositionEditor.createCellFactory()));
		pickingPositionsForOrderPos.setItems(model.getMatchingPicks(orderPositions.getSelectionModel().selectedItemProperty()));
		pickingPositionsForPick.setItems(model.getPicklistPositions(pickingOrders.valueProperty()));
	
		createNewPick.setOnAction(e -> {
			model.createNewPickingOrder(e, getContext())
			.thenAcceptAsync(defaultPickOrder::set, Platform::runLater);
		});
		createNewPick.disableProperty().bind(model.readonlyProperty());

		assignPicks.setOnAction(this::assignStockUnits);
		assignPicks.disableProperty().bind(model.readonlyProperty());
		assignPartialPicks.setOnAction(this::assignPartialStockUnits);
		assignPartialPicks.disableProperty().bind(model.readonlyProperty());

		removePicks.setOnAction(this::removePickPositions);
		removePicks.disableProperty().bind(model.readonlyProperty());

		loadStocks.setOnAction(this::loadStocks);
		
		itemData.setDisable(true);
		itemData.valueProperty().addListener(o -> onItemDataChanged());
		itemData.valueProperty().bind(MBindings.get(
				orderPositions.getSelectionModel().selectedItemProperty(), 
				TreatOrderPosition::getItemData));

		orderPositions.getSelectionModel().selectedItemProperty().addListener(this::onOrderItemChanged);
		lot.setFetchCompleteions(s -> {
			if (this.itemData.getValue() == null) {
				return CompletableFuture.completedFuture(Collections.emptyList());
			}
			else {
				QueryDetail qd = new QueryDetail(0, 100);
				qd.addOrderByToken("modified", false);
				
				ItemDataTO itemData = new ItemDataTO(this.itemData.getValue());
				BODTO<Client> client = new BODTO<>(model.getOrder().getClient());
				return getExecutor().call(() -> lotQuery.autoCompletionByClientAndItemData(s, client, itemData));
			}
		});
		
		lot.valueProperty().addListener(o -> onItemDataChanged());
		lot.setClearButtonVisible(true);
		
		stockUnits.placeholderProperty().bind(Bindings.when(stockUnitsLoading)
				.then((Node) new ProgressIndicator())
				.otherwise(new Label("No stock")));
	}
	
	public void setSelectedPick() {
		List<LOSPickingOrder> picks = pickingOrders.getItems();
		if ((picks == null) || picks.isEmpty()) return;
		Long defaultPick = defaultPickOrder.getValue();
		if (defaultPick == null) {
			pickingOrders.setValue(picks.get(0));			
		}
		else {
			LOSPickingOrder sel = picks.stream()
					.filter(p -> Objects.equals(p.getId(),defaultPick))
					.findFirst().orElse(picks.get(0));
			pickingOrders.setValue(sel);			
		}
	}
	
	public void onItemDataChanged() {
		stockUnits.setItems(FXCollections.emptyObservableList());
		loadStocks.setDisable(false);
	}

	public void onOrderItemChanged(Observable v, TreatOrderPosition o,  TreatOrderPosition n) {
		lot.setValue(n.getLot());
	}
	
	public void loadStocks(Event e) {
		loadStocks.setDisable(true);
		TreatOrderPosition p = orderPositions.getSelectionModel().getSelectedItem();
		Lot lot = this.lot.getValue();
		if (p == null) return;
		stockUnitsLoading.set(true);
		model.getStocks(p.getCustomerOrderPosition(), lot)
			.thenAcceptAsync(stockUnits.itemsProperty()::set, Platform::runLater)
			.whenComplete((v,ex) -> stockUnitsLoading.set(false));
	}
	
	public void assignStockUnits(Event e) {
		assignStockUnits(e, null);
	}

	BigDecimal lastMax = BigDecimal.ZERO;
	public void assignPartialStockUnits(Event e) {
		MDialogs.showFormattedInput(getView(), "Maximum pick amount", "Max Pick Qty", 
				new TextFormatter<BigDecimal>(new BigDecimalConverter(), lastMax, Filters.numeric()))
		.filter(max -> max.compareTo(BigDecimal.ZERO) > 0)
		.ifPresent(max -> {
			lastMax = max;
			assignStockUnits(e, max);
		});
	}

	public void assignStockUnits(Event e, BigDecimal maxAssignment) {
		LOSPickingOrder pickingOrder = pickingOrders.getValue();
		TreatOrderPosition orderPosition = orderPositions.getSelectionModel().getSelectedItem();
		if (pickingOrder == null) {
			FXErrors.selectionError(getView(), "Please select a picking order.");
			return;
		}

		if (orderPosition == null) {
			FXErrors.selectionError(getView(), "Please select a order position.");
			return;
		}

		List<LOSOrderStockUnitTO> selection = stockUnits.getSelectionModel().getSelectedItems();
		BigDecimal total = selection.stream().map(LOSOrderStockUnitTO::getAvailableAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
		BigDecimal amountRemaining;
		if (maxAssignment == null) { 
			amountRemaining = orderPosition.getAmountRemaining() ;
		}
		else {
			amountRemaining = maxAssignment.min(orderPosition.getAmountRemaining()) ;			
		}
		
		boolean breakStockUnit;
		if (total.compareTo(amountRemaining) >= 0) {
    	List<CommandLink<Boolean>> links = new ArrayList<>();
    	links.add(new CommandLink<>(true, "Break stock unit", 
    			Strings.format("Pick only {0} and leave an opened stock unit.", amountRemaining), 
    			R.svgPaths.openBox()));
    	
    	links.add(new CommandLink<>(false, "Use the full stock unit", 
    			Strings.format("Use the full stock unit and pick {0}", total), 
    			R.svgPaths.box()));			

    	links.add(new CommandLink<>(false, "Cancel", 
    			Strings.format("Do not pick any stock units"), 
    			R.svgPaths.box()));			

			Optional<Boolean> l = MDialogs.create(getView(), "Assign picks")
				.masthead("Would you to break the stock unit?")
				.showLinks(links);
			if (!l.isPresent()) return;
			breakStockUnit = l.get();
		}
		else {
			breakStockUnit = false;
		}
		
		BigDecimal remainingToPick = amountRemaining;
		List<CreatePickRequestPositionTO> picks = new ArrayList<>();			
		collectPicks: for (LOSOrderStockUnitTO p: selection) {
			CreatePickRequestPositionTO to = new CreatePickRequestPositionTO();
			to.amountToPick = p.getAvailableAmount();
			to.orderPosition = new LOSCustomerOrderPositionTO(orderPosition.getCustomerOrderPosition());
			to.pickRequestNumber = pickingOrder.getNumber();
			to.stock = p;
			to.targetPlace = null;

			picks.add(to);

			remainingToPick = remainingToPick.subtract(to.amountToPick);
	
			if (remainingToPick.compareTo(BigDecimal.ZERO) <= 0) {
				if (breakStockUnit) to.amountToPick = to.amountToPick.add(remainingToPick);
				break collectPicks;
			}
		}		
		
		model.assignStockUnits(picks)
			.thenRunAsync(() -> loadStocks(null), Platform::runLater);
	}

	public void removePickPositions(Event e) {
		List<LOSPickingPosition> pickPositions = pickingPositionsForOrderPos.getSelectionModel().getSelectedItems();

		if (pickPositions.isEmpty()) {
			FXErrors.selectionError(getView(), "Please select a picking position");
		}
		
		model.removePickingPositions(pickPositions)
			.thenRunAsync(() -> loadStocks(null), Platform::runLater);
	}


	public Supplier<CellRenderer<TreatOrderPosition>> createCellFactory() {
		return MaterialCells.withID(this::createPositionIcon, 
				s -> s.getItemData().getNumber(), 
				s -> Strings.format("{0}, {1}", s.getNumber(), Closures.resolve(() -> s.getLot().getName()).orElse("")),
				s -> Strings.format("{0}", s.getItemData().getName()),
				s -> {
					return Bindings.createStringBinding(() -> Strings.format("{0} of {1}", s.getAmount(), s.getAmountAssigned()),
							s.amountAssignedProperty());
				});
	}

	public Supplier<CellRenderer<LOSOrderStockUnitTO>> createStockUnitCellFactory() {
		return MaterialCells.<LOSOrderStockUnitTO>withID(s -> null,
				s -> Strings.format("Avail {0}", s.getAvailableAmount()),
				s -> Strings.format("{0} @ {1}", s.getUnitLoad(), s.getStorageLocation()), 
				null,
				s -> Strings.format("Lot {0}, {1} until {2}", s.getLot(), s.lotEntity.getUseNotBefore(), s.lotEntity.getBestBeforeEnd()));
	}

	public Node createPositionIcon(TreatOrderPosition pos) {
		ProgressIndicator pi = new ProgressIndicator();
		
		BigDecimalBinding picked = pos.amountAssignedProperty(); 
		BigDecimal amount = (pos.getAmount() == null) ? BigDecimal.ZERO : pos.getAmount();
			
		pi.progressProperty().bind(MBindings.get(picked, v -> v.doubleValue() / amount.doubleValue()));
		pi.styleProperty().bind(Bindings.when(picked.compareTo(amount).lessThanOrEqualTo(0)).then("").otherwise("-fx-accent: RED"));
		return pi;
	}
	
	@Override
	public TableKey getSelectedKey() {
		return CRUDKeyUtils.createKey(getData());
	}	
}
