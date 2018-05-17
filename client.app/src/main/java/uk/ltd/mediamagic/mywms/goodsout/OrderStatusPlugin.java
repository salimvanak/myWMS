package uk.ltd.mediamagic.mywms.goodsout;

import java.util.Objects;
import java.util.function.Function;

import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import uk.ltd.mediamagic.fx.flow.ApplicationContext;
import uk.ltd.mediamagic.fx.flow.Flow;

public class OrderStatusPlugin extends OrdersPlugin {
	
	@Override
	public String getPath() {
		return "{1, _Goods out} -> {100, _Orders Overview}";
	}
	
	@Override
	public void handle(ApplicationContext context, Parent source, Function<Node, Runnable> showNode) {
		Objects.requireNonNull(getQueryClass(), "No query class for " + getBoClass().getName() + " could be found");
		Objects.requireNonNull(getCRUDClass(), "No CRUD class for " + getBoClass().getName() + " could be found");
		Objects.requireNonNull(getBoClass(), "No BODTO class for " + getBoClass().getName() + " could be found");
		
		AnchorPane parent = new AnchorPane();
		parent.getChildren().add(new Label("Waiting..."));
		Flow flow = createNewFlow(context);

		if (flow == null) return; // opperation canceled
		flow.start(OrderStatusPane.class, con -> {
			OrderStatusPane pane = new OrderStatusPane();
			con.autoInjectBean(pane);
			return pane;
		});
		flow.setOnDisplayNode(showNode);
				
		configureFlowNode(parent, flow);

		Runnable onClose = showNode.apply(parent);
		flow.setOnClose(onClose);
		flow.executeStartAction(parent);
	}
}
