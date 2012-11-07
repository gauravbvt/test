package com.mindalliance.configuration;

import java.awt.Color;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.apache.commons.io.FileUtils;
import org.jopendocument.dom.spreadsheet.Sheet;
import org.jopendocument.dom.spreadsheet.SpreadSheet;
import org.junit.Assert;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.WebDriverException;

import com.csvreader.CsvReader;
import com.mindalliance.configuration.Configuration.TraceLevel;

/**
 * The Reporting class contains all the reporting related methods which are 
 * helpful for analyzing the results of the test case.
 * @author AFourTech
 */
public class Reporting
{
	public static int totalNoOfTestCasesPassed = 0;
	public static int totalNoOfTestCasesFailed = 0;
	public static String logDirectoryName;
	public static String logDirectoryPath;
	public static String errorLogSubDirectoryPath;
	public static String reportDirectoryName;
	public static String reportSrcDirectoryPath;
	public static String reportDstDirectoryPath;
	public static String testDataDirectoryPath;
	public static String blank = "";
	public static String passed = "Pass";
	public static String failed = "Fail";
	public static String verifyError;
	public static String notRun = "NOT RUN";
	public static String automatesYes = "YES";
	public static int testCasesAutomated = 0;
	public static int testCasesPassed = 0;
	public static int testCasesFailed = 0;
	public static CsvReader products;
	public static CsvReader csvTestCase;
	public static String sSummary;
	public static String csvResult;
	public static String csvScriptException;
	public static String csvErrorReport;
	public static String arrayOfTestCaseId[] = new String[600];
	public static String arrayOftestCaseSummary[] = new String[600];
	public static String arrayOftestCaseResult[] = new String[600];
	public static String testName;
	public static int noOfViewTestCasesExecuted=0;
	public static int noOfPlanTestCasesExecuted=0;
	public static int noOfCommandTestCasesExecuted=0;
	public static int noOfViewTestCasesPassed=0;
	public static int noOfPlanTestCasesPassed=0;
	public static int noOfCommandTestCasesPassed=0;
	public static int noOfViewTestCasesFailed=0;
	public static int noOfPlanTestCasesFailed=0;
	public static int noOfCommandTestCasesFailed=0;
	public static String startDateTime;
	public static String endDateTime;
	
	static DateFormat dateFormatGMT = new SimpleDateFormat("yy-MM-dd HH:mm:ss");
	
    /**
     * Takes a screenshot of the application page wherever there is a failure
     * 
     * @param  fileName		The name of the file where the screenshot is saved
     * @return String The path where the screenshot file is saved
     * @throws UIAutomationException 
     * @throws WebDriverException 
     */
    public static String getScreenShot(String fileName)
    {
		try 
		{
			// Captures screen shots and create .png file
   	 		fileName=GlobalVariables.configuration.getCurrentDir().getCanonicalPath() + File.separator + "Reports_Snapshot" + File.separator+fileName + ".png";
   	 		File scrFile = ((org.openqa.selenium.TakesScreenshot)Configuration.getConfigurationObject().getWebDriver()).getScreenshotAs(OutputType.FILE);
   	 		FileUtils.copyFile(scrFile, new File(fileName));
		} catch(IOException io) 
		{
			Assert.fail("Unable to create file '"+fileName+".png'");
		}
		catch(WebDriverException wde) 
		{
			Assert.fail("Unable to create file '"+fileName+".png'");
		}
		catch(UIAutomationException ce) 
		{
			Assert.fail("Unable to create file '"+fileName+".png'");
		}
   	 	return fileName;
	}
    
	/**
     * Returns time in format minutes:seconds.milliseconds
     * @param startTimeMilliSecs Start milli seconds value.
     * @return String
     */
    public static String actionTime(long startTimeMilliSecs) 
    {
		long duration = System.currentTimeMillis() - startTimeMilliSecs;
		return (""+duration/60000+":"+(duration/1000)%60+"."+duration%1000);
	}
    
    /**
     * Sends info to System.out  prefixed by timestamp and postfixed with duration time
     * @param startTimeMilliSecs Start milli seconds value.
     * @param message Message to sent to system for printing on console.
     */
    public static void timeStampedActionTime(long startTimeMilliSecs, String message) 
    {
    	// Record time before additional formatting.
    	String duration = actionTime(startTimeMilliSecs); 
		trace(TraceLevel.Info, dateFormatGMT.format(new Date())+" "+message+duration);
	}
    
    
    
    
    /**
     * Prints trace on console.
     * @param level Level defines trace leves such as fatal, error, info etc.
     * @param message Message to print on console.
     */
	public static void trace(TraceLevel level, String message)
	{
		// Code to print trace message
		if ( GlobalVariables.configuration.isTraceLevel(level))
		{
			System.out.println(message);
		}
	}
	/**
	 * Method to Update Test Case Execution Result
	 * @return void
	 * @throws UIAutomationException 
	 */
	public static void updateTestCaseExecutionResult() throws UIAutomationException {
		try {
			int index = 0;
			testCasesPassed = 0;
			testCasesFailed = 0;
			for (int i = 0; i < GlobalVariables.configuration.getList().getModel().getSize() ; i++)	{
				testName = GlobalVariables.configuration.getList().getModel().getElementAt(i).toString();
				if (testName != blank) {
				   // Call readCsvFile
					String sResult = readCsvFile(testName);
					if (sResult == failed) {
					    // Call generateAutomationReportInHtml()
					    generateAutomationReportInHtml(testName);
					    testCasesFailed ++;
					    arrayOfTestCaseId[index] = testName;
					    GlobalVariables.configuration.setArrayOfTestCaseId(arrayOfTestCaseId);
					    
					    arrayOftestCaseResult[index++] = failed;
					    GlobalVariables.configuration.setArrayOftestCaseResult(arrayOftestCaseResult);
				    }
				    else if (sResult == passed) {
					    // Call generateAutomationReportInHtml()
					    generateAutomationReportInHtml(testName);
					    testCasesPassed ++;
					    arrayOfTestCaseId[index] = testName;
					    GlobalVariables.configuration.setArrayOfTestCaseId(arrayOfTestCaseId);
					    arrayOftestCaseResult[index++] = passed;
					    GlobalVariables.configuration.setArrayOftestCaseResult(arrayOftestCaseResult);
				    }
			    }
			}
		}
		catch(Exception e) {
			throw new UIAutomationException("\nError in updateTestCaseExecutionResult() function.\n");			
		}
	}
	
