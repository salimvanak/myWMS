/*
 * Copyright (c) 2008 by Fraunhofer IML, Dortmund.
 * All rights reserved.
 *
 * Project: mywms.as
 */
package org.mywms.facade;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import org.mywms.model.ClearingItem;
import org.mywms.model.ClearingItemOption;

/**
 * Transfers the data of a Clearing.
 * 
 * @author lfu
 * @version $Revision: 451 $ provided by $Author: lfu $
 */
public class ClearingItemTO
    extends BasicTO
{
    private static final long serialVersionUID = 1L;

    /** Holds value of property host */
    public String host;

    /** Holds value of property resourceBundleName message. */
    public String resourceBundleName;

    /** Holds value of property message. */
    public String message;
    /** Holds value of property messageResourceKey. */
    public String messageResourceKey;
    /** Parameters being used in the i18n representation. */
    public Object[] messageParameters;

    /** Holds value of property shortMessage. */
    public String shortMessage;
    /** Holds value of property shortMessageResourceKey. */
    public String shortMessageResourceKey;
    /** Parameters being used in the i18n representation. */
    public Object[] shortMessageParameters;

    /**
     * Holds value of property source. The source will be used for
     * dispatching the ClearingItem back to its originator.
     */
    public String source;

    /** Holds value of property user. */
    public String user;

    /** The user who solved the clearing */
    public String solver;

    public ClearingItemOption solution;

    public List<ClearingItemOption> options =
        new ArrayList<ClearingItemOption>();

    protected ClearingItemTO(ClearingItem clearingItem) {
        super(clearingItem);

        this.host = clearingItem.getHost();
        this.source = clearingItem.getSource();
        this.user = clearingItem.getUser();

        this.resourceBundleName = clearingItem.getResourceBundleName();

        this.shortMessage = clearingItem.getShortMessage();
        this.shortMessageResourceKey =
            clearingItem.getShortMessageResourceKey();
        this.shortMessageParameters = clearingItem.getShortMessageParameters();

        this.message = clearingItem.getMessage();
        this.messageResourceKey = clearingItem.getMessageResourceKey();
        this.messageParameters = clearingItem.getMessageParameters();

        this.options = clearingItem.getOptions();

        this.solution = clearingItem.getSolution();
        this.solver = clearingItem.getSolver();

    }

    protected void merge(ClearingItem clearingItem) throws VersionException {
        super.merge(clearingItem);
        clearingItem.setSolution(solver, solution);
    }

    /**
     * Returns the localized content of the message, if available.
     * 
     * @return the localized content
     */
    public String getLocalizedShortMessage() {
        return getLocalizedMessage(Locale.getDefault());
    }

    /**
     * Returns the localized content of the message, if available.
     * 
     * @return the localized content
     */
    public String getLocalizedShortMessage(Locale locale) {
        try {
            String localeMessage =
                ResourceBundle.getBundle(resourceBundleName, locale).getString(
                    shortMessageResourceKey);

            localeMessage =
                String.format(localeMessage, shortMessageParameters);

            return localeMessage;
        }
        catch (Exception ex) {
            return message;
        }
    }

    /**
     * Returns the localized content of the message, if available.
     * 
     * @return the localized content
     */
    public String getLocalizedMessage() {
        return getLocalizedMessage(Locale.getDefault());
    }

    /**
     * Returns the localized content of the message, if available.
     * 
     * @return the localized content
     */
    public String getLocalizedMessage(Locale locale) {
        try {
            String localeMessage =
                ResourceBundle.getBundle(resourceBundleName, locale).getString(
                    messageResourceKey);

            localeMessage = String.format(localeMessage, messageParameters);

            return localeMessage;
        }
        catch (Exception ex) {
            return message;
        }
    }

}
