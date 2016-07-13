/*
 * Copyright (c) 2012 LinogistiX GmbH
 *
 *  www.linogistix.com
 *
 *  Project myWMS-LOS
 */
package de.linogistix.los.inventory.businessservice;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.mywms.facade.FacadeException;
import org.mywms.model.Client;
import org.mywms.model.ItemData;
import org.mywms.model.StockUnit;
import org.mywms.model.Zone;
import org.mywms.service.ClientService;

import de.linogistix.los.inventory.exception.InventoryException;
import de.linogistix.los.inventory.exception.InventoryExceptionKey;
import de.linogistix.los.inventory.model.LOSPickingUnitLoad;
import de.linogistix.los.inventory.model.LOSStorageRequest;
import de.linogistix.los.inventory.model.LOSStorageRequestState;
import de.linogistix.los.inventory.model.LOSStorageStrategy;
import de.linogistix.los.inventory.service.LOSPickingUnitLoadService;
import de.linogistix.los.inventory.service.LOSStorageRequestService;
import de.linogistix.los.inventory.service.LOSStorageStrategyService;
import de.linogistix.los.location.businessservice.LocationReserver;
import de.linogistix.los.location.constants.LOSStorageLocationLockState;
import de.linogistix.los.location.entityservice.LOSStorageLocationTypeService;
import de.linogistix.los.location.entityservice.LOSUnitLoadService;
import de.linogistix.los.location.exception.LOSLocationAlreadyFullException;
import de.linogistix.los.location.exception.LOSLocationNotSuitableException;
import de.linogistix.los.location.exception.LOSLocationReservedException;
import de.linogistix.los.location.exception.LOSLocationWrongClientException;
import de.linogistix.los.location.model.LOSArea;
import de.linogistix.los.location.model.LOSFixedLocationAssignment;
import de.linogistix.los.location.model.LOSStorageLocation;
import de.linogistix.los.location.model.LOSStorageLocationType;
import de.linogistix.los.location.model.LOSTypeCapacityConstraint;
import de.linogistix.los.location.model.LOSUnitLoad;
import de.linogistix.los.location.service.QueryFixedAssignmentService;

/**
 * Strategy Service,
 * Find a storage location for a unit load.
 *
 * @author krane
 *
 * This has been altered to allow for a target location to be calculated.
 * The bean then will look for the closest location to the target.
 *
 * The default implementation of "closest" uses the XPos, YPos, ZPos of {@link LOSStorageLocation}.
 * where (XPos, ZPos) is a point on the floor and YPos is the vertical distance from the floor (the level).
 * Locations within the same zone are considered closer than locations in different zones.
 *
 * @author Salim Vanak
 *
 */
@Stateless
public class LocationFinderBean implements LocationFinder {
	private static final Logger log = Logger.getLogger(LocationFinderBean.class);

	@EJB
	private LOSPickingUnitLoadService pickingUnitLoadService;
	@EJB
	private QueryFixedAssignmentService fixedAssignmentService;
	@EJB
	private ClientService clientService;
	@EJB
	private LOSStorageLocationTypeService storageLocationTypeService;
	@EJB
	private LocationReserver locationReserver;
	@EJB
	private LOSStorageRequestService storageRequestService;
	@EJB
	private LOSStorageStrategyService strategyService;
	@EJB
	private LOSUnitLoadService unitLoadService;

	@PersistenceContext(unitName = "myWMS")
	private EntityManager manager;

	private boolean isPreferOwnClient() {
		return true;
	}

	public LOSStorageLocation findLocation( LOSUnitLoad unitLoad ) throws FacadeException {


		return findLocation(unitLoad, null, null);
	}

	public LOSStorageLocation findPickingLocation( LOSUnitLoad unitLoad, Zone zone, LOSStorageStrategy strategy ) throws FacadeException {
		return findLocation( unitLoad, zone, strategy, true );
	}

	public LOSStorageLocation findLocation( LOSUnitLoad unitLoad, Zone zone, LOSStorageStrategy strategy ) throws FacadeException {
		return findLocation( unitLoad, zone, strategy, false );
	}

