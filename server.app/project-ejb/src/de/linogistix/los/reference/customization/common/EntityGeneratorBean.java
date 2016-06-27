/*
 * Copyright (c) 2012 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.reference.customization.common;

import javax.ejb.Stateless;

import org.apache.log4j.Logger;
import org.mywms.model.BasicEntity;
import org.mywms.model.UnitLoad;

import de.linogistix.los.customization.EntityGenerator;
import de.linogistix.los.location.model.LOSUnitLoad;

/**
 * @author krane
 *
 */
@Stateless
public class EntityGeneratorBean implements EntityGenerator {
	Logger log = Logger.getLogger(EntityGeneratorBean.class);

	@SuppressWarnings("unchecked")
	public <E extends BasicEntity> E generateEntity( Class<E> clazz ) {
		E e = null;

		// example to generate your own entities
		if( clazz == UnitLoad.class ) {
			UnitLoad o = new LOSUnitLoad();
			return (E)o;
		}
		
		try {
			e = (E) clazz.newInstance();
		} 
		catch (Exception e1) {
			log.error("Cannot get instance!",e1);
		}
			
		return e;
	}
	
}
