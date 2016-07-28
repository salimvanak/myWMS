package uk.ltd.mediamagic.los.reference.replenish;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.log4j.Logger;
import org.mywms.facade.FacadeException;
import org.mywms.model.ItemData;
import org.mywms.model.StockUnit;

import de.linogistix.los.inventory.businessservice.LOSInventoryComponent;
import de.linogistix.los.inventory.businessservice.LOSReplenishGenerator;
import de.linogistix.los.inventory.businessservice.LOSReplenishStockService;
import de.linogistix.los.inventory.exception.InventoryException;
import de.linogistix.los.inventory.model.LOSReplenishOrder;
import de.linogistix.los.inventory.service.LOSReplenishOrderService;
import de.linogistix.los.location.model.LOSFixedLocationAssignment;
import de.linogistix.los.location.model.LOSStorageLocation;
import de.linogistix.los.location.service.QueryFixedAssignmentService;

@Stateless
public class MagicReplenishServiceBean implements MagicReplenishService {
	Logger log = Logger.getLogger(MagicReplenishServiceBean.class);

	@EJB
	private LOSReplenishOrderService replemishOrderService;
	@EJB
	private LOSReplenishStockService replenishStockService;
	@EJB
	private LOSReplenishGenerator orderGenerator;
	@EJB
	private LOSInventoryComponent inventoryBusiness;
	@EJB
	private QueryFixedAssignmentService fixService;

	
	public LOSReplenishOrder replenishItem(LOSStorageLocation loc, BigDecimal amountReq) throws FacadeException {
		LOSFixedLocationAssignment fixed = fixService.getByLocation(loc);
		if (fixed == null) return null;
		BigDecimal desiredAmount = fixed.getDesiredAmount();
		// only replenish if the desired amount at that location is greater than 1;
		if (desiredAmount == null || desiredAmount.compareTo(BigDecimal.ONE) <= 0) return null;
		return replenishItem(fixed, amountReq);
	}

	public boolean isBeingReplenished(ItemData itemData) {
		List<?> active = replemishOrderService.getActive(itemData, null, null, null);
		return (active != null && active.size() > 0);	
	}
	
	public LOSReplenishOrder replenishItem(LOSFixedLocationAssignment ass, BigDecimal amountReq) throws InventoryException, FacadeException {
		// if there is an active order do nothing.
		List<LOSReplenishOrder> active = replemishOrderService.getActive(ass.getItemData(), null, ass.getAssignedLocation(), null);
		if (active != null && active.size() > 0) {
			log.info("replenishItem There is already replenishment ordered for location " + ass.getAssignedLocation().getName());
			return active.get(0);
		}

		
		BigDecimal amountAtLoc = inventoryBusiness.getAmountOfStorageLocation(ass.getItemData(), ass.getAssignedLocation());
		BigDecimal amountToMax = ass.getDesiredAmount().subtract(amountAtLoc);
		BigDecimal d = ass.getDesiredAmount().multiply(new BigDecimal(0.25));
		if( amountAtLoc.compareTo(d) >= 0 ) {
			log.info("There is still enough material on location " + ass.getAssignedLocation().getName());
			return null;
		}
		
		if (amountReq == null || amountReq.compareTo(BigDecimal.ZERO) == 0) { 
			// the the user did not request a specific amount
			StockUnit u = replenishStockService.findReplenishStock(ass.getItemData(), null, null, Collections.singleton(ass.getAssignedLocation()));
			if (u == null) return null;
			// the the UL will fit but the fix location is still too full to hold the UL, we wait.
			if (u.getAvailableAmount().compareTo(ass.getDesiredAmount()) <= 0 
					&& u.getAvailableAmount().compareTo(amountToMax) > 0) {
				return null;
			}
		}
		else {
			if (amountReq.compareTo(amountToMax) > 0) {
				amountReq = amountToMax;
			}
		}
		
		LOSReplenishOrder order = orderGenerator.calculateOrder(ass.getItemData(), null, amountReq, ass.getAssignedLocation(), null);
		return order;
	}

	/* (non-Javadoc)
	 * @see uk.ltd.mediamagic.los.reference.replenish.MagicReplenishService#replenishItem(org.mywms.model.ItemData, java.math.BigDecimal)
	 */
	@Override
	public LOSReplenishOrder replenishItem(ItemData itemData, BigDecimal amountReq) throws InventoryException, FacadeException {
		List<LOSFixedLocationAssignment> fixed = fixService.getByItemData(itemData);
		
		LOSReplenishOrder order = null;
		// first try to fill the bay
		for (LOSFixedLocationAssignment ass : fixed) {
			log.info("Check location " + ass.getAssignedLocation().getName());
			order = replenishItem(ass, null);
			if (order != null) break;
		}
		if (order == null && (amountReq != null && amountReq.compareTo(BigDecimal.ZERO) > 0)) {
			// now force replenish for the specific amount
			for (LOSFixedLocationAssignment ass : fixed) {
				log.info("Check location " + ass.getAssignedLocation().getName() + " amount " + amountReq);
				order = replenishItem(ass, amountReq);
				if (order != null) {
					break;
				}
			}			
		}
		return order;
	}
}

