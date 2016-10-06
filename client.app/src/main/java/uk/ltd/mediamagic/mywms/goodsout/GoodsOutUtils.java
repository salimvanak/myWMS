package uk.ltd.mediamagic.mywms.goodsout;

import de.linogistix.los.model.State;
import javafx.scene.Node;
import res.R;
import res.StandardIcons.IconSize;
import uk.ltd.mediamagic.flow.crud.BODTOTable;
import uk.ltd.mediamagic.mywms.common.QueryUtils;

public class GoodsOutUtils {
	public enum OpenFilter {Open, All};
	
	public static Node getIcon(int state) {
		if (state == State.RAW) return R.svgPaths.createIconFromFile("package8.svg", IconSize.XLarge);
		if (state <= State.RESERVED) return R.svgPaths.createIconFromFile("person279.svg", IconSize.XLarge);
		if (state <= State.STARTED) return R.svgPaths.createIconFromFile("delivery26.svg", IconSize.XLarge);
		if (state <= State.PENDING) return R.svgPaths.createIconFromFile("delivery35.svg", IconSize.XLarge);
		if (state <= State.PICKED) return R.svgPaths.createIconFromFile("delivery29.svg", IconSize.XLarge);
		if (state >= State.FINISHED) return R.svgPaths.createIconFromFile("packages1.svg", IconSize.XLarge);
		return R.svgPaths.goodsTransfer();
	}


	public static void addOpenFilter(BODTOTable<?> t, Runnable refreshData) {
		QueryUtils.addFilter(t, OpenFilter.Open, refreshData);
	}
	
	public static OpenFilter getFilter(BODTOTable<?> t) {
		return QueryUtils.getFilter(t, OpenFilter.Open);
	}
	
}
