package uk.ltd.mediamagic.flow.crud;

import java.lang.reflect.UndeclaredThrowableException;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.mywms.model.BasicEntity;

import de.linogistix.los.query.BODTO;
import de.linogistix.los.query.BusinessObjectQueryRemote;
import de.linogistix.los.query.QueryDetail;
import javafx.application.Platform;
import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ListProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.AccessibleAttribute;
import javafx.scene.AccessibleRole;
import javafx.scene.Scene;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.Skin;
import javafx.scene.control.SkinBase;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.TextFieldListCell;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.stage.Popup;
import javafx.stage.PopupWindow;
import javafx.util.Callback;
import javafx.util.Duration;
import javafx.util.StringConverter;
import uk.ltd.mediamagic.fx.AwesomeIcon;
import uk.ltd.mediamagic.fx.concurrent.MExecutor;
import uk.ltd.mediamagic.fx.control.DelayedChangeListener;
import uk.ltd.mediamagic.fx.controller.list.CellWrappers;
import uk.ltd.mediamagic.fx.converters.ToStringConverter;
import uk.ltd.mediamagic.fx.flow.ContextBase;
import uk.ltd.mediamagic.fx.flow.Flow;
import uk.ltd.mediamagic.mywms.BeanDirectory;
import uk.ltd.mediamagic.mywms.common.Editor;
import uk.ltd.mediamagic.plugin.PluginRegistry;

/**
 * Creates a ComboBox style editor for Objects extending BasicEntity.
 * The Edit format the item using the string converter and will show
 * completion result in a list view when the ComboBox is opened.
 * @author slim
 *
 * @param <T>
 */
public class BasicEntityEditor<T extends BasicEntity> extends Control {
	final private Logger log = MLogger.log(this);

	final private ObjectProperty<T> value = new SimpleObjectProperty<>(this, "value");
	/** The value selected by this control */
	public final ObjectProperty<T> valueProperty() { return this.value; }
	public final T getValue() {	return this.valueProperty().get(); }
	public final void setValue(final T value) {	this.valueProperty().set(value); }

	private ObjectProperty<StringConverter<T>> converter = new SimpleObjectProperty<>();
	/** A String converter for the text representation of the value */
	public final ObjectProperty<StringConverter<T>> converterProperty() { return this.converter; }
	public final StringConverter<T> getConverter() { return this.converterProperty().get(); }
	public final void setConverter(final StringConverter<T> converter) { this.converterProperty().set(converter); }

	private ObjectProperty<Function<String, CompletableFuture<List<BODTO<T>>>>> fetchCompleteions = new SimpleObjectProperty<>();	
	/** A callback for fetching completions for a given search string */
	public final ObjectProperty<Function<String, CompletableFuture<List<BODTO<T>>>>> fetchCompleteionsProperty() {	return this.fetchCompleteions; }
	public final Function<String, CompletableFuture<List<BODTO<T>>>> getFetchCompleteions() { return this.fetchCompleteionsProperty().get(); }
	public final  void setFetchCompleteions(final Function<String, CompletableFuture<List<BODTO<T>>>> fetchCompleteions) { this.fetchCompleteionsProperty().set(fetchCompleteions); }

	private ObjectProperty<Function<BODTO<T>, CompletableFuture<T>>> fetchValue = new SimpleObjectProperty<>();
	/** A callback to Look up a value of type <code>T</code> from a <code>BODOT<T></code> */
	public final ObjectProperty<Function<BODTO<T>, CompletableFuture<T>>> fetchValueProperty() {	return this.fetchValue; }
	public final Function<BODTO<T>,CompletableFuture<T>> getFetchValue() { return this.fetchValueProperty().get(); }
	public final  void setFetchValue(final Function<BODTO<T>,CompletableFuture<T>> fetchValue) { this.fetchValueProperty().set(fetchValue); }

	private ObjectProperty<Consumer<T>> valueEditor = new SimpleObjectProperty<>();
	/** A callback to edit a value of type <code>T</code>*/
	public final ObjectProperty<Consumer<T>> valueEditorProperty() { return this.valueEditor;	}
	public final Consumer<T> getValueEditor() {	return this.valueEditorProperty().get(); }
	public final void setValueEditor(final Consumer<T> editValue) {	this.valueEditorProperty().set(editValue); }

