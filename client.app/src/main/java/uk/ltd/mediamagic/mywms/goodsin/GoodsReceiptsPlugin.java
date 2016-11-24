package uk.ltd.mediamagic.mywms.goodsin;

import java.beans.PropertyDescriptor;
import java.sql.Date;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import org.mywms.model.Client;

import de.linogistix.los.inventory.facade.LOSGoodsReceiptFacade;
import de.linogistix.los.inventory.model.LOSGoodsReceipt;
import de.linogistix.los.inventory.model.LOSGoodsReceiptState;
import de.linogistix.los.location.model.LOSStorageLocation;
import de.linogistix.los.location.query.dto.StorageLocationTO;
import de.linogistix.los.query.BODTO;
import de.linogistix.los.query.QueryDetail;
import de.linogistix.los.query.TemplateQuery;
import de.linogistix.los.query.TemplateQueryFilter;
import de.linogistix.los.query.TemplateQueryWhereToken;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.scene.Node;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;
import javafx.util.StringConverter;
import res.R;
import uk.ltd.mediamagic.flow.crud.BODTOPlugin;
import uk.ltd.mediamagic.flow.crud.BODTOTable;
import uk.ltd.mediamagic.flow.crud.BasicEntityEditor;
import uk.ltd.mediamagic.flow.crud.MyWMSEditor;
import uk.ltd.mediamagic.flow.crud.SubForm;
import uk.ltd.mediamagic.fx.control.SimpleFormBuilder;
import uk.ltd.mediamagic.fx.controller.MapFormController;
import uk.ltd.mediamagic.fx.controller.list.MaterialListItems;
import uk.ltd.mediamagic.fx.converters.DateConverter;
import uk.ltd.mediamagic.fx.data.TableKey;
import uk.ltd.mediamagic.fx.flow.ApplicationContext;
import uk.ltd.mediamagic.fx.flow.ContextBase;
import uk.ltd.mediamagic.fx.flow.Flow;
import uk.ltd.mediamagic.fx.flow.ViewContext;
import uk.ltd.mediamagic.fx.flow.ViewContextBase;
import uk.ltd.mediamagic.mywms.FlowUtils;
import uk.ltd.mediamagic.mywms.common.MyWMSUserPermissions;
import uk.ltd.mediamagic.mywms.goodsout.GoodsOutUtils;
import uk.ltd.mediamagic.mywms.goodsout.GoodsOutUtils.OpenFilter;
import uk.ltd.mediamagic.util.DateUtils;

@SubForm(
		title="Main", columns=1, 
		properties={"goodsReceiptNumber", "referenceNo", "receiptState", "goodsInLocation", "operator"}
	)
@SubForm(
		title="Delivery", columns=2, 
		properties={"forwarder", "deliveryNoteNumber", "receiptDate", "deliverer", "driverName", "licencePlate"}
	)
public class GoodsReceiptsPlugin  extends BODTOPlugin<LOSGoodsReceipt> {

	public GoodsReceiptsPlugin() {
		super(LOSGoodsReceipt.class);
	}
	
	@Override
	public String getPath() {
		return "{1, _Goods in} -> {1, _Goods Receipts}";
	}
	
	@Override
	public Flow createNewFlow(ApplicationContext context) {
		Flow flow = super.createNewFlow(context);
		flow.with(BODTOTable.class) 
			.action(Flow.CREATE_ACTION, this::createNewGoodsReceipt)
		.end();
		return flow;
	}
	
	@Override
	protected void refresh(BODTOTable<LOSGoodsReceipt> source, ViewContextBase context) {
		OpenFilter filterValue = GoodsOutUtils.getFilter(source);

		TemplateQuery template = source.createQueryTemplate();
		if (filterValue != OpenFilter.All) {
			TemplateQueryFilter filter = template.addNewFilter();
			filter.addWhereToken(new TemplateQueryWhereToken(
					TemplateQueryWhereToken.OPERATOR_NOT_EQUAL, "receiptState", LOSGoodsReceiptState.FINISHED));
		}

		QueryDetail detail = source.createQueryDetail();

		source.setItems(null);
		getListData(context, detail, template)
			.thenApplyAsync(FXCollections::observableList, Platform::runLater)
			.thenAccept(source::setItems);			
	}

	@Override
	protected BODTOTable<LOSGoodsReceipt> getTable(ViewContextBase context) {
		BODTOTable<LOSGoodsReceipt> t = super.getTable(context);
		GoodsOutUtils.addOpenFilter(t, () -> refresh(t, t.getContext()));
		return t;
	}
	
