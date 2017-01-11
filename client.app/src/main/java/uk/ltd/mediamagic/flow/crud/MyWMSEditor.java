package uk.ltd.mediamagic.flow.crud;

import java.beans.BeanInfo;
import java.beans.PropertyDescriptor;
import java.util.Collection;
import java.util.Collections;
import java.util.function.Function;

import org.mywms.model.BasicEntity;

import javafx.beans.binding.BooleanExpression;
import javafx.beans.value.ObservableBooleanValue;
import javafx.beans.value.ObservableValue;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.ListView;
import javafx.util.StringConverter;
import uk.ltd.mediamagic.fx.FXUtils;
import uk.ltd.mediamagic.fx.controller.editor.EditorHelper;
import uk.ltd.mediamagic.fx.controller.editor.NodeHelper;
import uk.ltd.mediamagic.fx.controller.editor.NodeHelper.ControlHelperBase;
import uk.ltd.mediamagic.fx.controller.editor.NodeHelper.ListViewHelper;
import uk.ltd.mediamagic.fx.converters.ToStringConverter;
import uk.ltd.mediamagic.fx.data.TableKey;
import uk.ltd.mediamagic.fx.flow.ContextBase;
import uk.ltd.mediamagic.fx.flow.Flow;
import uk.ltd.mediamagic.mywms.common.BeanUtils;
import uk.ltd.mediamagic.mywms.common.Editor;
import uk.ltd.mediamagic.plugin.PluginRegistry;

public class MyWMSEditor<T extends BasicEntity> extends PoJoEditor<T> { 

	//final private Logger log = MLogger.log(this);

	public MyWMSEditor(BeanInfo beanInfo, Function<PropertyDescriptor, StringConverter<?>> getConveryer) {
		super(beanInfo);
		setEditorHelper(new MyWMSEditorHelper(beanInfo, getConveryer));
	}

	@Override
	public TableKey getSelectedKey() {
		T sel = getData();
		return CRUDKeyUtils.createKey(sel);
	}

	@Override
	public Collection<TableKey> getSelectedKeys() {
		return Collections.singleton(getSelectedKey());
	}

	@SuppressWarnings("unchecked")
	protected <N extends Node> NodeHelper<N> createHelper(String id, Class<?> cls) {
		if (cls == BasicEntityEditor.class) {
			return (NodeHelper<N>) new BEEditorHelper();
		}
		else if (cls == ListView.class) {
			return (NodeHelper<N>) new BEListViewHelper();
		}
		else {
			return super.createHelper(id, cls);
		}
	}

	public class MyWMSEditorHelper extends PojoEditorHelper {

		private final Function<PropertyDescriptor, StringConverter<?>> getConverter;
		
		public MyWMSEditorHelper(BeanInfo beanInfo, Function<PropertyDescriptor, StringConverter<?>> getConverter) {
			super(beanInfo);
			this.getConverter = getConverter; 
		}
		
		@Override
		public ObservableBooleanValue getEditableProperty(String id, ObservableValue<?> property) {
			return super.getEditableProperty(id, property);
		}

		@Override
		public StringConverter<?> getConverter(String id, ObservableValue<?> property) {
			PropertyDescriptor pds = getPropertyDescriptor(id);
			StringConverter<?> conv = (getConverter == null) ? null : getConverter.apply(pds);
			if (conv == null) {
				if (BasicEntity.class.isAssignableFrom(pds.getPropertyType())) {
					return ToStringConverter.of(BasicEntity::toUniqueString);
				}
				else {
					return super.getConverter(id, property);
				}
			}
			return conv;
		}
	}
	
	class BEListViewHelper extends ListViewHelper {

		@SuppressWarnings({ "unchecked", "rawtypes" })
		protected final void editItem(ContextBase context, Class<?> itemClass, long id) {
			Editor<? extends BasicEntity> editor = PluginRegistry.getPlugin(itemClass, Editor.class);
			editor.edit(context, (Class) itemClass, id);			
		}

		@SuppressWarnings({ "unchecked", "rawtypes" })
		protected final void viewItem(ContextBase context, Class<?> itemClass, long id) {
			Editor<? extends BasicEntity> editor = PluginRegistry.getPlugin(itemClass, Editor.class);
			editor.view(context, context.getBean(Flow.class), (Class) itemClass, id);			
		}

		@Override
		@SuppressWarnings({ "unchecked", "rawtypes" })		
		public void bind(EditorHelper e, String id, ListView node) {
			super.bind(e, id, node);
			if (e instanceof PoJoEditor.PojoEditorHelper) {
				PropertyDescriptor pd = ((PoJoEditor<?>.PojoEditorHelper)e).getPropertyDescriptor(id); 
				
				Class<?> elementType = BeanUtils.getListElementType(pd);
				if (BasicEntity.class.isAssignableFrom(elementType) && !node.getProperties().containsKey("BASIC-ENTITY-MENU-SET")) {
					ListView<? extends BasicEntity> listView = (ListView<? extends BasicEntity>) node;
					ContextMenu cm = node.getContextMenu();
					if (cm == null) {
						cm = new ContextMenu();
						node.setContextMenu(cm);
					}
					cm.getItems().add(FXUtils.createMenuItem("Edit", e1 -> {
						BasicEntity item = listView.getSelectionModel().getSelectedItem();
						if (item != null) editItem(getContext(), item.getClass(), item.getId());
						e1.consume();
					}));
					cm.getItems().add(FXUtils.createMenuItem("View", e1 -> {
						BasicEntity item = listView.getSelectionModel().getSelectedItem();
						if (item != null) viewItem(getContext(), item.getClass(), item.getId());
						e1.consume();
					}));
					node.setOnMouseClicked(e1 -> {
						if (e1.getClickCount() == 2) {
							BasicEntity item = listView.getSelectionModel().getSelectedItem();
							if (item != null) viewItem(getContext(), item.getClass(), item.getId());							
							e1.consume();
						}
					});
					node.getProperties().put("BASIC-ENTITY-MENU-SET", true);
				}
			}
		}		
	}
	
	@SuppressWarnings("rawtypes")
	class BEEditorHelper extends ControlHelperBase<BasicEntityEditor> {
		public BEEditorHelper() {
		}
	
		@SuppressWarnings("unchecked")
		@Override
		public void bind(EditorHelper e, String id, BasicEntityEditor node) {
			ObservableValue value = e.getValueProperty(id);
			Class type = e.getValueClass(id);
			
			if (BasicEntity.class.isAssignableFrom(type)) {
				node.configure(getContext(), type);
			}
			
			ObservableBooleanValue editable = e.getEditableProperty(id, value);
			ObservableBooleanValue visible = e.getVisibleProperty(id, value);

			StringConverter converter = e.getConverter(id, value);			
			if (converter != null) node.setConverter(converter);

			bindBidirectionalValue(node, node.valueProperty(), value);
			if (editable != null) node.disableProperty().bind(BooleanExpression.booleanExpression(editable).not());
			if (visible != null) node.visibleProperty().bind(visible);
			e.onBind(id, value, node);
		}

		@Override
		public void unbind(EditorHelper e, String id, BasicEntityEditor node) {
			node.visibleProperty().unbind();
			node.disableProperty().unbind();
			unbindBidirectionalValue(node, node.valueProperty());
			e.onUnbind(id, node);
		}		
	}
}
