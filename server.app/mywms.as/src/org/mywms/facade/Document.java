/*
 * Copyright (c) 2006 by Fraunhofer IML, Dortmund.
 * All rights reserved.
 *
 * Project: myWMS
 * File:    $id:$
 */
package org.mywms.facade;

import java.util.Date;

import javax.ejb.Remote;

import org.mywms.service.EntityNotFoundException;
import org.mywms.service.UniqueConstraintViolatedException;

/**
 * This fassade declares the interface to access the documents, stored
 * in the WMS.
 * 
 * @author Olaf Krause
 * @version $Revision: 442 $ provided by $Author: okrause $
 */
@Remote
public interface Document {

    /**
     * Creates a new Document, using the specified arguments.
     * 
     * @param name the name of the new document
     * @param type the type of the new document
     * @param document the bytes of the new document
     * @return the id of the new created document
     * @throws UniqueConstraintViolatedException if the name is non
     *             unique
     * @throws EntityNotFoundException if the referenced client could
     *             not be found
     */
    long createDocument(String name, String type, byte[] document)
        throws UniqueConstraintViolatedException,
            EntityNotFoundException;

    /**
     * Returns the document with the specified name.
     * 
     * @param name the unique (file-)name of the document
     * @return the document
     * @throws EntityNotFoundException if the specified Document could
     *             not be found
     */
    DocumentTO getDocument(String name) throws EntityNotFoundException;

    /**
     * Returns the document with the specified id.
     * 
     * @param id the unique id of the document
     * @return the document
     * @throws EntityNotFoundException if the specified Document could
     *             not be found
     */
    DocumentTO getDocument(long id) throws EntityNotFoundException;

    /**
     * Stores a changed document back into the database.
     * 
     * @param document the document to save
     * @throws EntityNotFoundException if the specified document could
     *             not be found
     */
    void setDocument(DocumentTO document)
        throws EntityNotFoundException,
            VersionException;

    /**
     * Returns a list of documents. If an argument of the method is
     * null, it has no filtering effect. The returned DocumentTOs do not
     * contain the document itself, but all other data (size, name,
     * type). To receive the document itself, please refer to the
     * getDocument() methods.
     * 
     * @see #getDocument(long)
     * @see #getDocument(String)
     * @param startDate the earliest creation date of the documents to
     *            return
     * @param endDate the latest creation date of the documents to
     *            return
     * @param name the name or wildcart identifying the filename
     * @param type the type of the searched document
     * @param limit the maximum number of documents returned
     * @return the matching documents
     */
    DocumentTO[] getDocuments(
        Date startDate,
        Date endDate,
        String name,
        String type,
        int limit);

    /**
     * Deletes the specified document.
     * 
     * @param id the unique id of the document to delete
     * @throws EntityNotFoundException if the specified document could
     *             not be found
     */
    void removeDocument(long id)
        throws EntityNotFoundException,
            DocumentException;
}
