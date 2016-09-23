package uk.ltd.mediamagic.mywms.goodsout;

import java.beans.PropertyDescriptor;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import de.linogistix.los.inventory.model.LOSCustomerOrder;
import de.linogistix.los.model.State;
import de.linogistix.los.query.BODTO;
import de.linogistix.los.query.QueryDetail;
import de.linogistix.los.query.TemplateQuery;
import de.linogistix.los.query.TemplateQueryFilter;
import de.linogistix.los.query.TemplateQueryWhereToken;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;
import javafx.util.StringConverter;
import uk.ltd.mediamagic.flow.crud.BODTOPlugin;
import uk.ltd.mediamagic.flow.crud.BODTOTable;
import uk.ltd.mediamagic.flow.crud.SubForm;
import uk.ltd.mediamagic.fx.controller.list.MaterialListItems;
import uk.ltd.mediamagic.fx.converters.DateConverter;
import uk.ltd.mediamagic.fx.converters.MapConverter;
import uk.ltd.mediamagic.fx.flow.ContextBase;
import uk.ltd.mediamagic.fx.flow.ViewContextBase;
import uk.ltd.mediamagic.mywms.goodsout.GoodsOutUtils.OpenFilter;
import uk.ltd.mediamagic.util.DateUtils;

@SubForm(
		title="Main", columns=1, 
		properties={"number", "externalNumber", "externalId", "state", "strategy", "destination", "prio"}
	)
@SubForm(
		title="Delivery", columns=2, 
		properties={"customerNumber", "customerName", "delivery", "documentUrl", "labelUrl", "dtype"}
	)
public class OrdersPlugin  extends BODTOPlugin<LOSCustomerOrder> {

	public OrdersPlugin() {
		super(LOSCustomerOrder.class);
	}
	
	@Override
	public String getPath() {
		return "{1, _Goods out} -> {1, _Orders}";
	}
	
	@Override
	protected StringConverter<?> getConverter(PropertyDescriptor property) {
		if ("delivery".equals(property.getName())) return new DateConverter();
		else if ("prio".equals(property.getName())) return new MapConverter<Integer>(GoodsOutTypes.priority);
		else if ("state".equals(property.getName())) return new MapConverter<Integer>(GoodsOutTypes.state);
		return super.getConverter(property);
	}
	
	@Override
	public Callback<ListView<LOSCustomerOrder>, ListCell<LOSCustomerOrder>> createListCellFactory() {
		return MaterialListItems.withDate(s -> GoodsOutUtils.getIcon(s.getState()), 
				s -> DateUtils.toLocalDateTime(s.getDelivery()), 
				s -> String.format("%s, %s, %s", s.toUniqueString(), s.getExternalNumber(), s.getDestination()),
				s -> String.format("%s, %s", GoodsOutTypes.state.getValue(s.getState()), s.getCustomerName()),
				null);
	}

	@Override
	protected void refresh(BODTOTable<LOSCustomerOrder> source, ViewContextBase context) {
		OpenFilter filterValue = GoodsOutUtils.getFilter(source);

		TemplateQuery template = source.createQueryTemplate();
		if (filterValue != OpenFilter.All) {
			TemplateQueryFilter filter = template.addNewFilter();
			filter.addWhereToken(new TemplateQueryWhereToken(TemplateQueryWhereToken.OPERATOR_SMALLER, "state", State.FINISHED));
		}

		QueryDetail detail = source.createQueryDetail();

		source.setItems(null);
		getListData(context, detail, template)
			.thenApplyAsync(FXCollections::observableList, Platform::runLater)
			.thenAccept(source::setItems);			
	}
	
	@Override
	public CompletableFuture<List<BODTO<LOSCustomerOrder>>> getListData(ContextBase context, QueryDetail detail, TemplateQuery template) {
		if (detail.getOrderBy().isEmpty()) {
			detail.addOrderByToken("created", false);
		}
		return super.getListData(context, detail, template);
	}

	@Override
	public List<String> getTableColumns() {
		return Arrays.asList("id", 
				"name AS number",	"clientNumber AS client.number", 
				"customerNumber", "customerName", "externalNumber", "delivery", "state");
	}
	
	@Override
	protected BODTOTable<LOSCustomerOrder> getTable(ViewContextBase context) {
		BODTOTable<LOSCustomerOrder> t = super.getTable(context);
		GoodsOutUtils.addOpenFilter(t, () -> refresh(t, t.getContext()));
		return t;
	}
	
}
