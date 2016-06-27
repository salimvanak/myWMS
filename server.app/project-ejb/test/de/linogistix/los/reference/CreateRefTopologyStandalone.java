package de.linogistix.los.reference;

import java.util.Properties;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.jboss.ejb.client.ContextSelector;
import org.jboss.ejb.client.EJBClientConfiguration;
import org.jboss.ejb.client.EJBClientContext;
import org.jboss.ejb.client.PropertiesBasedEJBClientConfiguration;
import org.jboss.ejb.client.remoting.ConfigBasedEJBClientContextSelector;
import org.mywms.facade.FacadeException;

import de.linogistix.los.reference.facade.RefTopologyFacade;

public class CreateRefTopologyStandalone {

	/**
	 * @param args
	 * @throws FacadeException
	 */
	public static void main(String[] args) throws FacadeException {

		// Lookup Format will be
		// <app-name>/<module-name>/<distinct-name>/<bean-name>!<fully-qualified-classname-of-the-remote-interface>



		Context context = null;
		try {
			Properties props = new Properties();
			// props.put(Context.INITIAL_CONTEXT_FACTORY,
			// "org.jboss.naming.remote.client.InitialContextFactory");

			// props.put(Context.PROVIDER_URL, "remote://localhost:8080");


			props.put("remote.connections", "default");
			props.put("remote.connection.default.port", "8080");
			props.put("remote.connection.default.host", "localhost");
			props.put("remote.connection.default.username", "de");
			props.put("remote.connection.default.password", "de");
			
//			props.setProperty(Context.PROVIDER_URL, "remote://localhost:8080/");
//			props.setProperty(Context.SECURITY_PRINCIPAL, "de");
//			props.setProperty(Context.SECURITY_CREDENTIALS, "de");
			
			props.put(
					"remote.connectionprovider.create.options.org.xnio.Options.SSL_ENABLED",
					"false");
			props.put(
					"remote.connection.default.connect.options.org.xnio.Options.SASL_POLICY_NOANONYMOUS",
					"false");
			
			props.put("remote.connection.default.connect.options.org.xnio.Options.SASL_DISALLOWED_MECHANISMS", "JBOSS-LOCAL-USER");

			props.put("jboss.naming.client.ejb.context", true);  
//			
			EJBClientConfiguration ejbClientConfiguration = new PropertiesBasedEJBClientConfiguration(
					props);
			ContextSelector<EJBClientContext> contextSelector = new ConfigBasedEJBClientContextSelector(
					ejbClientConfiguration);
			EJBClientContext.setSelector(contextSelector);

			Properties properties = new Properties();
			properties.put(Context.URL_PKG_PREFIXES,
					"org.jboss.ejb.client.naming");
			context = new InitialContext(properties);

			System.out.println("\n\tGot initial Context: "
					+ context.SECURITY_PRINCIPAL.toString());

			String lookUpString = "ejb:los.reference/project-ejb3/RefTopologyFacadeBean!de.linogistix.los.reference.facade.RefTopologyFacade";

			
			RefTopologyFacade topo = (RefTopologyFacade) context.lookup(lookUpString);
			context.close();
			topo.createDemoTopology();
		
		} catch (Exception e) {
			e.printStackTrace();
		}

		

	}

}
