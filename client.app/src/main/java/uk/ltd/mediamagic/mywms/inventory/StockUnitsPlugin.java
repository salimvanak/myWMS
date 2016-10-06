package uk.ltd.mediamagic.mywms.inventory;

import java.beans.PropertyDescriptor;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.mywms.model.StockUnit;

import de.linogistix.los.inventory.crud.StockUnitCRUDRemote;
import de.linogistix.los.inventory.facade.ManageInventoryFacade;
import de.linogistix.los.inventory.query.dto.StockUnitTO;
import de.linogistix.los.inventory.service.StockUnitLockState;
import de.linogistix.los.location.model.LOSUnitLoad;
import de.linogistix.los.location.query.dto.UnitLoadTO;
import de.linogistix.los.query.BODTO;
import de.linogistix.los.query.QueryDetail;
import de.linogistix.los.query.TemplateQuery;
import de.linogistix.los.query.TemplateQueryFilter;
import de.linogistix.los.query.TemplateQueryWhereToken;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.util.Callback;
import javafx.util.StringConverter;
import uk.ltd.mediamagic.common.utils.Strings;
import uk.ltd.mediamagic.flow.crud.BODTOPlugin;
import uk.ltd.mediamagic.flow.crud.BODTOTable;
import uk.ltd.mediamagic.flow.crud.BasicEntityEditor;
import uk.ltd.mediamagic.flow.crud.MyWMSEditor;
import uk.ltd.mediamagic.flow.crud.SubForm;
import uk.ltd.mediamagic.fx.AwesomeIcon;
import uk.ltd.mediamagic.fx.MDialogs;
import uk.ltd.mediamagic.fx.action.AC;
import uk.ltd.mediamagic.fx.action.RootCommand;
import uk.ltd.mediamagic.fx.controller.list.MaterialListItems;
import uk.ltd.mediamagic.fx.converters.BigDecimalConverter;
import uk.ltd.mediamagic.fx.converters.Filters;
import uk.ltd.mediamagic.fx.data.TableKey;
import uk.ltd.mediamagic.fx.flow.ApplicationContext;
import uk.ltd.mediamagic.fx.flow.ContextBase;
import uk.ltd.mediamagic.fx.flow.FXErrors;
import uk.ltd.mediamagic.fx.flow.Flow;
import uk.ltd.mediamagic.fx.flow.ViewContext;
import uk.ltd.mediamagic.fx.flow.ViewContextBase;
import uk.ltd.mediamagic.mywms.common.LockStateConverter;
import uk.ltd.mediamagic.mywms.common.QueryUtils;

@SubForm(
		title="Main", columns=2, 
		properties={"unitLoad", "serialNumber", "strategyDate"}
	)
@SubForm(
		title="Item", columns=2, 
		properties={"itemData", "lot"}
	)
@SubForm(
		title="Unit", columns=2, 
		properties={"itemUnit", "displayAmount"}
	)
@SubForm(
		title="Amount", columns=2, 
		properties={"amount", "reservedAmount", "availableAmount"}
	)
public class StockUnitsPlugin extends BODTOPlugin<StockUnit> {
	
	enum StockUnitFilter {All, Available, Quality_fault, Goods_out}
	private enum Action {
		LOCK, SEND_TO_NIRWANA, CHANGE_AMOUNT, TRANSFER 
	}
	
	public StockUnitsPlugin() {
		super(StockUnit.class);
	}
	
	@Override
	public String getPath() {
		return "{1, _Inventory} -> {1, _Stock Units}";
	}
	
	@Override
	public Flow createNewFlow(ApplicationContext context) {
		Flow flow = super.createNewFlow(context);
		flow.globalWithSelection()
			.withSelection(Action.LOCK, this::lock)
			.withSelection(Action.TRANSFER, this::transfer)
			.withSelection(Action.CHANGE_AMOUNT, this::changeAmount)
			.withSelection(Action.SEND_TO_NIRWANA, this::sendToNirwana)
		.end();
		return flow;
	}


