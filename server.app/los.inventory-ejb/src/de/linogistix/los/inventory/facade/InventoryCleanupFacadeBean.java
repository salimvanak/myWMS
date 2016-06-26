/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.inventory.facade;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.log4j.Logger;
import org.mywms.facade.FacadeException;

import de.linogistix.los.crud.BusinessObjectDeleteException;
import de.linogistix.los.example.CommonTopologyException;
import de.linogistix.los.inventory.model.LOSAdvice;
import de.linogistix.los.inventory.model.LOSAdviceState;
import de.linogistix.los.inventory.model.LOSCustomerOrder;
import de.linogistix.los.inventory.model.LOSGoodsOutRequest;
import de.linogistix.los.inventory.model.LOSGoodsOutRequestPosition;
import de.linogistix.los.inventory.model.LOSGoodsOutRequestState;
import de.linogistix.los.inventory.model.LOSPickingOrder;
import de.linogistix.los.inventory.model.LOSPickingPosition;
import de.linogistix.los.inventory.model.LOSStorageRequest;
import de.linogistix.los.inventory.model.LOSStorageRequestState;
import de.linogistix.los.inventory.query.LOSAdviceQueryRemote;
import de.linogistix.los.inventory.query.LOSCustomerOrderQueryRemote;
import de.linogistix.los.inventory.query.LOSGoodsOutRequestQueryRemote;
import de.linogistix.los.inventory.query.LOSPickingOrderQueryRemote;
import de.linogistix.los.inventory.query.LOSStorageRequestQueryRemote;
import de.linogistix.los.inventory.service.LOSPickingOrderService;
import de.linogistix.los.model.State;
import de.linogistix.los.query.BODTO;
import de.linogistix.los.query.QueryDetail;
import de.linogistix.los.query.TemplateQuery;
import de.linogistix.los.query.TemplateQueryWhereToken;
@Stateless
public class InventoryCleanupFacadeBean implements InventoryCleanupFacade {

	private static final Logger log = Logger.getLogger(InventoryCleanupFacadeBean.class);
	
	@EJB
	private LOSPickingOrderQueryRemote pickQuery;
	@EJB
	private LOSGoodsOutRequestQueryRemote outQuery;
	@EJB
	private LOSCustomerOrderQueryRemote orderReqQuery;
	@EJB
	private LOSStorageRequestQueryRemote storeReqQuery;
	@EJB
	private LOSAdviceQueryRemote adQuery;
	@EJB
	private LOSPickingOrderService pickRequestService;
	
	@PersistenceContext(unitName = "myWMS")
	private EntityManager em;

	
	
	public void cleanup() throws FacadeException{
		clearGoodsOutRequest();
//		clearPickReceipts();
//		clearExtinguishRequests();
//		clearReplenishOrders();
		clearPickingRequests();
		clearOrderRequests();
		clearStorageRequests();
		
////		clearInventories();
//		clearAdvices();
//		clearGoodsReceipts();
////		clearStockUnitLabels();
////		clearOrderReceipt();
//		
//		clearStockUnits();
//		clearUnitLoads();
//		clearStorageLocations();

	}

	public void clearAdvices() throws FacadeException {
		try {
			QueryDetail d = new QueryDetail(0, Integer.MAX_VALUE);
			TemplateQueryWhereToken t = new TemplateQueryWhereToken(
					TemplateQueryWhereToken.OPERATOR_EQUAL, "adviceState",
					LOSAdviceState.FINISHED);
			TemplateQuery q = new TemplateQuery();
			q.addWhereToken(t);
			q.setBoClass(LOSAdvice.class);

			List<LOSAdvice> l = adQuery.queryByTemplate(d, q);
			for (LOSAdvice a : l) {
				a = em.find(LOSAdvice.class, a.getId());
				em.remove(a);
			}
			em.flush();
		} catch (Throwable e) {
			log.error(e.getMessage(), e);
			throw new CommonTopologyException();
		}

	}

		
//	public void clearGoodsReceipts() throws TopologyException {
//		initClient();
//		try {
//			
//			QueryDetail d = new QueryDetail();
//			TemplateQueryWhereToken t = new TemplateQueryWhereToken(
//					TemplateQueryWhereToken.OPERATOR_EQUAL, "client",
//					TESTCLIENT);
//			TemplateQuery q = new TemplateQuery();
//			q.addWhereToken(t);
//			q.setBoClass(LOSGoodsReceiptPosition.class);
//
//			List<LOSGoodsReceiptPosition> l = goodsRecPosQuery.queryByTemplate(d, q);
//			for (LOSGoodsReceiptPosition pp : l) {
//				pp = em.find(LOSGoodsReceiptPosition.class, pp.getId());
//				em.remove(pp);
//			}
//			
//			//--------------
//			
//			d = new QueryDetail();
//			t = new TemplateQueryWhereToken(
//					TemplateQueryWhereToken.OPERATOR_EQUAL, "client",
//					TESTCLIENT);
//			q = new TemplateQuery();
//			q.addWhereToken(t);
//			q.setBoClass(LOSGoodsReceipt.class);
//
//			List<LOSGoodsReceipt> re = goodsRecQuery.queryByTemplate(d, q);
//			for (LOSGoodsReceipt u : re) {
//				u = em.find(LOSGoodsReceipt.class, u.getId());
//				em.remove(u);
//			}
//			em.flush();
//		} catch (Throwable e) {
//			log.error(e.getMessage(), e);
//			throw new TopologyException();
//		}
//	}

