package uk.ltd.mediamagic.flow.crud;

import java.beans.BeanInfo;
import java.beans.PropertyDescriptor;
import java.net.URL;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.mywms.model.BasicEntity;

import de.linogistix.los.crud.BusinessObjectCRUDRemote;
import de.linogistix.los.entityservice.BusinessObjectLockState;
import de.linogistix.los.query.BODTO;
import de.linogistix.los.query.BusinessObjectQueryRemote;
import de.linogistix.los.query.QueryDetail;
import de.linogistix.los.query.TemplateQuery;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.value.ObservableBooleanValue;
import javafx.collections.FXCollections;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.cell.TextFieldListCell;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.util.Callback;
import javafx.util.StringConverter;
import uk.ltd.mediamagic.annot.Worker;
import uk.ltd.mediamagic.debug.MLogger;
import uk.ltd.mediamagic.fx.ApplicationPane;
import uk.ltd.mediamagic.fx.FxExceptions;
import uk.ltd.mediamagic.fx.FxMainMenuPlugin;
import uk.ltd.mediamagic.fx.MFXMLLoader;
import uk.ltd.mediamagic.fx.concurrent.MExecutor;
import uk.ltd.mediamagic.fx.converters.ToStringConverter;
import uk.ltd.mediamagic.fx.data.TableKey;
import uk.ltd.mediamagic.fx.flow.ApplicationContext;
import uk.ltd.mediamagic.fx.flow.BasicFlow;
import uk.ltd.mediamagic.fx.flow.ContextBase;
import uk.ltd.mediamagic.fx.flow.FXErrors;
import uk.ltd.mediamagic.fx.flow.Flow;
import uk.ltd.mediamagic.fx.flow.FlowContext;
import uk.ltd.mediamagic.fx.flow.ViewContext;
import uk.ltd.mediamagic.fx.flow.ViewContextBase;
import uk.ltd.mediamagic.fxcommon.UserPermissions;
import uk.ltd.mediamagic.mywms.BeanDirectory;
import uk.ltd.mediamagic.mywms.FlowUtils;
import uk.ltd.mediamagic.mywms.common.BeanUtils;
import uk.ltd.mediamagic.mywms.common.Editor;
import uk.ltd.mediamagic.mywms.common.LockStateConverter;
import uk.ltd.mediamagic.mywms.common.MyWMSUserPermissions;
import uk.ltd.mediamagic.mywms.common.TableColumnBinding;

/**
 * Implements the basic behaviour for a BODTO back list with a editor.
 * 
 * The default implementation will look for an FXML file <code>T.class.getSimpleName() + ".fxml"</code>
 * to use at the editor.  If no file is found it will use the <code>@SubForm</code> annotations
 * set on this class to build a default form.
 * 
 * @author slim
 *
 * @param <T>
 */
public abstract class BODTOPlugin<T extends BasicEntity> extends FxMainMenuPlugin implements Editor<T> {
	
	private final Logger log = MLogger.log(this);
	private final Class<T> boClass;
	private final Class<? extends BusinessObjectQueryRemote<T>> queryBean;
	private final Class<? extends BusinessObjectCRUDRemote<T>> crudBean;
	private final Class<? extends BODTO<T>> toClass;
	private final BeanInfo beanInfo;
	private UserPermissions userPermissions = new MyWMSUserPermissions();
	
	public BODTOPlugin(Class<T> boClass) {
		super();
		this.boClass = boClass;
		this.beanInfo = BeanUtils.getBeanInfo(boClass);

		Class<? extends BusinessObjectQueryRemote<T>> myQueryBean;
		Class<? extends BusinessObjectCRUDRemote<T>> mycrudBean;
		Class<? extends BODTO<T>> myToClass;
		
		try {
			myQueryBean = BeanDirectory.getQuery(boClass);
			mycrudBean = BeanDirectory.getCRUD(boClass);
			myToClass = BeanDirectory.getBODTO(boClass);
		}
		catch (Throwable e) {
			log.log(Level.SEVERE, "While looking for helper classes", e);
			myQueryBean = null;
			mycrudBean = null;
			myToClass = null;
		}
		this.queryBean = myQueryBean;
		this.crudBean = mycrudBean;
		this.toClass = myToClass;
	}
	
