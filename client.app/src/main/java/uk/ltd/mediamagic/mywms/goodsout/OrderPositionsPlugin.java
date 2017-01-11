package uk.ltd.mediamagic.mywms.goodsout;

import java.beans.PropertyDescriptor;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.function.Supplier;

import de.linogistix.los.inventory.model.LOSCustomerOrderPosition;
import de.linogistix.los.query.BODTO;
import de.linogistix.los.query.LOSResultList;
import de.linogistix.los.query.QueryDetail;
import de.linogistix.los.query.TemplateQuery;
import javafx.scene.Node;
import javafx.scene.control.ProgressIndicator;
import javafx.util.StringConverter;
import uk.ltd.mediamagic.common.utils.Strings;
import uk.ltd.mediamagic.flow.crud.BODTOPlugin;
import uk.ltd.mediamagic.flow.crud.SubForm;
import uk.ltd.mediamagic.fx.controller.list.CellRenderer;
import uk.ltd.mediamagic.fx.controller.list.MaterialCells;
import uk.ltd.mediamagic.fx.converters.DateConverter;
import uk.ltd.mediamagic.fx.converters.MapConverter;
import uk.ltd.mediamagic.fx.flow.ContextBase;

@SubForm(
		title="Main", columns=1, 
		properties={"number", "externalNumber", "externalId", "state", "strategy", "destination", "prio"}
	)
@SubForm(
		title="Delivery", columns=2, 
		properties={"customerNumber", "customerName", "delivery", "documentUrl", "labelUrl", "dtype"}
	)
public class OrderPositionsPlugin  extends BODTOPlugin<LOSCustomerOrderPosition> {

	public OrderPositionsPlugin() {
		super(LOSCustomerOrderPosition.class);
	}
	
	@Override
	public String getPath() {
		return "{1, _Goods out} -> {2, _Order positions}";
	}
	
	@Override
	protected StringConverter<?> getConverter(PropertyDescriptor property) {
		if ("delivery".equals(property.getName())) return new DateConverter();
		else if ("prio".equals(property.getName())) return new MapConverter<Integer>(GoodsOutTypes.priority);
		else if ("state".equals(property.getName())) return new MapConverter<Integer>(GoodsOutTypes.state);
		return super.getConverter(property);
	}
	
	private static Node getIcon(LOSCustomerOrderPosition position) {
		BigDecimal picked = position.getAmountPicked();
		BigDecimal required = position.getAmount();
		if (required.compareTo(BigDecimal.ZERO) <= 0) return new ProgressIndicator(1);
		ProgressIndicator pi = new ProgressIndicator(picked.setScale(3, RoundingMode.HALF_DOWN).divide(required, RoundingMode.HALF_EVEN).doubleValue());
		return pi;
	}
	
	@Override
	public Supplier<CellRenderer<LOSCustomerOrderPosition>> createCellFactory() {
		return MaterialCells.withID(s -> getIcon(s), 
				s -> s.getNumber(), 
				s -> Strings.format("{0}, {1}, {2}", s.getItemData().getNumber(), s.getItemData().getName(), s.getLot()),
				null,
				s -> Strings.format("{0} of {1}", s.getAmount(), s.getAmountPicked()));
	}

	public static Supplier<CellRenderer<LOSCustomerOrderPosition>> createCellFactory(Function<LOSCustomerOrderPosition, Node> icon) {
		return MaterialCells.withID(icon, 
				s -> s.getNumber(), 
				s -> Strings.format("{0}, {1}, {2}", s.getItemData().getNumber(), s.getItemData().getName(), s.getLot()),
				null,
				s -> Strings.format("{0} of {1}", s.getAmount(), s.getAmountPicked()));
	}

	@Override
	public CompletableFuture<LOSResultList<BODTO<LOSCustomerOrderPosition>>>
	getListData(ContextBase context, QueryDetail detail, TemplateQuery template) {
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
	
}
