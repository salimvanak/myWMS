/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.location.facade;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.log4j.Logger;
import org.mywms.facade.FacadeException;
import org.mywms.model.UnitLoadType;
import org.mywms.service.EntityNotFoundException;

import de.linogistix.los.location.businessservice.LOSStorage;
import de.linogistix.los.location.businessservice.LocationSanityBusiness;
import de.linogistix.los.location.entityservice.LOSAreaService;
import de.linogistix.los.location.entityservice.LOSUnitLoadService;
import de.linogistix.los.location.model.LOSArea;
import de.linogistix.los.location.model.LOSStorageLocation;
import de.linogistix.los.location.model.LOSUnitLoad;
import de.linogistix.los.location.query.LOSAreaQueryRemote;
import de.linogistix.los.location.query.LOSStorageLocationQueryRemote;
import de.linogistix.los.location.service.QueryUnitLoadTypeService;
import de.linogistix.los.query.BODTO;
import de.linogistix.los.query.QueryDetail;
import de.linogistix.los.query.TemplateQuery;
import de.linogistix.los.query.TemplateQueryWhereToken;
import de.linogistix.los.util.businessservice.ContextService;
@Stateless
public class LocationSanityCheckFacadeBean implements LocationSanityCheckFacade {

	private static final Logger log = Logger.getLogger(LocationSanityCheckFacadeBean.class);
	@EJB
	LOSStorageLocationQueryRemote slQueryRemote;
	@EJB
	LOSAreaQueryRemote areaQueryRemote;
	@EJB
	LOSAreaService areaService;
	@EJB
	LOSUnitLoadService ulService;
	@EJB
	LOSStorage storage;
	@EJB
	ContextService contextService;
	@EJB
	LocationSanityBusiness sanityBusiness;
	
	@EJB
	QueryUnitLoadTypeService ulTypeService;
	
	@PersistenceContext(unitName = "myWMS")
	protected EntityManager manager;
	
	public void sanityCheck() throws FacadeException {
		sanityBusiness.sanityCheck();
	}
	
	public void assignDummyTypeToPicklocations() throws FacadeException {
	
//		List<LOSArea> areas = areaService.getByType(LOSAreaType.PICKING);
		List<LOSArea> areas = areaService.getForPicking();
		UnitLoadType dummy = ulTypeService.getPickLocationUnitLoadType();
		QueryDetail d = new QueryDetail(0, Integer.MAX_VALUE);
		
		for (LOSArea area  :areas){
		
			TemplateQueryWhereToken t = new TemplateQueryWhereToken(TemplateQueryWhereToken.OPERATOR_EQUAL, "area", area);
			TemplateQuery query = new TemplateQuery();
			query.addWhereToken(t);
			query.setBoClass(LOSStorageLocation.class);
			
			List<BODTO<LOSStorageLocation>> sls = slQueryRemote.queryByTemplateHandles(d, query );
			for (BODTO<LOSStorageLocation> to : sls){
				LOSStorageLocation sl = manager.find(LOSStorageLocation.class, to.getId());
				if (!sl.getArea().equals(area)){
					continue;
				}
				if (sl.getUnitLoads().isEmpty()){
					
					try {
						LOSUnitLoad existing = ulService.getByLabelId(sl.getClient(), sl.getName());
						log.error("UNITLOAD ON WRONG LOCATION: " + existing.toDescriptiveString());
					} catch (EntityNotFoundException e) {
						log.warn("MISSING UNITLOAD - GOING TO CREATE ON " + sl.toDescriptiveString());
						ulService.createLOSUnitLoad(sl.getClient(), sl.getName(), dummy, sl);
					}
				}
				if (sl.getCurrentTypeCapacityConstraint() != null){
					if ( ! sl.getCurrentTypeCapacityConstraint().getUnitLoadType().equals(dummy)){
						log.error("WRONG TYPE " + sl.getCurrentTypeCapacityConstraint().toDescriptiveString());
						continue;
					}
				}
				
				for (LOSUnitLoad ul : sl.getUnitLoads()){
					
					if (ul.getLabelId().equals(sl.getName())){
						ul = manager.find(LOSUnitLoad.class, ul.getId());
						if (ul.getType().equals(dummy)){
							log.info("Check OK: " + ul.toDescriptiveString());
						} else{
							log.error("Check NOT OK. Going to change UnitLoadType: " + ul.toDescriptiveString());
							ul.setType(dummy);
						}
					} else{
						log.error("Unitload has wrong labelID " + ul.toDescriptiveString());
						continue;
					}
				}
			}
		}

	}
	
	
	//-----------------------------------------------------------------
	
	public void createCronJob() {
		sanityBusiness.createCronJob();
		
	}


	public void cancelCronJob() throws Exception {
		sanityBusiness.cancelCronJob();
		
	}

	public String statusCronJob() {
		return sanityBusiness.statusCronJob();
	}

}
