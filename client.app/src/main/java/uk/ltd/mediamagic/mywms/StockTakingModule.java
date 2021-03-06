package uk.ltd.mediamagic.mywms;

import de.linogistix.los.stocktaking.model.LOSStocktakingRecord;
import uk.ltd.mediamagic.mywms.stocktaking.StockTakingOrdersPlugin;
import uk.ltd.mediamagic.mywms.stocktaking.StockTakingPlugin;
import uk.ltd.mediamagic.mywms.stocktaking.StockTakingRecordsPlugin;
import uk.ltd.mediamagic.plugin.AbstractPluginSet;
import uk.ltd.mediamagic.plugin.PluginRelation;

public class StockTakingModule extends AbstractPluginSet {

  public StockTakingModule() {
    super("StockTaking");
  }
  
  @Override
  public PluginRelation getPlugins() {
    PluginRelation m = new PluginRelation();

    m.put(LOSStocktakingRecord.class, StockTakingRecordsPlugin.class);
    m.put(StockTakingRecordsPlugin.class);
    m.put(StockTakingOrdersPlugin.class);
    m.put(StockTakingPlugin.class);

    return m;
  }
}
