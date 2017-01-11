package uk.ltd.mediamagic.mywms.goodsout;

import java.beans.PropertyDescriptor;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.function.Supplier;

import de.linogistix.los.inventory.model.LOSGoodsOutRequestPosition;
import de.linogistix.los.inventory.model.LOSGoodsOutRequestPositionState;
import de.linogistix.los.inventory.query.dto.LOSGoodsOutPositionTO;
import de.linogistix.los.query.BODTO;
import de.linogistix.los.query.LOSResultList;
import de.linogistix.los.query.QueryDetail;
import de.linogistix.los.query.TemplateQuery;
import javafx.scene.Node;
import javafx.util.StringConverter;
import res.R;
import res.StandardIcons.IconSize;
import uk.ltd.mediamagic.common.utils.Strings;
import uk.ltd.mediamagic.flow.crud.BODTOPlugin;
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
import uk.ltd.mediamagic.mywms.goodsout.actions.GoodsOutFinishPosition;
import uk.ltd.mediamagic.util.Closures;

@SubForm(
		title="Main", columns=1, 
		properties={"goodsOutRequest", "source", "outState"}
	)
public class GoodsOutPositionsPlugin  extends BODTOPlugin<LOSGoodsOutRequestPosition> {

	private enum Actions {FINISH_POSITION}
	
	public GoodsOutPositionsPlugin() {
		super(LOSGoodsOutRequestPosition.class);
	}
	
	@Override
	public String getPath() {
		return "{1, _Goods out} -> {2, _Goods out positions}";
	}
	
	@Override
	protected StringConverter<?> getConverter(PropertyDescriptor property) {
		if ("delivery".equals(property.getName())) return new DateConverter();
		else if ("prio".equals(property.getName())) return new MapConverter<Integer>(GoodsOutTypes.priority);
		else if ("state".equals(property.getName())) return new MapConverter<Integer>(GoodsOutTypes.state);
		return super.getConverter(property);
	}
	
	@Override
	public Supplier<CellRenderer<LOSGoodsOutRequestPosition>> 
	createCellFactory() {	
		return MaterialCells.withID(GoodsOutPositionsPlugin::getIcon, 
				s -> s.toUniqueString(), 
				s -> Strings.format("{0}, {1}", 
						Closures.resolve(() -> s.getSource().getLabelId()).orElse(""), 
						Closures.resolve(() -> s.getSource().getStorageLocation().getName()).orElse("")),
				null,
				null);
	}
	
	@Override
	public Supplier<CellRenderer<BODTO<LOSGoodsOutRequestPosition>>> 
	createTOCellFactory() {
		Function<BODTO<LOSGoodsOutRequestPosition>, LOSGoodsOutPositionTO> cast = Closures.cast(LOSGoodsOutPositionTO.class);
		return MaterialCells.withID(cast.andThen(GoodsOutPositionsPlugin::getIcon), 
				cast.andThen(s -> s.getName()), 
				cast.andThen(s -> Strings.format("UL:{0}, Location: {1}", s.getUnitLoadLabel(), s.getLocationName())),
				cast.andThen(s -> Strings.format("Goods Out: {0}", s.getGoodsOutNumber())),
				null);
	}
		
	@Override
	public CompletableFuture<LOSResultList<BODTO<LOSGoodsOutRequestPosition>>> 
	getListData(ContextBase context, QueryDetail detail, TemplateQuery template) {
		if (detail.getOrderBy().isEmpty()) {
			detail.addOrderByToken("created", false);
		}
		return super.getListData(context, detail, template);
	}
	
	public static Node getIcon(LOSGoodsOutRequestPosition gor) {
		LOSGoodsOutRequestPositionState state = gor.getOutState();
		switch (state) {
		case RAW: return R.svgPaths.createIconFromFile("packages1.svg", IconSize.XLarge);
		case FINISHED: return R.svgPaths.createIconFromFile("boxes1.svg", IconSize.XLarge);
		default: return R.svgPaths.cancelled();
		}
	}

	public static Node getIcon(LOSGoodsOutPositionTO gor) {
		String state = gor.getOutState();
		switch (state) {
		case "RAW": return R.svgPaths.createIconFromFile("packages1.svg", IconSize.XLarge);
		case "FINISHED": return R.svgPaths.createIconFromFile("boxes1.svg", IconSize.XLarge);
		default: return R.svgPaths.cancelled();
		}
	}
	
	@Override
	public Flow createNewFlow(ApplicationContext context) {
		return super.createNewFlow(context)
			.globalWithSelection()
				.withMultiSelection(Actions.FINISH_POSITION, new GoodsOutFinishPosition())
			.end();
	}
	
	@Override
	protected void configureCommands(RootCommand command) {
		super.configureCommands(command);
		command.menu(RootCommand.MENU)
			.add(AC.id(Actions.FINISH_POSITION).text("Finish Position"))
		.end();
	}

	@Override
	public List<String> getTableColumns() {
		return Arrays.asList("id", 
				"unitLoadLabel AS source.labelId", "goodsOutNumber AS goodsOutRequest.number",	
				"locationName AS source.storageLocation.name", "outState");
	}
	
}
