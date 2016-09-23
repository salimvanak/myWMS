/*
 * Copyright (c) 2006 LinogistiX GmbH. All rights reserved.
 *
 *<a href="http://www.linogistix.com/">browse for licence information</a>
 *
 */
package uk.ltd.mediamagic.mywms.services;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Logger;

/**
 * Load Properties from config file and store them back.
 *
 * Inspired by Geertjan's Weblog <a href="http://blogs.sun.com/geertjan/entry/s"/>
 *
 */
public class AppPreferences {

	private static final Logger log = Logger.getLogger(AppPreferences.class.getName());

	private String propertiesFile;
	private static final String PROPERTIES_EXTENSION =  "properties";
	private String settingsFolder;
	private URL settingsFile;
	private URI lock;
	private Properties settings;
	private String[] noStoreProps = new String[0];

	/** There can only be one!*/
	private static Map<String, AppPreferences> INSTANCES = new HashMap<String, AppPreferences>();

	private final static String CONFIG_ROOT = "/";

	private AppPreferences(String folder, String propertiesFileBaseName, Properties defaultProperties) {

		this.propertiesFile = propertiesFileBaseName;

		settings = new Properties();
		settingsFolder = AppPreferences.getConfigFolder(folder);

		if (settingsFolder!=null) {
			load(defaultProperties);
		}
		else {
			throw new IllegalStateException("Property file not found " + settingsFolder);
		}
	}

	public static AppPreferences getSettings(String propertiesFileBaseName, Properties defaultSettings) {
		return AppPreferences.getSettings("config",propertiesFileBaseName, defaultSettings);
	}

	public static String getSettingsPath(){
		return AppPreferences.getConfigFolder("config");
	}

	/**
	 *
	 */
	public static AppPreferences getSettings(String folder, String propertiesFileBaseName, Properties defaultSettings) {
		AppPreferences ret;

		ret = INSTANCES.get(propertiesFileBaseName);
		if ( ret == null){
			ret = new AppPreferences(folder,propertiesFileBaseName, defaultSettings);
			INSTANCES.put(propertiesFileBaseName,ret);
		}
		return ret;
	}

	public static String getConfigRoot(){
		return CONFIG_ROOT;
	}

	public static String getConfigFolder(String folder) {
		return getConfigRoot() + "/" + folder;
	}

	public static Properties loadFromClasspath(String resourcePath){
		Properties p = new Properties();

		InputStream is = AppPreferences.class.getClass().getResourceAsStream(resourcePath);
		if (is == null){
			throw new NullPointerException();
		}

		try {
			//Just Fallback for default settings
			p.load(is);
			// Retreieve from file or new with default settings
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		return p;
	}

	//  public void store() {
	//    try {
	//      settingsFile = getClass().getResource(settingsFolder + "/" + propertiesFile + "." + PROPERTIES_EXTENSION);
	//      if (settingsFile==null) {
	//        settingsFile = getClass().getResource(settingsFolder + "/" + propertiesFile + "." + PROPERTIES_EXTENSION);
	//      }
	//
	//      try (OutputStream out = settingsFile.openStream()) {
	//    	  Properties tmp = (Properties)settings.clone();
	//    	  for (String key : getNoStoreProps()){
	//    		  tmp.remove(key);
	//    	  }
	//    	  tmp.store(out,"Configuration File " + propertiesFile);
	//    	  out.close();
	//    	  //lock.releaseLock();
	//      } catch (IOException ex) {
	//      // TODO file can not be created , do something about it
	//    	  ex.printStackTrace();
	//      //ExceptionAnnotator.annotate(new InternalErrorException(ex));
	//      }
	//    }
	//  }
	/**
	 * Tries first to load from config file as set in parameters of {@link #getSettings()}.
	 * If this failes (i.e. file not found) it uses default properties.
	 *
	 * @param defaultProperties fall back if no properties file is found
	 */
	public void load(Properties defaultProperties) {
		settingsFile = getClass().getResource(settingsFolder + "/" + propertiesFile + "." + PROPERTIES_EXTENSION);
		if (settingsFile != null) {
			try (InputStream in = settingsFile.openStream()) {
				settings.load(in);
				in.close();
			} catch (IOException ex) {
				ex.printStackTrace();
				settings = defaultProperties;
			}
		} else {
			log.warning("No config file <" + propertiesFile + "> found!");
		}
	}

	public String getValue(String key) {
		return settings.getProperty(key);
	}

	public void setValue(String key, String value) {
		settings.setProperty(key, value.trim());
	}

	public Properties getProperties(){
		return settings;
	}

	boolean valid() {
		// TODO check whether form is consistent and complete
		return true;
	}

	public String[] getNoStoreProps() {
		return noStoreProps;
	}

	public void setNoStoreProps(String[] noStoreProps) {
		this.noStoreProps = noStoreProps;
	}
}
