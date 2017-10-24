package uk.ltd.mediamagic.mywms.system;

import java.util.function.Function;

import de.linogistix.los.inventory.facade.InventoryCleanupFacade;
import javafx.beans.binding.BooleanBinding;
import javafx.scene.Node;
import javafx.scene.Parent;
import uk.ltd.mediamagic.fx.MDialogs;
import uk.ltd.mediamagic.fx.concurrent.MExecutor;
import uk.ltd.mediamagic.fx.flow.ApplicationContext;
import uk.ltd.mediamagic.mywms.MyWMSMainMenuPlugin;
import uk.ltd.mediamagic.mywms.common.MyWMSUserPermissions;

public class StockUnitCleanupPlugin  extends MyWMSMainMenuPlugin {
	
	public StockUnitCleanupPlugin() {
	}

	@Override
	protected BooleanBinding createVisibleBinding() {
		return MyWMSUserPermissions.adminUser();
	}
	
	@Override
	public String getPath() {
		return "{1, _System} -> {999, _Cleanup requests} -> {999, _Cleanup Stock Units}";
	}

	@Override
	public void handle(ApplicationContext context, Parent source, Function<Node, Runnable> showNode) {
		boolean ok = MDialogs.create(source, "Clean up stock units").masthead("Delete empty stock unit on Nirwana.")
		.showYesNo(MDialogs.Yes3);
		
		if (!ok) return;
		
		InventoryCleanupFacade fascade = context.getBean(InventoryCleanupFacade.class);
		
		MExecutor exec = context.getBean(MExecutor.class);
		exec.executeAndWatch(source, p -> {
			p.updateMessage("Cleaning up stock units on Nirwana");
			p.printf("Cleaning up stock units on Nirwana, please wait...");
			int rounds = 0;
			long stockUnitsDeleted;
			do {
				rounds ++;
				stockUnitsDeleted = fascade.cleanupStockUnitsOnNirwana();
				p.printf("Round " + rounds + ": " + stockUnitsDeleted + " StockUnits were removed");
			}
			while (stockUnitsDeleted != 0);
			
			p.updateMessage("Done");
			p.done();
			return null;
		});
	}

}
