package uk.ltd.mediamagic.mywms.master;

import java.beans.PropertyDescriptor;
import java.util.Arrays;
import java.util.List;

import javax.persistence.Column;

import de.linogistix.los.location.model.LOSTypeCapacityConstraint;
import javafx.util.StringConverter;
import uk.ltd.mediamagic.flow.crud.BODTOPlugin;
import uk.ltd.mediamagic.flow.crud.SubForm;
import uk.ltd.mediamagic.fx.converters.PercentConverter;
import uk.ltd.mediamagic.mywms.common.BeanUtils;
import uk.ltd.mediamagic.mywms.common.MyWMSUserPermissions;

@SubForm(
		title="Main", 
		properties={"locationType", "unitLoadType","type", "allocation", "orderIndex"}
	)
@SubForm(
		title="Measurment", columns=2, 
		properties={"height", "width", "depth", "weight"}
	)
public class CapacitiesPlugin extends BODTOPlugin<LOSTypeCapacityConstraint> {
	
	public CapacitiesPlugin() {
		super(LOSTypeCapacityConstraint.class);
		setUserPermissions(new MyWMSUserPermissions.ForMasterData());
	}

	@Override
	protected StringConverter<?> getConverter(PropertyDescriptor property) {
		if ("allocation".equals(property.getName())) {
			Column annot = BeanUtils.getAnnotation(property, Column.class);
			if (annot == null) return PercentConverter.forScaledPercent();
			else return PercentConverter.forScaledPercent(annot.precision(), annot.scale());
		}
		else {
			return super.getConverter(property);			
		}
	}

	@Override
	public String getPath() {
		return "{1, _Master Data} -> {1, _Location} -> {1, _Capacities}";
	}

	@Override
	protected List<String> getTableColumns() {
		return Arrays.asList("id", "name", "locationType", "unitLoadType", "allocation");
	}

}