	protected BeanInfo getBeanInfo() {
		return beanInfo;
	}

	protected Class<T> getBoClass() {
		return boClass;
	}

	protected Class<? extends BODTO<T>> getTOClass() {
		return toClass;
	}

	protected Class<? extends BusinessObjectQueryRemote<T>> getQueryClass() {
		return queryBean;
	}

	protected Class<? extends BusinessObjectCRUDRemote<T>> getCRUDClass() {
		return crudBean;
	}

	public UserPermissions getUserPermissions() {
		return userPermissions;
	}

	protected void setUserPermissions(UserPermissions userPermissions) {
		this.userPermissions = userPermissions;
	}

	@Override
	public void edit(ContextBase context, Class<T> dataClass, long id) {
		ApplicationContext appContext = context.getBean(ApplicationContext.class);
		
		Flow flow = createNewFlow(appContext);
		flow.start(MyWMSEditor.class, c -> getEditor(c, new TableKey("id", id)));
		FlowUtils.startNewFlow(appContext, flow);
	}

	@Override
	public void view(ContextBase context, Flow flow, Class<T> dataClass, long id) {
		MyWMSEditor<T> editor = getEditor(context, new TableKey("id", id));
		editor.getCommands().clear();
		editor.setUserPermissions(new UserPermissions.ReadOnly());
		FlowUtils.showPopup(dataClass.getSimpleName(), context, editor);
	}
	
	@Override
	public Callback<ListView<BODTO<T>>, ListCell<BODTO<T>>> createTOListCellFactory() {
		return TextFieldListCell.forListView(ToStringConverter.of(BODTO::getName));
	}

	@Override
	public Callback<ListView<T>, ListCell<T>> createListCellFactory() {
		return TextFieldListCell.forListView(new ToStringConverter<>(BasicEntity::toUniqueString));
	}

	
	/**
	 * A lookup method for generating StringConverters for the type of the 
	 * property.  These converters are used in the default table and editors.
	 * It is left to the subclass to ensure that the converter is suitable for 
	 * the type of the property.
	 * 
	 * @param property the java bean property
	 * @return an appropriate string converter
	 */
	protected StringConverter<?> getConverter(PropertyDescriptor property) {
		if ("lock".equals(property.getName())) {
			return new LockStateConverter<>(BusinessObjectLockState.class);
		}
		return null;
	}
	
	@Override
	protected BooleanBinding createDisableBinding() {
		return Bindings.createBooleanBinding(() -> false);
	}
	
	@Override
	protected BooleanBinding createVisibleBinding() {
		return Bindings.createBooleanBinding(() -> true);
	}
	
	/**
	 * A method that is call outside of the GUI thread to save the data.
	 * @param context the context to lookup information
	 * @param data the data to save
	 * @throws Exception if an error occurs.
	 */
	CompletableFuture<Void> save(ContextBase context, T data) {
		BusinessObjectCRUDRemote<T> query = context.getBean(crudBean);
		return context.getBean(MExecutor.class).run(() -> query.update(data));
	}

	/**
	 * A method that is call outside of the GUI thread to delete the data.
	 * @param context the context to lookup information
	 * @param data the data to save
	 * @throws Exception if an error occurs.
	 */
	CompletableFuture<Void> delete(ContextBase context, T data) {
		BusinessObjectCRUDRemote<T> query = context.getBean(crudBean);
		return context.getBean(MExecutor.class).run(() -> query.delete(data));
	}

