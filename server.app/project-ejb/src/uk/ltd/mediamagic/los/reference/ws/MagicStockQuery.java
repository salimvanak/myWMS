package uk.ltd.mediamagic.los.reference.ws;

import java.math.BigDecimal;
import java.util.List;

import javax.ejb.Remote;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;

import org.mywms.facade.FacadeException;

import de.linogistix.los.inventory.exception.InventoryException;

@Remote
@WebService
@SOAPBinding(style = SOAPBinding.Style.RPC, use = SOAPBinding.Use.LITERAL)
public interface MagicStockQuery {

	/**
	 * Returns a list of fixed location for the given client and itemNumber.
	 * @param clientNumber the clientNumber, usually "System"
	 * @param itemNumber the item number.
	 * @return a list of fixed location names.
	 */
	@WebMethod
	public List<String> getFixedLocations(
			@WebParam(name = "clientNumber") String clientNumber, 
			@WebParam(name = "itemNumber") String itemNumber); 
	
	@WebMethod
	public StockCheckResult checkForPicking( 
			@WebParam(name = "clientNumber") String clientNumber, 
			@WebParam(name = "itemNumber") String itemNumber, 
			@WebParam(name = "amount") BigDecimal amount, 
			@WebParam(name = "lotNumber") String lotNumber, 
			@WebParam(name = "serialNumber") String serialNumber,
			@WebParam(name = "strategy") String strategyName) throws FacadeException, InventoryException;

	@WebMethod
	public boolean requestReplenish(
			@WebParam(name = "clientNumber") String clientNumber, 
			@WebParam(name = "itemNumber") String itemNumber, 
			@WebParam(name = "amount") BigDecimal amount ) throws FacadeException;		


	@WebMethod
    public List<LotCheckResult> getLots(@WebParam( name="clientNumber") String clientNumber, @WebParam( name="itemNumber") String itemNumber) throws FacadeException;    

	@WebMethod
    public List<StockUnitResult> getUnitLoads(@WebParam( name="clientNumber") String clientNumber, @WebParam( name="itemNumber") String itemNumber) throws FacadeException;
	
	
}
