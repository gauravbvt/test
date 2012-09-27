package com.mindalliance.configuration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.XMLConfiguration;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

/**
 * The Singleton Configuration class contains all the variables that are 
 * used throughout the application, and their getter/setter methods.
 * @author AFourTech
 */
public class Configuration {
	private static Configuration configuration=null;
	private WebElement webElement=null;
	private WebDriver webDriver=null;
	private Select select=null;
	private ArrayList<String> attrSearchList=null;
	private File currentDir = new File(".");
	public enum	TraceLevel {Fatal, Error, Warning, Info, Debug }; // Least output to most.
	private TraceLevel traceLevel = TraceLevel.Error;
	private int difference=0;
	private Hashtable<String,String> configData=null;
	DataController dataController=new DataController();
	private List<WebElement> webElements;
	
	/**
	 * Creates the singleton instance of the Configuration class.
	 * @return configuration object
	 * @throws UIAutomationException 
	 */
	public static Configuration getConfigurationObject() throws UIAutomationException{
		if (configuration == null){
			configuration = new Configuration();
			configuration.loadConfigData("configuartion.xml");
		}
		return configuration;
	}

	/**
	 * Gets WebElement object that Represents an HTML element in a page.
	 * @return the webElement object
	 */
	public WebElement getWebElement(){
		
		return webElement;
	}
	

	/**
	 * Registers the WebElement object to perform various operations such as click , clear , sendKeys on HTML element in a page.
	 * @param webElement the webElement to set
	 */
	public void setWebElement(WebElement webElement){
		this.webElement = webElement;
	}
	
	/**
	 * Registers the webElement List object to perform various operations
	 * @param WebElements the webElement list to set
	 */
	public void setWebElements(java.util.List<WebElement> webElements) 
	{		
		this.webElements = webElements;
	}
	
	/**
	 * Gets the WebElement List that Represents HTML elements in a page.
	 * @return the WebElement List 
	 */
	public List<WebElement> getWebElements() 
	{
		return webElements;
	}
	/**
	 * Gets the WebDriver object which is used to launch the web browser.
	 * @return the webDriver object
	 */
	public WebDriver getWebDriver(){
		return webDriver;
	}

	/**
	 * Registers the WebDriver object to control the browser , WebElement selection and Debugging aids
	 * @param webDriver the webDriver to set
	 */
	public void setWebDriver(WebDriver webDriver){
		this.webDriver = webDriver;
	}

	/**
	 * Gets the Select object. Used to select value(option) from a drop down list
	 * @return the select object
	 */
	public Select getSelect(){
		return select;
	}

	/**
	 * Registers the Select object to provide helper methods to select and de select options from the drop down list.
	 * @param select the select object to set
	 */
	public void setSelect(Select select){
		this.select = select;
	}

	/**
	 * Gets the ArrayList object which is a Resizable-array implementation of the List interface.
	 * @return the attrSearchList object
	 */
	public ArrayList<String> getAttrSearchList(){
		return attrSearchList;
	}

	/**
	 * Registers the ArrayList object that is used internally to store the list. 
	 * @param attrSearchList the attrSearchList to set
	 */
	public void setAttrSearchList(ArrayList<String> attrSearchList){
		this.attrSearchList = attrSearchList;
	}

	/**
	 * Gets the current directory of the project which contains representation of file and directory pathnames.
	 * @return the currentDir file object
	 */
	public File getCurrentDir(){
		return currentDir;
	}

	/**
	 * Registers the current directory of the project
	 * @param currentDir the currentDir file object to set
	 */
	public void setCurrentDir(File currentDir){
		this.currentDir = currentDir;
	}
	
	/**
	 * Registers the difference between the elements which are present on Accounts & Agents Pages
	 * @param difference
	 */
	public void setDifference(int difference){
		this.difference = difference;
	}

	/**
	 * Gets the difference between elements which are present on Accounts & Agents Pages
	 * @return
	 */
	public int getDifference(){
		return difference;
	}
	
	/**
	 * Gets the object of HashTable consisting of configuration data
	 * @return configData 
	 */
	public Hashtable<String,String> getConfigData(){
		return configData;
	}
	
	/**
     * Loads configuration data related to the application from the configuration xml file into HashTable
     * @param fileName		Name of XML file from which to load configuration data
     * @return void
    */
	private void loadConfigData(String fileName) throws UIAutomationException{
		try {
			String filePath = configuration.getCurrentDir().getCanonicalPath().toString()+ File.separator+ "TestData" + File.separator+fileName;

			String configFileName = new File(filePath).getAbsolutePath();
			XMLConfiguration config = new XMLConfiguration(configFileName);

			configData = new Hashtable<String,String>();		
			configData.put("URL",dataController.getRequiredConfig(config,"Credentials.URL"));
			configData.put("UserName",dataController.getRequiredConfig(config,"Credentials.UserName"));
			configData.put("PassWord",dataController.getRequiredConfig(config,"Credentials.PassWord"));
			configData.put("Browser", dataController.getRequiredConfig(config, "Credentials.Browser"));
			configData.put("TimeOutForFindingElementSeconds",dataController.getRequiredConfig(config,"TimeOutForFindingElementSeconds"));
			configData.put("TimeOutForFindingElementSecondsForClick",dataController.getRequiredConfig(config,"TimeOutForFindingElementSecondsForClick"));
			
			setTraceLevel(config.getString("TraceLevel", getTraceLevel().toString()));
		} 
		catch(IOException io) {
			throw new UIAutomationException("Unable to read '"+fileName+"' file");
		} 
		catch (ConfigurationException ce) {
			throw new UIAutomationException("Unable to configure '"+fileName+"' file");
		}
		
	}
	
    public boolean isTraceLevel(TraceLevel level){
		if (level.ordinal() <= traceLevel.ordinal()){
			return true;
		}
		return false;
	}

	public TraceLevel getTraceLevel(){
		return traceLevel;
	}

	public void setTraceLevel(String stringLevel){
		traceLevel = TraceLevel.valueOf(stringLevel);
	}
}
