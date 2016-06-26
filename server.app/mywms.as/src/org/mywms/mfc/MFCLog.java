/*
 * Copyright (c) 2006,2007 by Fraunhofer IML, Dortmund.
 * All rights reserved.
 *
 * Project: myWMS
 */
package org.mywms.mfc;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Date;

/**
 * This message logs a text to the myWMS system. It is used by the
 * remote mfc controll to log states and operations.
 * 
 * @author Olaf Krause
 * @version $Revision: 442 $ provided by $Author: okrause $
 */
public class MFCLog
    implements MFCMessage
{
    private static final long serialVersionUID = 1L;

    private static final String HOSTNAME;

    /** Set the hostname value once. */
    static {
        String hName;
        try {
            hName = InetAddress.getLocalHost().getHostName();
        }
        catch (UnknownHostException ex) {
            hName = "<undefined>";
        }
        HOSTNAME = hName;
    }

    private String hostname;
    private String source;
    private String logMessage;
    private String user;
    private String ressourceKey;
    private long created = System.currentTimeMillis();

    /**
     * Creates a new MFCLog instance.
     * 
     * @param source the source method/function of the log message
     * @param logMessage the logmessage itself
     * @param user the user created the log message
     * @param ressourceKey the ressource key for I18N of the message
     * @throws NullPointerException if one of the parameters is null
     */
    public MFCLog(
        String source,
        String logMessage,
        String user,
        String ressourceKey) throws NullPointerException
    {
        this(MFCLog.HOSTNAME, source, logMessage, user, ressourceKey);
    }

    /**
     * Creates a new MFCLog instance.
     * 
     * @param hostname the hostname where the logged event occured
     * @param source the source method/function of the log message
     * @param logMessage the logmessage itself
     * @param user the user created the log message
     * @param ressourceKey the ressource key for I18N of the message
     * @throws NullPointerException if one of the parameters is null
     */
    public MFCLog(
        String hostname,
        String source,
        String logMessage,
        String user,
        String ressourceKey) throws NullPointerException
    {
        if (hostname == null
            || source == null
            || logMessage == null
            || user == null
            || ressourceKey == null)
        {
            throw new NullPointerException();
        }

        this.hostname = hostname;
        this.source = source;
        this.logMessage = logMessage;
        this.user = user;
        this.ressourceKey = ressourceKey;
    }

    /**
     * @return Returns the hostname.
     */
    public String getHostname() {
        return this.hostname;
    }

    /**
     * @return Returns the logMessage.
     */
    public String getLogMessage() {
        return this.logMessage;
    }

    /**
     * @return Returns the ressourceKey.
     */
    public String getRessourceKey() {
        return this.ressourceKey;
    }

    /**
     * @return Returns the source.
     */
    public String getSource() {
        return this.source;
    }

    /**
     * @return Returns the user.
     */
    public String getUser() {
        return this.user;
    }

    /**
     * Returns a String representation of the MFCLog instance
     * 
     * @see java.lang.Object#toString()
     */
    public String toString() {
        StringBuffer strb = new StringBuffer();

        strb.append(new Date(created).toString())
            .append(" ")
            .append(user)
            .append("@")
            .append(hostname)
            .append(" [")
            .append(source)
            .append("]: ")
            .append(logMessage);

        return strb.toString();
    }

    /**
     * @return the created
     */
    public long getCreated() {
        return this.created;
    }
}
