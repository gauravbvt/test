package com.mindalliance.configuration;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.internal.selenesedriver.TakeScreenshot;

import com.mindalliance.configuration.Configuration.TraceLevel;

/**
 * The Reporting class contains all the reporting related methods which are 
 * helpful for analyzing the results of the test case.
 * @author AFourTech
 */
public class Reporting extends TakeScreenshot {
	
	static DateFormat dateFormatGMT = new SimpleDateFormat("yy-MM-dd HH:mm:ss");
	/**
     * Takes a screen shot of the application page 
     * @param  fileName		The name of the file where the screen shot is saved
     * @return String The path where the screen shot file is saved
	 * @throws UIAutomationException 
     * @throws IOException 
     */
    public static String getScreenShot(String fileName) throws UIAutomationException{    
   	   	try{
   	   		fileName=GlobalVariables.configuration.getCurrentDir().getCanonicalPath()+File.separator+"Reports_snapshot"+File.separator+fileName+".png";
	        File scrFile = ((org.openqa.selenium.TakesScreenshot)GlobalVariables.configuration.getWebDriver()).getScreenshotAs(OutputType.FILE);
    	    FileUtils.copyFile(scrFile, new File(fileName));
   	   	} catch(IOException io){
       		  throw new UIAutomationException("Unable to create file '"+fileName+".png'");
    	}
   	    return fileName;
    }
    
	// Returns minutes:seconds.milliseconds
    public static String actionTime(long startTimeMilliSecs){
		long duration = System.currentTimeMillis() - startTimeMilliSecs;
		return (""+duration/60000+":"+(duration/1000)%60+"."+duration%1000);
	}
    
    // Sends info to System.out  prefixed by time stamp and post fixed with duration time
    public static void timeStampedActionTime(long startTimeMilliSecs, String message){
    	String duration = actionTime(startTimeMilliSecs); // Record time before additional formatting.
		trace(TraceLevel.Info, dateFormatGMT.format(new Date())+" "+message+duration);
	}
    
	public static void trace(TraceLevel level, String message){
		// Auto-generated method stub
		if ( GlobalVariables.configuration.isTraceLevel(level)){
			System.out.println(message);
		}
	}
}
