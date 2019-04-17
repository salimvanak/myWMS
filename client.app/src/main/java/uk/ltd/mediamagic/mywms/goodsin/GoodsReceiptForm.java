package uk.ltd.mediamagic.mywms.goodsin;

import java.beans.BeanInfo;
import java.beans.PropertyDescriptor;
import java.io.File;
import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.mywms.model.BasicEntity;
import org.mywms.model.Client;
import org.mywms.model.ItemData;

import de.linogistix.los.inventory.crud.LOSAdviceCRUDRemote;
import de.linogistix.los.inventory.facade.AdviceFacade;
import de.linogistix.los.inventory.facade.LOSGoodsReceiptFacade;
import de.linogistix.los.inventory.model.LOSAdvice;
import de.linogistix.los.inventory.model.LOSGoodsReceipt;
import de.linogistix.los.inventory.model.LOSGoodsReceiptPosition;
import de.linogistix.los.inventory.model.LOSGoodsReceiptState;
import de.linogistix.los.inventory.model.StockUnitLabel;
import de.linogistix.los.inventory.query.LOSGoodsReceiptPositionQueryRemote;
import de.linogistix.los.inventory.query.dto.ItemDataTO;
import de.linogistix.los.inventory.query.dto.LOSAdviceTO;
import de.linogistix.los.query.BODTO;
import javafx.application.Platform;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.transformation.SortedList;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Control;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.util.StringConverter;
import uk.ltd.mediamagic.common.utils.MArrays;
import uk.ltd.mediamagic.common.utils.Strings;
import uk.ltd.mediamagic.flow.crud.BasicEntityEditor;
import uk.ltd.mediamagic.flow.crud.MyWMSEditor;
import uk.ltd.mediamagic.flow.crud.MyWMSForm;
import uk.ltd.mediamagic.flow.crud.PoJoEditor;
import uk.ltd.mediamagic.fx.MDialogs;
import uk.ltd.mediamagic.fx.action.AC;
import uk.ltd.mediamagic.fx.binding.MBindings;
import uk.ltd.mediamagic.fx.concurrent.FTask;
import uk.ltd.mediamagic.fx.control.FormBuilder;
import uk.ltd.mediamagic.fx.control.SimpleFormBuilder;
import uk.ltd.mediamagic.fx.controller.editor.EditorBase;
import uk.ltd.mediamagic.fx.converters.BigDecimalConverter;
import uk.ltd.mediamagic.fx.converters.Filters;
import uk.ltd.mediamagic.fx.flow.AutoInject;
import uk.ltd.mediamagic.fx.flow.FXErrors;
import uk.ltd.mediamagic.fx.flow.Flow;
import uk.ltd.mediamagic.fx.table.MTableViewBase;
import uk.ltd.mediamagic.mywms.FlowUtils;
import uk.ltd.mediamagic.mywms.common.FileOutputPane;
import uk.ltd.mediamagic.mywms.common.PDFConcat;
import uk.ltd.mediamagic.mywms.common.PositionComparator;
import uk.ltd.mediamagic.util.Files;

public class GoodsReceiptForm extends MyWMSEditor<LOSGoodsReceipt> {		

		private BasicEntityEditor<LOSAdvice> newAdvice = new BasicEntityEditor<>();
		private BasicEntityEditor<ItemData> newItemData = new BasicEntityEditor<>();
		private TextFormatter<BigDecimal> newQuantity = new TextFormatter<>(
			new BigDecimalConverter(), null, Filters.numeric());


		private final BooleanBinding finished;
		
		@AutoInject public LOSGoodsReceiptFacade facade;
		@AutoInject public LOSAdviceCRUDRemote adviceCRUD;
		@AutoInject public AdviceFacade adviceFacade;
		
		private TabPane tabs = new TabPane();
		
