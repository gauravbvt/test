package com.mindalliance.configurations;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
/**
 * The LogFunctions class contains all the logs related methods which are helpful for analyzing the results of the test case.
 * @author AFourTech
 */
public class LogFunctions {
	
	/**
	 * This method write logs details to the file 
	 * @param info
	 */
	public static String logDescription(String descriptions) {
		try {
			if (GlobalVariables.testResultLogFile.exists()) {
				BufferedWriter errorFileWriter = new BufferedWriter(new FileWriter(GlobalVariables.testResultLogFile,true));
				errorFileWriter.write(descriptions);
				errorFileWriter.newLine();
				errorFileWriter.close();
			}
		}catch (IOException e) {
			LogFunctions.logException(e.getMessage());
		}
		return descriptions;
	}
	
	/**
	 * Creates an exception details if an error occurred in file
	 * @param info
	 */
	public static String logException(String exception) {
		try {
			GlobalVariables.testResultErrorFile = generateLogFile(GlobalVariables.errorLogFile+"_"+GlobalVariables.testCaseId+".log");
			if (GlobalVariables.testResultErrorFile.exists()) {
				BufferedWriter errorFileWriter = new BufferedWriter(new FileWriter(GlobalVariables.testResultErrorFile,true));
				errorFileWriter.write(exception);
				errorFileWriter.newLine();
				errorFileWriter.close();
			}
		} catch (IOException e) {
			LogFunctions.logException(e.getMessage());
		}
		return exception;
	}
	
	/** 
	 * This method generate log directory in simple data format.
	 */
	public static void generateLogsDirectory() {
		try {
			DateFormat dateFormat = new SimpleDateFormat("yyyy_MM_dd_hh_mm_ss");
			GlobalVariables.currentDate = new Date();
			GlobalVariables.logDirectoryName = dateFormat.format(GlobalVariables.currentDate);
			GlobalVariables.logDirectoryPath = GlobalVariables.currentDirectory.getCanonicalPath().toString()+ "\\Logs\\"+ GlobalVariables.logDirectoryName;
			File Dir = new File(GlobalVariables.logDirectoryPath);
			Dir.mkdir();
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
	}

	/**
	 * This method create log file.
	 * @param logFileName
	 * @return
	 */
	public static File generateLogFile(String logFileName) {
		try {
			File logFile = new File(GlobalVariables.logDirectoryPath+ "\\" + logFileName);
			if (!logFile.exists())
				logFile.createNewFile();
			return logFile;
		} catch (IOException e) {
			System.out.println(e.getMessage());
			return null;
		}
	}
}