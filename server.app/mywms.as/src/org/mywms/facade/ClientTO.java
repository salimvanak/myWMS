/*
 * Copyright (c) 2006 by Fraunhofer IML, Dortmund.
 * All rights reserved.
 *
 * Project: myWMS
 */
package org.mywms.facade;

/**
 * Transfers the data of a Client.
 * 
 * @author Olaf Krause
 * @version $Revision: 442 $ provided by $Author: okrause $
 */
public class ClientTO
    extends BasicTO
{
    private static final long serialVersionUID = 1L;

    /** Read only: The unique number of the client. */
    public String number;
    /**
     * The unique name of the client, used to identify the client by the
     * user.
     */
    public String name;
    /** The email address of the client. */
    public String email;
    /** The phone number of the client. */
    public String phone;
    /** The fax number of the client. */
    public String fax;

    /**
     * Creates a new ClientTO, using the data of the given Client
     * entity.
     * 
     * @param client the origin of the data to be transfered
     */
    public ClientTO(org.mywms.model.Client client) {
        super(client);
        this.number = client.getNumber();
        this.name = client.getName();
        this.email = client.getEmail();
        this.phone = client.getPhone();
        this.fax = client.getFax();
    }

    /**
     * @see org.mywms.facade.BasicTO#merge(org.mywms.model.BasicEntity)
     * @param client
     * @throws VersionException
     */
    public void merge(org.mywms.model.Client client) throws VersionException {
        super.merge(client);

        client.setName(this.name);
        client.setEmail(this.email);
        client.setPhone(this.phone);
        client.setFax(this.fax);
    }

    /**
     * Returns a String containing the name and the number of the
     * client. Format: name/number
     * 
     * @return the name and the number of the client
     */
    public String toString() {
        return this.name + "/" + this.number;
    }

}