	public LOSStorageLocation findLocation( LOSUnitLoad unitLoad, Zone zone, LOSStorageStrategy strategy, boolean pickingOnly ) throws FacadeException {
		String logStr = "findLocation ";
		Date dateStart = new Date();

		// Initialize unit load data
		BigDecimal weightUnitLoad = readUnitLoadWeight(unitLoad);

		if( strategy == null ) {
			strategy = strategyService.getDefault();
		}
		if( strategy == null ) {
			log.error(logStr+"No strategy defined. Cannot find location");
			throw new InventoryException(InventoryExceptionKey.STORAGE_STRATEGY_UNDEFINED, "");
		}


		// Initialize item data
		// This is only needed for strategies, which try not to mix different items
		ItemData itemData = null;
		if( ! strategy.isMixItem() ) {
			// Take the first item. If the unit load is mixed, it will not find a really good location with this strategy
			for( StockUnit su : unitLoad.getStockUnitList() ) {
				itemData = su.getItemData();
				break;
			}
		}


		LOSStorageLocation targetLocation = null;

		if( zone == null ) {
			if( strategy.isUseItemZone() ) {
				zone = readUnitLoadZone(unitLoad);
				targetLocation = findTargetLocationLocation(unitLoad, zone);
				if (targetLocation != null) {
					log.info(logStr + " looking for location near "+ targetLocation.getName() +" for " + unitLoad.getLabelId());
				}
			}
			else {
				zone = strategy.getZone();
			}
		}


		// Initialize clients
		Client clientSys = clientService.getSystemClient();
		Client clientStock = unitLoad.getClient();
		List<Client> clientListAll = new ArrayList<Client>();
		clientListAll.add(clientStock);
		List<Client> clientListPreferred = new ArrayList<Client>();
		if( !clientStock.equals(clientSys) ) {
			if( isPreferOwnClient() ) {
				if( existsClientLocations( clientStock ) ) {
					clientListPreferred.add( clientStock );
				}
			}
			clientListAll.add( clientSys );
		}



		LOSStorageLocation location = null;


		if( clientListPreferred.size()>0 ) {
			location = searchLocationOfClient( clientListPreferred, unitLoad, strategy, itemData, zone, targetLocation, pickingOnly, weightUnitLoad );
			if( location != null ) {
				log.debug(logStr+"Found client location "+location.getName()+" in "+(new Date().getTime()-dateStart.getTime())+" ms");
				return location;
			}
		}

		location = searchLocationOfClient( clientListAll, unitLoad, strategy, itemData, zone, targetLocation, pickingOnly, weightUnitLoad );
		if( location != null ) {
			log.debug(logStr+"Found location "+location.getName()+" in "+(new Date().getTime()-dateStart.getTime())+" ms");
			return location;
		}


		log.debug(logStr+"found nothing in "+(new Date().getTime()-dateStart.getTime())+" ms");
		return null;
	}

	/**
	 * Returns a target location for the unit load.
	 * For example, this might be the fixed location of a product or the target location of a customer order
	 * if there is no target location the unit load the method will return the first target location found for
	 * one of its child unit loads.
	 * @param unitLoad the unit load
	 * @param zone the zone within which to find a target.
	 * @return the target location of this unit load or null if no target location is found.
	 */
	protected LOSStorageLocation findTargetLocationLocation(LOSUnitLoad unitLoad, Zone zone) {
		LOSPickingUnitLoad pickingLoad = pickingUnitLoadService.getByUnitLoad(unitLoad);
		if (pickingLoad != null) { // if it is a picking load place it near the destination
			return pickingLoad.getPickingOrder().getDestination();
		}
		// otherwise look for a fixed location.
		for( StockUnit su : unitLoad.getStockUnitList() ) {
			List<LOSFixedLocationAssignment> locations = fixedAssignmentService.getByItemData(su.getItemData());
			if (locations != null && !locations.isEmpty()) {
				for (LOSFixedLocationAssignment l : locations) {
					if (zone == null || zone.equals(l.getAssignedLocation().getZone())) {
						return l.getAssignedLocation();
					}
				}
			}
		}

		List<LOSUnitLoad> childs = unitLoadService.getChilds(unitLoad);
		for( LOSUnitLoad child : childs ) {
			LOSStorageLocation storageLocation = findTargetLocationLocation(child, zone);
			if (storageLocation != null) return storageLocation;
		}

		// no target location was found.
		return null;
	}

