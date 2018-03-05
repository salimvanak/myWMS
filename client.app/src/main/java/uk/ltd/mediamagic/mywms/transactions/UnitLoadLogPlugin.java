package uk.ltd.mediamagic.mywms.transactions;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

import de.linogistix.los.location.model.LOSUnitLoadRecord;
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
import uk.ltd.mediamagic.fx.ApplicationService;
import uk.ltd.mediamagic.fx.flow.ApplicationContext;
import uk.ltd.mediamagic.fx.flow.ContextBase;
import uk.ltd.mediamagic.fx.flow.ViewContextBase;
import uk.ltd.mediamagic.mywms.common.MyWMSUserPermissions;
import uk.ltd.mediamagic.mywms.common.QueryUtils;

public class UnitLoadLogPlugin extends BODTOPlugin<LOSUnitLoadRecord> {
	enum QueryType {
		LOCATION("fromLocation", "toLocation"),
		ACTIVITY("activityCode"),
		UNIT_LOAD("label"),
		OPERATOR("operator");
		
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
		
	public UnitLoadLogPlugin() {
		super(LOSUnitLoadRecord.class);
	}

	public UnitLoadLogPlugin(QueryType type, String value) {
		super(LOSUnitLoadRecord.class);
		this.type = type;
		this.value = value;
		if (Strings.isEmpty(value)) Objects.requireNonNull(type); 
	}
	
	@Override
	protected BooleanBinding createVisibleBinding() {
		return MyWMSUserPermissions.atLeastForeman();
	}
	
	@Override
	public String getPath() {
		return "{1, _Logs} -> {1, _Unit Load Records}";
	}

	@Override
	protected List<String> getTableColumns() {
		return Arrays.asList("recordDate AS created", "name AS label", "type", "activityCode",
				"fromLocation", "toLocation");
	}
	
	@Override
	public CompletableFuture<LOSResultList<BODTO<LOSUnitLoadRecord>>> 
	getListData(ContextBase context, QueryDetail detail, TemplateQuery template) {
		if (!Strings.isEmpty(value)) {
			System.out.println("VALUE " + value  + " type " + type);
			TemplateQueryFilter filter = template.addNewFilter();
			for (String field : type.getFields()) {
				filter.addWhereToken(QueryUtils.or(new TemplateQueryWhereToken(
					TemplateQueryWhereToken.OPERATOR_EQUAL, field, value)));
			}
		}
		return super.getListData(context, detail, template);
	}
	
	@Override
	protected BODTOTable<LOSUnitLoadRecord> getTable(ViewContextBase context) {
		BODTOTable<LOSUnitLoadRecord> t = super.getTable(context);
		if (!Strings.isEmpty(value)) {
			t.getContext().setTitle(type.toDisplayName() + ": " + value); 
		}
		return t;
	}

	public static void with(QueryType type, String value, ViewContextBase context) {
		UnitLoadLogPlugin p = new UnitLoadLogPlugin(type, value);
		ApplicationService appService = context.getBean(ApplicationService.class);
		p.handle(context.getBean(ApplicationContext.class), context.getRootNode(), appService::addNewNode);
	}
}
