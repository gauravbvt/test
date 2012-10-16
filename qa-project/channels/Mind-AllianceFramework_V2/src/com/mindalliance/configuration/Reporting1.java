package com.mindalliance.configuration;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.jopendocument.dom.spreadsheet.Sheet;
import org.jopendocument.dom.spreadsheet.SpreadSheet;

import com.csvreader.CsvReader;

public class Reporting1 {
	public static int totalNoOfTestCasesPassed = 0;
	public static int totalNoOfTestCasesFailed = 0;
	static int index = 0;
	static int testCasesAutomated = 0;
	static int testCasesPassed = 0;
	static int testCasesFailed = 0;
	static CsvReader products;
	static CsvReader csvTestCase;
	String sSummary;
	static String sCsvResult;
	static String sCsvScriptException;
	static String sCsvErrorReport;
	static String arrayOfTestCaseId[] = new String[500];
	static String arrayOftestCaseSummary[] = new String[400];
	static String arrayOftestCaseResult[] = new String[400];
	static String stestName;
	public static String sPassed = "Pass";
	public static String sFailed = "Fail";
	public static String blank = "";
	public static String sNotRun = "NOT RUN";
	public static String sAutomatesYes = "YES";
	public static String startDateTime;
	public static String endDateTime;
	
	
	/**
	 * Update TestCase Execution Result
	 * @param sheet
	 * @throws IOException
	 * @throws XMLStreamException
	 */
	public static void updateTestCaseExecutionResult(Sheet sheet) throws UIAutomationException {
		 testCasesPassed = 0;
		 testCasesFailed = 0;
		for (int i = 2; i < sheet.getRowCount() ; i++){
			   stestName = sheet.getCellAt("A"+i).getValue().toString();
			   if (stestName != blank) {
				   // Call readCsvFile
				   String sResult = readCsvFile(stestName);
				   if (sResult == sFailed) {
					   sheet.getCellAt("J"+i).setValue(sFailed);
					   sheet.getCellAt("K"+i).setValue(sCsvScriptException);
					   sheet.getCellAt("L"+i).setValue(sCsvErrorReport);
					   // Call generateAutomationReportInHtml()
					   generateAutomationReportInHtml(stestName);
					   testCasesFailed ++;
					   arrayOfTestCaseId[index] = stestName;
					   arrayOftestCaseResult[index] = sFailed;
					   arrayOftestCaseSummary[index++] = sheet.getCellAt("B"+i).getValue().toString();
				   }
				   else if (sResult ==sPassed) {
					   sheet.getCellAt("J"+i).setValue(sPassed);
					   // Call generateAutomationReportInHtml()
					   generateAutomationReportInHtml(stestName);
					   testCasesPassed ++;
					   arrayOfTestCaseId[index] = stestName;
					   arrayOftestCaseResult[index] =sPassed;
					   arrayOftestCaseSummary[index++] = sheet.getCellAt("B"+i).getValue().toString();
				   }
				   else if (sResult == sNotRun)
					   sheet.getCellAt("J"+i).setValue(sNotRun);
			   }
			}
	}
	
	/**
	 * Read TestCaseId for Automation UI (i.e. Home.java)
	 * @param sheetNumber
	 * @return
	 * @throws IOException
	 * @throws XMLStreamException
	 */
	public static String[] readTestCaseId(int sheetNumber) throws IOException, XMLStreamException {
		 File file = new File(GlobalVariables.configuration.getCurrentDir().getCanonicalPath().toString() + "\\TestCases\\Mind-AllianceTestCaseSheet.ods");
		 // TestCase sheet: Tree_Navigation_Views
		 Sheet sheet = SpreadSheet.createFromFile(file).getSheet(sheetNumber);
		 int iIndex = 0;
		 String[] arrayOfTestCaseId = new String[500];
		 stestName = null;
     		 for (int i = 2; i <= sheet.getRowCount() ; i++){
			   stestName = sheet.getCellAt("A"+i).getValue().toString();
  			   if (stestName.length() !=0) { 
  				     if (sheet.getCellAt("I"+i).getValue().toString().toUpperCase().equals(sAutomatesYes)) {
				      arrayOfTestCaseId[iIndex] = stestName;
				      iIndex++;
  				     }
			   }
		}
     		 sheet.detach();
		 return arrayOfTestCaseId;
	}
	
