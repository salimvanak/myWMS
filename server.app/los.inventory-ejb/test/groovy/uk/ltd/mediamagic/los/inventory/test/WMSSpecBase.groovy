package uk.ltd.mediamagic.los.inventory.test

import org.apache.log4j.Logger
import org.junit.After
import org.junit.BeforeClass
import org.mywms.facade.MyWMSSpecification
import org.mywms.model.Client

import de.linogistix.los.crud.BusinessObjectCRUDRemote
import de.linogistix.los.crud.ClientCRUDRemote
import de.linogistix.los.query.BODTO
import de.linogistix.los.query.ClientQueryRemote
import de.linogistix.los.query.exception.BusinessObjectNotFoundException
import de.linogistix.los.util.businessservice.LOSSequenceGeneratorServiceRemote
import groovy.transform.CompileDynamic
import groovy.transform.CompileStatic

@CompileStatic
trait WMSSpecBase extends MyWMSSpecification {
	static final String TESTCLIENT_NUMBER = "Test Client";	
	static final String TESTMANDANT_NUMBER = "Test Mandant";

	
	private static final Logger log = Logger.getLogger(WMSSpecBase.class)
	static ClientCRUDRemote clService;
	static ClientQueryRemote clQuery;
	static LOSSequenceGeneratorServiceRemote seqService;
			
	private List<Runnable> cleanupClosures = [];
		
	void addCleanup(Closure r) {
		cleanupClosures << r;
	}

	@CompileDynamic
	@BeforeClass
	void setupTestTopology() {
    clService = getBean(ClientCRUDRemote.class)
    clQuery = getBean(ClientQueryRemote.class)
    seqService = getBean(LOSSequenceGeneratorServiceRemote.class)
	}
			
	@After
	void cleanupTopology() {
		cleanupClosures.reverse().each { it.run() }
	}
		
	int nextSeqNumber() {
		return seqService.getNextSequenceNumber("testing-key-sequence");
	}
	
	Client getClient() {
		assert clQuery != null;
		Client testClient;
		try {
			testClient = clQuery.getByNumberIgnoreCase(TESTCLIENT_NUMBER)
		} catch (BusinessObjectNotFoundException ex) {
			testClient = new Client()
			testClient.name = TESTCLIENT_NUMBER
			testClient.number = TESTCLIENT_NUMBER
			testClient.code = TESTCLIENT_NUMBER
			testClient = create(clService, testClient)
		}
		return testClient;
	}	

	
	/**
	 * persists a BasicEntity subclass with automatic cleanup at the end of the test case.
	 * @param service the service to use to create it
	 * @param obj the object to persist
	 * @return
	 */
	@CompileDynamic
	public <T> T create(BusinessObjectCRUDRemote<?> service, T obj) {
		obj = service.create(obj);
		BODTO<T> bodto = new BODTO<>(obj)
		addCleanup { service.delete(Collections.singletonList(bodto)) }
		return obj
	}

		@CompileDynamic
	public <T> T create(BusinessObjectCRUDRemote<?> service, T obj, Closure before) {
		obj = service.create(obj);
		BODTO<T> bodto = new BODTO<>(obj)
		addCleanup { service.delete(Collections.singletonList(bodto)) }
		addCleanup { before(bodto) }
		return obj
	}
}
