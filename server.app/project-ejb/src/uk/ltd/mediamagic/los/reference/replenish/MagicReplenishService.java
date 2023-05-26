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
	 * No assertion is made as to which location is selected if there are multiple fixed
	 * locations to choose from.
	 * 
	 * This method will return null if none of the fixed location require replenishment.
	 * 
	 * If the <code>amountRequired</code> is null, the replenish is only requested if the 
	 * is required.
	 * 
	 * If the <code>amountRequired</code> is not null, the method to insure
	 * that the <code>amountRequired</code> is available on the picking locations.  
	 * If the <code>amountRequired</code> required is more than the capacity of the location 
	 * the location will be filled to its maximum.
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
	 * If the location provided is not a fixed location then this method will ignore the request.
	 * 
	 * If the <code>amountRequired</code> is null, the replenish is only requested if the 
	 * is required.
	 * 
	 * If the <code>amountRequired</code> is not null, the method to insure
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