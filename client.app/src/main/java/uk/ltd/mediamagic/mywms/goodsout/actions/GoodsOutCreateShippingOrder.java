package uk.ltd.mediamagic.mywms.goodsout.actions;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import org.mywms.facade.FacadeException;

import de.linogistix.los.inventory.exception.InventoryException;
import de.linogistix.los.inventory.exception.InventoryExceptionKey;
import de.linogistix.los.inventory.facade.LOSGoodsOutFacade;
import de.linogistix.los.inventory.model.LOSPickingUnitLoad;
import de.linogistix.los.inventory.query.LOSPickingUnitLoadQueryRemote;
import de.linogistix.los.model.State;
import javafx.scene.control.CheckBox;
import uk.ltd.mediamagic.annot.Worker;
import uk.ltd.mediamagic.fx.MDialogs;
import uk.ltd.mediamagic.fx.concurrent.function.BgFunction;
import uk.ltd.mediamagic.fx.data.TableKey;
import uk.ltd.mediamagic.fx.flow.Flow;
import uk.ltd.mediamagic.fx.flow.ViewContext;
import uk.ltd.mediamagic.fx.flow.ViewContextBase;
import uk.ltd.mediamagic.fx.flow.actions.WithMultiSelection;
import uk.ltd.mediamagic.mywms.common.MyWMSUserPermissions;

public class GoodsOutCreateShippingOrder implements WithMultiSelection<Object> {
	
	@Override
	public void execute(Object source, Flow flow, ViewContext context, Collection<TableKey> key) {
		
		final boolean allowFinished;
		final boolean sameOrderNumber;
		if (MyWMSUserPermissions.isAdmin()) {
			CheckBox useFinished = new CheckBox("Allow retour");
			CheckBox diffOrderNumber = new CheckBox("Allow selection from different order numbers");
			boolean ok = MDialogs.create(context.getRootNode())
				.input(useFinished)
				.input(diffOrderNumber)
				.showOkCancel();
			if (!ok) {
				return; // user cancelled.
			}
 			allowFinished = useFinished.isSelected();
  		sameOrderNumber = !diffOrderNumber.isSelected();
		}
		else {
			allowFinished = false;
			sameOrderNumber = true;			
		}
		
		List<Long> ids = key.stream().map(k -> (Long) k.get("id")).collect(Collectors.toList());
		createShippingOrder(context, ids, sameOrderNumber, allowFinished);				
	}
	
	public static CompletableFuture<Void> createShippingOrder(ViewContextBase context, List<Long> unitloadIds, boolean sameOrderNumber, boolean allowFinished) {
		LOSGoodsOutFacade facade = context.getBean(LOSGoodsOutFacade.class);
		LOSPickingUnitLoadQueryRemote query = context.getBean(LOSPickingUnitLoadQueryRemote.class);		
		
		return context.getExecutor().call(
				() -> checkUnitLoads(query, unitloadIds, sameOrderNumber, allowFinished))
				.thenCompose(BgFunction.bind(x -> {
					facade.createGoodsOutOrder(unitloadIds);
					return null;
				}));		
	}
	
	@Worker
	public static boolean checkUnitLoads(LOSPickingUnitLoadQueryRemote query, List<Long> ids, boolean sameOrderNumber, boolean allowFinished) throws Exception {
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