	public void clearStorageRequests() throws FacadeException {
		try {
			QueryDetail d = new QueryDetail(0, Integer.MAX_VALUE);
			TemplateQueryWhereToken t = new TemplateQueryWhereToken(
					TemplateQueryWhereToken.OPERATOR_EQUAL, "requestState",
					LOSStorageRequestState.TERMINATED);

			TemplateQuery q = new TemplateQuery();
			q.addWhereToken(t);
			q.setBoClass(LOSStorageRequest.class);

			List<LOSStorageRequest> l = storeReqQuery.queryByTemplate(d, q);
			for (LOSStorageRequest u : l) {
				u = em.find(LOSStorageRequest.class, u.getId());
				em.remove(u);
			}
			em.flush();
		} catch (FacadeException ex){
			throw ex;
		}
		catch (Throwable e) {
			log.error(e.getMessage(), e);
			throw new BusinessObjectDeleteException();
		}
	}


	public void clearOrderRequests() throws FacadeException {
		try {
			QueryDetail d = new QueryDetail(0, Integer.MAX_VALUE);
			TemplateQueryWhereToken t = new TemplateQueryWhereToken(
					TemplateQueryWhereToken.OPERATOR_GREATER, "state", State.FINISHED-1);

			TemplateQuery q = new TemplateQuery();
			q.addWhereToken(t);
			q.setBoClass(LOSCustomerOrder.class);
			List<LOSCustomerOrder> l;
			l = orderReqQuery.queryByTemplate(d, q);
			for (LOSCustomerOrder u : l) {
				u = em.find(LOSCustomerOrder.class, u.getId());
				
				List<LOSPickingOrder> children  = pickRequestService.getByCustomerOrderNumber(u.getNumber());
				if (children.size() > 0){
					log.error("Won't delete: " + u.toDescriptiveString());
					continue;
				}
				em.remove(u);
			}
			em.flush();
		} catch (FacadeException ex){
			throw ex;
		}
		catch (Throwable e) {
			log.error(e.getMessage(), e);
			throw new BusinessObjectDeleteException();
		}
	}

	public void clearGoodsOutRequest() throws FacadeException {
		try {
			QueryDetail d = new QueryDetail(0, Integer.MAX_VALUE);
			TemplateQueryWhereToken t = new TemplateQueryWhereToken(
					TemplateQueryWhereToken.OPERATOR_EQUAL, "outState",
					LOSGoodsOutRequestState.FINISHED);
			TemplateQuery q = new TemplateQuery();
			q.addWhereToken(t);
			q.setBoClass(LOSGoodsOutRequest.class);
			List<LOSGoodsOutRequest> outs = outQuery.queryByTemplate(d, q);
			for (LOSGoodsOutRequest o : outs) {
				o = em.find(LOSGoodsOutRequest.class, o.getId());
				for (LOSGoodsOutRequestPosition pos  :o.getPositions()){
					pos = em.find(LOSGoodsOutRequestPosition.class, pos.getId());
					em.remove(pos);
				}
				o = em.find(LOSGoodsOutRequest.class, o.getId());
				em.remove(o);
			}
			em.flush();
		} catch (FacadeException ex){
			throw ex;
		}
		catch (Throwable e) {
			log.error(e.getMessage(), e);
			throw new BusinessObjectDeleteException();
		}
	}
	
	public void clearPickingRequests() throws FacadeException {
		try {
			QueryDetail d = new QueryDetail(0, 25);
			TemplateQueryWhereToken t = new TemplateQueryWhereToken(
					TemplateQueryWhereToken.OPERATOR_GREATER, "state", State.FINISHED-1);

			TemplateQuery q = new TemplateQuery();
			q.addWhereToken(t);
			q.setBoClass(LOSPickingOrder.class);			
			
			List<BODTO<LOSPickingOrder>> l = pickQuery.queryByTemplateHandles(d, q);
			for (BODTO<LOSPickingOrder> to : l) {
				LOSPickingOrder u;
				u = pickQuery.queryById(to.getId());
				for( LOSPickingPosition pp : u.getPositions() ) {
					if( pp.getCustomerOrderPosition() != null && pp.getCustomerOrderPosition().getState() < State.FINISHED ) { 
						log.error("Parent request not FINISHED yet: " + u.toDescriptiveString());
						continue;
					}
				}
				log.error("GOING TO DELETE: " + u.toDescriptiveString());
				pickRequestService.delete(u);
			}
			
		} catch (FacadeException ex){
			throw ex;
		}
		catch (Throwable e) {
			log.error(e.getMessage(), e);
			throw new BusinessObjectDeleteException();
		}

	}

	
}
