package uk.ltd.mediamagic.los.inventory.businessservice;

import static org.junit.Assert.*

import org.mywms.facade.FacadeException
import org.mywms.facade.MyWMSSpecification

import de.linogistix.los.inventory.crud.LOSPickingOrderCRUDRemote
import de.linogistix.los.inventory.facade.LOSOrderFacade
import de.linogistix.los.inventory.facade.LOSPickingFacade
import de.linogistix.los.inventory.model.LOSCustomerOrder
import de.linogistix.los.inventory.model.LOSPickingOrder
import de.linogistix.los.model.Prio
import de.linogistix.los.model.State
import de.linogistix.los.inventory.facade.OrderPositionTO
import spock.lang.Ignore

class LOSOrderBusinessTest extends MyWMSSpecification {
	
	static def LOSPickingOrderCRUDRemote pickQuery
	static def LOSPickingFacade pickFacade
	static def LOSOrderFacade orderFacade
	static def LOSCustomerOrder order
	
	def LOSPickingOrder pick
	
	def setupSpec() {
		pickQuery = beanLocator.getStateless(LOSPickingOrderCRUDRemote.class)
		pickFacade = beanLocator.getStateless(LOSPickingFacade.class)
		orderFacade = beanLocator.getStateless(LOSOrderFacade.class);
		order = orderFacade.order(null, null, new OrderPositionTO[0], null, null, null, null, null, Prio.NORMAL, false, false, null);
	}
	
	def cleanupSpec() {
		orderFacade.removeOrder((Long) order.getId())
	}
	
	def setup() {
		pick = pickFacade.createNewPickingOrder(order.getNumber(), "TEST", true)
	}
	
	def cleanup() {
		pickFacade.removeOrder(pick.getId())
	}

	def "Check order release and halt movements without an operator assignment"() {
		when: "a new picking order is created"
			def myPick = pickQuery.retrieve(pick.getId())
			
		then: "it should be given the following properties"
			myPick.getState() == State.RAW	
			myPick.getOperator() == null
			myPick.getPrio() == Prio.NORMAL
			
		when: "the pick is released for picking before a user has been assigned to it"
			pickFacade.releaseOrder(pick.getId())
			myPick = pickQuery.retrieve(pick.getId())
		then: 
			myPick.getState() == State.PROCESSABLE
			myPick.getOperator() == null
			myPick.getPrio() == pick.getPrio()

		when: "the pick is halted"
			pickFacade.haltOrder(pick.getId())
			myPick = pickQuery.retrieve(pick.getId())
		then:
			myPick.getState() == State.RAW
			myPick.getOperator() == null
			myPick.getPrio() == pick.getPrio()
	}

	def "Check order release and halt movements when an operator is assigned"() {		
		when: "a new picking order is created"
			pickFacade.changePickingOrderUser(pick.getId(), USER)
			def myPick = pickQuery.retrieve(pick.getId())
			
		then: "the pick is assigned to a user"
			myPick.getState() == State.RAW
			myPick.getOperator() != null
			myPick.getOperator().getName() == USER
			myPick.getPrio() == Prio.NORMAL
						
		when: "the pick is released for picking"
			pickFacade.releaseOrder(pick.getId())
			myPick = pickQuery.retrieve(pick.getId())
		then:
			myPick.getState() == State.RESERVED
			myPick.getOperator() != null
			myPick.getOperator().getName() == USER
			myPick.getPrio() == pick.getPrio()

		when: "the pick is halted"
			pickFacade.haltOrder(pick.getId())
			myPick = pickQuery.retrieve(pick.getId())
		then:
			myPick.getState() == State.RAW
			myPick.getOperator() == null
			myPick.getPrio() == pick.getPrio()			
	}

	def "Check changing the user after picking has started"() {
		when: "set picking state to started to higher"
			pick.setState(state)
			pickQuery.update(pick)
			pickFacade.changePickingOrderUser(pick.getId(), USER)
		then: "throw an exception"
			thrown(FacadeException)
			
		where:
			state << [State.STARTED, State.PENDING, State.PICKED, State.FINISHED, 
				State.CANCELED, State.POSTPROCESSED, State.DELETED]			
	}

	@Ignore("Currently it is permissable to change the prio after picking has started.")
	def "Check changing the priority after picking has started"() {
		when: "set picking state to started to higher"
			pick.setState(state)
			pickQuery.update(pick)
			pickFacade.changePickingOrderPrio(pick.getId(), Prio.HIGH)
		then: "throw an exception"
			thrown(FacadeException)
			
		where:
			state << [State.STARTED, State.PENDING, State.PICKED, State.FINISHED,
				State.CANCELED, State.POSTPROCESSED, State.DELETED]
	}

	@Ignore("Currently it is permissable to change the destination after picking has started.")
	def "Check changing the destination after picking has started"() {
		when: "set picking state to started to higher"
			pick.setState(state)
			pickQuery.update(pick)
			pickFacade.changePickingOrderDestination(pick.getId(), "Nirwana")
		then: "throw an exception"
			thrown(FacadeException)
			
		where:
			state << [State.STARTED, State.PENDING, State.PICKED, State.FINISHED,
				State.CANCELED, State.POSTPROCESSED, State.DELETED]
	}

}
