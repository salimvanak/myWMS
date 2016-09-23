package uk.ltd.mediamagic.mywms.common;

import java.util.Arrays;

import de.linogistix.los.entityservice.BusinessObjectLock;
import javafx.util.StringConverter;

public class LockStateConverter<T extends Enum<T> & BusinessObjectLock> extends StringConverter<Integer> {

	Class<T> value;
	
	public LockStateConverter(Class<T> value) {
		super();
		this.value = value;
	}

	@Override
	public String toString(Integer object) {
		if (object == null) return null;
		if (object == 0) return "Not Locked";
		if (object == 1) return "General";
		if (object == 2) return "Going to delete";
		T[] values = value.getEnumConstants();
		if (value != null) {
			return Arrays.stream(values)
					.filter(v -> v.getLock() == object)
					.map(v -> v.getMessage())
					.findFirst()
					.orElse(null);
		}
		else {
			return null;
		}
	}

	@Override
	public Integer fromString(String string) {
		if (string == null) return null;
		if ("Not Locked".equals(string)) return 0;
		if ("General".equals(string)) return 1;
		if ("Going to delete".equals(string)) return 2;
		if (value != null) {
			T[] values = value.getEnumConstants();
			return Arrays.stream(values)
					.filter(v -> string.equals(v.getMessage()))
					.map(v -> v.getLock())
					.findFirst()
					.orElse(null);
		}
		else {
			return null;
		}
	}

	
	

}
