package com.mindalliance.configuration;

import java.awt.Color;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.apache.commons.io.FileUtils;
import org.jopendocument.dom.spreadsheet.Sheet;
import org.jopendocument.dom.spreadsheet.SpreadSheet;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.internal.selenesedriver.TakeScreenshot;

import com.csvreader.CsvReader;
import com.mindalliance.configuration.Configuration.TraceLevel;

/**
 * The Reporting class contains all the reporting related methods which are
 * helpful for analyzing the results of the test case.
 * 
 * @author AFourTech
 */
public class Reporting extends TakeScreenshot {
	public static int totalNoOfTestCasesPassed = 0;
	public static int totalNoOfTestCasesFailed = 0;
	public String logDirectoryName;
	public String logDirectoryPath;
	public String errorLogSubDirectoryPath;
	public String reportDirectoryName;
	public String reportSrcDirectoryPath;
	public String reportDstDirectoryPath;
	public String testDataDirectoryPath;
	public String blank = "";
	public String passed = "Pass";
	public String failed = "Fail";
	public String verifyError;
	public String notRun = "NOT RUN";
	public String automatesYes = "YES";
	int testCasesAutomated = 0;
	int testCasesPassed = 0;
	int testCasesFailed = 0;
	CsvReader products;
	CsvReader csvTestCase;
	String sSummary;
	String csvResult;
	String csvScriptException;
	String csvErrorReport;
	String arrayOfTestCaseId[] = new String[600];
	String arrayOftestCaseSummary[] = new String[600];
	String arrayOftestCaseResult[] = new String[600];
	String testName;
	public int noOfViewTestCasesExecuted = 0;
	public int noOfPlanTestCasesExecuted = 0;
	public int noOfCommandTestCasesExecuted = 0;
	public int noOfViewTestCasesPassed = 0;
	public int noOfPlanTestCasesPassed = 0;
	public int noOfCommandTestCasesPassed = 0;
	public int noOfViewTestCasesFailed = 0;
	public int noOfPlanTestCasesFailed = 0;
	public int noOfCommandTestCasesFailed = 0;
	public String startDateTime;
	public String endDateTime;

	static DateFormat dateFormatGMT = new SimpleDateFormat("yy-MM-dd HH:mm:ss");

	/**
	 * Takes a screen shot of the application page
	 * 
	 * @param fileName
	 *            The name of the file where the screen shot is saved
	 * @return String The path where the screen shot file is saved
	 * @throws UIAutomationException
	 * @throws IOException
	 */
	public static String getScreenShot(String fileName)
			throws UIAutomationException {
		try {
			fileName = GlobalVariables.configuration.getCurrentDir()
					.getCanonicalPath()
					+ File.separator
					+ "Reports_snapshot"
					+ File.separator + fileName + ".png";
			File scrFile = ((org.openqa.selenium.TakesScreenshot) GlobalVariables.configuration
					.getWebDriver()).getScreenshotAs(OutputType.FILE);
			FileUtils.copyFile(scrFile, new File(fileName));
		} catch (IOException io) {
			throw new UIAutomationException("Unable to create file '"
					+ fileName + ".png'");
		}
		return fileName;
	}

	// Returns minutes:seconds.milliseconds
	public static String actionTime(long startTimeMilliSecs) {
		long duration = System.currentTimeMillis() - startTimeMilliSecs;
		return ("" + duration / 60000 + ":" + (duration / 1000) % 60 + "." + duration % 1000);
	}

	// Sends info to System.out prefixed by time stamp and post fixed with
	// duration time
	public static void timeStampedActionTime(long startTimeMilliSecs,
			String message) {
		String duration = actionTime(startTimeMilliSecs); // Record time before
															// additional
															// formatting.
		trace(TraceLevel.Info, dateFormatGMT.format(new Date()) + " " + message
				+ duration);
	}

	/**
	 * This function prints stack trace if occurs
	 * 
	 * @param level
	 * @param message
	 */
	public static void trace(TraceLevel level, String message) {
		// Auto-generated method stub
		if (GlobalVariables.configuration.isTraceLevel(level)) {
			System.out.println(message);
		}
	}

	/**
	 * This function reads Results.csv file in Logs and generate testcase.html
	 */
	public void updateTestCaseExecutionResult() throws UIAutomationException,
			IOException {
		int index = 0;
		testCasesPassed = 0;
		testCasesFailed = 0;
		for (int i = 0; i < GlobalVariables.configuration.getList().getModel()
				.getSize(); i++) {
			testName = GlobalVariables.configuration.getList().getModel()
					.getElementAt(i).toString();
			if (testName != blank) {
				// Call readCsvFile
				String sResult = readCsvFile(testName);
				if (sResult == failed) {
					// Call generateAutomationReportInHtml()
					generateAutomationReportInHtml(testName);
					testCasesFailed++;
					arrayOfTestCaseId[index] = testName;
					GlobalVariables.configuration
							.setArrayOfTestCaseId(arrayOfTestCaseId);

					arrayOftestCaseResult[index++] = failed;
					GlobalVariables.configuration
							.setArrayOftestCaseResult(arrayOftestCaseResult);
				} else if (sResult == passed) {
					// Call generateAutomationReportInHtml()
					generateAutomationReportInHtml(testName);
					testCasesPassed++;
					arrayOfTestCaseId[index] = testName;
					GlobalVariables.configuration
							.setArrayOfTestCaseId(arrayOfTestCaseId);
					arrayOftestCaseResult[index++] = passed;
					GlobalVariables.configuration
							.setArrayOftestCaseResult(arrayOftestCaseResult);
				}
			}
		}
	}

