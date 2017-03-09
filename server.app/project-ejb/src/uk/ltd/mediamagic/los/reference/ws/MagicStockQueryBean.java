package uk.ltd.mediamagic.los.reference.ws;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.annotation.security.PermitAll;
import javax.ejb.EJB;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;

import org.apache.log4j.Logger;
import org.jboss.ejb3.annotation.SecurityDomain;
import org.jboss.ws.api.annotation.WebContext;
import org.mywms.facade.FacadeException;
import org.mywms.model.Client;
import org.mywms.model.ItemData;
import org.mywms.model.Lot;
import org.mywms.service.ClientService;
import org.mywms.service.ItemDataService;
import org.mywms.service.LotService;

import de.linogistix.los.inventory.businessservice.LOSPickingStockService;
import de.linogistix.los.inventory.businessservice.PickingStockUnitTO;
import de.linogistix.los.inventory.businessservice.QueryInventoryBusiness;
import de.linogistix.los.inventory.businessservice.QueryInventoryTO;
import de.linogistix.los.inventory.exception.InventoryException;
import de.linogistix.los.inventory.exception.InventoryExceptionKey;
import de.linogistix.los.inventory.facade.QueryInventoryFacade;
import de.linogistix.los.inventory.model.LOSOrderStrategy;
import de.linogistix.los.inventory.model.LOSReplenishOrder;
import de.linogistix.los.inventory.service.LOSOrderStrategyService;
import de.linogistix.los.inventory.service.QueryLotService;
import de.linogistix.los.inventory.service.QueryStockService;
import de.linogistix.los.location.model.LOSFixedLocationAssignment;
import de.linogistix.los.location.service.QueryFixedAssignmentService;
import de.linogistix.los.util.StringTools;
import de.linogistix.los.util.businessservice.ContextService;
import uk.ltd.mediamagic.los.reference.replenish.MagicReplenishService;

@Stateless
@SecurityDomain("los-login")
@Remote(MagicOrder.class)
@WebService(endpointInterface = "uk.ltd.mediamagic.los.reference.ws.MagicStockQuery")
@SOAPBinding(style = SOAPBinding.Style.RPC, use = SOAPBinding.Use.LITERAL)
@WebContext(contextRoot = "/automagic-mywms",  authMethod="BASIC", transportGuarantee="NONE", secureWSDLAccess=true)
@PermitAll
public class MagicStockQueryBean implements MagicStockQuery {
	Logger log = Logger.getLogger(MagicStockQueryBean.class);

	enum StockCheckResultType {AVAILABLE, WAIT_FOR_REPLENISH, NOT_AVIALABLE_FOR_PICKING, NOT_ENOUGH_STOCK, REMAINING_STOCK_RESERVED};

	@EJB private LOSOrderStrategyService orderStratService;
	@EJB private LOSPickingStockService pickingService;
	@EJB private ContextService contextService;
	@EJB private ClientService clientService;
	@EJB private LOSOrderStrategyService orderStrategyService;
	@EJB private ItemDataService itemService;
	@EJB private LotService lotService;
	@EJB private MagicReplenishService replenishService;

	@EJB private QueryLotService lotQueryService;
    @EJB private QueryInventoryFacade queryInventory;
    @EJB private QueryInventoryBusiness queryInventoryBusiness;
	@EJB private QueryFixedAssignmentService fixedAssignmentService;
	@EJB private QueryStockService queryStockUnitService;

    @Override
    public List<String> getFixedLocations(String clientNumber, String itemNumber) {
		Client client = null;
		if( StringTools.isEmpty(clientNumber) ) {
			client = contextService.getCallersClient();
		}
		else {
			client = clientService.getByNumber(clientNumber);
		}
    	ItemData itemData = itemService.getByItemNumber(client, itemNumber);
    	List<LOSFixedLocationAssignment> fixedAssn = fixedAssignmentService.getByItemData(itemData);
    	List<String> locations = new ArrayList<>();
    	for (LOSFixedLocationAssignment f : fixedAssn) {
    		locations.add(f.getAssignedLocation().getName());
    	}
    	return locations;
    }

