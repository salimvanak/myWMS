package uk.ltd.mediamagic.stocktaking.test

import org.junit.BeforeClass

import de.linogistix.los.stocktaking.crud.LOSStocktakingOrderCRUDRemote
import de.linogistix.los.stocktaking.facade.LOSStocktakingFacade
import de.linogistix.los.stocktaking.service.LOSStocktakingOrderService
import de.linogistix.los.stocktaking.service.QueryStockTakingOrderService
import groovy.transform.CompileDynamic
import uk.ltd.mediamagic.los.inventory.test.WMSSpecBase

trait StockTakingSpec extends WMSSpecBase {
	static LOSStocktakingFacade stocktakeFacade;
	static LOSStocktakingOrderCRUDRemote stocktakeCRUD;
	static QueryStockTakingOrderService stocktakeService;
	
	@CompileDynamic
	@BeforeClass
	void setupStocktakes() {
		stocktakeFacade = getBean(LOSStocktakingFacade.class);
		stocktakeService = getBean(QueryStockTakingOrderService.class);
		stocktakeCRUD = getBean(LOSStocktakingOrderCRUDRemote.class);
	}
	
	

}
