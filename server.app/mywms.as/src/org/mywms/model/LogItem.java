/*
 * Copyright (c) 2006 by Fraunhofer IML, Dortmund.
 * All rights reserved.
 *
 * Project: myWMS
 */
package org.mywms.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;

import org.mywms.globals.LogItemType;

/**
 * Contains operational event informations logged by the system.
 * 
 * @author Olaf Krause
 * @version $Revision: 681 $ provided by $Author: mkrane $
 */
@Entity
@Table(name = "mywms_logitem")
public class LogItem
    extends BasicClientAssignedEntity
{
    private static final long serialVersionUID = 1L;
    /** Stores the name of the localhost right from the start. */
    private static final String LOCALHOST_NAME = getLocalhostName();

    public static final String DEFAULT_RESOURCE_BUNDLE_NAME =
        "/org/mywms/res/mywms-messages";

    /** Holds value of property host. */
    private String host = LOCALHOST_NAME;
    /** Holds value of property message. */

    /** The name of the resource bundle to use. */
    private String resourceBundleName = DEFAULT_RESOURCE_BUNDLE_NAME;

    private String message;
    /** Holds value of property messageResourceKey. */
    private String messageResourceKey;
    /** Parameters being used in the i18n representation. */
    private Object[] messageParameters;
    
    /** Holds value of property source. */
    private String source;
    /** Holds value of property user. */
    private String user = System.getProperties().getProperty("user.name");
    /** Holds value of property type. */
    private LogItemType type = LogItemType.LOG;

    @SuppressWarnings("unchecked")
	private Class bundleResolver = org.mywms.res.BundleResolver.class;
    
    /**
     * Getter for property host.
     * 
     * @return Value of property host.
     */
    @Column(updatable = false, nullable = false)
    public String getHost() {

        return this.host;
    }

    /**
     * Setter for property host.
     * 
     * @param host New value of property host.
     */
    public void setHost(String host) {

        this.host = host;
    }

    /**
     * Getter for property message.
     * 
     * @return Value of property message.
     */
    @Column(updatable = false, nullable = false)
    public String getMessage() {
        return this.message;
    }

    /**
     * Setter for property message.
     * 
     * @param message New value of property message.
     */
    public void setMessage(String message) {

        this.message = message;
    }

    /**
     * Getter for property source.
     * 
     * @return Value of property source.
     */
    @Column(updatable = false, nullable = false)
    public String getSource() {

        return this.source;
    }

    /**
     * Setter for property source.
     * 
     * @param source New value of property source.
     */
    public void setSource(String source) {

        this.source = source;
    }

    /**
     * Getter for property user.
     * 
     * @return Value of property user.
     */
    @Column(updatable = false, name = "user_", nullable = false)
    public String getUser() {

        return this.user;
    }

    /**
     * Setter for property user.
     * 
     * @param osuser New value of property user.
     */
    public void setUser(String osuser) {

        this.user = osuser;
    }

    // -----------------------------------------------------------------------
    // private services
    // -----------------------------------------------------------------------

    private static String getLocalhostName() {
        try {
            return java.net.InetAddress.getLocalHost().toString();
        }
        catch (Exception ex) {
            return "unknown host";
        }
    }

    // -----------------------------------------------------------------------

    /**
     * Getter for property type.
     * 
     * @return Value of property type.
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    public LogItemType getType() {

        return this.type;
    }

    /**
     * Setter for property type.
     * 
     * @param type New value of property type.
     */
    public void setType(LogItemType type) {

        this.type = type;
    }

    /**
     * @return Returns the messageResourceKey.
     */
    @Column(nullable = false)
    public String getMessageResourceKey() {
        return this.messageResourceKey;
    }

    /**
     * @param messageResourceKey The messageResourceKey to set.
     */
    public void setMessageResourceKey(String messageResourceKey) {
        this.messageResourceKey = messageResourceKey;
    }

    public Object[] getMessageParameters() {
		return messageParameters;
	}

	public void setMessageParameters(Object[] messageParameters) {
		this.messageParameters = messageParameters;
	}

	/**
     * @return the resourceBundleName
     */
    @Column(nullable = false)
    public String getResourceBundleName() {
        return resourceBundleName;
    }

    /**
     * @param resourceBundleName the resourceBundleName to set
     */
    public void setResourceBundleName(String resourceBundleName) {
        this.resourceBundleName = resourceBundleName;
    }

	@SuppressWarnings("unchecked")
	public void setBundleResolver(Class bundleResolver) {
		this.bundleResolver = bundleResolver;
	}

	@SuppressWarnings("unchecked")
	public Class getBundleResolver() {
		return bundleResolver;
	}
}
