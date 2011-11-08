package com.mindalliance.globallibrary;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
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
	static String arrayOfTestCaseId[] = new String[600];
	static String arrayOftestCaseSummary[] = new String[600];
	static String arrayOftestCaseResult[] = new String[600];
	static String stestName;

	/**
	 * Update TestCase Execution Result
	 * @param sheet
	 * @throws IOException
	 * @throws XMLStreamException
	 */
	public static void updateTestCaseExecutionResult() {
		try {
			testCasesPassed = 0;
			testCasesFailed = 0;
			for (int i = 0; i < GlobalVariables.jListExecute.getModel().getSize() ; i++)	{
				stestName = GlobalVariables.jListExecute.getModel().getElementAt(i).toString();
				if (stestName != GlobalVariables.sBlank) {
				   // Call readCsvFile
					String sResult = readCsvFile(stestName);
					if (sResult == GlobalVariables.sFailed) {
					    // Call generateAutomationReportInHtml()
					    generateAutomationReportInHtml(stestName);
					    testCasesFailed ++;
					    arrayOfTestCaseId[index] = stestName;
					    arrayOftestCaseResult[index++] = GlobalVariables.sFailed;
				    }
				    else if (sResult == GlobalVariables.sPassed) {
					    // Call generateAutomationReportInHtml()
					    generateAutomationReportInHtml(stestName);
					    testCasesPassed ++;
					    arrayOfTestCaseId[index] = stestName;
					    arrayOftestCaseResult[index++] = GlobalVariables.sPassed;
				    }
			    }
			}
		}
		catch(Exception e) {
			System.out.println("\nError in updateTestCaseExecutionResult() function.\n");
			System.out.println("\n" + e.getMessage());
		}
	}
	
	public static void updateTestCaseSheetResult() {
		try {
			
			File file = new File(GlobalVariables.fCurrentDir.getCanonicalPath().toString() + "\\TestCases\\Mind-AllianceTestCaseSheet.ods");
			Sheet sheet=SpreadSheet.createFromFile(file).getSheet(0);
			
			// Update View, Plan & Command Sheets
			for (int i = 0; i < GlobalVariables.jListExecute.getModel().getSize() ; i++)	{
				stestName = GlobalVariables.jListExecute.getModel().getElementAt(i).toString();

				// Call readCsvFile
				String sResult = readCsvFile(stestName);
				
				if(stestName.contains("MAV")) { 
					sheet = sheet.getSpreadSheet().getSheet(1);
					// No Of Test Cases Passed & Failed of Views
					if(sResult.equals(GlobalVariables.sPassed))
						GlobalVariables.noOfViewTestCasesPassed++;
					else
						GlobalVariables.noOfViewTestCasesFailed++;

				}
				else if(stestName.contains("MAP")) {
					sheet = sheet.getSpreadSheet().getSheet(2);
					// No Of Test Cases Passed & Failed of Plans				
					if(sResult.equals(GlobalVariables.sPassed))
						GlobalVariables.noOfPlanTestCasesPassed++;
					else
						GlobalVariables.noOfPlanTestCasesFailed++;
				}
				else if(stestName.contains("MAC")) {
					sheet = sheet.getSpreadSheet().getSheet(3);
					// No Of Test Cases Passed & Failed of Commands
					if(sResult.equals(GlobalVariables.sPassed))
						GlobalVariables.noOfCommandTestCasesPassed++;
					else
						GlobalVariables.noOfCommandTestCasesFailed++;
				}
				
				for(int j=1;j<sheet.getRowCount();j++) {
					if(stestName.equals(sheet.getValueAt(0,j).toString())) {
						if (sResult == GlobalVariables.sFailed) {
							sheet.getCellAt("J"+(j+1)).setValue(GlobalVariables.sFailed);
							sheet.getCellAt("K"+(j+1)).setValue(sCsvScriptException);
							sheet.getCellAt("L"+(j+1)).setValue(sCsvErrorReport);					   
				   		}
				   		else if (sResult == GlobalVariables.sPassed) {
				    		sheet.getCellAt("J"+(j+1)).setValue(GlobalVariables.sPassed);
				   		}
					}
				}
			}
			
			// Update Summary Sheet
			sheet=sheet.getSpreadSheet().getSheet(0);
			// No. Of Test Cases Executed of Views, Plans & Commands
			sheet.getCellAt("G8").setValue(GlobalVariables.noOfViewTestCasesExecuted);
			sheet.getCellAt("G9").setValue(GlobalVariables.noOfPlanTestCasesExecuted);
			sheet.getCellAt("G10").setValue(GlobalVariables.noOfCommandTestCasesExecuted);
			// No. Of Test Cases Passed of Views, Plans & Commands			
			sheet.getCellAt("H8").setValue(GlobalVariables.noOfViewTestCasesPassed);
			sheet.getCellAt("H9").setValue(GlobalVariables.noOfPlanTestCasesPassed);
			sheet.getCellAt("H10").setValue(GlobalVariables.noOfCommandTestCasesPassed);
			// No. Of Test Cases Failed of Views, Plans & Commands
			sheet.getCellAt("I8").setValue(GlobalVariables.noOfViewTestCasesFailed);
			sheet.getCellAt("I9").setValue(GlobalVariables.noOfPlanTestCasesFailed);
			sheet.getCellAt("I10").setValue(GlobalVariables.noOfCommandTestCasesFailed);

			File outputFile = new File(GlobalVariables.sReportDstDirectoryPath + "\\Mind-AllianceTestCaseSheet.ods");
			sheet.getSpreadSheet().saveAs(outputFile);
		}
		catch(Exception e) {
			System.out.println("\nError Occured in UpdateTestCaseSheetResult Function.");
			e.printStackTrace();
		}
	}
	
	public static void updateTestCaseSheetResultForFunctionalTestCases() {
		try {
			File file = new File(GlobalVariables.fCurrentDir.getCanonicalPath().toString() + "\\TestCases\\FunctionalTestCase.ods");
			Sheet sheet=SpreadSheet.createFromFile(file).getSheet(0);
			for (int i = 0; i < GlobalVariables.jListExecute.getModel().getSize() ; i++)	{
				stestName = GlobalVariables.jListExecute.getModel().getElementAt(i).toString();
				if(stestName.contains("CL")) 
					sheet = sheet.getSpreadSheet().getSheet(1);
				else if(stestName.contains("HP"))
					sheet = sheet.getSpreadSheet().getSheet(2);
				else if(stestName.contains("CA"))
					sheet = sheet.getSpreadSheet().getSheet(3);
				else if(stestName.contains("DC"))
					sheet = sheet.getSpreadSheet().getSheet(6);
				else if(stestName.contains("CC"))
					sheet = sheet.getSpreadSheet().getSheet(7);
				else if(stestName.contains("CP"))
					sheet = sheet.getSpreadSheet().getSheet(8);
				else if(stestName.contains("PS"))
					sheet = sheet.getSpreadSheet().getSheet(9);
				else if(stestName.contains("TF"))
					sheet = sheet.getSpreadSheet().getSheet(10);
				else if(stestName.contains("TE"))
					sheet = sheet.getSpreadSheet().getSheet(11);
				else if(stestName.contains("IF"))
					sheet = sheet.getSpreadSheet().getSheet(12);
				else if(stestName.contains("PP"))
					sheet = sheet.getSpreadSheet().getSheet(13);
				else if(stestName.contains("PE"))
					sheet = sheet.getSpreadSheet().getSheet(14);
				else if(stestName.contains("LF"))
					sheet = sheet.getSpreadSheet().getSheet(15);
				else if(stestName.contains("SG"))
					sheet = sheet.getSpreadSheet().getSheet(16);
				else if(stestName.contains("IS"))
					sheet = sheet.getSpreadSheet().getSheet(17);

				// Update Functional Test Case Sheet
				for(int j=1;j<sheet.getRowCount();j++) {
					sheet.getCellAt("K"+j).setValue("");
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
				sheet.getCellAt("K1").setValue("Result");
				sheet.getCellAt("L1").setValue("ScriptException");
				sheet.getCellAt("R1").setValue("Error");

				// Call readCsvFile
				String sResult = readCsvFile(stestName);
				for(int j=1;j<sheet.getRowCount();j++) {
					if(stestName.equals(sheet.getValueAt(0,j).toString())) {
						if (sResult == GlobalVariables.sFailed) {
							sheet.getCellAt("K"+(j+1)).setValue(GlobalVariables.sFailed);
							sheet.getCellAt("L"+(j+1)).setValue(sCsvScriptException);
							sheet.getCellAt("R"+(j+1)).setValue(sCsvErrorReport);					   
				   		}
				   		else if (sResult == GlobalVariables.sPassed) {
				    		sheet.getCellAt("K"+(j+1)).setValue(GlobalVariables.sPassed);
				   		}
					}
				}
			}
			File outputFile = new File(GlobalVariables.sReportDstDirectoryPath + "\\FunctionalTestCase.ods");
			sheet.getSpreadSheet().saveAs(outputFile);
		}
		catch(Exception e) {
			System.out.println("\nError Occured in UpdateTestCaseSheetResultForFunctionalTestCases Function.");
			e.printStackTrace();
		}
	}
	
	/**
	 * Read TestCaseId for Automation UI (i.e. Home.java)
	 * @param sheetNumber
	 * @return
	 * @throws IOException
	 * @throws XMLStreamException
	 */
	public static String[] readTestCaseId(int sheetNumber)	{
		try {
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
			if(sheetNumber==1||sheetNumber==2||sheetNumber==3) {
				file = new File(GlobalVariables.fCurrentDir.getCanonicalPath().toString() + "\\TestCases\\Mind-AllianceTestCaseSheet_V2.ods");
				sheet = SpreadSheet.createFromFile(file).getSheet(sheetNumber);
				for (int i = 2; i <= sheet.getRowCount() ; i++) {
					stestName = sheet.getCellAt("A"+i).getValue().toString();
					if (sheet.getCellAt("I"+i).getValue().toString().toUpperCase().equals(GlobalVariables.sAutomatesYes)) {
						arrayOfTestCaseId[GlobalVariables.iIndex] = stestName;
						GlobalVariables.iIndex++;
					}
				}
			}
			sheet.detach();
			return arrayOfTestCaseId;
		}
		catch(Exception e) {
			System.out.println("\nError Occured in readTestCaseId() function. \n");
			System.out.println("\n" + e.getMessage());
			return null;
		}
	}

	/**
	 * Read Result Csv File
	 * @throws IOException
	 */
	public static String readCsvFile(String sTestCaseId) {
		try {
			sCsvResult = GlobalVariables.sPassed;
			sCsvScriptException = GlobalVariables.sBlank;
			sCsvErrorReport = GlobalVariables.sBlank;
			CsvReader csvTestCase = new CsvReader(GlobalVariables.sLogDirectoryPath + "\\Results.csv");
			csvTestCase.readHeaders();
			while (csvTestCase.readRecord()) {
				if (sTestCaseId.equals(csvTestCase.get("TestCaseId")) && csvTestCase.get("Result").equals(GlobalVariables.sFailed)) {
					sCsvResult = csvTestCase.get("Result");
					sCsvScriptException = csvTestCase.get("ScriptException");
					sCsvErrorReport = csvTestCase.get("ErrorReport");
				}
			}
			csvTestCase.close();
			if (sCsvResult.equals(GlobalVariables.sFailed))
				return GlobalVariables.sFailed;
			else if(sCsvResult.equals(GlobalVariables.sPassed))
				return GlobalVariables.sPassed;
			else
				return GlobalVariables.sNotRun;
		}
		catch(Exception e) {
			System.out.println("\nError in readCsvFile() Function.\n");
			System.out.println("\n" + e.getMessage());
			return "Error";
		}
	}
		
	/**
	 * Generate AutomationReport in Ods
	 * @throws IOException
	 * @throws XMLStreamException
	 */
	public static void generateAutomationReport() {
		try {
			// Update Test Case Execution Result
			updateTestCaseExecutionResult();
			
			// Update Test Case Execution Result
			updateTestCaseSheetResult();
			
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
			System.out.println("\nError Occured in generateAutomationReport() function.\n");
			System.out.println("\n" + e.getMessage());
		}
	}

	/**
	 * Generate Final TestPass Report
	 * @throws XMLStreamException
	 * @throws IOException
	 */
	private static void generateFinalTestPassReport() {
		try {
			OutputStream destination = new FileOutputStream(GlobalVariables.sReportDstDirectoryPath + "\\index.htm");
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
			System.out.println("\nError Occured in generateFinalTestPassReport() function. \n");
			System.out.println("\n" + e.getMessage());
		}
	}

	/**
	 *  Generate TestCase Summary
	 * @throws XMLStreamException
	 * @throws IOException
	 */
	private static void generateTestCaseSummary() {
		try {
			OutputStream destination = new FileOutputStream(GlobalVariables.sReportDstDirectoryPath + "\\TestPassSummary.htm");
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
			xml.writeEndElement();
			xml.writeEndDocument();
			xml.close();
			destination.close();
		}
		catch(Exception e) {
			System.out.println("\nError Occured in generateTestCaseSummary() function. \n");
			System.out.println("\n" + e.getMessage());
		}
	}

	private static void generateFailureReport() {
		try {
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
						while(csvTestCase.readRecord()) {
							if(csvTestCase.get("TestCaseId").equals(arrayOfTestCaseId[i])) {
								if(csvTestCase.get("ScriptException")!= GlobalVariables.sBlank || 
								   csvTestCase.get("ErrorReport")!= GlobalVariables.sBlank) { 
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
															if(arrayOftestCaseResult[i].equals(GlobalVariables.sPassed))
																xml.writeAttribute("color", "GREEN");
															else
																xml.writeAttribute("color", "RED");
																xml.writeCharacters(arrayOftestCaseResult[i]);
															xml.writeEndElement();
														xml.writeEndElement();
														xml.writeStartElement("td");
															xml.writeAttribute("onMouseover", "this.bgColor='#EEEEEE'");
															xml.writeAttribute("onMouseout", "this.bgColor='#DDDDDD'");
																xml.writeStartElement("center");
																if(csvTestCase.get("ScriptException")!= GlobalVariables.sBlank) {
																	xml.writeCharacters(csvTestCase.get("ScriptException"));
																	xml.writeEndElement();
																}
																if(csvTestCase.get("ErrorReport")!= GlobalVariables.sBlank) {
																	xml.writeStartElement("td");
																		xml.writeAttribute("onMouseover", "this.bgColor='#EEEEEE'");
																		xml.writeAttribute("onMouseout", "this.bgColor='#DDDDDD'");
																			xml.writeStartElement("center");
																				xml.writeCharacters(csvTestCase.get("ErrorReport"));
																			xml.writeEndElement();
																	xml.writeEndElement();
																}
																else
																{
																	xml.writeStartElement("td");
																		xml.writeAttribute("bgColor","#DDDDDD");
																		xml.writeAttribute("onMouseover", "this.bgColor='#EEEEEE'");
																		xml.writeAttribute("onMouseout", "this.bgColor='#DDDDDD'");
																	xml.writeEndElement();
																}
															xml.writeEndElement();
												xml.writeEndElement();
										xml.writeEndElement();
									}
							}
							else 
								i++;
					}
				}
			}
			xml.writeEndDocument();
			xml.close();
			destination.close();
			csvTestCase.close();
		}
		catch(Exception e){
			System.out.println("\nError Occured in generateFailureReport() function.\n");
			System.out.println("\n" + e.getMessage());
		}
		}		
	
	/**
	 * Generate TestCase Index
	 * @throws XMLStreamException
	 * @throws IOException
	 */
	private static void generateTestCaseIndex() {
		try	{
			OutputStream destination = new FileOutputStream(GlobalVariables.sReportDstDirectoryPath + "\\TestCaseList.htm");
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
				for (int i = 0; i < GlobalVariables.jListExecute.getModel().getSize() ; i++) {
					if(GlobalVariables.jListExecute.getModel().getElementAt(i) != null) {
						xml.writeStartElement("tr");
							xml.writeAttribute("style","WIDTH:235;BORDER:0;OVERFLOW-Y:scroll;WORD-WRAP:BREAK-WORD;OVERFLOW-X:hidden;padding:  2px 0px 2px 5px");
							xml.writeAttribute("bgColor","#DDDDDD");
							xml.writeAttribute("padding","");
							xml.writeAttribute("onMouseover", "this.bgColor='#EEEEEE'");
							xml.writeAttribute("onMouseout", "this.bgColor='#DDDDDD'");
							xml.writeStartElement("td");
								xml.writeStartElement("a");
									xml.writeAttribute("href", GlobalVariables.jListExecute.getModel().getElementAt(i) + ".htm");
									xml.writeAttribute("target", "targetframe");
									xml.writeCharacters(GlobalVariables.jListExecute.getModel().getElementAt(i).toString());
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
		catch(Exception e) {
			System.out.println("\nError Occured in generateTestCaseIndex() function. \n");
			System.out.println("\n" + e.getMessage());
		}
	}

	/**
	 * Generate AutomationReport in HTML
	 * @throws IOException
	 * @throws XMLStreamException
	 */
	public static void generateAutomationReportInHtml(String testName) {
	try {
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
								if(csvTestCase.get("Result").equals(GlobalVariables.sPassed))
									xml.writeAttribute("color", "GREEN");
								else
									xml.writeAttribute("color", "RED");
									xml.writeCharacters(csvTestCase.get("Result"));
								xml.writeEndElement();
							xml.writeEndElement();
						xml.writeEndElement();
					if (csvTestCase.get("ScriptException") != GlobalVariables.sBlank) {
						xml.writeStartElement("td");
							xml.writeCharacters(csvTestCase.get("ScriptException"));
					}
					else {
							xml.writeStartElement("td");
					}
						xml.writeEndElement();
					if (csvTestCase.get("ErrorReport") != GlobalVariables.sBlank) {
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
	catch(Exception e) {
		System.out.println("\nError in generateAutomationReportInHtml() function. \n");
		System.out.println("\n" + e.getMessage());
	}
	}

	public static String[] readTestCaseIdForFunctional(int sheetNumber) {
		try {
			File file1 = new File(GlobalVariables.fCurrentDir.getCanonicalPath().toString() + "\\TestCases\\FunctionalTestCase.ods");
			// TestCase sheet: Tree_Navigation_Views
			Sheet sheet1 = SpreadSheet.createFromFile(file1).getSheet(sheetNumber);
			System.out.println(sheet1.getHeaderRowCount());
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
		catch(Exception e) {
			System.out.println("\nError Occured in readTestCaseIdForFunctional() function.\n");
			System.out.println("\n" + e.getMessage());
			return null;
		}
	}

	public static void generateAutomationReportForFunctionalTestCases() {
		try {
			// Update Test Case Execution Result
			updateTestCaseExecutionResult();
			
			// Update Test Case Execution Result
			updateTestCaseSheetResultForFunctionalTestCases();
			
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
			System.out.println("\nError Occured in generateAutomationReportForFunctionalTestCases() function.");
			System.out.println("\n" + e.getMessage());
		}
	}
}