/*
 * Copyright (c) 2006 by Fraunhofer IML, Dortmund.
 * All rights reserved.
 *
 * Project: myWMS
 */
package org.mywms.facade;

import java.util.Date;
import java.util.List;

import javax.annotation.security.PermitAll;
import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.log4j.Logger;
import org.mywms.globals.ServiceExceptionKey;
import org.mywms.model.Client;
import org.mywms.service.ConstraintViolatedException;
import org.mywms.service.DocumentService;
import org.mywms.service.EntityNotFoundException;
import org.mywms.service.UniqueConstraintViolatedException;

/**
 * @see org.mywms.facade.Document
 * @author Olaf Krause
 * @version $Revision: 740 $ provided by $Author: mkrane $
 */
@Stateless
@PermitAll
public class DocumentBean
    extends BasicFacadeBean
    implements Document
{
    private static final Logger log =
        Logger.getLogger(DocumentBean.class.getName());

    @EJB
    private DocumentService documentService;

    /**
     * @see org.mywms.facade.Document#getDocument(java.lang.String)
     */
    public DocumentTO getDocument(String name) throws EntityNotFoundException {
        Client cc = getCallersClient();

        org.mywms.model.Document document = documentService.getByName(cc, name);

        // return the transfer object
        DocumentTO documentTO = new DocumentTO(document);
        return documentTO;
    }

    /**
     * @see org.mywms.facade.Document#getDocument(long)
     */
    public DocumentTO getDocument(long id) throws EntityNotFoundException {
        Client cc = getCallersClient();

        org.mywms.model.Document document = documentService.get(id);

        // check, if client is allowed to request the apropriate
        // document
        if (!cc.isSystemClient() && document.getClient() != cc) {
            throw new EntityNotFoundException(
                ServiceExceptionKey.NO_ENTITY_WITH_ID);
        }

        // return the transfer object
        DocumentTO documentTO = new DocumentTO(document);
        return documentTO;
    }

    /**
     * @see org.mywms.facade.Document#setDocument(org.mywms.facade.DocumentTO)
     */
    public void setDocument(DocumentTO documentTO)
        throws EntityNotFoundException,
            VersionException
    {
        Client cc = getCallersClient();

        org.mywms.model.Document document = documentService.get(documentTO.id);

        // check, if client is allowed to request the apropriate
        // document
        if (!cc.isSystemClient() && document.getClient() != cc) {
            throw new EntityNotFoundException(
                ServiceExceptionKey.NO_ENTITY_WITH_ID);
        }

        // merge the transfer object
        documentTO.merge(document);
    }

    /**
     * @see org.mywms.facade.Document#getDocuments(java.util.Date,
     *      java.util.Date, java.lang.String, java.lang.String, int)
     */
    public DocumentTO[] getDocuments(
        Date startDate,
        Date endDate,
        String name,
        String type,
        int limit)
    {
        Client cc = getCallersClient();

        log.info("startDate="
            + startDate
            + "; endDate="
            + endDate
            + "; name="
            + name
            + "; type="
            + type
            + "; limit="
            + limit);
        List<org.mywms.model.Document> documents =
            documentService.getList(cc, startDate, endDate, name, type, limit);
        int n = documents.size();
        DocumentTO[] documentTOs = new DocumentTO[n];
        for (int i = 0; i < n; i++) {
            // copy descriptions only - no document-data
            documentTOs[i] = new DocumentTO(documents.get(i), false);
            log.info("i=" + i + "; name=" + documentTOs[i].name);
        }
        return documentTOs;
    }

    /**
     * @see org.mywms.facade.Document#removeDocument(long)
     */
    public void removeDocument(long id)
        throws EntityNotFoundException,
            DocumentException
    {
        Client cc = getCallersClient();

        org.mywms.model.Document document = documentService.get(id);
        // check, if client is allowed to request the apropriate
        // document
        if (!cc.isSystemClient() && document.getClient() != cc) {
            throw new EntityNotFoundException(
                ServiceExceptionKey.NO_ENTITY_WITH_ID);
        }

        // remove the document
        try {
            documentService.delete(document);
        }
        catch (ConstraintViolatedException e) {
            throw new DocumentException(
                "Removind the document failed, because a constraint was "
                    + "violated: "
                    + e.getMessage(),
                "org.mywms.facade.Document.removeConstraintViolated",
                new Object[0]);
        }
    }

    /**
     * @see org.mywms.facade.Document#createDocument(java.lang.String,
     *      java.lang.String, byte[])
     */
    public long createDocument(String name, String type, byte[] document)
        throws UniqueConstraintViolatedException,
            EntityNotFoundException
    {
        Client cc = getCallersClient();

        org.mywms.model.Document doc = documentService.create(cc, name);

        doc.setType(type);
        doc.setDocument(document);

        return doc.getId();
    }
}
