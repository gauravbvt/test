package com.mindalliance.configuration;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * This function write logs,result and get system time
 * @author afour
 *
 */
public class LogFunctions {
	public String testCaseId="";
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
	 * @throws UIAutomationException 
	 */
	public static void writeLogs(String sLog) throws UIAutomationException {	
		try {	
			FileWriter fileWriter=new FileWriter(GlobalVariables.configuration.getCurrentDir().getCanonicalPath().toString()+ File.separator+"Logs\\UILogs\\Logs.log",true);
			
//			FileWriter fileWriter = new FileWriter(GlobalVariables.configuration.getLogFile(), true);
			
			BufferedWriter out = new BufferedWriter(fileWriter);
		    out.write(getDateTime() + ":" + sLog);
//			out.write(getDateTime() + ":" + sLog);
		    out.newLine();
		    out.flush();
		    out.close();
		}
		catch (IOException e) {
			throw new UIAutomationException("Can not write log to logfile");
		}
	}
	
	/**
	 * Result File Logger
	 * @param testCaseId
	 * @param verifyStepNo
	 * @param description
	 * @param result
	 * @param comment
	 * @throws UIAutomationException 
	 */
	public static void writeResults(String sTestCaseId, int iVerifyStepNo, String sDescription, String sResult,
		String sScriptException, String sErrorReport) throws UIAutomationException {
		try{
			if (sScriptException.contains(",")) {
				sScriptException = sScriptException.replaceAll(",", "|");
				sScriptException = sScriptException.replaceAll("\n", " ");
				// sScriptException = sScriptException.substring(0, sScriptException.indexOf("}") + 1);	
			}
			FileWriter fileWriter=new FileWriter(GlobalVariables.configuration.getCurrentDir().getCanonicalPath().toString()+ File.separator+"Logs\\UILogs\\Results.csv",true);
			
//			FileWriter fileWriter = new FileWriter(GlobalVariables.configuration.getResultCsvFile(), true);
			BufferedWriter out = new BufferedWriter(fileWriter);
			out.write(sTestCaseId + "," + iVerifyStepNo + "," + sDescription + "," + sResult + "," + sScriptException + "," + sErrorReport);
//			out.write(sTestCaseId + "," + iVerifyStepNo + "," + sDescription + "," + sResult + "," + sScriptException + "," + sErrorReport);
			out.newLine();
			out.flush();
			out.close();
		}
		catch(IOException ie){
			throw new UIAutomationException("Can not write result to result file.");
		}
	}

	
	/**
	 * Error File Logger
	 * @param logString
	 * @throws UIAutomationException 
	 */
	public static void writeErrorLogs(String sLog) throws UIAutomationException {
		try {
//			String testCaseId="";
			FileWriter fileWriter=new FileWriter(GlobalVariables.configuration.getCurrentDir().getCanonicalPath().toString()+ File.separator+"Logs\\UILogs\\Errors");
//			FileWriter fileWriter = new FileWriter(GlobalVariables.configuration.getErrorLogSubDirectoryPath() + "\\" +testCaseId + ".logs", true);
		    BufferedWriter out = new BufferedWriter(fileWriter);
		    out.write(getDateTime() + ":" + sLog);
		    out.newLine(); 
		    out.flush();
		    out.close();
		}
		catch (IOException e) {
			throw new UIAutomationException("Can not write error logs to error log file.");
		}
	}
}