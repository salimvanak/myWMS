package uk.ltd.mediamagic.flow.crud;

import java.beans.BeanInfo;
import java.beans.PropertyDescriptor;
import java.net.URL;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.mywms.model.BasicEntity;

import de.linogistix.los.crud.BusinessObjectCRUDRemote;
import de.linogistix.los.entityservice.BusinessObjectLockState;
import de.linogistix.los.query.BODTO;
import de.linogistix.los.query.BusinessObjectQueryRemote;
import de.linogistix.los.query.LOSResultList;
import de.linogistix.los.query.QueryDetail;
import de.linogistix.los.query.TemplateQuery;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.value.ObservableBooleanValue;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.util.StringConverter;
import uk.ltd.mediamagic.annot.Worker;
import uk.ltd.mediamagic.fx.ApplicationPane;
import uk.ltd.mediamagic.fx.FxExceptions;
import uk.ltd.mediamagic.fx.MDialogs;
import uk.ltd.mediamagic.fx.MFXMLLoader;
import uk.ltd.mediamagic.fx.action.RootCommand;
import uk.ltd.mediamagic.fx.concurrent.MExecutor;
import uk.ltd.mediamagic.fx.concurrent.function.BgConsumer;
import uk.ltd.mediamagic.fx.controller.list.CellRenderer;
import uk.ltd.mediamagic.fx.controller.list.TextRenderer;
import uk.ltd.mediamagic.fx.converters.ToStringConverter;
import uk.ltd.mediamagic.fx.data.TableKey;
import uk.ltd.mediamagic.fx.flow.ApplicationContext;
import uk.ltd.mediamagic.fx.flow.BasicFlow;
import uk.ltd.mediamagic.fx.flow.ContextBase;
import uk.ltd.mediamagic.fx.flow.FXErrors;
import uk.ltd.mediamagic.fx.flow.Flow;
import uk.ltd.mediamagic.fx.flow.FlowContext;
import uk.ltd.mediamagic.fx.flow.FlowLifeCycle;
import uk.ltd.mediamagic.fx.flow.ViewContext;
import uk.ltd.mediamagic.fx.flow.ViewContextBase;
import uk.ltd.mediamagic.fxcommon.ObservableConstant;
import uk.ltd.mediamagic.fxcommon.UserPermissions;
import uk.ltd.mediamagic.mywms.BeanDirectory;
import uk.ltd.mediamagic.mywms.FlowUtils;
import uk.ltd.mediamagic.mywms.MyWMSMainMenuPlugin;
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
public abstract class BODTOPlugin<T extends BasicEntity> extends MyWMSMainMenuPlugin implements Editor<T> {
	
	protected final Logger log = MLogger.log(this);
	private final Class<T> boClass;
	private final Class<? extends BusinessObjectQueryRemote<T>> queryBean;
	private final Class<? extends BusinessObjectCRUDRemote<T>> crudBean;
	private final Class<? extends BODTO<T>> toClass;
	private final BeanInfo beanInfo;
	private MyWMSUserPermissions userPermissions = new MyWMSUserPermissions();
	
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
		
	/**
	 * Gets the bean info object for this plugin
	 * @return
	 */
	protected BeanInfo getBeanInfo() {
		return beanInfo;
	}

	/**
	 * Gets the class representing this plugins data type
	 * @return
	 */
	protected Class<T> getBoClass() {
		return boClass;
	}

	/**
	 * Gets the transfer object class for this plugin
	 * @return
	 */
	protected Class<? extends BODTO<T>> getTOClass() {
		return toClass;
	}

	protected Class<? extends BusinessObjectQueryRemote<T>> getQueryClass() {
		return queryBean;
	}

	protected Class<? extends BusinessObjectCRUDRemote<T>> getCRUDClass() {
		return crudBean;
	}
	
	protected void configureCommands(RootCommand command) {
	}

	public MyWMSUserPermissions getUserPermissions() {
		return userPermissions;
	}
	
