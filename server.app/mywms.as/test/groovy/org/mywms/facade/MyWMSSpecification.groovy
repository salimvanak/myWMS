package org.mywms.facade

import org.mywms.ejb.BeanLocator

import spock.lang.Specification

class MyWMSSpecification extends Specification {
	public static def BeanLocator beanLocator;
	static final String USER = 'admin'
	static final String PASSWORD = 'admin'
	
	def setupSpec() {
		beanLocator = BeanLocator.lookupBeanLocator("127.0.0.1", 8080, USER, PASSWORD);
	}

}
