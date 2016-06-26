/*
 * Copyright (c) 2006,2007 by Fraunhofer IML, Dortmund.
 * All rights reserved.
 *
 * Project: myWMS
 */
package org.mywms.globals;

import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import junit.framework.TestCase;

import org.mywms.facade.FacadeException;

/**
 * Tests the existence of DefaultMessages
 * 
 * @author Olaf Krause
 * @version $Revision: 446 $ provided by $Author: okrause $
 */
public class DefaultMessageTest
    extends TestCase
{
    public static final String resourceBundleName =
        FacadeException.DEFAULT_RESOURCE_BUNDLE_NAME;

    public static final String JUNIT_HOOK = "JUnitHook";

    public void testLog() throws Exception {
        Locale[] locales = {
            null, Locale.ENGLISH, Locale.GERMAN
        };
        boolean failure = false;

        System.out.println("# requesting resource '"
            + resourceBundleName
            + "'.");

        for (Locale locale: locales) {
            System.out.println("# ------------------------------");
            ResourceBundle bundle;
            if (locale != null) {
                bundle = ResourceBundle.getBundle(resourceBundleName, locale);

            }
            else {
                bundle = ResourceBundle.getBundle(resourceBundleName);
            }

            if (locale != null
                && !bundle.getString(JUNIT_HOOK).equals("" + locale))
            {
                failure = true;
                System.out.println("# ERROR: bundle for: '"
                    + locale
                    + "' is incorrect with '"
                    + bundle.getString(JUNIT_HOOK)
                    + "'");

            }

            System.out.println("# bundle for: " + locale);

            for (DefaultMessage key: DefaultMessage.values()) {
                try {
                    String message;

                    message = bundle.getString(key.toString());
                    System.out.println("# " + key.toString() + "=" + message);
                }
                catch (MissingResourceException ex) {
                    failure = true;
                    System.out.println("# ERROR: resource for key '"
                        + key.toString()
                        + "' in locale '"
                        + locale
                        + "' is missing");
                }
            }
            for (ServiceExceptionKey key: ServiceExceptionKey.values()) {
                try {
                    String message;

                    message = bundle.getString(key.toString());
                    System.out.println("# " + key.toString() + "=" + message);
                }
                catch (MissingResourceException ex) {
                    failure = true;
                    System.out.println("# ERROR: resource for key '"
                        + key.toString()
                        + "' in locale '"
                        + locale
                        + "' is missing");
                }
            }
        }
        if (failure) {
            fail("one or more resource keys cannot be resolved");
        }
    }
}
