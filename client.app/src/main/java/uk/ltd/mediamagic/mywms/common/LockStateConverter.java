package uk.ltd.mediamagic.mywms.common;

import java.util.Arrays;
import java.util.Objects;

import de.linogistix.los.entityservice.BusinessObjectLock;
import javafx.util.StringConverter;

public class LockStateConverter<T extends Enum<T> & BusinessObjectLock> extends StringConverter<Integer> {

	private Class<T> value;
	
	public LockStateConverter(Class<T> value) {
		super();
		Objects.requireNonNull(value);
		this.value = value;
	}

	@Override
	public String toString(Integer object) {
		if (object == null) return "N/A";
		if (object == 0) return "Not Locked";
		if (object == 1) return "General";
		if (object == 2) return "Going to delete";
		T[] values = value.getEnumConstants();
		return Arrays.stream(values)
				.filter(v -> v.getLock() == object)
				.map(v -> v.getMessage())
				.findFirst()
				.orElse("Lock (" + object.toString() + ")");
	}

	private Integer parseUnknownState(String string) {
		if (string.startsWith("Lock (") && string.endsWith(")")) {
			try {
				return Integer.valueOf(string.substring(7, string.length()-1));
			} 
			catch (NumberFormatException e) {
				// was not a number between the brackets so carry on.
				return null;
			}
		}
		else {
			return null;
		}
	}
	
	@Override
	public Integer fromString(String string) {
		if (string == null) return null;
		if ("N/A".equals(string)) return null;
		
		if ("Not Locked".equals(string)) return 0;
		if ("General".equals(string)) return 1;
		if ("Going to delete".equals(string)) return 2;
		T[] values = value.getEnumConstants();
		return Arrays.stream(values)
				.filter(v -> string.equals(v.getMessage()))
				.map(v -> v.getLock())
				.findFirst()
				.orElse(parseUnknownState(string));

	}
}
