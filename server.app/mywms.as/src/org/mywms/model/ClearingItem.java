/*
 * Copyright (c) 2008 by Fraunhofer IML, Dortmund.
 * All rights reserved.
 *
 * Project: myWMS
 */
package org.mywms.model;

import java.io.NotSerializableException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.mywms.util.BundleHelper;

/**
 * ClearingItems cover errors and operational problems to be solved by
 * an user.
 * 
 * @author Olaf Krause
 * @version $Revision: 720 $ provided by $Author: mkrane $
 */
@Entity
@Table(name = "mywms_clearingitem")
public class ClearingItem
    extends BasicClientAssignedEntity
{
    private static final long serialVersionUID = 1L;
    /** Stores the name of the localhost right from the start. */
    private static final String LOCALHOST_NAME = getLocalhostName();

    /** Holds value of property host. */
    private String host = LOCALHOST_NAME;

    /**
     * Holds the name of the resource bundle to resolve the resource
     * keys. The resource bundle will be used for the current
     * ClearingItem object and all contained options and retvals.
     */
    private String resourceBundleName;

    /** Holds value of property shortMessageResourceKey. */
    private String shortMessageResourceKey;
    /** Parameters being used in the i18n representation. */
    private Object[] shortMessageParameters;
    /** Holds value of property messageResourceKey. */
    private String messageResourceKey;
    /** Parameters being used in the i18n representation. */
    private Object[] messageParameters;
    /**
     * Holds value of property source. The source will be used for
     * dispatching the ClearingItem back to its originator.
     */
    private String source;

    /** Holds value of property user. */
    private String user = System.getProperties().getProperty("user.name");

    /** The user who solved the clearing */
    private String solver;

    private ClearingItemOption solution;

    private HashMap<String, String> propertyMap = new HashMap<String, String>();

	private Class bundleResolver;
	
    private ArrayList<ClearingItemOption> options =
        new ArrayList<ClearingItemOption>();

    /**
     * Getter for property host.
     * 
     * @return Value of property host.
     */
    @Column(nullable = false)
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
    @Transient
    public String getMessage() {
        return BundleHelper.resolve(
        		getMessageResourceKey(), getMessageResourceKey(), getMessageParameters(),
        		getResourceBundleName(), getBundleResolver(), Locale.getDefault());
    }


    /**
     * Getter for property source.
     * 
     * @return Value of property source.
     */
    @Column(nullable = false)
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
    @Column(name = "user_", nullable = false)
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

    /**
     * @return the messageParameters
     */
    @Column(nullable = false)
    public Object[] getMessageParameters() {
        return messageParameters;
    }

    /**
     * @param messageParameters the messageParameters to set
     * @throws NotSerializableException
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
     * @return the options
     */
    @Column(nullable = false)
    @Lob
    public ArrayList<ClearingItemOption> getOptions() {
        return options;
    }

    /**
     * @param options the options to set
     */
    public void setOptions(ArrayList<ClearingItemOption> options) {
        this.options = options;
    }

    /**
     * The user who solved the clearing
     * 
     * @return the solver
     */
    public String getSolver() {
        return solver;
    }

    /**
     * @param solver the solver to set
     */
    protected void setSolver(String solver) {
        this.solver = solver;
    }

    /**
     * Solves the ClearingItem by specifying a solver and a solution.
     * 
     * @param solver
     * @param solution
     */
    @Transient
    public void setSolution(String solver, ClearingItemOption solution) {
        this.solver = solver;
        this.solution = solution;
    }

    /**
     * @return the solution
     */
    @Lob
    public ClearingItemOption getSolution() {
        return solution;
    }

    /**
     * @param solution the solution to set
     */
    protected void setSolution(ClearingItemOption solution) {
        this.solution = solution;
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

    /**
     * @return the shortMessage
     */
    @Transient
    public String getShortMessage() {
    	return BundleHelper.resolve(
        		getShortMessageResourceKey(), getShortMessageResourceKey(), getShortMessageParameters(),
        		getResourceBundleName(), getBundleResolver(), Locale.getDefault());
    }


    /**
     * @return the shortMessageResourceKey
     */
    @Column(nullable = false)
    public String getShortMessageResourceKey() {
        return shortMessageResourceKey;
    }

    /**
     * @param shortMessageResourceKey the shortMessageResourceKey to set
     */
    public void setShortMessageResourceKey(String shortMessageResourceKey) {
        this.shortMessageResourceKey = shortMessageResourceKey;
    }

    /**
     * @return the shortMessageParameters
     */
    @Column(nullable = false)
    public Object[] getShortMessageParameters() {
        return shortMessageParameters;
    }

    /**
     * @param shortMessageParameters the shortMessageParameters to set
     */
    public void setShortMessageParameters(Object[] shortMessageParameters)
        throws NotSerializableException
    {
        for (Object parameter: shortMessageParameters) {
            if (!(parameter instanceof Serializable)) {
                throw new NotSerializableException();
            }
        }
        this.shortMessageParameters = shortMessageParameters;
    }
    
    public HashMap<String, String> getPropertyMap() {
		return propertyMap;
	}

	public void setPropertyMap(HashMap<String, String> propertyMap) {
		this.propertyMap = propertyMap;
	}
	
	@Transient
	public String getProperty(String key){
		return propertyMap.get(key);
	}
	
	public void setProperty(String key, String value){
		propertyMap.put(key, value);
	}

    @SuppressWarnings("rawtypes")
	@Column(nullable = true)
    @Lob
	public Class getBundleResolver() {
		return bundleResolver;
	}

	@SuppressWarnings("rawtypes")
	public void setBundleResolver(Class bundleResolver) {
		this.bundleResolver = bundleResolver;
	}
}