	/**
	 * Method to Update Test Case sheet for Functional Test Cases
	 * @return void 
	 * @throws UIAutomationException 
	 */
	public static void updateTestCaseSheetResultForFunctionalTestCases() throws UIAutomationException {
		try {
			File file = new File(GlobalVariables.configuration.getCurrentDir().getCanonicalPath().toString() + "\\TestCases\\FunctionalTestCase.ods");
			Sheet sheet=SpreadSheet.createFromFile(file).getSheet(0);
			
			// Update Test Case Sheet
			for(int i = 1; i < 18 ; i++) {
				sheet=sheet.getSpreadSheet().getSheet(i);
				for(int j=1;j<sheet.getRowCount();j++) {
					sheet.getCellAt("I"+j).setValue("");
					sheet.getCellAt("L"+j).setValue("");
					sheet.getCellAt("M"+j).setValue("");
					sheet.getCellAt("N"+j).setValue("");
					sheet.getCellAt("O"+j).setValue("");
					sheet.getCellAt("P"+j).setValue("");
					sheet.getCellAt("Q"+j).setValue("");
					sheet.getCellAt("R"+j).setValue("");
					sheet.getCellAt("S"+j).setValue("");
					sheet.getCellAt("T"+j).setValue("");
				}
				sheet.getCellAt("I1").setValue("RESULT");
				sheet.getCellAt("L1").setValue("SCRIPT EXCEPTION");
				sheet.getCellAt("R1").setValue("ERROR");
			}
			for (int i = 0; i < GlobalVariables.configuration.getList().getModel().getSize() ; i++)	{
				testName = GlobalVariables.configuration.getList().getModel().getElementAt(i).toString();
				if(testName.contains("CL")) 
					sheet = sheet.getSpreadSheet().getSheet(1);
				else if(testName.contains("HP"))
					sheet = sheet.getSpreadSheet().getSheet(2);
				else if(testName.contains("CA"))
					sheet = sheet.getSpreadSheet().getSheet(3);
				else if(testName.contains("DC"))
					sheet = sheet.getSpreadSheet().getSheet(6);
				else if(testName.contains("CC"))
					sheet = sheet.getSpreadSheet().getSheet(7);
				else if(testName.contains("CP"))
					sheet = sheet.getSpreadSheet().getSheet(8);
				else if(testName.contains("PS"))
					sheet = sheet.getSpreadSheet().getSheet(9);
				else if(testName.contains("TF"))
					sheet = sheet.getSpreadSheet().getSheet(10);
				else if(testName.contains("TE"))
					sheet = sheet.getSpreadSheet().getSheet(11);
				else if(testName.contains("IF"))
					sheet = sheet.getSpreadSheet().getSheet(12);
				else if(testName.contains("PP"))
					sheet = sheet.getSpreadSheet().getSheet(13);
				else if(testName.contains("PE"))
					sheet = sheet.getSpreadSheet().getSheet(14);
				else if(testName.contains("LF"))
					sheet = sheet.getSpreadSheet().getSheet(15);
				else if(testName.contains("SG"))
					sheet = sheet.getSpreadSheet().getSheet(16);
				else if(testName.contains("IS"))
					sheet = sheet.getSpreadSheet().getSheet(17);

				// Call readCsvFile
				String sResult = readCsvFile(testName);
				for(int j=1;j<sheet.getRowCount();j++) {
					if(testName.equals(sheet.getValueAt(0,j).toString())) {
						if (sResult == failed) {
							sheet.getCellAt("I"+(j+1)).setBackgroundColor(Color.CYAN);
							sheet.getCellAt("I"+(j+1)).setValue(failed);
							sheet.getCellAt("L"+(j+1)).setBackgroundColor(Color.CYAN);
							sheet.getCellAt("L"+(j+1)).setValue(csvScriptException);
							sheet.getCellAt("R"+(j+1)).setBackgroundColor(Color.CYAN);
							sheet.getCellAt("R"+(j+1)).setValue(csvErrorReport);					   
				   		}
				   		else if (sResult == passed) {
				   			sheet.getCellAt("I"+(j+1)).setBackgroundColor(Color.ORANGE);
				    		sheet.getCellAt("I"+(j+1)).setValue(passed);
				   		}
						break;
					}
				}
			}
			
			File outputFile = new File(GlobalVariables.configuration.getCurrentDir().getCanonicalPath().toString()+ File.separator+"Reports\\UIAutomationReport" + "\\FunctionalTestCase.ods");
//			File outputFile = new File(GlobalVariables.configuration.getReportDstDirectoryPath() + "\\FunctionalTestCase.ods");
			sheet.getSpreadSheet().saveAs(outputFile);
		}
		catch(Exception e) {
			throw new UIAutomationException("\nError Occured in UpdateTestCaseSheetResultForFunctionalTestCases Function.");
			
		}
	}
	
