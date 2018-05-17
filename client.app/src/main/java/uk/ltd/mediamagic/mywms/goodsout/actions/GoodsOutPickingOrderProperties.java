package uk.ltd.mediamagic.mywms.goodsout.actions;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;

import org.mywms.facade.FacadeException;
import org.mywms.model.User;

import de.linogistix.los.inventory.exception.InventoryException;
import de.linogistix.los.inventory.exception.InventoryExceptionKey;
import de.linogistix.los.inventory.facade.LOSPickingFacade;
import de.linogistix.los.inventory.model.LOSPickingOrder;
import de.linogistix.los.inventory.model.LOSPickingUnitLoad;
import de.linogistix.los.inventory.query.LOSPickingOrderQueryRemote;
import de.linogistix.los.inventory.query.LOSPickingUnitLoadQueryRemote;
import de.linogistix.los.location.model.LOSStorageLocation;
import de.linogistix.los.model.State;
import javafx.application.Platform;
import javafx.scene.control.ComboBox;
import uk.ltd.mediamagic.annot.Worker;
import uk.ltd.mediamagic.flow.crud.BasicEntityEditor;
import uk.ltd.mediamagic.fx.MDialogs;
import uk.ltd.mediamagic.fx.data.TableKey;
import uk.ltd.mediamagic.fx.flow.Flow;
import uk.ltd.mediamagic.fx.flow.ViewContext;
import uk.ltd.mediamagic.fx.flow.ViewContextBase;
import uk.ltd.mediamagic.fx.flow.actions.WithSelection;
import uk.ltd.mediamagic.mywms.common.QueryUtils;
import uk.ltd.mediamagic.util.Closures;

public class GoodsOutPickingOrderProperties implements WithSelection<Object> {
	
	@Override
	public void execute(Object source, Flow flow, ViewContext context, TableKey key) {
		Long id = (Long) key.get("id");
		changeProperties(context, Collections.singletonList(id))
		.thenRunAsync(() -> flow.executeCommand(Flow.REFRESH_ACTION), Platform::runLater);
	}
	
	public static CompletableFuture<List<Long>> changeProperties(ViewContextBase context, List<Long> pickingOrderIds) {
		LOSPickingOrderQueryRemote query = context.getBean(LOSPickingOrderQueryRemote.class);
		LOSPickingOrder order = context.getExecutor().executeAndWait(context.getRootNode(), 
				() -> query.queryById(pickingOrderIds.get(0)));
		
		PickingOrderProperties inProps = new PickingOrderProperties(order.getPrio(), order.getDestination(), order.getOperator());
		
		final PickingOrderProperties r = changeProperties(context, inProps);
		
		if (r == null) { // user cancelled
			CompletableFuture<List<Long>> c = new CompletableFuture<>();
			c.completeExceptionally(new CancellationException("User cancelled"));
			return c;
		}
		else {
			int prio = r.getPrio();
			String destinationName = Closures.guardedValue(r.getDestination(), LOSStorageLocation::getName, null);
			String userName = Closures.guardedValue(r.getUser(), User::getName, null);
			
			LOSPickingFacade facade = context.getBean(LOSPickingFacade.class);
			
			boolean changePrio = r.isPrioityChanged();
			boolean changeLocation = r.isDestinationChanged();
			boolean changeUser = r.isUserChanged();
			
			return context.getExecutor().call(
					() -> {
						for (long pickingOrderId : pickingOrderIds) {								
							if (changeLocation)	facade.changePickingOrderDestination(pickingOrderId, destinationName);
							if (changePrio) facade.changePickingOrderPrio(pickingOrderId, prio);
							if (changeUser) facade.changePickingOrderUser(pickingOrderId, userName);
						}
						return pickingOrderIds;
					});
		}
	}
	
	public static PickingOrderProperties changeProperties(ViewContextBase context, PickingOrderProperties props) {
		ComboBox<Integer> prioField = QueryUtils.priorityCombo();
		BasicEntityEditor<LOSStorageLocation> destinationField = new BasicEntityEditor<>();
		BasicEntityEditor<User> userField = new BasicEntityEditor<>();

		userField.configure(context, User.class);
		destinationField.configure(context, LOSStorageLocation.class);
		userField.setValue(props.getOrigUser());
		destinationField.setValue(props.getOrigDestination());
		prioField.setValue(props.getOrigPrio());
		
		boolean ok = MDialogs.create(context.getRootNode(), "Lock Stock Unit")
			.input("Priority", prioField)
			.input("Destination", destinationField)
			.input("User", userField)
			.showOkCancel();

		if (!ok) return null; // user canceled

		props.setDestination(destinationField.getValue());
		props.setUser(userField.getValue());
		props.setPrio(prioField.getValue());
		
		return props;
	}
	
	@Worker
	private boolean checkUnitLoads(LOSPickingUnitLoadQueryRemote query, List<Long> ids, boolean sameOrderNumber, boolean allowFinished) throws Exception {
		Set<String> orderNumbers = new TreeSet<>();
		
		for (long id : ids) {
			LOSPickingUnitLoad ul = query.queryById(id);
			orderNumbers.add(ul.getCustomerOrderNumber());
			if (!allowFinished && ul.getState() == State.FINISHED) {
				throw new InventoryException(InventoryExceptionKey.GOODS_OUT_EXISTS_FOR_UNITLOAD, ul.toUniqueString());
			}
			else if (ul.getState() < State.PICKED) {
				throw new InventoryException(InventoryExceptionKey.GOODS_OUT_NOT_FINISHED, ul.toUniqueString());
			}
		}
		
		if (sameOrderNumber && orderNumbers.size() != 1) throw new FacadeException("Selection contains multiple order numbers", null, null);
		return true;
	}

	
	public static final class PickingOrderProperties {
		private final int origPrio;
		private final LOSStorageLocation origDestination;
		private final User origUser;

		private int prio;
		private LOSStorageLocation destination;
		private User user;
		
		public PickingOrderProperties(int origPrio, LOSStorageLocation origDestination, User origUser) {
			super();
			this.origPrio = origPrio;
			this.origDestination = origDestination;
			this.origUser = origUser;
		}

		public int getPrio() {
			return prio;
		}

		public void setPrio(int prio) {
			this.prio = prio;
		}

		public LOSStorageLocation getDestination() {
			return destination;
		}

		public void setDestination(LOSStorageLocation destination) {
			this.destination = destination;
		}

		public User getUser() {
			return user;
		}

		public void setUser(User user) {
			this.user = user;
		}

		public int getOrigPrio() {
			return origPrio;
		}

		public LOSStorageLocation getOrigDestination() {
			return origDestination;
		}

		public User getOrigUser() {
			return origUser;
		}
		
		public boolean isUserChanged() {
			if ((origUser == null) && (user == null)) return false; 
			if (user == null) return true; 
			if (origUser == null) return true; 
			return origUser.getId() != user.getId();
		}

		public boolean isDestinationChanged() {
			if ((origDestination == null) && (destination == null)) return false; 
			if ((origDestination == null)) return true; 
			if ((destination == null)) return true; 
			return origDestination.getId() != destination.getId();
		}

		public boolean isPrioityChanged() {
			return origPrio != prio;
		}		
	}
}
