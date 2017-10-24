package uk.ltd.mediamagic.mywms;

import java.util.function.Function;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.ButtonBase;
import javafx.scene.control.MenuItem;
import uk.ltd.mediamagic.fx.ApplicationPane;
import uk.ltd.mediamagic.fx.FXPlugin;
import uk.ltd.mediamagic.fx.flow.ApplicationContext;
import uk.ltd.mediamagic.fx.plugins.FxMenu;

public abstract class MyWMSMainMenuPlugin implements FxMenu {
	final private BooleanBinding disable;
  final private BooleanBinding visible;

  public MyWMSMainMenuPlugin() {
  	disable = createDisableBinding();
  	visible = createVisibleBinding();
  }
  
  public String getDescription() {
  	return null;
  }
  
	protected BooleanBinding createDisableBinding() {
		return Bindings.createBooleanBinding(() -> false);
	}
	
	protected BooleanBinding createVisibleBinding() {
		return Bindings.createBooleanBinding(() -> true);
	}

  public BooleanBinding disableProperty() {
  	return disable;
  }
  
  @Override
  public BooleanBinding visibleProperty() {
    return visible;
  }
    
  public void install(ApplicationContext context, Parent root, MenuItem control, Function<Node, Runnable> showNode) {
  	control.setText(getName(getPath()));
  	control.setOnAction(e -> handle(context, root, showNode));
  	control.visibleProperty().bind(visibleProperty());
  	control.disableProperty().bind(disableProperty());
  }
  
  public void install(ApplicationContext context, ButtonBase control, Function<Node, Runnable> showNode) {
  	control.managedProperty().bind(visibleProperty());
  	control.setText(getName(getPath()));
  	control.setOnAction(e -> handle(context, control, showNode));
  	control.visibleProperty().bind(visibleProperty());
  	control.disableProperty().bind(disableProperty());
  }
  
  public static String getName(String path) {
  	return FXPlugin.getName(path);
  }
  
	public static String convertToFx(String old) {
		return FXPlugin.convertToFx(old);
	}
	
	protected static Parent setTitle(Parent parent, String title) {		
		ApplicationPane.setTitle(parent, title);
		return parent;
	}

	/**
	 * Is called when this plugin is executed.
	 * @param source can be used to get the scene for dialogs.  Do not modify this node.
	 * @param showNode is a callback used to display a new node on the scene.
	 * @param ActionEvent the action event that caused this to execute.
	 */
	public abstract void handle(ApplicationContext context, Parent source, Function<Node, Runnable> showNode);
	
}