	/**
	 * A method that is call outside of the GUI thread to fetch a list of data.
	 * @param context the context to lookup information
	 * @param detail the query detail to use in the fetch.
	 * @param template the query detail to use in the fetch.
	 * @param data the data to save
	 * @throws Exception if an error occurs.
	 */
	public CompletableFuture<List<BODTO<T>>> getListData(ContextBase context,  QueryDetail detail, TemplateQuery template) {
		BusinessObjectQueryRemote<T> query = context.getBean(queryBean);
		template.setBoClass(boClass);
		return context.getBean(MExecutor.class).call(() -> query.queryByTemplateHandles(detail,template));
	}
	
	/**
	 * A method that is call outside of the GUI thread to the data with the given id.
	 * @param context the context to lookup information
	 * @param id the id for the object to fetch.
	 * @throws Exception if an error occurs.
	 */
	@Worker 
	public CompletableFuture<T> getData(ContextBase context, long id) {
		BusinessObjectQueryRemote<T> query = context.getBean(queryBean);
		return context.getBean(MExecutor.class).call(() ->  query.queryById(id));
	}

	protected	void save(PoJoEditor<T> source, Flow flow, ViewContext context) {
		T data = source.getData();
		source.setData(null);
		save(context, data)
		.thenCompose(x -> getData(context, data.getId()))
		.whenCompleteAsync((d,e) -> {
			if (e != null) {
				source.setData(data);
				FxExceptions.exceptionThrown(e);
			}
			else if (d != null) {
				source.setData(d);
			}
			else {
				FXErrors.error("Data load error", "Count not refresh data after save. ID:" + data.getId());
			}
		}, Platform::runLater);
	}

	/**
	 * this method is called by the editor when the user requests a refresh.
	 * @param source the editor that made the call
	 * @param flow the current flow
	 * @param context the current context
	 */
	protected void refresh(PoJoEditor<T> source, Flow flow, ViewContext context) {
		T data = source.getData();
		source.setData(null);
		getData(context, data.getId())
		.whenCompleteAsync((d,e) -> {
			if (e != null) {
				FxExceptions.exceptionThrown(e);
			}
			else if (d != null) {
				source.setData(d);
			}
			else {
				FXErrors.error("Data load error", "Count not refresh data after save. ID:" + data.getId());
			}
		}, Platform::runLater);
	}

	/**
	 * this method is called by the table view when the user requests a refresh.
	 * @param source the table view that made the call
	 * @param context the current context
	 */
	protected	void refresh(BODTOTable<T> source, ViewContextBase context) {
		TemplateQuery template = source.createQueryTemplate();
		QueryDetail detail = source.createQueryDetail();
		source.setItems(null);
		getListData(context, detail, template)
			.thenApplyAsync(FXCollections::observableList, Platform::runLater)
			.thenAccept(source::setItems);			
	}

	/**
	 * Create a control flow for this data type.
	 * @param context the context to be the parent of this flow
	 * @return a control flow object.
	 */
	@SuppressWarnings("unchecked")
	public Flow createNewFlow(ApplicationContext context) {
		BasicFlow flow = new BasicFlow(new FlowContext(context));		
		String title = getName(getPath());
		flow.setTitle(title);
		flow
		.global()
			.back()
			.alias(Flow.CANCEL_ACTION, Flow.BACK_ACTION)
		.end()
		.with(BODTOTable.class)
			.withSelection(Flow.EDIT_ACTION, this::getEditor)
			.alias(Flow.TABLE_SELECT_ACTION, Flow.EDIT_ACTION)
			.action(Flow.REFRESH_ACTION, (s,f,c) -> this.refresh((BODTOTable<T>)s, c))
		.end()
		.with(MyWMSEditor.class)
			.action(Flow.SAVE_ACTION, this::save)
			.action(Flow.REFRESH_ACTION, (s,f,c) -> this.refresh((MyWMSEditor<T>)s, f, c))
		.end();		
		return flow;
	}
	
