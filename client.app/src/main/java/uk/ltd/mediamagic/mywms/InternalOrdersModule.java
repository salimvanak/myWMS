package uk.ltd.mediamagic.mywms;

import uk.ltd.mediamagic.mywms.internal.ReplenishPlugin;
import uk.ltd.mediamagic.mywms.internal.StorageRequestsPlugin;
import uk.ltd.mediamagic.plugin.AbstractPluginSet;
import uk.ltd.mediamagic.plugin.PluginRelation;

public class InternalOrdersModule extends AbstractPluginSet {

  public InternalOrdersModule() {
    super("Internal Orders");
  }
  
  @Override
  public PluginRelation getPlugins() {
    PluginRelation m = new PluginRelation();
    m.put(StorageRequestsPlugin.class);
    m.put(ReplenishPlugin.class);
    
    return m;
  }
}
