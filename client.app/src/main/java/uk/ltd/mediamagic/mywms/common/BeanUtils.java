package uk.ltd.mediamagic.mywms.common;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.UndeclaredThrowableException;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.persistence.Column;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;

import org.mywms.model.BasicEntity;
import org.mywms.model.ItemMeasure;

import de.linogistix.los.location.model.LOSWorkingArea;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.util.Callback;
import javafx.util.StringConverter;
import uk.ltd.mediamagic.common.utils.Strings;
import uk.ltd.mediamagic.debug.MLogger;
import uk.ltd.mediamagic.fx.converters.BigDecimalConverter;
import uk.ltd.mediamagic.fx.converters.DateConverter;
import uk.ltd.mediamagic.fx.converters.EnumConverter;
import uk.ltd.mediamagic.fx.converters.NumberConverter;
import uk.ltd.mediamagic.fx.converters.StringLenConverter;
import uk.ltd.mediamagic.fx.converters.TimeConverter;
import uk.ltd.mediamagic.fx.converters.TimestampConverter;
import uk.ltd.mediamagic.fx.converters.ToStringConverter;
import uk.ltd.mediamagic.fxcommon.ObservableConstant;

public class BeanUtils {
	public static final Logger log = MLogger.log(BeanUtils.class);
	
	public static BeanInfo getBeanInfo(Class<?> cls) {
		try {
			BeanInfo beanInfo = Introspector.getBeanInfo(cls, Object.class);
			return beanInfo;
		} 
		catch (IntrospectionException e) {
			throw new UndeclaredThrowableException(e);
		}
	}
	
	public static PropertyDescriptor getNestedProperty(Class<?> clazz, String propertyName) {
		Objects.requireNonNull(clazz, "Clazz must not be null.");
		Objects.requireNonNull(propertyName, "PropertyName must not be null.");

		final String[] path = propertyName.split("\\.");

		for (int i = 0; i < path.length; i++) {
			propertyName = path[i];
			final List<PropertyDescriptor> propDescs = getProperties(clazz);
			for (final PropertyDescriptor propDesc : propDescs)
				if (propDesc.getName().equals(propertyName)) {
					clazz = propDesc.getPropertyType();
					if (i == path.length - 1)	return propDesc;
				}
		}
		log.log(Level.WARNING, "Property not found " + propertyName + " on " + clazz.getName());
    return null;
	}

	public static PropertyDescriptor getProperty(BeanInfo beanInfo, String propertyName) {
		for (PropertyDescriptor p : beanInfo.getPropertyDescriptors()) {
			if (Strings.equals(propertyName, p.getName())) return p;
		}
		log.log(Level.WARNING, "Properties " + toString(beanInfo.getPropertyDescriptors()));			
		log.log(Level.WARNING, "Property not found " + propertyName + " on " + beanInfo.getBeanDescriptor().getDisplayName());
		return null;
	}
	
	public static List<PropertyDescriptor> getProperties(Class<?> cls) {
		BeanInfo info = getBeanInfo(cls);
		return Arrays.asList(info.getPropertyDescriptors());
	}
	
	public static <D> 
	Callback<CellDataFeatures<D,?>, ObservableValue<?>> getCellValueFactory(PropertyDescriptor pds) {
		return a -> {
			try {
				return ObservableConstant.of(pds.getReadMethod().invoke(a.getValue()));
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				throw new UndeclaredThrowableException(e);
			}
		};
	}

