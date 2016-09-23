package uk.ltd.mediamagic.mywms;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.TreeMap;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.logging.Logger;

import org.jboss.logmanager.Level;
import org.mywms.ejb.BeanLocator;
import org.mywms.facade.Authentication;
import org.mywms.facade.AuthenticationInfoTO;
import org.mywms.model.User;

import com.sun.glass.ui.Robot;

import de.linogistix.los.query.exception.BusinessObjectNotFoundException;
import de.linogistix.los.user.query.UserQueryRemote;
import javafx.application.Application;
import javafx.application.HostServices;
import javafx.application.Platform;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.binding.DoubleExpression;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Rectangle2D;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.CustomMenuItem;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Screen;
import javafx.stage.Stage;
import res.R;
import uk.ltd.mediamagic.common.utils.MArrays;
import uk.ltd.mediamagic.concurrent.ThreadPool;
import uk.ltd.mediamagic.debug.MLogger;
import uk.ltd.mediamagic.flow.crud.JNIBeanFactory;
import uk.ltd.mediamagic.fx.ApplicationPane;
import uk.ltd.mediamagic.fx.ApplicationService;
import uk.ltd.mediamagic.fx.FxExceptions;
import uk.ltd.mediamagic.fx.FxHacks;
import uk.ltd.mediamagic.fx.FxMainMenuPlugin;
import uk.ltd.mediamagic.fx.MenuUtils;
import uk.ltd.mediamagic.fx.Units;
import uk.ltd.mediamagic.fx.Utils;
import uk.ltd.mediamagic.fx.binding.MBindings;
import uk.ltd.mediamagic.fx.concurrent.MExecutor;
import uk.ltd.mediamagic.fx.concurrent.function.BgFunction;
import uk.ltd.mediamagic.fx.control.MStatusBar;
import uk.ltd.mediamagic.fx.flow.ApplicationContext;
import uk.ltd.mediamagic.fx.flow.Flow;
import uk.ltd.mediamagic.mywms.userlogin.LoginService;
import uk.ltd.mediamagic.mywms.userlogin.LoginServiceImpl;
import uk.ltd.mediamagic.plugin.AbstractPluginSet;
import uk.ltd.mediamagic.plugin.PluginRegistry;

@SuppressWarnings("restriction")
public class MyWMS extends Application {

	private static final Logger log = MLogger.log(MyWMS.class);
	private static MyWMS application = null; 
	
	private final ObjectProperty<LoginService> loginService = new SimpleObjectProperty<LoginService>(this, "loginService") {
		protected void invalidated() {
			context.register(loginService.get());
			context.register(beanLocator);
		};
	};

	private ApplicationPane applicationPane = new ApplicationPane();
	private ApplicationContext context = new ApplicationContext(new JNIBeanFactory(this));
	private MStatusBar statusBar = new MStatusBar(MBindings.asString(loginService, LoginService::getUser));
 	private Spinner<Double> zoomSpinner = new Spinner<>(new SpinnerValueFactory.DoubleSpinnerValueFactory(70, 200, 100, 5));
 	private String serverAddress;
	private TreeMap<String, FxMainMenuPlugin>	runnerActions	= new TreeMap<String, FxMainMenuPlugin>();

 	private VBox menuBox;
 	private VBox main = new VBox();
	private Stage primaryStage;
 	
 	private BeanLocator beanLocator = null;
 	private ObjectProperty<Throwable> lastLoginError = new SimpleObjectProperty<>();
 	public ObjectProperty<Throwable> loginErrorProperty() { return lastLoginError; }
 	
 	
	public static final BooleanBinding hasRole(String... roles) {
		return MBindings.createBoolean(application.loginService, false, l -> l.checkRolesAllowed(roles));
	}
	
 	public static Consumer<ApplicationContext> onLoadComplete = null;
 	
	private ApplicationService applicationService = new ApplicationService() {
		public void setMessage(String message) {
			statusBar.setText(message);
		}

		@Override
		public Runnable addNewNode(Node n) {
			return applicationPane.getOnAddNode().apply(n);
		}

		@Override
		public void realiseFlow(String title, BiFunction<ApplicationContext, Pane, Flow> flowSupplier) {
			AnchorPane parent = new AnchorPane();

			Flow flow = flowSupplier.apply(context, parent);
			if (flow == null) return; // operation cancelled
			flow.setOnDisplayNode(applicationPane.getOnAddNode());

			if (!flow.titleProperty().isBound()) flow.setTitle(title);
			FlowUtils.configureFlowNode(parent, flow);

			Runnable onClose = addNewNode(parent);
			flow.setOnClose(onClose);
			flow.executeStartAction();
		}

		public String getServerAddress() {
			return serverAddress;
		};
	};
	
	@Override
	public void init() throws Exception {
		super.init();
		application = this;
		lastLoginError.addListener((v,o,n)-> log.log(Level.SEVERE,"While logging in", n));
		ThreadPool.startup();
		registerPlugins(context.getBean(MExecutor.class), new String[] {"MasterDataModule"});
		//Thread.sleep(5000);
	}
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		this.primaryStage = primaryStage;
		this.primaryStage.getIcons().add(new Image(MyWMS.class.getResourceAsStream("/logo.png")));
		MLogger.setLevel(Level.INFO);
		serverAddress = getParameters().getNamed().get("server");

