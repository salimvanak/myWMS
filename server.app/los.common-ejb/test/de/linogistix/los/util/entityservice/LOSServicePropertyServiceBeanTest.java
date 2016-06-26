/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.util.entityservice;

import java.util.List;

import junit.framework.TestCase;

import org.apache.log4j.Logger;
import org.mywms.model.Client;
import org.mywms.service.EntityNotFoundException;

import de.linogistix.los.crud.LOSServicePropertyCRUDRemote;
import de.linogistix.los.example.CommonTestTopologyRemote;
import de.linogistix.los.model.LOSServiceProperty;
import de.linogistix.los.query.ClientQueryRemote;
import de.linogistix.los.query.LOSServicePropertyQueryRemote;
import de.linogistix.los.query.QueryDetail;
import de.linogistix.los.query.TemplateQuery;
import de.linogistix.los.query.TemplateQueryWhereToken;
import de.linogistix.los.query.exception.BusinessObjectNotFoundException;
import de.linogistix.los.query.exception.BusinessObjectQueryException;
import de.linogistix.los.test.TestUtilities;
import de.linogistix.los.util.entityservice.LOSServicePropertyService;
import de.linogistix.los.util.entityservice.LOSServicePropertyServiceBean;

public class LOSServicePropertyServiceBeanTest extends TestCase {
	
	private static final Logger log = Logger.getLogger(LOSServicePropertyServiceBeanTest.class);
	protected void setUp() throws Exception {
		super.setUp();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}
	
	
	public void testMatch(){
		LOSServicePropertyServiceBean bean = new LOSServicePropertyServiceBean();
		
		assertTrue(bean.match("", new String[0]));
		assertTrue(bean.match(null, new String[0]));
		assertTrue(bean.match("", null));
		assertTrue(bean.match(null, null));
		
		assertTrue(bean.match("A", new String[]{"A"}));
		assertTrue(bean.match("A_B", new String[]{"A", "B"}));
		
		assertFalse(bean.match("A", new String[]{"B"}));
		assertFalse(bean.match("A_B", new String[]{"A"}));
		
	}
	public void testProp() throws BusinessObjectNotFoundException, EntityNotFoundException, BusinessObjectQueryException{

		clear();
		
		LOSServicePropertyCRUDRemote confCrud = TestUtilities.beanLocator.getStateless(LOSServicePropertyCRUDRemote.class);
		LOSServicePropertyQueryRemote confQuery = TestUtilities.beanLocator.getStateless(LOSServicePropertyQueryRemote.class);
		

		Client c1 = TestUtilities.beanLocator.getStateless(ClientQueryRemote.class).getSystemClient();
		Client c2 = TestUtilities.beanLocator.getStateless(ClientQueryRemote.class).queryByIdentity(CommonTestTopologyRemote.TESTCLIENT_NUMBER);
		
		confCrud.create(LOSServicePropertyService.class, c1, "Merkmal1", "Wert1");
		confCrud.create(LOSServicePropertyService.class, c2, "Merkmal1", "Wert1");
		
		confCrud.create(LOSServicePropertyService.class, c1, "Merkmal1", "Wert1_1", "s1", "s2");
		confCrud.create(LOSServicePropertyService.class, c2, "Merkmal1", "Wert1", "s1");
		
		String v1 = confQuery.getValue(LOSServicePropertyService.class, c1, "Merkmal1");
		assertEquals( "Wert1", v1);
		
		String v2 = confQuery.getValue(LOSServicePropertyService.class, c1, "Merkmal1", "s1", "s2");
		assertEquals(v2, "Wert1_1");
		
		try{
			v2 = confQuery.getValue(LOSServicePropertyService.class, c1, "Merkmal1", "s1");
			fail ("expected EntityNotFoundException");
		} catch (EntityNotFoundException ex){
			//ok
		}
		
		clear();
		
		
		
	}

	
	private void clear(){
		
		LOSServicePropertyCRUDRemote confCrud = TestUtilities.beanLocator.getStateless(LOSServicePropertyCRUDRemote.class);
		LOSServicePropertyQueryRemote confQuery = TestUtilities.beanLocator.getStateless(LOSServicePropertyQueryRemote.class);
		
		try{
			TemplateQueryWhereToken t = new TemplateQueryWhereToken(TemplateQueryWhereToken.OPERATOR_EQUAL, "service", LOSServicePropertyService.class.getName());
			QueryDetail detail = new QueryDetail(0, Integer.MAX_VALUE);
			TemplateQuery q = new TemplateQuery();
			q.addWhereToken(t);
			q.setBoClass(LOSServiceProperty.class);
			
			
			List<LOSServiceProperty> l = confQuery.queryByTemplate(detail, q);
			
			for (LOSServiceProperty p : l){
				confCrud.delete(p);
			}
		} catch (Exception ex){
			log.error(ex.getMessage(), ex);
			fail(ex.getMessage());
		}
	}
}
