package uk.ltd.mediamagic.flow.crud;

import java.beans.BeanInfo;
import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.persistence.Lob;

import org.mywms.model.BasicEntity;

import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.cell.TextFieldListCell;
import uk.ltd.mediamagic.common.utils.MArrays;
import uk.ltd.mediamagic.common.utils.Strings;
import uk.ltd.mediamagic.debug.MLogger;
import uk.ltd.mediamagic.fx.control.SimpleFormBuilder;
import uk.ltd.mediamagic.fx.control.SimpleFormBuilder.Row;
import uk.ltd.mediamagic.fx.controller.list.CellWrappers;
import uk.ltd.mediamagic.fx.converters.ToStringConverter;
import uk.ltd.mediamagic.mywms.common.BeanUtils;
import uk.ltd.mediamagic.mywms.common.Editor;
import uk.ltd.mediamagic.plugin.PluginRegistry;

public class MyWMSForm extends PojoForm {

	private static final Logger log = MLogger.log(MyWMSForm.class);
	
	private final List<SubForm> forms;

	public MyWMSForm(BeanInfo beanInfo, List<SubForm> columnOrder) {
		super(beanInfo);
		setStyle(".text-field { -fx-max-width: 1000ex }");
		Comparator<PropertyDescriptor> propertyComparator = this::compareProperty;
		this.forms = columnOrder;
		setPropertyComparator(propertyComparator.thenComparing(getPropertyComparator()));
		init();
	}

	@SuppressWarnings("unchecked")
	public void addListFor(PropertyDescriptor pds) {
		Class<? extends BasicEntity> elementType = (Class<? extends BasicEntity>) BeanUtils.getListElementType(pds);

		ListView<?> list = createList(elementType);
		String fieldName = pds.getName();

		addRight(BeanUtils.getDisplayName(pds), list);
		addToNamespace(fieldName, list);
	}

	public static <T extends BasicEntity> ListView<T> createList(Class<T> elementType) {
		ListView<T> list = new ListView<>();

		@SuppressWarnings("unchecked")
		Editor<T> editor = PluginRegistry.getPlugin(elementType, Editor.class);
		if (editor == null) {
			list.setCellFactory(TextFieldListCell.forListView(ToStringConverter.of(Object::toString)));
		}
		else {
			list.setCellFactory(CellWrappers.forList(editor.createCellFactory()));
		}
		return list;
	}

	@Override
	public void addFieldFor(Row row, PropertyDescriptor pds) {
		if (Strings.equals("additionalContent", pds.getName())) {
			TextArea area = new TextArea();
			area.setPrefRowCount(10);
			addRight(BeanUtils.getDisplayName(pds), area);
			addToNamespace(pds.getName(), area);
		}
		else if (Strings.equals("lock", pds.getName())) {
			row.label(BeanUtils.getDisplayName(pds)).comboBox("lock");
		}
		else if (Strings.equals("state", pds.getName())) {
			row.label(BeanUtils.getDisplayName(pds)).comboBox("state");
		}
		else {
			super.addFieldFor(row, pds);
		}
	}

	protected int compareProperty(PropertyDescriptor a, PropertyDescriptor b) {
		int i = forms.indexOf(a.getName());
		int j = forms.indexOf(b.getName());
		if (i < 0) i = Integer.MAX_VALUE;
		if (j < 0) j = Integer.MAX_VALUE;
		return Integer.compare(i, j);
	}

	@Override
	protected void buildForm(SimpleFormBuilder form, List<PropertyDescriptor> descriptorsIn) {
		List<PropertyDescriptor> descriptors = new ArrayList<>(descriptorsIn);
		String[] basic = {"created", "modified", "id", "client"};
		{
			buildSubForm("Basic properties", f -> doFields(f, 2, basic));
		}

		String[] lock = {"lock", "locked"};
		{
			buildSubForm("Lock", f -> doFields(f, 2, lock));
		}

		descriptors.removeIf(p -> MArrays.contains(p.getName(), basic));
		descriptors.removeIf(p -> MArrays.contains(p.getName(), lock));

		for (SubForm s : forms) {
			if (s.columns() > 0) {
				buildSubForm(s.title(), f -> doFields(f, s.columns(), s.properties()));
			}
			descriptors.removeIf(p -> MArrays.contains(p.getName(), s.properties()));
		}
		descriptors.removeIf(p -> MArrays.contains(p.getName(), "version"));
		super.buildForm(form, descriptors);
	}
	
	public void doFields(SimpleFormBuilder form, int colCount, String[] names) {
		int col = 0;
		Row row = null;
		for (String name : names) {
			PropertyDescriptor p = BeanUtils.getProperty(getBeanInfo(), name);
			if (p == null) {
				log.log(Level.WARNING, "Unable to find property {0}", name);
				continue;
			}
			else if (List.class.isAssignableFrom(p.getPropertyType())) {
				addListFor(p);
			}
			else if (p.getReadMethod().isAnnotationPresent(Lob.class)) {
				TextArea area = new TextArea();
				area.setPrefRowCount(10);
				addRight(BeanUtils.getDisplayName(p), area);
				addToNamespace(p.getName(), area);				
			}
			else {
				if (row == null) {
					row = form.row();
					col = 0;
				}
				row.apply(r -> addFieldFor(r, p));
				col ++;
				if (col >= colCount) {
					row.end();
					row = null;
				}
			}
		}
		if (row != null) {
			row.end();
		}
	}

}
