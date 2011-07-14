package com.mindalliance.globallibrary;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.xml.bind.JAXBElement.GlobalScope;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.jopendocument.dom.spreadsheet.Sheet;
import org.jopendocument.dom.spreadsheet.SpreadSheet;

import com.csvreader.CsvReader;

public class ReportFunctions {
	
	static int index = 0;
	public static int totalNoOfTestCasesPassed = 0;
	public static int totalNoOfTestCasesFailed = 0;
	static int testCasesAutomated = 0;
	static int testCasesPassed = 0;
	static int testCasesFailed = 0;
	static CsvReader products;
	static CsvReader csvTestCase;
	static String sSummary;
	static String sCsvResult;
	static String sCsvScriptException;
	static String sCsvErrorReport;
	static String arrayOfTestCaseId[] = new String[150];
	static String arrayOftestCaseSummary[] = new String[100];
	static String arrayOftestCaseResult[] = new String[100];
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
					   sheet.getCellAt("K"+i).setValue(GlobalVariables.sFailed);
					   sheet.getCellAt("L"+i).setValue(sCsvScriptException);
					   sheet.getCellAt("M"+i).setValue(sCsvErrorReport);
					   // Call generateAutomationReportInHtml()
					   generateAutomationReportInHtml(stestName);
					   testCasesFailed ++;
					   arrayOfTestCaseId[index] = stestName;
					   arrayOftestCaseResult[index] = GlobalVariables.sFailed;
					   arrayOftestCaseSummary[index++] = sheet.getCellAt("B"+i).getValue().toString();
				   }
				   else if (sResult == GlobalVariables.sPassed) {
					   sheet.getCellAt("K"+i).setValue(GlobalVariables.sPassed);
					   // Call generateAutomationReportInHtml()
					   generateAutomationReportInHtml(stestName);
					   testCasesPassed ++;
					   arrayOfTestCaseId[index] = stestName;
					   arrayOftestCaseResult[index] = GlobalVariables.sPassed;
					   arrayOftestCaseSummary[index++] = sheet.getCellAt("B"+i).getValue().toString();
				   }
				   else if (sResult == GlobalVariables.sNotRun)
					   sheet.getCellAt("K"+i).setValue(GlobalVariables.sNotRun);
			   }
			}
	}
	
	
	public static String[] readTestCaseId(int sheetNumber) throws IOException, XMLStreamException {
		 File file = new File(GlobalVariables.fCurrentDir.getCanonicalPath().toString() + "\\TestCases\\Mind-AllianceTestCaseSheet.ods");
			// TestCase sheet: Concurrent_Users_Scenario
		 Sheet sheet = SpreadSheet.createFromFile(file).getSheet(sheetNumber);
		 int iIndex = 0;
		 String[] arrayOfTestCaseId = new String[200];
		 stestName = null;
     		 for (int i = 2; i <= sheet.getRowCount() ; i++){
			   stestName = sheet.getCellAt("A"+i).getValue().toString();
  			   if (stestName.length() !=0) { 
				      arrayOfTestCaseId[iIndex] = stestName;
				      iIndex++;
			   			
			   }
		}
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
		//InputStream in = new FileInputStream(GlobalStatic.sLogDirectoryPath);
		// Load the ODF document from the path
        //OdfDocument odfDoc = OdfDocument.loadDocument("C:\\Users\\admin\\workspace\\Mind-AllianceAutomationFramework\\Reports\2011_02_19_16_55_24\\Mind-AllianceTestCaseSheet.ods");
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
				
		File outputFile = new File(GlobalVariables.sReportDstDirectoryPath + "\\Mind-AllianceTestCaseSheet.ods");
		sheet.getSpreadSheet().saveAs(outputFile);
		
		generateTestCaseIndex();
		generateTestCaseSummary();
		generateFinalTestPassReport();
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
		xml.writeCharacters("Mind-Alliance Concurrent User TestPass Report: " + GlobalVariables.sReportDirectoryName);
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
						/*xml.writeStartElement("td");
						xml.writeAttribute("bgColor","#BBBBBB");
						xml.writeAttribute("onMouseover", "this.bgColor='#DDDDDD'");
						xml.writeAttribute("onMouseout", "this.bgColor='#BBBBBB'");
							xml.writeStartElement("center");
								xml.writeStartElement("strong");
									xml.writeCharacters("Title");
								xml.writeEndElement();
							xml.writeEndElement();
						xml.writeEndElement();*/
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
					/*xml.writeStartElement("td");
						xml.writeCharacters(arrayOftestCaseSummary[i]);
					xml.writeEndElement();*/
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
		//csvTestCase = new CsvReader("C:\\Users\\admin\\workspace\\Mind-AllianceAutomationFramework\\Logs\\2011_02_22_14_34_49\\Results.csv");
		OutputStream destination = new FileOutputStream(GlobalVariables.sReportDstDirectoryPath + "\\" + testName + ".htm");
		//OutputStream destination = new FileOutputStream("C:\\Users\\admin\\workspace\\Mind-AllianceAutomationFramework\\Reports\\2011_02_22_14_34_49\\" + testName + ".html");
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
}
