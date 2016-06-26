/*
 * Copyright (c) 2006,2007 by Fraunhofer IML, Dortmund.
 * All rights reserved.
 *
 * Project: myWMS
 */
package org.mywms.mfc;

/**
 * This is a message switching the log level inside the
 * MaterialFlowControll.
 * 
 * @author Olaf Krause
 * @version $Revision: 442 $ provided by $Author: okrause $
 */
public class MFCLoglevel
    implements MFCMessage
{
    private static final long serialVersionUID = 1L;

    enum LogLevel {
        OFF, ERROR, WARN, INFO, DEBUG, ALL
    };

    public LogLevel logLevel;
}
