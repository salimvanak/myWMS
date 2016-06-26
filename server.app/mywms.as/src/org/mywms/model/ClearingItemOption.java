/*
 * Copyright (c) 2008 by Fraunhofer IML, Dortmund.
 * All rights reserved.
 *
 * Project: mywms.as
 */
package org.mywms.model;

import java.io.NotSerializableException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.mywms.util.BundleHelper;

/**
 * @author Olaf Krause
 * @version $Revision: 597 $ provided by $Author: trautm $
 */
public class ClearingItemOption
    implements Serializable
{
    private static final long serialVersionUID = 1L;

    /** A key for an i18n representation of the message */
    private String messageResourceKey;
    /** Parameters being used in the i18n representation. */
    private Object[] messageParameters;

    /** The options of which one will be the solution of the problem. */
    private List<ClearingItemOptionRetval> retvals =
        new ArrayList<ClearingItemOptionRetval>();

    /**
     * @return the message
     */
    public String getMessage(String bundleName, Class bundleResolver) {
        return BundleHelper.resolve(messageResourceKey, messageResourceKey, 
        		messageParameters, bundleName, bundleResolver, Locale.getDefault());
        
    }

    /**
     * @return the messageResourceKey
     */
    public String getMessageResourceKey() {
        return messageResourceKey;
    }

    /**
     * @param messageResourceKey the messageResourceKey to set
     */
    public void setMessageResourceKey(String messageResourceKey) {
        this.messageResourceKey = messageResourceKey;
    }

    /**
     * @return the messageParameters
     */
    public Object[] getMessageParameters() {
        return messageParameters;
    }

    /**
     * @param messageParameters the messageParameters to set
     */
    public void setMessageParameters(Object[] messageParameters)
        throws NotSerializableException
    {
        for (Object parameter: messageParameters) {
            if (!(parameter instanceof Serializable)) {
                throw new NotSerializableException();
            }
        }
        this.messageParameters = messageParameters;
    }

    /**
     * @return the retvals
     */
    public List<ClearingItemOptionRetval> getRetvals() {
        return retvals;
    }

    /**
     * @param retvals the retvals to set
     */
    public void setRetvals(List<ClearingItemOptionRetval> retvals) {
        this.retvals = retvals;
    }

    @Override
    public String toString() {
        StringBuffer str = new StringBuffer();

        if (getMessageResourceKey() != null)
            str.append(getMessageResourceKey());

        if (messageParameters != null) {
            for (Object o: getMessageParameters()) {
                str.append(o.toString());
            }
        }

        return str.toString();
    }
}
