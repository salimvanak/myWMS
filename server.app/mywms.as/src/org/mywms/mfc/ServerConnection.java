/*
 * Copyright (c) 2006,2007 by Fraunhofer IML, Dortmund.
 * All rights reserved.
 *
 * Project: myWMS
 */
package org.mywms.mfc;

import java.util.List;

import javax.jms.ExceptionListener;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.jms.QueueConnection;
import javax.jms.QueueConnectionFactory;
import javax.jms.QueueReceiver;
import javax.jms.QueueSender;
import javax.jms.QueueSession;
import javax.jms.Session;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.log4j.Logger;

/**
 * @author Olaf Krause
 * @version $Revision: 485 $ provided by $Author: okrause $
 */
public class ServerConnection
    implements MessageListener, ExceptionListener
{
    private static final Logger log =
        Logger.getLogger(ServerConnection.class.getName());

    public static final String INPUT_QUEUE_DEFAULT_NAME = "myWMS2mfcControll";
    public static final String OUTPUT_QUEUE_DEFAULT_NAME = "mfcControll2myWMS";

    private final String INPUT_QUEUE_NAME;
    private final String OUTPUT_QUEUE_NAME;

    private InitialContext context;
    private QueueConnection connection;
    private QueueSession session;
    private List<ASDispatcher> asDispatchers;

    /**
     * Creates a new server connection.
     * 
     * @param asDispatchers the dispatchers to forward the incoming
     *            messages to
     * @throws NamingException if the jndi name could not be looked up
     * @throws JMSException if the jms communication fails
     */
    public ServerConnection(List<ASDispatcher> asDispatchers)
        throws NamingException,
            JMSException
    {
        this(asDispatchers, INPUT_QUEUE_DEFAULT_NAME, OUTPUT_QUEUE_DEFAULT_NAME);
    }

    /**
     * Creates a new server connection.
     * 
     * @param asDispatchers the dispatchers to forward the incoming
     *            messages to
     * @param inputQueueName the jndi name of the input queue to connect
     *            to
     * @param outputQueueName the jndi name of the output queue to
     *            connect to
     * @throws NamingException if the jndi name could not be looked up
     * @throws JMSException if the jms communication fails
     */
    public ServerConnection(
        List<ASDispatcher> asDispatchers,
        String inputQueueName,
        String outputQueueName) throws NamingException, JMSException
    {
        this.asDispatchers = asDispatchers;
        INPUT_QUEUE_NAME = inputQueueName;
        OUTPUT_QUEUE_NAME = outputQueueName;

        context = new InitialContext();
        Object tmp = context.lookup("ConnectionFactory");
        QueueConnectionFactory qcf = (QueueConnectionFactory) tmp;
        connection = qcf.createQueueConnection();
        session =
            connection.createQueueSession(false, QueueSession.AUTO_ACKNOWLEDGE);
        connection.setExceptionListener(this);
        connection.start();

        // setthe link back to the dispatchers
        for (ASDispatcher asDispatcher: asDispatchers) {
            asDispatcher.setServerConnection(this);
        }

        // log the startup
        MFCLog logItem =
            new MFCLog("MFCControll", "application startup", "MFCControll", "");
        send(logItem);

        // set the asyncronous listener
        Queue inputQueue = (Queue) context.lookup("queue/" + INPUT_QUEUE_NAME);
        QueueReceiver receiver = session.createReceiver(inputQueue);
        receiver.setMessageListener(this);
        log.info("bound to queue/" + INPUT_QUEUE_NAME);
    }

    /**
     * Sends a message to the myWMS application inside the application
     * server.
     * 
     * @param mfcMessage the message to be send
     * @throws JMSException
     * @throws NamingException
     */
    public void send(MFCMessage mfcMessage)
        throws JMSException,
            NamingException
    {

        // create the jms session
        QueueSession session =
            connection.createQueueSession(false, Session.AUTO_ACKNOWLEDGE);

        // lookup of the jms queue over jndi
        Queue outputQueue =
            (Queue) context.lookup("queue/" + OUTPUT_QUEUE_NAME);

        // create the message publisher
        QueueSender sender = session.createSender(outputQueue);

        ObjectMessage objectMessage = session.createObjectMessage();
        objectMessage.setObject(mfcMessage);
        objectMessage.setJMSTimestamp(System.currentTimeMillis());

        sender.send(objectMessage);
        sender.close();

        session.close();
    }

    /**
     * This is the main event handler of the server connection.
     * 
     * @throws JMSException
     */
    public void onMessage(Message jmsMessage) {
        MFCMessage message;
        try {
            if (!(jmsMessage instanceof ObjectMessage)) {
                log.info("received a non object message from peer - skipping");
                log.info(jmsMessage.toString());
                return;
            }
            if (!(((ObjectMessage) jmsMessage).getObject() instanceof MFCMessage))
            {
                log.info("received a non MFC message from peer - skipping");
                log.info(jmsMessage.toString());
                return;
            }

            message = (MFCMessage) ((ObjectMessage) jmsMessage).getObject();
            log.info("received a MFCMessage:");
            log.info("" + message);
        }
        catch (JMSException ex) {
            log.error("unhandled exception in handling a jms message:" + ex);
            log.error("skipping message");
            log.error(jmsMessage.toString());
            log.error(ex, ex);
            return;
        }

        // dispatch message
        try {
            // -------------------------------------------------------
            // loglevel
            // -------------------------------------------------------
            if (message instanceof MFCLoglevel) {
                // TODO: switch the log4j log level
            }
            // -------------------------------------------------------
            // ping
            // -------------------------------------------------------
            else if (message instanceof MFCPing) {
                MFCPing ping = (MFCPing) message;
                // touch
                ping.responderTouch();
                // respond
                send(message);
            }
            // //
            // -------------------------------------------------------
            // // move request
            // //
            // -------------------------------------------------------
            // else if(message instanceof MFCMoveRequest) {
            // @SuppressWarnings("unused")
            // MFCMoveRequest moveRequest = (MFCMoveRequest)message;
            // // TODO: forward to ControllerConnection
            // }
            // -------------------------------------------------------
            // log
            // -------------------------------------------------------
            else if (message instanceof MFCLog) {
                log.info("dispatching log messages is not implemented for the mfc part - skipping:");
                log.info("" + message);
            }
            // -------------------------------------------------------
            // unknown - forward to dispatcher
            // -------------------------------------------------------
            else {
                for (ASDispatcher dispatcher: asDispatchers) {
                    try {
                        log.debug("dispatcher "
                            + dispatcher.getClass().getName()
                            + ".evaluate() calling");
                        dispatcher.evaluate(message);
                        log.debug("dispatcher "
                            + dispatcher.getClass().getName()
                            + ".evaluate() returned");
                    }
                    catch (Exception e) {
                        log.error("call to dispatcher "
                            + dispatcher.toString()
                            + " failed with message: \n"
                            + message.toString()
                            + "\n");
                        log.error("this is what I know about it:");
                        log.error(e, e);
                    }
                }
            }
        }
        catch (JMSException ex) {
            log.error("jms communication failed");
            log.error("this is what I know about it:\n" + ex);
            log.error(ex, ex);
        }
        catch (NamingException ex) {
            log.error("lookup of the jms service failed");
            log.error("this is what I know about it:\n" + ex);
            log.error(ex, ex);
        }
    }

    /**
     * @see javax.jms.ExceptionListener#onException(javax.jms.JMSException)
     */
    public void onException(JMSException arg0) {
        QueueConnection oldConnection = connection;
        QueueConnection newConnection;
        log.info("try to recover from exception ("
            + Thread.currentThread().getName()
            + ")...");

        // try to reconnect
        try {
            context = new InitialContext();
            Object tmp = context.lookup("ConnectionFactory");
            QueueConnectionFactory qcf = (QueueConnectionFactory) tmp;
            newConnection = qcf.createQueueConnection();
            session =
                newConnection.createQueueSession(
                    false,
                    QueueSession.AUTO_ACKNOWLEDGE);
            newConnection.setExceptionListener(this);
            newConnection.start();

            // set the asyncronous listener
            Queue inputQueue =
                (Queue) context.lookup("queue/" + INPUT_QUEUE_NAME);
            QueueReceiver receiver = session.createReceiver(inputQueue);
            receiver.setMessageListener(this);

            // switch to new connection
            connection = newConnection;
        }
        catch (NamingException e) {
            log.error("...failed: " + e.getLocalizedMessage());
            return;
        }
        catch (JMSException e) {
            log.error("...failed: " + e.getLocalizedMessage());
            return;
        }

        // clear old connection
        if (oldConnection != null) {
            try {
                oldConnection.stop();
            }
            catch (JMSException e) {
                log.error("...stopping old connection failed - but don't worry");
            }
            try {
                oldConnection.close();
            }
            catch (JMSException e) {
                log.error("...closing old connection failed - but don't worry");
            }
            oldConnection = newConnection;
        }
        log.info("...succeed");
    }
}
