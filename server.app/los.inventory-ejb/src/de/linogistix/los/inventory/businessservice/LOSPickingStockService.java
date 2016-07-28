/*
 * Copyright (c) 2009-2012 LinogistiX GmbH
 *
 *  www.linogistix.com
 *
 *  Project myWMS-LOS
 */
package de.linogistix.los.inventory.businessservice;

import java.math.BigDecimal;
import java.util.List;

import javax.ejb.Local;

import org.mywms.facade.FacadeException;
import org.mywms.model.Client;
import org.mywms.model.ItemData;
import org.mywms.model.Lot;
import org.mywms.model.StockUnit;

import de.linogistix.los.inventory.model.LOSOrderStrategy;

/**
 * Strategy Service.<br>
 * Find stocks for goods out.
 *
 * @author krane
 *
 */
@Local
public interface LOSPickingStockService {


	/**
	 * Reads a list of stock units usable for goods out.
	 *
	 * @param client
	 * @param itemData
	 * @param lot
	 * @param serialNumber
	 * @param reservationNumber
	 * @param strategy
	 * @param readReserved
	 * @return
	 */
	public List<StockUnit> getPickFromStockList( Client client, ItemData itemData, Lot lot, String serialNumber, String reservationNumber, LOSOrderStrategy strategy, boolean readReserved );

	/**
	 * Read the next suitable stock unit for picking.<br>
	 *
	 * @param client If null the callers client is used
	 * @param itemData Mandatory
	 * @param lot If null, any lot is valid
	 * @param amount
	 * @param serialNumber
	 * @param reservationNumber
	 * @param strategy
	 * @return
	 * @throws FacadeException
	 */
	public StockUnit findPickFromStock( Client client, ItemData itemData, Lot lot, BigDecimal amount, String serialNumber, String reservationNumber, LOSOrderStrategy strategy ) throws FacadeException;

	/**
	 * Reads a list of stock units suitable for picking..
	 *
	 * This method is the part of {@link #findPickFromStock(Client, ItemData, Lot, BigDecimal, String, String, LOSOrderStrategy)}
	 * that queries the database for a list of suitable stock units to pick from.
	 *
	 * @param client If null the callers client is used
	 * @param itemData the itemData required, mandatory
	 * @param lot the lot number. If null any lot is valid
	 * @param serialNumber
	 * @param reservationNumber
	 * @param strategy the picking strategy
	 * @param readReserved false reserved stock is ignored.
	 * @return
	 */
	public List<PickingStockUnitTO> readPickFromStockList( Client client, ItemData itemData, Lot lot, String serialNumber, String reservationNumber, LOSOrderStrategy strategy, boolean readReserved);

	/**
	 * Selects a suitable picking stock unit from a list of possible stock units.
	 *
	 * This method is the part of {@link #findPickFromStock(Client, ItemData, Lot, BigDecimal, String, String, LOSOrderStrategy)}
	 * that selects which stock unit to pick from the query.
	 *
	 * @param itemData the itemData required, mandatory
	 * @param lot the lot number. If null any lot is valid
	 * @param amount the amount required
	 * @param strategy the picking strategy
	 * @param stockList the list of stock to choose from.
	 * @return
	 * @throws FacadeException
	 */
	public PickingStockUnitTO findPickFrom(ItemData itemData, Lot lot, BigDecimal amount, LOSOrderStrategy strategy, List<PickingStockUnitTO> stockList) throws FacadeException;

}