	@WebMethod
	public StockCheckResult checkForPicking( String clientNumber, String itemNumber, BigDecimal amount, String lotNumber, String serialNumber, String strategyName) throws FacadeException, InventoryException {
		final String logStr = "checkForPicking";
		Client client = null;
		if( StringTools.isEmpty(clientNumber) ) {
			client = contextService.getCallersClient();
		}
		else {
			client = clientService.getByNumber(clientNumber);
		}

		if( client == null ){
			String msg = "Client does not exist. name="+clientNumber;
			log.error(logStr+msg);
			throw new InventoryException(InventoryExceptionKey.CUSTOM_TEXT, msg);
		}

		LOSOrderStrategy strat = null;
		if( StringTools.isEmpty(strategyName) ) {
			strat = orderStratService.getDefault(client);
		}
		else {
			strat = orderStratService.getByName(client, strategyName);
		}
		if( strat == null ){
			String msg = "OrderStrategy does not exist. name="+strategyName;
			log.error(logStr+msg);
			throw new InventoryException(InventoryExceptionKey.CUSTOM_TEXT, msg);
		}

		ItemData itemData = itemService.getByItemNumber(client, itemNumber);
		Lot lot = lotQueryService.getByNameAndItemData(lotNumber, itemData);
		return checkForPicking(client, itemData, amount, lot, serialNumber, strat);
	}

	@WebMethod
	public boolean requestReplenish(String clientNumber, String itemNumber, BigDecimal amountReq) throws FacadeException {
		Client client = null;
		if (amountReq.compareTo(BigDecimal.ZERO) == 0) amountReq = null;
		if( StringTools.isEmpty(clientNumber) ) {
			client = contextService.getCallersClient();
		}
		else {
			client = clientService.getByNumber(clientNumber);
		}

		if( client == null ){
			String msg = "Client does not exist. name="+clientNumber;
			log.error("requestReplenish " + msg);
			throw new InventoryException(InventoryExceptionKey.CUSTOM_TEXT, msg);
		}

		ItemData itemData = itemService.getByItemNumber(client, itemNumber);
		if (itemData == null) {
			log.warn("requestReplenish cannot find item " + itemNumber);
			return false;
		}
		LOSReplenishOrder order = replenishService.replenishItem(itemData, amountReq);

		if (order == null) {
			return false;
		}
		else {
			order.setPrio(25);
			return true;
		}
	}

