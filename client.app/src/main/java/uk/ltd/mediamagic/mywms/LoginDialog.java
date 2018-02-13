package uk.ltd.mediamagic.mywms;

import java.net.URL;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.CompletableFuture;

import org.mywms.ejb.BeanLocator;

import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
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
import uk.ltd.mediamagic.fx.AwesomeIcon;
import uk.ltd.mediamagic.fx.Units;
import uk.ltd.mediamagic.fx.control.SimpleFormBuilder;
import uk.ltd.mediamagic.fx.flow.FXErrors;

public class LoginDialog {

	private SimpleFormBuilder loginForm = new SimpleFormBuilder();
	private TextField usernameField = new TextField();
	private PasswordField passwordField = new PasswordField();
	private Button login = new Button("Login");
	private Button quit = new Button("Quit");
	private Map<String,String> parameters;
	private Stage loginStage = new Stage();
	
	private AnchorPane main = new AnchorPane();
	
	final ObjectProperty<BeanLocator> beanLocator = new SimpleObjectProperty<>();
	
  public LoginDialog(MyWMS app) {
		super();
		this.parameters = app.getParameters().getNamed();
		loginStage.initOwner(app.getPrimaryStage());
		
		beanLocator.addListener((v,o,n) -> { 
			if (n != null) {
				app.setBeanLocator(n);
			}
		});
		
		app.loginServiceProperty().addListener((v,o,n) -> {
			if (n != null) loginStage.hide();
		});
		
		String osUserName = System.getProperty("user.name");
		usernameField.setText(osUserName);
		usernameField.setPromptText("Username");
		usernameField.selectAll();

		passwordField.setPromptText("Password");

		Label errorField = new Label();
		PauseTransition wipeErrorField = new PauseTransition(Duration.seconds(3));
		wipeErrorField.setOnFinished(e -> errorField.setText(""));
		app.loginErrorProperty().addListener((v,o,n) -> {
			if (n != null) {
				errorField.setText("Login Failed");
				wipeErrorField.playFromStart();
			}
		});

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
		AnchorPane.setBottomAnchor(versionLabel, Units.em(1));
		
		AnchorPane.clearConstraints(loginForm);
		AnchorPane.setRightAnchor(loginForm, Units.em(1));
		AnchorPane.setBottomAnchor(loginForm, Units.em(3));

		main.getChildren().add(loginForm);
		main.getChildren().add(versionLabel);
		
	}

  public void showLogin(Stage stage) {
  	String username = parameters.get("user");
  	String password = parameters.get("password");
  
  	if (!Strings.isEmpty(username) && !Strings.isEmpty(password)) {
  		checkLogin(username, password);
  	}
  	else if (!loginStage.isShowing()) {
  		loginStage.initOwner(stage);
  		loginStage.setScene(new Scene(main));
  		loginStage.initStyle(StageStyle.UNDECORATED);
  		loginStage.getScene().getStylesheets().addAll(R.getDefaultStyleSheets());
  		loginStage.show();
  		loginStage.centerOnScreen();
  		loginStage.requestFocus();  
  		loginStage.toFront();
  		//loginStage.setAlwaysOnTop(true);
  	}
  }


  private void checkLogin(String user, String pass) {
		loginForm.setDisable(true);

		String server = parameters.get("server");
		if (Strings.isEmpty(server)) server = "localhost";
  	Integer port;
  	
  	try {
			port = Integer.parseInt(parameters.get("port"));
		} 
  	catch (NumberFormatException e1) {
  		port = 8080;
		}

  	String myServer = server;
  	int myPort = port;
  	
		CompletableFuture.supplyAsync(() -> lookupBeanLocator(myServer, myPort, user, pass))
//    .exceptionally(e -> { 
//    	FXErrors.exception(e);
//    	loginForm.setDisable(false);
//    	return null; 
//    })
    .thenAcceptAsync(b -> {
    	if (b != null) {
    		beanLocator.set(b);	
    		loginForm.setDisable(false);
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