	/**
	 * Read TestCaseId for Automation UI (i.e. Home.java)
	 * @param sheetNumber
	 * @return
	 * @throws UIAutomationException 
	 * @throws IOException
	 * @throws XMLStreamException
	 */
	public static String[] readTestCaseId(int sheetNumber) throws UIAutomationException	{
		int index = 0;
		try {
			File currentDir=new File(".");
			String path= currentDir.getCanonicalPath().toString() + "\\TestCases\\";
			File file=new File(path+"Mind-AllianceTestCaseSheet.ods");
			
			// TestCase sheet: Tree_Navigation_Views
			Sheet sheet = SpreadSheet.createFromFile(file).getSheet(sheetNumber);
			String[] arrayOfTestCaseId = new String[600];
			testName = null;
			index=0;
			for (int i = 2; i <= sheet.getRowCount() ; i++){
				testName = sheet.getCellAt("A"+i).getValue().toString();
				if(testName!=null){
					if (sheet.getCellAt("I"+i).getValue().toString().toUpperCase().equals(automatesYes)) {
						arrayOfTestCaseId[index] = testName;
						index++;
					}
				}
				
			}
			sheet.detach();
			return arrayOfTestCaseId;
		}
		catch(Exception e) {
			throw new UIAutomationException("\nError Occured in readTestCaseId() function. \n");
			
			
		}
	}

	/**
	 * Read Result Csv File
	 * @throws UIAutomationException 
	 * @throws IOException
	 */
	public static String readCsvFile(String sTestCaseId) throws UIAutomationException {
		try {
			csvResult = passed;
			csvScriptException = blank;
			csvErrorReport = blank;
			CsvReader csvTestCase = new CsvReader(GlobalVariables.configuration.getCurrentDir().getCanonicalPath().toString()+ File.separator+"Logs\\UILogs" + "\\Results.csv");
//			CsvReader csvTestCase = new CsvReader(GlobalVariables.configuration.getLogDirectoryPath() + "\\Results.csv");
			csvTestCase.readHeaders();
			while (csvTestCase.readRecord()) {
				if (sTestCaseId.equals(csvTestCase.get("TestCaseId")) && csvTestCase.get("Result").equals(failed))
				{
					csvResult = csvTestCase.get("Result");
					csvScriptException = csvTestCase.get("ScriptException");
					csvErrorReport = csvTestCase.get("ErrorReport");
				}
			}
			csvTestCase.close();
			if (csvResult.equals(failed))
				return failed;
			else if(csvResult.equals(passed))
				return passed;
			else
				return notRun;
		}
		catch(Exception e) {
			throw new UIAutomationException("\nError in readCsvFile() Function.\n");
			
		}
	}
		
	/**
	 * Generate AutomationReport in Ods
	 * @throws UIAutomationException 
	 * @throws IOException
	 * @throws XMLStreamException
	 */
	public void generateAutomationReport() throws UIAutomationException {
		try {
				GlobalVariables.configuration.setEndtime(LogFunctions.getDateTime());
				// Update Test Case Execution Result
				updateTestCaseExecutionResult();		
						
				
				// No. of Test Cases Passed and Failed
				totalNoOfTestCasesPassed = testCasesPassed;
				totalNoOfTestCasesFailed = testCasesFailed;
				
				// Test Case Index
				generateTestCaseIndex();
				
				// Test Case Summary
				generateTestCaseSummary();
				
				// Final Test Pass Report
				generateFinalTestPassReport();
				
				// Failure Report
				generateFailureReport();
				
				System.out.println("Report generated successfully");
			}
		catch(Exception e) {
			throw new UIAutomationException("\nError Occured in generateAutomationReport() function.\n");			
		}
	}

	/**
	 * Generate Final TestPass Report
	 * @throws UIAutomationException 
	 * @throws XMLStreamException
	 * @throws IOException
	 */
	private static void generateFinalTestPassReport() throws UIAutomationException {
		try {
			OutputStream destination = new FileOutputStream(GlobalVariables.configuration.getCurrentDir().getCanonicalPath().toString()+ File.separator+"Reports\\UIAutomationReport" + "\\index.htm");
//			OutputStream destination = new FileOutputStream(GlobalVariables.configuration.getReportDstDirectoryPath() + "\\index.htm");
			XMLOutputFactory outputFactory = XMLOutputFactory.newInstance();
			XMLStreamWriter xml = outputFactory.createXMLStreamWriter(destination);

			xml.writeStartDocument();
			xml.writeStartElement("html");
				xml.writeDefaultNamespace("http://www.w3.org/1999/xhtml");
				xml.writeStartElement("head");
					xml.writeStartElement("title");
						xml.writeCharacters("Mind-Alliance UIAutomation TestPass Report: " + reportDirectoryName);
					xml.writeEndElement();
				xml.writeEndElement();
				xml.writeStartElement("frameset");
					xml.writeAttribute("rows", "25%,*");
					xml.writeAttribute("border", "0");
					xml.writeStartElement("frame");
						xml.writeAttribute("src","TestPassSummary.htm");
					xml.writeEndElement();
					xml.writeStartElement("frameset");
						xml.writeAttribute("cols", "35%,*");
						xml.writeStartElement("frame");
							xml.writeAttribute("src", "TestCaseList.htm");
						xml.writeEndElement();
						xml.writeStartElement("frame");
							xml.writeAttribute("name", "targetframe");
						xml.writeEndElement();
					xml.writeEndElement();
				xml.writeEndElement();
			xml.writeEndElement();
			xml.writeEndDocument();
			xml.close();
			destination.close();
		}
		catch(Exception e) {
			throw new UIAutomationException("\nError Occured in generateFinalTestPassReport() function. \n");
			
		}
	}

