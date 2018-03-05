package uk.ltd.mediamagic.mywms;

import java.net.URL;
import java.util.Collections;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.CompletableFuture;

import org.mywms.ejb.BeanLocator;

import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.beans.value.WeakChangeListener;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import res.R;
import uk.ltd.mediamagic.common.utils.Strings;
import uk.ltd.mediamagic.flow.crud.MLogger;
import uk.ltd.mediamagic.fx.AwesomeIcon;
import uk.ltd.mediamagic.fx.FXUtils;
import uk.ltd.mediamagic.fx.Units;
import uk.ltd.mediamagic.fx.control.SimpleFormBuilder;
import uk.ltd.mediamagic.fx.flow.FXErrors;

public class LoginDialog {

	private final PauseTransition wipeErrorField = new PauseTransition(Duration.seconds(3));

	private SimpleFormBuilder loginForm = new SimpleFormBuilder();
	private Label errorField = new Label();
	private TextField usernameField = new TextField();
	private PasswordField passwordField = new PasswordField();
	private Button login = new Button("Login");
	private Button quit = new Button("Quit");
	private Stage loginStage = null;

	private ObjectProperty<MyWMS> app = new SimpleObjectProperty<>();
	final private ObjectBinding<Map<String,String>> parameters;
	
	private AnchorPane main = new AnchorPane();
	
	final ObjectProperty<BeanLocator> beanLocator = new SimpleObjectProperty<BeanLocator>() {
		protected void invalidated() {
			if (get() != null && app.get() != null) app.get().setBeanLocator(get());
		}
	};
	
  private final ChangeListener<Object> hideLoginStage = (v,o,n) -> hideStageOnComplete();

	private final ChangeListener<Throwable> errorMessageListener = (v,o,n) -> {
		if (n != null) {
			errorField.setText("Login Failed");
			wipeErrorField.playFromStart();
  		loginForm.setDisable(false);
		}
	};

  public LoginDialog(ObservableValue<MyWMS> app) {
  	this();
  	this.app.bind(app);
  }
  
  public LoginDialog(MyWMS app) {
  	this();
  	this.app.set(app);
  }
  	
  private LoginDialog() {
		super();
		this.parameters = Bindings.createObjectBinding(() -> {
			if (app.get() == null) {
				return Collections.emptyMap();
			}
			else {
				return app.get().getParameters().getNamed();
			}
		}, app);
		
		this.parameters.addListener((v,o,n) ->  {
			if (n != null) {
				String username = n.get("user");
				String password = n.get("password");
				
				if (!Strings.isEmpty(username) && !Strings.isEmpty(password)) {
					checkLogin(username, password);
				}
			}
		});
		
		app.addListener(this::onAppChanged);	
		
		String osUserName = System.getProperty("user.name");
		usernameField.setText(osUserName);
		usernameField.setPromptText("Username");
		usernameField.selectAll();

		passwordField.setPromptText("Password");

		wipeErrorField.setOnFinished(e -> errorField.setText(""));

		HBox buttons = new HBox(10, quit, login);
		buttons.setAlignment(Pos.BASELINE_RIGHT);
		login.setDefaultButton(true);
		login.setGraphic(new AwesomeIcon(AwesomeIcon.check));
		quit.setGraphic(new AwesomeIcon(AwesomeIcon.sign_out));
		login.setOnAction(e -> checkLogin(usernameField.getText(), passwordField.getText()));
		quit.setOnAction(e -> System.exit(0));

		loginForm.row()
			.fieldNode(errorField,GridPane.REMAINING,1)
		.end();
		loginForm.row()
			.fieldNode(new AwesomeIcon(AwesomeIcon.user)).fieldNode(usernameField)
		.end();
		loginForm.row()
			.fieldNode(new AwesomeIcon(AwesomeIcon.lock)).fieldNode(passwordField)
		.end();
		loginForm.row()
			.fieldNode(buttons, GridPane.REMAINING, 1)
		.end();
		//loginForm.setStyle("-fx-background-color: GREY");
		
		AnchorPane.setRightAnchor(loginForm, 5d);
		AnchorPane.setBottomAnchor(loginForm, 5d);
		AnchorPane.setTopAnchor(loginForm, 5d);
		
		URL splashURL = getClass().getResource("/splash.png");
		if (splashURL != null) {
			ImageView background = new ImageView(splashURL.toExternalForm());
			AnchorPane.setRightAnchor(background, 0d);
			AnchorPane.setBottomAnchor(background, 0d);
			AnchorPane.setTopAnchor(background, 0d);
			AnchorPane.setLeftAnchor(background, 0d);
			main.getChildren().add(background);
		}
		Label versionLabel = new Label("MyWMS v1.8.1");
		AnchorPane.setRightAnchor(versionLabel, Units.em(1));
		AnchorPane.setTopAnchor(versionLabel, Units.em(1));
		
		AnchorPane.clearConstraints(loginForm);
		AnchorPane.setRightAnchor(loginForm, Units.em(1));
		AnchorPane.setBottomAnchor(loginForm, Units.em(3));

		main.getChildren().add(loginForm);
		main.getChildren().add(versionLabel);		
	}
  
  public void hideStageOnComplete() {
  	MyWMS appValue = app.get();
  	if (appValue == null) return;
  	if (appValue.getPrimaryStage() == null) return;
  	if (!appValue.getPrimaryStage().isShowing()) return;
  	if (appValue.getLoginService() == null) return; 
  	loginStage.hide();  	
  }
  
