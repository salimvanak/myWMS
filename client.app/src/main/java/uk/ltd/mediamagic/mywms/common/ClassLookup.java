package uk.ltd.mediamagic.mywms.common;

import java.util.TreeMap;
import java.util.function.Function;

import org.mywms.model.BasicEntity;

/**
 * A factory generates an object of type T for a given class of <code>Class<? extends BasicEntity</code>
 * 
 * A factory will cache the created object and therefore make repeated lookups faster.
 * 
 * @author slim
 *
 * @param <T>
 */
public class ClassLookup<T> {
	private final Function<Class<? extends BasicEntity>, T> generate;
	private final TreeMap<String, T> cache = new TreeMap<>();
	
	public ClassLookup(Function<Class<? extends BasicEntity>, T> generate) {
		super();
		this.generate = generate;
	}

	public T get(Class<? extends BasicEntity> cls) {
		return cache.computeIfAbsent(cls.getName(), s -> generate.apply(cls));
	}
}
