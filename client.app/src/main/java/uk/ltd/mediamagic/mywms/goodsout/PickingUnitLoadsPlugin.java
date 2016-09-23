package uk.ltd.mediamagic.mywms.goodsout;

import java.beans.PropertyDescriptor;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import de.linogistix.los.inventory.model.LOSPickingUnitLoad;
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
import uk.ltd.mediamagic.fx.converters.MapConverter;
import uk.ltd.mediamagic.fx.flow.ContextBase;
import uk.ltd.mediamagic.fx.flow.ViewContextBase;
import uk.ltd.mediamagic.mywms.goodsout.GoodsOutUtils.OpenFilter;

@SubForm(
		title="Main", columns=1, 
		properties={"unitLoad", "customerOrderNumber", "pickingOrder", "state", "positionIndex"}
	)
public class PickingUnitLoadsPlugin  extends BODTOPlugin<LOSPickingUnitLoad> {

	public PickingUnitLoadsPlugin() {
		super(LOSPickingUnitLoad.class);
	}
	
	@Override
	public String getPath() {
		return "{1, _Goods out} -> {1, _Picking unit loads}";
	}
	
	@Override
	protected StringConverter<?> getConverter(PropertyDescriptor property) {
		if ("state".equals(property.getName())) return new MapConverter<Integer>(GoodsOutTypes.state);
		return super.getConverter(property);
	}
	
	@Override
	public Callback<ListView<LOSPickingUnitLoad>, ListCell<LOSPickingUnitLoad>> createListCellFactory() {
		return MaterialListItems.withID(s -> GoodsOutUtils.getIcon(s.getState()), 
				s -> s.getUnitLoad().toUniqueString(), 
				s -> String.format("%s", s.getCustomerOrderNumber()),
				s -> String.format("%s", s.getPickingOrder().toUniqueString()),
				s -> String.format("%s", s.getUnitLoad().getStorageLocation()));
	}
		
	@Override
	public CompletableFuture<List<BODTO<LOSPickingUnitLoad>>> 
	getListData(ContextBase context, QueryDetail detail, TemplateQuery template) {
		if (detail.getOrderBy().isEmpty()) {
			detail.addOrderByToken("created", false);
		}
		return super.getListData(context, detail, template);
	}

	@Override
	protected void refresh(BODTOTable<LOSPickingUnitLoad> source, ViewContextBase context) {
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
	protected BODTOTable<LOSPickingUnitLoad> getTable(ViewContextBase context) {
		BODTOTable<LOSPickingUnitLoad> t = super.getTable(context);
		GoodsOutUtils.addOpenFilter(t, () -> refresh(t, t.getContext()));
		return t;
	}

	@Override
	public List<String> getTableColumns() {
		return Arrays.asList("id", 
				"name AS unitload.labelId",	"clientNumber AS client.number", 
				"customerOrderNumber", "customerOrderNumber", "pickingOrderNumber AS pickingOrder.number", 
				"locationName AS unitLoad.storageLocation", "state");
	}
	
}