	private void onAppChanged(Observable v, MyWMS o, MyWMS n) {
		if (o != null) {
			o.loginServiceProperty().removeListener(hideLoginStage);
			o.loginErrorProperty().removeListener(errorMessageListener);
		}

  	if (n != null) {
			n.loginServiceProperty().addListener(new WeakChangeListener<>(hideLoginStage));
			n.loginErrorProperty().addListener(new WeakChangeListener<>(errorMessageListener));
		}
  }

  public void showLogin(Stage stage) {
  	showLogin(stage, null);
  }

  public void showLogin(Stage stage, ProgressBar progress) {
  	if (loginStage != null) throw new IllegalStateException("Second call to showLogin");
  	loginStage = stage;
  	loginStage.setScene(new Scene(main));
 		loginStage.initStyle(StageStyle.UNDECORATED);
 		loginStage.getScene().getStylesheets().addAll(R.getDefaultStyleSheets());
 		loginStage.show();
 		loginStage.centerOnScreen();
 		loginStage.requestFocus();  
 		loginStage.toFront();
 		loginStage.setAlwaysOnTop(true);
 		
 		if (progress != null) {
 			AnchorPane.clearConstraints(progress);
 			AnchorPane.setLeftAnchor(progress, Units.em(3));
 			AnchorPane.setRightAnchor(progress, Units.em(3));
 			AnchorPane.setBottomAnchor(progress, Units.em(1));
 			main.getChildren().add(progress);
 		}
  }


  private void checkLogin(String user, String pass) {
		loginForm.setDisable(true);
		FXUtils.executeOnceWhenPropertyIsNonNull(this.parameters, p -> {
			doLogin(p, user, pass);
		});
  }
  
  private void doLogin(Map<String,String> params, String user, String pass) {
		String server = params.getOrDefault("server", "localhost");
  	Integer port;
  	
  	try {
			port = Integer.parseInt(params.get("port"));
		} 
  	catch (NumberFormatException e1) {
  		port = 8080;
		}

  	String myServer = server;
  	int myPort = port;
  	
		CompletableFuture.supplyAsync(() -> lookupBeanLocator(myServer, myPort, user, pass))
    .exceptionally(e -> { 
			System.out.println(MLogger.format(e));
			System.err.println(MLogger.format(e));
    	FXErrors.exception(e);
    	loginForm.setDisable(false);
    	return null; 
    })
    .thenAcceptAsync(b -> {
    	if (b != null) {
    		beanLocator.set(b);	
    		//loginForm.setDisable(false);
    	}
    	else {
    		FXErrors.error("Login failed", "Login failed");
    		loginForm.setDisable(false);
    	}
    }, Platform::runLater);
	}
	
	public static BeanLocator lookupBeanLocator(String server, int port, String username, String password) {
		// if you want to load the config from a file. 
		//Properties jndi = AppPreferences.loadFromClasspath("/config/wf8-context.properties");
		//Properties appserver = AppPreferences.loadFromClasspath("/config/appserver.properties");
		
		Properties jndi = new Properties();
		jndi.setProperty("org.mywms.env.as.vendor", "jboss");
		jndi.setProperty("org.mywms.env.as.version", "8.2");

		jndi.setProperty("remote.connections", "default");
		jndi.setProperty("remote.connection.default.port", Integer.toString(port));
		jndi.setProperty("remote.connection.default.host", server);
		jndi.setProperty("remote.connection.default.connect.timeout","1500");

		jndi.setProperty("remote.connection.default.connect.options.org.xnio.Options.SASL_POLICY_NOANONYMOUS", "true");
		jndi.setProperty("remote.connection.default.connect.options.org.xnio.Options.SASL_POLICY_NOPLAINTEXT", "false");
		jndi.setProperty("remote.connection.default.connect.options.org.xnio.Options.SASL_DISALLOWED_MECHANISMS", "JBOSS-LOCAL-USER");
		jndi.setProperty("remote.connectionprovider.create.options.org.xnio.Options.SSL_ENABLED", "false");

		Properties appserver = new Properties();
		appserver.setProperty("org.mywms.env.applicationName", "los.reference");
		appserver.setProperty("org.mywms.env.mapping.project-ejb3", "de.linogistix.los.reference,de.linogistix.los.common.facade.VersionFacade");
		appserver.setProperty("org.mywms.env.mapping.myWMS-comp", "org.mywms");
		appserver.setProperty("org.mywms.env.mapping.los.stocktaking-comp", "de.linogistix.los.stocktaking");
		appserver.setProperty("org.mywms.env.mapping.los.mobile-comp", "de.linogistix.mobileserver");
		appserver.setProperty("org.mywms.env.mapping.los.mobile","de.linogistix.mobile.common, de.linogistix.mobile.processes");
		appserver.setProperty("org.mywms.env.mapping.los.location-comp", "de.linogistix.los.location");
		appserver.setProperty("org.mywms.env.mapping.los.inventory-ws", "de.linogistix.los.inventory.ws");
		appserver.setProperty("org.mywms.env.mapping.los.inventory-comp", "de.linogistix.los.inventory");
		appserver.setProperty("org.mywms.env.mapping.los.common-comp", "de.linogistix.los.common,de.linogistix.los.crud,de.linogistix.los.customization,de.linogistix.los.entityservice,de.linogistix.los.query,de.linogistix.los.report,de.linogistix.los.runtime,de.linogistix.los.user,de.linogistix.los.util");

		BeanLocator b = new BeanLocator(username, password, jndi, appserver);
		return b;
	}
  
}
