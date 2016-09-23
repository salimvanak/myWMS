package uk.ltd.mediamagic.flow.crud;

import java.lang.reflect.InvocationTargetException;

import org.mywms.ejb.BeanLocator;

import uk.ltd.mediamagic.fx.flow.BeanFactory2;
import uk.ltd.mediamagic.fx.flow.BeanNotFoundException;
import uk.ltd.mediamagic.fx.flow.MagicDIBase;
import uk.ltd.mediamagic.mywms.MyWMS;

/** 
 * The bean factory for generating JNI Beans from the bean locator.
 * This factory will attempt to generate stateless beans from the 
 * bean locator registered in the application context.
 * @author slim
 *
 */
public class JNIBeanFactory implements BeanFactory2 {

	MyWMS application;
	
	public JNIBeanFactory(MyWMS application) {
		this.application = application;
	}

	@Override
	public <T> T newInstance(String name, Class<T> type, MagicDIBase context) throws 
			InstantiationException, IllegalAccessException, InstantiationException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException, BeanNotFoundException {
		BeanLocator beanLocator = application.getBeanLocator();
		if (beanLocator == null) return null;
		return beanLocator.getStateless(type);
	}
	
}