	@SuppressWarnings("unchecked")
	public static <T, D> 
	Callback<D, ObservableValue<T>> getValueFactory(PropertyDescriptor pds) {
		return a -> {
			try {
				return ObservableConstant.of((T)pds.getReadMethod().invoke(a));
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				log.log(Level.SEVERE, "While getting property " + pds.getName() + " on " + a.getClass().getName());
				throw new UndeclaredThrowableException(e);
			}
		};
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static StringConverter<?> getConverter(PropertyDescriptor pds) {
			Class<?> type = pds.getPropertyType();
			String id = pds.getName();
			
			if (String.class.isAssignableFrom(type)) {
				Column column = pds.getReadMethod().getAnnotation(Column.class);
				if (column != null) return new StringLenConverter(column.length());
				return new StringLenConverter();
			}
			else if (type == Integer.TYPE || Integer.class.isAssignableFrom(type)) {
				return NumberConverter.forInteger();				
			}
			else if (type == Long.TYPE || Long.class.isAssignableFrom(type)) {
				return NumberConverter.forLong(10);				
			}
			else if (BigDecimal.class.isAssignableFrom(type)) {
				Column column = pds.getReadMethod().getAnnotation(Column.class);
				if (column != null) return new BigDecimalConverter(column.precision(), column.scale());
				return new BigDecimalConverter();
			}
			else if (Date.class.isAssignableFrom(type)) {
				Temporal temporal = pds.getReadMethod().getAnnotation(Temporal.class);
				if (temporal == null) {
					log.log(Level.WARNING, () -> "Missing @Temporal annotation on getter for " + id);
					return new TimestampConverter();
				}
				switch (temporal.value()) {
					case DATE: return new DateConverter();
					case TIME: return new TimeConverter();
					case TIMESTAMP:
					default:
						return new TimestampConverter();
				}
			}
			else if (Enum.class.isAssignableFrom(type)) {
				return new EnumConverter(type);					
			}
			else if (BasicEntity.class.isAssignableFrom(type)) {
				return ToStringConverter.of(BasicEntity::toShortString);
			}
			else if (ItemMeasure.class.isAssignableFrom(type)) {				
				return ToStringConverter.of(ItemMeasure::toString);
			}
			else {
				throw new UnsupportedOperationException("No Converter for " + id + " of type " + pds.toString());
			}
			//return new DefaultStringConverter();
	}

	public static String toString(PropertyDescriptor... descriptors) {
		StringBuilder builder = Arrays.stream(descriptors)
				.map(PropertyDescriptor::getName)
				.collect(StringBuilder::new, (sb, s) ->sb.append(s).append("\n"), StringBuilder::append);
		return builder.toString();
	}
	
	public static String getDisplayName(PropertyDescriptor p) {
		String name = p.getDisplayName();
		if (name.contains(" ")) return name;
		StringBuilder sb = new StringBuilder();
		boolean first = true;
		for (char c : name.toCharArray()) {
			if (first) {
				sb.append(Character.toUpperCase(c));
				first = false;
			}
			else if (Character.isUpperCase(c)) {
				sb.append(' ').append(c);
			}
			else {
				sb.append(c);
			}
		}
		return sb.toString();
	}

	
	public static boolean isList(PropertyDescriptor pd) {
		return (List.class.isAssignableFrom(pd.getPropertyType()));
	}

	public static boolean isBoolean(PropertyDescriptor pd) {
		return (Boolean.class.isAssignableFrom(pd.getPropertyType()) || Boolean.TYPE == pd.getPropertyType());
	}
	
	public static Class<?> getListElementType(PropertyDescriptor descriptor) {
		if (!isList(descriptor)) throw new IllegalArgumentException(descriptor.getName() + " is not a list");
		OneToMany annot = getAnnotation(descriptor, OneToMany.class);
		if (annot != null && annot.targetEntity() != void.class) {
			return annot.targetEntity();
		}
		else {
			Type rt = descriptor.getReadMethod().getGenericReturnType();
			if (rt instanceof ParameterizedType) {
				ParameterizedType  prt = (ParameterizedType) descriptor.getReadMethod().getGenericReturnType();			
				return (Class<?>) prt.getActualTypeArguments()[0];
			}
			else {
				return Object.class;
			}
		}
	}
	
	public static <T extends Annotation> T getAnnotation(PropertyDescriptor descriptor, Class<T> annotationClass) {
    T annotation = null;

    Method writeMethod = descriptor.getWriteMethod();
    if (writeMethod != null) {
        annotation = writeMethod.getAnnotation(annotationClass);
    }

    if (annotation == null) {
        Method readMethod = descriptor.getReadMethod();
        if (readMethod != null) {
            annotation = readMethod.getAnnotation(annotationClass);
        }
    }

    return annotation;
	}

	public static void main(String[] args) {
		PropertyDescriptor p = getProperty(getBeanInfo(LOSWorkingArea.class), "positionList");
		System.out.println("ELEMENT TYPE : " + getListElementType(p));
	}
}
