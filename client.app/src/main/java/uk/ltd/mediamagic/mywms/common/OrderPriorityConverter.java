package uk.ltd.mediamagic.mywms.common;

import de.linogistix.los.model.Prio;
import javafx.util.StringConverter;

public class OrderPriorityConverter extends StringConverter<Integer> {

	private static final String HIGHEST_STR = "Highest (" + Prio.HIGHEST + ")";
	private static final String HIGH_STR = "High (" + Prio.HIGH + ")";
	private static final String NORMAL_STR = "Normal (" + Prio.NORMAL + ")";
	private static final String LOW_STR = "Low (" + Prio.LOW + ")";
	private static final String LOWEST_STR = "Lowest (" + Prio.LOWEST + ")";

	@Override
	public String toString(Integer object) {
		if (object == null) return null;
		if (object == Prio.LOWEST) return LOWEST_STR;
		if (object == Prio.LOW) return LOW_STR;
		if (object == Prio.NORMAL) return NORMAL_STR;
		if (object == Prio.HIGH) return HIGH_STR;
		if (object == Prio.HIGHEST) return HIGHEST_STR;
		return null;
	}

	@Override
	public Integer fromString(String string) {
		if (string == null) return null;
		if (LOWEST_STR.equals(string)) return Prio.LOWEST;
		if (LOW_STR.equals(string)) return Prio.LOW;
		if (NORMAL_STR.equals(string)) return Prio.NORMAL;
		if (HIGH_STR.equals(string)) return Prio.HIGH;
		if (HIGHEST_STR.equals(string)) return Prio.HIGHEST;
		return null;
	}

	
	

}
