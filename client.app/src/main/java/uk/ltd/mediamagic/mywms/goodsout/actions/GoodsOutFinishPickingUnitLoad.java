package uk.ltd.mediamagic.mywms.goodsout.actions;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import de.linogistix.los.inventory.facade.LOSPickingFacade;
import de.linogistix.los.inventory.model.LOSInventoryPropertyKey;
import de.linogistix.los.inventory.model.LOSPickingUnitLoad;
import de.linogistix.los.inventory.query.LOSPickingUnitLoadQueryRemote;
import de.linogistix.los.util.entityservice.LOSSystemPropertyServiceRemote;
import javafx.application.Platform;
import uk.ltd.mediamagic.fx.data.TableKey;
import uk.ltd.mediamagic.fx.flow.Flow;
import uk.ltd.mediamagic.fx.flow.ViewContext;
import uk.ltd.mediamagic.fx.flow.actions.WithMultiSelection;

/**
 * Finishes the picking unit load.  Only useful if there was some error to correct
 * @author slim
 *
 */
public class GoodsOutFinishPickingUnitLoad implements WithMultiSelection<Object> {
	
	@Override
	public void execute(Object source, Flow flow, ViewContext context, Collection<TableKey> key) {
		LOSPickingFacade facade = context.getBean(LOSPickingFacade.class);
		LOSPickingUnitLoadQueryRemote query = context.getBean(LOSPickingUnitLoadQueryRemote.class);
		LOSSystemPropertyServiceRemote propertyService = context.getBean(LOSSystemPropertyServiceRemote.class);
		List<Long> ids = key.stream().map(k -> (Long) k.get("id")).collect(Collectors.toList());		
		
		context.getExecutor().call(
				() -> {
					for (long id : ids) {
						String goodsOutLocation = propertyService.getString(LOSInventoryPropertyKey.SHIPPING_LOCATION);
						LOSPickingUnitLoad ul = query.queryById(id);	
						facade.finishPickingUnitLoad(ul.getUnitLoad().getLabelId(), goodsOutLocation);
					}
					return null;
				})
		.thenRunAsync(() -> flow.executeCommand(Flow.REFRESH_ACTION), Platform::runLater);
	}
	
}
