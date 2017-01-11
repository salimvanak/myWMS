package uk.ltd.mediamagic.mywms.test;

import java.util.Properties;

import org.mywms.ejb.BeanLocator;
import org.mywms.model.ItemData;
import org.mywms.model.User;
import org.mywms.service.StockUnitService;

import de.linogistix.los.inventory.crud.ItemDataCRUDRemote;
import de.linogistix.los.inventory.query.ItemDataQueryRemote;
import de.linogistix.los.query.ClientQueryRemote;
import de.linogistix.los.user.query.UserQueryRemote;

public class Main {


	public static void main(String[] string) throws Exception {
		try {
			BeanLocator beans = lookupBeanLocator();

			ItemDataQueryRemote items = beans.getStateless(ItemDataQueryRemote.class);
			StockUnitService bean = beans.getStateless(StockUnitService.class);
			ItemDataCRUDRemote itemDataService = beans.getStateless(ItemDataCRUDRemote.class);
			ClientQueryRemote clientService = beans.getStateless(ClientQueryRemote.class);

			ItemData item = items.queryByIdentity("01066");
			System.out.print(item.toString() + "  -  ");
//		System.out.println(bean.getAvailableStock(item));

			UserQueryRemote userQuery = beans.getStateless(UserQueryRemote.class);
			User user = userQuery.queryByIdentity("slim");
			System.out.println("GOT USERS " + user);
			System.out.print(user.getRoles());
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			for (Throwable ex : e.getSuppressed()) {
				System.out.println("* " + ex);
			}
		}
	}

	public static BeanLocator lookupBeanLocator() {
		// if you want to load the config from a file.
		//Properties jndi = AppPreferences.loadFromClasspath("/config/wf8-context.properties");
		//Properties appserver = AppPreferences.loadFromClasspath("/config/appserver.properties");

		Properties jndi = new Properties();
		jndi.setProperty("org.mywms.env.as.vendor", "jboss");
		jndi.setProperty("org.mywms.env.as.version", "8.2");

		jndi.setProperty("remote.connections", "default");
		jndi.setProperty("remote.connection.default.port","8080");
		jndi.setProperty("remote.connection.default.host", "localhost");
		jndi.setProperty("remote.connection.default.connect.timeout","1500");

		jndi.setProperty("remote.connection.default.connect.options.org.xnio.Options.SASL_POLICY_NOANONYMOUS", "true");
		jndi.setProperty("remote.connection.default.connect.options.org.xnio.Options.SASL_POLICY_NOPLAINTEXT", "false");
		jndi.setProperty("remote.connection.default.connect.options.org.xnio.Options.SASL_DISALLOWED_MECHANISMS", "JBOSS-LOCAL-USER");
		jndi.setProperty("remote.connectionprovider.create.options.org.xnio.Options.SSL_ENABLED", "false");

		Properties appserver = new Properties();
		appserver.setProperty("org.mywms.env.applicationName", "los.reference");
		appserver.setProperty("org.mywms.env.mapping.project-ejb3", "de.linogistix.los.reference,de.linogistix.los.common.facade.VersionFacade");
		appserver.setProperty("org.mywms.env.mapping.myWMS-comp", "org.mywms");
		appserver.setProperty("org.mywms.env.mapping.los.stocktaking-comp", "de.linogistix.los.stocktaking");
		appserver.setProperty("org.mywms.env.mapping.los.mobile-comp", "de.linogistix.mobileserver");
		appserver.setProperty("org.mywms.env.mapping.los.mobile","de.linogistix.mobile.common, de.linogistix.mobile.processes");
		appserver.setProperty("org.mywms.env.mapping.los.location-comp", "de.linogistix.los.location");
		appserver.setProperty("org.mywms.env.mapping.los.inventory-ws", "de.linogistix.los.inventory.ws");
		appserver.setProperty("org.mywms.env.mapping.los.inventory-comp", "de.linogistix.los.inventory");
		appserver.setProperty("org.mywms.env.mapping.los.common-comp", "de.linogistix.los.common,de.linogistix.los.crud,de.linogistix.los.customization,de.linogistix.los.entityservice,de.linogistix.los.query,de.linogistix.los.report,de.linogistix.los.runtime,de.linogistix.los.user,de.linogistix.los.util");

		BeanLocator b = new BeanLocator("slim", "god1rifewoX", jndi, appserver);
		return b;
	}

}
