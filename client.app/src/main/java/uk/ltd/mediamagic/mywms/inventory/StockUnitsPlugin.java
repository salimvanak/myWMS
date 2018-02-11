package uk.ltd.mediamagic.mywms.inventory;

import java.beans.PropertyDescriptor;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.mywms.model.StockUnit;

import de.linogistix.los.inventory.crud.StockUnitCRUDRemote;
import de.linogistix.los.inventory.facade.ManageInventoryFacade;
import de.linogistix.los.inventory.query.dto.StockUnitTO;
import de.linogistix.los.inventory.service.StockUnitLockState;
import de.linogistix.los.location.model.LOSUnitLoad;
import de.linogistix.los.location.query.dto.UnitLoadTO;
import de.linogistix.los.query.BODTO;
import de.linogistix.los.query.LOSResultList;
import de.linogistix.los.query.QueryDetail;
import de.linogistix.los.query.TemplateQuery;
import de.linogistix.los.query.TemplateQueryFilter;
import de.linogistix.los.query.TemplateQueryWhereToken;
import javafx.application.Platform;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.util.StringConverter;
import uk.ltd.mediamagic.common.utils.Strings;
import uk.ltd.mediamagic.flow.crud.BODTOPlugin;
import uk.ltd.mediamagic.flow.crud.BODTOTable;
import uk.ltd.mediamagic.flow.crud.BasicEntityEditor;
import uk.ltd.mediamagic.flow.crud.CRUDKeyUtils;
import uk.ltd.mediamagic.flow.crud.MyWMSEditor;
import uk.ltd.mediamagic.flow.crud.SubForm;
import uk.ltd.mediamagic.fx.AwesomeIcon;
import uk.ltd.mediamagic.fx.MDialogs;
import uk.ltd.mediamagic.fx.action.AC;
import uk.ltd.mediamagic.fx.action.RootCommand;
import uk.ltd.mediamagic.fx.controller.list.CellRenderer;
import uk.ltd.mediamagic.fx.controller.list.MaterialCells;
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
import uk.ltd.mediamagic.mywms.transactions.StockUnitRecordAction;

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
		LOCK, SEND_TO_NIRWANA, CHANGE_AMOUNT, TRANSFER, TRANSACTION_LOG 
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
			.withMultiSelection(Action.LOCK, this::lock)
			.withSelection(Action.TRANSFER, this::transfer)
			.withSelection(Action.CHANGE_AMOUNT, this::changeAmount)
			.withMultiSelection(Action.SEND_TO_NIRWANA, this::sendToNirwana)
			.withSelection(Action.TRANSACTION_LOG, StockUnitRecordAction.forStockUnit())
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

	private void sendToNirwana(Object source, Flow flow, ViewContext context, Collection<TableKey> keys) {
		if (keys.isEmpty()) {
			FXErrors.selectionError(context.getRootNode());
			return;
		}
		
		boolean ok = MDialogs.create(context.getRootNode())
				.masthead("Send stock selected units to Nirwana")
				.showYesNo(MDialogs.Yes3);
		
		if (!ok) return;
		
		List<BODTO<StockUnit>> stockUnits = keys.stream().map(k -> (StockUnitTO) CRUDKeyUtils.getTO(k)).collect(Collectors.toList());
	
		ManageInventoryFacade manageInventory = context.getBean(ManageInventoryFacade.class);
		context.getExecutor().call(() -> {
			manageInventory.sendStockUnitsToNirwana(stockUnits);
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
	
	private void lock(Object source, Flow flow, ViewContext context, Collection<TableKey> key) {
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
		withMultiSelectionTO(context, key, k -> {
			long id = k.getId();
			StockUnit su = crud.retrieve(id);
			crud.lock(su, lock, lockCause);			
		});
	}

	
	@Override
	public Supplier<CellRenderer<StockUnit>> createCellFactory() {
		return MaterialCells.withID(s -> (s.getLock() == 0) ? new AwesomeIcon(AwesomeIcon.unlock) : new AwesomeIcon(AwesomeIcon.lock), 
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
		source.clearTable();
		
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
			.thenAcceptAsync(source::setLOSResultList, Platform::runLater);			
	}
	
	@Override
	public CompletableFuture<LOSResultList<BODTO<StockUnit>>>
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
				.seperator()
				.add(AC.id(Action.TRANSACTION_LOG).text("Transaction Log"))
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
				.add(AC.id(Action.TRANSACTION_LOG).text("Transaction Log"))
			.end()
		.end();
		return editor;
	}
}
