/*
 * TopologyRemote.java
 *
 * Created on 12. September 2006, 11:36
 *
 * Copyright (c) 2006 LinogistiX GmbH. All rights reserved.
 *
 *<a href="http://www.linogistix.com/">browse for licence information</a>
 *
 */

package de.linogistix.los.example;

import javax.ejb.Remote;

import org.mywms.model.BasicEntity;

/**
 * Creates a topology.
 * 
 * @author <a href="http://community.mywms.de/developer.jsp">Andreas Trautmann</a>
 */
@Remote()
public interface LocationTestTopologyRemote {
  
  String PALETTE_NAME = "EuroPalette, upto 1,2m high";
		
	String KLT_NAME = "KLT Container, 400 x 600 mm";
	
	String STORAGE_LOCATION_TYPE_NAME = "Pallet area, Euro palette TYP 2";
		
	String PICKING_LOCATION_TYPE_NAME = "Picking Location";
	
	String SYSTEM_LOCATION_TYPE_NAME = "System";
	
	String MANY_PALETTE_NAME = "Many Palettes";
	
	String ONE_PALETTE_NAME = "One Palette";
	
	String KOMM_FACH_DUMMY_LHM_CONSTR_NAME = "A virtual LHM for testing";
	
	String STORE_AREA_NAME = "Storage";
	
	String PICKING_AREA_NAME = "Picking";
	
	String WE_BEREICH_NAME = "Goods-IN";
	
	String WA_BEREICH_NAME = "Goods-OUT";
	
	String CLEARING_BEREICH_NAME = "Clearing";
	
	String PRODUCTION_BEREICH_NAME = "Produktion";
	
	String SL_WE_TESTCLIENT_NAME = "Test Goods-IN 1";
	
	String SL_WE_TESTMANDANT_NAME = "Test Goods-IN 2";
	
	String SL_PRODUCTION_TESTCLIENT_NAME = "Test Production 1";
	
	String SL_PRODUCTION_TESTMANDANT_NAME = "Test Production 2";
	
	String TEST_RACK_1_NAME = "T1";
	
	String TEST_RACK_2_NAME = "T2";
	
	String SL_WA_TESTCLIENT_NAME = "TESTWA 1";
	
	String SL_WA_TESTMANDANT_NAME = "TESTWA 2";
	
	String SL_CLEARING_NAME = "Clearing";
		
	String UL_NIRWANA_NAME = "Nirwana";
	
	String EINE_DEFAULT_PALETTE_NAME = "Constraint 1 Standard palette";
	
	void clear() throws LocationTopologyException;

  void create() throws LocationTopologyException;

  void remove(Class<BasicEntity> clazz) throws LocationTopologyException;
  
}
