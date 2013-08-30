package com.mindalliance.configuration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import javax.swing.JList;

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
	public int count=0;
	public JList<String> list;
	
	public JList<String> getList() {
		return list;
	}

	public void setList(JList<String> list) {
		this.list = list;
	}
	
	

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	String logFile;
	String resultCsvFile;
	String reportDirectoryName;
	String reportSrcDirectoryPath;
	String reportDstDirectoryPath;
	String logDirectoryName;
	String logDirectoryPath;
	String errorLogSubDirectoryPath;
	Date currentDate;
	String arrayOfTestCaseId[] = new String[600];
	
	public String[] getArrayOfTestCaseId() {
		return arrayOfTestCaseId;
	}

	public void setArrayOfTestCaseId(String[] arrayOfTestCaseId) {
		this.arrayOfTestCaseId = arrayOfTestCaseId;
	}

	public String[] getArrayOftestCaseResult() {
		return arrayOftestCaseResult;
	}

	public void setArrayOftestCaseResult(String[] arrayOftestCaseResult) {
		this.arrayOftestCaseResult = arrayOftestCaseResult;
	}

	String arrayOftestCaseResult[] = new String[600];
	
	/**
	 * Method To add Test case Id To JList
	 * 
	 * @param testCaseID
	 */

	public void addTestCaseIdToJList(String testCaseID) {
		if (getList() != null) {
			int arraySize = getList().getModel().getSize();

			String[] tempArray = new String[arraySize + 1];

			for (int count = 0; count < arraySize; count++) {
				tempArray[count] = getList().getModel().getElementAt(count);
			}
			tempArray[arraySize] = testCaseID;
			JList<String> templist = new JList<String>(tempArray);
			setList(templist);
		} else {
			String[] tempArray = new String[1];
			tempArray[0] = testCaseID;
			JList<String> templist = new JList<String>(tempArray);
			setList(templist);
		}
	}
	
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
	 * Registers the Select object to provide helper methods to select and de-select options from the drop down list.
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
	
	
	public String getLogFile() {
		return logFile;
	}

	public void setLogFile(String logFile) {
		this.logFile = logFile;
	}

	public String getResultCsvFile() {
		return resultCsvFile;
	}

	public void setResultCsvFile(String resultCsvFile) {
		this.resultCsvFile = resultCsvFile;
	}

	public String getReportDirectoryName() {
		return reportDirectoryName;
	}

	public void setReportDirectoryName(String reportDirectoryName) {
		this.reportDirectoryName = reportDirectoryName;
	}

	public String getReportSrcDirectoryPath() {
		return reportSrcDirectoryPath;
	}

	public void setReportSrcDirectoryPath(String reportSrcDirectoryPath) {
		this.reportSrcDirectoryPath = reportSrcDirectoryPath;
	}

	public String getReportDstDirectoryPath() {
		return reportDstDirectoryPath;
	}

	public void setReportDstDirectoryPath(String reportDstDirectoryPath) {
		this.reportDstDirectoryPath = reportDstDirectoryPath;
	}

	public String getLogDirectoryName() {
		return logDirectoryName;
	}

	public void setLogDirectoryName(String logDirectoryName) {
		this.logDirectoryName = logDirectoryName;
	}

	public String getLogDirectoryPath() {
		return logDirectoryPath;
	}

	public void setLogDirectoryPath(String logDirectoryPath) {
		this.logDirectoryPath = logDirectoryPath;
	}

	public String getErrorLogSubDirectoryPath() {
		return errorLogSubDirectoryPath;
	}

	public void setErrorLogSubDirectoryPath(String errorLogSubDirectoryPath) {
		this.errorLogSubDirectoryPath = errorLogSubDirectoryPath;
	}
	
	public Date getCurrentDate() {
		return currentDate;
	}

	public void setCurrentDate(Date currentDate) {
		this.currentDate = currentDate;
	}

	public String startTime;
	public String getStartTime() {
		return startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	public String getEndtime() {
		return endtime;
	}

	public void setEndtime(String endtime) {
		this.endtime = endtime;
	}

	public String endtime;
	
	
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