	private ObjectProperty<Consumer<T>> valueViewer = new SimpleObjectProperty<>();
	/** A callback to display a read only viewer a value of type <code>T</code>*/
	public final ObjectProperty<Consumer<T>> valueViewerProperty() { return this.valueViewer; }
	public final Consumer<T> getValueViewer() { return this.valueViewerProperty().get(); }
	public final void setValueViewer(final Consumer<T> viewValue) { this.valueViewerProperty().set(viewValue); }

	private ObjectProperty<Callback<ListView<BODTO<T>>, ListCell<BODTO<T>>>> cellFactory = new SimpleObjectProperty<>();
	/** A callback generate a list cell for the auto completion list*/
	public final ObjectProperty<Callback<ListView<BODTO<T>>, ListCell<BODTO<T>>>> cellFactoryProperty() {	return this.cellFactory; }
	public final Callback<ListView<BODTO<T>>, ListCell<BODTO<T>>> getCellFactory() { return this.cellFactoryProperty().get(); }
	public final  void setCellFactory(final Callback<ListView<BODTO<T>>, ListCell<BODTO<T>>> cellFactory) { this.cellFactoryProperty().set(cellFactory); }

	final private ReadOnlyBooleanWrapper showPopup = new ReadOnlyBooleanWrapper(false);

	public static <T extends BasicEntity> BasicEntityEditor<T> create(ContextBase context, Class<T> boClass) {
		BasicEntityEditor<T> e = new BasicEntityEditor<T>();
		e.configure(context, boClass);
		return e;
	}
	
	public BasicEntityEditor() {
		super();
		getStyleClass().addAll("combo-box-base", "combo-box");
		setConverter(ToStringConverter.of(BasicEntity::toUniqueString));
	} 

	/**
	 * Attempts to automatically assign the <code>valueEditor</code>, <code>valueViewer</code>, 
	 * <code>fetchValue</code> and <code>fetchCompletions</code>
	 * properties.
	 * @param context
	 * @param boClass
	 */
	public void configure(ContextBase context, Class<T> boClass) {
		try {
			Class<? extends BusinessObjectQueryRemote<T>> queryClass = BeanDirectory.getQuery(boClass);
			BusinessObjectQueryRemote<T> query = context.getBean(queryClass);
			MExecutor exec = context.getBean(MExecutor.class);
			if (getFetchCompleteions() == null) {
				setFetchCompleteions(s -> {
					QueryDetail qd = new QueryDetail(0, 100);
					qd.addOrderByToken("modified", false);
					return exec.call(() -> query.autoCompletion(s, qd));
				});
			}
			if (getFetchValue() == null) {
				setFetchValue(bto -> exec.call(() -> query.queryById(bto.getId())));
			}
		}
		catch (UndeclaredThrowableException e) {
			if (e.getUndeclaredThrowable() instanceof ClassNotFoundException) {
				log.log(Level.SEVERE, "No query bean available, not completion will be possible for " + boClass.getName());
			}
			else {
				throw e; 
			}
		}
	
		@SuppressWarnings("unchecked")
		Editor<T> editorPlugin = PluginRegistry.getPlugin(boClass, Editor.class);
		if (editorPlugin != null) {
			Callback<ListView<BODTO<T>>, ListCell<BODTO<T>>> cellFactory = CellWrappers.forList(editorPlugin.createTOCellFactory());
			if (cellFactory == null) cellFactory = TextFieldListCell.forListView(ToStringConverter.of(BODTO::getName));
			setCellFactory(cellFactory);
			setValueEditor(d -> editorPlugin.edit(context, boClass, d.getId()));
			setValueViewer(d -> {
				Flow flow = context.getBean(Flow.class);
				Objects.requireNonNull(flow, "Cannot find flow");
				editorPlugin.view(context, flow, boClass, d.getId());
			});
		}
		else {
			setCellFactory(TextFieldListCell.forListView(ToStringConverter.of(BODTO::getName)));
			log.warning(()->"No editor found for " + boClass.getName());
		}
	
	}

	void editValue() {
		T value = getValue();
		Consumer<T> edit = getValueEditor();
		if (value != null && edit != null) edit.accept(value);
	}

	void viewValue() {
		T value = getValue();
		Consumer<T> view = getValueViewer();
		if (value != null && view != null) view.accept(value);
	}

