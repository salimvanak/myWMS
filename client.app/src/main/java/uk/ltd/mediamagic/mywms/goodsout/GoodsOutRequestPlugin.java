package uk.ltd.mediamagic.mywms.goodsout;

import java.beans.PropertyDescriptor;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

import de.linogistix.los.inventory.model.LOSGoodsOutRequest;
import de.linogistix.los.inventory.model.LOSGoodsOutRequestState;
import de.linogistix.los.query.BODTO;
import de.linogistix.los.query.LOSResultList;
import de.linogistix.los.query.QueryDetail;
import de.linogistix.los.query.TemplateQuery;
import de.linogistix.los.query.TemplateQueryFilter;
import de.linogistix.los.query.TemplateQueryWhereToken;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.scene.Node;
import javafx.util.StringConverter;
import res.R;
import res.StandardIcons.IconSize;
import uk.ltd.mediamagic.common.utils.Strings;
import uk.ltd.mediamagic.flow.crud.BODTOPlugin;
import uk.ltd.mediamagic.flow.crud.BODTOTable;
import uk.ltd.mediamagic.flow.crud.SubForm;
import uk.ltd.mediamagic.fx.action.AC;
import uk.ltd.mediamagic.fx.action.RootCommand;
import uk.ltd.mediamagic.fx.controller.list.CellRenderer;
import uk.ltd.mediamagic.fx.controller.list.MaterialCells;
import uk.ltd.mediamagic.fx.converters.DateConverter;
import uk.ltd.mediamagic.fx.converters.MapConverter;
import uk.ltd.mediamagic.fx.flow.ApplicationContext;
import uk.ltd.mediamagic.fx.flow.ContextBase;
import uk.ltd.mediamagic.fx.flow.Flow;
import uk.ltd.mediamagic.fx.flow.ViewContextBase;
import uk.ltd.mediamagic.mywms.goodsout.GoodsOutUtils.OpenFilter;
import uk.ltd.mediamagic.mywms.transactions.UnitLoadRecordAction;
import uk.ltd.mediamagic.util.DateUtils;

@SubForm(
		title="Main", columns=1, 
		properties={"number", "externalNumber", "outState", "customerOrder", "operator"}
	)
@SubForm(
		title="Out", columns=2, 
		properties={"outLocation", "groupName", "courier", "shippingDate"}
	)
public class GoodsOutRequestPlugin  extends BODTOPlugin<LOSGoodsOutRequest> {

	private enum Actions {UNIT_LOAD_LOG} 
	
	public GoodsOutRequestPlugin() {
		super(LOSGoodsOutRequest.class);
	}
	
	@Override
	public String getPath() {
		return "{1, _Goods out} -> {1, _Goods out request}";
	}
	
	@Override
	protected StringConverter<?> getConverter(PropertyDescriptor property) {
		if ("shippingDate".equals(property.getName())) return new DateConverter();
		else if ("state".equals(property.getName())) return new MapConverter<Integer>(GoodsOutTypes.state);
		return super.getConverter(property);
	}
	
	@Override
	public Supplier<CellRenderer<LOSGoodsOutRequest>> createCellFactory() {
		return MaterialCells.withDateTime(GoodsOutRequestPlugin::getIcon, 
				s -> DateUtils.toLocalDateTime(s.getShippingDate()), 
				s -> Strings.format("{0}, {1}", s.getCustomerOrder().getNumber(), s.getCustomerOrder().getExternalNumber()),
				s -> Strings.format("{0}", s.getOperator()),
				null);
	}

	public static Node getIcon(LOSGoodsOutRequest gor) {
		LOSGoodsOutRequestState state = gor.getOutState();
		switch (state) {
		case RAW: return R.svgPaths.createIconFromFile("packages1.svg", IconSize.XLarge);
		case PROCESSING: return R.svgPaths.createIconFromFile("packages2.svg", IconSize.XLarge);
		case FINISHED: return R.svgPaths.createIconFromFile("boxes1.svg", IconSize.XLarge);
		default: return R.svgPaths.cancelled();
		}
	}
	
	@Override
	protected void refresh(BODTOTable<LOSGoodsOutRequest> source, ViewContextBase context) {
		OpenFilter filterValue = GoodsOutUtils.getFilter(source);

		TemplateQuery template = source.createQueryTemplate();
		if (filterValue != OpenFilter.All) {
			TemplateQueryFilter filter = template.addNewFilter();
			filter.addWhereToken(new TemplateQueryWhereToken(
					TemplateQueryWhereToken.OPERATOR_NOT_EQUAL, "outState", LOSGoodsOutRequestState.FINISHED));
		}

		QueryDetail detail = source.createQueryDetail();

		source.setItems(null);
		getListData(context, detail, template)
			.thenApplyAsync(FXCollections::observableList, Platform::runLater)
			.thenAccept(source::setItems);			
	}

	
	@Override
	public CompletableFuture<LOSResultList<BODTO<LOSGoodsOutRequest>>> 
	getListData(ContextBase context, QueryDetail detail, TemplateQuery template) {
		if (detail.getOrderBy().isEmpty()) {
			detail.addOrderByToken("created", false);
		}
		return super.getListData(context, detail, template);
	}

	@Override
	protected BODTOTable<LOSGoodsOutRequest> getTable(ViewContextBase context) {
		BODTOTable<LOSGoodsOutRequest> t = super.getTable(context);
		GoodsOutUtils.addOpenFilter(t, () -> refresh(t, t.getContext()));
		return t;
	}
	
	@Override
	protected void configureCommands(RootCommand command) {
		super.configureCommands(command);
		command.begin(RootCommand.MENU)
			.add(AC.id(Actions.UNIT_LOAD_LOG).text("Unit load log"))
		.end();
	}
	
	@Override
	public Flow createNewFlow(ApplicationContext context) {
		return super.createNewFlow(context)
				.globalWithSelection()
					.withSelection(Actions.UNIT_LOAD_LOG, UnitLoadRecordAction.forActivityCode())
				.end();
	}

	@Override
	public List<String> getTableColumns() {
		return Arrays.asList("id", 
				"name AS number",	"clientNumber AS client.number", "customerOrderExternalNumber AS customerOrder.externalNumber",
				"customerOrderNumber AS customerOrder.numbercustomerNumber", "outState");
	}
	
}