	/**
	 * see {@link #searchLocation(Collection, LOSUnitLoad, LOSStorageStrategy, ItemData, Zone, LOSStorageLocation, boolean, BigDecimal)}
	 */
	private LOSStorageLocation searchLocationOfClient( Collection<Client> clients, LOSUnitLoad unitLoad, LOSStorageStrategy strategy, ItemData itemData, Zone zone, LOSStorageLocation near, boolean pickingOnly, BigDecimal weightUnitLoad ) throws FacadeException{
		LOSStorageLocation location = null;

		location = searchLocation( clients, unitLoad, strategy, itemData, zone, near, pickingOnly, weightUnitLoad );
		if( location != null ) {
			return location;
		}

		return null;
	}


	/**
	 * Searches for a location suitable for sorting the given unit load.
	 * This method will return a location from an active storage request if one exists.
	 * @param clients
	 * @param unitLoad the unit load to be stored.
	 * @param strategy the {@link LOSStorageStrategy} to use
	 * @param itemData the item data on the unit load
	 * @param zone the zone within which to store the unit load
	 * @param targetLocation the location to be used as the origin for geometric distance ordering.
	 * @param pickingOnly true is only picking location should be considered
	 * @param weight the require weight for storage.
	 * @return a suitable location.
	 * @throws FacadeException if an error occurs.
	 */
	private LOSStorageLocation searchLocation( Collection<Client> clients, LOSUnitLoad unitLoad, LOSStorageStrategy strategy, ItemData itemData, Zone zone, LOSStorageLocation targetLocation, boolean pickingOnly, BigDecimal weight ) throws FacadeException{
		String logStr = "searchNearLocation ";
		int startSearchIndex = 0;


		List<Integer> locks = new ArrayList<Integer>();
		locks.add(LOSStorageLocationLockState.NOT_LOCKED.getLock());
		locks.add(LOSStorageLocationLockState.RETRIEVAL.getLock());


		// Do not read all. Usually one of the first location is suitable
		while(true) {

			List<Object[]> locationList = null;
			if (zone == null && targetLocation != null) {
				zone = targetLocation.getZone();
			}
			locationList = getLocationList( clients, locks, unitLoad, strategy, zone, targetLocation, pickingOnly, weight, startSearchIndex );


			for( Object[] o : locationList ) {

				LOSStorageLocation location = manager.find(LOSStorageLocation.class, o[0]);
				if( location == null ) {
					continue;
				}

				// Check storage requests for strategies which require unique item data on a location
				if( !strategy.isMixItem() && itemData != null ) {
					if( existsDiffItem( location, itemData ) ) {
						log.warn(logStr+"Not allowed to mix items");
						continue;
					}
				}

				// Check weight
				BigDecimal liftingCapacity = location.getType().getLiftingCapacity();
				if( liftingCapacity != null && liftingCapacity.compareTo(BigDecimal.ZERO)>0 ) {

					BigDecimal weightLoc = (weight == null ? BigDecimal.ZERO : weight);
					for( LOSUnitLoad ulLoc : location.getUnitLoads() ) {
						if( ulLoc.getWeight() != null ) {
							weightLoc = weightLoc.add( ulLoc.getWeight() );
						}
					}

					if( BigDecimal.ZERO.compareTo(location.getAllocation())<0 ) {
						List<LOSStorageRequest> reqList = storageRequestService.getActiveListByDestination(location);
						for( LOSStorageRequest req : reqList ) {
							LOSUnitLoad ulStorage = req.getUnitLoad();
							if( ! unitLoad.equals(ulStorage) ) {
								BigDecimal weightUnitLoad = readUnitLoadWeight(ulStorage);
								if( weightUnitLoad != null ) {
									weightLoc = weightLoc.add( weightUnitLoad );
								}
							}
						}
					}
					if( weightLoc.compareTo(liftingCapacity)>0 ) {
						log.debug(logStr+"Too much wieght for location. name="+location.getName());
						continue;
					}
				}

				try {
					locationReserver.checkAllocateLocation(location, unitLoad, false);
				} catch(LOSLocationAlreadyFullException e){
					log.debug(logStr+"Location not usable. LOSLocationAlreadyFullException="+e.getMessage());
					continue;
				} catch(LOSLocationNotSuitableException e) {
					log.debug(logStr+"Location not usable. LOSLocationNotSuitableException="+e.getMessage());
					continue;
				} catch(LOSLocationReservedException e) {
					log.debug(logStr+"Location not usable, LOSLocationReservedException="+e.getMessage());
					continue;
				} catch(LOSLocationWrongClientException e) {
					log.debug(logStr+"Location not usable, LOSLocationWrongClientException="+e.getMessage());
					continue;
				}


				return location;
			}

			if( locationList.size()<10 ) {
				break;
			}
			startSearchIndex += 10;
		}

		return null;
	}


