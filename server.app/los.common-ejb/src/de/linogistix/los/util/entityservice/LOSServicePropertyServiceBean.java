/*
 * Copyright (c) 2006 - 2012 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.util.entityservice;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.Query;

import org.apache.log4j.Logger;
import org.mywms.globals.ServiceExceptionKey;
import org.mywms.model.Client;
import org.mywms.service.BasicServiceBean;
import org.mywms.service.EntityNotFoundException;

import de.linogistix.los.crud.BasicEntityMergeException;
import de.linogistix.los.crud.BasicEntityMerger;
import de.linogistix.los.customization.EntityGenerator;
import de.linogistix.los.model.LOSServiceProperty;
@Stateless
public class LOSServicePropertyServiceBean extends BasicServiceBean<LOSServiceProperty> implements
		LOSServicePropertyService {

	private static final Logger log = Logger.getLogger(LOSServicePropertyServiceBean.class);
	

	@EJB
	private EntityGenerator entityGenerator;
	
	public LOSServiceProperty create(Class<?> service, Client client,
			String key, String value, String ... sub) {
		
		LOSServiceProperty p = entityGenerator.generateEntity(LOSServiceProperty.class);
		p.setClient(client);
		p.setService(service.getName());
		p.setKey(key);
		p.setValue(value);
		p.setSubkey(createSubKey(sub));
		manager.persist(p);
		
		return p;
		
	}

	@SuppressWarnings("unchecked")
	public LOSServiceProperty get(Class<?> service, Client client, String key, String ... sub) throws EntityNotFoundException{
		
		Query q;
		StringBuffer s;
		List<LOSServiceProperty> props;
		String subkeyStr = createSubKey(sub);
		
		s = new StringBuffer();
		s.append(" SELECT p FROM ");
		s.append(LOSServiceProperty.class.getSimpleName());
		s.append(" p WHERE ");
		s.append(" p.service=:service");
		s.append(" AND p.key=:key");
		
		if (subkeyStr != null){
			s.append(" AND p.subkey=:subkey");
		}
		
		if (client != null) s.append("  AND p.client=:client ");
		
		q = manager.createQuery(new String(s));

		if (client != null) q = q.setParameter("client", client);
		if (subkeyStr != null) q = q.setParameter("subkey", subkeyStr);
		
		q.setParameter("service", service.getName()).
		setParameter("key", key);
		props = q.getResultList();
		
		if (props.size() == 0){
			throw new EntityNotFoundException(ServiceExceptionKey.NO_ENTITY_WITH_ID);
		} else if (props.size() == 1){
			return props.get(0);
		}
		
		List<LOSServiceProperty> found  = new ArrayList<LOSServiceProperty>();
		
		
		for (LOSServiceProperty p : props){
			if (match(p.getSubkey(), sub)){
				found.add(p);
			}
		}
		
		if (found.size() == 0){
			throw new EntityNotFoundException(ServiceExceptionKey.NO_ENTITY_WITH_ID);
		} else if (found.size() > 1){
			throw new RuntimeException("System misconfigured: Found " + found.size() + " entries for" + createSubKey(sub) );
		} else{
			return found.get(0);
		}
		
	}

	public void delete(LOSServiceProperty entity){
		
		entity = manager.find(LOSServiceProperty.class, entity.getId());
		manager.remove(entity);

	}

	public LOSServiceProperty merge(LOSServiceProperty entity) {
		BasicEntityMerger<LOSServiceProperty> merger = new BasicEntityMerger<LOSServiceProperty>();
		LOSServiceProperty p = manager.find(LOSServiceProperty.class, entity.getId());
		try{
	      merger.mergeInto(entity, p);
	      return p;
	    } catch (BasicEntityMergeException ex){
	    	log.error(ex.getMessage(), ex);
	    	throw new RuntimeException("merge failed for " + entity.toDescriptiveString());
	    }
	}

	public String getValue(Class<?> service, Client client, String key, String ... sub){
		LOSServiceProperty p;
		try{
			p =  get(service, client, key, sub);
			return p.getValue();
		} catch (EntityNotFoundException ex){
			log.warn(ex.getMessage());
			return null;
		}
	
	}
	
	public void setValue(Class<?> service, Client client,
			String key, String value, String ... sub) throws EntityNotFoundException{
		
		LOSServiceProperty p = get(service, client, key, sub);
		p.setValue(value);
		
		manager.flush();
		
	}
	
	protected String createSubKey(String ... sub){
		StringBuffer b = new StringBuffer();
		
		Arrays.sort(sub);
		
		if (sub != null){	
			int i = 0;
			for (String s : sub){
				if (i>0) b.append("_");
				b.append(s);
				i++;
			}
		} else{
			return null;
		}
		
		return new String(b);
	}
	
	protected String[] splitSubKey(String subKey){
		return subKey.split("_");
	}
	
	boolean match(String subKey, String[] sub){
		
		if ((subKey == null || subKey.length() == 0 ) && (sub == null || sub.length == 0)){
			return true;
		}
		
		Arrays.sort(sub);
		
		String[] split = splitSubKey(subKey);
		
		if (split.length != sub.length){
			return false;
		}
		
		for (int i = 0;i<split.length;i++){
			if (!split[i].equals(sub[i])){
				return false;
			}
		}
		
		return true;

	}
	
	
	
	
	

}