	/**
	 *  Generate TestCase Summary
	 * @throws UIAutomationException 
	 * @throws XMLStreamException
	 * @throws IOException
	 */
	private static void generateTestCaseSummary() throws UIAutomationException {
		try {
			
			OutputStream destination = new FileOutputStream(GlobalVariables.configuration.getCurrentDir().getCanonicalPath().toString()+ File.separator+"Reports\\UIAutomationReport"+"\\TestPassSummary.htm");
//			OutputStream destination = new FileOutputStream(GlobalVariables.configuration.getReportDstDirectoryPath()+"\\TestPassSummary.htm");
//			OutputStream destination = new FileOutputStream(reportDstDirectoryPath + "\\TestPassSummary.htm");
			XMLOutputFactory outputFactory = XMLOutputFactory.newInstance();
			XMLStreamWriter xml = outputFactory.createXMLStreamWriter(destination);

			xml.writeStartDocument();
			xml.writeStartElement("html");
				xml.writeDefaultNamespace("http://www.w3.org/1999/xhtml");
				xml.writeStartElement("head");
					xml.writeStartElement("title");
						xml.writeCharacters("TestCaseId Summary");
					xml.writeEndElement();
				xml.writeEndElement();
				xml.writeStartElement("body");
					xml.writeStartElement("table");
						xml.writeAttribute("border","0");
						xml.writeAttribute("cellpadding","0");
						xml.writeAttribute("cellspacing","0");
						xml.writeAttribute("width","100%");
						xml.writeStartElement("tr");
							xml.writeAttribute("bgColor","#DDDDDD");
							xml.writeStartElement("td");
								xml.writeStartElement("center");
									xml.writeCharacters("Start Datetime: " + GlobalVariables.configuration.getStartTime());
									xml.writeEmptyElement("br");
									xml.writeCharacters("End Datetime: " + GlobalVariables.configuration.getEndtime());
									xml.writeEmptyElement("br");
									xml.writeCharacters("Browser: " + GlobalVariables.configuration.getConfigData().get("Browser"));
									xml.writeEmptyElement("br");
								xml.writeEndElement();
							xml.writeEndElement();
							xml.writeStartElement("td");
								xml.writeStartElement("center");
									xml.writeStartElement("img");
										xml.writeAttribute("src","../../images/Mind-Alliance_Logo.png");
										xml.writeAttribute("style","border-style: none");
									xml.writeEndElement();
								xml.writeEndElement();
							xml.writeEndElement();
							xml.writeStartElement("td");
								xml.writeStartElement("center");
									xml.writeCharacters("Number of TestCases Executed: " + (totalNoOfTestCasesPassed + totalNoOfTestCasesFailed));	
									xml.writeEmptyElement("br");
									xml.writeCharacters("Number of TestCases Passed: " + totalNoOfTestCasesPassed);
									xml.writeEmptyElement("br");
									xml.writeCharacters("Number of TestCases Failed: " + totalNoOfTestCasesFailed);
								xml.writeEndElement();
							xml.writeEndElement();
						xml.writeEndElement();
					xml.writeEndElement();
				xml.writeEndElement();
			xml.writeEndElement();
			xml.writeEndDocument();
			xml.close();
			destination.close();
		}
		catch(Exception e) {
			throw new UIAutomationException("\nError Occured in generateTestCaseSummary() function. \n");
			
		}
	}
/**
 * Method to generate Failure Reports
 * @return void
 * @throws UIAutomationException 
 */
	private static void generateFailureReport() throws UIAutomationException {
		try {
			OutputStream destination = new FileOutputStream(GlobalVariables.configuration.getCurrentDir().getCanonicalPath().toString()+ File.separator+"Reports\\UIAutomationReport"+ "\\TestCaseFailureList.htm");
//			OutputStream destination = new FileOutputStream(GlobalVariables.configuration.getReportDstDirectoryPath() + "\\TestCaseFailureList.htm");
			XMLOutputFactory outputFactory = XMLOutputFactory.newInstance();
			XMLStreamWriter xml = outputFactory.createXMLStreamWriter(destination);

			xml.writeStartDocument();
			xml.writeStartElement("html");
				xml.writeDefaultNamespace("http://www.w3.org/1999/xhtml");
				xml.writeStartElement("head");
					xml.writeStartElement("title");
						xml.writeCharacters("Mind-Alliance Failure Report");
					xml.writeEndElement();
				xml.writeEndElement();
				xml.writeStartElement("body");
					xml.writeStartElement("table");
						xml.writeAttribute("border","0");
						xml.writeAttribute("cellpadding","0");
						xml.writeAttribute("cellspacing","0");
						xml.writeAttribute("width","100%");
						xml.writeStartElement("caption");
							xml.writeStartElement("strong");
								xml.writeCharacters("Mind-Alliance Test scripts Failure Report");
							xml.writeEndElement();
							xml.writeStartElement("tr");
								xml.writeAttribute("bgColor","#DDDDDD");
								xml.writeStartElement("td");
								xml.writeStartElement("center");
									xml.writeCharacters("Start Datetime: " + startDateTime);
									xml.writeEmptyElement("br");
									xml.writeCharacters("End Datetime: " + endDateTime);
									xml.writeEmptyElement("br");
									xml.writeCharacters("Browser: " + GlobalVariables.configuration.getConfigData().get("Browser"));
									xml.writeEmptyElement("br");
								xml.writeEndElement();
							xml.writeEndElement();
							xml.writeStartElement("td");
								xml.writeStartElement("center");
									xml.writeStartElement("img");
										xml.writeAttribute("src","../../images/Mind-Alliance_Logo.png");
										xml.writeAttribute("style","border-style: solid");
									xml.writeEndElement();
								xml.writeEndElement();
							xml.writeEndElement();
							xml.writeStartElement("td");
								xml.writeStartElement("center");
									xml.writeCharacters("Number of TestCases Executed: " + (totalNoOfTestCasesPassed + totalNoOfTestCasesFailed));
									xml.writeEmptyElement("br");
									xml.writeCharacters("Number of TestCases Passed: " + totalNoOfTestCasesPassed);
									xml.writeEmptyElement("br");
									xml.writeCharacters("Number of TestCases Failed: " + totalNoOfTestCasesFailed);
									xml.writeEmptyElement("br");

								xml.writeEndElement();
							xml.writeEndElement();
						xml.writeEndElement();
//					xml.writeEndElement();
				xml.writeEndElement();
			xml.writeEndElement();
			xml.writeEndDocument();
			
			xml.writeStartDocument();
			xml.writeStartElement("html");
				xml.writeDefaultNamespace("http://www.w3.org/1999/xhtml");
				xml.writeStartElement("head");
					xml.writeStartElement("title");
						xml.writeCharacters("TestCaseId Index");
					xml.writeEndElement();
				xml.writeEndElement();
				xml.writeStartElement("body");
					xml.writeEmptyElement("br");
					xml.writeStartElement("table");
						xml.writeAttribute("border", "0");
						xml.writeAttribute("width","100%");
						xml.writeStartElement("th");
							xml.writeStartElement("tr");
								xml.writeStartElement("td");
									xml.writeAttribute("bgColor","#BBBBBB");
									xml.writeAttribute("onMouseover", "this.bgColor='#DDDDDD'");
									xml.writeAttribute("onMouseout", "this.bgColor='#BBBBBB'");
									xml.writeStartElement("strong");
										xml.writeCharacters("TestCaseId");
									xml.writeEndElement();
								xml.writeEndElement();
								xml.writeStartElement("td");
									xml.writeAttribute("bgColor","#BBBBBB");
									xml.writeAttribute("onMouseover", "this.bgColor='#DDDDDD'");
									xml.writeAttribute("onMouseout", "this.bgColor='#BBBBBB'");
									xml.writeStartElement("center");
										xml.writeStartElement("strong");
											xml.writeCharacters("Result");
										xml.writeEndElement();
									xml.writeEndElement();
								xml.writeEndElement();
								xml.writeStartElement("td");
									xml.writeAttribute("bgColor","#BBBBBB");
									xml.writeAttribute("onMouseover", "this.bgColor='#DDDDDD'");
									xml.writeAttribute("onMouseout", "this.bgColor='#BBBBBB'");
									xml.writeStartElement("center");
										xml.writeStartElement("strong");
											xml.writeCharacters("Script Exception");
										xml.writeEndElement();
									xml.writeEndElement();
								xml.writeEndElement();
								xml.writeStartElement("td");
									xml.writeAttribute("bgColor","#BBBBBB");
									xml.writeAttribute("onMouseover", "this.bgColor='#DDDDDD'");
									xml.writeAttribute("onMouseout", "this.bgColor='#BBBBBB'");
									xml.writeStartElement("center");
										xml.writeStartElement("strong");
											xml.writeCharacters("Error Report");
										xml.writeEndElement();
									xml.writeEndElement();
								xml.writeEndElement();
							xml.writeEndElement();
						xml.writeEndElement();
			for (int i = 0; i < GlobalVariables.configuration.getList().getModel().getSize() ;i++) {
				if(GlobalVariables.configuration.getList().getModel().getElementAt(i) != null) {
					csvTestCase = new CsvReader(GlobalVariables.configuration.getCurrentDir().getCanonicalPath().toString()+ File.separator+"Logs\\UILogs" + "\\Results.csv");
//					csvTestCase = new CsvReader(GlobalVariables.configuration.getLogDirectoryPath() + "\\Results.csv");
					csvTestCase.readHeaders();
					while(csvTestCase.readRecord()) {
						if(csvTestCase.get("TestCaseId").equals(GlobalVariables.configuration.getList().getModel().getElementAt(i).toString())) {
							if(csvTestCase.get("ScriptException")!= blank || csvTestCase.get("ErrorReport")!= blank) { 
						xml.writeStartElement("tr");
							xml.writeAttribute("style","WIDTH:235;BORDER:0;OVERFLOW-Y:scroll;WORD-WRAP:BREAK-WORD;OVERFLOW-X:hidden;padding:  2px 0px 2px 5px");
							xml.writeAttribute("bgColor","#DDDDDD");
							xml.writeAttribute("padding","");
							xml.writeStartElement("td");
								xml.writeAttribute("onMouseover", "this.bgColor='#EEEEEE'");
								xml.writeAttribute("onMouseout", "this.bgColor='#DDDDDD'");
								xml.writeStartElement("left");
									xml.writeCharacters(GlobalVariables.configuration.getList().getModel().getElementAt(i).toString());
								xml.writeEndElement();
							xml.writeEndElement();
							xml.writeStartElement("td");
								xml.writeAttribute("onMouseover", "this.bgColor='#EEEEEE'");
								xml.writeAttribute("onMouseout", "this.bgColor='#DDDDDD'");
								xml.writeStartElement("center");
									xml.writeStartElement("font");
									if(arrayOftestCaseResult[i].equals(passed))
										xml.writeAttribute("color", "GREEN");
									else
										xml.writeAttribute("color", "RED");
										xml.writeCharacters(arrayOftestCaseResult[i]);
									xml.writeEndElement();
								xml.writeEndElement();
							xml.writeEndElement();
							xml.writeStartElement("td");
								xml.writeAttribute("onMouseover", "this.bgColor='#EEEEEE'");
								xml.writeAttribute("onMouseout", "this.bgColor='#DDDDDD'");
								xml.writeStartElement("center");
							if(csvTestCase.get("ScriptException")!= blank) {
									xml.writeCharacters(csvTestCase.get("ScriptException"));
								xml.writeEndElement();
							}
							if(csvTestCase.get("ErrorReport")!= blank) {
								xml.writeStartElement("td");
									xml.writeAttribute("onMouseover", "this.bgColor='#EEEEEE'");
									xml.writeAttribute("onMouseout", "this.bgColor='#DDDDDD'");
									xml.writeStartElement("center");
										xml.writeCharacters(csvTestCase.get("ErrorReport"));
									xml.writeEndElement();
								xml.writeEndElement();
							}
							else {
								xml.writeStartElement("td");
									xml.writeAttribute("bgColor","#DDDDDD");
									xml.writeAttribute("onMouseover", "this.bgColor='#EEEEEE'");
									xml.writeAttribute("onMouseout", "this.bgColor='#DDDDDD'");
								xml.writeEndElement();
							}
							xml.writeEndElement();
						xml.writeEndElement();
							}
						}
					}
					csvTestCase.close();
				}
			}
			xml.writeEndElement();
			xml.writeEndElement();
			xml.writeEndElement();
			xml.writeEndDocument();
			xml.close();
			destination.close();
		}
		catch(Exception e) {
			throw new UIAutomationException("\nError Occured in generateFailureReport() function.\n");			
		}
	}		

/**
	 * Generate TestCase Index
	 * @throws XMLStreamException
	 * @throws IOException
	 */
	public static void generateTestCaseIndex() {
		
		String arrayOftestCaseResult[] = new String[600];
		
		try	{
			OutputStream destination = new FileOutputStream(GlobalVariables.configuration.getCurrentDir().getCanonicalPath().toString()+ File.separator+"Reports\\UIAutomationReport"+"\\TestCaseList.htm");
			
//			OutputStream destination = new FileOutputStream(reportDstDirectoryPath + "\\TestCaseList.htm");
			XMLOutputFactory outputFactory = XMLOutputFactory.newInstance();
			XMLStreamWriter xml = outputFactory.createXMLStreamWriter(destination);

			xml.writeStartDocument();
			xml.writeStartElement("html");
				xml.writeDefaultNamespace("http://www.w3.org/1999/xhtml");
				xml.writeStartElement("head");
					xml.writeStartElement("title");
						xml.writeCharacters("TestCaseId Index");
					xml.writeEndElement();
				xml.writeEndElement();
				xml.writeStartElement("body");
					xml.writeStartElement("table");
						xml.writeAttribute("border", "0");
						xml.writeAttribute("width","100%");
						xml.writeStartElement("th");
							xml.writeStartElement("tr");
								xml.writeStartElement("td");
									xml.writeAttribute("bgColor","#BBBBBB");
									xml.writeAttribute("onMouseover", "this.bgColor='#DDDDDD'");
									xml.writeAttribute("onMouseout", "this.bgColor='#BBBBBB'");
									xml.writeStartElement("strong");
										xml.writeCharacters("TestCaseId");
									xml.writeEndElement();
								xml.writeEndElement();
								xml.writeStartElement("td");
									xml.writeAttribute("bgColor","#BBBBBB");
									xml.writeAttribute("onMouseover", "this.bgColor='#DDDDDD'");
									xml.writeAttribute("onMouseout", "this.bgColor='#BBBBBB'");
									xml.writeStartElement("center");
										xml.writeStartElement("strong");
											xml.writeCharacters("Result");
										xml.writeEndElement();
									xml.writeEndElement();
								xml.writeEndElement();
							xml.writeEndElement();
						xml.writeEndElement();
				for (int i = 0; i < GlobalVariables.configuration.getList().getModel().getSize() ; i++) {
					if(GlobalVariables.configuration.getList().getModel().getElementAt(i) != null) {
						xml.writeStartElement("tr");
							xml.writeAttribute("style","WIDTH:235;BORDER:0;OVERFLOW-Y:scroll;WORD-WRAP:BREAK-WORD;OVERFLOW-X:hidden;padding:  2px 0px 2px 5px");
							xml.writeAttribute("bgColor","#DDDDDD");
							xml.writeAttribute("padding","");
							xml.writeAttribute("onMouseover", "this.bgColor='#EEEEEE'");
							xml.writeAttribute("onMouseout", "this.bgColor='#DDDDDD'");
							xml.writeStartElement("td");
								xml.writeStartElement("a");
									xml.writeAttribute("href", GlobalVariables.configuration.getList().getModel().getElementAt(i) + ".htm");
									xml.writeAttribute("target", "targetframe");
									xml.writeCharacters(GlobalVariables.configuration.getList().getModel().getElementAt(i).toString());
								xml.writeEndElement();
							xml.writeEndElement();
							xml.writeStartElement("td");
								xml.writeStartElement("center");
									xml.writeStartElement("font");
									
									arrayOftestCaseResult=GlobalVariables.configuration.getArrayOftestCaseResult();
																	
									if (arrayOftestCaseResult[i].equals(passed))
										xml.writeAttribute("color", "GREEN");
									else
										xml.writeAttribute("color", "RED");
										xml.writeCharacters(arrayOftestCaseResult[i]);
									xml.writeEndElement();
								xml.writeEndElement();
							xml.writeEndElement();
						xml.writeEndElement();
					}
				}
					xml.writeEndElement();
				xml.writeEndElement();
			xml.writeEndDocument();
			xml.close();
			destination.close();
		}
		catch(Exception e) {
			System.out.println("\nError Occured in generateTestCaseIndex() function. \n");
			e.printStackTrace();
		}
	}

