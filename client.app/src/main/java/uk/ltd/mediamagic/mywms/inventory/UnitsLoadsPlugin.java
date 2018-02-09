package uk.ltd.mediamagic.mywms.inventory;

import java.beans.PropertyDescriptor;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

import org.mywms.model.StockUnit;

import de.linogistix.los.inventory.facade.ManageInventoryFacade;
import de.linogistix.los.location.constants.LOSUnitLoadLockState;
import de.linogistix.los.location.crud.LOSUnitLoadCRUDRemote;
import de.linogistix.los.location.model.LOSStorageLocation;
import de.linogistix.los.location.model.LOSUnitLoad;
import de.linogistix.los.location.query.dto.StorageLocationTO;
import de.linogistix.los.location.query.dto.UnitLoadTO;
import de.linogistix.los.query.BODTO;
import de.linogistix.los.query.LOSResultList;
import de.linogistix.los.query.QueryDetail;
import de.linogistix.los.query.TemplateQuery;
import de.linogistix.los.query.TemplateQueryFilter;
import de.linogistix.los.query.TemplateQueryWhereToken;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.util.StringConverter;
import uk.ltd.mediamagic.flow.crud.BODTOPlugin;
import uk.ltd.mediamagic.flow.crud.BODTOTable;
import uk.ltd.mediamagic.flow.crud.BasicEntityEditor;
import uk.ltd.mediamagic.flow.crud.MyWMSEditor;
import uk.ltd.mediamagic.flow.crud.SubForm;
import uk.ltd.mediamagic.fx.AwesomeIcon;
import uk.ltd.mediamagic.fx.MDialogs;
import uk.ltd.mediamagic.fx.action.AC;
import uk.ltd.mediamagic.fx.action.RootCommand;
import uk.ltd.mediamagic.fx.controller.list.CellRenderer;
import uk.ltd.mediamagic.fx.controller.list.MaterialCells;
import uk.ltd.mediamagic.fx.data.TableKey;
import uk.ltd.mediamagic.fx.flow.ApplicationContext;
import uk.ltd.mediamagic.fx.flow.ContextBase;
import uk.ltd.mediamagic.fx.flow.FXErrors;
import uk.ltd.mediamagic.fx.flow.Flow;
import uk.ltd.mediamagic.fx.flow.ViewContext;
import uk.ltd.mediamagic.fx.flow.ViewContextBase;
import uk.ltd.mediamagic.mywms.common.LockStateConverter;
import uk.ltd.mediamagic.mywms.common.QueryUtils;
import uk.ltd.mediamagic.mywms.transactions.StockUnitRecordAction;

@SubForm(
		title="Main", columns=1, 
		properties={"labelId", "type", "packageType", "storageLocation", "opened", "carrier", "carrierUnitLoad", "stockTakingDate"}
	)
@SubForm(
		title="Weight", columns=2, 
		properties={"weight", "weightCalculated", "weightMeasure"}
	)
@SubForm(
		title="Hidden", columns=0, 
		properties={"carrierUnitLoadId", "index"}
	)

public class UnitsLoadsPlugin extends BODTOPlugin<LOSUnitLoad> {

	enum UnitLoadFilter {All, Available, Empty, Carrier, Goods_out};

	private enum Action {
		LOCK, SEND_TO_NIRWANA, TRANSFER, STOCK_UNIT_LOG
	}
	
	public UnitsLoadsPlugin() {
		super(LOSUnitLoad.class);
	}
	
	@Override
	public String getPath() {
		return "{1, _Inventory} -> {1, _Unit Load}";
	}
	
	@Override
	public Supplier<CellRenderer<LOSUnitLoad>> createCellFactory() {
		return MaterialCells.withID(s -> (s.getLock() == 0) ? new AwesomeIcon(AwesomeIcon.unlock) : new AwesomeIcon(AwesomeIcon.lock), 
				LOSUnitLoad::getLabelId, 
				s -> String.format("%s, %s", s.getLabelId(), s.getType().getName()),
				s -> String.format("%s", s.getStorageLocation().getName()),
				s -> "");
	}

	@Override
	protected StringConverter<?> getConverter(PropertyDescriptor property) {
		if ("lock".equals(property.getName())) {
			return new LockStateConverter<>(LOSUnitLoadLockState.class);
		}
		else {
			return super.getConverter(property);			
		}
	}