	/**
	 * This function updates Test case sheet
	 */
	public void updateTestCaseSheetResult() throws UIAutomationException,
			IOException {
		try {
			int cnt = 0;
			File file, outputFile;
			do {
				cnt++;
				if (cnt == 1)
					file = new File(GlobalVariables.configuration
							.getCurrentDir().getCanonicalPath().toString()
							+ "\\TestCases\\Mind-AllianceTestCaseSheet.ods");
				else
					file = new File(GlobalVariables.configuration
							.getCurrentDir().getCanonicalPath().toString()
							+ "\\TestCases\\Mind-AllianceTestCaseSheet_V2.ods");
				Sheet sheet = SpreadSheet.createFromFile(file).getSheet(0);

				// Update View, Plan & Command Sheets
				for (int i = 0; i < GlobalVariables.configuration.getList()
						.getModel().getSize(); i++) {
					testName = GlobalVariables.configuration.getList()
							.getModel().getElementAt(i).toString();

					// Call readCsvFile
					String sResult = readCsvFile(testName);

					if (testName.contains("MAV")) {
						sheet = sheet.getSpreadSheet().getSheet(1);
						// No Of Test Cases Passed & Failed of Views
						if (sResult.equals(passed))
							noOfViewTestCasesPassed++;
						else
							noOfViewTestCasesFailed++;

					} else if (testName.contains("MAP")) {
						sheet = sheet.getSpreadSheet().getSheet(2);
						// No Of Test Cases Passed & Failed of Plans
						if (sResult.equals(passed))
							noOfPlanTestCasesPassed++;
						else
							noOfPlanTestCasesFailed++;
					} else if (testName.contains("MAC")) {
						sheet = sheet.getSpreadSheet().getSheet(3);
						// No Of Test Cases Passed & Failed of Commands
						if (sResult.equals(passed))
							noOfCommandTestCasesPassed++;
						else
							noOfCommandTestCasesFailed++;
					}

					for (int j = 1; j < sheet.getRowCount(); j++) {
						if (testName.equals(sheet.getValueAt(0, j).toString())) {
							if (sResult == failed) {
								sheet.getCellAt("J" + (j + 1))
										.setBackgroundColor(Color.CYAN);
								sheet.getCellAt("J" + (j + 1)).setValue(failed);
								sheet.getCellAt("K" + (j + 1))
										.setBackgroundColor(Color.CYAN);
								sheet.getCellAt("K" + (j + 1)).setValue(
										csvScriptException);
								sheet.getCellAt("L" + (j + 1))
										.setBackgroundColor(Color.CYAN);
								sheet.getCellAt("L" + (j + 1)).setValue(
										csvErrorReport);
							} else if (sResult == passed) {
								sheet.getCellAt("J" + (j + 1))
										.setBackgroundColor(Color.ORANGE);
								sheet.getCellAt("J" + (j + 1)).setValue(passed);
							}
						}
					}
				}

				// Update Summary Sheet
				sheet = sheet.getSpreadSheet().getSheet(0);
				// No. Of Test Cases Executed of Views, Plans & Commands
				sheet.getCellAt("G8").setValue(noOfViewTestCasesExecuted);
				sheet.getCellAt("G9").setValue(noOfPlanTestCasesExecuted);
				sheet.getCellAt("G10").setValue(noOfCommandTestCasesExecuted);
				// No. Of Test Cases Passed of Views, Plans & Commands
				sheet.getCellAt("H8").setValue(noOfViewTestCasesPassed);
				sheet.getCellAt("H9").setValue(noOfPlanTestCasesPassed);
				sheet.getCellAt("H10").setValue(noOfCommandTestCasesPassed);
				// No. Of Test Cases Failed of Views, Plans & Commands
				sheet.getCellAt("I8").setValue(noOfViewTestCasesFailed);
				sheet.getCellAt("I9").setValue(noOfPlanTestCasesFailed);
				sheet.getCellAt("I10").setValue(noOfCommandTestCasesFailed);

				if (cnt == 1)
					outputFile = new File(GlobalVariables.configuration
							.getCurrentDir().getCanonicalPath()
							+ "\\Reports\\UIAutomationReport"
							+ "\\Mind-AllianceTestCaseSheet.ods");
				else
					outputFile = new File(GlobalVariables.configuration
							.getCurrentDir().getCanonicalPath()
							+ "\\Reports\\UIAutomationReport"
							+ "\\Mind-AllianceTestCaseSheet_V2.ods");
				sheet.getSpreadSheet().saveAs(outputFile);

				// Set Pass/Fail Count to 0.
				noOfViewTestCasesPassed = 0;
				noOfPlanTestCasesPassed = 0;
				noOfCommandTestCasesPassed = 0;
				noOfViewTestCasesFailed = 0;
				noOfPlanTestCasesFailed = 0;
				noOfCommandTestCasesFailed = 0;

			} while (cnt != 2);
		} catch (IOException ie) {
			throw new UIAutomationException("File not found at path '"
					+ GlobalVariables.configuration.getCurrentDir()
							.getCanonicalPath()
					+ "\\Reports\\UIAutomationReport"
					+ "\\Mind-AllianceTestCaseSheet.ods" + "'");
		}
	}

