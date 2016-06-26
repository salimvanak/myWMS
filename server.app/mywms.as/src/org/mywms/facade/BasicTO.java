/*
 * Copyright (c) 2006 by Fraunhofer IML, Dortmund.
 * All rights reserved.
 *
 * Project: myWMS
 */
package org.mywms.facade;

import java.io.Serializable;
import java.util.Date;

import org.mywms.model.BasicEntity;
import org.mywms.model.BasicClientAssignedEntity;

/**
 * Transfers the data of the BasicEntity. This pattern differs from the
 * <i>Core J2EE Pattern Catalog</i>.<br>
 * The cause is: in myWMS the transfer objects are special to the facade
 * in which they are used. On the other hand the creation of transfer
 * objects should be the same mechanism over and over again. So building
 * transfer objects in their respective entity class is not facade
 * special and will be different from facade special mechanisms. Also
 * there is no need to transfer properties of enhanced (derived)
 * entities if the facade contract with the client does not offer those
 * properties.<br>
 * <br>
 * Please refer also to the <a
 * href="http://java.sun.com/blueprints/corej2eepatterns/Patterns/TransferObject.html">TransferObject</a>
 * on the j2ee pattern page.
 * 
 * @author Olaf Krause
 * @version $Revision: 442 $ provided by $Author: okrause $
 */
public class BasicTO
    implements Serializable
{
    private static final long serialVersionUID = 1L;

    /**
     * Read Only:<br>
     * The unique id of the entity. Manipulation of this property is not
     * supported.
     */
    public final long id;
    /**
     * Read Only:<br>
     * The version of the entity, according to the optimistic locking
     * concept. Changing this property will cause a failing the save
     * operation.
     */
    public final int version;
    /**
     * Read Only:<br>
     * The date, when the entity has been created. This property is in
     * fact imutable.
     */
    public final Date created;
    /**
     * Read Only:<br>
     * The date, when the entity has been saved last. This property is
     * set automaticly.
     */
    public final Date modified;
    /** If set, the entity is logically locked. */
    public int lock;

    /**
     * Read Only:<br>
     * A client, pursessing this entity.
     */
    public String clientName;
    /**
     * Read Only:<br>
     * A client, pursessing this entity.
     */
    public String clientNumber;

    /** The additional content, not mapped to properties. */
    public String additionalContent;

    protected BasicTO(BasicEntity entity) {
        this.id = entity.getId();
        this.version = entity.getVersion();
        this.created = entity.getCreated();
        this.modified = entity.getModified();
        this.lock = entity.getLock();
        this.additionalContent = entity.getAdditionalContent();
        if (entity instanceof BasicClientAssignedEntity) {
            BasicClientAssignedEntity ce = (BasicClientAssignedEntity) entity;
            if (ce.getClient() != null) {
                this.clientName = ce.getClient().getName();
                this.clientNumber = ce.getClient().getNumber();
            }
        }
    }

    /**
     * Copies the contents of the transfer object back to the
     * originating entity.
     * 
     * @param entity the entity to copy the data to
     * @throws VersionException if the version differs because the
     *             entity has been changed in the meantime
     */
    protected void merge(BasicEntity entity) throws VersionException {
        if (entity.getVersion() != this.version) {
            throw new VersionException();
        }

        entity.setLock(this.lock);
        entity.setAdditionalContent(this.additionalContent);
    }
}
