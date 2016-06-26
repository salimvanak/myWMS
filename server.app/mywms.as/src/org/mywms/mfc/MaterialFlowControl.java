/*
 * Copyright (c) 2006,2007 by Fraunhofer IML, Dortmund.
 * All rights reserved.
 *
 * Project: myWMS
 */
package org.mywms.mfc;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import javax.jms.JMSException;
import javax.naming.NamingException;

import org.apache.log4j.Logger;

/**
 * This class implements a basic material flow control process. It
 * connects to a JMS Queue, reads requests and forwards the requests to
 * the controllers. Responses are read from the controllers and are
 * forewardet to the queue. Some preparations to execute this software
 * extend the file <code>java.policy</code>:
 * 
 * <pre>
 *  grant {
 *  ...
 *  // eigene Erweiterungen
 *  permission java.net.SocketPermission &quot;127.0.0.1:1099&quot;, &quot;connect,resolve&quot;;
 *  permission java.net.SocketPermission &quot;localhost:1099&quot;, &quot;connect, resolve&quot;;
 *  permission java.net.SocketPermission &quot;servername:1099&quot;, &quot;connect, resolve&quot;;
 *  }
 * </pre>
 * 
 * add the following lines to the <code>server</code> section of the
 * file <code>jbossmq-destinations-service.xml</code>:
 * 
 * <pre>
 *  &lt;mbean code=&quot;org.jboss.mq.server.jmx.Queue&quot;
 *  name=&quot;jboss.mq.destination:service=Queue,name=myWMS2mfcControll&quot;&gt;
 *  &lt;depends optional-attribute-name=&quot;DestinationManager&quot;&gt;jboss.mq:service=DestinationManager&lt;/depends&gt;
 *  &lt;/mbean&gt;
 *  &lt;mbean code=&quot;org.jboss.mq.server.jmx.Queue&quot;
 *  name=&quot;jboss.mq.destination:service=Queue,name=mfcControll2myWMS&quot;&gt;
 *  &lt;depends optional-attribute-name=&quot;DestinationManager&quot;&gt;jboss.mq:service=DestinationManager&lt;/depends&gt;
 *  &lt;/mbean&gt;
 * </pre>
 * 
 * The other side of the queue is a myWMS application, residing in the
 * application server.
 * 
 * @author Olaf Krause
 * @version $Revision: 491 $ provided by $Author: lxjordan $
 */
public class MaterialFlowControl {
    private static final Logger log =
        Logger.getLogger(MaterialFlowControl.class.getName());

    private static final String VERSION = "$Revision: 491 $";

    private static final String CONFIG_FILE_NAME = "/org/mywms/res/MFCConfig";

    private static final int ARG_HELP = 0;

    private static final int ARG_STOP_ARGS = 1;

    private static final int ARG_SHOW_CONFIG = 2;

    private static final int ARG_SHOW_CONFIG_HELP = 3;

    private static final int ARG_INPUT_QUEUE = 4;

    private static final int ARG_OUTPUT_QUEUE = 5;

    private static final String[] SHORT_ARGS = {
        "-h", "--", "-sc", "-hc", "-iq", "-oq"
    };

    private static final String[] LONG_ARGS =
        {
            "--help",
            "---",
            "--show-config",
            "--help-config",
            "--input-queue",
            "--output-queue"
        };

    private static final String[] ARGS_HELP =
        {
            "shows this help text and terminates the application instandly",
            "stops execution of command line parameters",
            "lists the config parameters of the external config file",
            "shows a list of parameters usable in the config file",
            "the jndi name of the input queue to use; overrides configuration",
            "the jndi name of the output queue to use; overrides configuration"
        };

    private static ServerConnection serverConnection;

    private static ResourceBundle config;

    private static List<ASDispatcher> asDispatchers;

    @SuppressWarnings("unused")
    private static List<MFCDispatcher> mfcDispatchers;

    static final String CONFIG_MFC_DISPATCHER_COUNT = "mfcDispatcherCount";

    static final String CONFIG_MFC_DISPATCHER_PREFIX = "mfcDispatcher";

    static final String CONFIG_AS_DISPATCHER_COUNT = "asDispatcherCount";

    static final String CONFIG_AS_DISPATCHER_PREFIX = "asDispatcher";

    static final String CONFIG_OUTPUT_QUEUE_NAME = "outputQueueName";

    static final String CONFIG_INPUT_QUEUE_NAME = "inputQueueName";

