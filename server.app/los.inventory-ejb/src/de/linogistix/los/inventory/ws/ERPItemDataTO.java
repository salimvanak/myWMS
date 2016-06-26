/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.inventory.ws;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

/**
 * TO for receiving ItemData from an ERP
 * 
 * @author trautm
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)             
@XmlType(
		name = "ItemDataTO",
		namespace="http://com.linogistix/connector/wms/inventory" 
)
public class ERPItemDataTO implements Serializable{
	
	private static final long serialVersionUID = 1L;

	public ERPItemDataTO(){
		this("<< no client reference>>", "<< no article reference >>");
	}
	
	public ERPItemDataTO(String clientRef, String articleRef){
		this.clientRef = clientRef;
		this.articleRef = articleRef;
	}
	
	/**
	 * The unique number of the ItemData/article.
	 */
	public String articleRef;
	
	/**
	 * A description for this ItemData/article.
	 */
	public String description;
	
	/**
	 * A handle for the client this ItemData/article belongs to
	 */
	public String clientRef;
	
	/**
	 * Amount of stock has not to be smaller than this. 
	 */
	int minimumStock; 
	
	
	@Override
	public String toString() {
		StringBuffer ret = new StringBuffer();
		ret.append("ItemDataTO: ");
		
		ret.append("[clientRef=");
		ret.append(clientRef);
		ret.append("] ");
		ret.append("[articleRef=");
		ret.append(articleRef);
		ret.append("] ");
		
		ret.append("[description=");
		ret.append(description);
		ret.append("] ");
		ret.append("[minimumStock=");
		ret.append(minimumStock);
		ret.append("] ");
		
		return new String(ret);
	}
	
	
}
