package uk.ltd.mediamagic.mywms.master;

import java.util.Arrays;
import java.util.List;

import de.linogistix.los.inventory.model.LOSOrderStrategy;
import javafx.beans.binding.BooleanBinding;
import uk.ltd.mediamagic.flow.crud.BODTOPlugin;
import uk.ltd.mediamagic.flow.crud.SubForm;
import uk.ltd.mediamagic.mywms.common.MyWMSUserPermissions;

@SubForm(title="Main", isRequired=true, properties={"client", "name", "defaultDestination", "manualCreationIndex"})
@SubForm(title="Processing", properties={"createGoodsOutOrder", "createFollowUpPicks"})
@SubForm(title="Stock Selection", properties={"useLockedStock", "useLockedLot", "preferUnopened", "preferMatchingStock"})
public class OrderStrategyPlugin extends BODTOPlugin<LOSOrderStrategy> {
	
	public OrderStrategyPlugin() {
		super(LOSOrderStrategy.class);
		setUserPermissions(new MyWMSUserPermissions.ForMasterData());
	}
	
	@Override
	protected BooleanBinding createVisibleBinding() {
		return MyWMSUserPermissions.atLeastForeman();
	}
	
	@Override
	public String getPath() {
		return "{1, _Master Data} -> {1, _Strategies} -> {1, _Order Strategy}";
	}

	@Override
	protected List<String> getTableColumns() {
		return Arrays.asList("id", "name", "client");
	}

}
