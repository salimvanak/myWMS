/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.mywms.model.BasicClientAssignedEntity;

/**
 *
 * @author Jordan
 */
@Entity
@Table(name="los_serviceconf", uniqueConstraints={
    @UniqueConstraint(columnNames={"service","client_id","servKey"})
})
public class LOSServiceProperty extends BasicClientAssignedEntity {
   
	private static final long serialVersionUID = 1L;
    
	private String service;
	
	private String key;
	
	private String value;

	private String subkey;
	
	public void setService(String service) {
		this.service = service;
	}
	
	@Column(nullable=false)
	public String getService() {
		return service;
	}	

	public void setKey(String key) {
		this.key = key;
	}

	@Column(name="servKey", nullable=false)
	public String getKey() {
		return key;
	}

	public void setValue(String value) {
		this.value = value;
	}

	@Column(name="servValue")
	public String getValue() {
		return value;
	}

	public String getSubkey() {
		return subkey;
	}

	public void setSubkey(String subkey) {
		this.subkey = subkey;
	}
	
	@Override
	public String toUniqueString() {
		String ret = service + "(" + getClient().getName() + ")" + " :"  + key + "-" + subkey;
		return ret;
	}
	
	
   
}
