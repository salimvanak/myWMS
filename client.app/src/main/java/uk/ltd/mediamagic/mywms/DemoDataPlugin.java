package uk.ltd.mediamagic.mywms;

import java.util.function.Function;

import org.mywms.facade.FacadeException;
import org.mywms.service.ItemDataServiceBean;

import de.linogistix.los.reference.facade.RefTopologyFacade;
import de.linogistix.los.reference.model.ProjectPropertyKey;
import de.linogistix.los.util.entityservice.LOSSystemPropertyServiceRemote;
import javafx.beans.binding.BooleanBinding;
import javafx.scene.Node;
import javafx.scene.Parent;
import uk.ltd.mediamagic.fx.concurrent.MExecutor;
import uk.ltd.mediamagic.fx.flow.ApplicationContext;
import uk.ltd.mediamagic.mywms.common.MyWMSUserPermissions;

public class DemoDataPlugin extends MyWMSMainMenuPlugin {
	public DemoDataPlugin() {
	}
	
	
	@Override
	public String getPath() {
		return "{1, _System} -> {1, Create Demo Topology}";
	}
	
	@Override
	protected BooleanBinding createVisibleBinding() {
		return MyWMSUserPermissions.adminUser();
	}
	
	@Override
	public void handle(ApplicationContext context, Parent source, Function<Node, Runnable> showNode) {
		RefTopologyFacade facade = context.getBean(RefTopologyFacade.class);
		LOSSystemPropertyServiceRemote prop = context.getBean(LOSSystemPropertyServiceRemote.class);

		
		context.getBean(MExecutor.class).executeAndWatch(source, p -> {
	    boolean allowed = prop.getBooleanDefault(ProjectPropertyKey.CREATE_DEMO_TOPOLOGY, true);
	    if (allowed) {
	    	facade.createDemoTopology();
	    }
	    else {
	    	throw new FacadeException("Cannot create demo topology, there is already data in the database.", null, null);
	    }
	    return null;
		});

	}
	
}