	public void showPopup() {
		if (getSkin() != null) ((BasicEntityEditorSkin<?>)getSkin()).showPopup();
	}

	public void hidePopup() {
		if (getSkin() != null) ((BasicEntityEditorSkin<?>)getSkin()).hidePopup();
	}

	public ReadOnlyBooleanProperty showPopupProperty() {
		return showPopup.getReadOnlyProperty();
	}

	protected void commitValue(BODTO<T> bodto) {
		if (bodto == null) {
			setValue(null);
		}
		else {
			Function<BODTO<T>,CompletableFuture<T>> getValue = getFetchValue();
			if (getValue != null) {
				getValue.apply(bodto)
				.thenAcceptAsync(this::setValue, Platform::runLater);
			}
		}
	}

	@Override
	protected Skin<?> createDefaultSkin() {
		return new BasicEntityEditorSkin<>(this);
	}

	public static class BasicEntityEditorSkin<T extends BasicEntity> extends SkinBase<BasicEntityEditor<T>> {
		private Label valueField = new Label();
		private TextField textField = new TextField();
		private AwesomeIcon arrowButton = new AwesomeIcon(AwesomeIcon.caret_down, AwesomeIcon.SMALL_SIZE);
	
		private PopupWindow popup;
	
		final private BasicEntityEditor<T> control;
	
		final private ListProperty<BODTO<T>> completionItems = new SimpleListProperty<>(FXCollections.observableArrayList());
	
		final private DelayedChangeListener<String> textInput = new DelayedChangeListener<>(Duration.millis(250), this::loadList);
	
		public BasicEntityEditorSkin(BasicEntityEditor<T> control) {
			super(control);
			this.control = control;
			this.control.showPopupProperty().addListener((v, o, n) -> {
				if (n) showPopup(); else hidePopup();
			});
	
			this.control.valueProperty().addListener(this::updateTextField);
			this.control.converterProperty().addListener(this::updateTextField);
	
			if (control.showPopupProperty().get()) showPopup(); else hidePopup();
	
			textField.textProperty().addListener(textInput);
			//textField.setManaged(false);
			textField.setEditable(true);
	
			valueField.setMouseTransparent(true);
			// As long as the screen-reader is concerned this node is not a list item.
			// This matters because the screen-reader counts the number of list item
			// within combo and speaks it to the user.
			valueField.setAccessibleRole(AccessibleRole.NODE);
			valueField.setAlignment(Pos.BASELINE_LEFT);
			valueField.setMaxWidth(Integer.MAX_VALUE);
			HBox.setHgrow(valueField, Priority.ALWAYS);
			valueField.textProperty().bind(Bindings.createObjectBinding(()-> {
				StringConverter<T> conv = control.converter.get();
				return conv.toString(control.getValue());
			}, control.converter, control.value));
	
			control.setOnKeyReleased(e -> {
				if (e.getCode() == KeyCode.DOWN) {
					showPopup();
				}
				else if (e.getCode() == KeyCode.F7) {
					if (e.isControlDown()) {
						hidePopup();
						control.editValue();
					}
					else {
						hidePopup();
						control.viewValue();
					}
				}
			});
	
			arrowButton.getStyleClass().addAll("arrow-button");
	
			valueField.visibleProperty().bind(control.showPopupProperty().not());
			textField.visibleProperty().bind(control.showPopupProperty());
	
			HBox hbox = new HBox();
			hbox.getStyleClass().remove("hbox");
			hbox.setOnMouseClicked(e -> onArrowClicked());
			hbox.setAlignment(Pos.BASELINE_LEFT);
			valueField.setStyle("-fx-padding: 0.5em 0.8ex");			
			StackPane sp = new StackPane(valueField, textField);
			
			//HBox.setHgrow(valueField, Priority.ALWAYS);
			//HBox.setHgrow(textField, Priority.ALWAYS);
			HBox.setHgrow(sp, Priority.ALWAYS);
			HBox.setHgrow(arrowButton, Priority.NEVER);
			HBox.setMargin(arrowButton, new Insets(5));
			hbox.getChildren().addAll(sp, arrowButton);
			getChildren().add(hbox);
	
		}
	
		public void updateTextField(Observable o) {
			StringConverter<T> conv = control.getConverter();
			if (conv == null) {
				T value = control.getValue();
				textField.setText((value == null) ? "" : value.toUniqueString());				
			}
			else {
				textField.setText(conv.toString(control.getValue()));
			}
		}
	
