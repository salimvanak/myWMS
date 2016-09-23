package uk.ltd.mediamagic.mywms.inventory;

import java.beans.PropertyDescriptor;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import de.linogistix.los.location.constants.LOSUnitLoadLockState;
import de.linogistix.los.location.model.LOSUnitLoad;
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
import uk.ltd.mediamagic.fx.AwesomeIcon;
import uk.ltd.mediamagic.fx.controller.list.MaterialListItems;
import uk.ltd.mediamagic.fx.flow.ContextBase;
import uk.ltd.mediamagic.fx.flow.ViewContextBase;
import uk.ltd.mediamagic.mywms.common.LockStateConverter;
import uk.ltd.mediamagic.mywms.common.QueryUtils;

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
	
	public UnitsLoadsPlugin() {
		super(LOSUnitLoad.class);
	}
	
	@Override
	public String getPath() {
		return "{1, _Inventory} -> {1, _Unit Load}";
	}
	
	@Override
	public Callback<ListView<LOSUnitLoad>, ListCell<LOSUnitLoad>> createListCellFactory() {
		return MaterialListItems.withID(s -> (s.getLock() == 0) ? new AwesomeIcon(AwesomeIcon.unlock) : new AwesomeIcon(AwesomeIcon.lock), 
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
				filter.addWhereToken(new TemplateQueryWhereToken(TemplateQueryWhereToken.OPERATOR_ISEMPTY, "stockUnitList", 0));				
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
	public CompletableFuture<List<BODTO<LOSUnitLoad>>> 
	getListData(ContextBase context, QueryDetail detail, TemplateQuery template) {
		if (detail.getOrderBy().isEmpty()) {
			detail.addOrderByToken("created", false);
		}
		return super.getListData(context, detail, template);
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
