package uk.ltd.mediamagic.mywms;

import uk.ltd.mediamagic.mywms.master.JasperReportsPlugin;
import uk.ltd.mediamagic.plugin.AbstractPluginSet;
import uk.ltd.mediamagic.plugin.PluginRelation;

public class SystemModule extends AbstractPluginSet {

  public SystemModule() {
    super("System");
  }
  
  @Override
  public PluginRelation getPlugins() {
    PluginRelation m = new PluginRelation();

    m.put(JasperReportsPlugin.class);

    return m;
  }
}
