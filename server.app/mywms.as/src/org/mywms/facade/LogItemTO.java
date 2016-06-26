/*
 * Copyright (c) 2006 by Fraunhofer IML, Dortmund.
 * All rights reserved.
 *
 * Project: myWMS
 */
package org.mywms.facade;

import org.mywms.globals.LogItemType;
import org.mywms.model.LogItem;

/**
 * Transfers the data of a LogItem.
 * 
 * @author Olaf Krause
 * @version $Revision: 442 $ provided by $Author: okrause $
 */
public class LogItemTO
    extends BasicTO
{
    private static final long serialVersionUID = 1L;

    /** Holds value of property host. */
    public String host;
    /** Holds value of property message. */
    public String message;
    /** Holds value of property messageResourceKey. */
    public String messageResourceKey;
    /** Holds value of property source. */
    public String source;
    /** Holds value of property osuser. */
    public String user;
    /** Holds value of property type. */
    public LogItemType type;

    /**
     * Creates a new LogItemTO, using the data of the given LogItem.
     * 
     * @param logItem the source of the data to transfer
     */
    public LogItemTO(LogItem logItem) {
        super(logItem);
        this.host = logItem.getHost();
        this.message = logItem.getMessage();
        this.messageResourceKey = logItem.getMessageResourceKey();
        this.source = logItem.getSource();
        this.user = logItem.getUser();
        this.type = logItem.getType();
    }
}