		public GoodsReceiptForm(BeanInfo beanInfo, Function<PropertyDescriptor, StringConverter<?>> getConveryer) {
			super(beanInfo, getConveryer);
			setEditorHelper(new MyEditorHelper(this, beanInfo, getConveryer));
			finished = MBindings.createBoolean(dataProperty(), false, 
					d -> !MArrays.contains(d.getReceiptState(), LOSGoodsReceiptState.RAW, LOSGoodsReceiptState.ACCEPTED));
			
			getCommands()
				.menu("Finish")
				.add(AC.idText("Finish goods receipt").action(s -> finishGoodsReceipt())
						.description("Mark stock ready for storage and lock this Goods Receipt"))
				.add(AC.idText("Finish outstanding advices").action(s -> finishGoodsReceipt())
						.description("Mark and outstanding advices on this goods receipt as finished"))
					.end()
			.end();
						
			tabs.getTabs().add(new Tab("Main", createMainTab()));
			tabs.getTabs().add(new Tab("Positions", createAssignmentsTab()));
		
			setView(tabs);
		}

		public Node createMainTab() {
			ListView<LOSAdvice> advices = MyWMSForm.createList(LOSAdvice.class);

			Button addAdviceButton = new Button("Add");
			Button deleteAdviceButton = new Button("Del");

			SimpleFormBuilder form = new SimpleFormBuilder();
			form.row()
				.label("ID").field("id")
				.label("Client").comboBox("client")
				.label("Created").field("created")
				.label("Modified").field("modified")
			.end();
			form.row()
				.label("Receipt No").field("goodsReceiptNumber")
				.label("Operator").fieldNode("operator", new BasicEntityEditor<>(), 1, 1)
				.label("Lock").comboBox("lock")
				.label("Locked").checkbox("locked")
			.end();
			form.row()
				.label("Reference").field("referenceNo")
				.label("Receipt Date").field("receiptDate")
				.label("Location").fieldNode("goodsInLocation", new BasicEntityEditor<>(), 1, 1)
				.label("Receipt State").comboBox("receiptState")
			.end();
			form.row()
				.label("Delivery No").field("deliveryNoteNumber")
				.label("Forwarder").field("forwarder")
				.label("Driver").field("driverName")
				.label("Licence Plate").field("licencePlate")
			.end();

			TextField newQuantityField = Filters.of(newQuantity, "Qty", 6);
			TextField newDescription = new TextField();
			newDescription.setEditable(false);
			newDescription.setPrefColumnCount(20);
			newDescription.setMaxWidth(Double.MAX_VALUE);
						
			addAdviceButton.setOnAction(e -> addAdviceAction(addAdviceButton));
			deleteAdviceButton.setOnAction(e -> {
				LOSAdvice advice = advices.getSelectionModel().getSelectedItem();			
				deleteAdviceAction(deleteAdviceButton, advice);
			});
			
			newDescription.textProperty().bind(MBindings.get(newItemData.valueProperty(), ItemData::getDescription));
			newItemData.disableProperty().bind(newAdvice.valueProperty().isNotNull());
			newQuantityField.disableProperty().bind(newAdvice.valueProperty().isNotNull());
			newAdvice.valueProperty().addListener((v,o,n) -> {
				if (n != null) {
					newItemData.setValue(n.getItemData());
					newQuantity.setValue(n.getNotifiedAmount());
				}
			});
			
			form.sub("Advices")
				.doubleRow()
					.label("Advice").fieldNode(newAdvice)
					.label("Item").fieldNode(newItemData)
					.label("Description").fieldNode(newDescription)
					.label("Quantity").fieldNode(newQuantityField)
					.fieldNode(addAdviceButton)
					.fieldNode(deleteAdviceButton)
				.end()
				.row().fieldNode("assignedAdvices", advices, GridPane.REMAINING, 1)
			.end(new RowConstraints(10d, 2000d, Double.MAX_VALUE));
			
			form.bindController(this);
			newAdvice.disableProperty().bind(finished);
			newItemData.disableProperty().bind(finished);
			newDescription.disableProperty().bind(finished);
			newQuantityField.disableProperty().bind(finished);
			addAdviceButton.disableProperty().bind(finished);
			deleteAdviceButton.disableProperty().bind(finished);
			return form;
		}
		
