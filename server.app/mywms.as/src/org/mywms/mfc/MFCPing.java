/*
 * Copyright (c) 2006,2007 by Fraunhofer IML, Dortmund.
 * All rights reserved.
 *
 * Project: myWMS
 */
package org.mywms.mfc;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * A ping is send to the MaterialFlowControl on a regular basis. It will
 * be used to measure the turnaround time of the JMS communication and
 * so the overall performance of the material flow communication.
 * 
 * @author Olaf Krause
 * @version $Revision: 442 $ provided by $Author: okrause $
 */
public class MFCPing
    implements MFCMessage
{
    private static final long serialVersionUID = 1L;

    private long timestampSender = System.currentTimeMillis();
    private long timestampResponder = timestampSender - 1;
    private long timestampReceiver = timestampSender - 1;
    private String hostnameResponder = "<undefined>";

    /**
     * Creates a new MFCPing instance.
     */
    public MFCPing() {
    }

    /**
     * Called by the responder to set the responder timestamp and its
     * hostname.
     */
    public void responderTouch() {
        timestampResponder = System.currentTimeMillis();
        try {
            this.hostnameResponder =
                InetAddress.getLocalHost().getCanonicalHostName();
        }
        catch (UnknownHostException ex) {
            this.hostnameResponder = "<undefined>";
        }
    }

    /**
     * Called by the receiver to set the receiver timestamp.
     */
    public void receiverTouch() {
        timestampReceiver = System.currentTimeMillis();
    }

    /**
     * Returns the time the ping travelled in milliseconds.
     * 
     * @return the time the ping travelled
     */
    public long getTravelTime() {
        return timestampReceiver - timestampSender;
    }

    /**
     * @return Returns the timestampReceiver.
     */
    public long getTimestampReceiver() {
        return this.timestampReceiver;
    }

    /**
     * @return Returns the timestampResponder.
     */
    public long getTimestampResponder() {
        return this.timestampResponder;
    }

    /**
     * @return Returns the timestampSender.
     */
    public long getTimestampSender() {
        return this.timestampSender;
    }

    /**
     * @return Returns the hostnameResponder.
     */
    public String getHostnameResponder() {
        return this.hostnameResponder;
    }

    public String toString() {
        StringBuffer strb = new StringBuffer();

        strb.append("Ping travelTime:")
            .append(getTravelTime())
            .append(" ms (send/respond/received: ")
            .append(getTimestampSender())
            .append("/")
            .append((getTimestampResponder() - getTimestampSender()))
            .append("/")
            .append(
                getTimestampReceiver() == getTimestampSender() - 1
                    ? -1
                    : (getTimestampReceiver() - getTimestampResponder()))
            .append(")");

        return strb.toString();
    }
}