	/**
	 * Updates test case sheet result for functional test cases
	 * 
	 * @throws IOException
	 * @throws UIAutomationException
	 */
	public void updateTestCaseSheetResultForFunctionalTestCases()
			throws UIAutomationException, IOException {
		try {
			File file = new File(
					GlobalVariables.configuration.getCurrentDir()
							.getCanonicalPath().toString()
							+ "\\TestCases\\Mind-AllianceChannelsFunctionalTestCaseSheetV2.ods");
			Sheet sheet = SpreadSheet.createFromFile(file).getSheet(0);

			// Update Test Case Sheet
			for (int i = 1; i < 18; i++) {
				sheet = sheet.getSpreadSheet().getSheet(i);
				for (int j = 1; j < sheet.getRowCount(); j++) {
					sheet.getCellAt("I" + j).setValue("");
					sheet.getCellAt("L" + j).setValue("");
					sheet.getCellAt("M" + j).setValue("");
					sheet.getCellAt("N" + j).setValue("");
					sheet.getCellAt("O" + j).setValue("");
					sheet.getCellAt("P" + j).setValue("");
					sheet.getCellAt("Q" + j).setValue("");
					sheet.getCellAt("R" + j).setValue("");
					sheet.getCellAt("S" + j).setValue("");
					sheet.getCellAt("T" + j).setValue("");
				}
				sheet.getCellAt("I1").setValue("RESULT");
				sheet.getCellAt("L1").setValue("SCRIPT EXCEPTION");
				sheet.getCellAt("R1").setValue("ERROR");
			}
			for (int i = 0; i < GlobalVariables.configuration.getList()
					.getModel().getSize(); i++) {
				testName = GlobalVariables.configuration.getList().getModel()
						.getElementAt(i).toString();
				if (testName.contains("CL"))
					sheet = sheet.getSpreadSheet().getSheet(2);
				else if (testName.contains("HP"))
					sheet = sheet.getSpreadSheet().getSheet(5);
				else if (testName.contains("CA"))
					sheet = sheet.getSpreadSheet().getSheet(3);
				else if (testName.contains("DC"))
					sheet = sheet.getSpreadSheet().getSheet(6);
				else if (testName.contains("CC"))
					sheet = sheet.getSpreadSheet().getSheet(7);
				else if (testName.contains("CP"))
					sheet = sheet.getSpreadSheet().getSheet(8);
				else if (testName.contains("PS"))
					sheet = sheet.getSpreadSheet().getSheet(9);
				else if (testName.contains("TF"))
					sheet = sheet.getSpreadSheet().getSheet(10);
				else if (testName.contains("TE"))
					sheet = sheet.getSpreadSheet().getSheet(11);
				else if (testName.contains("IF"))
					sheet = sheet.getSpreadSheet().getSheet(12);
				else if (testName.contains("PP"))
					sheet = sheet.getSpreadSheet().getSheet(13);
				else if (testName.contains("PE"))
					sheet = sheet.getSpreadSheet().getSheet(14);
				else if (testName.contains("LF"))
					sheet = sheet.getSpreadSheet().getSheet(15);
				else if (testName.contains("SG"))
					sheet = sheet.getSpreadSheet().getSheet(16);
				else if (testName.contains("IS"))
					sheet = sheet.getSpreadSheet().getSheet(17);

				// Call readCsvFile
				String sResult = readCsvFile(testName);
				for (int j = 1; j < sheet.getRowCount(); j++) {
					if (testName.equals(sheet.getValueAt(0, j).toString())) {
						if (sResult == failed) {
							sheet.getCellAt("I" + (j + 1)).setBackgroundColor(
									Color.CYAN);
							sheet.getCellAt("I" + (j + 1)).setValue(failed);
							sheet.getCellAt("L" + (j + 1)).setBackgroundColor(
									Color.CYAN);
							sheet.getCellAt("L" + (j + 1)).setValue(
									csvScriptException);
							sheet.getCellAt("R" + (j + 1)).setBackgroundColor(
									Color.CYAN);
							sheet.getCellAt("R" + (j + 1)).setValue(
									csvErrorReport);
						} else if (sResult == passed) {
							sheet.getCellAt("I" + (j + 1)).setBackgroundColor(
									Color.ORANGE);
							sheet.getCellAt("I" + (j + 1)).setValue(passed);
						}
						break;
					}
				}
			}

			File outputFile = new File(GlobalVariables.configuration
					.getCurrentDir().getCanonicalPath()
					+ "\\Reports\\UIAutomationReport"
					+ "\\Mind-AllianceChannelsFunctionalTestCaseSheetV2.ods");
			sheet.getSpreadSheet().saveAs(outputFile);
		} catch (IOException ie) {
			throw new UIAutomationException(
					"File not found at path '"
							+ GlobalVariables.configuration.getCurrentDir()
									.getCanonicalPath()
							+ "\\TestCases\\Mind-AllianceChannelsFunctionalTestCaseSheetV2.ods"
							+ "'");
		}
	}

