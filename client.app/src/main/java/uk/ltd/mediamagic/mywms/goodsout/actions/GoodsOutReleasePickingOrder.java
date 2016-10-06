package uk.ltd.mediamagic.mywms.goodsout.actions;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import de.linogistix.los.inventory.facade.LOSPickingFacade;
import uk.ltd.mediamagic.fx.MDialogs;
import uk.ltd.mediamagic.fx.data.TableKey;
import uk.ltd.mediamagic.fx.flow.Flow;
import uk.ltd.mediamagic.fx.flow.ViewContext;
import uk.ltd.mediamagic.fx.flow.actions.WithMultiSelection;

public class GoodsOutReleasePickingOrder implements WithMultiSelection<Object> {
	
	@Override
	public void execute(Object source, Flow flow, ViewContext context, Collection<TableKey> key) {
		boolean ok = MDialogs.create(context.getRootNode(), "Release Picking Order")
				.masthead("Release selected picking orders for picking.")
			.showOkCancel();
		
		if (!ok) return; // user canceled

		LOSPickingFacade facade = context.getBean(LOSPickingFacade.class);
		List<Long> ids = key.stream().map(k -> (Long) k.get("id")).collect(Collectors.toList());		
		
		context.getExecutor().call(
				() -> {
					for (long id : ids) facade.releaseOrder(id);
					return null;
				})
		.thenRun(()-> flow.executeCommand(Flow.REFRESH_ACTION));
	}
	
}
