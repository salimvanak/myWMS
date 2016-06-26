/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.query.dto;

import de.linogistix.los.model.LOSServiceProperty;
import de.linogistix.los.query.BODTO;

public class LOSServicePropertyTO extends BODTO<LOSServiceProperty> {

	private static final long serialVersionUID = 1L;
    
	private String service;
	
	private String key;
	
	private String value;

	private String subkey;
	
	private String client;
	
	public LOSServicePropertyTO(LOSServiceProperty prop){
		this(prop.getId(), prop.getVersion(), prop.getService(), prop.getClient().getName(), prop.getKey(), prop.getValue(), prop.getSubkey());
	}
	
	public LOSServicePropertyTO(Long id, int version, String service, String client, String key, String value, String subkey){
		super(id, version, service+"."+key+(subkey==null?"":"."+subkey));
		this.service = service;
		this.key = key;
		this.value = value;
		this.subkey = subkey;
		this.client = client;
	}
	
	public String getService() {
		return service;
	}	

	public void setKey(String key) {
		this.key = key;
	}

	public String getKey() {
		return key;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}

	public String getSubkey() {
		return subkey;
	}

	public void setSubkey(String subkey) {
		this.subkey = subkey;
	}

	public String getClient() {
		return client;
	}

	public void setClient(String client) {
		this.client = client;
	}
	

}
