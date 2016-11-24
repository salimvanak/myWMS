package uk.ltd.mediamagic.mywms;

import java.util.function.Function;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.ListBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBase;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import uk.ltd.mediamagic.common.utils.Strings;
import uk.ltd.mediamagic.fx.ApplicationPane;
import uk.ltd.mediamagic.fx.ApplicationService;
import uk.ltd.mediamagic.fx.MDialogs;
import uk.ltd.mediamagic.fx.action.RootCommand;
import uk.ltd.mediamagic.fx.controller.ControllerBase;
import uk.ltd.mediamagic.fx.data.TableKey;
import uk.ltd.mediamagic.fx.flow.AnimatedFlow;
import uk.ltd.mediamagic.fx.flow.ApplicationContext;
import uk.ltd.mediamagic.fx.flow.BeanFactory2;
import uk.ltd.mediamagic.fx.flow.ContextBase;
import uk.ltd.mediamagic.fx.flow.Flow;
import uk.ltd.mediamagic.fx.flow.FlowContext;
import uk.ltd.mediamagic.fx.flow.ViewContext;
import uk.ltd.mediamagic.fx.flow.ViewContextBase;
import uk.ltd.mediamagic.fx.flow.actions.CancelAction;
import uk.ltd.mediamagic.fx.flow.actions.CommandProvider;
import uk.ltd.mediamagic.fx.flow.actions.FlowAction;
import uk.ltd.mediamagic.fx.flow.actions.RefreshTask;
import uk.ltd.mediamagic.fx.flow.actions.SaveAndBackAction;
import uk.ltd.mediamagic.fx.flow.actions.SaveTask;

public class FlowUtils {

	public static void startNewFlow(ApplicationContext context, Flow flow) {
		AnchorPane root = new AnchorPane();

		FlowUtils.configureFlowNode(root, flow);

		Runnable onClose = context.displayNode(root);
		flow.executeStartAction(root);
		flow.setOnClose(onClose);
	}

	public static <T> void startNewFlow(ContextBase context, Class<T> clazz, Function<ViewContext, T> startFunction) {
		ApplicationService appService = context.getBean(ApplicationService.class);	
		AnchorPane parent = new AnchorPane();

		Flow flow = FlowUtils.defaultFlow(new FlowContext(context, null));
  	flow.start(clazz, parent, startFunction);
		flow.setOnDisplayNode(appService::addNewNode);
	
		FlowUtils.configureFlowNode(parent, flow);
		
		Runnable onClose = appService.addNewNode(parent);
		flow.setOnClose(onClose);
		flow.executeStartAction();
	}

	public static void executeCommand(ViewContextBase context, String action, Class<?> cls, TableKey key) {
		Flow f = context.getBean(Flow.class);
		f.executeCommand(action, key); 
	}

	public static void executeCommand(ViewContextBase context, Enum<?> action, Class<?> cls, TableKey key) {
		Flow f = context.getBean(Flow.class);
		f.executeCommand(action, key); 
	}

	public static void executeCommand(ViewContextBase context, String action) {
		Flow f = context.getBean(Flow.class);
		f.executeCommand(action); 
	}

	public static void executeCommand(ViewContextBase context, Enum<?> action) {
		Flow f = context.getBean(Flow.class);
		f.executeCommand(action); 
	}

	public static <T> void showNext(Flow f, ViewContext newContext, Class<T> cls, T state) {
  	newContext.setActiveBean(cls, state);
  	f.next(newContext);
	}

	public static <T> void showNext(T state, Flow f, FlowAction<T> action) {
		f.processCommand(state, action);
	}

	public static <T extends Enum<T>> void showPopup(T name, Flow f, Object controller) {
		showPopup(Flow.enumToActionName(name), f, controller);
	}

