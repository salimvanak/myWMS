package uk.ltd.mediamagic.mywms;

import java.util.Properties;
import java.util.concurrent.CompletableFuture;

import org.mywms.ejb.BeanLocator;

import javafx.application.Platform;
import javafx.application.Preloader;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.beans.value.WeakChangeListener;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class LoginPreloader extends Preloader {
    public static interface CredentialsConsumer {
        public void setCredential(String user, String password);
    }
   
    public static final boolean DEBUG = true;
    
    //private final Logger log = MLogger.log(this);
    
    Stage stage = null;
    ProgressBar bar = null;
    MyWMS consumer = null;

    private final BooleanProperty disableLoginBox = new SimpleBooleanProperty();
    
    final Button button = new Button("Log in");
    final TextField userNameBox = new TextField();
    final PasswordField passwordBox = new PasswordField();

    private Scene createLoginScene() {
        VBox vbox = new VBox();
        
        userNameBox.setPromptText("Username");
        vbox.getChildren().add(userNameBox);
        
        passwordBox.setPromptText("Password");
        vbox.getChildren().add(passwordBox);
        
        vbox.disableProperty().bind(disableLoginBox);
        
        
        button.setOnAction(new EventHandler<ActionEvent>(){
            public void handle(ActionEvent t) {
                // Save credentials
            		disableLoginBox.set(true);
                checkLogin();
            }
        });
        vbox.getChildren().add(button);
        
        bar = new ProgressBar(0);
        vbox.getChildren().add(bar);
        bar.setVisible(false);
        
        if (DEBUG) {
        	userNameBox.setText("slim");
        	passwordBox.setText("god1rifewo");
      		disableLoginBox.set(true);
        	checkLogin();
        }

        Scene sc = new Scene(vbox, 200, 200);
       
        return sc;
    }
    
  	public BeanLocator lookupBeanLocator(String username, String password) {
  		// if you want to load the config from a file.
  		//Properties jndi = AppPreferences.loadFromClasspath("/config/wf8-context.properties");
  		//Properties appserver = AppPreferences.loadFromClasspath("/config/appserver.properties");
  		
  		Properties jndi = new Properties();
  		jndi.setProperty("org.mywms.env.as.vendor", "jboss");
  		jndi.setProperty("org.mywms.env.as.version", "8.2");

  		jndi.setProperty("remote.connections", "default");
  		jndi.setProperty("remote.connection.default.port","8080");
  		jndi.setProperty("remote.connection.default.host", "localhost");
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
    
    public void checkLogin() {
    	if (consumer == null) return;
    	if (!disableLoginBox.get()) return;
    	
      String username = userNameBox.getText();
      String password = passwordBox.getText();
      
      CompletableFuture.supplyAsync(() -> lookupBeanLocator(username, password))
      .thenAcceptAsync(b -> {
      	if (b != null) {
      		consumer.setBeanLocator(b);	
      		disableLoginBox.set(false);
      	}
      	else {
      		disableLoginBox.set(false);
      	}
      }, Platform::runLater);
      
      // Do not allow any further edits
    }
    
    @Override
    public void start(Stage stage) throws Exception {
        this.stage = stage;
        stage.setAlwaysOnTop(true);
        stage.setScene(createLoginScene());
        stage.show();
    }
    
    @Override
    public void handleProgressNotification(ProgressNotification pn) {
        bar.setProgress(pn.getProgress());
        if (pn.getProgress() > 0 && pn.getProgress() < 1.0) {
            bar.setVisible(true);
        }
    }
     
    private ChangeListener<Object> hideStageListener = new ChangeListener<Object>() {
    	public void changed(ObservableValue<? extends Object> observable, Object oldValue, Object newValue) {
      	if (newValue != null) {
      		Platform.runLater(stage::hide);
      		observable.removeListener(hideStageListener);
      	}
    	}
    };
    
    @Override
    public void handleStateChangeNotification(StateChangeNotification evt) {
        if (evt.getType() == StateChangeNotification.Type.BEFORE_START) {
            //application is loaded => hide progress bar
            bar.setVisible(false);
            
            consumer = (MyWMS) evt.getApplication();
            consumer.loginServiceProperty().addListener(new WeakChangeListener<>(hideStageListener));

            checkLogin();
        }
    }    
}