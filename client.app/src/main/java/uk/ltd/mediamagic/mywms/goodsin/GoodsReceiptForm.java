package uk.ltd.mediamagic.mywms.goodsin;

import java.beans.BeanInfo;
import java.beans.PropertyDescriptor;
import java.io.File;
import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDate;
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
import de.linogistix.los.inventory.model.StockUnitLabel;
import de.linogistix.los.inventory.query.LOSGoodsReceiptPositionQueryRemote;
import de.linogistix.los.inventory.query.dto.ItemDataTO;
import de.linogistix.los.inventory.query.dto.LOSAdviceTO;
import de.linogistix.los.query.BODTO;
import javafx.application.Platform;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.layout.GridPane;
import javafx.util.StringConverter;
import uk.ltd.mediamagic.flow.crud.BasicEntityEditor;
import uk.ltd.mediamagic.flow.crud.MyWMSEditor;
import uk.ltd.mediamagic.flow.crud.MyWMSForm;
import uk.ltd.mediamagic.fx.MDialogs;
import uk.ltd.mediamagic.fx.action.AC;
import uk.ltd.mediamagic.fx.binding.MBindings;
import uk.ltd.mediamagic.fx.concurrent.FTask;
import uk.ltd.mediamagic.fx.control.SimpleFormBuilder;
import uk.ltd.mediamagic.fx.converters.BigDecimalConverter;
import uk.ltd.mediamagic.fx.converters.Filters;
import uk.ltd.mediamagic.fx.flow.AutoInject;
import uk.ltd.mediamagic.fx.flow.FXErrors;
import uk.ltd.mediamagic.fx.flow.Flow;
import uk.ltd.mediamagic.mywms.FlowUtils;
import uk.ltd.mediamagic.mywms.common.FileOutputPane;
import uk.ltd.mediamagic.mywms.common.PDFConcat;
import uk.ltd.mediamagic.util.Files;

public class GoodsReceiptForm extends MyWMSEditor<LOSGoodsReceipt> {
		private ListView<LOSAdvice> advices = MyWMSForm.createList(LOSAdvice.class);
		private ListView<LOSGoodsReceiptPosition> positions = MyWMSForm.createList(LOSGoodsReceiptPosition.class);

		private Button addAdviceButton = new Button("Add");
		private Button deleteAdviceButton = new Button("Del");
		private Button assignPositionButton = new Button("Assign");
		private Button deletePositionButton = new Button("Remove");
		private Button printPositionButton = new Button("Print");
		private Button selectAllPositionButton = new Button("Select All");

		private BasicEntityEditor<LOSAdvice> newAdvice = new BasicEntityEditor<>();
		private BasicEntityEditor<ItemData> newItemData = new BasicEntityEditor<>();
		private TextFormatter<BigDecimal> newQuantity = new TextFormatter<>(
				new BigDecimalConverter(), null, Filters.numeric());
		
		@AutoInject public LOSGoodsReceiptFacade facade;
		@AutoInject public LOSAdviceCRUDRemote adviceCRUD;
		@AutoInject public AdviceFacade adviceFacade;
		
		public GoodsReceiptForm(BeanInfo beanInfo, Function<PropertyDescriptor, StringConverter<?>> getConveryer) {
			super(beanInfo, getConveryer);
			
			getCommands()
				.add(AC.idText("Finish Receipt").action(s -> finishGoodsReceipt())
						.description("Make stock read for storage and lock this Goods Receipt"))
			.end();
			
			setUserPermissions(getUserPermissions());
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
			
			positions.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
			
			addAdviceButton.setOnAction(e -> addAdviceAction());
			deleteAdviceButton.setOnAction(e -> deleteAdviceAction());
			selectAllPositionButton.setOnAction(e -> {
				if (positions.getSelectionModel().getSelectedItems().size() <= 1) {
					positions.getSelectionModel().selectAll();
				}
				else {
					positions.getSelectionModel().clearSelection();
				}
			});
			
			assignPositionButton.setOnAction(e -> assignPositionsAction());
			deletePositionButton.setOnAction(e -> deletePositionsAction());
			printPositionButton.setOnAction(e -> printPositionsAction());
			
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
			.end();
			form.sub("Positions")
				.row()
					.fieldNode(assignPositionButton)
					.fieldNode(deletePositionButton)
					.fieldNode(printPositionButton)
					.fieldNode(selectAllPositionButton)
				.end()
				.row().fieldNode("positionList", positions, GridPane.REMAINING, 1)
			.end();
			
			form.bindController(this);
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
			saveAndRefresh();
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
		
		private void addAdviceAction() {
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

		private void deleteAdviceAction() {
			boolean yes = MDialogs.create(getView(), "Delete positions")
				.masthead("Delete selection positions")
				.showYesNo();

			if (!yes) return; // user cancelled.
			
			LOSAdvice advice = advices.getSelectionModel().getSelectedItem();
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

		private void assignPositionsAction() {
			LOSAdvice advice = advices.getSelectionModel().getSelectedItem();			
			if (advice != null) {
				CreateGoodsReceiptPosition c = new CreateGoodsReceiptPosition(getData(), advice);
				getContext().autoInjectBean(c);
				FlowUtils.showPopup("assign-positions", getContext(), c);
			}
			else {
				FXErrors.selectionError(getView(), "Select an advice");
			}
		}

		private void deletePositionsAction() {
			LOSGoodsReceiptPosition pos = positions.getSelectionModel().getSelectedItem();
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

		private void printPositionsAction() {
			List<LOSGoodsReceiptPosition> selection = positions.getSelectionModel().getSelectedItems();
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

	}