	/**
	 * Gets a list of locations that match the given criteria.
	 * The list is ordered and will return a maximum of 10 results. The idxStart can be used to collect more locations.
	 *
	 * The location ordering is as follows.
	 * <ul>
	 * 	<li>
	 * 		<b>if the <code>targetLocation</code> is set and the {@link LOSStorageStrategy.isUseItemZone()} is true:</b><br/>
	 * 		The search prefers to find a location with the same zone as the target location
	 * 		The location order is the (x,y,z) distance from the near location.
	 *      If the near location in within a zone only locations within the same zone will be considered.
	 * </li>
	 *
	 * <li>
	 * 		<b>if the <code>targetLocation</code> location is null.</b><br/>
	 * 		The location ordering is that set in {@link LOSStorageStrategy.getOrderMode()}
	 * </li>
	 *
	 * </ul>
	 *
	 * @param clients only locations for clients in this list.
	 * @param locks only locations whos lock status is contained in this list.
	 * @param unitLoad only locations that can sort this unit load
	 * @param strategy the storage strategy to use
	 * @param zone only locations in this zone, null indicates all zones
	 * @param near when not null, order the location by distance from this location
	 * @param pickingOnly if true only picking locations are selected
	 * @param weight only locations capable of holding this weight
	 * @param idxStart the number of locations to skip at the top of the query.
	 * @return
	 * @throws FacadeException
	 */
	@SuppressWarnings("unchecked")
	private List<Object[]> getLocationList( Collection<Client> clients, Collection<Integer> locks, LOSUnitLoad unitLoad, LOSStorageStrategy strategy, Zone zone, LOSStorageLocation near, boolean pickingOnly, BigDecimal weight, int idxStart ) throws FacadeException{
		log.setLevel(Level.DEBUG);
		String logStr = "getLocationList ";
		String paramLog = "";

		StringBuilder sb = new StringBuilder();
		sb.append(" SELECT loc.id, loc.YPos, loc.XPos, loc.name  FROM ");
		sb.append(LOSStorageLocation.class.getSimpleName()+" loc, ");
		sb.append(LOSStorageLocationType.class.getSimpleName()+" locType, ");
		sb.append(LOSArea.class.getSimpleName()+" area, ");
		sb.append(LOSTypeCapacityConstraint.class.getSimpleName()+" constraint ");
		sb.append(" WHERE loc.type = locType and loc.area=area");
		sb.append(" AND constraint.unitLoadType=:unitLoadType and constraint.storageLocationType=locType");
		sb.append(" AND locType!=:fixedType ");
		sb.append(" AND loc.allocation<100 ");
		sb.append(" AND loc.lock in (:lockList) ");
		sb.append(" AND loc.client in (:clientList) ");

		if( pickingOnly ) {
			sb.append(" AND area.useForPicking=true");
		}
		else {
			if( strategy.getUsePicking() != LOSStorageStrategy.UNDEFINED ) {
				sb.append(" AND area.useForPicking="+(strategy.getUsePicking() == LOSStorageStrategy.TRUE));
			}
			if( strategy.getUseStorage() != LOSStorageStrategy.UNDEFINED ) {
				sb.append(" AND area.useForStorage="+(strategy.getUseStorage() == LOSStorageStrategy.TRUE));
			}
		}
		if( weight != null ) {
			sb.append(" AND (locType.liftingCapacity is null or locType.liftingCapacity >= :weight) ");
		}

		if( zone != null ) {
			sb.append(" AND loc.zone=:zone ");
		}

		if( !strategy.isMixClient() ) {
			sb.append(" AND NOT EXISTS (select 1 from "+LOSUnitLoad.class.getSimpleName()+" otherUnitLoad ");
			sb.append("   WHERE otherUnitLoad.client!=:stockClient and otherUnitLoad.storageLocation=loc ");
			sb.append(" ) ");
			sb.append(" AND NOT EXISTS (select 1 from "+LOSStorageRequest.class.getSimpleName()+" otherStorageRequest ");
			sb.append("   WHERE otherStorageRequest.client!=:stockClient and otherStorageRequest.destination=loc and otherStorageRequest.requestState in (:srStateRaw, :srStateProcessing) ");
			sb.append(" ) ");
		}

		sb.append(" AND (");
		sb.append("    (loc.currentTypeCapacityConstraint is null) ");
		sb.append(" or exists( select 1 from "+LOSTypeCapacityConstraint.class.getSimpleName()+" cc1");
		sb.append("            where cc1=loc.currentTypeCapacityConstraint and cc1.allocationType=:typePercentage ) ");
		sb.append(" or exists( select 1 from "+LOSTypeCapacityConstraint.class.getSimpleName()+" cc1");
		sb.append("            where cc1=loc.currentTypeCapacityConstraint and cc1.allocationType=:typeUnitLoadType and cc1.unitLoadType=:unitLoadType ) ");
		sb.append(" ) ");

		sb.append(" AND ( ");
		sb.append("     (constraint.allocationType=:typeUnitLoadType and constraint.allocation>0 and loc.allocation<=(100-constraint.allocation) ) ");
		sb.append("  or (constraint.allocationType=:typePercentage and constraint.allocation>0 and loc.allocation<=(100-constraint.allocation) ) ");
		sb.append(" ) ");

		if (near != null) {
			sb.append(" AND (loc.name != :nearLocationName) ");
		}

		if (near != null) {
			if (near.getZone() != null) {
				sb.append(" ORDER BY (case when loc.zone = :nearLocationZone then 1 else 0 end), abs(loc.XPos - :nearLocationX), abs(loc.ZPos - :nearLocationZ), loc.YPos, loc.name, loc.id ");
			}
			else {
				sb.append(" ORDER BY abs(loc.XPos - :nearLocationX), abs(loc.ZPos - :nearLocationZ), loc.YPos, loc.name, loc.id ");
			}
		}
		else {
			if( strategy.getOrderByMode() == LOSStorageStrategy.ORDER_BY_XPOS ) {
				sb.append(" ORDER BY loc.XPos, loc.YPos, loc.name, loc.id ");
			}
			else {
				sb.append(" ORDER BY loc.YPos, loc.XPos, loc.name, loc.id ");
			}
		}

		LOSStorageLocationType fixedType = storageLocationTypeService.getAttachedUnitLoadType();

		log.info(logStr+"Search location Query="+sb.toString());
		Query query = manager.createQuery(sb.toString());

		query.setParameter("unitLoadType", unitLoad.getType());
		paramLog += ", unitLoadType="+unitLoad.getType();
		query.setParameter("fixedType", fixedType);
		paramLog += ", fixedType="+fixedType;
		query.setParameter("clientList", clients);
		paramLog += ", clientList="+clients;
		query.setParameter("lockList", locks);
		paramLog += ", lockList="+locks;

		if( !strategy.isMixClient() ) {
			query.setParameter("stockClient", unitLoad.getClient());
			paramLog += ", stockClient="+unitLoad.getClient();
			query.setParameter("srStateRaw", LOSStorageRequestState.RAW);
			query.setParameter("srStateProcessing", LOSStorageRequestState.PROCESSING);
		}

		if( weight != null ) {
			query.setParameter("weight", weight );
			paramLog += ", weight="+weight;
		}
		if( zone != null ) {
			query.setParameter("zone", zone);
			paramLog += ", zone="+zone;
		}
		if (near != null) {
			if (near.getZone() != null) {
				query.setParameter("nearLocationZone", near.getZone());
				paramLog += ", nearLocationZone="+near.getZone();
			}
			query.setParameter("nearLocationName", near.getName());
			paramLog += ", nearLocationName="+near.getName();
			query.setParameter("nearLocationX", near.getXPos());
			paramLog += ", nearLocationX="+near.getXPos();
			query.setParameter("nearLocationZ", near.getZPos());
			paramLog += ", nearLocationZ="+near.getZPos();
		}


		query.setParameter("typeUnitLoadType", LOSTypeCapacityConstraint.ALLOCATE_UNIT_LOAD_TYPE);
		query.setParameter("typePercentage", LOSTypeCapacityConstraint.ALLOCATE_PERCENTAGE);

		query.setFirstResult(idxStart);
		query.setMaxResults(10);

		List<Object[]> result = query.getResultList();

		if( result==null || result.size()==0 ) {
			log.info(logStr+"Search location Query="+sb.toString());
			log.info(logStr+"Search location Paramter="+paramLog);
		}
		else {
			log.info(logStr+"NO RESULTS");
		}

		return result;
	}