		protected PopupWindow createPopup() {
			ListView<BODTO<T>> listView = new ListView<>();
			listView.getStyleClass().addAll("combo-box-popup");
			listView.setItems(completionItems);
			listView.cellFactoryProperty().bind(control.cellFactoryProperty());
			listView.setOnKeyReleased(e -> { 
				if (e.getCode() == KeyCode.ENTER ) { select(listView); e.consume(); }
				if (e.getCode() == KeyCode.ESCAPE ) { hidePopup(); e.consume(); }
			});
	
			listView.setOnMouseClicked(e -> { if (e.getClickCount() == 1) select(listView); e.consume();});
	
			Popup popup = new Popup();
			control.showPopup.bind(popup.showingProperty());
			popup.getContent().add(listView);
			textInput.updateValue("");
			return popup;
		}
	
		public void select(ListView<BODTO<T>> tView) {
			BODTO<T> t = tView.getSelectionModel().getSelectedItem();
			if (t != null) {
				control.commitValue(t);
				hidePopup();
			}
		}
	
		private void onArrowClicked() {
			if (control.showPopup.get()) {
				control.hidePopup();
			}
			else {
				control.showPopup();
			}
		}
	
		public void loadList(String s) {
			//if (!Strings.isEmpty(s)) {
			//System.out.println("************ FETCHING " + s);
				Function<String, CompletableFuture<List<BODTO<T>>>> get = control.getFetchCompleteions();
				if (get != null) {
					completionItems.clear();
					get.apply(s).thenAcceptAsync(l -> completionItems.addAll(l), Platform::runLater);
				}
				else {
					completionItems.clear();
				}
			//}
			//else {
			//	completionItems.clear();
			//}
		}
	
		public boolean isPopupShowing() {
			return (popup != null) && popup.isShowing();
		}
	
		public void showPopup() {
			if (popup == null) popup = createPopup();
			if (popup.isShowing()) return;
			final Scene scene = control.getScene();
			final Point2D windowCoord = new Point2D(scene.getWindow().getX(), scene.getWindow().getY());
			final Point2D sceneCoord = new Point2D(scene.getX(), scene.getY());
			final Point2D nodeCoord = control.localToScene(0.0, 0.0);
			Bounds p = control.getBoundsInLocal();
			final double x = Math.round(windowCoord.getX() + sceneCoord.getX() + nodeCoord.getX());
			final double y = Math.round(windowCoord.getY() + sceneCoord.getY() + nodeCoord.getY()) + p.getHeight();
			popup.setWidth(p.getWidth());
			popup.setHideOnEscape(true);
			popup.setAutoHide(true);
			popup.show(control, x, y);
			updateTextField(null);
			textField.selectAll();
			textField.requestFocus();
		}
	
		public void hidePopup() {
			if (popup != null) popup.hide();
			updateTextField(null);
		}
	}


	public static final class FakeFocusTextField extends TextField {

		@Override public void requestFocus() {
			if (getParent() != null) {
				getParent().requestFocus();
			}
			super.requestFocus();
		}

		public void setFakeFocus(boolean b) {
			setFocused(b);
		}

		@Override
		public Object queryAccessibleAttribute(AccessibleAttribute attribute, Object... parameters) {
			switch (attribute) {
			case FOCUS_ITEM:
				/* Internally comboBox reassign its focus the text field.
				 * For the accessibility perspective it is more meaningful
				 * if the focus stays with the comboBox control.
				 */
				return getParent();
			default: return super.queryAccessibleAttribute(attribute, parameters);
			}
		}
	}	

	public static final class FakeFocusHBox extends HBox {

		@Override public void requestFocus() {
			if (getParent() != null) {
				getParent().requestFocus();
			}
			super.requestFocus();
		}

		public void setFakeFocus(boolean b) {
			setFocused(b);
		}

		@Override
		public Object queryAccessibleAttribute(AccessibleAttribute attribute, Object... parameters) {
			switch (attribute) {
			case FOCUS_ITEM:
				/* Internally comboBox reassign its focus the text field.
				 * For the accessibility perspective it is more meaningful
				 * if the focus stays with the comboBox control.
				 */
				return getParent();
			default: return super.queryAccessibleAttribute(attribute, parameters);
			}
		}
	}	

	
}