	/**
	 * Creates an editor for this object type.
	 * @param context the current context
	 * @param key the key form the table view for the object to be edited.
	 * @return an appropriate editor
	 */
	protected MyWMSEditor<T> getEditor(ContextBase context, TableKey key) {
		Long id = key.get("id");
		if (id == null) return null;

		URL url = getClass().getResource(boClass.getSimpleName() + ".fxml");

		MyWMSEditor<T> controller = new MyWMSEditor<>(getBeanInfo(), this::getConverter);
		controller.setUserPermissions(getUserPermissions());
		context.autoInjectBean(controller);
		if (url == null) {
			SubForm[] subForms = getClass().getAnnotationsByType(SubForm.class);
			List<SubForm> subFormsList = (subForms == null) ? Collections.emptyList() : Arrays.asList(subForms);
			PojoForm form = new MyWMSForm(getBeanInfo(), subFormsList);
			form.bindController(controller);
		}
		else {
			MFXMLLoader.loadFX(url, controller);
		}
		getData(context, id).thenAcceptAsync(controller::setData, Platform::runLater);
		return controller;
	}
	
	
	
	/**
	 * This is the flow action that is used to move to the editing state.
	 * 
	 * @param source the source state for this transation
	 * @param flow the control flow
	 * @param context the new context for the target state
	 * @param key selected item from the previous state.
	 */
	void getEditor(BODTOTable<T> source, Flow flow, ViewContext context, TableKey key) {
		MyWMSEditor<T> controller = getEditor(context, key);
		context.setActiveBean(MyWMSEditor.class, controller);
		flow.next(context);
	}
	
	/**
	 * A list of property names for display in the table view.
	 * This is used by the <code>getTable()</code> method.
	 * @return a list of property names.
	 */
	protected abstract List<String> getTableColumns();

	/**
	 * Generate the table layout for table selectors.
	 * The method should be overridden when the table layout needs to be customised.
	 * @param context the context for preparing the controller.
	 * @return the table layout.
	 */
	protected BODTOTable<T> getTable(ViewContextBase context) {
		BODTOTable<T> table = new BODTOTable<>();	
		context.autoInjectBean(table);

		UserPermissions userPermissions = getUserPermissions();
		Iterable<String> columns = getTableColumns().stream()
				.filter(s -> Optional.ofNullable(userPermissions.isVisible(s)).map(ObservableBooleanValue::get).orElse(true))
				::iterator;
		
		TableColumnBinding<BODTO<T>> tcb = new TableColumnBinding<>(BeanUtils.getBeanInfo(toClass),	columns, "id"::equals);
		tcb.setConverterFactory(this::getConverter);
		Bindings.bindContent(table.getTable().getColumns(), tcb);

		table.getCommands()
			.cru()
		.end();
				
		refresh(table, context);
		table.addQueryListener(o -> refresh(table, context));
		
		return table;
	}
		
	@Override
	public void handle(ApplicationContext context, Parent source, Function<Node, Runnable> showNode) {
		if (queryBean == null) throw new NullPointerException("No query class for " + getBoClass().getName() + " could be found");
		if (crudBean == null) throw new NullPointerException("No CRUD class for " + getBoClass().getName() + " could be found");
		if (toClass == null) throw new NullPointerException("No BODTO class for " + getBoClass().getName() + " could be found");
		AnchorPane parent = new AnchorPane();
		parent.getChildren().add(new Label("Waiting..."));
		Flow flow = createNewFlow(context);

		if (flow == null) return; // opperation canceled
		flow.start(BODTOTable.class, parent, (c) -> getTable(c));
		flow.setOnDisplayNode(showNode);
				
		configureFlowNode(parent, flow);

		Runnable onClose = showNode.apply(parent);
		flow.setOnClose(onClose);
		flow.executeStartAction(parent);
	}

	public static Pane configureFlowNode(Pane parent, Flow flow) {		
		EventHandler<Event> onCloseRequestHandler = flow.createOnCloseRequestHandler();		
		ApplicationPane.getTitleProperty(parent).bind(flow.titleProperty());
		ApplicationPane.setOnCloseRequestHandler(parent, onCloseRequestHandler);
		return parent;
	}
}