	@SuppressWarnings("rawtypes")
	private boolean existsClientLocations( Client client ) {
		StringBuilder sb = new StringBuilder();
		sb.append(" SELECT name FROM ");
		sb.append(LOSStorageLocation.class.getSimpleName()+" loc ");
		sb.append(" WHERE client = :client ");
		Query query = manager.createQuery(sb.toString());
		query.setParameter("client", client);
		query.setMaxResults(1);
		List res = query.getResultList();
		return res.size()>0;

	}

	private BigDecimal readUnitLoadWeight(LOSUnitLoad unitLoad) {
		BigDecimal weight = unitLoad.getWeight();
		List<LOSUnitLoad> childs = unitLoadService.getChilds(unitLoad);
		for( LOSUnitLoad child : childs ) {
			BigDecimal weightChild = readUnitLoadWeight(child);
			if( weightChild != null ) {
				weight = weight == null ? weightChild : weight.add( weightChild );
			}
		}
		return weight;
	}

	private Zone readUnitLoadZone(LOSUnitLoad unitLoad) {
		Zone zone = null;

		for( StockUnit su : unitLoad.getStockUnitList() ) {
			Zone itemZone = su.getItemData().getZone();
			if( itemZone != null ) {
				return itemZone;
			}
		}

		List<LOSUnitLoad> childs = unitLoadService.getChilds(unitLoad);
		for( LOSUnitLoad child : childs ) {
			zone = readUnitLoadZone(child);
			if( zone != null ) {
				return zone;
			}
		}

		return null;
	}

	@SuppressWarnings("rawtypes")
	private boolean existsDiffItem( LOSStorageLocation location, ItemData item ) {
		StringBuilder sb = new StringBuilder();
		sb.append(" SELECT su.id FROM ");
		sb.append(StockUnit.class.getSimpleName()+" su, "+LOSUnitLoad.class.getSimpleName()+" ul ");
		sb.append(" WHERE ul.storageLocation=:location and su.unitLoad=ul and su.itemData != :item ");
		Query query = manager.createQuery(sb.toString());
		query.setParameter("location", location);
		query.setParameter("item", item);
		query.setMaxResults(1);
		List res = query.getResultList();
		return res.size()>0;
	}
	}
