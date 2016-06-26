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
 * TO for receiving a Batch from an ERP
 * 
 * @author trautm
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)             
@XmlType(
		name = "ERPBatchDataTO",
		namespace="http://com.linogistix/connector/wms/inventory" 
)
public class ERPBatchDataTO implements Serializable{
	
	private static final long serialVersionUID = 1L;

	public ERPBatchDataTO(){
		this("<< no client reference>>", "<< no article reference >>" , "<< no batch reference >>");
	}
	
	public ERPBatchDataTO(String clientRef, String articleRef, String batchRef){
		this.clientRef = clientRef;
		this.articleRef = articleRef;
	}
	
	/**
	 * A unique reference to the client
	 */
	String clientRef;
	
	/**
	 * A unique reference to the article the batch belongs to
	 */
	String articleRef;
	
	/**
	 * A unique reference to the batch itself
	 */
	String batchRef;
	
	/**
	 * A description 
	 */
	String description;
	
	
	
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
		ret.append("[batchRef=");
		ret.append(batchRef);

		ret.append("] ");
		
		return new String(ret);
	}
	
	
}
