package uk.ltd.mediamagic.mywms;

import uk.ltd.mediamagic.mywms.system.ClientsPlugin;
import uk.ltd.mediamagic.mywms.system.JasperReportsPlugin;
import uk.ltd.mediamagic.mywms.system.PropertiesPlugin;
import uk.ltd.mediamagic.mywms.system.RolesPlugin;
import uk.ltd.mediamagic.mywms.system.UsersPlugin;
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
    m.put(UsersPlugin.class);
    m.put(RolesPlugin.class);
    m.put(ClientsPlugin.class);
    m.put(PropertiesPlugin.class);

    return m;
  }
}
