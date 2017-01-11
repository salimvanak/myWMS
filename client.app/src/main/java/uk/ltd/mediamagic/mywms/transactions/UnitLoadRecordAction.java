package uk.ltd.mediamagic.mywms.transactions;

import java.util.function.BiConsumer;
import java.util.function.Function;

import uk.ltd.mediamagic.fx.data.TableKey;
import uk.ltd.mediamagic.fx.flow.Flow;
import uk.ltd.mediamagic.fx.flow.ViewContext;
import uk.ltd.mediamagic.fx.flow.ViewContextBase;
import uk.ltd.mediamagic.fx.flow.actions.WithSelection;
import uk.ltd.mediamagic.mywms.transactions.UnitLoadLogPlugin.QueryType;

public class UnitLoadRecordAction implements WithSelection<Object> {
	private final Function<TableKey, String> getValue;
	private final BiConsumer<String, ViewContextBase> run;
	
	public UnitLoadRecordAction(Function<TableKey, String> getValue, BiConsumer<String, ViewContextBase> run) {
		super();
		this.getValue = getValue;
		this.run = run;
	}

	public UnitLoadRecordAction(BiConsumer<String, ViewContextBase> run) {
		super();
		this.getValue = t -> t.get("name");
		this.run = run;
	}

	@Override
	public void execute(Object source, Flow flow, ViewContext context, TableKey key) {		
		String value = getValue.apply(key);
		run.accept(value, context);
	}
	
	public static UnitLoadRecordAction forActivityCode(String prefix) {
		return new UnitLoadRecordAction(t -> prefix + " " + t.get("name"), StockUnitLogPlugin::withActivityCode);
	}

	public static UnitLoadRecordAction forActivityCode() {
		return new UnitLoadRecordAction(t -> t.get("name"), (s,c) -> UnitLoadLogPlugin.with(QueryType.ACTIVITY, s, c));
	}

	public static UnitLoadRecordAction forUnitLoad() {
		return new UnitLoadRecordAction(t -> t.get("name"), (s,c) -> UnitLoadLogPlugin.with(QueryType.UNIT_LOAD, s, c));
	}

	public static UnitLoadRecordAction forStorageLocation() {
		return new UnitLoadRecordAction(t -> t.get("name"), (s,c) -> UnitLoadLogPlugin.with(QueryType.LOCATION, s, c));
	}

	public static UnitLoadRecordAction forOperator() {
		return new UnitLoadRecordAction(t -> t.get("name"), (s,c) -> UnitLoadLogPlugin.with(QueryType.OPERATOR, s, c));
	}
}
