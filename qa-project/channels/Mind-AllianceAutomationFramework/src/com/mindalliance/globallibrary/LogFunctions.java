package com.mindalliance.globallibrary;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class LogFunctions {
	
	/**
	 * Get Current DateTime
	 * @return
	 */
	public static String getDateTime() {
	    DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
	    Date date = new Date();
	    return dateFormat.format(date);
	} 
	
	/**
	 * File Logger
	 * @param logString
	 */
	public static void writeLogs(String sLog) {
		try {
			FileWriter fileWriter = new FileWriter(GlobalVariables.sLogFile, true);
			BufferedWriter out = new BufferedWriter(fileWriter);
		    out.write(getDateTime() + ":" + sLog);
		    out.newLine();
		    out.flush();
		    out.close();
		}
		catch (IOException e) {
			System.out.println("Error Occured in writeLogs() function. \n");
			e.printStackTrace();
		}
	}
	
	/**
	 * Result File Logger
	 * @param testCaseId
	 * @param verifyStepNo
	 * @param description
	 * @param result
	 * @param comment
	 */
	public static void writeResults(String sTestCaseId, int iVerifyStepNo, String sDescription, String sResult,
			String sScriptException, String sErrorReport) {
		try {
			if (sScriptException.contains(",")) {
				sScriptException = sScriptException.replaceAll(",", "|");
				sScriptException = sScriptException.replaceAll("\n", " ");
				// sScriptException = sScriptException.substring(0, sScriptException.indexOf("}") + 1);	
			}
			FileWriter fileWriter = new FileWriter(GlobalVariables.sResultCsvFile, true);
			BufferedWriter out = new BufferedWriter(fileWriter);
			out.write(sTestCaseId + "," + iVerifyStepNo + "," + sDescription + "," + sResult + "," + sScriptException + "," + sErrorReport);
			out.newLine();
			out.flush();
			out.close();
		}
		catch (IOException e) {
			System.out.println("Error Occured in writeResults() function.\n");
		}
	}
	
	/**
	 * Error File Logger
	 * @param logString
	 */
	public static void writeErrorLogs(String sLog) {
		try {
			FileWriter fileWriter = new FileWriter(GlobalVariables.sErrorLogSubDirectoryPath + "\\" + GlobalVariables.sTestCaseId + ".logs", true);
		    BufferedWriter out = new BufferedWriter(fileWriter);
		    out.write(getDateTime() + ":" + sLog);
		    out.newLine(); 
		    out.flush();
		    out.close();
		}
		catch (IOException e) {
			System.out.println("Error Occured in writeErrorLogs() function.\n");
		}
	}	
}