	/**
	 * Read Result Csv File
	 * @throws IOException 
	 * @throws UIAutomationException 
	 */
	public static String readCsvFile(String sTestCaseId) throws  UIAutomationException{
		try{
			int isAvailable = 0;
			CsvReader csvTestCase = new CsvReader(GlobalVariables.configuration.getLogDirectoryPath() + "\\Results.csv");
			csvTestCase.readHeaders();
			while(csvTestCase.readRecord()) {
				if (sTestCaseId.equals(csvTestCase.get("TestCaseId"))) {
						isAvailable = 1;
						break;
				}
			}
			csvTestCase.close();
			sCsvResult = blank;
			sCsvScriptException = blank;
			sCsvErrorReport = blank;
			csvTestCase = new CsvReader(GlobalVariables.configuration.getLogDirectoryPath()+ "\\Results.csv");
			if (isAvailable == 1) {
				csvTestCase.readHeaders();
			while (csvTestCase.readRecord()) {
					if (sTestCaseId.equals(csvTestCase.get("TestCaseId"))) {
						sCsvResult = csvTestCase.get("Result");
						sCsvScriptException = csvTestCase.get("ScriptException");
						sCsvErrorReport = csvTestCase.get("ErrorReport");
					}
					if (sCsvResult.equals(sFailed))
						return sFailed;
				}
			return sPassed;
			}
			else
				return sNotRun;
		}
		catch(IOException ie){
			throw new UIAutomationException("File Results.csv not found at specified path '"+GlobalVariables.configuration.getLogDirectoryPath() + "'\\Results.csv");
		}
		
	}
	
	/**
	 * Generate AutomationReport in Ods
	 * @throws UIAutomationException 
	 * @throws IOException 
	 * @throws XMLStreamException 
	 */
	public static void generateAutomationReport() throws UIAutomationException{
		try{
			// Initialize the variables
			index = 0;
			Arrays.fill(arrayOfTestCaseId, null);
			// Load the ODF document from the path
			File file = new File(GlobalVariables.configuration.getCurrentDir().getCanonicalPath().toString() + "\\TestCases\\Mind-AllianceTestCaseSheet.ods");
			// TestCase sheet: Tree_Navigation_Views
			Sheet sheet = SpreadSheet.createFromFile(file).getSheet(1);
			updateTestCaseExecutionResult(sheet);
			// Generate Summary Sheet 
			sheet = sheet.getSpreadSheet().getSheet(0);
			sheet.getCellAt("G8").setValue((testCasesPassed + testCasesFailed));
			sheet.getCellAt("H8").setValue(testCasesPassed);
			sheet.getCellAt("I8").setValue(testCasesFailed);
			// totalNoOfTestCasesPassed & totolNoOfTestCasesFailed
			totalNoOfTestCasesPassed = testCasesPassed;
			totalNoOfTestCasesFailed = testCasesFailed;
			// TestCase sheet: Plan
			sheet = sheet.getSpreadSheet().getSheet(2);
			updateTestCaseExecutionResult(sheet);
			// Generate Summary Sheet
			sheet = sheet.getSpreadSheet().getSheet(0);
			sheet.getCellAt("G9").setValue((testCasesPassed + testCasesFailed));
			sheet.getCellAt("H9").setValue(testCasesPassed);
			sheet.getCellAt("I9").setValue(testCasesFailed);
			// totalNoOfTestCasesPassed & totolNoOfTestCasesFailed
			totalNoOfTestCasesPassed = totalNoOfTestCasesPassed + testCasesPassed;
			totalNoOfTestCasesFailed = totalNoOfTestCasesFailed + testCasesFailed;
			// TestCase sheet: Command_Execution_Undo_and_Redo
			sheet = sheet.getSpreadSheet().getSheet(3);
			updateTestCaseExecutionResult(sheet);
			// Generate Summary Sheet 
			sheet = sheet.getSpreadSheet().getSheet(0);
			sheet.getCellAt("G10").setValue((testCasesPassed + testCasesFailed));
			sheet.getCellAt("H10").setValue(testCasesPassed);
			sheet.getCellAt("I10").setValue(testCasesFailed);
			// totalNoOfTestCasesPassed & totolNoOfTestCasesFailed
			totalNoOfTestCasesPassed = totalNoOfTestCasesPassed + testCasesPassed;
			totalNoOfTestCasesFailed = totalNoOfTestCasesFailed + testCasesFailed;
			
			File outputFile = new File(GlobalVariables.configuration.getReportDstDirectoryPath()+ "\\Mind-AllianceTestCaseSheet.ods");
			sheet.getSpreadSheet().saveAs(outputFile);
			
			generateTestCaseIndex();
			generateTestCaseSummary();
			generateFinalTestPassReport();
			System.out.println("Report generated successfully");
		}
		catch(IOException ie){
			throw new UIAutomationException("File Mind-AllianceTestCaseSheet.ods not found at specified path.");
		}
	}
	
