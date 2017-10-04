/*
 * StorageLocationQueryRemote.java
 *
 * Created on 14. September 2006, 06:59
 *
 * Copyright (c) 2006 LinogistiX GmbH. All rights reserved.
 *
 *<a href="http://www.linogistix.com/">browse for licence information</a>
 *
 */

package de.linogistix.los.location.query;


import java.util.List;

import javax.ejb.Remote;

import de.linogistix.los.location.model.LOSStorageLocation;
import de.linogistix.los.location.model.LOSUnitLoad;
import de.linogistix.los.query.BusinessObjectQueryRemote;

/**
 *
 * @author <a href="http://community.mywms.de/developer.jsp">Andreas Trautmann</a>
 */
@Remote
public interface LOSUnitLoadQueryRemote 
        extends BusinessObjectQueryRemote<LOSUnitLoad>
{ 
	
  /**
   * counts the unit loads stored on the storage location
   * @param sl the storage location
   * @return number of unit loads stored on the location.
   */
  public long countUnitLoadsByStorageLocation(LOSStorageLocation sl);
  
  /**
   * Returns all the unit loads on the storage location.
   * @param sl the storage location
   * @return a list of unit loads.
   */
  public List<LOSUnitLoad> getListByStorageLocation(LOSStorageLocation sl);

}
