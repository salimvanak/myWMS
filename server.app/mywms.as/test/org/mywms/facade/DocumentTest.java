/*
 * Copyright (c) 2006 by Fraunhofer IML, Dortmund.
 * All rights reserved.
 *
 * Project: myWMS
 */
package org.mywms.facade;

import java.util.Date;


/**
 * Tests the facade Document.
 * 
 * @author Olaf Krause
 * @version $Revision: 198 $ provided by $Author: okrause $
 */
public class DocumentTest 
	extends TestInit 
{
	private Document document;
	
	/*
	 * @see TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();
		document = beanLocator.getStateless(Document.class);
	}

	/*
	 * @see TestCase#tearDown()
	 */
	protected void tearDown() throws Exception {
		super.tearDown();
	}
	
	public void testDocument()
		throws Exception
	{
		Date dateEarlier = new Date(System.currentTimeMillis() - 1000L);
		final String NAME = Long.toString(System.currentTimeMillis());
		final String NAME2 = NAME+"-B";
		final String TYPE = org.mywms.globals.DocumentTypes.TEXT_PLAIN.toString();
		final byte[] BYTES = "Das ist ein Test.".getBytes();
		
		// -------------------------------------------------------------
		// check creation of a document
		document.createDocument(NAME, TYPE, BYTES);
		// -------------------------------------------------------------

		Date dateLater = new Date(System.currentTimeMillis());

		// wait
		synchronized(this) {
			wait(500);
		}
		
		
		// -------------------------------------------------------------
		// check get list
		DocumentTO[] documents 
			= document.getDocuments(null, null, null, null, 1);
		
		assertEquals("wrong number of elements", 1, documents.length);
		assertEquals("wrong document data", NAME, documents[0].name );
		assertEquals("wrong document data", TYPE, documents[0].type);
		// list of documents must not contain document data:
		assertEquals("wrong document data", 0, documents[0].document.length);
		
		// -------------------------------------------------------------
		// check get single document
		DocumentTO doc 
			= document.getDocument(documents[0].id);
		
		assertEquals("wrong document data", NAME, doc.name );
		assertEquals("wrong document data", TYPE, doc.type);
		// check document data
		assertEquals("wrong document data", BYTES.length, doc.document.length );
		for(int i=0;i<BYTES.length;i++) {
			assertEquals("wrond document data in byte "+i, BYTES[i], doc.document[i]);
		}
		
		// -------------------------------------------------------------
		// check creation of a second document
		document.createDocument(NAME2, TYPE, BYTES);
		
		// -------------------------------------------------------------
		// check get list
		documents 
			= document.getDocuments(null, null, null, null, 2);
		assertEquals("wrong number of elements", 2, documents.length);
		assertEquals("wrong document data", NAME2, documents[0].name );
		assertEquals("wrong document data", NAME, documents[1].name );

		// -------------------------------------------------------------
		// check get list
		// lower and upper time bound 
		documents 
			= document.getDocuments(dateEarlier, dateLater, null, null, 0);
		
		// only one document was created within the time
		assertEquals("wrong number of elements", 1, documents.length);
		assertEquals("wrong document data", NAME, documents[0].name );
		assertEquals("wrong document data", TYPE, documents[0].type);
		// list of documents must not contain document data:
		assertEquals("wrong document data", 0, documents[0].document.length);

		// -------------------------------------------------------------
		// check get list
		// upper time bound 
		documents 
			= document.getDocuments(null, dateLater, null, null, 0);
		
		// assertEquals("wrong number of elements", 1, documents.length); // fails if older documents are stored
		assertEquals("wrong document data", NAME, documents[0].name );
		assertEquals("wrong document data", TYPE, documents[0].type);
		// list of documents must not contain document data:
		assertEquals("wrong document data", 0, documents[0].document.length);

		// -------------------------------------------------------------
		// check get list
		// name of document
		documents 
			= document.getDocuments(null, null, NAME, null, 0);
		
		assertEquals("wrong number of elements", 1, documents.length); 
		assertEquals("wrong document data", NAME, documents[0].name );

		documents 
			= document.getDocuments(null, null, NAME2, null, 0);
		
		assertEquals("wrong number of elements", 1, documents.length); 
		assertEquals("wrong document data", NAME2, documents[0].name );

		// -------------------------------------------------------------
		// check get list
		// type of document
		documents 
			= document.getDocuments(null, null, null, TYPE, 0);
		
		assertEquals("wrong number of elements", 2, documents.length); 
		assertEquals("wrong document data", TYPE, documents[0].type );
		assertEquals("wrong document data", TYPE, documents[1].type );

		// -------------------------------------------------------------
		// check get list
		// name of document
		documents 
			= document.getDocuments(null, null, NAME2, null, 0);
		
		assertEquals("wrong number of elements", 1, documents.length); 
		assertEquals("wrong document data", NAME2, documents[0].name );

		// -------------------------------------------------------------
		// check get list
		// lower time bound 
		documents 
			= document.getDocuments(dateEarlier, null, null, null, 0);
		
		assertEquals("wrong number of elements", 2, documents.length);
		assertEquals("wrong document name", NAME2, documents[0].name );
		assertEquals("wrong document data", TYPE, documents[0].type);
		// list of documents must not contain document data:
		assertEquals("wrong document data", 0, documents[0].document.length);

		assertEquals("wrong document name", NAME, documents[1].name );
		assertEquals("wrong document data", TYPE, documents[1].type);
		// list of documents must not contain document data:
		assertEquals("wrong document data", 0, documents[1].document.length);
		
		// -------------------------------------------------------------
		// check deletion
		document.removeDocument(documents[0].id);
		
		// -------------------------------------------------------------
		// check get list
		documents 
			= document.getDocuments(null, null, null, null, 1);
		assertEquals("wrong number of elements", 1, documents.length);
		assertEquals("wrong document data", NAME, documents[0].name );
		
		// -------------------------------------------------------------
		// check deletion
		document.removeDocument(documents[0].id);
	}
	
}
