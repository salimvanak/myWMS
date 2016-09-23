package uk.ltd.mediamagic.mywms;

import de.linogistix.los.inventory.model.LOSCustomerOrder;
import de.linogistix.los.inventory.model.LOSCustomerOrderPosition;
import de.linogistix.los.inventory.model.LOSGoodsOutRequest;
import de.linogistix.los.inventory.model.LOSGoodsOutRequestPosition;
import de.linogistix.los.inventory.model.LOSPickingOrder;
import de.linogistix.los.inventory.model.LOSPickingPosition;
import de.linogistix.los.inventory.model.LOSPickingUnitLoad;
import uk.ltd.mediamagic.mywms.goodsout.OrdersPlugin;
import uk.ltd.mediamagic.mywms.goodsout.PickingOrdersPlugin;
import uk.ltd.mediamagic.mywms.goodsout.PickingPositionsPlugin;
import uk.ltd.mediamagic.mywms.goodsout.PickingUnitLoadsPlugin;
import uk.ltd.mediamagic.mywms.goodsout.GoodsOutPositionsPlugin;
import uk.ltd.mediamagic.mywms.goodsout.GoodsOutRequestPlugin;
import uk.ltd.mediamagic.mywms.goodsout.OrderPositionsPlugin;
import uk.ltd.mediamagic.plugin.AbstractPluginSet;
import uk.ltd.mediamagic.plugin.PluginRelation;

public class GoodsOutModule extends AbstractPluginSet {

  public GoodsOutModule() {
    super("Goods in");
  }
  
  @Override
  public PluginRelation getPlugins() {
    PluginRelation m = new PluginRelation();
    
    m.put(OrdersPlugin.class);
    m.put(LOSCustomerOrder.class, OrdersPlugin.class);
    m.put(OrderPositionsPlugin.class);
    m.put(LOSCustomerOrderPosition.class, OrderPositionsPlugin.class);

    m.put(PickingOrdersPlugin.class);
    m.put(LOSPickingOrder.class, PickingOrdersPlugin.class);
    m.put(PickingPositionsPlugin.class);
    m.put(LOSPickingPosition.class, PickingPositionsPlugin.class);

    m.put(PickingUnitLoadsPlugin.class);
    m.put(LOSPickingUnitLoad.class, PickingUnitLoadsPlugin.class);

    m.put(GoodsOutRequestPlugin.class);
    m.put(LOSGoodsOutRequest.class, GoodsOutRequestPlugin.class);
    m.put(GoodsOutPositionsPlugin.class);
    m.put(LOSGoodsOutRequestPosition.class, GoodsOutPositionsPlugin.class);

    return m;
  }
}
