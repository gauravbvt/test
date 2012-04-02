package com.mindalliance.configurations;

import java.io.File;
import java.net.HttpURLConnection;
import java.util.Date;
import java.util.Hashtable;
/**
 * The GlobalVariables class contains all the member variables used through out the application.
 * @author AFourTech 
 */
public class GlobalVariables {
	public static HttpURLConnection connection=null;
	public static String responseType="responsetype";
	public static String userCredentials="";
	public static String logDirectoryName;
	public static String logDirectoryPath;
	public static String testDataDirectoryPath;
	public static Hashtable<String,String> testData=new Hashtable<String,String>();
	public static Hashtable<String,String> responseData=new Hashtable<String,String>();
	public static String responseString="";
	public static String errorLogFile="Error";
	public static String logFile="TestResults";
	public static File testResultLogFile=null;
	public static File testResultErrorFile=null;
	public static int steps=0;
	public static String testCaseId="";	
	public static File currentDirectory = new File(".");
	public static Date currentDate = null;
//	public static String logDirectory="log";

}
