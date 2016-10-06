package uk.ltd.mediamagic.mywms;

import java.lang.reflect.UndeclaredThrowableException;
import java.util.Arrays;
import java.util.Collections;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.mywms.model.BasicEntity;

import de.linogistix.los.crud.BusinessObjectCRUDRemote;
import de.linogistix.los.inventory.model.LOSGoodsOutRequestPosition;
import de.linogistix.los.inventory.query.dto.LOSGoodsOutPositionTO;
import de.linogistix.los.location.model.LOSRack;
import de.linogistix.los.location.model.LOSStorageLocation;
import de.linogistix.los.location.model.LOSUnitLoad;
import de.linogistix.los.location.query.RackQueryRemote;
import de.linogistix.los.location.query.dto.StorageLocationTO;
import de.linogistix.los.location.query.dto.UnitLoadTO;
import de.linogistix.los.query.BODTO;
import de.linogistix.los.query.BusinessObjectQueryRemote;
import uk.ltd.mediamagic.debug.MLogger;
import uk.ltd.mediamagic.mywms.common.ClassLookup;

public class BeanDirectory {
	private static final Logger log = MLogger.log(BeanDirectory.class);
	private static final String CRUD_SUFFIX = "CRUDRemote";
	private static final String QUERY_SUFFIX = "QueryRemote";
	private static final String[] curdPackages = {"de.linogistix.los.crud", "de.linogistix.los.inventory.crud", "de.linogistix.los.location.crud"};
	private static final String[] queryPackages = {"de.linogistix.los.query","de.linogistix.los.inventory.query", "de.linogistix.los.location.query"};  
	
	private static final ClassLookup<Class<? extends BusinessObjectCRUDRemote<? extends BasicEntity>>> 
		crudLookup = new ClassLookup<>(BeanDirectory::findCRUD);
	private static final ClassLookup<Class<? extends BusinessObjectQueryRemote<? extends BasicEntity>>> 
		queryLookup = new ClassLookup<>(BeanDirectory::findQuery);
	
	@SuppressWarnings("rawtypes")
	private static final ClassLookup<Class<? extends BODTO>> 
		bodtoLookup = new ClassLookup<>(BeanDirectory::findBODTO);

	static {
		log.setLevel(Level.INFO);
	}
	/**
	 * gets the CURD class for the given BasicEntity.
	 * @param cls the basic entity
	 * @return the CURD class
	 */
	@SuppressWarnings("unchecked")
	public static <T extends BasicEntity> Class<? extends BusinessObjectCRUDRemote<T>> getCRUD(Class<T> cls) {
		return (Class<? extends BusinessObjectCRUDRemote<T>>) crudLookup.get(cls);
	}

	/**
	 * gets the query class for the given BasicEntity.
	 * @param cls the basic entity
	 * @return the query class
	 */
	@SuppressWarnings("unchecked")
	public static <T extends BasicEntity> Class<? extends BusinessObjectQueryRemote<T>> getQuery(Class<T> cls) {
		return (Class<? extends BusinessObjectQueryRemote<T>>) queryLookup.get(cls);
	}

	/**
	 * gets the query class for the given BasicEntity.
	 * @param cls the basic entity
	 * @return the query class
	 */
	@SuppressWarnings("unchecked")
	public static <T extends BasicEntity> Class<? extends BODTO<T>> getBODTO(Class<T> cls) {
		return (Class<? extends BODTO<T>>) bodtoLookup.get(cls);
	}

	private static Class<? extends BusinessObjectCRUDRemote<?>> findCRUD(Class<? extends BasicEntity> cls) {
		return find(cls, BusinessObjectCRUDRemote.class, BeanDirectory.getCRUDClassName(cls));
	}
	
	private static Class<? extends BusinessObjectQueryRemote<?>> findQuery(Class<? extends BasicEntity> cls) {
		return find(cls, BusinessObjectQueryRemote.class, BeanDirectory.getQueryClassName(cls));
	}
	
	@SuppressWarnings("rawtypes")
	static Class<? extends BODTO> findBODTO(Class<? extends BasicEntity> cls) {
		return (Class<? extends BODTO>) find(cls, BODTO.class, BeanDirectory.getBODTOClassName(cls));
	}
	
	/**
	 * Iterates over the classNames to find a class of type <code>resultClass</code>
	 * This method returns the first class it finds.  If the class is not found it will 
	 * throw a ClassNotFoundException wrapped in an UndeclaredThrowableException
	 * @param cls the entity class
	 * @param resultClass the required result super class
	 * @param classNames the list of class names to test.
	 * @return the first class found with super class <code>resultClass</code>
	 */
	@SuppressWarnings("unchecked")
	private static <T> Class<T> find(Class<? extends BasicEntity> cls, Class<? super T> resultClass, Iterable<String> classNames) {
		for (String className : classNames) {
			try {
				Class<?> result = Class.forName(className);
				if (resultClass.isAssignableFrom(result)) {
					return (Class<T>) result;					
				}
				else {
					log.log(Level.SEVERE, "Class {0} is not of the type {1}", new Object[] {className, resultClass.getName()});
				}
			} 
			catch (ClassNotFoundException e) {
				log.log(Level.FINE, "Class {0} not found", className);
			}
		}
		throw new UndeclaredThrowableException(new ClassNotFoundException("No " + resultClass.getSimpleName() + " class found for " + cls.getName()));
	}

	/**
	 * Generates a list of possible class names to test for a relevant query class.
	 * If the class is know the method will return a singleton to prevent searching
	 * @param cls
	 * @return an iterable of strings, so as to allow lazy generation of the next class name.
	 */
	private static Iterable<String> getQueryClassName(Class<? extends BasicEntity> cls) {
		if (cls == LOSRack.class) return Collections.singleton(RackQueryRemote.class.getName());
		String simpleName = cls.getSimpleName();
		return Arrays.stream(queryPackages).map(s -> s + "." + simpleName + QUERY_SUFFIX)::iterator;
	}
	
	/**
	 * Generates a list of possible class names to test for a relevant CRUD class.
	 * If the class is know the method will return a singleton to prevent searching
	 * @param cls
	 * @return an iterable of strings, so as to allow lazy generation of the next class name.
	 */
	private static Iterable<String> getCRUDClassName(Class<? extends BasicEntity> cls) {
		String simpleName = cls.getSimpleName();
		return Arrays.stream(curdPackages).map(s -> s + "." + simpleName + CRUD_SUFFIX)::iterator;
	}

	/**
	 * Generates a list of possible class names to test for a relevant CRUD class.
	 * If the class is know the method will return a singleton to prevent searching
	 * @param cls
	 * @return an iterable of strings, so as to allow lazy generation of the next class name.
	 */
	private static Iterable<String> getBODTOClassName(Class<? extends BasicEntity> cls) {
		String simpleName = cls.getSimpleName();
		if (cls == LOSStorageLocation.class) return Collections.singleton(StorageLocationTO.class.getName());
		if (cls == LOSUnitLoad.class) return Collections.singleton(UnitLoadTO.class.getName());
		if (cls == LOSGoodsOutRequestPosition.class) return Collections.singleton(LOSGoodsOutPositionTO.class.getName());
		return Arrays.stream(queryPackages).map(s -> s + ".dto." + simpleName + "TO")::iterator;
	}
	
	private BeanDirectory() {
	}
	
}
