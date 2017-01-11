package uk.ltd.mediamagic.mywms.goodsout;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.mywms.model.ItemData;
import org.mywms.model.Lot;

import de.linogistix.los.inventory.crud.LOSPickingOrderCRUDRemote;
import de.linogistix.los.inventory.facade.LOSCompatibilityFacade;
import de.linogistix.los.inventory.facade.LOSPickingFacade;
import de.linogistix.los.inventory.model.LOSCustomerOrder;
import de.linogistix.los.inventory.model.LOSCustomerOrderPosition;
import de.linogistix.los.inventory.model.LOSPickingOrder;
import de.linogistix.los.inventory.model.LOSPickingPosition;
import de.linogistix.los.inventory.pick.facade.CreatePickRequestPositionTO;
import de.linogistix.los.inventory.query.LOSPickingOrderQueryRemote;
import de.linogistix.los.inventory.query.LOSPickingPositionQueryRemote;
import de.linogistix.los.inventory.query.dto.LOSCustomerOrderPositionTO;
import de.linogistix.los.inventory.query.dto.LOSOrderStockUnitTO;
import de.linogistix.los.inventory.query.dto.LotTO;
import javafx.application.Platform;
import javafx.beans.Observable;
import javafx.beans.binding.ListBinding;
import javafx.beans.property.ListProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyListProperty;
import javafx.beans.property.ReadOnlyListWrapper;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.value.ObservableBooleanValue;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import uk.ltd.mediamagic.common.utils.Strings;
import uk.ltd.mediamagic.fx.binding.BigDecimalBinding;
import uk.ltd.mediamagic.fx.binding.FxCollectors;
import uk.ltd.mediamagic.fx.concurrent.MExecutor;
import uk.ltd.mediamagic.fx.flow.AutoInject;
import uk.ltd.mediamagic.fxcommon.ObservableConstant;

public class TreatOrderModel {
	private final ObjectProperty<LOSCustomerOrder> order;
	
	@AutoInject public MExecutor exec;
	@AutoInject public LOSPickingOrderQueryRemote orderQuery;
	@AutoInject public LOSPickingPositionQueryRemote pickingPositionsQuery;
	@AutoInject public LOSPickingOrderCRUDRemote pickingOrderCrud;
	@AutoInject public LOSCompatibilityFacade pickManager;
	@AutoInject public LOSPickingFacade pickingFacade;
  
	
	private ListProperty<LOSPickingPosition> pickingPositions = new SimpleListProperty<>(FXCollections.emptyObservableList());
	private ReadOnlyListWrapper<LOSPickingOrder> pickingOrders = new ReadOnlyListWrapper<>(FXCollections.emptyObservableList());
	private ReadOnlyListWrapper<TreatOrderPosition> orderPositions = new ReadOnlyListWrapper<>();
	
	private ObservableBooleanValue readonly = ObservableConstant.FALSE;
	
	public TreatOrderModel(ObjectProperty<LOSCustomerOrder> order) {
		super();
		this.order = order;
	}

	@PostConstruct
	public void post() {
		Objects.requireNonNull(exec);
		onOrderChanged(order, null, order.get());
		order.addListener(this::onOrderChanged);
	}
	
	public final ObservableBooleanValue readonlyProperty() {
		return readonly;
	}
	
	public final ObjectProperty<LOSCustomerOrder> orderProperty() {
		return this.order;
	}
	
	public final LOSCustomerOrder getOrder() {
		return this.orderProperty().get();
	}
	
	public final ListProperty<LOSPickingPosition> pickingPositionsProperty() {
		return this.pickingPositions;
	}
	

	public final ObservableList<LOSPickingPosition> getPickingPositions() {
		return this.pickingPositionsProperty().get();
	}
	

	public final  void setPickingPositions(final ObservableList<LOSPickingPosition> pickingPositions) {
		this.pickingPositionsProperty().set(pickingPositions);
	}
	
	
	public void createNewPickingOrder(Event e) {
		String orderNumber = getOrder().getNumber();
		exec.call(() -> pickingFacade.createNewPickingOrder(orderNumber, null, true))
		.thenRunAsync(() -> reloadPickingOrders(getOrder()), Platform::runLater);
	}
	
	@SuppressWarnings("deprecation")
	public CompletableFuture<ObservableList<LOSOrderStockUnitTO>> getStocks(LOSCustomerOrderPosition pos) {
		LotTO lotTo = (pos.getLot() == null) ? null : new LotTO(pos.getLot());
		return exec
				.call(() -> pickManager.querySuitableStocksByOrderPosition(new LOSCustomerOrderPositionTO(pos), lotTo, null))
				.thenApply(FXCollections::observableList);
	}

