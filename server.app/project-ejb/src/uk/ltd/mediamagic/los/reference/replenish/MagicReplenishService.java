package uk.ltd.mediamagic.los.reference.replenish;

import java.math.BigDecimal;

import javax.ejb.Local;

import org.mywms.facade.FacadeException;
import org.mywms.model.ItemData;

import de.linogistix.los.inventory.model.LOSReplenishOrder;
import de.linogistix.los.location.model.LOSStorageLocation;

/**
 * A modified replenish service that is used to trigger replenish task 
 * 
 * @author slim
 *
 */
@Local
public interface MagicReplenishService {

	/**
	 * Replenish the item data.
	 * If more than one fixed location exists a location is selected.  
	 * We do not contract which location is selected.
	 * 
	 * If this method returns null then it is the case that non of the fixed
	 * locations require replenish.  i.e. if any of the fixed locations needs
	 * replenishing.
	 * 
	 * This method may behave differently if the amountReqired is provided.
	 * with an amount may trigger a replenish when a null value might not.
	 * 
	 * If the <code>amountRequired</code> is will force the method to insure
	 * that the amount is available on the picking location.  If the amount required
	 * is more than the capacity of the location the location will be fill to its maximum.
	 * 
	 * @param itemData the item data.
	 * @param amountReqired the amount required at one of the pick locations.  If null the system will 
	 * determine the amount to replenish.
	 * @return the replenish order object generated.
	 * @throws FacadeException
	 */
	LOSReplenishOrder replenishItem(ItemData itemData, BigDecimal amountReqired) throws FacadeException;

	/**
	 * Replenish the storage location.
	 * If the location provided is not a fixed location then this method can ignore the request.
	 * 
	 * This method may behave differently if the amountReqired is provided.
	 * with an amount may trigger a replenish where a null value may not.
	 * 
	 * If the <code>amountRequired</code> is will force the method to insure
	 * that the amount is available on the picking location.  If the amount required
	 * is more than the capacity of the location the location will be filled to its maximum.
	 * 
	 * @param location the location to replenish
	 * @param amountReqired the amount required at one of the pick locations.  If null the system will 
	 * determine the amount to replenish.
	 * @return the replenish order object generated null if no replenish was needed or no stock could be found.
	 * @throws FacadeException
	 */
	LOSReplenishOrder replenishItem(LOSStorageLocation location, BigDecimal amountReqired) throws FacadeException;

	
	/**
	 * returns true if the is an active replenish order for this item.
	 * @param itemData
	 * @return
	 */
	public boolean isBeingReplenished(ItemData itemData);

}