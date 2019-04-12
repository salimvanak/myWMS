package uk.ltd.mediamagic.stocktaking.test;

import org.apache.log4j.Logger
import org.junit.BeforeClass

import de.linogistix.los.location.model.LOSStorageLocation
import de.linogistix.los.query.QueryDetail
import de.linogistix.los.query.TemplateQuery
import de.linogistix.los.query.TemplateQueryFilter
import de.linogistix.los.query.TemplateQueryWhereToken
import de.linogistix.los.stocktaking.model.*
import de.linogistix.los.stocktaking.crud.LOSStockTakingCRUDRemote
import de.linogistix.los.stocktaking.crud.LOSStocktakingOrderCRUDRemote
import de.linogistix.los.stocktaking.crud.LOSStocktakingRecordCRUDRemote
import de.linogistix.los.stocktaking.facade.LOSStocktakingFacade
import de.linogistix.los.stocktaking.query.LOSStockTakingQueryRemote
import de.linogistix.los.stocktaking.query.LOSStocktakingOrderQueryRemote
import de.linogistix.los.stocktaking.query.LOSStocktakingRecordQueryRemote
import de.linogistix.los.stocktaking.query.dto.StockTakingOrderTO
import de.linogistix.los.stocktaking.query.dto.StockTakingRecordTO
import groovy.util.logging.Log4j
import uk.ltd.mediamagic.los.inventory.test.WMSSpecBase

@Log4j
trait StockTakingOrderSpec extends WMSSpecBase {
	private static final Logger log = Logger.getLogger(StockTakingOrderSpec.class)
	
	static LOSStockTakingQueryRemote stockTakeQuery
	static LOSStockTakingCRUDRemote stockTakeCRUD
	static LOSStocktakingOrderQueryRemote stockTakeOrderQuery
	static LOSStocktakingOrderCRUDRemote stockTakeOrderCRUD
	static LOSStocktakingRecordQueryRemote stockTakeRecordQuery
	static LOSStocktakingRecordCRUDRemote stockTakeRecordCRUD
	static LOSStocktakingFacade stockTakeFacade
	
	private def createLabel(String prefix, String tag) {
		def serialNumber = nextSeqNumber()
		return prefix + "/" + tag +"-" + serialNumber
	}

	@BeforeClass
	void setupStockTaking() {
		stockTakeQuery = getBean(LOSStockTakingQueryRemote.class);
		stockTakeCRUD = getBean(LOSStockTakingCRUDRemote.class);
		stockTakeOrderQuery = getBean(LOSStocktakingOrderQueryRemote.class);
		stockTakeOrderCRUD = getBean(LOSStocktakingOrderCRUDRemote.class);
		stockTakeRecordQuery = getBean(LOSStocktakingRecordQueryRemote.class);
		stockTakeRecordCRUD = getBean(LOSStocktakingRecordCRUDRemote.class);
		stockTakeFacade = getBean(LOSStocktakingFacade.class);
	}
	
	int createStockTakingOrders(LOSStorageLocation sl) {
		def orderCount = stockTakeFacade.generateOrders(true, null, null, null, null, sl.getId(), null, null, null, null, true, true)
		def locationName = sl.name;
		addCleanup {
			//TODO delete the LOSStockTake for this order as well.
			def orders = getOrderForStorageLocation(locationName); 
			orders.each {
				def records = getRecordsForOrder(it)
				stockTakeRecordCRUD.delete(records)
			}
			stockTakeOrderCRUD.delete(orders)
		}
		return orderCount;
	}
	
	List<StockTakingOrderTO> getOrderForStorageLocation(String locationName) {
		TemplateQuery template = new TemplateQuery();
		template.setBoClass(LOSStocktakingOrder.class)
		TemplateQueryFilter filter = template.addNewFilter();
		filter.addWhereToken(new TemplateQueryWhereToken(TemplateQueryWhereToken.OPERATOR_EQUAL, "locationName", locationName));

		return stockTakeOrderQuery.queryByTemplateHandles(new QueryDetail(0,500), template);
	}		

	List<StockTakingRecordTO> getRecordsForOrder(StockTakingOrderTO order) {
		TemplateQuery template = new TemplateQuery();
		template.setBoClass(LOSStocktakingRecord.class)
		TemplateQueryFilter filter = template.addNewFilter();
		filter.addWhereToken(new TemplateQueryWhereToken(TemplateQueryWhereToken.OPERATOR_EQUAL, "stocktakingOrder.id", order.id));

		return stockTakeRecordQuery.queryByTemplateHandles(new QueryDetail(0,500), template);
	}		
}
