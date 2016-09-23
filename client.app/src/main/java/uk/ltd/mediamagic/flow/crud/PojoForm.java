package uk.ltd.mediamagic.flow.crud;

import java.beans.BeanInfo;
import java.beans.PropertyDescriptor;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.logging.Logger;

import org.jboss.logmanager.Level;
import org.mywms.model.BasicEntity;

import javafx.scene.Node;
import javafx.scene.control.ListView;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import uk.ltd.mediamagic.debug.MLogger;
import uk.ltd.mediamagic.fx.Units;
import uk.ltd.mediamagic.fx.control.FxmlNamespaceAware;
import uk.ltd.mediamagic.fx.control.SimpleFormBuilder;
import uk.ltd.mediamagic.fx.control.SimpleFormBuilder.Row;
import uk.ltd.mediamagic.fx.controller.MapFormController;
import uk.ltd.mediamagic.mywms.common.BeanUtils;

public class PojoForm extends SplitPane {
	protected static final Comparator<PropertyDescriptor> propertyTypeComparator = Comparator.comparing(p -> p.getPropertyType().getName()) ;
	protected static final Comparator<PropertyDescriptor> propertyNameComparator = Comparator.comparing(p -> p.getName()) ;

	private final Logger log = MLogger.log(this);
	private Comparator<PropertyDescriptor> propertyComparator = propertyTypeComparator.thenComparing(propertyNameComparator);
	
	private SimpleFormBuilder mainForm = new SimpleFormBuilder();
	private VBox lists = new VBox(Units.ex(1));
	private final BeanInfo beanInfo;
	
	public PojoForm(BeanInfo beanInfo) {
		this.beanInfo = beanInfo;
	}
	
	public void init() {
		List<PropertyDescriptor> descriptors = Arrays.asList(beanInfo.getPropertyDescriptors());
		if (propertyComparator != null) {
			Collections.sort(descriptors, propertyComparator);
		}
		buildForm(mainForm, descriptors);

		getItems().add(mainForm);
		
		if (lists.getChildren().isEmpty()) {
			lists = null;
		}
		else {
			getItems().add(lists);
		}		
	}
	
	public void addRight(String title, Node n) {
		AnchorPane.setBottomAnchor(n, 0d);
		AnchorPane.setTopAnchor(n, 0d);
		AnchorPane.setLeftAnchor(n, 0d);
		AnchorPane.setRightAnchor(n, 0d);
		TitledPane t = new TitledPane(title, new AnchorPane(n));
		t.setExpanded(lists.getChildren().size() < 3); 
		lists.getChildren().add(t);		
	}
	
	protected void buildSubForm(String title, Consumer<SimpleFormBuilder> builder) {
		SimpleFormBuilder subForm = new SimpleFormBuilder();
		builder.accept(subForm);
		TitledPane t = new TitledPane(title, subForm);
		mainForm.row().fieldNode(t, GridPane.REMAINING, 1).end();
		mainForm.bindSubForm(subForm);
	}
	
	protected void buildForm(SimpleFormBuilder form, List<PropertyDescriptor> descriptors) {
		for(PropertyDescriptor pds : descriptors) {
			if ("class".equals(pds.getName())) continue; // skip the class property
			else if (Collection.class.isAssignableFrom(pds.getPropertyType())) {
				addListFor(pds);
			}
			else {
				Row row = form.row();
				row.apply(r -> addFieldFor(r, pds));
				HBox spring = new HBox();
				spring.setPrefSize(0, 0);
				spring.setMinSize(0, 0);
				spring.setMaxSize(Double.MAX_VALUE, 0);
				
				row.fieldNode(spring);
				row.hGrow(Priority.SOMETIMES);
				row.end();
			}
		}
	}
	
	protected SimpleFormBuilder getForm() {
		return mainForm;
	}

	public void addFieldFor(Row row, PropertyDescriptor pds) {
		row.label(BeanUtils.getDisplayName(pds)).hGrow(Priority.NEVER);
		addFieldForClass(row, pds.getName(), pds.getPropertyType());
	}
	
	protected void addToNamespace(String fieldName, Node n) {
		mainForm.getNamespace().put(fieldName, n);		
		MapFormController.setField(n, "#"+fieldName);
	}

	public void addListFor(PropertyDescriptor pds) {
		ListView<?> list = new ListView<>();
		String fieldName = pds.getName();
		
		addRight(BeanUtils.getDisplayName(pds), list);
		addToNamespace(fieldName, list);
	}
	
	public void addFieldForClass(Row row, String name, Class<?> clazz) {
		
		if ((clazz.isPrimitive() && clazz == Boolean.TYPE) || Boolean.class.isAssignableFrom(clazz)) {
			row.checkbox(name);
		}
		else if (Enum.class.isAssignableFrom(clazz)) {
			row.comboBox(name);
		}
		else if (BasicEntity.class.isAssignableFrom(clazz)) {
			row.fieldNode(name, new BasicEntityEditor<>(), 1, 1);
		}
		else {
			log.log(Level.WARNING, "Using default for " + name + " - "+ clazz.getName());
			row.field(name);
		}
		row.hGrow(Priority.SOMETIMES);
	}
	
	public Comparator<PropertyDescriptor> getPropertyComparator() {
		return propertyComparator;
	}

	public void setPropertyComparator(Comparator<PropertyDescriptor> propertyComparator) {
		this.propertyComparator = propertyComparator;
	}

	public BeanInfo getBeanInfo() {
		return beanInfo;
	}

	public void bindController(FxmlNamespaceAware c) {
		Map<String, Node> ns = mainForm.getNamespace();
		ns.put("root", this);
		c.register(ns);
	}
}
