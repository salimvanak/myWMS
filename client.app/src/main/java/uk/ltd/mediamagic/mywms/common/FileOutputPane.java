package uk.ltd.mediamagic.mywms.common;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;

import javax.swing.SwingUtilities;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.event.Event;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Screen;
import javafx.stage.Stage;
import res.R;
import uk.ltd.mediamagic.debug.MLogger;
import uk.ltd.mediamagic.fx.AwesomeIcon;
import uk.ltd.mediamagic.fx.FxExceptions;
import uk.ltd.mediamagic.fx.Utils;
import uk.ltd.mediamagic.fx.binding.MBindings;
import uk.ltd.mediamagic.fx.flow.ContextBase;
import uk.ltd.mediamagic.fx.flow.FXErrors;
import uk.ltd.mediamagic.plugin.ExtensionPoint;
import uk.ltd.mediamagic.plugin.PluginRegistry;
import uk.ltd.mediamagic.util.Constants.PrintFlavour;

public class FileOutputPane extends BorderPane {

	private final ObjectProperty<File> file = new SimpleObjectProperty<File>() {
		protected void invalidated() {
			requestLayout();
		};
	};
	public final ObjectProperty<File> fileProperty() { return file; }
	public final void setFile(File file) { this.file.set(file); }
	public final File getFile() { return file.get(); }
	
	private final StringProperty message = new SimpleStringProperty();
	public final StringProperty messageProperty() { return message; }
	
	public final DoubleProperty progressProperty() {
		return pi.progressProperty();
	}
	
	private ProgressIndicator pi = new ProgressIndicator();
	
	private ObjectBinding<Node> icon = Bindings.createObjectBinding(this::getIcon, file);
	private StringBinding fileName = MBindings.asString(file, File::getName);

	public FileOutputPane() {
		setPadding(new Insets(10));
		
		pi.setPrefSize(50, 50);
		
		Label fileNameLabel = new Label();
		BorderPane.setAlignment(fileNameLabel, Pos.CENTER);
		fileNameLabel.setAlignment(Pos.CENTER);
		fileNameLabel.textProperty().bind(Bindings.when(file.isNull()).then(message).otherwise(fileName));
		
		setOnMouseClicked(this::openFile);
		setOnDragDetected(this::startDragFile);
		setOnDragDone(this::doneDragFile);
		setBottom(fileNameLabel);
		centerProperty().bind(icon);				
	}
		
	private void openFile(Event e) {
		File file = getFile();
		if (file == null) return;
		if (!Desktop.isDesktopSupported()) return;
		if (e instanceof MouseEvent && ((MouseEvent)e).getClickCount() != 2) return;

		SwingUtilities.invokeLater(() -> {
			try {
				Desktop.getDesktop().open(file);
			} 
			catch (IOException ex) {
				FXErrors.exception(this, "Unable to open file", ex);
			}
		});
	}

	private void printFile(Event e) {
		File file = getFile();
		if (file == null) return;
		if (!Desktop.isDesktopSupported()) return;
		if (e instanceof MouseEvent && ((MouseEvent)e).getClickCount() != 2) return;

		SwingUtilities.invokeLater(() -> {
			try {
				Desktop.getDesktop().print(file);
			} 
			catch (IOException ex) {
				FXErrors.exception(this, "Unable to open file", ex);
			}
		});
	}

	private void openFolder(Event e) {
		File file = getFile();
		if (file == null) return;
		if (!Desktop.isDesktopSupported()) return;
		if (e instanceof MouseEvent && ((MouseEvent)e).getClickCount() != 2) return;

		SwingUtilities.invokeLater(() -> {
			try {
				Desktop.getDesktop().open(file.getParentFile());
			} 
			catch (IOException ex) {
				FXErrors.exception(this, "Unable to open file", ex);
			}
		});
	}

	private void startDragFile(MouseEvent me) {
    Dragboard db = startDragAndDrop(TransferMode.ANY);
    db.setDragView(snapshot(new SnapshotParameters(), null));

    ClipboardContent filesToCopyClipboard = new ClipboardContent();
    filesToCopyClipboard.putFiles(Collections.singletonList(getFile()));
    db.setContent(filesToCopyClipboard);
    me.consume();		
	}
	
	public void doneDragFile(DragEvent me) {
    me.consume();
	}
	
