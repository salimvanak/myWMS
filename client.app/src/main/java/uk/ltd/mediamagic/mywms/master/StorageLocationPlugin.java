package uk.ltd.mediamagic.mywms.master;

import java.beans.PropertyDescriptor;
import java.util.Arrays;
import java.util.List;

import javax.persistence.Column;

import de.linogistix.los.location.constants.LOSStorageLocationLockState;
import de.linogistix.los.location.model.LOSStorageLocation;
import javafx.util.StringConverter;
import uk.ltd.mediamagic.flow.crud.BODTOPlugin;
import uk.ltd.mediamagic.flow.crud.SubForm;
import uk.ltd.mediamagic.fx.converters.PercentConverter;
import uk.ltd.mediamagic.mywms.common.BeanUtils;
import uk.ltd.mediamagic.mywms.common.Editor;
import uk.ltd.mediamagic.mywms.common.LockStateConverter;
import uk.ltd.mediamagic.mywms.common.MyWMSUserPermissions;

@SubForm(
		title="Main", columns=2, 
		properties={"name", "scanCode", "type", "area", "cluster", "zone", "rack", "field", "XPos", "YPos", "ZPos", "fieldIndex", "orderIndex"}
	)
@SubForm(
		title="Allocation", columns=2, 
		properties={"allocation", "allocationState", "currentTypeCapacityConstraint"}
	)
@SubForm(
		title="Stock Taking", columns=2, 
		properties={"stockTakingDate"}
	)

@SubForm(
		title="Hidden", columns=0, 
		properties={"age", "code", "locked", "version", "volume"}
	)

public class StorageLocationPlugin extends BODTOPlugin<LOSStorageLocation> implements Editor<LOSStorageLocation> {

	public StorageLocationPlugin() {
		super(LOSStorageLocation.class);
		setUserPermissions(new MyWMSUserPermissions.ForMasterData());
		setUserPermissions(new MyWMSUserPermissions.ForMasterData());
	}
	
	@Override
	public String getPath() {
		return "{1, _Master Data} -> {1, _Location} -> {1, _Locations}";
	}
	
	@Override
	protected StringConverter<?> getConverter(PropertyDescriptor property) {
		if ("lock".equals(property.getName())) {
			return new LockStateConverter<>(LOSStorageLocationLockState.class);
		}
		else if ("allocation".equals(property.getName())) {
			Column annot = BeanUtils.getAnnotation(property, Column.class);
			if (annot == null) return PercentConverter.forScaledPercent();
			else return PercentConverter.forScaledPercent(annot.precision(), annot.scale());
		}
		else {
			return super.getConverter(property);			
		}
	}
	
	@Override
	public List<String> getTableColumns() {
		return Arrays.asList("name", "client", "type", "area", "zone", "lock");
	}
}
