package uk.ltd.mediamagic.mywms.goodsout.actions;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;

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
import uk.ltd.mediamagic.common.utils.Strings;
import uk.ltd.mediamagic.flow.crud.BasicEntityEditor;
import uk.ltd.mediamagic.fx.MDialogs;
import uk.ltd.mediamagic.fx.data.TableKey;
import uk.ltd.mediamagic.fx.flow.Flow;
import uk.ltd.mediamagic.fx.flow.ViewContext;
import uk.ltd.mediamagic.fx.flow.actions.WithSelection;
import uk.ltd.mediamagic.mywms.common.QueryUtils;
import uk.ltd.mediamagic.util.Closures;

public class GoodsOutPickingOrderProperties implements WithSelection<Object> {
	
	@Override
	public void execute(Object source, Flow flow, ViewContext context, TableKey key) {
		Long id = (Long) key.get("id");
		
		LOSPickingOrderQueryRemote query = context.getBean(LOSPickingOrderQueryRemote.class);
		LOSPickingOrder order = context.getExecutor().executeAndWait(context.getRootNode(), 
				() -> query.queryById(id));
		
		ComboBox<Integer> prioField = QueryUtils.priorityCombo();
		BasicEntityEditor<LOSStorageLocation> destinationField = new BasicEntityEditor<>();
		BasicEntityEditor<User> userField = new BasicEntityEditor<>();

		userField.configure(context, User.class);
		destinationField.configure(context, LOSStorageLocation.class);
		userField.setValue(order.getOperator());
		destinationField.setValue(order.getDestination());
		prioField.setValue(order.getPrio());
		
		boolean ok = MDialogs.create(context.getRootNode(), "Lock Stock Unit")
			.input("Priority", prioField)
			.input("Destination", destinationField)
			.input("User", userField)
			.showOkCancel();

		if (!ok) return; // user canceled

		int prio = prioField.getValue();
		String destinationName = Closures.guardedValue(
				destinationField.getValue(), LOSStorageLocation::getName, null);
		String userName = Closures.guardedValue(userField.getValue(), User::getName, null);

		LOSPickingFacade facade = context.getBean(LOSPickingFacade.class);

		boolean changePrio = order.getPrio() != prio;
		boolean changeLocation = Objects.equals(order.getDestination(), destinationField.getValue());
		boolean changeUser = Objects.equals(order.getOperator(), userField.getValue());
		
		context.getExecutor().call(
				() -> {
					if (changeLocation) facade.changePickingOrderDestination(id, destinationName);
					if (changePrio) facade.changePickingOrderPrio(id, prio);
					if (changeUser) {
						if (Strings.isEmpty(userName)) 
							facade.resetOrder(id);
						else 
							facade.reserveOrder(id, userName);
					}
					return null;
				})
		.thenRunAsync(() -> flow.executeCommand(Flow.REFRESH_ACTION), Platform::runLater);
				
	}
	
	@Worker
	public boolean checkUnitLoads(LOSPickingUnitLoadQueryRemote query, List<Long> ids, boolean sameOrderNumber, boolean allowFinished) throws Exception {
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

}
