package uk.ltd.mediamagic.mywms.goodsout;

import java.beans.BeanInfo;
import java.beans.PropertyDescriptor;
import java.util.Collection;
import java.util.function.Function;

import org.mywms.model.BasicEntity;

import javafx.beans.value.ObservableValue;
import javafx.util.StringConverter;
import uk.ltd.mediamagic.common.utils.Strings;
import uk.ltd.mediamagic.flow.crud.MyWMSEditor.MyWMSEditorHelper;
import uk.ltd.mediamagic.flow.crud.PoJoEditor;

public class GoodsOutEditorHelper<T extends BasicEntity> extends MyWMSEditorHelper<T> {
			
	public GoodsOutEditorHelper(PoJoEditor<T> editor, BeanInfo beanInfo, Function<PropertyDescriptor, StringConverter<?>> getConverter) {
		super(editor, beanInfo, getConverter);
	}

	@Override
	public Collection<?> getValuesList(String id, ObservableValue<?> property) {
		if (Strings.equals(id, "state")) {
			return GoodsOutTypes.state.keySet();
		}
		else if (Strings.equals(id, "prio")) {
			return GoodsOutTypes.priority.keySet();
		}
		else {
			return super.getValuesList(id, property);
		}
	}
}