		public Node createAssignmentsTab() {
			
			TableView<LOSAdvice> advices = createAdviceTable();
			TableView<LOSGoodsReceiptPosition> positions = createGoodsReceiptsTable();

			Button assignPositionButton = new Button("Assign");
			Button deletePositionButton = new Button("Remove");
			Button printPositionButton = new Button("Print");
			Button selectAllPositionButton = new Button("Select All");
			
			FormBuilder form = new FormBuilder();
			form.row()
				.label("Receipt No").text("").forColumn("goodsReceiptNumber_2")
					.withLastNode(f -> EditorBase.setField(f, "#goodsReceiptNumber"))
				.label("Reference").text("").forColumn("referenceNo_2")
					.withLastNode(f -> EditorBase.setField(f, "#referenceNo"))
				.label("Location").fieldNode(new BasicEntityEditor<>(), 1, 1).forColumn("goodsInLocation_2")
					.withLastNode(f -> EditorBase.setField(f, "#goodsInLocation"))
			.end();
			form.row()
				.label("Receipt State").comboBox().forColumn("receiptState_2")
					.withLastNode(f -> EditorBase.setField(f, "#receiptState"))
				.label("Delivery No").text("").forColumn("deliveryNoteNumber_2")
					.withLastNode(f -> EditorBase.setField(f, "#deliveryNoteNumber"))
			.end();

			positions.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

			selectAllPositionButton.setOnAction(e -> {
				if (positions.getSelectionModel().getSelectedItems().size() <= 1) {
					positions.getSelectionModel().selectAll();
				}
				else {
					positions.getSelectionModel().clearSelection();
				}
			});
			
			assignPositionButton.setOnAction(e -> {
				LOSAdvice advice = advices.getSelectionModel().getSelectedItem();			
				assignPositionsAction(advice);
			});
			deletePositionButton.setOnAction(e -> {
				LOSGoodsReceiptPosition pos = positions.getSelectionModel().getSelectedItem();
				deletePositionsAction(deletePositionButton, pos);
			});
			printPositionButton.setOnAction(e -> {
				List<LOSGoodsReceiptPosition> pos = positions.getSelectionModel().getSelectedItems();
				printPositionsAction(pos);
			});
						
			FormBuilder.Row adviceRow = form.sub("Advices").row();
			adviceRow.fieldNode(advices, GridPane.REMAINING, 1)
				.forColumn("assignedAdvices_2")
				.withLastNode(f -> EditorBase.setField(f, "#assignedAdvices"))
			.end(new RowConstraints(10d, 2000d, Double.MAX_VALUE));
			adviceRow.end();
			
			FormBuilder positionForm = form.sub("Positions");
			positionForm.row()
				.fieldNode(assignPositionButton)
				.fieldNode(deletePositionButton)
				.fieldNode(printPositionButton)
				.fieldNode(selectAllPositionButton)
			.end();

			assignPositionButton.disableProperty().bind(finished);
			deletePositionButton.disableProperty().bind(finished);
			
			FormBuilder.Row positionsRow = positionForm.row();
			positionsRow.fieldNode(positions, GridPane.REMAINING, 1)
				.forColumn("positionList_2")
				.withLastNode(f -> EditorBase.setField(f, "#positionList"))
			.end(new RowConstraints(10d, 2000d, Double.MAX_VALUE));
			
			positionForm.end();

			form.bindController(this);
			return form;
		}
				
		@PostConstruct
		public void post() {
			newAdvice.configure(getContext(), LOSAdvice.class);
			newItemData.configure(getContext(), ItemData.class);
			
			newAdvice.setFetchCompleteions(this::fetchAdviceList);
			newItemData.setFetchCompleteions(this::fetchItemDataList);
		}
		
		public void finishGoodsReceipt() {
			LOSGoodsReceipt gr = getData();
			saveAndRefresh();
			getExecutor().executeAndWait(getView(), () -> {
				facade.finishGoodsReceipt(gr);
				return null;
			});
			restoreState(getContext(), true);
		}

		public void finishAdvices() {
			LOSGoodsReceipt gr = getData();
			saveAndRefresh();
			getExecutor().executeAndWait(getView(), () -> {
				for (LOSAdvice a : gr.getAssignedAdvices()) {
					adviceFacade.finishAdvise(new LOSAdviceTO(a));
				}
				return null;
			});
			restoreState(getContext(), true);
		}

		public void clearAdvice() {
			newAdvice.setValue(null);
			newItemData.setValue(null);
			newQuantity.setValue(null);
		}
		