	private void changeAmount(Object source, Flow flow, ViewContext context, TableKey key) {
		final long id = key.get("id");
		
		TextFormatter<BigDecimal> amount = new TextFormatter<>(new BigDecimalConverter());
		TextFormatter<BigDecimal> amountReserved = new TextFormatter<>(new BigDecimalConverter());
		TextField comment = new TextField();
		
		boolean ok = MDialogs.create(context.getRootNode(), "Transfer Stock Unit")
			.input("Amount", Filters.of(amount,8))	
			.input("Reserved", Filters.of(amountReserved,8))	
			.input("Comment", comment)	
			.showOkCancel();
		
		if (!ok) return;
		BigDecimal amountVal = amount.getValue();
		BigDecimal resVal = amountReserved.getValue();
		String commentVal = comment.getText();
	
		StockUnitCRUDRemote suCrud = context.getBean(StockUnitCRUDRemote.class);
		ManageInventoryFacade manageInventory = context.getBean(ManageInventoryFacade.class);
		context.getExecutor().call(() -> {
			StockUnit su = suCrud.retrieve(id);
			manageInventory.changeAmount(new StockUnitTO(su), amountVal, resVal, commentVal);
			return null;
		});
	}

	private void sendToNirwana(Object source, Flow flow, ViewContext context, TableKey key) {
		final long id = key.get("id");
		
		boolean ok = MDialogs.create(context.getRootNode())
				.masthead("Send stock unit " + id + " to Nirwana")
				.showYesNo(MDialogs.Yes3);
		
		if (!ok) return;
	
		StockUnitCRUDRemote suCrud = context.getBean(StockUnitCRUDRemote.class);
		ManageInventoryFacade manageInventory = context.getBean(ManageInventoryFacade.class);
		context.getExecutor().call(() -> {
			StockUnit su = suCrud.retrieve(id);
			manageInventory.sendStockUnitsToNirwana(Collections.singletonList(new StockUnitTO(su)));
			return null;
		});
	}

	private void transfer(Object source, Flow flow, ViewContext context, TableKey key) {
		final long id = key.get("id");

		BasicEntityEditor<LOSUnitLoad> unitLoad = new BasicEntityEditor<>();
		unitLoad.configure(context, LOSUnitLoad.class);
		
		MDialogs.create(context.getRootNode(), "Transfer Stock Unit")
			.input("To Unit Load", unitLoad)	
			.showInputDialog(unitLoad.valueProperty())
			.ifPresent(ul -> {
				StockUnitCRUDRemote suCrud = context.getBean(StockUnitCRUDRemote.class);
				ManageInventoryFacade manageInventory = context.getBean(ManageInventoryFacade.class);
				context.getExecutor().call(() -> {
					StockUnit su = suCrud.retrieve(id);
					manageInventory.transferStockUnit(new StockUnitTO(su), new UnitLoadTO(ul));
					return null;
				});
			});
	}
	
	private void lock(Object source, Flow flow, ViewContext context, TableKey key) {
		ComboBox<Integer> lockStateField = QueryUtils.lockStateCombo(StockUnitLockState.class);
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
		
		StockUnitCRUDRemote crud = context.getBean(StockUnitCRUDRemote.class);
		long id = key.get("id");
		context.getExecutor().run(() -> {
			StockUnit su = crud.retrieve(id);
			crud.lock(su, lock, lockCause);
		})
		.thenAcceptAsync(x -> flow.executeCommand(Flow.REFRESH_ACTION), Platform::runLater);
	}

	
	@Override
	public Callback<ListView<StockUnit>, ListCell<StockUnit>> createListCellFactory() {
		return MaterialListItems.withID(s -> (s.getLock() == 0) ? new AwesomeIcon(AwesomeIcon.unlock) : new AwesomeIcon(AwesomeIcon.lock), 
				StockUnit::getId, 
				s -> Strings.format("{0}, {1}", s.getItemData().getNumber(), s.getItemData().getName()),
				s -> {
					if (s.getLot() != null) {
						return Strings.format("Lot: {0}, {1} -> {2}", s.getLot().getName(), s.getLot().getUseNotBefore(), s.getLot().getBestBeforeEnd()); 						
					}
					else {						
						return String.format("No lot information"); 						
					}
				},
				s -> Strings.format("Available {0}, Reserved {1}", s.getAvailableAmount(), s.getReservedAmount()));
	}
	