	@Override
	protected	void refresh(BODTOTable<LOSUnitLoad> source, ViewContextBase context) {
		UnitLoadFilter filterValue = QueryUtils.getFilter(source, UnitLoadFilter.Available);
		TemplateQuery template = source.createQueryTemplate();
		TemplateQueryFilter filter = template.addNewFilter();
		switch (filterValue) {
			case Available: 
				filter.addWhereToken(new TemplateQueryWhereToken(TemplateQueryWhereToken.OPERATOR_EQUAL, "lock", LOSUnitLoadLockState.NOT_LOCKED.getLock()));
				break;
			case Empty:
  			//FIXME the stockUnitList dose not exist anymore.
  			filter.addWhereToken(new TemplateQueryWhereToken(TemplateQueryWhereToken.OPERATOR_MANUAL, 
  					"NOT EXISTS(FROM " + StockUnit.class.getSimpleName() + " su WHERE su.unitLoad = o)"));
				filter.addWhereToken(new TemplateQueryWhereToken(TemplateQueryWhereToken.OPERATOR_FALSE, "carrier", 0));				
				// the original software contained this constraint, but i cannot see where 
				// this would be true as 9 is not a valid lock value for UnitLoads
				// template.addWhereToken(new TemplateQueryWhereToken(TemplateQueryWhereToken.OPERATOR_NOT_EQUAL, "lock", 9));				
				break;
			case Carrier:
				filter.addWhereToken(new TemplateQueryWhereToken(TemplateQueryWhereToken.OPERATOR_TRUE, "carrier", 0));
				// template.addWhereToken(new TemplateQueryWhereToken(TemplateQueryWhereToken.OPERATOR_NOT_EQUAL, "lock", 9));				
				break;
				
			case Goods_out:
				filter.addWhereToken(new TemplateQueryWhereToken(TemplateQueryWhereToken.OPERATOR_EQUAL, "lock", LOSUnitLoadLockState.SHIPPED.getLock()));
				// OR lock = 100, but again there is no case where it will be 100.				
				break;

			default:
		}
		
		QueryDetail detail = source.createQueryDetail();
		detail.addOrderByToken("created", false);
		source.setItems(null);
		getListData(context, detail, template)
			.thenApplyAsync(FXCollections::observableList, Platform::runLater)
			.thenAccept(source::setItems);			
	}
	
	@Override
	public CompletableFuture<LOSResultList<BODTO<LOSUnitLoad>>> 
	getListData(ContextBase context, QueryDetail detail, TemplateQuery template) {
		if (detail.getOrderBy().isEmpty()) {
			detail.addOrderByToken("created", false);
		}
		return super.getListData(context, detail, template);
	}
	
	private void lock(Object source, Flow flow, ViewContext context, TableKey key) {
		ComboBox<Integer> lockStateField = QueryUtils.lockStateCombo(LOSUnitLoadLockState.class);
		TextArea causeField = new TextArea();
		causeField.setPromptText("Reason");
		
		boolean ok = MDialogs.create(context.getRootNode(), "Lock Stock Unit")
			.input("Lock State",lockStateField)
			.input("Cause", causeField)
			.showOkCancel();
		
		if (!ok) return;
		
		Integer lock = lockStateField.getValue();
		String lockCause = causeField.getText();
		if (lock == null) {
			FXErrors.error(context.getRootNode(), "Lock state was empty.");
			return;
		}
		
		LOSUnitLoadCRUDRemote crud = context.getBean(LOSUnitLoadCRUDRemote.class);
		long id = key.get("id");
		context.getExecutor().run(() -> {
			LOSUnitLoad ul = crud.retrieve(id);
			crud.lock(ul, lock, lockCause);
		})
		.thenAcceptAsync(x -> flow.executeCommand(Flow.REFRESH_ACTION), Platform::runLater);
	}
	