	public static <T extends ControllerBase>
	void showWithoutFlow(Node parent, String name, T controller) {
		BooleanProperty close = new SimpleBooleanProperty(false);
		EventHandler<ActionEvent> onClose = e -> close.set(true);

		boolean hasCloseAction = false;
		HBox toolbar = null;
		if (controller instanceof CommandProvider) {
			RootCommand commands = ((CommandProvider) controller).getCommands();
			hasCloseAction = commands.exists(Flow.BACK_ACTION) || commands.exists(Flow.CANCEL_ACTION); 
			toolbar = new HBox();
			ListBinding<Node> actions = commands.asListBinding(t -> {
				if (t instanceof ButtonBase) {
					ButtonBase b = (ButtonBase) t;
					EventHandler<ActionEvent> oEH = b.getOnAction();
					if (Strings.equals(Flow.OK_ACTION, Flow.getCommand(b))) {
						b.setOnAction((oEH == null) ?  onClose : e -> { oEH.handle(e); onClose.handle(e); });
					}
					else if (Strings.equals(Flow.CANCEL_ACTION, Flow.getCommand(b))) {
						b.setOnAction((oEH == null) ?  onClose : e -> { oEH.handle(e); onClose.handle(e); });				
					}
					else if (Strings.equals(Flow.BACK_ACTION, Flow.getCommand(b))) {
						b.setOnAction((oEH == null) ?  onClose : e -> { oEH.handle(e); onClose.handle(e); });				
					}
				}
			});			
			Bindings.bindContent(toolbar.getChildren(), actions); 
		}		

		if (toolbar == null || toolbar.getChildren().size() == 0) {
			Button closeButton = new Button("_Close");
			closeButton.setOnAction(onClose);
			closeButton.setMnemonicParsing(true);
			
			toolbar = new HBox(closeButton);
		}
		else if (!hasCloseAction) {
			Button closeButton = new Button("_Close");
			closeButton.setOnAction(onClose);
			closeButton.setMnemonicParsing(true);
			
			toolbar.getChildren().addAll(closeButton);
		}
		
		toolbar.getStyleClass().add("popup-buttons");

		Node mainNode = controller.getView();
		mainNode.addEventFilter(KeyEvent.KEY_RELEASED, e -> { if (e.getCode() == KeyCode.ESCAPE) close.set(true); });
		VBox popup = new VBox(mainNode, toolbar);
		popup.getStyleClass().addAll("background-filled", "flow-popup");

		MDialogs.create(parent, name).content(popup).show(close);
	}
	
	public static void showPopup(String name, Flow f, Object controller) {
		ViewContext context = f.createChildContext(name);
		context.setPopup(true);
		context.setActiveBean(Object.class, controller);
		f.next(context);
	}

	public static void showPopup(String name, ContextBase context, Object controller) {
		Flow f = context.getBean(Flow.class);
		showPopup(name, f, controller);			
	}
	
//	public static void showTablePopup(String name, ViewContext context, DataSet ds) {
//  	FxTableChooser<DataSet> c = new FxTableChooser<>();
//  	context.autoInjectBean(c);
//		Bindings.bindContent(c.getTable().getColumns(), new TableColumnBinding<>(c.getTable()));
//		context.getExecutor().apply(DataUtils::lockAndLoad, ds).thenSetUI(c.dataSetProperty());
//
//		FlowUtils.showPopup(name, context, c);
//	}

//	public static CompletableFuture<List<TableKey>> showTableChooser(String name, ViewContextBase context, DataSet ds) {
//		CompletableFuture<List<TableKey>> result = new CompletableFuture<>();
//
//		FxTableChooser<DataSet> c = new FxTableChooser<>();
//  	context.autoInjectBean(c);
//		Bindings.bindContent(c.getTable().getColumns(), new TableColumnBinding<>(c.getTable()));
//		context.getExecutor().apply(DataUtils::lockAndLoad, ds).thenSetUI(c.dataSetProperty());
//
//		c.getTable().getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
//  	c.getTable().setOnMouseClicked(e -> {
//  		if (e.getClickCount() == 2) {
//  			result.complete(c.getSelectedKeys());
//  			c.getContext().getBean(Flow.class).back(false);
//  		}
//  	});
//  	c.commands().okCancel(e -> {
//  		result.complete(c.getSelectedKeys());
//  	});
//
//		FlowUtils.showPopup(name, context, c);
//		return result;
//	}

	public static Pane configureFlowNode(Pane parent, Flow flow) {		
		EventHandler<Event> onCloseRequestHandler = flow.createOnCloseRequestHandler();		
		ApplicationPane.getTitleProperty(parent).bind(flow.titleProperty());
		ApplicationPane.setOnCloseRequestHandler(parent, onCloseRequestHandler);
		return parent;
	}

	public static final Flow defaultFlow(FlowContext context) {
		//Flow flow = new BasicFlow(context);
		Flow flow = new AnimatedFlow(context);
		flow.global()
			.back()
			.put(new SaveTask())
			.put(new SaveAndBackAction())
			.put(new CancelAction())
			.put(new RefreshTask())
		.end();
		
		return flow;
	}

	public static final Flow defaultFlow(ContextBase context, BeanFactory2 factory) {		
		return defaultFlow(new FlowContext(context, factory));
	}
}