	private StockCheckResult checkForPicking( Client client, ItemData itemData, BigDecimal amount, Lot lot, String serialNumber, LOSOrderStrategy strategy) throws FacadeException, InventoryException {
		final String logStr = "checkForPicking";

		List<PickingStockUnitTO> stockList = pickingService.readPickFromStockList(client, itemData, lot, serialNumber, null, strategy, false);

		if( strategy == null ) {
			strategy = orderStratService.getDefault(client);
		}

		if( amount == null ) {
			log.error(logStr+"No amount given. Cannot generate picks");
			return new StockCheckResult(itemData.getNumber(), BigDecimal.ZERO, StockCheckResultType.AVAILABLE);
		}

		BigDecimal amountToPick = amount;
		while( BigDecimal.ZERO.compareTo(amountToPick)<0 ) {
			PickingStockUnitTO stock = pickingService.findPickFrom(itemData, lot, amountToPick, strategy, stockList);
			if( stock == null ) {
				if (stockList.isEmpty()) {
					if (replenishService.isBeingReplenished(itemData)) {
						log.error(logStr+" " + StockCheckResultType.WAIT_FOR_REPLENISH);
						return new StockCheckResult(itemData.getNumber(), amountToPick,
								StockCheckResultType.WAIT_FOR_REPLENISH);
					}
					else {
						Map<String, QueryInventoryTO> amountInStock = queryInventoryBusiness.getInvMap(client, lot, itemData, true, true);
						if (amountInStock.size() == 0) {
							log.error(logStr+" " + StockCheckResultType.NOT_ENOUGH_STOCK);
							return new StockCheckResult(itemData.getNumber(), amount.subtract(amountToPick),
								StockCheckResultType.NOT_ENOUGH_STOCK);
						}
						else {
							BigDecimal stockOnHand = BigDecimal.ZERO;
							for (QueryInventoryTO i :amountInStock.values()) {
								stockOnHand = stockOnHand.add(i.available);
							}
							
							if (stockOnHand.compareTo(amount) < 0) {
								log.error(logStr+" " + StockCheckResultType.NOT_ENOUGH_STOCK);
								return new StockCheckResult(itemData.getNumber(), amount.subtract(amountToPick),
									StockCheckResultType.NOT_ENOUGH_STOCK);								
							}
							else {
								log.error(logStr+" " + StockCheckResultType.NOT_AVIALABLE_FOR_PICKING);
								return new StockCheckResult(itemData.getNumber(), amountToPick,
										StockCheckResultType.NOT_AVIALABLE_FOR_PICKING);
							}
						}
					}
				}
				else {
					log.error(logStr+" " + StockCheckResultType.REMAINING_STOCK_RESERVED);
					return new StockCheckResult(itemData.getNumber(), amountToPick, StockCheckResultType.REMAINING_STOCK_RESERVED);
				}
			}

			BigDecimal amountPicked = amountToPick;
			BigDecimal amountStock = stock.amountAvailable;
			if( amountPicked.compareTo(amountStock)>0 ) {
				amountPicked = amountStock;
			}

			stockList.remove(stock);

			amountToPick = amountToPick.subtract(amountPicked);
		}
		return new StockCheckResult(itemData.getNumber(), amount, StockCheckResultType.AVAILABLE);
	}

	@Override
	public List<LotCheckResult> getLots(String clientNumber, String itemNumber) throws FacadeException {
		Client client = null;
		if( StringTools.isEmpty(clientNumber) ) {
			client = contextService.getCallersClient();
		}
		else {
			client = clientService.getByNumber(clientNumber);
		}

		List<LotCheckResult> result = new ArrayList<LotCheckResult>();
		ItemData item = itemService.getByItemNumber(client, itemNumber);
		for (Lot lot : lotService.getListByItemData(item)) {
			result.add(new LotCheckResult(lot.getItemData().getName(), lot.getName(), lot.getBestBeforeEnd()));
		}
		return result;
	}

	@Override
	public List<StockUnitResult> getUnitLoads(String clientNumber, String itemNumber) throws FacadeException {
		Client client = null;
		if( StringTools.isEmpty(clientNumber) ) {
			client = contextService.getCallersClient();
		}
		else {
			client = clientService.getByNumber(clientNumber);
		}

		List<StockUnitResult> result = new ArrayList<StockUnitResult>();
		ItemData item = itemService.getByItemNumber(client, itemNumber);

		for (QueryInventoryTO inv : queryInventory.getInventoryByArticle(clientNumber, itemNumber, false, true)) {
			StockUnitResult r = new StockUnitResult(inv.lotRef, inv.articleRef);
			r.setInStock(inv.inStock);
			r.setAvailable(inv.available);
			r.setReserved(inv.reserved);
			r.setLot(inv.lotRef);

			Lot lot = lotQueryService.getByNameAndItemData(inv.lotRef, item);
			if (lot != null) {
				r.setBestBefore(lot.getBestBeforeEnd());
			}
			result.add(r);
		}
		return result;
	}

	//@Override
	public List<QueryInventoryTO> getStockInventroy(String clientNumber, boolean consolidateLot) throws FacadeException {
		return Arrays.asList(queryInventory.getInventoryList(clientNumber, consolidateLot, true));
	}


}