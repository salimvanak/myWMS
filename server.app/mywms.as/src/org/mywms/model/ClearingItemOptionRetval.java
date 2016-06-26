/*
 * Copyright (c) 2008 by Fraunhofer IML, Dortmund.
 * All rights reserved.
 *
 * Project: mywms.as
 */
package org.mywms.model;

import java.io.NotSerializableException;
import java.io.Serializable;
import java.util.Locale;

import javax.persistence.Column;

import org.mywms.util.BundleHelper;

/**
 * @author Olaf Krause
 * @version $Revision: 597 $ provided by $Author: trautm $
 */
public class ClearingItemOptionRetval
    implements Serializable
{
    private static final long serialVersionUID = 1L;

    /** A key to be used for an i18n representation inside the GUI */
    String nameResourceKey;

    /** The type of the retval */
    @SuppressWarnings("unchecked")
    private Class type;

    /** The retval, the user has specified. */
    private Object retval;

    /**
     * Will be resolved via nameResourceKey!
     * @return the name
     */
    public String getName(String bundleName, Class bundleResolver) {
        return BundleHelper.resolve(getNameResourceKey(), getNameResourceKey(),
        		new String[0], bundleName, bundleResolver, Locale.getDefault());
    }

    /**
     * @return the nameResourceKey
     */
    public String getNameResourceKey() {
        return nameResourceKey;
    }

    /**
     * @param nameResourceKey the nameResourceKey to set
     */
    public void setNameResourceKey(String nameResourceKey) {
        this.nameResourceKey = nameResourceKey;
    }

    /**
     * @return the type
     */
    @SuppressWarnings("unchecked")
    public Class getType() {
        return type;
    }

    /**
     * @param type the type to set
     */
    @SuppressWarnings("unchecked")
    public void setType(Class type) {
        this.type = type;
    }

    /**
     * @return the retval
     */
    @Column(columnDefinition = "BLOB")
    public Object getRetval() {
        return retval;
    }

    /**
     * @param retval the retval to set
     * @throws NotSerializableException
     */
    public void setRetval(Object retval) throws NotSerializableException {
        if (!(retval instanceof Serializable)) {
            throw new NotSerializableException();
        }

        this.retval = retval;
    }

    @Override
    public String toString() {
        return "NameResourceKey: "
                + getNameResourceKey()
                + ", Retval: "
                + getRetval().toString()
                + ", Class: "
                + getType().toString();
    }
}