	public void createNewGoodsReceipt(BODTOTable<LOSGoodsReceipt> source, Flow flow, ViewContext context) {
		LOSGoodsReceiptFacade facade = context.getBean(LOSGoodsReceiptFacade.class);
		BasicEntityEditor<Client> clientField = new BasicEntityEditor<>();
		clientField.configure(context, Client.class);
		BasicEntityEditor<LOSStorageLocation> locationField = new BasicEntityEditor<>();
		locationField.configure(context, LOSStorageLocation.class);
		locationField.setFetchCompleteions(s -> context.getExecutor().call(facade::getGoodsReceiptLocations));
		SimpleFormBuilder form = new SimpleFormBuilder();
		form.doubleRow()
			.label("Client").fieldNode(clientField).label("Location").fieldNode(locationField)
		.end()
		.doubleRow()
			.label("Delivery Note No").field("deliveryNoteNumber").label("Receipt Date").datePicker("receiptDate")
		.end()
		.doubleRow()
			.label("Forwarder").field("forwarder")
		.end()
		.doubleRow()
			.label("Driver").field("driverName").label("Licence Plate").field("licencePlate")
		.end();
		
		MapFormController c = new MapFormController();
		c.setValidator(d -> d
				.notEmpty("receiptDate")
				.check("Client cannot be empty", () -> clientField.getValue() != null)
				.check("Location cannot be empty", () -> locationField.getValue() != null));
		context.autoInjectBean(c);
		
		form.bindController(c);
		ButtonType result = MapFormController.showDialog(source.getView(), "New Goods Receipt", form, c);
		if (result.getButtonData().isCancelButton()) return; // if user cancelled
		
		BODTO<Client> client = new BODTO<Client>(clientField.getValue());
		String licencePlate = c.getValue("licencePlate");
		String driverName = c.getValue("driverName");
		String forwarder = c.getValue("forwarder");
		String deliveryNoteNumber = c.getValue("deliveryNoteNumber");
		LocalDate receiptDate = c.getValue("receiptDate");
		StorageLocationTO goodsInLocation = new StorageLocationTO(locationField.getValue());
		
		LOSGoodsReceipt pojo =context.getExecutor().executeAndWait(source.getView(), 
				() -> facade.createGoodsReceipt(client, licencePlate, driverName, forwarder, 
						deliveryNoteNumber, Date.valueOf(receiptDate), goodsInLocation, null));
		
		MyWMSEditor<LOSGoodsReceipt> editor = getEditor(context, new TableKey("id", pojo.getId()));
		FlowUtils.showNext(flow, context, MyWMSEditor.class, editor);
	}
	
	@Override
	protected StringConverter<?> getConverter(PropertyDescriptor property) {
		if ("receiptDate".equals(property.getName())) return new DateConverter();
		return super.getConverter(property);
	}
	
	@Override
	public Callback<ListView<LOSGoodsReceipt>, ListCell<LOSGoodsReceipt>> createListCellFactory() {
		return MaterialListItems.withDate(GoodsReceiptsPlugin::getIcon, 
				s -> DateUtils.toLocalDate(s.getReceiptDate()), 
				s -> String.format("%s, %s, %s", s.toUniqueString(), s.getReferenceNo(), s.getDeliveryNoteNumber()),
				s -> String.format("%s, %s", s.getLicencePlate(), s.getDriverName()),
				s -> String.format("%s", s.getReceiptState()));
	}
	
	private static Node getIcon(LOSGoodsReceipt d) {
		if (d == null) return null;
		switch (d.getReceiptState()) {
		case RAW: return R.svgPaths.goodsWaiting();
		case ACCEPTED: return R.svgPaths.goodsArrived();
		case TRANSFER: return R.svgPaths.goodsTransfer();
		case CANCELED: return R.svgPaths.cancelled();
		case FINISHED: return R.svgPaths.goodsStored();
		}
		return R.svgPaths.unknown();
	}
	
	@Override
	public List<String> getTableColumns() {
		return Arrays.asList("id", 
				"name AS goodsReceiptNumber",	"clientNumber AS client.number", 
				"deliveryNoteNumber", "receiptDate", "receiptState");
	}
	
	protected GoodsReceiptForm getEditor(ContextBase context, TableKey key) {
		Long id = key.get("id");
		if (id == null) return null;

		GoodsReceiptForm controller = new GoodsReceiptForm(getBeanInfo(), this::getConverter);
		context.autoInjectBean(controller);
		controller.setUserPermissions(new MyWMSUserPermissions.ForLockedWhen(getUserPermissions(), 
				() -> controller.getData().getReceiptState() != LOSGoodsReceiptState.RAW));
		
		getData(context, id).thenAcceptAsync(controller::setData, Platform::runLater);
		return controller;
	}
}
