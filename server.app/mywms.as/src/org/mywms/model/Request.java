/*
 * Copyright (c) 2006 by Fraunhofer IML, Dortmund.
 * All rights reserved.
 *
 * Project: myWMS
 */
package org.mywms.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

/**
 * Request is the root class for all requests, handled by the myWMS.
 * Standard requests are for example:
 * <ul>
 * <li> TransportRequest
 * <li> PickingRequest
 * <li> SupplyRequest
 * </ul>
 * 
 * @author Olaf Krause
 * @version $Revision: 591 $ provided by $Author: trautm $
 */
@Entity
@Table(name = "mywms_request")
@Inheritance(strategy = InheritanceType.JOINED)
public class Request
    extends BasicClientAssignedEntity
{
    private static final long serialVersionUID = 1L;

    private String number = null;

//    private Request parentRequest = null;

    private String parentRequestNumber = "";
    /**
     * The system unique (internal) number of the request.
     * 
     * @return Returns the number.
     */
    @Column(nullable = false, unique = true, name="request_nr")
    @SequenceGenerator(name = "seqRequestNumber", sequenceName = "seqRequestNumber")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seqRequestNumber")
    public String getNumber() {
        return this.number;
    }

    /**
     * @param number The number to set.
     */
    public void setNumber(String number) {
        this.number = number;
    }

    @Override
    public String toUniqueString() {
        return getNumber();
    }

    /**
     * A handle to another request (i.e. a {@link Request} or a subclass or an external handle ).
     * The parent request is normally the cause for the creation of this {@link Request}. 
     * 
     * @return
     */
	public String getParentRequestNumber() {
		return parentRequestNumber;
	}

	public void setParentRequestNumber(String parentRequestNumber) {
		this.parentRequestNumber = parentRequestNumber;
	}
    
    
}
