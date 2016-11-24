package uk.ltd.mediamagic.mywms.transactions;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

import de.linogistix.los.inventory.model.LOSStockUnitRecord;
import de.linogistix.los.query.BODTO;
import de.linogistix.los.query.LOSResultList;
import de.linogistix.los.query.QueryDetail;
import de.linogistix.los.query.TemplateQuery;
import de.linogistix.los.query.TemplateQueryFilter;
import de.linogistix.los.query.TemplateQueryWhereToken;
import uk.ltd.mediamagic.common.utils.Strings;
import uk.ltd.mediamagic.flow.crud.BODTOPlugin;
import uk.ltd.mediamagic.flow.crud.BODTOTable;
import uk.ltd.mediamagic.fx.ApplicationService;
import uk.ltd.mediamagic.fx.flow.ApplicationContext;
import uk.ltd.mediamagic.fx.flow.ContextBase;
import uk.ltd.mediamagic.fx.flow.ViewContextBase;
import uk.ltd.mediamagic.mywms.common.QueryUtils;

public class StockUnitLogPlugin extends BODTOPlugin<LOSStockUnitRecord> {
	enum QueryType {
		LOCATION("fromStorageLocation", "toStorageLocations"),
		ACTIVITY("activityCode"),
		STOCK_UNIT("toStockUnitIdentity", "fromStockUnitIdentity"),
		UNIT_LOAD("toUnitLoad", "fromUnitLoad"),
		OPERATOR("operator"),
		LOT("lot");
		
		private final String[] fields;
		
		private QueryType(String... fields) {
			this.fields = fields;
		}

		public String[] getFields() {
			return fields;
		}
		
		public String toDisplayName() {
			return Strings.prettyString(name());
		}

	};
	
	private QueryType type;
	private String value;
		
	public StockUnitLogPlugin() {
		super(LOSStockUnitRecord.class);
	}

	public StockUnitLogPlugin(QueryType type, String value) {
		super(LOSStockUnitRecord.class);
		this.type = type;
		this.value = value;
		if (Strings.isEmpty(value)) Objects.requireNonNull(type); 
	}
	
	@Override
	public String getPath() {
		return "{1, _Logs} -> {1, _Stock Unit Records}";
	}

	@Override
	protected List<String> getTableColumns() {
		return Arrays.asList("recordDate AS created", "name", "type", "activityCode", "operator", "itemData", "lot",
				"amount", "amountStock", "toSu AS toStockUnitIdentity", "toUl AS toUnitLoad");
	}
	
	@Override
	public CompletableFuture<LOSResultList<BODTO<LOSStockUnitRecord>>> getListData(ContextBase context,
			QueryDetail detail, TemplateQuery template) {
		
		if (!Strings.isEmpty(value)) {
			TemplateQueryFilter filter = template.addNewFilter();
			for (String field : type.getFields()) {
				filter.addWhereToken(QueryUtils.or(new TemplateQueryWhereToken(
					TemplateQueryWhereToken.OPERATOR_EQUAL, field, value)));
			}
		}
		return super.getListData(context, detail, template);
	}
	
	@Override
	protected BODTOTable<LOSStockUnitRecord> getTable(ViewContextBase context) {
		BODTOTable<LOSStockUnitRecord> t = super.getTable(context);
		if (!Strings.isEmpty(value)) {
			t.getContext().setTitle(type.toDisplayName() + ": " + value); 
		}
		return t;
	}

	static void with(QueryType type, String value, ViewContextBase context) {
		StockUnitLogPlugin p = new StockUnitLogPlugin(type, value);
		ApplicationService appService = context.getBean(ApplicationService.class);
		p.handle(context.getBean(ApplicationContext.class), context.getRootNode(), appService::addNewNode);
	}

	public static void withActivityCode(String activityCode, ViewContextBase context) {
		with(QueryType.ACTIVITY, activityCode, context);
	}

	public static void withStockUnit(String stockUnitName, ViewContextBase context) {
		with(QueryType.STOCK_UNIT, stockUnitName, context);
	}

	public static void withUnitLoad(String unitLoad, ViewContextBase context) {
		with(QueryType.UNIT_LOAD, unitLoad, context);
	}

	public static void withOperator(String operator, ViewContextBase context) {
		with(QueryType.OPERATOR, operator, context);
	}

	public static void withLocation(String location, ViewContextBase context) {
		with(QueryType.LOCATION, location, context);
	}
}
