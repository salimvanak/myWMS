package uk.ltd.mediamagic.mywms.goodsin;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Date;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import javax.annotation.PostConstruct;

import org.mywms.model.Client;
import org.mywms.model.ItemData;
import org.mywms.model.Lot;
import org.mywms.model.UnitLoadType;

import de.linogistix.los.crud.LotCRUDRemote;
import de.linogistix.los.inventory.facade.LOSGoodsReceiptFacade;
import de.linogistix.los.inventory.model.LOSAdvice;
import de.linogistix.los.inventory.model.LOSGoodsReceipt;
import de.linogistix.los.inventory.model.LOSGoodsReceiptType;
import de.linogistix.los.inventory.query.LotQueryRemote;
import de.linogistix.los.inventory.query.dto.ItemDataTO;
import de.linogistix.los.inventory.query.dto.LOSAdviceTO;
import de.linogistix.los.inventory.query.dto.LotTO;
import de.linogistix.los.query.BODTO;
import de.linogistix.los.query.exception.BusinessObjectNotFoundException;
import javafx.beans.binding.StringBinding;
import javafx.event.Event;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.control.ToggleGroup;
import uk.ltd.mediamagic.common.data.DataCheckException;
import uk.ltd.mediamagic.common.utils.Strings;
import uk.ltd.mediamagic.flow.crud.BasicEntityEditor;
import uk.ltd.mediamagic.fx.FxExceptions;
import uk.ltd.mediamagic.fx.MDialogs;
import uk.ltd.mediamagic.fx.binding.BigDecimalBinding;
import uk.ltd.mediamagic.fx.binding.MBindings;
import uk.ltd.mediamagic.fx.control.SimpleFormBuilder;
import uk.ltd.mediamagic.fx.controller.ControllerCommandBase;
import uk.ltd.mediamagic.fx.converters.BigDecimalConverter;
import uk.ltd.mediamagic.fx.converters.Filters;
import uk.ltd.mediamagic.fx.converters.IntegerConverter;
import uk.ltd.mediamagic.fx.data.DataCheck;
import uk.ltd.mediamagic.fx.flow.AutoInject;
import uk.ltd.mediamagic.fx.flow.Flow;
import uk.ltd.mediamagic.fx.helpers.ComboBoxes;
import uk.ltd.mediamagic.mywms.FlowUtils;
import uk.ltd.mediamagic.util.Closures;
import uk.ltd.mediamagic.util.DateUtils;

public class CreateGoodsReceiptPosition extends ControllerCommandBase {
 
	public @AutoInject LOSGoodsReceiptFacade facade;
	public @AutoInject LotQueryRemote lotQuery;
	public @AutoInject LotCRUDRemote lotCRUD;
	
	private final LOSGoodsReceipt goodsReceipt;
	ComboBox<LOSGoodsReceiptType> receiptType = ComboBoxes.createComboForEnum(LOSGoodsReceiptType.INTAKE); 
	BasicEntityEditor<UnitLoadType> unitloadType = new BasicEntityEditor<>();
	TextField unitLoadID = new TextField();
	BasicEntityEditor<ItemData> itemData = new BasicEntityEditor<>();
	BasicEntityEditor<LOSAdvice> advice = new BasicEntityEditor<>();
	TextFormatter<BigDecimal> unitLoadAmount = new TextFormatter<>(new BigDecimalConverter(), null, Filters.numeric());
	TextFormatter<Integer> unitLoadCount = new TextFormatter<>(new IntegerConverter(), null, Filters.numeric());
	TextFormatter<BigDecimal> remainingAmount = new TextFormatter<>(new BigDecimalConverter(), null, Filters.numeric());
	
	RadioButton noLotAssignment = new RadioButton("No lot assignment");
	RadioButton useExistingLot = new RadioButton("Use existing lot");
	RadioButton createNewLot = new RadioButton("Create new lot");
	ToggleGroup lotButtonGroup = new ToggleGroup();
	BasicEntityEditor<Lot> lot = new BasicEntityEditor<>();
	
	TextField lotName = new TextField();
	DatePicker notUseBefore = new DatePicker();
	DatePicker bestBeforeEnd = new DatePicker();
	
	StringBinding adviceDescription;
	StringBinding lotDescription;
	StringBinding itemDataDescription;
	
	DataCheck dataCheck = DataCheck.with(Collections.emptyMap());
	
	SimpleFormBuilder form = new SimpleFormBuilder();
	