    static String inputQueue = null; // ServerConnection.INPUT_QUEUE_DEFAULT_NAME;

    static String outputQueue = null; // ServerConnection.OUTPUT_QUEUE_DEFAULT_NAME;

    /**
     * Creates a new instance of the material flow control.
     * 
     * @throws JMSException if the jms communication fails
     * @throws NamingException if the lookup of the queue fails
     */
    private MaterialFlowControl() throws NamingException, JMSException {
        super();
        asDispatchers = createASDispatchers();
        mfcDispatchers = createMFCDispatchers();
        MaterialFlowControl.serverConnection =
            new ServerConnection(
                asDispatchers,
                getInputQueueName(),
                getOutputQueueName());
    }

    /**
     * Reads the name of the default output queue name from the
     * configuration.
     * 
     * @return the output queue name
     */
    private String getOutputQueueName() {
        // check, if a parameter has been set
        if (inputQueue != null) {
            return inputQueue;
        }

        // check, if a configuration entry has been set
        try {
            inputQueue = config.getString(CONFIG_OUTPUT_QUEUE_NAME).trim();
        }
        catch (MissingResourceException ex) {
            inputQueue = ServerConnection.INPUT_QUEUE_DEFAULT_NAME;
        }

        return inputQueue;
    }

    /**
     * Reads the name of the default output queue name from the
     * configuration.
     * 
     * @return the output queue name
     */
    private String getInputQueueName() {
        // check, if a parameter has been set
        if (outputQueue != null) {
            return outputQueue;
        }

        // check, if a configuration entry has been set
        try {
            outputQueue = config.getString(CONFIG_INPUT_QUEUE_NAME).trim();
        }
        catch (MissingResourceException ex) {
            outputQueue = ServerConnection.OUTPUT_QUEUE_DEFAULT_NAME;
        }

        return outputQueue;
    }

    /**
     * Analyses the configurations and returns a list of instantiated
     * dispatchers
     * 
     * @return a list of instantiated dispatchers
     */
    private List<ASDispatcher> createASDispatchers() {
        int n =
            Integer.parseInt(config.getString(CONFIG_AS_DISPATCHER_COUNT)
                .trim());
        List<ASDispatcher> dispatchers = new ArrayList<ASDispatcher>();
        for (int i = 0; i < n; i++) {
            String classname =
                config.getString(CONFIG_AS_DISPATCHER_PREFIX + i).trim();
            try {
                ASDispatcher dispatcher =
                    (ASDispatcher) Class.forName(classname).newInstance();
                dispatcher.setConfiguration(config);
                dispatchers.add(dispatcher);
            }
            catch (Exception e) {
                log.error("could not instantiate dispatcher '"
                    + classname
                    + "'");
                log.error("this is what I know about it:");
                log.error(e);
            }
        }
        return dispatchers;
    }

