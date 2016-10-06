package uk.ltd.mediamagic.mywms.goodsout.actions;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import de.linogistix.los.inventory.facade.LOSPickingFacade;
import javafx.application.Platform;
import uk.ltd.mediamagic.fx.MDialogs;
import uk.ltd.mediamagic.fx.data.TableKey;
import uk.ltd.mediamagic.fx.flow.Flow;
import uk.ltd.mediamagic.fx.flow.ViewContext;
import uk.ltd.mediamagic.fx.flow.actions.WithMultiSelection;

public class GoodsOutFinishPickingOrder implements WithMultiSelection<Object> {
	
	@Override
	public void execute(Object source, Flow flow, ViewContext context, Collection<TableKey> key) {
		boolean ok = MDialogs.create(context.getRootNode(), "Finish Picking Order")
				.masthead("Finish the selected picking orders.\n Remaining picks will be canceled")
			.showOkCancel();
		
		if (!ok) return; // user canceled

		LOSPickingFacade facade = context.getBean(LOSPickingFacade.class);
		List<Long> ids = key.stream().map(k -> (Long) k.get("id")).collect(Collectors.toList());		
		
		context.getExecutor().call(
				() -> {
					for (long id : ids) facade.finishOrder(id);
					return null;
				})
		.thenRunAsync(() -> flow.executeCommand(Flow.REFRESH_ACTION), Platform::runLater);

	}
	
}
