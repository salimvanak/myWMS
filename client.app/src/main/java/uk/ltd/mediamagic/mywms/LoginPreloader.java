package uk.ltd.mediamagic.mywms;

import javafx.application.Preloader;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.ProgressBar;
import javafx.stage.Stage;
import uk.ltd.mediamagic.mywms.MyWMS.StageVisibleNotification;

public class LoginPreloader extends Preloader {
	//private final Logger log = MLogger.log(this);

	Stage stage = null;
	ProgressBar bar = new ProgressBar();

	private ObjectProperty<MyWMS> app = new SimpleObjectProperty<>();
	private final LoginDialog loginDialog = new LoginDialog(app);

	@Override
	public void start(Stage stage) throws Exception {
		this.stage = stage;

		loginDialog.showLogin(stage, bar);
	}

	@Override
	public void handleProgressNotification(ProgressNotification pn) {
		bar.setProgress(pn.getProgress());
		if (pn.getProgress() > 0 && pn.getProgress() < 1.0) {
			bar.setVisible(true);
		}
	}

	@Override
	public void handleApplicationNotification(PreloaderNotification info) {
		super.handleApplicationNotification(info);
		if (info instanceof StageVisibleNotification) {
			if (stage != null && stage.isShowing()) stage.toFront();
			if (app.get().getLoginService() != null) stage.hide();
			//application is loaded => hide progress bar
			bar.setVisible(false);
			loginDialog.hideStageOnComplete();
		}
	}
	
	@Override
	public boolean handleErrorNotification(ErrorNotification info) {
		super.handleErrorNotification(info);
		return true;
	}
	
	@Override
	public void handleStateChangeNotification(StateChangeNotification evt) {
		if (evt.getType() == StateChangeNotification.Type.BEFORE_INIT) {
			app.set((MyWMS) evt.getApplication());
		}
	}    
}