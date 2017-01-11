package uk.ltd.mediamagic.flow.crud;

import java.beans.BeanInfo;
import java.beans.PropertyDescriptor;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.UnaryOperator;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.persistence.Column;
import javax.persistence.Id;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.adapter.JavaBeanObjectPropertyBuilder;
import javafx.beans.property.adapter.ReadOnlyJavaBeanObjectPropertyBuilder;
import javafx.beans.value.ObservableBooleanValue;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.scene.Node;
import javafx.scene.control.TextFormatter.Change;
import javafx.util.StringConverter;
import uk.ltd.mediamagic.debug.MLogger;
import uk.ltd.mediamagic.fx.binding.AutoFill;
import uk.ltd.mediamagic.fx.binding.MBindings;
import uk.ltd.mediamagic.fx.controller.editor.EditorBase;
import uk.ltd.mediamagic.fx.controller.editor.EditorHelper;
import uk.ltd.mediamagic.fx.data.TableKey;
import uk.ltd.mediamagic.fxcommon.ObservableConstant;
import uk.ltd.mediamagic.mywms.common.BeanUtils;

public abstract class PoJoEditor<T> extends EditorBase {

	private ObjectProperty<T> data = new SimpleObjectProperty<>(); 
	private Logger log = MLogger.log(this);
	
	public PoJoEditor(BeanInfo beanInfo) {
		super();
		getCommands()
			.back()
		  .saveAndRefresh()
		.end();
		setEditorHelper(new PojoEditorHelper(beanInfo));
		data.addListener((v,o,n) -> rebind());
	} 
	
	public void rebind() {
		nodeNamespace.forEach((k, n) -> {
			String field = getField(n);
			unbind(field, n);
			bind(field, n);			
		});
	}
	
	@Override
	public Collection<TableKey> getSelectedKeys() {
		return Collections.singleton(getSelectedKey());
	}

	public class PojoEditorHelper implements EditorHelper {

		private final BeanInfo beanInfo;
		
		public PojoEditorHelper(BeanInfo beanInfo) {
			super();
			this.beanInfo = beanInfo;
		}
		
		@Override
		public AutoFill<?> getAutoFill(String id, ObservableValue<?> property) {
			return null;
		}

		protected PropertyDescriptor getPropertyDescriptor(String name) {
			return Arrays.stream(beanInfo.getPropertyDescriptors())
				.filter(p -> name.equals(p.getName()))
				.findFirst().orElse(null);
		}
		
		public ObservableValue<?> getValueProperty(String id) {
			try {
				if (data.get() == null) return new SimpleObjectProperty<>(data, id);
				PropertyDescriptor pds = getPropertyDescriptor(id);
				if (pds == null) throw new IllegalArgumentException("Property " + id + " does not exist on " + beanInfo.getBeanDescriptor().getDisplayName());
				if (List.class.isAssignableFrom(pds.getPropertyType())) {
					@SuppressWarnings("unchecked")
					Property<java.util.List<?>> p = JavaBeanObjectPropertyBuilder.create().bean(data.get()).name(id).build();
					return MBindings.get(p, FXCollections::observableList); 
				}
				if (pds.getWriteMethod() == null) {					
					return ReadOnlyJavaBeanObjectPropertyBuilder.create().bean(data.get()).name(id).build();
				}
				else {
					return JavaBeanObjectPropertyBuilder.create().bean(data.get()).name(id).build();
				}
			}
			catch (NoSuchMethodException e) {
				log.log(Level.SEVERE, "While generating property " + id , e);
				throw new RuntimeException(e);
			}
		};

		@Override
		public Class<?> getValueClass(String id) {
			PropertyDescriptor pds = getPropertyDescriptor(id);
			return pds.getPropertyType();
		}
		
		public Collection<?> getValuesList(String id, ObservableValue<?> property) {
			PropertyDescriptor pds = getPropertyDescriptor(id);
			Class<?> type = pds.getPropertyType();
			if (Enum.class.isAssignableFrom(type)) {
				return Arrays.asList(type.getEnumConstants());
			}
			else {
				return Collections.emptyList();
			}
		};

		public ObservableBooleanValue getEditableProperty(String id, ObservableValue<?> property) {
			PropertyDescriptor pds = getPropertyDescriptor(id);
			if (pds.getWriteMethod() == null) {
				return ObservableConstant.FALSE;
			}
			else if (pds.getReadMethod().isAnnotationPresent(Id.class)) {				
				return ObservableConstant.FALSE;
			}
			ObservableBooleanValue up = getUserPermissions().isEditable(id);
			return up;
		}

		public ObservableBooleanValue getVisibleProperty(String id, ObservableValue<?> property) {
			ObservableBooleanValue up = getUserPermissions().isVisible(id);
			return up;
		}
		
		public StringConverter<?> getConverter(String id, ObservableValue<?> property) {
			PropertyDescriptor pds = getPropertyDescriptor(id);
			return BeanUtils.getConverter(pds);
		}
		
		public UnaryOperator<Change> getFilter(String id, ObservableValue<?> property) {
			PropertyDescriptor pds = getPropertyDescriptor(id);
			Class<?> type = pds.getPropertyType();
			Column column = pds.getReadMethod().getAnnotation(Column.class);

			if (Number.class.isAssignableFrom(type)) {
				return uk.ltd.mediamagic.fx.converters.Filters.numeric();
			}
			else if (String.class.isAssignableFrom(type) && column != null) {				
				return uk.ltd.mediamagic.fx.converters.Filters.width(column.length());
			}
			return null;
		}
		
		public AutoFill<?> getAutoFill(String id, Property<?> property) {
			PropertyDescriptor pds = getPropertyDescriptor(id);
			Class<?> type = pds.getPropertyType();
			return getAutoFillFactory().createAutoFill(type, id);
		}
		
		public void onBind(String id, ObservableValue<?> property, Node n) {
		}
		
		public void onUnbind(String id, Node n) {
		}

		public BeanInfo getBeanInfo() {
			return beanInfo;
		}
	}


	public final ObjectProperty<T> dataProperty() {
		return this.data;
	}
	

	public final T getData() {
		return this.dataProperty().get();
	}
	

	public final void setData(final T data) {
		this.dataProperty().set(data);
	}
}
