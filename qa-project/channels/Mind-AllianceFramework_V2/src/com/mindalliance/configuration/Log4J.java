package com.mindalliance.configuration;

import java.io.File;
import java.io.IOException;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

public class Log4J {

	 static DataController dataController;
	 static String baseDirPath;
	 static Logger logger = null;
	 static String logDirectoryName;
	 
	 private Log4J() {}
	 
	 /**
	  * Method to set Log4J property file
	 * @throws IOException 
	  */
	 public static void setLog4JProperties() throws IOException {
//	  DataController dataController = new DataController();
	  baseDirPath = GlobalVariables.configuration.getCurrentDir().getCanonicalPath()+ File.separator;
	  PropertyConfigurator.configure(baseDirPath+ File.separator+"Log4J"+ File.separator+"log4j.properties");
	 }
	 
	 /**
	  * Get Method to get Logger class instance
	  * @param packageAddress
	  * @return
	  */
	 public static Logger getlogger(Class<?> clazz) {
	  logger = Logger.getLogger(clazz.getName());
	  return logger;
	 }
}