	public CreateGoodsReceiptPosition(LOSGoodsReceipt goodsReceipt, LOSAdvice advice) {
		this.goodsReceipt = goodsReceipt;
		this.advice.setValue(advice);
		
		adviceDescription = MBindings.asString(this.advice.valueProperty(), l -> Strings.format("Remaining {0}", l.getDiffAmount()));
		lotDescription = MBindings.asString(lot.valueProperty(), Lot::getName);
		itemDataDescription = MBindings.asString(itemData.valueProperty(), ItemData::getDescription);		

		unitLoadID.setPromptText("Generate ID");
		
		getCommands()
			.okCancel(this::okPressed)
		.end();
		
		form.sub("Stock Item Data")
			.doubleRow()
				.label("Advice").fieldNode(this.advice).field(adviceDescription)
			.end()
			.doubleRow()
				.label("Item Data").fieldNode(itemData).field(itemDataDescription)
			.end()
		.end();
		form.doubleRow()
			.label("Receipt Type").fieldNode(receiptType)
	  .end();
		form.doubleRow()
			.label("Unit Load Type").fieldNode(unitloadType)
			.label("Unit Load ID").fieldNode(unitLoadID)
		.end();
		form.doubleRow()
			.label("Qty per unit load").fieldNode(Filters.of(unitLoadAmount, 6))
			.label("No of unit loads").fieldNode(Filters.of(unitLoadCount, 6))
			.label("Remaining").fieldNode(Filters.of(remainingAmount, 6))
		.end();
		form.sub("Lot Information")
			.row()
				.fieldNode(noLotAssignment).fieldNode(useExistingLot).fieldNode(createNewLot)
			.end()
			.doubleRow()
				.label("Lot").fieldNode(lot).field(lotDescription)
			.end()
			.doubleRow()
				.label("Lot Number").fieldNode(lotName)
				.label("Not use before").fieldNode(notUseBefore)
				.label("Best before end").fieldNode(bestBeforeEnd)
			.end()
		.end();
		
		lot.valueProperty().addListener(o -> {
			if (useExistingLot.isSelected()) {
				lotName.setText(Closures.guardedValue(lot.getValue(),  Lot::getName, ""));
				bestBeforeEnd.setValue(Closures.guardedValue(lot.getValue(), l -> DateUtils.toLocalDate(l.getBestBeforeEnd()), null));
				notUseBefore.setValue(Closures.guardedValue(lot.getValue(),  l -> DateUtils.toLocalDate(l.getUseNotBefore()), null));
			}
		});
		
		lotButtonGroup.getToggles().addAll(useExistingLot, createNewLot, noLotAssignment);
		lotButtonGroup.selectToggle(noLotAssignment);
		
		this.itemData.setDisable(true);
		this.advice.setDisable(true);
		
		itemData.valueProperty().bind(MBindings.get(this.advice.valueProperty(), LOSAdvice::getItemData));
		
		remainingAmount.valueProperty().bind(BigDecimalBinding.create(this::calculateAmountRemaining, 
				unitLoadAmount.valueProperty(),
				unitLoadCount.valueProperty(),
				this.advice.valueProperty()));
		
		lot.disableProperty().bind(useExistingLot.selectedProperty().not());
		lotName.disableProperty().bind(createNewLot.selectedProperty().not());
		notUseBefore.disableProperty().bind(createNewLot.selectedProperty().not());
		bestBeforeEnd.disableProperty().bind(createNewLot.selectedProperty().not());
		
		unitLoadAmount.valueProperty().addListener((v,o,n) -> {
			BigDecimal remaining = advice.getDiffAmount();
			BigDecimal loads = remaining.divide(n, RoundingMode.HALF_EVEN);
			if (n.multiply(new BigDecimal(loads.intValue())).compareTo(remaining) > 0) {
				unitLoadCount.setValue(loads.intValue()-1);				
			}
			else {
				unitLoadCount.setValue(loads.intValue());
			}
		});
	}
	
	@PostConstruct
	public void post() {
		itemData.configure(getContext(), ItemData.class);
		advice.configure(getContext(), LOSAdvice.class);
		lot.configure(getContext(), Lot.class);
		lot.setFetchCompleteions(this::fetchLotsList);
		unitloadType.configure(getContext(), UnitLoadType.class);
	}
	