		context.register(applicationService);
		context.register(Stage.class, primaryStage);
		context.register(Application.class, this);
		context.register(ApplicationService.class, applicationService);
		context.register(HostServices.class, getHostServices());

		context.autoInjectBean(statusBar);

		Robot robot = com.sun.glass.ui.Application.GetApplication().createRobot();
		Screen screen = Utils.getScreenForPoint(robot.getMouseX(), robot.getMouseY());
		robot.destroy();
		
		Rectangle2D screenBounds = screen.getBounds();

  	MLogger.log(this).severe("Next Creating scene");
		Scene scene = FxHacks.createSceneWithoutAccelerators(main);

  	MLogger.log(this).severe("Setting style sheet.");
		scene.getStylesheets().add(R.CSS_MAIN);
		double scale = 1;
		Units.setEx(Font.getDefault().getSize() * scale);
		VBox.setVgrow(applicationPane, Priority.ALWAYS);
		double prefSize = (960 * scale);
		if (screenBounds.getWidth() / (prefSize) >= 1) {
			applicationPane.splitMain(Side.RIGHT, prefSize/screenBounds.getWidth());
		}
		else if (screenBounds.getHeight() / prefSize >= 2) {
			applicationPane.splitMain(Side.BOTTOM, 0.5d);
		}

		VBox.setVgrow(statusBar, Priority.NEVER);

  	MLogger.log(this).severe("Making menubar");
		menuBox = makeMenuBar();

		main.setId("main-pane");
		main.styleProperty().bind(R.mainStyle.styleProperty());

		main.getChildren().add(menuBox);
		main.getChildren().add(applicationPane);
		main.getChildren().add(statusBar);

		MLogger.log(this).severe("Configuring primary stage");

		loginService.addListener((v,o,n) -> {
			if (n != null) primaryStage.toFront(); 
		});
		
		scene.getRoot().disableProperty().bind(loginService.isNull());

		primaryStage.setScene(scene);
		primaryStage.setTitle("MyWMS");
		primaryStage.setMaximized(true);
		primaryStage.setMinWidth(800);
		primaryStage.setMinHeight(600);
		primaryStage.setX(0);
		primaryStage.setY(0);
		
		primaryStage.setScene(scene);
		primaryStage.show();
		
