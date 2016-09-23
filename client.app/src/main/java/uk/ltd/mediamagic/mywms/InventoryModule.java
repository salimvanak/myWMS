package uk.ltd.mediamagic.mywms;

import org.mywms.model.Lot;
import org.mywms.model.StockUnit;

import de.linogistix.los.location.model.LOSUnitLoad;
import uk.ltd.mediamagic.mywms.inventory.LotsPlugin;
import uk.ltd.mediamagic.mywms.inventory.StockUnitsPlugin;
import uk.ltd.mediamagic.mywms.inventory.UnitsLoadsPlugin;
import uk.ltd.mediamagic.plugin.AbstractPluginSet;
import uk.ltd.mediamagic.plugin.PluginRelation;

public class InventoryModule extends AbstractPluginSet {

  public InventoryModule() {
    super("Inventory");
  }
  
  @Override
  public PluginRelation getPlugins() {
    PluginRelation m = new PluginRelation();
    
    m.put(LotsPlugin.class);
    m.put(Lot.class, LotsPlugin.class);
    
    m.put(StockUnitsPlugin.class);
    m.put(StockUnit.class, StockUnitsPlugin.class);
    
    m.put(UnitsLoadsPlugin.class);
    m.put(LOSUnitLoad.class, UnitsLoadsPlugin.class);
    return m;
  }
}