	/**
	 * Sets the permission object for this plugin.
	 * The permissions object is used to determine editable and visible 
	 * fields for this data type 
	 * @param userPermissions
	 */
	protected void setUserPermissions(MyWMSUserPermissions userPermissions) {
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
	
	/**
	 * Creates a list cell factory for displaying the Transfer object of this data type;
	 */
	@Override
	public Supplier<CellRenderer<BODTO<T>>> createTOCellFactory() {
    return TextRenderer.of(new ToStringConverter<BODTO<T>>(BODTO::getName));
	}

	
	/**
	 * Creates a list cell factory for displaying this data type;
	 */
	@Override
	public Supplier<CellRenderer<T>> createCellFactory() {
    return TextRenderer.of(new ToStringConverter<>(BasicEntity::toUniqueString));
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

	protected ObservableBooleanValue createAllowedBinding() {
		SubForm[] subForms = getClass().getAnnotationsByType(SubForm.class);
		boolean create = Arrays.stream(subForms)
				.filter(SubForm::isRequired)
				.findAny().isPresent();
		
		return ObservableConstant.of(create);
	}

	protected ObservableBooleanValue deleteAllowedBinding() {
		return getUserPermissions().isDeleteAllowed();
	}

	/**
	 * A method that is call outside of the GUI thread to save the data.
	 * @param context the context to lookup information
	 * @param data the data to save
	 * @throws Exception if an error occurs.
	 */
	CompletableFuture<T> save(ContextBase context, T data) {
		BusinessObjectCRUDRemote<T> query = context.getBean(crudBean);
		if (data.getId() == null) {
			return context.getBean(MExecutor.class).call(() -> {
				return query.create(data);
			});			
		}
		else {			
			return context.getBean(MExecutor.class).call(() -> {
				query.update(data);
				return data;
			});
		}
	}

	/**
	 * A method that is call outside of the GUI thread to delete the data.
	 * @param context the context to lookup information
	 * @param data the data to save
	 * @throws Exception if an error occurs.
	 */
	CompletableFuture<Void> delete(ContextBase context, List<BODTO<T>> data) {
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
	public CompletableFuture<LOSResultList<BODTO<T>>> getListData(ContextBase context,  QueryDetail detail, TemplateQuery template) {
		BusinessObjectQueryRemote<T> query = context.getBean(queryBean);
		template.setBoClass(boClass);
		return context.getBean(MExecutor.class).call(() -> {
			LOSResultList<BODTO<T>> r = query.queryByTemplateHandles(detail,template);
			
			return r;
		});
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
	 * @param context the current context
	 */
	protected void refresh(PoJoEditor<T> source, ViewContextBase context) {
		T data = source.getData();
		if (data == null) return;
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
		source.clearTable();
		getListData(context, detail, template)
			.thenAcceptAsync(source::setLOSResultList, Platform::runLater);			
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
			.put(Flow.CREATE_ACTION, this::create)
			.withSelection(Flow.EDIT_ACTION, (s,f,c,k) -> getEditor((BODTOTable<T>) s,f,c,k))
			.withMultiSelection(Flow.DELETE_ACTION, (s,f,c,k) -> delete((BODTOTable<T>) s,f,c,k))
			.alias(Flow.TABLE_SELECT_ACTION, Flow.EDIT_ACTION)
			.action(Flow.REFRESH_ACTION, (s,f,c) -> this.refresh((BODTOTable<T>)s, c))
		.end()
		.with(MyWMSEditor.class)
			.action(Flow.SAVE_ACTION, this::save)
			.action(Flow.REFRESH_ACTION, (s,f,c) -> this.refresh((MyWMSEditor<T>)s, c))
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
		if (id == null) {
			log.log(Level.INFO, "id is null so cannot generate controller");
			return null;
		}

		MyWMSEditor<T> controller = new MyWMSEditor<>(getBeanInfo(), this::getConverter);
		controller.addFlowLifecycle(new FlowLifeCycle() {
			
			@Override
			public boolean saveState(ViewContextBase context) {
				CompletableFuture<T> r = save(controller.getContext(), controller.getData());
				T data = context.getExecutor().executeAndWait(context.getRootNode(), () -> r.get());
				controller.setData(data);
				return true;
			}
			
			@Override
			public void restoreState(ViewContextBase context, boolean force) {
				refresh(controller, context);
			}
			
			@Override
			public boolean isSaveRequired() {
				return controller.isSaveReqired();
			}
		});
		context.autoInjectBean(controller);
		initialiseEditController(context, key, controller);
		return controller;
	}

	/**
	 * Creates an editor for this object type.
	 * @param context the current context
	 * @param key the key form the table view for the object to be edited.
	 * @return an appropriate editor
	 */
	protected MyWMSEditor<T> getCreateEditor(Flow flow, ViewContext context) {
		MyWMSEditor<T> controller = new MyWMSEditor<>(getBeanInfo(), this::getConverter);
		controller.addFlowLifecycle(new FlowLifeCycle() {
			@Override
			public boolean saveState(ViewContextBase context) {
				return true;
			}
			
			@Override
			public void restoreState(ViewContextBase context, boolean force) {
			}
			
			@Override
			public boolean isSaveRequired() {
				return false;
			}
		});
		context.autoInjectBean(controller);
		initialiseCreateController(context, controller);
		controller.getCommands().clear();
		controller.getCommands().okCancel(e -> {
			save(controller.getContext(), controller.getData())
				.thenAccept(d -> {
					flow.back(false);
					MyWMSEditor<T> editor = getEditor(context, CRUDKeyUtils.createKey(d));
					FlowUtils.showNext(flow, context, MyWMSEditor.class, editor);
				});
		}).end();
		return controller;
	}
	
	protected void initialiseEditController(ContextBase context, TableKey key, MyWMSEditor<T> controller) {		
		Long id = key.get("id");
		if (id == null) {
			log.log(Level.INFO, "id is null so cannot generate controller");
			throw new IllegalStateException("ID cannot be null");
		}
		
		URL url = getClass().getResource(boClass.getSimpleName() + ".fxml");

		configureCommands(controller.getCommands());
		controller.getCommands().end();
		controller.setUserPermissions(getUserPermissions());
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
	}

	protected void initialiseCreateController(ContextBase context, MyWMSEditor<T> controller) {		
		URL url = getClass().getResource(boClass.getSimpleName() + ".create.fxml");
		controller.setUserPermissions(getUserPermissions());

		if (url == null) {
			SubForm[] subForms = getClass().getAnnotationsByType(SubForm.class);
			List<SubForm> subFormsList = Arrays.stream(subForms)
					.filter(SubForm::isRequired)
					.collect(Collectors.toList());

			PojoForm form = new MyWMSForm(getBeanInfo(), subFormsList, false);
			form.bindController(controller);
		}
		else {
			MFXMLLoader.loadFX(url, controller);
		}
		try {
			controller.setData(boClass.newInstance());
		} 
		catch (InstantiationException | IllegalAccessException e) {
			log.log(Level.SEVERE, "While creating object", e);
		}
	}

	
	/**
	 * This is the flow action that is used to move to the editing state.
	 * 
	 * @param source the source state for this transation
	 * @param flow the control flow
	 * @param context the new context for the target state
	 * @param key selected item from the previous state.
	 */
	protected void getEditor(BODTOTable<T> source, Flow flow, ViewContext context, TableKey key) {
		MyWMSEditor<T> controller = getEditor(context, key);
		context.setActiveBean(MyWMSEditor.class, controller);
		flow.next(context);
	}
	
	protected void create(BODTOTable<T> table, Flow flow, ViewContext context) {
		MyWMSEditor<?> editor = getCreateEditor(flow, context);
		FlowUtils.showPopup("Create", context, editor);
	}

	protected void delete(BODTOTable<T> source, Flow flow, ViewContext context, Collection<TableKey> key) {
		if (key.isEmpty()) {
			FXErrors.selectionError(source.getTable());
			return;
		}

		boolean yes = MDialogs.create(context.getRootNode(), "Delete selected items")
			.masthead("Delete items, are you sure?")
			.showYesNo();
		
		if (!yes) return;
		List<BODTO<T>> delList = key.stream().map(CRUDKeyUtils::<T>getBOTO).collect(Collectors.toList());
		delete(context, delList)
			.thenRunAsync(() -> source.saveAndRefresh(), MExecutor.UI)
			.whenComplete((v, x) -> {
				if (x != null) FxExceptions.exceptionThrown(x);
			});
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
		table.setRowDecorator((d,r) -> {});
		configureCommands(table.getCommands());
		table.getCommands().end();
		context.autoInjectBean(table);

		UserPermissions userPermissions = getUserPermissions();
		Iterable<String> columns = getTableColumns().stream()
				.filter(s -> Optional.ofNullable(userPermissions.isVisible(s)).map(ObservableBooleanValue::get).orElse(true))
				::iterator;
		
		TableColumnBinding<BODTO<T>> tcb = new TableColumnBinding<>(BeanUtils.getBeanInfo(toClass),	columns, "id"::equals);
		tcb.setConverterFactory(this::getConverter);
		Bindings.bindContent(table.getTable().getColumns(), tcb);

		table.getCommands()
			.crud(createAllowedBinding().get(), true, true, false, deleteAllowedBinding().get())
		.end();
				
		refresh(table, context);
		table.addQueryListener(o -> refresh(table, context));
		table.addFlowLifecycle(new FlowLifeCycle.OnRefresh((c,f) -> refresh(table, context)));
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
		flow.start(BODTOTable.class, parent, c -> getTable(c));
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

	protected void withMultiSelectionTO(ViewContext context, Collection<TableKey> sel, BgConsumer<BODTO<T>> closure) {
		List<BODTO<T>> values = sel.stream().map(CRUDKeyUtils::<T>getBOTO).collect(Collectors.toList());
		withMultiSelection(context, values, closure);
	}
	
	protected <S> void withMultiSelection(ViewContext context, Collection<S> values, BgConsumer<S> closure) {
		if (values.isEmpty()) {
			FXErrors.selectionError(context.getRootNode());
			return;
		}
		
		context.getExecutor().executeAndWait(context.getRootNode(), p -> {
			p.setSteps(values.size());
			for (S v : values) {
				p.step();
				closure.consume(v);
			}
			return null;
		});
		Flow flow = context.getBean(Flow.class);
		flow.executeCommand(Flow.REFRESH_ACTION);
	}

}