		LoginDialog login = new LoginDialog(this);
		login.showLogin(primaryStage);
	
		
//		Platform.runLater(() ->  {
//			BasicEntityEditor<ItemData> combo = new BasicEntityEditor<>();
//			combo.setFetchCompleteions(s -> {
//				ItemDataQueryRemote query = context.getBean(ItemDataQueryRemote.class);
//				return context.getBean(MExecutor.class).call(() -> query.autoCompletion(s, new QueryDetail(0, 100)));
//			});
//			combo.setFetchValue(bto -> {
//				ItemDataQueryRemote query = context.getBean(ItemDataQueryRemote.class);
//				return context.getBean(MExecutor.class)
//						.call(() -> query.queryById(bto.getId()))
//						.exceptionally(e -> {
//							log.log(Level.SEVERE, "While fethcing data", e); 
//							return null;
//						});
//			});
//			combo.setConverter(ToStringConverter.of(ItemData::getNumber));
//			context.autoInjectBean(combo);
//			MDialogs.create(scene, "TestCombo").input("ItemData", combo).showOkCancel();
//			System.out.println("GOT VALUE " + combo.getValue());			
//		});
	}
	
	@Override
	public void stop() throws Exception {
		super.stop();
		Platform.exit();
		System.exit(0);
	}
	
  public static AbstractPluginSet[] getModules(String[] modules) {
    modules = MArrays.toSet(modules);
    ArrayList<AbstractPluginSet> plugins = new ArrayList<AbstractPluginSet>(modules.length);

    for(String mod: modules) {
    	if (mod == null) continue;
    	try {
    		try {
    			Class<?> cls = Class.forName(mod);
    			if (AbstractPluginSet.class.isAssignableFrom(cls)) {
    				plugins.add((AbstractPluginSet)cls.newInstance());
    				log.log(Level.INFO, "Activated Module {0}", "Fx"+mod);
    			}
    			else {
    				log.log(Level.INFO, "Unvalid Class for module {0}", "Fx"+mod);
    			}
				}
    		catch (ClassNotFoundException e) {
      		Class<?> clsCompat = Class.forName("uk.ltd.mediamagic.mywms." + mod);
  	      if (AbstractPluginSet.class.isAssignableFrom(clsCompat)) {
  					plugins.add((AbstractPluginSet)clsCompat.newInstance());
  					log.log(Level.INFO, "Activated Module {0}, {1}", new Object[] {mod, clsCompat.getName()});
    			}
    			else {
    				log.log(Level.INFO, "Unvalid Class for module {0}", mod);
    			}
				}
      }
      catch (ClassNotFoundException e) {
      	log.severe(MLogger.format(e));
	      //ExceptionDialog.show(null, e);
      }
      catch (InstantiationException e) {
      	log.severe(MLogger.format(e));
      	FxExceptions.exceptionThrown(e);
      }
      catch (IllegalAccessException e) {
      	log.severe(MLogger.format(e));
      	FxExceptions.exceptionThrown(e);
      }
    }
    plugins.add(new InventoryModule());
    plugins.add(new GoodsInModule());
    plugins.add(new GoodsOutModule());

    return plugins.toArray(new AbstractPluginSet[plugins.size()]);
  }

	public static void registerPlugins(MExecutor executor, String[] moduleStr) {
    AbstractPluginSet[] modules = getModules(moduleStr);
    for (AbstractPluginSet mod : modules) {
    	executor.run(mod::initialise);
    }
    for (AbstractPluginSet mod : modules) {
      PluginRegistry.addAll(mod.getPlugins());
    }

    PluginRegistry.compact();
	}

	List<FxMainMenuPlugin> getActions() {
		//PluginRegistry.dump();

		MLogger.log(this).severe("Fetching Plugins");
		List<FxMainMenuPlugin> list;
		try {
			list = PluginRegistry.getExtentionPoints(FxMainMenuPlugin.class);
		} catch (Exception e) {
			e.printStackTrace();
			list = Collections.emptyList();
		}
		MLogger.log(this).severe("Found " + list.size() + " Plugins");
		return list;
	}
	
	public VBox makeMenuBar() {
	  Menu fileMenu = new Menu("_File");

	  fileMenu.getItems().add(createZoomMenus());
	  fileMenu.getItems().add(new SeparatorMenuItem());

	  MenuItem mi = new MenuItem("Garbage Collect");
	  mi.setOnAction((e) -> System.gc());
	  fileMenu.getItems().add(mi);

	  fileMenu.getItems().add(new SeparatorMenuItem());

	  CheckMenuItem hideToolBar = new CheckMenuItem("Hide toolbar");

	  MenuItem mi2 = new MenuItem("Exit");
	  mi2.setOnAction((e) -> {
			Platform.exit();
			System.exit(0);
	  });
	  fileMenu.getItems().addAll(hideToolBar, mi2);

	  MenuBar menuBar = new MenuBar();
	  menuBar.setUseSystemMenuBar(true);
		menuBar.getMenus().addAll(fileMenu);

    List<FxMainMenuPlugin> runners = getActions();
    for (FxMainMenuPlugin m : runners) {
      runnerActions.put(m.getPath(), m);
    }

    MenuUtils.buildMenu(context, menuBar, runners,  applicationPane.getOnAddNode());

//	  ToolBar tool = makeToolBar(toolBarActions);
//	  tool.managedProperty().bind(tool.visibleProperty());
//	  tool.visibleProperty().bind(hideToolBar.selectedProperty().not());

	  VBox box = new VBox(menuBar);

    return box;
	}
	
	private Integer getDefaultZoom() {
		return 100;
	}

	public MenuItem createZoomMenus() {
  	Integer zoomValue = getDefaultZoom();
  	R.mainStyle.fontSizeProperty().bind(DoubleExpression.doubleExpression(zoomSpinner.valueProperty()).multiply(0.13d));
  	zoomSpinner.getValueFactory().setValue(zoomValue.doubleValue());
  	Label l = new Label("Zoom", zoomSpinner);
  	l.setContentDisplay(ContentDisplay.RIGHT);
  	return new CustomMenuItem(l, false);
  }

	public static void main(String[] args) {
		Thread.setDefaultUncaughtExceptionHandler((t,e) -> {
			Logger.getGlobal().log(Level.SEVERE, "Unhandeled Exception", e);
		});
		launch(args);
	}
	
	public LoginService createLoginService(BeanLocator beanLocator) throws BusinessObjectNotFoundException {
		LoginService loginService = new LoginServiceImpl();
		Authentication authBean = beanLocator.getStateless(Authentication.class);
		AuthenticationInfoTO auth = authBean.getUserInfo();

		UserQueryRemote userQuery = beanLocator.getStateless(UserQueryRemote.class);
		User user = userQuery.queryByIdentity(auth.userName);
		loginService.setAuthentification(auth);
		loginService.processUser(user);
		
		return loginService;
	}
	
	public void setBeanLocator(BeanLocator beanLocator) {
		this.beanLocator = beanLocator;
		CompletableFuture.supplyAsync(() -> beanLocator)
			.thenCompose(BgFunction.bind(this::createLoginService))
			.handleAsync((l, e) -> {
				lastLoginError.set(e);
				loginService.set(l);
				return null;
			}, Platform::runLater);
	}

	public BeanLocator getBeanLocator() {
		return beanLocator;
	}

	public final ObjectProperty<LoginService> loginServiceProperty() {
		return this.loginService;
	}
	

	public final LoginService getLoginService() {
		return this.loginServiceProperty().get();
	}
	

	public final void setLoginService(final LoginService loginService) {
		this.loginServiceProperty().set(loginService);
	}

}