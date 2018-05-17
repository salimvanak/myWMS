package uk.ltd.mediamagic.mywms.transactions;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import de.linogistix.los.inventory.model.LOSStockUnitRecord;
import de.linogistix.los.inventory.query.dto.LOSStockUnitRecordTO;
import de.linogistix.los.query.BODTO;
import de.linogistix.los.query.LOSResultList;
import de.linogistix.los.query.QueryDetail;
import de.linogistix.los.query.TemplateQuery;
import de.linogistix.los.query.TemplateQueryFilter;
import de.linogistix.los.query.TemplateQueryWhereToken;
import javafx.beans.binding.BooleanBinding;
import uk.ltd.mediamagic.common.utils.Strings;
import uk.ltd.mediamagic.flow.crud.BODTOPlugin;
import uk.ltd.mediamagic.flow.crud.BODTOTable;
import uk.ltd.mediamagic.flow.crud.CRUDKeyUtils;
import uk.ltd.mediamagic.fx.ApplicationService;
import uk.ltd.mediamagic.fx.action.AC;
import uk.ltd.mediamagic.fx.action.RootCommand;
import uk.ltd.mediamagic.fx.data.TableKey;
import uk.ltd.mediamagic.fx.flow.ApplicationContext;
import uk.ltd.mediamagic.fx.flow.ContextBase;
import uk.ltd.mediamagic.fx.flow.ViewContextBase;
import uk.ltd.mediamagic.mywms.common.MyWMSUserPermissions;
import uk.ltd.mediamagic.mywms.common.QueryUtils;

public class StockUnitLogPlugin extends BODTOPlugin<LOSStockUnitRecord> {
	enum QueryType {
		LOCATION("fromStorageLocation", "toStorageLocation"),
		ACTIVITY("activityCode"),
		ITEM_DATA("itemData"),
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
	
	private final Consumer<TemplateQuery> filterGenerator;
	private final String title;	
	
	public StockUnitLogPlugin() {
		super(LOSStockUnitRecord.class);
		filterGenerator = t -> {};
		title = null;
	}
	
	public StockUnitLogPlugin(String title, Consumer<TemplateQuery> filter) {
		super(LOSStockUnitRecord.class);
		this.filterGenerator = filter;
		this.title = title;
	}

	private static Consumer<TemplateQuery> typeFilter(QueryType type, List<String> values) {
		if (values.isEmpty()) return (template) -> {};
		else {
			return (template) -> {
				TemplateQueryFilter filter = template.addNewFilter();
				for (String field : type.getFields()) {
					for (String value : values) {
						filter.addWhereToken(QueryUtils.or(new TemplateQueryWhereToken(
								TemplateQueryWhereToken.OPERATOR_EQUAL, field, value)));
					}
				}
			};
		}
	}

	
	@Override
	protected BooleanBinding createVisibleBinding() {
		return MyWMSUserPermissions.atLeastForeman();
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
		
	private List<String> getValues(QueryType type, TableKey key) {
		LOSStockUnitRecordTO r = CRUDKeyUtils.getTO(key);
		switch(type) {
		case ACTIVITY:  return Collections.singletonList(r.getActivityCode());
		case LOT:       return Collections.singletonList(r.getLot());
		case ITEM_DATA: return Collections.singletonList(r.getItemData());
		case STOCK_UNIT: return Arrays.asList(r.getFromSu(), r.getToSu());
		case UNIT_LOAD: return Arrays.asList(r.getFromUl(), r.getToUl());
		case LOCATION: return  Arrays.asList(r.getFromSl(), r.getToSl());
		default: return Collections.emptyList();
		}				
	}
	
	@Override
	public CompletableFuture<LOSResultList<BODTO<LOSStockUnitRecord>>> getListData(ContextBase context,
			QueryDetail detail, TemplateQuery template) {
		
		if (filterGenerator != null) {
			filterGenerator.accept(template);
		}
		return super.getListData(context, detail, template);
	}
	
	@Override
	protected BODTOTable<LOSStockUnitRecord> getTable(ViewContextBase context) {
		BODTOTable<LOSStockUnitRecord> t = super.getTable(context);
		t.getContext().setTitle(title);
		
		Consumer<QueryType> action = q -> with(q, getValues(q,t.getSelectedKey()),t.getContext());
		
		t.getCommands()
		.begin(RootCommand.MENU)
			.begin(RootCommand.MENU_WITH_SELECTION)
				.add(AC.id(QueryType.UNIT_LOAD).text("Search unit load").action(action))
				.add(AC.id(QueryType.STOCK_UNIT).text("Search stock unit").action(action))
				.add(AC.id(QueryType.LOCATION).text("Search location").action(action))
				.add(AC.id(QueryType.ACTIVITY).text("Search activity code").action(action))
				.add(AC.id(QueryType.ITEM_DATA).text("Search item data").action(action))
				.add(AC.id(QueryType.LOT).text("Search lot").action(action))
			.end()
		.end();

		return t;
	}
	
	@Override
	protected void configureCommands(RootCommand command) {
		super.configureCommands(command);
	}

	static void with(QueryType type, String value, ViewContextBase context) {
		with(type, Collections.singletonList(value), context);
	}
	
	static void with(QueryType type, List<String> values, ViewContextBase context) {
		String title = type.toDisplayName() + ": " + Strings.left(values.stream().collect(Collectors.joining(", ")), 20); 
		with(title, typeFilter(type, values), context);
	}

	static void with(String title, Consumer<TemplateQuery> filter, ViewContextBase context) {
		StockUnitLogPlugin p = new StockUnitLogPlugin(title, filter);
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