	public Node getIcon() {
		File file = getFile();
		if (file == null) return pi;

		String name = file.getName();
		if (name.endsWith(".pdf")) return new AwesomeIcon(AwesomeIcon.file_pdf_alt, AwesomeIcon.XLARGE_SIZE);			
		else if (name.endsWith(".csv")) return new AwesomeIcon(AwesomeIcon.file_excel_alt, AwesomeIcon.XLARGE_SIZE);			
		else if (name.endsWith(".txt")) return new AwesomeIcon(AwesomeIcon.file_text, AwesomeIcon.XLARGE_SIZE);			
		else if (name.endsWith(".xml")) return new AwesomeIcon(AwesomeIcon.file_text_alt, AwesomeIcon.XLARGE_SIZE);			
		else if (name.endsWith(".zip")) return new AwesomeIcon(AwesomeIcon.file_zip_alt, AwesomeIcon.XLARGE_SIZE);			
		else if (name.endsWith(".gz")) return new AwesomeIcon(AwesomeIcon.file_zip_alt, AwesomeIcon.XLARGE_SIZE);			
		else {
			return new AwesomeIcon(AwesomeIcon.file_alt, AwesomeIcon.XLARGE_SIZE);			
		}
	}
	
	public static void show(String title, ObservableValue<File> file) {
		show(title, file, null, null);
	}
	
	public static void show(String title, ContextBase context, Task<File> task) {
		show(title, task.valueProperty(), context, task);
	}
	
	public static void show(String title, ObservableValue<File> file, ContextBase context, Task<?> task) {
		// listen for exceptions.
		task.exceptionProperty().addListener((v,o,n) -> FxExceptions.exceptionThrown(n));

		Stage stage = new Stage();
		stage.setTitle(title);
		
		FileOutputPane fo = new FileOutputPane();
		fo.fileProperty().bind(file);
		if (task != null) {
			fo.progressProperty().bind(task.progressProperty());
			fo.messageProperty().bind(task.messageProperty());
		}

		Button open = new Button("_Open");
		open.disableProperty().bind(fo.fileProperty().isNull());
		open.setOnAction(fo::openFile);

		Button openFolder = new Button("_Show in folder");
		openFolder.disableProperty().bind(fo.fileProperty().isNull());
		openFolder.setOnAction(fo::openFolder);

		Button print = new Button("_Print");
		print.disableProperty().bind(fo.fileProperty().isNull());
		print.setOnAction(fo::printFile);

		HBox buttons = new HBox(open, openFolder, print);

		if (context != null) {
			List<FileOutputEP> plugins = PluginRegistry.getExtentionPoints(FileOutputEP.class);
			MLogger.log(FileOutputPane.class).log(Level.INFO, "FileOutputPane found plugins " + plugins.size());
			for(FileOutputEP ep : plugins) {
				Button button = new Button(ep.getName());
				button.setMaxHeight(Double.MAX_VALUE);
				button.setGraphic(ep.getIcon());
				button.disableProperty().bind(fo.fileProperty().isNull());
				button.setOnAction(e -> {
					if (ep.accept(fo, context, file.getValue())) {
						stage.close();
					}
				});			
				buttons.getChildren().add(button);
			}
		}
		
		Button close = new Button("_Close");
		close.setOnAction(e -> {
			if (task != null && !task.isDone()) {
				if (task.cancel(true)) stage.hide();
			}
			else {
				stage.hide();
			}
		});
		buttons.getChildren().add(close);
				
		buttons.setAlignment(Pos.CENTER);
		buttons.setPadding(new Insets(0, 10, 10, 10));
		buttons.setSpacing(10);
		
		VBox content = new VBox(fo, buttons);
		
		stage.setScene(new Scene(content));
  	

		stage.setY(0);
		stage.widthProperty().addListener(new ChangeListener<Number>() {
			int count = 0;
			public void changed(javafx.beans.value.ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				if (Double.isNaN(oldValue.doubleValue()) || oldValue.doubleValue() < 10d) {
					Screen screen = Utils.getScreenForPoint(stage.getX(), stage.getY());
					Rectangle2D screenBounds = screen.getBounds();
					double xPos = screenBounds.getWidth()-newValue.doubleValue();
					stage.setX(xPos);
				}
				else {
					count ++;
				}
				if (count > 10) observable.removeListener(this);
			}
		});
		stage.getScene().getStylesheets().addAll(R.getStyleSheets());
		stage.show();
	}	
	
	public static interface FileOutputEP extends ExtensionPoint {
		public String getName();
		public Node getIcon();
		public PrintFlavour getPrintFlavour();
		public boolean accept(Parent source, ContextBase context, File file);
	}
	
}