	public ListBinding<LOSPickingPosition> getMatchingPicks(ObservableValue<TreatOrderPosition> posProperty) {
		return new ListBinding<LOSPickingPosition>() {
			{ bind(posProperty); }
			@Override
			protected ObservableList<LOSPickingPosition> computeValue() {
				TreatOrderPosition pos = posProperty.getValue();
				if (pos == null) return FXCollections.emptyObservableList();
				String posNumber = pos.getNumber();
				return pickingPositions.filtered(p -> Strings.equals(p.getCustomerOrderPosition().getNumber(), posNumber));
			}
		};
	}

	public ListBinding<LOSPickingPosition> getPicklistPositions(ObservableValue<LOSPickingOrder> pickValue) {
		return new ListBinding<LOSPickingPosition>() {
			{ bind(pickValue); }
			@Override
			protected ObservableList<LOSPickingPosition> computeValue() {
				LOSPickingOrder pick = pickValue.getValue();
				if (pick == null) return FXCollections.emptyObservableList();
				String posNumber = pick.getNumber();
				return pickingPositions.filtered(p -> Strings.equals(p.getPickingOrderNumber(), posNumber));
			}
		};
	}

	public void onOrderChanged(Observable o, LOSCustomerOrder oldOrder, LOSCustomerOrder newOrder) {
		if (newOrder == null) {
			this.orderPositions.set(FXCollections.emptyObservableList());
		}
		else {
			List<LOSCustomerOrderPosition> positions = newOrder.getPositions();
			this.orderPositions.set(positions.stream().map(TreatOrderPosition::new).collect(FxCollectors.toList()));			
		}		
		reloadPickingOrders(newOrder);
	}
	
	public void reloadPickingOrders(LOSCustomerOrder order) {
		if (order == null) {
			pickingOrders.set(FXCollections.emptyObservableList());
			pickingPositions.set(FXCollections.emptyObservableList());
		}
		else {
			String orderNumber = order.getNumber();		
		
			exec.apply(orderQuery::getByCustomerOrder, orderNumber)
				.thenApply(FXCollections::observableList)
				.thenAcceptAsync(pickingOrders::set, Platform::runLater);

			CompletableFuture<List<LOSPickingPosition>> result = exec.apply(pickingPositionsQuery::getByCustomerOrder, orderNumber);
			result.thenApply(FXCollections::observableList)
					.thenAcceptAsync(pickingPositions::set, Platform::runLater);
		}
	}

	public CompletableFuture<Void> assignStockUnits(List<CreatePickRequestPositionTO> selection) {
		return exec.run(() -> pickingFacade.createPickRequests(selection))
			.thenRunAsync(() -> reloadPickingOrders(getOrder()), Platform::runLater);
	}

	public CompletableFuture<Void> removePickingPositions(List<LOSPickingPosition> selection) {
		List<Long> ids = selection.stream().map(LOSPickingPosition::getId).collect(Collectors.toList());
		return exec.run(() -> pickingFacade.removePicks(ids))
			.thenRunAsync(() -> reloadPickingOrders(getOrder()), Platform::runLater);
	}

	public final ReadOnlyListProperty<TreatOrderPosition> orderPositionsProperty() {
		return this.orderPositions.getReadOnlyProperty();
	}

	public final ObservableList<TreatOrderPosition> getOrderPositions() {
		return this.orderPositionsProperty().get();
	}

	public final ReadOnlyListProperty<LOSPickingOrder> pickingOrdersProperty() {
		return this.pickingOrders.getReadOnlyProperty();
	}
	

	public final ObservableList<LOSPickingOrder> getPickingOrders() {
		return this.pickingOrdersProperty().get();
	}	
	
	class TreatOrderPosition {
		private final LOSCustomerOrderPosition position;
	  private final BigDecimalBinding amountAssigned;
	  private final ObservableList<LOSPickingPosition> picks;
	  public TreatOrderPosition(LOSCustomerOrderPosition orderPosition) {
	  	this.position = orderPosition;

	  	picks = pickingPositions.filtered(p -> Strings.equals(p.getCustomerOrderPosition().getNumber(), position.getNumber()));
	  	
	  	amountAssigned = BigDecimalBinding.create(() -> {
	  		if (picks.isEmpty()) return BigDecimal.ZERO;
	  		return picks.stream().map(LOSPickingPosition::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
	  	}, picks);
	  }
	  
	  public Long getId() {
			return position.getId();
		}
	  
	  LOSCustomerOrderPosition getCustomerOrderPosition() {
	  	return position;
	  }

		public String getNumber() {
			return position.getNumber();
		}

		public ItemData getItemData() {
			return position.getItemData();
		}

		public BigDecimal getAmount() {
			return position.getAmount();
		}

		public Lot getLot() {
			return position.getLot();
		}

		public int getState() {
			return position.getState();
		}

		public BigDecimalBinding amountAssignedProperty() {
			return amountAssigned;
		}

		public BigDecimal getAmountAssigned() {
			return amountAssigned.get();
		}
		
		public BigDecimal getAmountRemaining() {
			return getAmount().subtract(amountAssigned.get());
		}	  
	}
}
