package uk.ltd.mediamagic.los.inventory.test;

import org.apache.log4j.Logger
import org.junit.BeforeClass

import de.linogistix.los.inventory.crud.LOSStockUnitRecordCRUDRemote
import de.linogistix.los.inventory.crud.LOSStorageRequestCRUDRemote
import de.linogistix.los.inventory.model.LOSStockUnitRecord 
import de.linogistix.los.inventory.model.LOSStorageRequest
import de.linogistix.los.inventory.query.LOSStockUnitRecordQueryRemote
import de.linogistix.los.inventory.query.LOSStorageRequestQueryRemote
import de.linogistix.los.location.crud.LOSUnitLoadCRUDRemote
import de.linogistix.los.location.crud.LOSUnitLoadRecordCRUDRemote
import de.linogistix.los.location.crud.UnitLoadTypeCRUDRemote
import de.linogistix.los.location.model.LOSStorageLocation
import de.linogistix.los.location.model.LOSUnitLoad
import de.linogistix.los.location.model.LOSUnitLoadRecord
import de.linogistix.los.location.query.LOSUnitLoadQueryRemote
import de.linogistix.los.location.query.LOSUnitLoadRecordQueryRemote
import de.linogistix.los.location.query.UnitLoadTypeQueryRemote
import de.linogistix.los.query.BODTO
import de.linogistix.los.query.QueryDetail
import de.linogistix.los.query.TemplateQuery
import de.linogistix.los.query.TemplateQueryWhereToken
import groovy.transform.CompileDynamic

trait UnitLoadSpec extends WMSSpecBase {
	private static final Logger log = Logger.getLogger(UnitLoadSpec.class)
	
  static LOSStockUnitRecordQueryRemote stockUnitRecordQuery
  static LOSStockUnitRecordCRUDRemote stockUnitRecordCRUD
  static LOSUnitLoadRecordQueryRemote unitLoadRecordQuery
  static LOSUnitLoadRecordCRUDRemote unitLoadRecordCRUD
	static LOSStorageRequestCRUDRemote storageRequestCRUD
	static LOSStorageRequestQueryRemote storageRequestQuery
	static UnitLoadTypeQueryRemote ulTypeQuery	
	static UnitLoadTypeCRUDRemote ulTypeService
	static LOSUnitLoadQueryRemote ulQuery
	static LOSUnitLoadCRUDRemote ulCRUD
	
	@CompileDynamic
	@BeforeClass
	void setupUnitLoads() {
    ulCRUD = getBean(LOSUnitLoadCRUDRemote.class)
    ulQuery = getBean(LOSUnitLoadQueryRemote.class)
    ulTypeService = getBean(UnitLoadTypeCRUDRemote.class)
    ulTypeQuery = getBean(UnitLoadTypeQueryRemote.class)
    storageRequestCRUD = getBean(LOSStorageRequestCRUDRemote.class)
    storageRequestQuery = getBean(LOSStorageRequestQueryRemote.class)
    unitLoadRecordQuery = getBean(LOSUnitLoadRecordQueryRemote.class)
    unitLoadRecordCRUD = getBean(LOSUnitLoadRecordCRUDRemote.class)
    stockUnitRecordQuery = getBean(LOSStockUnitRecordQueryRemote.class)
    stockUnitRecordCRUD = getBean(LOSStockUnitRecordCRUDRemote.class)
  }

	def createUnitLoadLabel(String tag) {
		def serialNumber = nextSeqNumber()
		return "UL" + "/" + tag +"-" + serialNumber
	}
	
	def LOSUnitLoad createUnitLoad(String tag, LOSStorageLocation location) {
		def ul = new LOSUnitLoad()
		ul.client = getClient()
		ul.labelId = createUnitLoadLabel(tag)
		ul.storageLocation = location
		ul.type = ulTypeQuery.getDefaultUnitLoadType()
		ul = create(ulCRUD, ul, { 
        cleanUpStorageRequestsForUnitLoads(it)
        cleanUpUnitLoadRecords(it)
        cleanUpStorageRecords(it)
      });
		log.info "Creating unit load " + ul.labelId + "(" + ul.id + ")"
		return ul
	}

	def LOSUnitLoad getUnitLoad(String label) {
		return ulQuery.queryByIdentity(label);
	}
	
	private List<BODTO<LOSStorageRequest>> cleanUpStorageRequestsForUnitLoads(BODTO<LOSUnitLoad> ul) {
		TemplateQuery template = new TemplateQuery().with {	
			addWhereToken(new TemplateQueryWhereToken(TemplateQueryWhereToken.OPERATOR_EQUAL, "unitLoad.id", ul.id))
			return it
		};
		template.setBoClass(LOSStorageRequest.class);
		List<BODTO<LOSStorageRequest>> list = storageRequestQuery.queryByTemplateHandles(new QueryDetail(0, 500), template);
		storageRequestCRUD.delete(list);
	}

	private List<BODTO<LOSStorageRequest>> cleanUpUnitLoadRecords(BODTO<LOSUnitLoad> ul) {
		TemplateQuery template = new TemplateQuery().with {	
			addWhereToken(new TemplateQueryWhereToken(TemplateQueryWhereToken.OPERATOR_EQUAL, "label", ul.name))
			return it
		};
		template.setBoClass(LOSUnitLoadRecord.class);
		List<BODTO<LOSUnitLoadRecord>> list = unitLoadRecordQuery.queryByTemplateHandles(new QueryDetail(0, 500), template);
		unitLoadRecordCRUD.delete(list);
	}

	private List<BODTO<LOSStockUnitRecord>> cleanUpStorageRecords(BODTO<LOSUnitLoad> ul) {
		TemplateQuery template = new TemplateQuery().with {	
			addWhereToken(new TemplateQueryWhereToken(TemplateQueryWhereToken.OPERATOR_EQUAL, "toUnitLoad", ul.name))
			return it
		};
		template.setBoClass(LOSStockUnitRecord.class);
		List<BODTO<LOSStockUnitRecord>> list = stockUnitRecordQuery.queryByTemplateHandles(new QueryDetail(0, 500), template);
		stockUnitRecordCRUD.delete(list);
		TemplateQuery template2 = new TemplateQuery().with {	
			addWhereToken(new TemplateQueryWhereToken(TemplateQueryWhereToken.OPERATOR_EQUAL, "fromUnitLoad", ul.name))
			return it
		};
		template2.setBoClass(LOSStockUnitRecord.class);
		List<BODTO<LOSStockUnitRecord>> list2 = stockUnitRecordQuery.queryByTemplateHandles(new QueryDetail(0, 500), template2);
		stockUnitRecordCRUD.delete(list2);
	}
	
	/**
	 * Create a number of unit loads at the give location.
	 * @param sl the location to store the unit loads
	 * @param unitloads the number of unit loads
	 */
	def createUnitLoadsOnLocation(LOSStorageLocation sl, int unitloads) {
		def uls = [];
		unitloads.times {
			uls << createUnitLoad("COUNT", sl);
		}
		return uls;
	}

}