    /**
     * Analyses the configurations and returns a list of instantiated
     * dispatchers
     * 
     * @return a list of instantiated dispatchers
     */
    private List<MFCDispatcher> createMFCDispatchers() {
        int n =
            Integer.parseInt(config.getString(CONFIG_MFC_DISPATCHER_COUNT)
                .trim());
        List<MFCDispatcher> dispatchers = new ArrayList<MFCDispatcher>();
        for (int i = 0; i < n; i++) {
            String classname =
                config.getString(CONFIG_MFC_DISPATCHER_PREFIX + i).trim();
            try {
                MFCDispatcher dispatcher =
                    (MFCDispatcher) Class.forName(classname).newInstance();
                dispatcher.setConfiguration(config);
                dispatchers.add(dispatcher);
            }
            catch (Exception e) {
                log.error("could not instantiate dispatcher '"
                    + classname
                    + "'");
                log.error("this is what I know about it:");
                log.error(e);
            }
        }
        return dispatchers;
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        log.info("starting...");
        log.info("" + MaterialFlowControl.VERSION);

        // load the external config file
        config = ResourceBundle.getBundle(CONFIG_FILE_NAME);

        int i, n;
        n = args.length;
        for (i = 0; i < n; i++) {
            String arg = args[i];
            String nextArg;
            if (i + 1 < n) {
                nextArg = args[i + 1];
            }
            else {
                nextArg = null;
            }

            if (arg.equals(SHORT_ARGS[ARG_HELP])
                || arg.equals(LONG_ARGS[ARG_HELP]))
            {
                log.info("usage:");
                for (int j = 0; j < ARGS_HELP.length; j++) {
                    log.info("" + SHORT_ARGS[j] + " " + LONG_ARGS[j]);
                    log.info("   " + ARGS_HELP[j]);
                }
                return;
            }
            else if (arg.equals(SHORT_ARGS[ARG_STOP_ARGS])
                || arg.equals(LONG_ARGS[ARG_STOP_ARGS]))
            {
                break;
            }
            else if (arg.equals(SHORT_ARGS[ARG_SHOW_CONFIG])
                || arg.equals(LONG_ARGS[ARG_SHOW_CONFIG]))
            {
                Enumeration<String> keys = config.getKeys();
                while (keys.hasMoreElements()) {
                    String key = keys.nextElement();
                    log.info("" + key + "=" + config.getString(key).trim());
                }
            }
            else if (arg.equals(SHORT_ARGS[ARG_SHOW_CONFIG_HELP])
                || arg.equals(LONG_ARGS[ARG_SHOW_CONFIG_HELP]))
            {
                log.info("-------------------------------------");
                log.info("Config File Parameters:");
                log.info("-------------------------------------");
                log.info("# the number of following MFC dispatcher modules");
                log.info(CONFIG_MFC_DISPATCHER_COUNT + "=<integer>");
                log.info("# occurs according to the number specified in "
                    + CONFIG_MFC_DISPATCHER_COUNT);
                log.info("# replace X with the appropriate number, beginning with 0");
                log.info(CONFIG_MFC_DISPATCHER_PREFIX + "X=<full class name>");
                log.info("# the number of following application server dispatcher modules");
                log.info(CONFIG_AS_DISPATCHER_COUNT + "=<integer>");
                log.info("# occurs according to the number specified in "
                    + CONFIG_AS_DISPATCHER_COUNT);
                log.info("# replace X with the appropriate number, beginning with 0");
                log.info(CONFIG_AS_DISPATCHER_PREFIX + "X=<full class name>");
                log.info("# name of the input queue, containing the incomming messsages from the application server");
                log.info(CONFIG_INPUT_QUEUE_NAME + "=<jndi queue name>");
                log.info("# name of the output queue, transfering the outgoing messsages for the application server");
                log.info(CONFIG_OUTPUT_QUEUE_NAME + "=<jndi queue name>");
                log.info(" ");
                log.info("-------------------------------------");
                log.info("Proposal for Default Parameters:");
                log.info("-------------------------------------");
                log.info(CONFIG_MFC_DISPATCHER_COUNT + "=1");
                log.info(CONFIG_MFC_DISPATCHER_PREFIX
                    + "0="
                    + MFCDefaultDispatcher.class.getName());
                log.info(CONFIG_AS_DISPATCHER_COUNT + "=1");
                log.info(CONFIG_AS_DISPATCHER_PREFIX
                    + "0="
                    + ASDefaultDispatcher.class.getName());
                log.info(CONFIG_INPUT_QUEUE_NAME
                    + "="
                    + ServerConnection.INPUT_QUEUE_DEFAULT_NAME);
                log.info(CONFIG_OUTPUT_QUEUE_NAME
                    + "="
                    + ServerConnection.OUTPUT_QUEUE_DEFAULT_NAME);
                log.info(" ");
                log.info("-------------------------------------");
            }
            else if (arg.equals(SHORT_ARGS[ARG_INPUT_QUEUE])
                || arg.equals(LONG_ARGS[ARG_INPUT_QUEUE]))
            {
                if (arg == null) {
                    log.info("name of input queue is missing");
                    continue;
                }
                inputQueue = nextArg;
                i++;
            }
            else if (arg.equals(SHORT_ARGS[ARG_OUTPUT_QUEUE])
                || arg.equals(LONG_ARGS[ARG_OUTPUT_QUEUE]))
            {
                if (arg == null) {
                    log.info("name of output queue is missing");
                    continue;
                }
                outputQueue = nextArg;
                i++;
            }
        }

        try {
            new MaterialFlowControl();
        }
        catch (NamingException ex) {
            log.error("failed to connect to myWMS application");
            log.error("this is what I know about it:\n" + ex);
            log.error(ex);
        }
        catch (JMSException ex) {
            log.error("failed to communicate with myWMS application");
            log.error("this is what I know about it:\n" + ex);
            log.error(ex);
        }

        log.info("startup finished (I'am on service now!)");
    }

    /**
     * @return the serverConnection
     */
    public static ServerConnection getServerConnection() {
        return serverConnection;
    }
}