		public CompletableFuture<List<BODTO<LOSAdvice>>> fetchAdviceList(String s) {
			Client client = getData().getClient();
//			ItemData item = newItemData.getValue();
			return getExecutor().call(() -> facade.getAllowedAdvices(s,  new BODTO<>(client), null, null))
					.thenApply(l -> l.stream().map(LOSAdviceTO::new).collect(Collectors.<BODTO<LOSAdvice>>toList()));
		}

		public CompletableFuture<List<BODTO<ItemData>>> fetchItemDataList(String s) {
			Client client = getData().getClient();
			return getExecutor().call(() -> facade.getAllowedItemData(s, new BODTO<>(client), null));
		}
		
		private void addAdviceAction(Control addAdviceButton) {
			if (finished.get()) {
				FXErrors.error(getView(), "Cannot add advice to finished order");
				return;
			}
			LOSAdviceTO advice;
			LOSGoodsReceipt receipt = getData();
			if (newAdvice.getValue() != null) {
				advice = new LOSAdviceTO(newAdvice.getValue());
				getExecutor().runAndDisable(addAdviceButton, p -> {
					facade.assignLOSAdvice(advice, receipt);
					return null;
				})
				.thenAcceptAsync(l -> FlowUtils.executeCommand(getContext(), Flow.REFRESH_ACTION), Platform::runLater)
				.thenAcceptAsync(l -> clearAdvice(), Platform::runLater);
			}
			else {
				if (this.newItemData.getValue() == null) FXErrors.error(getView(), "Invalid item number");
				BODTO<Client> client = new BODTO<>(getData().getClient());
				ItemDataTO itemData = new ItemDataTO(this.newItemData.getValue());
				BigDecimal qty = this.newQuantity.getValue();
				if (qty == null || qty.compareTo(BigDecimal.ZERO) <= 0) FXErrors.error(getView(), "Invalid quantity");
				LOSGoodsReceipt gr = getData();
				getExecutor().runAndDisable(addAdviceButton, p -> {
					LOSAdvice newAdvice = adviceFacade.createAdvise(client, itemData, null, qty, false, Date.valueOf(LocalDate.now()), null);
					facade.assignLOSAdvice(new LOSAdviceTO(newAdvice), gr);
					return null;
				})
				.thenAcceptAsync(l -> FlowUtils.executeCommand(getContext(), Flow.REFRESH_ACTION), Platform::runLater)
				.thenAcceptAsync(l -> clearAdvice(), Platform::runLater);				
			}
		}

		private void deleteAdviceAction(Control addAdviceButton, LOSAdvice advice) {
			if (finished.get()) {
				FXErrors.error(getView(), "Cannot delete advice from finished order");
				return;
			}
			boolean yes = MDialogs.create(getView(), "Delete positions")
				.masthead("Delete selection positions")
				.showYesNo();

			if (!yes) return; // user cancelled.
			
			LOSGoodsReceipt receipt = getData();
			if (advice != null) {
				LOSAdviceTO adviceTo = new LOSAdviceTO(advice);
				getExecutor().runAndDisable(addAdviceButton, p -> {
					facade.removeAssigendLOSAdvice(adviceTo, receipt);
					return null;
				})
				.thenAcceptAsync(l -> FlowUtils.executeCommand(getContext(), Flow.REFRESH_ACTION), Platform::runLater)
				.thenAcceptAsync(l -> clearAdvice(), Platform::runLater);
			}
			else {
				FXErrors.selectionError(getView());
			}
		}

		private void assignPositionsAction(LOSAdvice advice) {
			if (finished.get()) {
				FXErrors.error(getView(), "Cannot assign position to finished order");
				return;
			}
			if (advice != null) {
				CreateGoodsReceiptPosition c = new CreateGoodsReceiptPosition(getData(), advice);
				getContext().autoInjectBean(c);
				FlowUtils.showPopup("assign-positions", getContext(), c);
			}
			else {
				FXErrors.selectionError(getView(), "Select an advice");
			}
		}