	/**
	 * Generate AutomationReport in HTML
	 * @throws UIAutomationException 
	 * @throws IOException
	 * @throws XMLStreamException
	 */
	public static void generateAutomationReportInHtml(String testName) throws UIAutomationException {
	try {
		csvTestCase = new CsvReader(GlobalVariables.configuration.getCurrentDir().getCanonicalPath().toString()+ File.separator+"Logs\\UILogs" + "\\Results.csv");
//		csvTestCase = new CsvReader(GlobalVariables.configuration.getLogDirectoryPath() + "\\Results.csv");
		
		OutputStream destination = new FileOutputStream(GlobalVariables.configuration.getCurrentDir().getCanonicalPath().toString()+ File.separator+"Reports\\UIAutomationReport" + "\\" + testName + ".htm");
//		OutputStream destination = new FileOutputStream(GlobalVariables.configuration.getReportDstDirectoryPath() + "\\" + testName + ".htm");
		XMLOutputFactory outputFactory = XMLOutputFactory.newInstance();
		XMLStreamWriter xml = outputFactory.createXMLStreamWriter(destination);

		xml.writeStartDocument();
		xml.writeStartElement("html");
			xml.writeDefaultNamespace("http://www.w3.org/1999/xhtml");
			xml.writeStartElement("head");
		 		xml.writeStartElement("title");
		 			xml.writeCharacters("TestCaseId: " + testName);
		 		xml.writeEndElement();
		 	xml.writeEndElement();
		 	xml.writeStartElement("body");
		 		xml.writeCharacters("TestCase Id: " + testName);
		 		xml.writeStartElement("table");
		 			xml.writeAttribute("border", "0");
		 			xml.writeAttribute("width","100%");
		 			xml.writeStartElement("th");
		 				xml.writeStartElement("tr");
		 					xml.writeStartElement("td");
		 						xml.writeAttribute("bgColor","#BBBBBB");
		 						xml.writeAttribute("onMouseover", "this.bgColor='#DDDDDD'");
		 						xml.writeAttribute("onMouseout", "this.bgColor='#BBBBBB'");
		 						xml.writeStartElement("center");
		 							xml.writeStartElement("strong");
		 								xml.writeCharacters("Step No");
		 							xml.writeEndElement();
		 						xml.writeEndElement();
		 					xml.writeEndElement();
		 					xml.writeStartElement("td");
		 						xml.writeAttribute("bgColor","#BBBBBB");
		 						xml.writeAttribute("onMouseover", "this.bgColor='#DDDDDD'");
		 						xml.writeAttribute("onMouseout", "this.bgColor='#BBBBBB'");
		 						xml.writeStartElement("center");
		 							xml.writeStartElement("strong");
		 								xml.writeCharacters("Description");
		 							xml.writeEndElement();
		 						xml.writeEndElement();
		 					xml.writeEndElement();
		 					xml.writeStartElement("td");
		 						xml.writeAttribute("bgColor","#BBBBBB");
		 						xml.writeAttribute("onMouseover", "this.bgColor='#DDDDDD'");
		 						xml.writeAttribute("onMouseout", "this.bgColor='#BBBBBB'");
		 						xml.writeStartElement("center");
		 							xml.writeStartElement("strong");
		 								xml.writeCharacters("Result");
		 							xml.writeEndElement();
		 						xml.writeEndElement();
		 					xml.writeEndElement();
		 					xml.writeStartElement("td");
		 						xml.writeAttribute("bgColor","#BBBBBB");
		 						xml.writeAttribute("onMouseover", "this.bgColor='#DDDDDD'");
		 						xml.writeAttribute("onMouseout", "this.bgColor='#BBBBBB'");
		 						xml.writeStartElement("center");
		 							xml.writeStartElement("strong");
		 								xml.writeCharacters("Script Exception");
		 							xml.writeEndElement();
		 						xml.writeEndElement();
		 					xml.writeEndElement();
		 					xml.writeStartElement("td");
		 						xml.writeAttribute("bgColor","#BBBBBB");
		 						xml.writeAttribute("onMouseover", "this.bgColor='#DDDDDD'");
		 						xml.writeAttribute("onMouseout", "this.bgColor='#BBBBBB'");
		 						xml.writeStartElement("center");
									xml.writeStartElement("strong");
										xml.writeCharacters("Error Report");
									xml.writeEndElement();
								xml.writeEndElement();
							xml.writeEndElement();
						xml.writeEndElement();
					xml.writeEndElement();
			csvTestCase.readHeaders();
			while(csvTestCase.readRecord()) {
				if(testName.equals(csvTestCase.get("TestCaseId"))) {
					xml.writeStartElement("tr");
						xml.writeAttribute("style","WIDTH:235;BORDER:0;OVERFLOW-Y:scroll;WORD-WRAP:BREAK-WORD;OVERFLOW-X:hidden;padding:  2px 0px 2px 5px");
						xml.writeAttribute("bgColor","#DDDDDD");
						xml.writeAttribute("padding","");
						xml.writeAttribute("onMouseover", "this.bgColor='#EEEEEE'");
						xml.writeAttribute("onMouseout", "this.bgColor='#DDDDDD'");
						xml.writeStartElement("td");
							xml.writeStartElement("center");
								xml.writeCharacters(csvTestCase.get("VerificationStepNo"));
							xml.writeEndElement();
						xml.writeEndElement();
						xml.writeStartElement("td");
							xml.writeCharacters(csvTestCase.get("Description"));
						xml.writeEndElement();
						xml.writeStartElement("td");
							xml.writeStartElement("center");
								xml.writeStartElement("font");
								if(csvTestCase.get("Result").equals(passed))
									xml.writeAttribute("color", "GREEN");
								else
									xml.writeAttribute("color", "RED");
									xml.writeCharacters(csvTestCase.get("Result"));
								xml.writeEndElement();
							xml.writeEndElement();
						xml.writeEndElement();
					if (csvTestCase.get("ScriptException") != blank) {
						xml.writeStartElement("td");
							xml.writeCharacters(csvTestCase.get("ScriptException"));
					}
					else {
							xml.writeStartElement("td");
					}
						xml.writeEndElement();
					if (csvTestCase.get("ErrorReport") != blank) {
						xml.writeStartElement("td");
							xml.writeCharacters(csvTestCase.get("ErrorReport"));
					}
					else {
						xml.writeStartElement("td");
					}
						xml.writeEndElement();
					xml.writeEndElement();
				}
			}
				xml.writeEndElement();
			xml.writeEndElement();
		xml.writeEndElement();
		xml.writeEndDocument();
		xml.close();
		destination.close();
		csvTestCase.close();
		}
		catch(Exception e) 
		{
			throw new UIAutomationException("\nError in generateAutomationReportInHtml() function. \n");		
		}
	}
/**
 * Method to read Test Case ID's
 * @param sheetNumber
 * @return
 * @throws UIAutomationException 
 */
	public static String[] readTestCaseIdForFunctional(int sheetNumber) throws UIAutomationException {
		int index = 0;
		try {
			File file1 = new File(GlobalVariables.configuration.getCurrentDir().getCanonicalPath().toString() + "\\TestCases\\FunctionalTestCase.ods");
			// TestCase sheet: Tree_Navigation_Views
			Sheet sheet1 = SpreadSheet.createFromFile(file1).getSheet(sheetNumber);
			String[] arrayOfTestCaseId = new String[600];
			testName = null;
			index=0;
			for (int i = 2; i <= sheet1.getRowCount() ; i++){
				testName = sheet1.getCellAt("A"+i).getValue().toString();
				if (sheet1.getCellAt("J"+i).getValue().toString().toUpperCase().equals(automatesYes)) {
					arrayOfTestCaseId[index] = testName;
					index++;
				}
			}
			sheet1.detach();
			return arrayOfTestCaseId;
		}
		catch(Exception e) {
			throw new UIAutomationException("\nError Occured in readTestCaseIdForFunctional() function.\n");
			
		}
	}
/**
 * Method to Generate Automation report
 * @return void
 * @throws UIAutomationException 
 */
	public static void generateAutomationReportForFunctionalTestCases() throws UIAutomationException {
		try {
			// Update Test Case Execution Result
			updateTestCaseExecutionResult();
			
			// Update Test Case Sheet Execution Result
//			updateTestCaseSheetResultForFunctionalTestCases();
			
			// No. of Test Cases Passed and Failed
			totalNoOfTestCasesPassed = testCasesPassed;
			totalNoOfTestCasesFailed = testCasesFailed;
			
			// Test Case Index
			generateTestCaseIndex();
			
			// Test Case Summary
			generateTestCaseSummary();
			
			// Final Test Pass Report
			generateFinalTestPassReport();
			
			// Failure Report
			generateFailureReport();
			
			System.out.println("Report generated successfully");
		}
		catch(Exception e) {
			throw new UIAutomationException("\nError Occured in generateAutomationReportForFunctionalTestCases() function.");
			
		}
	}
}