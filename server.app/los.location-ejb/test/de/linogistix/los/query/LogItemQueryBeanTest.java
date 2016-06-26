/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.query;

import de.linogistix.los.crud.BusinessObjectCreationException;
import de.linogistix.los.crud.BusinessObjectExistsException;
import de.linogistix.los.crud.LogItemCRUDRemote;
import de.linogistix.los.query.ClientQueryRemote;
import de.linogistix.los.query.LogItemQueryBeanTest;
import de.linogistix.los.query.exception.BusinessObjectNotFoundException;
import de.linogistix.los.runtime.BusinessObjectSecurityException;
import de.linogistix.los.test.TestUtilities;

import java.util.logging.Level;
import java.util.logging.Logger;

import junit.framework.TestCase;

import org.mywms.globals.LogItemType;
import org.mywms.model.Client;
import org.mywms.model.LogItem;

/**
 *
 * @author artur
 */
public class LogItemQueryBeanTest extends TestCase{

    private LogItemCRUDRemote logItemCRUD;
        
    private ClientQueryRemote clientQuery;
    
    protected void setUp() throws Exception {
	super.setUp();
        logItemCRUD = TestUtilities.beanLocator.getStateless(LogItemCRUDRemote.class);
        
        clientQuery = TestUtilities.beanLocator.getStateless(ClientQueryRemote.class);
    }
    
    public void testQueryRecent() {
       
            Client client = null;

            try {
                client = clientQuery.queryById(0L);
            } catch (BusinessObjectNotFoundException ex) {
                fail("SystemClient nicht gefunden");
            } catch (BusinessObjectSecurityException ex) {
                fail("Durfte SystemClient nicht abfragen");
            }
            LogItem l1 = new LogItem();
            l1.setClient(client);
            l1.setUser("System Admin");
            l1.setHost("localhost");
            l1.setMessage("Fuenftes LogItem");
            l1.setSource("MySource");
            l1.setType(LogItemType.LOG);

         try {
            logItemCRUD.create(l1);
        } catch (BusinessObjectExistsException ex) {
            Logger.getLogger(LogItemQueryBeanTest.class.getName()).log(Level.SEVERE, null, ex);
        } catch (BusinessObjectCreationException ex) {
            Logger.getLogger(LogItemQueryBeanTest.class.getName()).log(Level.SEVERE, null, ex);
        } catch (BusinessObjectSecurityException ex) {
            Logger.getLogger(LogItemQueryBeanTest.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
}
