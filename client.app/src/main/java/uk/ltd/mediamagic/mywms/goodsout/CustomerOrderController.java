package uk.ltd.mediamagic.mywms.goodsout;

import java.beans.BeanInfo;
import java.beans.PropertyDescriptor;
import java.math.BigDecimal;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.function.Function;

import org.mywms.model.BasicEntity;
import org.mywms.model.ItemData;
import org.mywms.model.Lot;

import de.linogistix.los.inventory.facade.LOSOrderFacade;
import de.linogistix.los.inventory.facade.OrderPositionTO;
import de.linogistix.los.inventory.model.LOSCustomerOrder;
import de.linogistix.los.inventory.model.LOSCustomerOrderPosition;
import de.linogistix.los.inventory.query.LOSCustomerOrderQueryRemote;
import de.linogistix.los.location.model.LOSStorageLocation;
import de.linogistix.los.model.State;
import javafx.beans.binding.ObjectBinding;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.util.StringConverter;
import uk.ltd.mediamagic.flow.crud.BasicEntityEditor;
import uk.ltd.mediamagic.fx.binding.MBindings;
import uk.ltd.mediamagic.fx.converters.BigDecimalConverter;
import uk.ltd.mediamagic.fx.converters.DateTimeConverter;
import uk.ltd.mediamagic.fx.converters.Filters;
import uk.ltd.mediamagic.fx.flow.FXErrors;
import uk.ltd.mediamagic.fx.table.MTableViewBase;
import uk.ltd.mediamagic.util.Closures;

public class CustomerOrderController extends GoodsOutEditController<LOSCustomerOrder> implements Initializable {

	@FXML private BasicEntityEditor<LOSStorageLocation> field_destination;
	@FXML private BasicEntityEditor<ItemData> itemData;
	@FXML private BasicEntityEditor<Lot> lot;
	@FXML private TextField quantity;
	@FXML private Button addButton;
	@FXML private Button deleteButton;
	@FXML private MTableViewBase<LOSCustomerOrderPosition> field_positions;
	
	private TextFormatter<BigDecimal> quantityFormatter = 
			new TextFormatter<>(new BigDecimalConverter(), null, Filters.numeric());

	public CustomerOrderController(BeanInfo beanInfo, Function<PropertyDescriptor, StringConverter<?>> getConverter) {
		super(beanInfo, getConverter);
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		field_destination.setFetchCompleteions(this::getStorageLocations);
		itemData.configure(getContext(), ItemData.class);
		lot.configure(getContext(), Lot.class);
		quantity.setTextFormatter(quantityFormatter);
		addButton.setOnAction(this::addPosition);
		deleteButton.setOnAction(this::deletePosition);
		
		ObjectBinding<Boolean> orderReleased = MBindings.get(dataProperty(), d -> (d.getState() >= State.RELEASED));
		addButton.disableProperty().bind(orderReleased);
		deleteButton.disableProperty().bind(orderReleased);
		
		MTableViewBase<LOSCustomerOrderPosition> t = field_positions;
		t.setColumns(
				t.column().title("Item No.").width(10).valueFactory((LOSCustomerOrderPosition p) -> p.getItemData().toUniqueString()).show(),
				t.column().title("Description").valueFactory((LOSCustomerOrderPosition p) -> p.getItemData().getDescription()).show(),
				t.column().title("Lot").valueFactory(Closures.nullsTo((LOSCustomerOrderPosition p) ->  p.getLot().toUniqueString(), "N/A")).show(),
				t.column(new DateTimeConverter("dd-MMM-yyyy")).title("Best Before").valueFactory(Closures.nullsTo((LOSCustomerOrderPosition p) ->  p.getLot().getBestBeforeEnd(), null)).hide(),
				t.column(new DateTimeConverter("dd-MMM-yyyy")).title("Not Before").valueFactory(Closures.nullsTo((LOSCustomerOrderPosition p) ->  p.getLot().getUseNotBefore(), null)).hide(),
				t.column(new BigDecimalConverter()).title("Amount").valueFactory(LOSCustomerOrderPosition::getAmount).show(),
				t.column(new BigDecimalConverter()).title("Picked").valueFactory(LOSCustomerOrderPosition::getAmountPicked).show()
				);

	}
	
	public void addPosition(Event e) {
		if (this.itemData.getValue() == null) return;

		String itemData = this.itemData.getValue().getNumber();
		String lot = (this.lot.getValue() == null) ? null : this.lot.getValue().getName();
		BigDecimal qty = quantityFormatter.getValue();
		
		String clientRef = getData().getClient().toUniqueString();
		long orderID = getData().getId();
		
		if (qty == null) return;
		if (qty.compareTo(BigDecimal.ZERO) <= 0) return;
		
		LOSOrderFacade facade = getContext().getBean(LOSOrderFacade.class);
		LOSCustomerOrderQueryRemote query = getContext().getBean(LOSCustomerOrderQueryRemote.class);
		getExecutor().runAndDisable(addButton, p -> {
			facade.addOrderPosition(orderID, new OrderPositionTO[] {new OrderPositionTO(clientRef, lot, itemData, qty)});
			LOSCustomerOrder order = query.queryById(orderID);
			return order;
		})
		.thenAcceptUI(order -> {
			setData(null);
			setData(order);
			this.itemData.setValue(null);
			this.lot.setValue(null);
			this.quantityFormatter.setValue(null);
			this.itemData.requestFocus();
		});
	}

	public void deletePosition(Event e) {
		List<LOSCustomerOrderPosition> sel = field_positions.getSelectionModel().getSelectedItems();
		long[] positionIds = sel.stream().mapToLong(BasicEntity::getId).toArray();
		long orderID = getData().getId();
		
		LOSOrderFacade facade = getContext().getBean(LOSOrderFacade.class);
		LOSCustomerOrderQueryRemote query = getContext().getBean(LOSCustomerOrderQueryRemote.class);			
		try {
			LOSCustomerOrder order = getExecutor().callAndWait(field_positions, () -> {
				facade.deleteOrderPositions(positionIds);
				return query.queryById(orderID);
			});
			setData(null);
			setData(order);
		} 
		catch (Exception ex) {
			FXErrors.exception(ex);
		}
	}
}
