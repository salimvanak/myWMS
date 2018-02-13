package uk.ltd.mediamagic.los.inventory.test;

import org.mywms.ejb.BeanLocator;
import org.mywms.model.Client;
import org.mywms.service.ItemDataService

import de.linogistix.los.crud.ClientCRUDRemote;
import de.linogistix.los.example.CommonTestTopologyRemote;
import de.linogistix.los.example.InventoryTestTopologyRemote;
import de.linogistix.los.example.LocationTestTopologyRemote;
import de.linogistix.los.inventory.crud.ItemDataCRUDRemote
import de.linogistix.los.inventory.crud.StockUnitCRUDRemote;
import de.linogistix.los.inventory.query.ItemDataQueryRemote
import de.linogistix.los.inventory.query.StockUnitQueryRemote;
import de.linogistix.los.location.crud.LOSAreaCRUDRemote;
import de.linogistix.los.location.crud.LOSRackCRUDRemote;
import de.linogistix.los.location.crud.LOSStorageLocationCRUDRemote;
import de.linogistix.los.location.crud.LOSStorageLocationTypeCRUDRemote;
import de.linogistix.los.location.crud.LOSTypeCapacityConstraintCRUDRemote;
import de.linogistix.los.location.crud.UnitLoadCRUDRemote;
import de.linogistix.los.location.crud.UnitLoadTypeCRUDRemote;
import de.linogistix.los.location.entityservice.LOSStorageLocationService
import de.linogistix.los.location.query.LOSAreaQueryRemote;
import de.linogistix.los.location.query.LOSStorageLocationQueryRemote;
import de.linogistix.los.location.query.LOSStorageLocationTypeQueryRemote;
import de.linogistix.los.location.query.LOSTypeCapacityConstraintQueryRemote;
import de.linogistix.los.location.query.RackQueryRemote;
import de.linogistix.los.location.query.UnitLoadQueryRemote;
import de.linogistix.los.location.query.UnitLoadTypeQueryRemote;
import de.linogistix.los.query.ClientQueryRemote;
import de.linogistix.los.query.exception.BusinessObjectNotFoundException;
import de.linogistix.los.test.TestUtilities;

/**
 * @deprecated
 * do not use this class. The new test case are design to be independent of state so that 
 * test can be executed concurrently, using this class will prevent concurrent tests from working. 
 * @author slim
 *
 */
@Deprecated
public class TopologyHelper {
  BeanLocator beanLocator;
  
  ClientCRUDRemote clService;
	ClientQueryRemote clQuery;
	
  LOSStorageLocationTypeQueryRemote slTypeQuery;
	LOSStorageLocationTypeCRUDRemote slTypeCRUD;
	LOSStorageLocationQueryRemote slQuery;
  LOSStorageLocationCRUDRemote slCRUD;

  UnitLoadTypeQueryRemote ulTypeQuery;
	UnitLoadTypeCRUDRemote ulTypeService;
  UnitLoadQueryRemote ulQuery;
	UnitLoadCRUDRemote ulCRUD;
	
  StockUnitQueryRemote suQuery;
	StockUnitCRUDRemote suService;	
  
  LOSTypeCapacityConstraintQueryRemote capacityQuery;
  LOSTypeCapacityConstraintCRUDRemote capacityService;
  LOSAreaQueryRemote areaQuery;
  LOSAreaCRUDRemote areaService;
  RackQueryRemote rackQuery;
	LOSRackCRUDRemote rackService;
	
	ItemDataCRUDRemote itemService;
	ItemDataQueryRemote itemQuery;
	
  LocationTestTopologyRemote locTopology;
  InventoryTestTopologyRemote invTopology;
  CommonTestTopologyRemote commonTopology;

  
  TopologyHelper(BeanLocator beanLocator) {
		Objects.requireNonNull(beanLocator);
  	this.beanLocator = beanLocator;
    clService = getBeanLocator().getStateless(ClientCRUDRemote.class);
  	locTopology = getBeanLocator().getStateless(LocationTestTopologyRemote.class, "los.location-comp");
    invTopology = getBeanLocator().getStateless(InventoryTestTopologyRemote.class, "los.inventory-comp");
    commonTopology = getBeanLocator().getStateless(CommonTestTopologyRemote.class, "los.common-comp");
				
    slCRUD = getBeanLocator().getStateless(LOSStorageLocationCRUDRemote.class);
    ulCRUD = getBeanLocator().getStateless(UnitLoadCRUDRemote.class);
    suService = getBeanLocator().getStateless(StockUnitCRUDRemote.class);
    ulTypeService = getBeanLocator().getStateless(UnitLoadTypeCRUDRemote.class);
    slTypeCRUD = getBeanLocator().getStateless(LOSStorageLocationTypeCRUDRemote.class);
    capacityService = getBeanLocator().getStateless(LOSTypeCapacityConstraintCRUDRemote.class);
    areaService = getBeanLocator().getStateless(LOSAreaCRUDRemote.class);
    rackService = getBeanLocator().getStateless(LOSRackCRUDRemote.class);
		itemService = getBeanLocator().getStateless(ItemDataCRUDRemote.class);
		
    clQuery = getBeanLocator().getStateless(ClientQueryRemote.class);
    slQuery = getBeanLocator().getStateless(LOSStorageLocationQueryRemote.class);
    ulQuery = getBeanLocator().getStateless(UnitLoadQueryRemote.class);
    suQuery = getBeanLocator().getStateless(StockUnitQueryRemote.class);
    ulTypeQuery = getBeanLocator().getStateless(UnitLoadTypeQueryRemote.class);
    slTypeQuery = getBeanLocator().getStateless(LOSStorageLocationTypeQueryRemote.class);
    capacityQuery = getBeanLocator().getStateless(LOSTypeCapacityConstraintQueryRemote.class);
    areaQuery = getBeanLocator().getStateless(LOSAreaQueryRemote.class);
    rackQuery = getBeanLocator().getStateless(RackQueryRemote.class);
    itemQuery = getBeanLocator().getStateless(ItemDataQueryRemote.class);
	}
  
  /**
   * Test of create method, of class TopologyBean.
   */
  def createTestTopology() throws Exception {
		Objects.requireNonNull(commonTopology);
		Objects.requireNonNull(invTopology);
		Objects.requireNonNull(locTopology);
  	commonTopology.create();
  	locTopology.create();
    invTopology.create();
  }
	
	def removeTestTopology() {
		invTopology.clear();
		locTopology.clear();
		commonTopology.clear();
	}

	def Client getTESTCLIENT() throws BusinessObjectNotFoundException {
		return getClQuery().queryByIdentity(CommonTestTopologyRemote.TESTCLIENT_NUMBER);
	}

	def Client getTESTMANDANT() throws BusinessObjectNotFoundException {
		return getClQuery().queryByIdentity(CommonTestTopologyRemote.TESTMANDANT_NUMBER);
	}
}
