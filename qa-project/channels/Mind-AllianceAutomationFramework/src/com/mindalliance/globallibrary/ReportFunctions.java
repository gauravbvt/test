package com.mindalliance.globallibrary;

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

public class ReportFunctions {
	public static int totalNoOfTestCasesPassed = 0;
	public static int totalNoOfTestCasesFailed = 0;
	static int index = 0;
	static int testCasesAutomated = 0;
	static int testCasesPassed = 0;
	static int testCasesFailed = 0;
	static CsvReader products;
	static CsvReader csvTestCase;
	static String sSummary;
	static String sCsvResult;
	static String sCsvScriptException;
	static String sCsvErrorReport;
	static String arrayOfTestCaseId[] = new String[500];
	static String arrayOftestCaseSummary[] = new String[400];
	static String arrayOftestCaseResult[] = new String[400];
	static String stestName;

	/**
	 * Update TestCase Execution Result
	 * @param sheet
	 * @throws IOException
	 * @throws XMLStreamException
	 */
	public static void updateTestCaseExecutionResult(Sheet sheet) throws IOException, XMLStreamException {
		 testCasesPassed = 0;
		 testCasesFailed = 0;
		for (int i = 2; i < sheet.getRowCount() ; i++){
			   stestName = sheet.getCellAt("A"+i).getValue().toString();
			   if (stestName != GlobalVariables.sBlank) {
				   // Call readCsvFile
				   String sResult = readCsvFile(stestName);
				   if (sResult == GlobalVariables.sFailed) {
					   sheet.getCellAt("J"+i).setValue(GlobalVariables.sFailed);
					   sheet.getCellAt("K"+i).setValue(sCsvScriptException);
					   sheet.getCellAt("L"+i).setValue(sCsvErrorReport);
					   // Call generateAutomationReportInHtml()
					   generateAutomationReportInHtml(stestName);
					   testCasesFailed ++;
					   arrayOfTestCaseId[index] = stestName;
					   arrayOftestCaseResult[index] = GlobalVariables.sFailed;
					   arrayOftestCaseSummary[index++] = sheet.getCellAt("B"+i).getValue().toString();
				   }
				   else if (sResult == GlobalVariables.sPassed) {
					   sheet.getCellAt("J"+i).setValue(GlobalVariables.sPassed);
					   // Call generateAutomationReportInHtml()
					   generateAutomationReportInHtml(stestName);
					   testCasesPassed ++;
					   arrayOfTestCaseId[index] = stestName;
					   arrayOftestCaseResult[index] = GlobalVariables.sPassed;
					   arrayOftestCaseSummary[index++] = sheet.getCellAt("B"+i).getValue().toString();
				   }
				   else if (sResult == GlobalVariables.sNotRun)
					   sheet.getCellAt("J"+i).setValue(GlobalVariables.sNotRun);
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
		File file = new File(GlobalVariables.fCurrentDir.getCanonicalPath().toString() + "\\TestCases\\Mind-AllianceTestCaseSheet.ods");
		// TestCase sheet: Tree_Navigation_Views
		Sheet sheet = SpreadSheet.createFromFile(file).getSheet(sheetNumber);
		String[] arrayOfTestCaseId = new String[600];
		stestName = null;
		GlobalVariables.iIndex=0;
     	for (int i = 2; i <= sheet.getRowCount() ; i++){
     		stestName = sheet.getCellAt("A"+i).getValue().toString();
  			if (sheet.getCellAt("I"+i).getValue().toString().toUpperCase().equals(GlobalVariables.sAutomatesYes)) {
  				arrayOfTestCaseId[GlobalVariables.iIndex] = stestName;
			    GlobalVariables.iIndex++;
  			}
		}
//     	System.out.println(GlobalVariables.iIndex);
     	sheet.detach();
		return arrayOfTestCaseId;
	}

	/**
	 * Read Result Csv File
	 * @throws IOException
	 */
	public static String readCsvFile(String sTestCaseId) throws IOException{
		int isAvailable = 0;
		//CsvReader csvTestCase = new CsvReader("D:\\code\\JavaWorkspace\\mind-alliance\\mind-alliance\\trunk\\Mind-AllianceAutomationFramework\\Logs\\2011_03_15_17_18_43\\Results.csv");
		CsvReader csvTestCase = new CsvReader(GlobalVariables.sLogDirectoryPath + "\\Results.csv");
		csvTestCase.readHeaders();
		while(csvTestCase.readRecord()) {
			if (sTestCaseId.equals(csvTestCase.get("TestCaseId"))) {
					isAvailable = 1;
					break;
			}
		}
		csvTestCase.close();
		sCsvResult = GlobalVariables.sBlank;
		sCsvScriptException = GlobalVariables.sBlank;
		sCsvErrorReport = GlobalVariables.sBlank;
		//csvTestCase = new CsvReader("D:\\code\\JavaWorkspace\\mind-alliance\\mind-alliance\\trunk\\Mind-AllianceAutomationFramework\\Logs\\2011_03_15_17_18_43\\Results.csv");
		csvTestCase = new CsvReader(GlobalVariables.sLogDirectoryPath + "\\Results.csv");
		if (isAvailable == 1) {
			csvTestCase.readHeaders();
		while (csvTestCase.readRecord()) {
				if (sTestCaseId.equals(csvTestCase.get("TestCaseId"))) {
					sCsvResult = csvTestCase.get("Result");
					sCsvScriptException = csvTestCase.get("ScriptException");
					sCsvErrorReport = csvTestCase.get("ErrorReport");
				}
				if (sCsvResult.equals(GlobalVariables.sFailed))
					return GlobalVariables.sFailed;
			}
		return GlobalVariables.sPassed;
		}
		else
			return GlobalVariables.sNotRun;
	}

	/**
	 * Generate AutomationReport in Ods
	 * @throws IOException
	 * @throws XMLStreamException
	 */
	public static void generateAutomationReport() throws IOException, XMLStreamException {
		// Initialize the variables
		index = 0;
		Arrays.fill(arrayOfTestCaseId, null);
		// Load the ODF document from the path
		File file = new File(GlobalVariables.fCurrentDir.getCanonicalPath().toString() + "\\TestCases\\Mind-AllianceTestCaseSheet.ods");
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

		File outputFile = new File(GlobalVariables.sReportDstDirectoryPath + "\\Mind-AllianceTestCaseSheet.ods");
		sheet.getSpreadSheet().saveAs(outputFile);

		generateTestCaseIndex();
		generateTestCaseSummary();
		generateFinalTestPassReport();
		generateFailureReport();
		System.out.println("Report generated successfully");
	}

	/**
	 * Generate Final TestPass Report
	 * @throws XMLStreamException
	 * @throws IOException
	 */
	private static void generateFinalTestPassReport() throws XMLStreamException, IOException {
		OutputStream destination = new FileOutputStream(GlobalVariables.sReportDstDirectoryPath + "\\index.htm");
		//OutputStream destination = new FileOutputStream("C:\\Users\\admin\\workspace\\Mind-AllianceAutomationFramework\\Reports\\2011_02_22_14_34_49\\TestCaseIndex.html");
		XMLOutputFactory outputFactory = XMLOutputFactory.newInstance();
		XMLStreamWriter xml = outputFactory.createXMLStreamWriter(destination);

		xml.writeStartDocument();
		xml.writeStartElement("html");
		xml.writeDefaultNamespace("http://www.w3.org/1999/xhtml");
		xml.writeStartElement("head");
		xml.writeStartElement("title");
		xml.writeCharacters("Mind-Alliance Automation TestPass Report: " + GlobalVariables.sReportDirectoryName);
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

	/**
	 *  Generate TestCase Summary
	 * @throws XMLStreamException
	 * @throws IOException
	 */
	private static void generateTestCaseSummary() throws XMLStreamException, IOException {
		OutputStream destination = new FileOutputStream(GlobalVariables.sReportDstDirectoryPath + "\\TestPassSummary.htm");
		//OutputStream destination = new FileOutputStream("C:\\Users\\admin\\workspace\\Mind-AllianceAutomationFramework\\Reports\\2011_02_22_14_34_49\\TestCaseIndex.html");
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
		xml.writeCharacters("Start Datetime: " + GlobalVariables.sStartDateTime);
		xml.writeEmptyElement("br");
		xml.writeCharacters("End Datetime: " + GlobalVariables.sEndDateTime);
		xml.writeEmptyElement("br");
		xml.writeCharacters("Browser: " + GlobalVariables.sBrowser);
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

	private static void generateFailureReport() throws XMLStreamException, IOException
	{
		csvTestCase = new CsvReader(GlobalVariables.sLogDirectoryPath + "\\Results.csv");
		OutputStream destination = new FileOutputStream(GlobalVariables.sReportDstDirectoryPath + "\\TestCaseFailureList.htm");
		XMLOutputFactory outputFactory = XMLOutputFactory.newInstance();
		XMLStreamWriter xml = outputFactory.createXMLStreamWriter(destination);

		xml.writeStartDocument();
		xml.writeStartElement("html");
		xml.writeDefaultNamespace("http://www.w3.org/1999/xhtml");
		xml.writeStartElement("head");
		xml.writeStartElement("title");
		xml.writeCharacters("Mind-alliance Failure Report");
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
		xml.writeCharacters("Start Datetime: " + GlobalVariables.sStartDateTime);
		xml.writeEmptyElement("br");
		xml.writeCharacters("End Datetime: " + GlobalVariables.sEndDateTime);
		xml.writeEmptyElement("br");
		xml.writeCharacters("Browser: " + GlobalVariables.sBrowser);
		xml.writeEmptyElement("br");
		xml.writeEndElement();
		xml.writeEndElement();
		xml.writeStartElement("td");
		xml.writeStartElement("center");
		xml.writeStartElement("img");
		xml.writeAttribute("src","../../Images/Mind-Alliance_Logo.png");
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
		xml.writeEndElement();
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
				xml.writeEndElement();
				csvTestCase.readHeaders();
				for (int i = 0; i < arrayOfTestCaseId.length ;i++) {
					if(arrayOfTestCaseId[i] != null) {
						xml.writeStartElement("tr");
						xml.writeAttribute("style","WIDTH:235;BORDER:0;OVERFLOW-Y:scroll;WORD-WRAP:BREAK-WORD;OVERFLOW-X:hidden;padding:  2px 0px 2px 5px");
						xml.writeAttribute("bgColor","#DDDDDD");
						xml.writeAttribute("padding","");
						xml.writeStartElement("td");
						xml.writeAttribute("onMouseover", "this.bgColor='#EEEEEE'");
						xml.writeAttribute("onMouseout", "this.bgColor='#DDDDDD'");
							xml.writeStartElement("left");
								xml.writeCharacters(arrayOfTestCaseId[i]);
							xml.writeEndElement();
						xml.writeEndElement();
						xml.writeStartElement("td");
						xml.writeAttribute("onMouseover", "this.bgColor='#EEEEEE'");
						xml.writeAttribute("onMouseout", "this.bgColor='#DDDDDD'");
							xml.writeStartElement("center");
								xml.writeStartElement("font");
									if (arrayOftestCaseResult[i].equals(GlobalVariables.sPassed))
										xml.writeAttribute("color", "GREEN");
									else
										xml.writeAttribute("color", "RED");
								xml.writeCharacters(arrayOftestCaseResult[i]);
								xml.writeEndElement();
							xml.writeStartElement("td");
							xml.writeAttribute("onMouseover", "this.bgColor='#EEEEEE'");
							xml.writeAttribute("onMouseout", "this.bgColor='#DDDDDD'");
								xml.writeStartElement("center");
									while(csvTestCase.readRecord()) {
										if(csvTestCase.get("ScriptException")!= GlobalVariables.sBlank) {
											xml.writeCharacters(csvTestCase.get("ScriptException"));
											xml.writeEndElement();
											break;
										}
										if(csvTestCase.get("ErrorReport")!= GlobalVariables.sBlank) {
											xml.writeStartElement("td");
											xml.writeAttribute("onMouseover", "this.bgColor='#EEEEEE'");
											xml.writeAttribute("onMouseout", "this.bgColor='#DDDDDD'");
											xml.writeStartElement("center");
											xml.writeCharacters(csvTestCase.get("ErrorReport"));
											xml.writeEndElement();
											break;
										}
									}
						}
				}
				
				xml.writeEndElement();
				xml.writeEndElement();
				xml.writeEndDocument();
				xml.close();
				destination.close();
				csvTestCase.close();
		}
		
	/**
	 * Generate TestCase Index
	 * @throws XMLStreamException
	 * @throws IOException
	 */
	private static void generateTestCaseIndex() throws XMLStreamException, IOException {
		OutputStream destination = new FileOutputStream(GlobalVariables.sReportDstDirectoryPath + "\\TestCaseList.htm");
		//OutputStream destination = new FileOutputStream("C:\\Users\\admin\\workspace\\Mind-AllianceAutomationFramework\\Reports\\2011_02_22_14_34_49\\TestCaseIndex.html");
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
								if (arrayOftestCaseResult[i].equals(GlobalVariables.sPassed))
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

	/**
	 * Generate AutomationReport in HTML
	 * @throws IOException
	 * @throws XMLStreamException
	 */
	public static void generateAutomationReportInHtml(String testName) throws IOException, XMLStreamException {
		csvTestCase = new CsvReader(GlobalVariables.sLogDirectoryPath + "\\Results.csv");
		OutputStream destination = new FileOutputStream(GlobalVariables.sReportDstDirectoryPath + "\\" + testName + ".htm");
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
				 			if(csvTestCase.get("Result").equals(GlobalVariables.sPassed))
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
				 	if (csvTestCase.get("ErrorReport") != GlobalVariables.sBlank) {
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

	public static String[] readTestCaseIdForFunctional(int sheetNumber) throws IOException {
		File file1 = new File(GlobalVariables.fCurrentDir.getCanonicalPath().toString() + "\\TestCases\\FunctionalTestCase.ods");
   		// TestCase sheet: Tree_Navigation_Views
		Sheet sheet1 = SpreadSheet.createFromFile(file1).getSheet(sheetNumber);
		String[] arrayOfTestCaseId = new String[600];
		stestName = null;
		GlobalVariables.iIndex=0;
 		for (int i = 2; i <= sheet1.getRowCount() ; i++){
 			stestName = sheet1.getCellAt("A"+i).getValue().toString();
 			if (sheet1.getCellAt("J"+i).getValue().toString().toUpperCase().equals(GlobalVariables.sAutomatesYes)) {
 				arrayOfTestCaseId[GlobalVariables.iIndex] = stestName;
 				GlobalVariables.iIndex++;
 			}
 		}
 		sheet1.detach();
		return arrayOfTestCaseId;
	}

	public static void generateAutomationReportForFunctionalTestCases() throws IOException, XMLStreamException {
		// Initialize the variables
		index = 0;
		Arrays.fill(arrayOfTestCaseId, null);
		// Load the ODF document from the path
		File file = new File(GlobalVariables.fCurrentDir.getCanonicalPath().toString() + "\\TestCases\\FunctionalTestCase.ods");

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

		// TestCase sheet: Channels Administration
		sheet = sheet.getSpreadSheet().getSheet(6);
		updateTestCaseExecutionResult(sheet);
		// Generate Summary Sheet
		sheet = sheet.getSpreadSheet().getSheet(0);
		sheet.getCellAt("G10").setValue((testCasesPassed + testCasesFailed));
		sheet.getCellAt("H10").setValue(testCasesPassed);
		sheet.getCellAt("I10").setValue(testCasesFailed);
		// totalNoOfTestCasesPassed & totolNoOfTestCasesFailed
		totalNoOfTestCasesPassed = totalNoOfTestCasesPassed + testCasesPassed;
		totalNoOfTestCasesFailed = totalNoOfTestCasesFailed + testCasesFailed;
		
		// TestCase sheet: Channels Command Execution
		sheet = sheet.getSpreadSheet().getSheet(7);
		updateTestCaseExecutionResult(sheet);
		// Generate Summary Sheet 
		sheet = sheet.getSpreadSheet().getSheet(0);
		sheet.getCellAt("G10").setValue((testCasesPassed + testCasesFailed));
		sheet.getCellAt("H10").setValue(testCasesPassed);
		sheet.getCellAt("I10").setValue(testCasesFailed);
		// totalNoOfTestCasesPassed & totolNoOfTestCasesFailed
		totalNoOfTestCasesPassed = totalNoOfTestCasesPassed + testCasesPassed;
		totalNoOfTestCasesFailed = totalNoOfTestCasesFailed + testCasesFailed;
		
		// TestCase sheet: Collaboration Panel
		sheet = sheet.getSpreadSheet().getSheet(8);
		updateTestCaseExecutionResult(sheet);
		// Generate Summary Sheet 
		sheet = sheet.getSpreadSheet().getSheet(0);
		sheet.getCellAt("G10").setValue((testCasesPassed + testCasesFailed));
		sheet.getCellAt("H10").setValue(testCasesPassed);
		sheet.getCellAt("I10").setValue(testCasesFailed);
		
		// TestCase sheet: Task and Flow Panel
		sheet = sheet.getSpreadSheet().getSheet(10);
		updateTestCaseExecutionResult(sheet);
		// Generate Summary Sheet 
		sheet = sheet.getSpreadSheet().getSheet(0);
		sheet.getCellAt("G10").setValue((testCasesPassed + testCasesFailed));
		sheet.getCellAt("H10").setValue(testCasesPassed);
		sheet.getCellAt("I10").setValue(testCasesFailed);
		// totalNoOfTestCasesPassed & totolNoOfTestCasesFailed
		totalNoOfTestCasesPassed = totalNoOfTestCasesPassed + testCasesPassed;
		totalNoOfTestCasesFailed = totalNoOfTestCasesFailed + testCasesFailed;

		// TestCase sheet: Plan and Segment Bar 
		sheet = sheet.getSpreadSheet().getSheet(9);
		updateTestCaseExecutionResult(sheet);
		// Generate Summary Sheet 
		sheet = sheet.getSpreadSheet().getSheet(0);
		sheet.getCellAt("G10").setValue((testCasesPassed + testCasesFailed));
		sheet.getCellAt("H10").setValue(testCasesPassed);
		sheet.getCellAt("I10").setValue(testCasesFailed);
		// totalNoOfTestCasesPassed & totolNoOfTestCasesFailed
		totalNoOfTestCasesPassed = totalNoOfTestCasesPassed + testCasesPassed;
		totalNoOfTestCasesFailed = totalNoOfTestCasesFailed + testCasesFailed;
		
		// TestCase sheet : Entities
		sheet = sheet.getSpreadSheet().getSheet(11);
		updateTestCaseExecutionResult(sheet);
		// Generate Summary Sheet 
		sheet = sheet.getSpreadSheet().getSheet(0);
		sheet.getCellAt("G10").setValue((testCasesPassed + testCasesFailed));
		sheet.getCellAt("H10").setValue(testCasesPassed);
		sheet.getCellAt("I10").setValue(testCasesFailed);
		// totalNoOfTestCasesPassed & totolNoOfTestCasesFailed
		totalNoOfTestCasesPassed = totalNoOfTestCasesPassed + testCasesPassed;
		totalNoOfTestCasesFailed = totalNoOfTestCasesFailed + testCasesFailed;
		
		// TestCase sheet : Issue Summary Report
		sheet = sheet.getSpreadSheet().getSheet(17);
		updateTestCaseExecutionResult(sheet);
		// Generate Summary Sheet 
		sheet = sheet.getSpreadSheet().getSheet(0);
		sheet.getCellAt("G10").setValue((testCasesPassed + testCasesFailed));
		sheet.getCellAt("H10").setValue(testCasesPassed);
		sheet.getCellAt("I10").setValue(testCasesFailed);
		// totalNoOfTestCasesPassed & totolNoOfTestCasesFailed
		totalNoOfTestCasesPassed = totalNoOfTestCasesPassed + testCasesPassed;
		totalNoOfTestCasesFailed = totalNoOfTestCasesFailed + testCasesFailed;
		
		// TestCase sheet : Information Sharing Guidelines
		sheet = sheet.getSpreadSheet().getSheet(13);
		updateTestCaseExecutionResult(sheet);
		// Generate Summary Sheet 
		sheet = sheet.getSpreadSheet().getSheet(0);
		sheet.getCellAt("G10").setValue((testCasesPassed + testCasesFailed));
		sheet.getCellAt("H10").setValue(testCasesPassed);
		sheet.getCellAt("I10").setValue(testCasesFailed);
		// totalNoOfTestCasesPassed & totolNoOfTestCasesFailed
		totalNoOfTestCasesPassed = totalNoOfTestCasesPassed + testCasesPassed;
		totalNoOfTestCasesFailed = totalNoOfTestCasesFailed + testCasesFailed;
		
		// TestCase sheet : Information Sharing Guidelines(User)
		sheet = sheet.getSpreadSheet().getSheet(16);
		updateTestCaseExecutionResult(sheet);
		// Generate Summary Sheet 
		sheet = sheet.getSpreadSheet().getSheet(0);
		sheet.getCellAt("G10").setValue((testCasesPassed + testCasesFailed));
		sheet.getCellAt("H10").setValue(testCasesPassed);
		sheet.getCellAt("I10").setValue(testCasesFailed);
		// totalNoOfTestCasesPassed & totolNoOfTestCasesFailed
		totalNoOfTestCasesPassed = totalNoOfTestCasesPassed + testCasesPassed;
		totalNoOfTestCasesFailed = totalNoOfTestCasesFailed + testCasesFailed;

		File outputFile = new File(GlobalVariables.sReportDstDirectoryPath + "\\FunctionalTestCase.ods");
		sheet.getSpreadSheet().saveAs(outputFile);

		generateTestCaseIndex();
		generateTestCaseSummary();
		generateFinalTestPassReport();
		generateFailureReport();
		System.out.println("Report generated successfully");

	}
}
