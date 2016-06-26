/*
 * Copyright (c) 2006 by Fraunhofer IML, Dortmund.
 * All rights reserved.
 *
 * Project: myWMS
 */
package org.mywms.facade;

import org.mywms.globals.DocumentTypes;

/**
 * Transfers the data of a Document.
 * 
 * @author Olaf Krause
 * @version $Revision: 442 $ provided by $Author: okrause $
 */
public class DocumentTO
    extends BasicTO
{
    private static final long serialVersionUID = 1L;

    public String name = Long.toString(System.currentTimeMillis());
    public String type = DocumentTypes.TEXT_PLAIN.toString();
    public int size = 0;
    public byte[] document = new byte[0];

    /** true, if the data has been copied to this TO; false otherwise. */
    public boolean containsData;

    /**
     * Creates a new DocumentTO, using the data of the given Document.
     * 
     * @param document the origin of the data to be transfered
     */
    public DocumentTO(org.mywms.model.Document document) {
        this(document, true);
    }

    /**
     * Creates a new DocumentTO, using the data of the given Document.
     * 
     * @param document the origin of the data to be transfered
     * @param withDocument if true, the document data is copied into the
     *            TO, otherwise only the name, the type and the size is
     *            transfered
     */
    public DocumentTO(org.mywms.model.Document document, boolean withDocument) {
        super(document);
        this.name = document.getName();
        this.type = document.getType();
        this.size = document.getSize();
        this.containsData = withDocument;
        if (withDocument) {
            this.document = document.getDocument();
        }
    }

    /**
     * @see org.mywms.facade.BasicTO#merge(org.mywms.model.BasicEntity)
     * @param document
     * @throws VersionException
     */
    public void merge(org.mywms.model.Document document)
        throws VersionException
    {
        super.merge(document);

        document.setName(this.name);
        document.setType(this.type);
        if (containsData) {
            document.setDocument(this.document);
        }
    }
}