	/**
	 * Read TestCaseId for Automation UI (i.e. Home.java)
	 * 
	 * @param sheetNumber
	 * @return
	 * @throws UIAutomationException
	 * @throws IOException
	 * @throws XMLStreamException
	 */
	public String[] readTestCaseId(int sheetNumber)
			throws UIAutomationException, IOException {
		int index = 0;
		try {
			File currentDir = new File(".");
			String path = currentDir.getCanonicalPath().toString()
					+ "\\TestCases\\";
			File file = new File(path + "Mind-AllianceTestCaseSheet.ods");

			// TestCase sheet: Tree_Navigation_Views
			Sheet sheet = SpreadSheet.createFromFile(file)
					.getSheet(sheetNumber);
			String[] arrayOfTestCaseId = new String[600];
			testName = null;
			index = 0;
			for (int i = 2; i <= sheet.getRowCount(); i++) {
				testName = sheet.getCellAt("A" + i).getValue().toString();
				if (testName != null) {
					if (sheet.getCellAt("I" + i).getValue().toString()
							.toUpperCase().equals(automatesYes)) {
						arrayOfTestCaseId[index] = testName;
						index++;
					}
				}

			}
			sheet.detach();
			return arrayOfTestCaseId;
		} catch (IOException ie) {
			throw new UIAutomationException("File not found at path '"
					+ GlobalVariables.configuration.getCurrentDir()
							.getCanonicalPath() + "\\TestCases\\" + "'");
		}
	}

	/**
	 * Read Result Csv File
	 * @throws UIAutomationException
	 * @throws IOException
	 */
	public String readCsvFile(String sTestCaseId) throws UIAutomationException,
			IOException {
		try {
			csvResult = passed;
			csvScriptException = blank;
			csvErrorReport = blank;
			CsvReader csvTestCase = new CsvReader(GlobalVariables.configuration
					.getCurrentDir().getCanonicalPath()
					+ "\\Logs\\UILogs\\Results.csv");
			csvTestCase.readHeaders();
			while (csvTestCase.readRecord()) {
				if (sTestCaseId.equals(csvTestCase.get("TestCaseId"))
						&& csvTestCase.get("Result").equals(failed)) {
					csvResult = csvTestCase.get("Result");
					csvScriptException = csvTestCase.get("ScriptException");
					csvErrorReport = csvTestCase.get("ErrorReport");
				}
			}
			csvTestCase.close();
			if (csvResult.equals(failed))
				return failed;
			else if (csvResult.equals(passed))
				return passed;
			else
				return notRun;
		} catch (IOException e) {
			throw new UIAutomationException("File not found at path '"
					+ GlobalVariables.configuration.getCurrentDir()
							.getCanonicalPath() + "\\Logs\\UILogs\\Results.csv"
					+ "'");
		}
	}

	/**
	 * Generate AutomationReport in Ods
	 * 
	 * @throws UIAutomationException
	 * @throws Exception
	 * @throws XMLStreamException
	 */
	public void generateAutomationReport() throws IOException,
			UIAutomationException {
		// Update Test Case Execution Result
		updateTestCaseExecutionResult();

		// Update Test Case Sheet Execution Result
		updateTestCaseSheetResult();

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
		zipFolder(GlobalVariables.configuration.getCurrentDir()
				.getCanonicalPath() + "\\Reports\\UIAutomationReport",
				GlobalVariables.configuration.getCurrentDir()
						.getCanonicalPath()
						+ "\\Reports\\UIAutomationReport.zip");
		System.out.println("Zipped");
	}

	/**
	 * Generate Final TestPass Report
	 * 
	 * @throws UIAutomationException
	 * @throws XMLStreamException
	 * @throws IOException
	 */
	private void generateFinalTestPassReport() throws UIAutomationException,
			IOException {
		try {
			OutputStream destination = new FileOutputStream(GlobalVariables.configuration.getCurrentDir().getCanonicalPath()+ "\\Reports\\UIAutomationReport\\index.htm");
			XMLOutputFactory outputFactory = XMLOutputFactory.newInstance();
			XMLStreamWriter xml = outputFactory.createXMLStreamWriter(destination);

			xml.writeStartDocument();
			xml.writeStartElement("html");
			xml.writeDefaultNamespace("http://www.w3.org/1999/xhtml");
			xml.writeStartElement("head");
			xml.writeStartElement("title");
			xml.writeCharacters("Mind-Alliance Automation TestPass Report: "+ reportDirectoryName);
			xml.writeEndElement();
			xml.writeEndElement();
			xml.writeStartElement("frameset");
			xml.writeAttribute("rows", "25%,*");
			xml.writeAttribute("border", "0");
			xml.writeStartElement("frame");
			xml.writeAttribute("src", "TestPassSummary.htm");
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
		} catch (IOException ie) {
			throw new UIAutomationException("File not found at path '"+ GlobalVariables.configuration.getCurrentDir().getCanonicalPath()+ "\\Reports\\UIAutomationReport\\index.htm" + "'");
		} catch (XMLStreamException xe) {
			throw new UIAutomationException("XML File not found");
		}
	}

