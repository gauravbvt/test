package com.mindalliance.configuration;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.HierarchicalConfiguration;
import org.apache.commons.configuration.XMLConfiguration;

import com.mindalliance.configuration.Configuration.TraceLevel;

/**
 * The DataLocators class contains the methods for manipulating data such as
 * reading test data from CSV file which we can use throughout the application.
 * @author AFourTech
 */
public class DataController {
	static String stestName;
	public String automatesYes = "YES";
	private static Hashtable<String, XMLConfiguration> pageDataCache = new Hashtable<String, XMLConfiguration>();
	
	/**
     * Checks for the key in the XML file
     * @param config 	XMLConfiguration object 
     * @param key 		The key string to be searched
     * @return	The value of the key string
     */
    public String getRequiredConfig(XMLConfiguration config, String key)throws UIAutomationException{
    	String value="";
    		value = config.getString(key);
    		if(value.equals("")){
    			throw new UIAutomationException("Value for: "+key+", not provided in file: "+config.getFileName());
    		} 
    	return value;
    }
    
	
    /**
     * Gets the page data elements (Like xpath / id / classname / linktext / partiallinktext) stored in the xml file 'fileName'
     * 
     * @param  fileName 		Name of XML file of which to get elements.
     * @param  elementName   	GivenName in the XML file
     * @param  attributeName 	Name of the attribute in the XML file
     * 
     * @return attributeValue 	Value associated with the 'attributeName'
     * @throws UIAutomationException 
     */
    public String getPageDataElements(String fileName,String elementName,String attributeName) throws UIAutomationException{
    	String attributeValue="";
    	List<?> pageDataElements=null;
    
       	XMLConfiguration pageData=null;
    	HierarchicalConfiguration subElements=null;
    	try{
    		String filePath=GlobalVariables.configuration.getCurrentDir().getCanonicalPath().toString()+File.separator+"ObjectRepository"
    		+File.separator+fileName;

    		String pagePath = new File(filePath).getAbsolutePath();
    		pageData = (XMLConfiguration) pageDataCache.get(pagePath);
    		if  (pageData == null){
    			pageData = new XMLConfiguration(new File(filePath).getAbsolutePath());
    			pageDataCache.put(pagePath,  pageData);
    		}
    		
    		pageDataElements = pageData.configurationsAt("Element");
    		if(pageDataElements.size() != 0){
    			for(Iterator<?> it = pageDataElements.iterator(); it.hasNext();){
    				subElements = (HierarchicalConfiguration) it.next();
   					if(subElements.getString("GivenName").equals(elementName)){
   						attributeValue=subElements.getProperty(attributeName).toString(); 
   							if(attributeValue.length()== 0 ){
   								Reporting.trace(TraceLevel.Info, "Attribute '" + attributeName +"' contains a blank value in the file " + fileName);
   							}
   						break;
					}
    			}
    		} else{
    			throw new UIAutomationException("List of page data elements is empty.");
    	}
    	}catch(IOException io){	 
    		throw new UIAutomationException("Unable to read '"+fileName+"' file." );
     	}catch(ConfigurationException cnf){
    		throw new UIAutomationException("Unable to read '"+fileName+"' file." );
   	 	}
   	 	return attributeValue;
    }

    public static void createResultFiles() {
		try {
			String logFile;
			String resultCsvFile;
			String reportDirectoryName;
			String reportSrcDirectoryPath;
			String reportDstDirectoryPath;
			String logDirectoryName;
			String logDirectoryPath;
			String errorLogSubDirectoryPath;
						
			// Get Current Date
			DateFormat dateFormat = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss");
			Date currentDate = null;
			currentDate = new Date();
			
			// Set date
			GlobalVariables.configuration.setCurrentDate(currentDate);
			
			// Create Report Directory
			reportDirectoryName = dateFormat.format(currentDate);
//			reportSrcDirectoryPath="D:\\Channels\\Mind-AllianceFramework_V2\\TestCases\\Mind-AllianceTestCaseSheet.ods";
//			reportDstDirectoryPath="D:\\Channels\\Mind-AllianceFramework_V2\\Reports\\"+reportDirectoryName;
			
			reportSrcDirectoryPath = GlobalVariables.configuration.getCurrentDir().getCanonicalPath().toString() + "\\TestCases\\Mind-AllianceTestCaseSheet.ods";
			reportDstDirectoryPath = GlobalVariables.configuration.getCurrentDir().getCanonicalPath().toString() + "\\Reports\\" + reportDirectoryName;
			
			
			
			// Set
			GlobalVariables.configuration.setReportDirectoryName(reportDirectoryName);
			GlobalVariables.configuration.setReportDstDirectoryPath(reportDstDirectoryPath);
			
			
			File Dir = new File(reportDstDirectoryPath);
			if (!Dir.exists())
				Dir.mkdir();
			
			// Create Log Directory
			logDirectoryName = dateFormat.format(currentDate);
//			logDirectoryPath="D:\\Channels\\Mind-AllianceFramework_V2\\Logs\\"+logDirectoryName;
			logDirectoryPath = GlobalVariables.configuration.getCurrentDir().getCanonicalPath().toString() + "\\Logs\\" + logDirectoryName;
		
			// Set Log directory
			GlobalVariables.configuration.setLogDirectoryName(logDirectoryName);
			GlobalVariables.configuration.setLogDirectoryPath(logDirectoryPath);		
			Dir = new File(logDirectoryPath); 
			if (!Dir.exists())
				Dir.mkdir();
			
			// Create Errors sub-directory
//			errorLogSubDirectoryPath="D:\\Channels\\Mind-AllianceFramework_V2\\Logs\\"+logDirectoryName+"\\Errors";
			errorLogSubDirectoryPath = GlobalVariables.configuration.getCurrentDir().getCanonicalPath().toString() + "\\Logs\\" + logDirectoryName + "\\Errors";
			
			GlobalVariables.configuration.setErrorLogSubDirectoryPath(errorLogSubDirectoryPath);
			Dir = new File(errorLogSubDirectoryPath);
			if (!Dir.exists())
				Dir.mkdir();
			
			
			// Logs Files
			resultCsvFile = logDirectoryPath + "\\Results.csv";
			logFile = logDirectoryPath + "\\Logs.logs";
			GlobalVariables.configuration.setLogFile(logFile);
			
			GlobalVariables.configuration.setResultCsvFile(resultCsvFile);
			
			FileWriter fileWriter = new FileWriter(resultCsvFile, true);
			BufferedWriter oBWriter = new BufferedWriter(fileWriter);
			oBWriter.write("TestCaseId,VerificationStepNo,Description,Result,ScriptException,ErrorReport");
			oBWriter.newLine();
			oBWriter.flush();
			oBWriter.close();
		}
		catch(Exception e) {
			System.out.println("\nError occured in CreateResultFiles Function.");
			e.printStackTrace();
		}
	}
}