	private BigDecimal calculateAmountRemaining() {
		BigDecimal amount = unitLoadAmount.getValue();
		Integer count = unitLoadCount.getValue();
		BigDecimal diffAmount = this.advice.getValue().getDiffAmount();
		
		return diffAmount.subtract(amount.multiply(new BigDecimal(count)));		
	}
	
	private CompletableFuture<List<BODTO<Lot>>> fetchLotsList(String s) {
		BODTO<Client> client = new BODTO<Client>(goodsReceipt.getClient());
		ItemDataTO itemData = new ItemDataTO(this.itemData.getValue());
		return getExecutor().call(() -> facade.getAllowedLots(s, client, itemData));
	}
	
	@Override
	public Node getView() {
		return form;
	}
	
	private void okPressed(Event e) {
		try {
			DataCheck.with(Collections.emptyMap())
			.check("Invalid UnitLoadType", () -> unitloadType.getValue() != null)
			.check("Invalid ItemData", () -> itemData.getValue() != null)
			.check("Invalud unit load amount", () -> unitLoadAmount.getValue() != null)
			.check("Invalud unit load amount", () -> unitLoadAmount.getValue().compareTo(BigDecimal.ZERO) > 0)
			.check("Invalud unit load count", () -> unitLoadCount.getValue() != null && unitLoadCount.getValue() > 0);
		} 
		catch (DataCheckException e1) {
			FxExceptions.exceptionThrown(e1);
			return;	
		}
	
	
		BigDecimal amount = unitLoadAmount.getValue();
		Integer count = unitLoadCount.getValue();
		BigDecimal diffAmount = this.advice.getValue().getDiffAmount();
		BigDecimal remainingAmount = calculateAmountRemaining();
		if (remainingAmount.compareTo(BigDecimal.ZERO) < 0) {
			boolean yes = MDialogs.create(getView(), "Over received")
				.masthead("Total item count is greater that the advised amount (" + remainingAmount.negate() + " over).")
				.message("Do you want to continue?")
				.showYesNo();
			
			if (!yes) return; // user cancelled.
		}
		
		ItemData itemData = this.itemData.getValue();
		Client client = goodsReceipt.getClient();
		String unitLoadLabel = this.unitLoadID.getText();
		UnitLoadType unitLoadType = this.unitloadType.getValue();
		LOSGoodsReceiptType receiptType = this.receiptType.getValue();
		BODTO<Client> clientTo = new BODTO<Client>(client);
		LOSAdviceTO adviceTo = new LOSAdviceTO(this.advice.getValue());
		ItemDataTO itemDataTo = new ItemDataTO(itemData);
		BODTO<UnitLoadType> unitLoadTypeTo = new BODTO<>(unitLoadType);
		
		Lot lotIn = this.lot.getValue();
		boolean useExistingLot = this.useExistingLot.isSelected();
		boolean createNewLot = this.createNewLot.isSelected();
		String lotName = this.lotName.getText();
		Date validTo = (this.bestBeforeEnd.getValue() == null) ? null : Date.valueOf(this.bestBeforeEnd.getValue());
		Date validFrom = (this.notUseBefore.getValue() == null) ?  null : Date.valueOf(this.notUseBefore.getValue());
		
		getExecutor().execute(p -> {
			Lot lot;
			if (useExistingLot) {
				lot = lotIn;
			}
			else if (createNewLot){
				try {
					lot = lotQuery.queryByNameAndItemData(client, lotName, itemData);
					// no exception was thrown so this lot must exist
					if (lot != null) throw new DataCheckException("LotName", "Lot name alread exists.");
				} 
				catch (BusinessObjectNotFoundException notFoundException) {
					// we expect this exception as we are check to see if the lot exists.
					// do nothing;
				}
				lot = facade.createLot(client, lotName, itemData, validTo);
				lot.setUseNotBefore(validFrom);
				lotCRUD.update(lot);
			}
			else {
				lot = null;
			}
			LotTO lotTo = (lot == null) ? null : new LotTO(lot);
			
			p.setSteps(count);
			for (int i = 0; i < count; i++) {
				p.step();
				facade.createGoodsReceiptPosition(clientTo, goodsReceipt, lotTo, itemDataTo, unitLoadLabel, 
						unitLoadTypeTo, amount, adviceTo, receiptType, 0, null);			
			}
			
			return null;
		})
		.thenRunUI(() -> {
			FlowUtils.executeCommand(getContext(), Flow.BACK_ACTION);
			FlowUtils.executeCommand(getContext(), Flow.REFRESH_ACTION);
		});
	}
}