	/**
	 * Generate TestCase Summary
	 * 
	 * @throws UIAutomationException
	 * @throws XMLStreamException
	 * @throws IOException
	 */
	private void generateTestCaseSummary() throws UIAutomationException {
		try {

			OutputStream destination = new FileOutputStream(
					GlobalVariables.configuration.getCurrentDir()
							.getCanonicalPath()
							+ "\\Reports\\UIAutomationReport\\TestPassSummary.htm");
			XMLOutputFactory outputFactory = XMLOutputFactory.newInstance();
			XMLStreamWriter xml = outputFactory
					.createXMLStreamWriter(destination);

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
			xml.writeAttribute("border", "0");
			xml.writeAttribute("cellpadding", "0");
			xml.writeAttribute("cellspacing", "0");
			xml.writeAttribute("width", "100%");
			xml.writeStartElement("tr");
			xml.writeAttribute("bgColor", "#DDDDDD");
			xml.writeStartElement("td");
			xml.writeStartElement("center");
			xml.writeCharacters("Start Datetime: "
					+ GlobalVariables.configuration.getStartTime().toString());
			xml.writeEmptyElement("br");
		//	xml.writeCharacters("End Datetime: "
			//		+ GlobalVariables.configuration.endtime.toString());
			xml.writeEmptyElement("br");
			xml.writeCharacters("Browser: " + GlobalVariables.configuration.getConfigData().get("Browser").toString());
			xml.writeEmptyElement("br");
			xml.writeEndElement();
			xml.writeEndElement();
			xml.writeStartElement("td");
			xml.writeStartElement("center");
			xml.writeStartElement("img");
			xml.writeAttribute("src", "../../Images/Mind-Alliance_Logo.png");
			xml.writeAttribute("style", "border-style: none");
			xml.writeEndElement();
			xml.writeEndElement();
			xml.writeEndElement();
			xml.writeStartElement("td");
			xml.writeStartElement("center");
			xml.writeCharacters("Number of TestCases Executed: "
					+ (totalNoOfTestCasesPassed + totalNoOfTestCasesFailed));
			xml.writeEmptyElement("br");
			xml.writeCharacters("Number of TestCases Passed: "
					+ totalNoOfTestCasesPassed);
			xml.writeEmptyElement("br");
			xml.writeCharacters("Number of TestCases Failed: "
					+ totalNoOfTestCasesFailed);
			xml.writeEndElement();
			xml.writeEndElement();
			xml.writeEndElement();
			xml.writeEndElement();
			xml.writeEndElement();
			xml.writeEndElement();
			xml.writeEndDocument();
			xml.close();
			destination.close();
		} catch (IOException ie) {
			throw new UIAutomationException("File not found");
		} catch (XMLStreamException xe) {
			throw new UIAutomationException("XML File not found");
		}
	}

