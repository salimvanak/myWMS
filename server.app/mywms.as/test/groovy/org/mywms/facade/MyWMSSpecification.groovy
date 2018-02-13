package org.mywms.facade

import org.junit.After
import org.mywms.ejb.BeanLocator
import org.mywms.model.BasicEntity

import groovy.transform.CompileStatic

@CompileStatic
trait MyWMSSpecification {
	static final String USER = 'admin'
	static final String PASSWORD = 'admin'
	
	static BeanLocator beanLocator;
		
	static void setupBeanLocator() {
		if (beanLocator == null) {
			beanLocator = BeanLocator.lookupBeanLocator("127.0.0.1", 8080, USER, PASSWORD);		
		}
	}
	
	public static <T> T getBean(Class<T> c) {
		setupBeanLocator();
		return beanLocator.getStateless(c);
	}

	public static <T> T getBean(Class<T> c, String name) {		
		setupBeanLocator();
		return beanLocator.getStateless(c, name);
	}
}
