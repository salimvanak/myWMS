package uk.ltd.mediamagic.mywms.common;

import java.util.ArrayList;

import de.linogistix.los.entityservice.BusinessObjectLock;
import de.linogistix.los.model.Prio;
import de.linogistix.los.query.TemplateQuery;
import de.linogistix.los.query.TemplateQueryWhereToken;
import javafx.collections.FXCollections;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import uk.ltd.mediamagic.fx.action.AC;
import uk.ltd.mediamagic.fx.action.AC.NodeAction;
import uk.ltd.mediamagic.fx.flow.actions.CommandProvider;
import uk.ltd.mediamagic.fx.helpers.ComboBoxes;

public class QueryUtils {
	
	public static TemplateQueryWhereToken or(TemplateQueryWhereToken t) {
		t.setLogicalOperator("OR");
		return t;
	}
	
	public static TemplateQuery cloneFilters(TemplateQuery q) {
		TemplateQuery nq = new TemplateQuery();
		nq.getWhereFilter().addAll(q.getWhereFilter());
		return nq;
	}
	
	public static <T extends Enum<T>> void addFilter(CommandProvider t, T defaultValue, Runnable refreshData) {
		ComboBox<T> filterCB = ComboBoxes.createComboForEnum(defaultValue);
		filterCB.valueProperty().addListener(o -> refreshData.run());
		t.getCommands().add(AC.node("Filter",filterCB)).end();
	}

	@SuppressWarnings("unchecked")
	public static <T extends Enum<T>> T getFilter(CommandProvider t, T defaultValue) {
		AC<?,?> n = t.getCommands().find("Filter");
		if (n == null) return defaultValue;
		Label l = (Label) ((NodeAction)n).get();
		ComboBox<T> cb = (ComboBox<T>) l.getGraphic();
		return cb.getValue();
	}
	
	public static <T extends Enum<T> & BusinessObjectLock> ComboBox<Integer> lockStateCombo(T defaultValue) {
		ComboBox<Integer> cb = lockStateCombo(defaultValue.getDeclaringClass());
		cb.setValue(defaultValue.getLock());
		return cb;
	}

	public static <T extends Enum<T> & BusinessObjectLock> ComboBox<Integer> lockStateCombo(Class<T> cls) {
		int defaultInt = 0;
		ArrayList<Integer> items = new ArrayList<>();
		items.add(0);
		items.add(1);
		items.add(2);
		if (cls != null) {
			for(T v : cls.getEnumConstants()) {
				items.add(v.getLock());
			}
		}
		
		ComboBox<Integer> cb = new ComboBox<>();
		cb.setItems(FXCollections.observableList(items));
		cb.setConverter(new LockStateConverter<>(cls));
		cb.setValue(defaultInt);
		return cb;
	}

	public static ComboBox<Integer> priorityCombo() {
		return priorityCombo(new ComboBox<>());
	}

	public static ComboBox<Integer> priorityCombo(ComboBox<Integer> cb) {
		int defaultInt = Prio.NORMAL;
		ArrayList<Integer> items = new ArrayList<>();
		items.add(Prio.LOWEST);
		items.add(Prio.LOW);
		items.add(Prio.NORMAL);
		items.add(Prio.HIGH);
		items.add(Prio.HIGHEST);
		
		cb.setItems(FXCollections.observableList(items));
		cb.setConverter(new OrderPriorityConverter());
		cb.setValue(defaultInt);
		return cb;
	}

	
	
}
