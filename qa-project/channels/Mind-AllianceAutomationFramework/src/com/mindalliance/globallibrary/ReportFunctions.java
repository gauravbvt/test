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
	public static void updateTestCaseExecutionResultForFunctionalTestCases(Sheet sheet) {
//		try {
//			testCasesPassed = 0;
//			testCasesFailed = 0;
//			for (int i = 0; i < GlobalVariables.jListExecute.getModel().getSize() ; i++)	{
//				stestName = GlobalVariables.jListExecute.getModel().getElementAt(i).toString();
//				System.out.println(GlobalVariables.jListExecute.getModel().getSize()+ "\t" + stestName);
//				if (stestName != GlobalVariables.sBlank) {
//				   // Call readCsvFile
//					String sResult = readCsvFile(stestName);
//					if (sResult == GlobalVariables.sFailed) {
//						sheet.getCellAt("J"+(i+2)).setValue(GlobalVariables.sFailed);
//					    sheet.getCellAt("K"+(i+2)).setValue(sCsvScriptException);
//					    sheet.getCellAt("L"+(i+2)).setValue(sCsvErrorReport);
//					    // Call generateAutomationReportInHtml()
//					    generateAutomationReportInHtml(stestName);
//					    testCasesFailed ++;
//					    arrayOfTestCaseId[index] = stestName;
//					    arrayOftestCaseResult[index] = GlobalVariables.sFailed;
//					    arrayOftestCaseSummary[index++] = sheet.getCellAt("B"+(i+2)).getValue().toString();
//				    }
//				    else if (sResult == GlobalVariables.sPassed) {
//					    sheet.getCellAt("J"+(i+2)).setValue(GlobalVariables.sPassed);
//					    // Call generateAutomationReportInHtml()
//					    generateAutomationReportInHtml(stestName);
//					    testCasesPassed ++;
//					    arrayOfTestCaseId[index] = stestName;
//					    arrayOftestCaseResult[index] = GlobalVariables.sPassed;
//					    arrayOftestCaseSummary[index++] = sheet.getCellAt("B"+(i+2)).getValue().toString();
//				    }
//				    else if (sResult == GlobalVariables.sNotRun)
//					    sheet.getCellAt("J"+(i+2)).setValue(GlobalVariables.sNotRun);
//			    }
//			}
//		}
//		catch(Exception e) {
//			System.out.println("Error in updateTestCaseExecutionResult() function.\n");
//			e.printStackTrace();
//		}		
	}
	public static void updateTestCaseExecutionResult(Sheet sheet) {
		try {
			testCasesPassed = 0;
			testCasesFailed = 0;
			for (int i = 0; i < GlobalVariables.jListExecute.getModel().getSize() ; i++)	{
				stestName = GlobalVariables.jListExecute.getModel().getElementAt(i).toString();
				if (stestName != GlobalVariables.sBlank) {
				   // Call readCsvFile
					String sResult = readCsvFile(stestName);
					if (sResult == GlobalVariables.sFailed) {
//						sheet.getCellAt("J"+(i+2)).setValue(GlobalVariables.sFailed);
//					    sheet.getCellAt("K"+(i+2)).setValue(sCsvScriptException);
//					    sheet.getCellAt("L"+(i+2)).setValue(sCsvErrorReport);
					    // Call generateAutomationReportInHtml()
					    generateAutomationReportInHtml(stestName);
					    testCasesFailed ++;
					    arrayOfTestCaseId[index] = stestName;
					    arrayOftestCaseResult[index++] = GlobalVariables.sFailed;
				    }
				    else if (sResult == GlobalVariables.sPassed) {
//					    sheet.getCellAt("J"+(i+2)).setValue(GlobalVariables.sPassed);
					    // Call generateAutomationReportInHtml()
					    generateAutomationReportInHtml(stestName);
					    testCasesPassed ++;
					    arrayOfTestCaseId[index] = stestName;
					    arrayOftestCaseResult[index++] = GlobalVariables.sPassed;
				    }
				    else if (sResult == GlobalVariables.sNotRun) {
//					    sheet.getCellAt("J"+(i+2)).setValue(GlobalVariables.sNotRun);
				    }
			    }
			}
		}
		catch(Exception e) {
			System.out.println("Error in updateTestCaseExecutionResult() function.\n");
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
			System.out.println("Error Occured in readTestCaseId() function. \n");
			e.printStackTrace();
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
			System.out.println("Error in readCsvFile() Function.\n");
			e.printStackTrace();
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
			// Initialize the variables
			index = 0;
			Arrays.fill(arrayOfTestCaseId, null);
			// Load the ODF document from the path
			File file = new File(GlobalVariables.fCurrentDir.getCanonicalPath().toString() + "\\TestCases\\Mind-AllianceTestCaseSheet.ods");
			
			// TestCase sheet: Tree_Navigation_Views
			Sheet sheet = SpreadSheet.createFromFile(file).getSheet(1);
			updateTestCaseExecutionResult(sheet);
//			// Generate Summary Sheet
//			sheet = sheet.getSpreadSheet().getSheet(0);
//			sheet.getCellAt("G8").setValue((testCasesPassed + testCasesFailed));
//			sheet.getCellAt("H8").setValue(testCasesPassed);
//			sheet.getCellAt("I8").setValue(testCasesFailed);
			// totalNoOfTestCasesPassed & totolNoOfTestCasesFailed
			totalNoOfTestCasesPassed = testCasesPassed;
			totalNoOfTestCasesFailed = testCasesFailed;

			// TestCase sheet: Plan
			sheet = SpreadSheet.createFromFile(file).getSheet(2);
			updateTestCaseExecutionResult(sheet);
//			// Generate Summary Sheet
//			sheet = sheet.getSpreadSheet().getSheet(0);
//			sheet.getCellAt("G9").setValue((testCasesPassed + testCasesFailed));
//			sheet.getCellAt("H9").setValue(testCasesPassed);
//			sheet.getCellAt("I9").setValue(testCasesFailed);
			// totalNoOfTestCasesPassed & totolNoOfTestCasesFailed
			totalNoOfTestCasesPassed = testCasesPassed;
			totalNoOfTestCasesFailed = testCasesFailed;
			
			// TestCase sheet: Tree_Navigation_Views
			sheet = SpreadSheet.createFromFile(file).getSheet(3);
			updateTestCaseExecutionResult(sheet);
//			// Generate Summary Sheet
//			sheet = sheet.getSpreadSheet().getSheet(0);
//			sheet.getCellAt("G9").setValue((testCasesPassed + testCasesFailed));
//			sheet.getCellAt("H9").setValue(testCasesPassed);
//			sheet.getCellAt("I9").setValue(testCasesFailed);
			// totalNoOfTestCasesPassed & totolNoOfTestCasesFailed
			totalNoOfTestCasesPassed = testCasesPassed;
			totalNoOfTestCasesFailed = testCasesFailed;

			File outputFile = new File(GlobalVariables.sReportDstDirectoryPath + "\\Mind-AllianceTestCaseSheet.ods");
			sheet.getSpreadSheet().saveAs(outputFile);
			
			generateTestCaseIndex();
			generateTestCaseSummary();
			generateFinalTestPassReport();
			generateFailureReport();
			System.out.println("Report generated successfully");
		}
		catch(Exception e) {
			System.out.println("Error Occured in generateAutomationReport() function.\n");
			e.printStackTrace();
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
			System.out.println("Error Occured in generateFinalTestPassReport() function. \n");
			e.printStackTrace();
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
			System.out.println("Error Occured in generateTestCaseSummary() function. \n");
			e.printStackTrace();
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
			System.out.println("Error Occured in generateFailureReport() function.\n");
			e.printStackTrace();
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
			System.out.println("Error Occured in generateTestCaseIndex() function. \n");
			e.printStackTrace();
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
		System.out.println("Error in generateAutomationReportInHtml() function. \n");
		e.printStackTrace();
	}
	}

	public static String[] readTestCaseIdForFunctional(int sheetNumber) {
		try {
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
		catch(Exception e) {
			System.out.println("Error Occured in readTestCaseIdForFunctional() function.\n");
			e.printStackTrace();
			return null;
		}
	}

	public static void generateAutomationReportForFunctionalTestCases() {
		try {
			// Initialize the variables
			index = 0;
			Arrays.fill(arrayOfTestCaseId, null);
			// Load the ODF document from the path
			File file = new File(GlobalVariables.fCurrentDir.getCanonicalPath().toString() + "\\TestCases\\FunctionalTestCase.ods");

			// TestCase sheet: Tree_Navigation_Views
			Sheet sheet = SpreadSheet.createFromFile(file).getSheet(8);
			updateTestCaseExecutionResult(sheet);
			// Generate Summary Sheet
			sheet = sheet.getSpreadSheet().getSheet(0);
			sheet.getCellAt("G8").setValue((testCasesPassed + testCasesFailed));
			sheet.getCellAt("H8").setValue(testCasesPassed);
			sheet.getCellAt("I8").setValue(testCasesFailed);
			// totalNoOfTestCasesPassed & totolNoOfTestCasesFailed
			totalNoOfTestCasesPassed = testCasesPassed;
			totalNoOfTestCasesFailed = testCasesFailed;

			File outputFile = new File(GlobalVariables.sReportDstDirectoryPath + "\\FunctionalTestCase.ods");
			sheet.getSpreadSheet().saveAs(outputFile);

			generateTestCaseIndex();
			generateTestCaseSummary();
			generateFinalTestPassReport();
			generateFailureReport();
			System.out.println("Report generated successfully");
		}
		catch(Exception e) {
			System.out.println("Error Occured in generateAutomationReportForFunctionalTestCases() function.");
			e.printStackTrace();
		}
	}
}