	/**
	 * Generate failure report
	 * 
	 * @throws UIAutomationException
	 */
	private void generateFailureReport() throws UIAutomationException {
		try {
			OutputStream destination = new FileOutputStream(
					GlobalVariables.configuration.getCurrentDir()
							.getCanonicalPath()
							+ "\\Reports\\UIAutomationReport\\TestCaseFailureList.htm");
			XMLOutputFactory outputFactory = XMLOutputFactory.newInstance();
			XMLStreamWriter xml = outputFactory
					.createXMLStreamWriter(destination);

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
			xml.writeAttribute("border", "0");
			xml.writeAttribute("cellpadding", "0");
			xml.writeAttribute("cellspacing", "0");
			xml.writeAttribute("width", "100%");
			xml.writeStartElement("caption");
			xml.writeStartElement("strong");
			xml.writeCharacters("Mind-Alliance Test scripts Failure Report");
			xml.writeEndElement();
			xml.writeStartElement("tr");
			xml.writeAttribute("bgColor", "#DDDDDD");
			xml.writeStartElement("td");
			xml.writeStartElement("center");
			xml.writeCharacters("Start Datetime: "+ GlobalVariables.configuration.getStartTime());
			xml.writeEmptyElement("br");
			xml.writeCharacters("End Datetime: "+ GlobalVariables.configuration.getEndtime());
			xml.writeEmptyElement("br");
			xml.writeCharacters("Browser: "
					+ GlobalVariables.configuration.getConfigData().get(
							"Browser"));
			xml.writeEmptyElement("br");
			xml.writeEndElement();
			xml.writeEndElement();
			xml.writeStartElement("td");
			xml.writeStartElement("center");
			xml.writeStartElement("img");
			xml.writeAttribute("src", "../../Images/Mind-Alliance_Logo.png");
			xml.writeAttribute("style", "border-style: solid");
			xml.writeEndElement();
			xml.writeEndElement();
			xml.writeEndElement();
			xml.writeStartElement("td");
			xml.writeStartElement("center");
			xml.writeCharacters("Number of TestCases Executed: "
					+ (totalNoOfTestCasesPassed + totalNoOfTestCasesFailed));
			xml.writeEmptyElement("br");
			xml.writeCharacters("Number of TestCases Passed: "
					+ totalNoOfTestCasesPassed);
			xml.writeEmptyElement("br");
			xml.writeCharacters("Number of TestCases Failed: "
					+ totalNoOfTestCasesFailed);
			xml.writeEmptyElement("br");

			xml.writeEndElement();
			xml.writeEndElement();
			xml.writeEndElement();
			// xml.writeEndElement();
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
			xml.writeAttribute("width", "100%");
			xml.writeStartElement("th");
			xml.writeStartElement("tr");
			xml.writeStartElement("td");
			xml.writeAttribute("bgColor", "#BBBBBB");
			xml.writeAttribute("onMouseover", "this.bgColor='#DDDDDD'");
			xml.writeAttribute("onMouseout", "this.bgColor='#BBBBBB'");
			xml.writeStartElement("strong");
			xml.writeCharacters("TestCaseId");
			xml.writeEndElement();
			xml.writeEndElement();
			xml.writeStartElement("td");
			xml.writeAttribute("bgColor", "#BBBBBB");
			xml.writeAttribute("onMouseover", "this.bgColor='#DDDDDD'");
			xml.writeAttribute("onMouseout", "this.bgColor='#BBBBBB'");
			xml.writeStartElement("center");
			xml.writeStartElement("strong");
			xml.writeCharacters("Result");
			xml.writeEndElement();
			xml.writeEndElement();
			xml.writeEndElement();
			xml.writeStartElement("td");
			xml.writeAttribute("bgColor", "#BBBBBB");
			xml.writeAttribute("onMouseover", "this.bgColor='#DDDDDD'");
			xml.writeAttribute("onMouseout", "this.bgColor='#BBBBBB'");
			xml.writeStartElement("center");
			xml.writeStartElement("strong");
			xml.writeCharacters("Script Exception");
			xml.writeEndElement();
			xml.writeEndElement();
			xml.writeEndElement();
			xml.writeStartElement("td");
			xml.writeAttribute("bgColor", "#BBBBBB");
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
			for (int i = 0; i < GlobalVariables.configuration.getList()
					.getModel().getSize(); i++) {
				if (GlobalVariables.configuration.getList().getModel()
						.getElementAt(i) != null) {
					csvTestCase = new CsvReader(GlobalVariables.configuration
							.getCurrentDir().getCanonicalPath()
							+ "\\Logs\\UILogs\\Results.csv");
					// csvTestCase = new
					// CsvReader(GlobalVariables.configuration.getLogDirectoryPath()
					// + "\\Results.csv");
					csvTestCase.readHeaders();
					while (csvTestCase.readRecord()) {
						if (csvTestCase.get("TestCaseId").equals(
								GlobalVariables.configuration.getList()
										.getModel().getElementAt(i).toString())) {
							if (csvTestCase.get("ScriptException") != blank
									|| csvTestCase.get("ErrorReport") != blank) {
								xml.writeStartElement("tr");
								xml.writeAttribute(
										"style",
										"WIDTH:235;BORDER:0;OVERFLOW-Y:scroll;WORD-WRAP:BREAK-WORD;OVERFLOW-X:hidden;padding:  2px 0px 2px 5px");
								xml.writeAttribute("bgColor", "#DDDDDD");
								xml.writeAttribute("padding", "");
								xml.writeStartElement("td");
								xml.writeAttribute("onMouseover",
										"this.bgColor='#EEEEEE'");
								xml.writeAttribute("onMouseout",
										"this.bgColor='#DDDDDD'");
								xml.writeStartElement("left");
								xml.writeCharacters(GlobalVariables.configuration
										.getList().getModel().getElementAt(i)
										.toString());
								xml.writeEndElement();
								xml.writeEndElement();
								xml.writeStartElement("td");
								xml.writeAttribute("onMouseover",
										"this.bgColor='#EEEEEE'");
								xml.writeAttribute("onMouseout",
										"this.bgColor='#DDDDDD'");
								xml.writeStartElement("center");
								xml.writeStartElement("font");
								if (arrayOftestCaseResult[i].equals(passed))
									xml.writeAttribute("color", "GREEN");
								else
									xml.writeAttribute("color", "RED");
								xml.writeCharacters(arrayOftestCaseResult[i]);
								xml.writeEndElement();
								xml.writeEndElement();
								xml.writeEndElement();
								xml.writeStartElement("td");
								xml.writeAttribute("onMouseover",
										"this.bgColor='#EEEEEE'");
								xml.writeAttribute("onMouseout",
										"this.bgColor='#DDDDDD'");
								xml.writeStartElement("center");
								if (csvTestCase.get("ScriptException") != blank) {
									xml.writeCharacters(csvTestCase
											.get("ScriptException"));
									xml.writeEndElement();
								}
								if (csvTestCase.get("ErrorReport") != blank) {
									xml.writeStartElement("td");
									xml.writeAttribute("onMouseover",
											"this.bgColor='#EEEEEE'");
									xml.writeAttribute("onMouseout",
											"this.bgColor='#DDDDDD'");
									xml.writeStartElement("center");
									xml.writeCharacters(csvTestCase
											.get("ErrorReport"));
									xml.writeEndElement();
									xml.writeEndElement();
								} else {
									xml.writeStartElement("td");
									xml.writeAttribute("bgColor", "#DDDDDD");
									xml.writeAttribute("onMouseover",
											"this.bgColor='#EEEEEE'");
									xml.writeAttribute("onMouseout",
											"this.bgColor='#DDDDDD'");
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
		} catch (IOException ie) {
			throw new UIAutomationException("File not found");
		} catch (XMLStreamException xe) {
			throw new UIAutomationException("XML File not found");
		}
	}

	/**
	 * Generate TestCase Index
	 * 
	 * @throws UIAutomationException
	 * @throws XMLStreamException
	 * @throws IOException
	 */
	private void generateTestCaseIndex() throws UIAutomationException {

		String arrayOftestCaseResult[] = new String[600];

		try {
			OutputStream destination = new FileOutputStream(
					GlobalVariables.configuration.getCurrentDir()
							.getCanonicalPath()
							+ "\\Reports\\UIAutomationReport\\TestCaseList.htm");
			XMLOutputFactory outputFactory = XMLOutputFactory.newInstance();
			XMLStreamWriter xml = outputFactory
					.createXMLStreamWriter(destination);

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
			xml.writeAttribute("width", "100%");
			xml.writeStartElement("th");
			xml.writeStartElement("tr");
			xml.writeStartElement("td");
			xml.writeAttribute("bgColor", "#BBBBBB");
			xml.writeAttribute("onMouseover", "this.bgColor='#DDDDDD'");
			xml.writeAttribute("onMouseout", "this.bgColor='#BBBBBB'");
			xml.writeStartElement("strong");
			xml.writeCharacters("TestCaseId");
			xml.writeEndElement();
			xml.writeEndElement();
			xml.writeStartElement("td");
			xml.writeAttribute("bgColor", "#BBBBBB");
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
			for (int i = 0; i < GlobalVariables.configuration.getList()
					.getModel().getSize(); i++) {
				if (GlobalVariables.configuration.getList().getModel()
						.getElementAt(i) != null) {
					xml.writeStartElement("tr");
					xml.writeAttribute(
							"style",
							"WIDTH:235;BORDER:0;OVERFLOW-Y:scroll;WORD-WRAP:BREAK-WORD;OVERFLOW-X:hidden;padding:  2px 0px 2px 5px");
					xml.writeAttribute("bgColor", "#DDDDDD");
					xml.writeAttribute("padding", "");
					xml.writeAttribute("onMouseover", "this.bgColor='#EEEEEE'");
					xml.writeAttribute("onMouseout", "this.bgColor='#DDDDDD'");
					xml.writeStartElement("td");
					xml.writeStartElement("a");
					xml.writeAttribute("href", GlobalVariables.configuration
							.getList().getModel().getElementAt(i)
							+ ".htm");
					xml.writeAttribute("target", "targetframe");
					xml.writeCharacters(GlobalVariables.configuration.getList()
							.getModel().getElementAt(i).toString());
					xml.writeEndElement();
					xml.writeEndElement();
					xml.writeStartElement("td");
					xml.writeStartElement("center");
					xml.writeStartElement("font");

					arrayOftestCaseResult = GlobalVariables.configuration
							.getArrayOftestCaseResult();

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
		} catch (IOException ie) {
			throw new UIAutomationException("File not found");
		} catch (XMLStreamException xe) {
			throw new UIAutomationException("XML File not found");
		}
	}

	/**
	 * Generate AutomationReport in HTML
	 * 
	 * @throws UIAutomationException
	 * @throws IOException
	 * @throws XMLStreamException
	 */
	public void generateAutomationReportInHtml(String testName)
			throws UIAutomationException {
		try {
			csvTestCase = new CsvReader(GlobalVariables.configuration
					.getCurrentDir().getCanonicalPath()
					+ "\\Logs\\UILogs\\Results.csv");
			OutputStream destination = new FileOutputStream(
					GlobalVariables.configuration.getCurrentDir()
							.getCanonicalPath()
							+ "\\Reports\\UIAutomationReport"
							+ "\\"
							+ testName
							+ ".htm");
			XMLOutputFactory outputFactory = XMLOutputFactory.newInstance();
			XMLStreamWriter xml = outputFactory
					.createXMLStreamWriter(destination);

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
			xml.writeAttribute("width", "100%");
			xml.writeStartElement("th");
			xml.writeStartElement("tr");
			xml.writeStartElement("td");
			xml.writeAttribute("bgColor", "#BBBBBB");
			xml.writeAttribute("onMouseover", "this.bgColor='#DDDDDD'");
			xml.writeAttribute("onMouseout", "this.bgColor='#BBBBBB'");
			xml.writeStartElement("center");
			xml.writeStartElement("strong");
			xml.writeCharacters("Step No");
			xml.writeEndElement();
			xml.writeEndElement();
			xml.writeEndElement();
			xml.writeStartElement("td");
			xml.writeAttribute("bgColor", "#BBBBBB");
			xml.writeAttribute("onMouseover", "this.bgColor='#DDDDDD'");
			xml.writeAttribute("onMouseout", "this.bgColor='#BBBBBB'");
			xml.writeStartElement("center");
			xml.writeStartElement("strong");
			xml.writeCharacters("Description");
			xml.writeEndElement();
			xml.writeEndElement();
			xml.writeEndElement();
			xml.writeStartElement("td");
			xml.writeAttribute("bgColor", "#BBBBBB");
			xml.writeAttribute("onMouseover", "this.bgColor='#DDDDDD'");
			xml.writeAttribute("onMouseout", "this.bgColor='#BBBBBB'");
			xml.writeStartElement("center");
			xml.writeStartElement("strong");
			xml.writeCharacters("Result");
			xml.writeEndElement();
			xml.writeEndElement();
			xml.writeEndElement();
			xml.writeStartElement("td");
			xml.writeAttribute("bgColor", "#BBBBBB");
			xml.writeAttribute("onMouseover", "this.bgColor='#DDDDDD'");
			xml.writeAttribute("onMouseout", "this.bgColor='#BBBBBB'");
			xml.writeStartElement("center");
			xml.writeStartElement("strong");
			xml.writeCharacters("Script Exception");
			xml.writeEndElement();
			xml.writeEndElement();
			xml.writeEndElement();
			xml.writeStartElement("td");
			xml.writeAttribute("bgColor", "#BBBBBB");
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
			while (csvTestCase.readRecord()) {
				if (testName.equals(csvTestCase.get("TestCaseId"))) {
					xml.writeStartElement("tr");
					xml.writeAttribute(
							"style",
							"WIDTH:235;BORDER:0;OVERFLOW-Y:scroll;WORD-WRAP:BREAK-WORD;OVERFLOW-X:hidden;padding:  2px 0px 2px 5px");
					xml.writeAttribute("bgColor", "#DDDDDD");
					xml.writeAttribute("padding", "");
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
					if (csvTestCase.get("Result").equals(passed))
						xml.writeAttribute("color", "GREEN");
					else
						xml.writeAttribute("color", "RED");
					xml.writeCharacters(csvTestCase.get("Result"));
					xml.writeEndElement();
					xml.writeEndElement();
					xml.writeEndElement();
					if (csvTestCase.get("ScriptException") != blank) {
						xml.writeStartElement("td");
						xml.writeStartElement("a");
						xml.writeAttribute("href",
								GlobalVariables.configuration.getCurrentDir()
										.getCanonicalPath()
										+ "\\Reports_Snapshot\\"
										+ testName
										+ ".png");
						xml.writeCharacters(csvTestCase.get("ScriptException"));
					} else {
						xml.writeStartElement("td");
					}
					xml.writeEndElement();
					if (csvTestCase.get("ErrorReport") != blank) {
						xml.writeStartElement("td");
						xml.writeCharacters(csvTestCase.get("ErrorReport"));
					} else {
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
		} catch (IOException ie) {
			throw new UIAutomationException("File not found");
		} catch (XMLStreamException xe) {
			throw new UIAutomationException("XML File not found");
		}
	}

	/**
	 * Read Test case Id of Functional test cases
	 * 
	 * @param sheetNumber
	 * @return
	 * @throws UIAutomationException
	 */
	public String[] readTestCaseIdForFunctional(int sheetNumber)
			throws UIAutomationException {
		int index = 0;
		try {
			File file1 = new File(
					GlobalVariables.configuration.getCurrentDir()
							.getCanonicalPath().toString()
							+ "\\TestCases\\Mind-AllianceChannelsFunctionalTestCaseSheetV2.ods");
			// TestCase sheet: Tree_Navigation_Views
			Sheet sheet1 = SpreadSheet.createFromFile(file1).getSheet(
					sheetNumber);
			String[] arrayOfTestCaseId = new String[600];
			testName = null;
			index = 0;
			for (int i = 2; i <= sheet1.getRowCount(); i++) {
				testName = sheet1.getCellAt("A" + i).getValue().toString();
				if (sheet1.getCellAt("J" + i).getValue().toString()
						.toUpperCase().equals(automatesYes)) {
					arrayOfTestCaseId[index] = testName;
					index++;
				}
			}
			sheet1.detach();
			return arrayOfTestCaseId;
		} catch (IOException io) {
			throw new UIAutomationException("File not found");
		}
	}

	/**
	 * Generate automation report For Functional test cases
	 * 
	 * @throws IOException
	 * @throws UIAutomationException
	 */
	public void generateAutomationReportForFunctionalTestCases()
			throws UIAutomationException, IOException {
		// Update Test Case Execution Result
		updateTestCaseExecutionResult();

		// Update Test Case Sheet Execution Result
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

		System.out.println("Zipped Completed");
	}

	public static void zipFolder(String srcFolder, String destZipFile)
			throws IOException {
		ZipOutputStream zip = null;
		FileOutputStream fileWriter = null;

		fileWriter = new FileOutputStream(destZipFile);
		zip = new ZipOutputStream(fileWriter);

		try {
			addFolderToZip("", srcFolder, zip);
		} catch (Exception e) {
			e.printStackTrace();
		}
		zip.flush();
		zip.close();
	}

	static private void addFileToZip(String path, String srcFile,
			ZipOutputStream zip) throws Exception {

		File folder = new File(srcFile);
		if (folder.isDirectory()) {
			addFolderToZip(path, srcFile, zip);
		} else {
			byte[] buf = new byte[1024];
			int len;
			@SuppressWarnings("resource")
			FileInputStream in = new FileInputStream(srcFile);
			zip.putNextEntry(new ZipEntry(path + "/" + folder.getName()));
			while ((len = in.read(buf)) > 0) {
				zip.write(buf, 0, len);
			}
		}
	}

	private static void addFolderToZip(String path, String srcFolder,
			ZipOutputStream zip) throws Exception {
		File folder = new File(srcFolder);

		for (String fileName : folder.list()) {
			if (path.equals("")) {
				addFileToZip(folder.getName(), srcFolder + "/" + fileName, zip);
			} else {
				addFileToZip(path + "/" + folder.getName(), srcFolder + "/"
						+ fileName, zip);
			}
		}
	}
}