	private void transfer(Object source, Flow flow, ViewContext context, TableKey key) {
		final long id = key.get("id");

		BasicEntityEditor<LOSStorageLocation> location = new BasicEntityEditor<>();
		location.configure(context, LOSStorageLocation.class);
		CheckBox ignoreLocationLock = new CheckBox();
		TextField infoField = new TextField();
		
		boolean ok = MDialogs.create(context.getRootNode(), "Transfer Stock Unit")
			.input("To Unit Load", location)
			.input("Ignore locked locations", ignoreLocationLock)
			.input("Comment", infoField)
			.showOkCancel();
		if (!ok) return;
		
		LOSStorageLocation sl = location.getValue();
		boolean ignoreLockedSl = ignoreLocationLock.isSelected();
		String info = infoField.getText();
		if (sl == null) return;
		
		LOSUnitLoadCRUDRemote ulCrud = context.getBean(LOSUnitLoadCRUDRemote.class);
		ManageInventoryFacade manageInventory = context.getBean(ManageInventoryFacade.class);
		context.getExecutor().call(() -> {
			LOSUnitLoad ul = ulCrud.retrieve(id);
			manageInventory.transferUnitLoad(new StorageLocationTO(sl), new UnitLoadTO(ul), 0, ignoreLockedSl, info);
			return null;
		});
	}

	private void sendToNirwana(Object source, Flow flow, ViewContext context, TableKey key) {
		final long id = key.get("id");
		
		boolean ok = MDialogs.create(context.getRootNode())
				.masthead("Send unit load " + id + " to Nirwana (DELETE)")
				.showYesNo(MDialogs.Yes3);
		
		if (!ok) return;
		
		LOSUnitLoadCRUDRemote ulCrud = context.getBean(LOSUnitLoadCRUDRemote.class);
		ManageInventoryFacade manageInventory = context.getBean(ManageInventoryFacade.class);
		context.getExecutor().call(() -> {
			LOSUnitLoad ul = ulCrud.retrieve(id);
			manageInventory.sendStockUnitsToNirwanaFromUl(Collections.singletonList(new UnitLoadTO(ul)));
			return null;
		});
	}

	@Override
	public Flow createNewFlow(ApplicationContext context) {
		Flow flow = super.createNewFlow(context);
		flow.globalWithSelection()
			.withSelection(Action.LOCK, this::lock)
			.withSelection(Action.TRANSFER, this::transfer)
			.withSelection(Action.SEND_TO_NIRWANA, this::sendToNirwana)
			.withSelection(Action.STOCK_UNIT_LOG, StockUnitRecordAction.forUnitLoad())
		.end();
		return flow;
	}

	@Override
	protected void configureCommands(RootCommand command) {
		super.configureCommands(command);
		command
		.begin(RootCommand.MENU)
			.add(AC.id(Action.STOCK_UNIT_LOG).text("Stock Unit Log"))
		.end();
	}
	
	@Override
	protected MyWMSEditor<LOSUnitLoad> getEditor(ContextBase context, TableKey key) {
		MyWMSEditor<LOSUnitLoad> editor = super.getEditor(context, key);
		editor.getCommands()
			.begin(RootCommand.MENU)
				.add(AC.id(Action.LOCK).text("Lock"))
				.add(AC.id(Action.TRANSFER).text("Transfer stock unit"))
				.add(AC.id(Action.SEND_TO_NIRWANA).text("Send to Nirwana"))
			.end()
		.end();
		return editor;
	}
	
	/**
	 * Generate the table layout for table selectors.
	 * The method should be overridden when the table layout needs to be customised.
	 * @param context the context for preparing the controller.
	 * @return the table layout.
	 */
	protected BODTOTable<LOSUnitLoad> getTable(ViewContextBase context) {
		BODTOTable<LOSUnitLoad> table = super.getTable(context);
		Runnable refreshData = () -> refresh(table, context);
		QueryUtils.addFilter(table, UnitLoadFilter.Available, refreshData);
		table.getCommands()
			.begin(RootCommand.MENU)
				.add(AC.id(Action.LOCK).text("Lock"))
				.add(AC.id(Action.TRANSFER).text("Transfer stock unit"))
				.add(AC.id(Action.SEND_TO_NIRWANA).text("Send to Nirwana"))
				.end()
		.end();
		return table;
	}

	@Override
	public List<String> getTableColumns() {
		return Arrays.asList("name AS labelId", 
				"clientNumber AS client.number",	"typeName AS type.name", 
				"storageLocation AS storageLocation.name", 
				"lock");
	}
	
}
