package uk.ltd.mediamagic.mywms.transactions;

import java.util.function.BiConsumer;
import java.util.function.Function;

import uk.ltd.mediamagic.fx.data.TableKey;
import uk.ltd.mediamagic.fx.flow.Flow;
import uk.ltd.mediamagic.fx.flow.ViewContext;
import uk.ltd.mediamagic.fx.flow.ViewContextBase;
import uk.ltd.mediamagic.fx.flow.actions.WithSelection;
import uk.ltd.mediamagic.mywms.transactions.StockUnitLogPlugin.QueryType;

public class StockUnitRecordAction implements WithSelection<Object> {
	private final Function<TableKey, String> getValue;
	private final BiConsumer<String, ViewContextBase> run;
	
	public StockUnitRecordAction(Function<TableKey, String> getValue, BiConsumer<String, ViewContextBase> run) {
		super();
		this.getValue = getValue;
		this.run = run;
	}

	public StockUnitRecordAction(BiConsumer<String, ViewContextBase> run) {
		super();
		this.getValue = t -> t.get("name");
		this.run = run;
	}

	@Override
	public void execute(Object source, Flow flow, ViewContext context, TableKey key) {		
		String value = getValue.apply(key);
		run.accept(value, context);
	}
	
	public static StockUnitRecordAction forActivityCode(String prefix) {
		return new StockUnitRecordAction(t -> prefix + " " + t.get("name"), StockUnitLogPlugin::withActivityCode);
	}

	public static StockUnitRecordAction forActivityCode() {
		return new StockUnitRecordAction(t -> t.get("name"), StockUnitLogPlugin::withActivityCode);
	}

	public static StockUnitRecordAction forLot() {
		return new StockUnitRecordAction((s, c) -> StockUnitLogPlugin.with(QueryType.LOT, s, c));
	}

	public static StockUnitRecordAction forStockUnit() {
		return new StockUnitRecordAction(StockUnitLogPlugin::withStockUnit);
	}

	public static StockUnitRecordAction forUnitLoad() {
		return new StockUnitRecordAction(StockUnitLogPlugin::withUnitLoad);
	}

	public static StockUnitRecordAction forStorageLocation() {
		return new StockUnitRecordAction(StockUnitLogPlugin::withLocation);
	}

	public static StockUnitRecordAction forOperator() {
		return new StockUnitRecordAction(StockUnitLogPlugin::withOperator);
	}
}