	@Override
	protected StringConverter<?> getConverter(PropertyDescriptor property) {
		if ("lock".equals(property.getName())) {
			return new LockStateConverter<>(StockUnitLockState.class);
		}
		else {
			return super.getConverter(property);			
		}
	}
	
	@Override
	protected void refresh(BODTOTable<StockUnit> source, ViewContextBase context) {
		TemplateQuery template = source.createQueryTemplate();
		QueryDetail detail = source.createQueryDetail();
		source.setItems(null);
		
		StockUnitFilter filterValue = QueryUtils.getFilter(source, StockUnitFilter.Available);
		TemplateQueryFilter filter = template.addNewFilter();
		switch (filterValue) {
		case Available:
			filter.addWhereToken(new TemplateQueryWhereToken(TemplateQueryWhereToken.OPERATOR_EQUAL, "lock", 0));
			filter.addWhereToken(new TemplateQueryWhereToken(TemplateQueryWhereToken.OPERATOR_EQUAL, "unitLoad.lock", 0));
			filter.addWhereToken(new TemplateQueryWhereToken(TemplateQueryWhereToken.OPERATOR_GREATER, "amount", BigDecimal.ZERO));
			break;
		case Goods_out:			
			filter.addWhereToken(new TemplateQueryWhereToken(
					TemplateQueryWhereToken.OPERATOR_EQUAL, "lock", StockUnitLockState.PICKED_FOR_GOODSOUT.getLock()));
			break;
		case Quality_fault:			
			filter.addWhereToken(new TemplateQueryWhereToken(
					TemplateQueryWhereToken.OPERATOR_EQUAL, "lock", StockUnitLockState.QUALITY_FAULT.getLock()));
			break;
		case All: default:
		}
		
		getListData(context, detail, template)
			.thenApplyAsync(FXCollections::observableList, Platform::runLater)
			.thenAccept(source::setItems);					
	}
	
	@Override
	public CompletableFuture<List<BODTO<StockUnit>>>
	getListData(ContextBase context, QueryDetail detail, TemplateQuery template) {
		if (detail.getOrderBy().isEmpty()) {
			detail.addOrderByToken("created", false);
		}
		return super.getListData(context, detail, template);
	}
	
	@Override
	public List<String> getTableColumns() {
		return Arrays.asList("id", 
				"itemData AS itemData.number",	"itemDataName AS itemData.name", 
				"lot AS lot.name", "unitLoad AS unitLoad.labelId", 
				"storageLocation AS unitLoad.storageLocation.name", 
				"amount", "reservedAmount");
	}

	@Override
	protected BODTOTable<StockUnit> getTable(ViewContextBase context) {
		BODTOTable<StockUnit> table =  super.getTable(context);
		table.getCommands()
			.begin(RootCommand.MENU)
				.add(AC.id(Action.LOCK).text("Lock"))
				.add(AC.id(Action.TRANSFER).text("Transfer stock unit"))
				.add(AC.id(Action.CHANGE_AMOUNT).text("Change amount"))
				.add(AC.id(Action.SEND_TO_NIRWANA).text("Send to Nirwana"))
			.end()
		.end();
		QueryUtils.addFilter(table, StockUnitFilter.Available, () -> refresh(table, context));
		return  table;
	}
	
	@Override
	protected MyWMSEditor<StockUnit> getEditor(ContextBase context, TableKey key) {
		MyWMSEditor<StockUnit> editor = super.getEditor(context, key);
		editor.getCommands()
			.begin(RootCommand.MENU)
				.add(AC.id(Action.LOCK).text("Lock"))
				.add(AC.id(Action.TRANSFER).text("Transfer stock unit"))
				.add(AC.id(Action.CHANGE_AMOUNT).text("Change amount"))
				.add(AC.id(Action.SEND_TO_NIRWANA).text("Send to Nirwana"))
			.end()
		.end();
		return editor;
	}
}
