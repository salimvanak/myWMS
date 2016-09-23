package uk.ltd.mediamagic.mywms;

import de.linogistix.los.inventory.model.LOSAdvice;
import de.linogistix.los.inventory.model.LOSGoodsReceipt;
import de.linogistix.los.inventory.model.LOSGoodsReceiptPosition;
import uk.ltd.mediamagic.mywms.goodsin.AdvicePlugin;
import uk.ltd.mediamagic.mywms.goodsin.GoodsReceiptPositionsPlugin;
import uk.ltd.mediamagic.mywms.goodsin.GoodsReceiptsPlugin;
import uk.ltd.mediamagic.plugin.AbstractPluginSet;
import uk.ltd.mediamagic.plugin.PluginRelation;

public class GoodsInModule extends AbstractPluginSet {

  public GoodsInModule() {
    super("Goods in");
  }
  
  @Override
  public PluginRelation getPlugins() {
    PluginRelation m = new PluginRelation();
    
    m.put(AdvicePlugin.class);
    m.put(LOSAdvice.class, AdvicePlugin.class);
    m.put(GoodsReceiptsPlugin.class);
    m.put(LOSGoodsReceipt.class, GoodsReceiptsPlugin.class);
    m.put(GoodsReceiptPositionsPlugin.class);
    m.put(LOSGoodsReceiptPosition.class, GoodsReceiptPositionsPlugin.class);

    return m;
  }
}
