package uk.ltd.mediamagic.mywms;

import uk.ltd.mediamagic.mywms.documents.OrderReceiptsPlugin;
import uk.ltd.mediamagic.mywms.documents.PickingReceiptsPlugin;
import uk.ltd.mediamagic.mywms.documents.StockUnitLabelsPlugin;
import uk.ltd.mediamagic.plugin.AbstractPluginSet;
import uk.ltd.mediamagic.plugin.PluginRelation;

public class DocumentsModule extends AbstractPluginSet {

  public DocumentsModule() {
    super("Documents");
  }
  
  @Override
  public PluginRelation getPlugins() {
    PluginRelation m = new PluginRelation();
    m.put(OrderReceiptsPlugin.class);
    m.put(PickingReceiptsPlugin.class);
    m.put(StockUnitLabelsPlugin.class);
    return m;
  }
}