	/**
	 * Generate Final TestPass Report
	 * @throws XMLStreamException 
	 * @throws IOException 
	 * @throws UIAutomationException 
	 */
	private static void generateFinalTestPassReport() throws UIAutomationException {
		try{
			OutputStream destination = new FileOutputStream(GlobalVariables.configuration.getReportDstDirectoryPath() + "\\index.htm");
			XMLOutputFactory outputFactory = XMLOutputFactory.newInstance();
			XMLStreamWriter xml = outputFactory.createXMLStreamWriter(destination);

			xml.writeStartDocument();
			xml.writeStartElement("html");
			xml.writeDefaultNamespace("http://www.w3.org/1999/xhtml");
			xml.writeStartElement("head");
			xml.writeStartElement("title");
			xml.writeCharacters("Mind-Alliance Automation TestPass Report: " + "D:\\Channels\\Mind-AllianceFramework_V2\\Reports");
			xml.writeEndElement();
			xml.writeEndElement();

			xml.writeStartElement("frameset");
			xml.writeAttribute("rows", "20%,*");
			xml.writeAttribute("border", "0");
			xml.writeStartElement("frame");
			xml.writeAttribute("src", "TestPassSummary.htm");
			xml.writeEndElement();
			xml.writeStartElement("frameset");
			xml.writeAttribute("cols", "30%,*");
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
		catch(XMLStreamException xe){
			throw new UIAutomationException("Can not create index.htm file.");
			
		}
		catch (IOException ie) {
			throw new UIAutomationException("File index.htm not found at specified path '"+GlobalVariables.configuration.getReportDstDirectoryPath() + "'\\index.htm");
		}
		
		
	}

	/**
	 *  Generate TestCase Summary
	 * @throws XMLStreamException 
	 * @throws IOException 
	 * @throws UIAutomationException 
	 */
	private static void generateTestCaseSummary() throws UIAutomationException {
		try{
			startDateTime = LogFunctions.getDateTime();
			
			OutputStream destination = new FileOutputStream(GlobalVariables.configuration.getReportDstDirectoryPath() + "\\TestPassSummary.htm");
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
			xml.writeCharacters("Browser: "+GlobalVariables.configuration.getConfigData().get("Browser"));
			xml.writeEmptyElement("br");
			xml.writeEndElement();
			xml.writeEndElement();
			
			xml.writeStartElement("td");
			xml.writeStartElement("center");
			xml.writeStartElement("img");
			xml.writeAttribute("src","../../Images/Mind-Alliance_Logo.png");
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
			xml.close();
			destination.close();
		}
		catch(XMLStreamException xe){
			throw new UIAutomationException("Can not create TestPassSummary.htm file.");
		}
		catch (IOException e) {
			throw new UIAutomationException("File TestPassSummary.htm File not found at specified path '"+GlobalVariables.configuration.getReportDstDirectoryPath() + "' \\TestPassSummary.htm");
		}
		
		
	}

	/**
	 * Generate TestCase Index
	 * @throws XMLStreamException 
	 * @throws IOException 
	 * @throws UIAutomationException 
	 */
	private static void generateTestCaseIndex() throws UIAutomationException{
		try{
		
		OutputStream destination = new FileOutputStream(GlobalVariables.configuration.getReportDstDirectoryPath() + "\\TestCaseList.htm");
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
		for (int i = 0; i < arrayOfTestCaseId.length ; i++) {
			if(arrayOfTestCaseId[i] != null) {
				xml.writeStartElement("tr");
				xml.writeAttribute("style","WIDTH:235;BORDER:0;OVERFLOW-Y:scroll;WORD-WRAP:BREAK-WORD;OVERFLOW-X:hidden;padding:  2px 0px 2px 5px");
				xml.writeAttribute("bgColor","#DDDDDD");
				xml.writeAttribute("padding","");
				xml.writeAttribute("onMouseover", "this.bgColor='#EEEEEE'");
				xml.writeAttribute("onMouseout", "this.bgColor='#DDDDDD'");
					xml.writeStartElement("td");
							xml.writeStartElement("a");
							xml.writeAttribute("href", arrayOfTestCaseId[i] + ".htm");
							xml.writeAttribute("target", "targetframe");
								xml.writeCharacters(arrayOfTestCaseId[i]);
							xml.writeEndElement();
					xml.writeEndElement();
					xml.writeStartElement("td");
						xml.writeStartElement("center");
							xml.writeStartElement("font");
								if (arrayOftestCaseResult[i].equals(sPassed))
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
		catch(XMLStreamException xe){
			throw new UIAutomationException("Can not create TestCaseList.htm file.");
		}
		catch (IOException e) {
			throw new UIAutomationException("TestCaseList.htm File not found");
		}
	}

	/**
	 * Generate AutomationReport in HTML
	 * @throws IOException 
	 * @throws XMLStreamException 
	 * @throws UIAutomationException 
	 */
	public static void generateAutomationReportInHtml(String testName) throws UIAutomationException {
		try{
			csvTestCase = new CsvReader(GlobalVariables.configuration.getLogDirectoryPath()+ "\\Results.csv");
			OutputStream destination = new FileOutputStream(GlobalVariables.configuration.getReportDstDirectoryPath()+ "\\" + testName + ".htm");
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
			 csvTestCase.readHeaders();
			 while(csvTestCase.readRecord())
			 {
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
					 			if(csvTestCase.get("Result").equals(sPassed))
					 				xml.writeAttribute("color", "GREEN"); 
					 			else 
					 				xml.writeAttribute("color", "RED"); 
					 				xml.writeCharacters(csvTestCase.get("Result"));
					 			xml.writeEndElement();
					 		xml.writeEndElement();
					 	xml.writeEndElement();
					 	xml.writeStartElement("td");
					 		xml.writeCharacters(csvTestCase.get("ScriptException"));
					 	xml.writeEndElement();
					 	if (csvTestCase.get("ErrorReport") != blank) {
					 		xml.writeStartElement("td");
					 			xml.writeCharacters(csvTestCase.get("ErrorReport"));
					 		xml.writeEndElement();
					 	}
					 	else{
					 		xml.writeStartElement("td");
					 		xml.writeEndElement();
					 	}
					 		
					 xml.writeEndElement();
				 }
			}
			 	xml.writeEndElement();
			 xml.writeEndElement();
			 xml.writeEndDocument();
			 xml.close();
			 destination.close();
			 csvTestCase.close(); 
		}
		catch(IOException e){
			throw new UIAutomationException("Results.csv file not found on specified path." +GlobalVariables.configuration.getLogDirectoryPath()+ "\\Results.csv");
			
		}
		catch (XMLStreamException xe) {
			throw new UIAutomationException("Can not create '"+ testName +"'.htm file.");
		}
		
	}	
}