/*
 * Copyright (c) 2009-2012 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.inventory.query;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.Query;

import org.mywms.model.Client;

import de.linogistix.los.inventory.model.LOSPickingOrder;
import de.linogistix.los.inventory.query.dto.LOSPickingOrderTO;
import de.linogistix.los.inventory.service.LOSCustomerOrderService;
import de.linogistix.los.model.State;
import de.linogistix.los.query.BODTOConstructorProperty;
import de.linogistix.los.query.BusinessObjectQueryBean;
import de.linogistix.los.query.QueryDetail;
import de.linogistix.los.query.TemplateQuery;
import de.linogistix.los.query.TemplateQueryWhereToken;
import de.linogistix.los.query.exception.BusinessObjectNotFoundException;
import de.linogistix.los.query.exception.BusinessObjectQueryException;
import de.linogistix.los.util.businessservice.ContextService;

/**
 * @author krane
 *
 */
@Stateless
public class LOSPickingOrderQueryBean extends BusinessObjectQueryBean<LOSPickingOrder> implements LOSPickingOrderQueryRemote {

	@EJB
	private ContextService ctxService;

	@EJB private LOSCustomerOrderService orderService;
	
	@Override
	public String getUniqueNameProp() {
		return "number";
	}
	
	@Override
	public Class<LOSPickingOrderTO> getBODTOClass() {
		return LOSPickingOrderTO.class;
	}
	
	@Override
	protected String[] getBODTOConstructorProps() {
		return new String[]{};
	}
	
	@Override
	protected List<BODTOConstructorProperty> getBODTOConstructorProperties() {
		List<BODTOConstructorProperty> propList = super.getBODTOConstructorProperties();
	
		propList.add(new BODTOConstructorProperty("id", false));
		propList.add(new BODTOConstructorProperty("version", false));
		propList.add(new BODTOConstructorProperty("number", false));
		propList.add(new BODTOConstructorProperty("client.number", false));
		propList.add(new BODTOConstructorProperty("customerOrderNumber", false));
		propList.add(new BODTOConstructorProperty("state", false));
//		propList.add(new BODTOConstructorProperty("positions.size", false));
		propList.add(new BODTOConstructorProperty("prio", false));
		propList.add(new BODTOConstructorProperty("operator.name", null, BODTOConstructorProperty.JoinType.LEFT, "operator"));
		propList.add(new BODTOConstructorProperty("destination.name", null, BODTOConstructorProperty.JoinType.LEFT, "destination"));
		
		return propList;
	}
	
	public List<LOSPickingOrder> getByCustomerOrder(String customerOrderNumber) throws BusinessObjectQueryException {
		if (customerOrderNumber == null) return Collections.emptyList();
		try {
			TemplateQuery query = new TemplateQuery();
			query.setBoClass(LOSPickingOrder.class);
			query.addWhereToken(new TemplateQueryWhereToken(TemplateQueryWhereToken.OPERATOR_EQUAL, "customerOrderNumber", customerOrderNumber));
			return queryByTemplate(new QueryDetail(0, 1000), query);
		} catch (BusinessObjectNotFoundException e) {
			return Collections.emptyList();
		}
//		Query query = manager.createNamedQuery("LOSPickingOrder.queryByCustomerOrder");
//		query.setParameter("customerOrder", order);
//		List<LOSPickingOrder> res = query.getResultList();
//		return res;
	}

	@SuppressWarnings("unchecked")
	public List<LOSPickingOrder> queryAll( Client client ) {
		
		if( !ctxService.getCallersClient().isSystemClient() ) {
			client = ctxService.getCallersClient();
		}
		
		StringBuffer buffer = new StringBuffer();
		buffer.append("SELECT order FROM ");
		buffer.append(LOSPickingOrder.class.getSimpleName());
		buffer.append(" order ");
		if( client != null ) {
			buffer.append("WHERE client=:client");
		}
		Query q = manager.createQuery(new String(buffer));
		if( client != null ) {
			q = q.setParameter("client", client);
		}
		
		return q.getResultList();
	}
    @Override
    
	protected List<TemplateQueryWhereToken> getAutoCompletionTokens(String value) {
		List<TemplateQueryWhereToken> ret =  new ArrayList<TemplateQueryWhereToken>();
		

		Integer iValue = null;
		try {
			iValue = Integer.valueOf(value);
		}
		catch( Throwable t) {}

		
		TemplateQueryWhereToken token;
		
		token = new TemplateQueryWhereToken(TemplateQueryWhereToken.OPERATOR_LIKE, "client.number", value);
		token.setLogicalOperator(TemplateQueryWhereToken.OPERATOR_OR);
		ret.add(token);

		token = new TemplateQueryWhereToken(TemplateQueryWhereToken.OPERATOR_LIKE, "number", value);
		token.setLogicalOperator(TemplateQueryWhereToken.OPERATOR_OR);
		ret.add(token);
		
		token = new TemplateQueryWhereToken(TemplateQueryWhereToken.OPERATOR_LIKE, "customerOrderNumber", value);
		token.setLogicalOperator(TemplateQueryWhereToken.OPERATOR_OR);
		ret.add(token);

		if( iValue != null ) {
			token = new TemplateQueryWhereToken(TemplateQueryWhereToken.OPERATOR_EQUAL, "state", iValue);
			token.setLogicalOperator(TemplateQueryWhereToken.OPERATOR_OR);
			ret.add(token);
	
		}
		
		token = new TemplateQueryWhereToken(TemplateQueryWhereToken.OPERATOR_LIKE, "operator.name", value);
		token.setLogicalOperator(TemplateQueryWhereToken.OPERATOR_OR);
		ret.add(token);
		

		token = new TemplateQueryWhereToken(TemplateQueryWhereToken.OPERATOR_LIKE, "destination.name", value);
		token.setLogicalOperator(TemplateQueryWhereToken.OPERATOR_OR);
		ret.add(token);
		
		return ret;
	}
    
    @Override
	protected List<TemplateQueryWhereToken> getFilterTokens(String filterString) {

		List<TemplateQueryWhereToken> ret =  new ArrayList<TemplateQueryWhereToken>();
		TemplateQueryWhereToken token;

		if( "OPEN".equals(filterString) ) {
			token = new TemplateQueryWhereToken(TemplateQueryWhereToken.OPERATOR_SMALLER, "state", State.FINISHED);
			token.setLogicalOperator(TemplateQueryWhereToken.OPERATOR_AND);
			token.setParameterName("finishedState");
			ret.add(token);
		}
		
		return ret;
	}

}