		private void deletePositionsAction(Control addAdviceButton, LOSGoodsReceiptPosition pos) {
			if (finished.get()) {
				FXErrors.error(getView(), "Cannot delete position from finished order");
				return;
			}
			LOSGoodsReceipt receipt = getData();
			if (pos != null) {
				getExecutor().runAndDisable(addAdviceButton, p -> {
					facade.removeGoodsReceiptPosition(receipt, pos);
					return null;
				})
				.thenAcceptAsync(l -> FlowUtils.executeCommand(getContext(), Flow.REFRESH_ACTION), Platform::runLater)
				.thenAcceptAsync(l -> clearAdvice(), Platform::runLater);
			}
			else {
				FXErrors.selectionError(getView());
			}
		}

		private void printPositionsAction(List<LOSGoodsReceiptPosition> selection) {
			if (selection != null && selection.size() > 0) {
				List<Long> ids = selection.stream()
						.map(BasicEntity::getId)
						.collect(Collectors.toList());
				
				LOSGoodsReceiptPositionQueryRemote query = getContext().getBean(LOSGoodsReceiptPositionQueryRemote.class);
				
				FTask<File> task = getContext().getExecutor().fileGenerator(getContext(), "Stock unit labels", p -> {
					p.setSteps(ids.size());
					File file = Files.createTempFile("stock-unit", ".pdf");
					try (PDFConcat concat = new PDFConcat(file)) {
						for (Long id: ids) {
							LOSGoodsReceiptPosition pos = query.queryById(id);
							StockUnitLabel doc = facade.createStockUnitLabel(pos, null);
							concat.add(doc);
							p.step();
						}
						return file;
					}
				});
				FileOutputPane.show("Stock unit labels", getContext(), task);
			}
			else {
				FXErrors.selectionError(getView());
			}
		}
	
		public TableView<LOSGoodsReceiptPosition> createGoodsReceiptsTable() {
			
			MTableViewBase<LOSGoodsReceiptPosition> t = new MTableViewBase<>();
			t.setColumns(
					t.column().title("Item").valueFactory(LOSGoodsReceiptPosition::getItemData).show(),
					t.column().title("Lot").valueFactory(LOSGoodsReceiptPosition::getLot).show(),
					t.column().title("Unit load").valueFactory(LOSGoodsReceiptPosition::getUnitLoad).show(),
					t.column(new BigDecimalConverter()).title("Qty").valueFactory(LOSGoodsReceiptPosition::getAmount).show(),
					t.column().title("Advice").valueFactory(LOSGoodsReceiptPosition::getPositionNumber).show()
					);
			return t;
		}

		public TableView<LOSAdvice> createAdviceTable() {
			
			MTableViewBase<LOSAdvice> t = new MTableViewBase<>();
			t.setColumns(
					t.column().title("Advice").width(15).valueFactory(LOSAdvice::getAdviceNumber).show(),
					t.column().title("Item").width(15).valueFactory((LOSAdvice l) -> l.getItemData().getNumber()).show(),
					t.column().title("Description").valueFactory((LOSAdvice l) -> l.getItemData().getName()).show(),
					t.column(new BigDecimalConverter()).title("Exp").valueFactory(LOSAdvice::getNotifiedAmount).show(),
					t.column(new BigDecimalConverter()).title("Rec").valueFactory(LOSAdvice::getReceiptAmount).show()
					);
			return t;
		}

		private static class MyEditorHelper extends MyWMSEditorHelper<LOSGoodsReceipt> {

			
			public MyEditorHelper(PoJoEditor<LOSGoodsReceipt> editor, BeanInfo beanInfo,
					Function<PropertyDescriptor, StringConverter<?>> getConverter) {
				super(editor, beanInfo, getConverter);
			}


			public ObservableValue<?> getValueProperty(String id) {
				if (Strings.equals(id, "positionList")) {
					Comparator<LOSGoodsReceiptPosition> comparator = Comparator.comparing(
							LOSGoodsReceiptPosition::getPositionNumber,  
							new PositionComparator());
					
					return MBindings.get(dataProperty(), d -> new SortedList<LOSGoodsReceiptPosition>(
							FXCollections.observableList(d.getPositionList()), comparator)); 
				}
				else {
					return super.getValueProperty(id);
				}
			}
			
		}
	}