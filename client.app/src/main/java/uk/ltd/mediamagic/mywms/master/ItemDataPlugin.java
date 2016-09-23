package uk.ltd.mediamagic.mywms.master;

import java.beans.PropertyDescriptor;
import java.util.Arrays;
import java.util.List;

import org.mywms.model.ItemData;

import de.linogistix.los.inventory.query.dto.ItemDataTO;
import de.linogistix.los.query.BODTO;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.cell.TextFieldListCell;
import javafx.util.Callback;
import javafx.util.StringConverter;
import uk.ltd.mediamagic.flow.crud.BODTOPlugin;
import uk.ltd.mediamagic.flow.crud.SubForm;
import uk.ltd.mediamagic.fx.AwesomeIcon;
import uk.ltd.mediamagic.fx.controller.list.MaterialListItems;
import uk.ltd.mediamagic.fx.converters.ToStringConverter;
import uk.ltd.mediamagic.mywms.common.Editor;
import uk.ltd.mediamagic.mywms.common.MyWMSUserPermissions;

@SubForm(
		title="Main Properties", columns=1, 
		properties={"number","name","description", "safetyStock", "defaultUnitLoadType", "tradeGroup"}
	)
@SubForm(
		title="Controls", columns=2,
		properties={"zone", "lotMandatory", "lotSubstitutionType", "adviceMandatory", "residualTermOfUsageGI", "serialNoRecordType"}
	)
@SubForm(
		title="Unit", columns=2,
		properties={"numberOfDecimals", "handlingUnit"}
		)
@SubForm(
		title="Measurment", columns=2, 
		properties={"height", "width", "depth", "weight"}
	)
@SubForm(
		title="Hidden", columns=0, 
		properties={"locked", "scale", "version", "volume"}
	)
public class ItemDataPlugin extends BODTOPlugin<ItemData> implements Editor<ItemData> {

	public ItemDataPlugin() {
		super(ItemData.class);
		setUserPermissions(new MyWMSUserPermissions.ForMasterData());
	}
	
	@Override
	protected StringConverter<?> getConverter(PropertyDescriptor property) {
		return super.getConverter(property);
	}
	
	@Override
	public Callback<ListView<BODTO<ItemData>>, ListCell<BODTO<ItemData>>> createTOListCellFactory() {
		return TextFieldListCell.forListView(ToStringConverter.of(i -> {
			ItemDataTO to = (ItemDataTO) i;
			return String.format("%s, %s", to.getNumber(), to.getNameX());
		}));
	}
	
	@Override
	public Callback<ListView<ItemData>, ListCell<ItemData>> createListCellFactory() {
		return MaterialListItems.withID(a -> new AwesomeIcon(AwesomeIcon.foursquare), 
				ItemData::getClient, ItemData::toUniqueString,
				ItemData::getDefaultUnitLoadType, ItemData::getDescription);
	}

	@Override
	public String getPath() {
		return "{1, _Master Data} -> {1, _Inventory} -> {1, _Item Data}";
	}

	@Override
	public List<String> getTableColumns() {
		return Arrays.asList("number", "clientNumber AS client.name", "nameX AS name");
	}
	
}
