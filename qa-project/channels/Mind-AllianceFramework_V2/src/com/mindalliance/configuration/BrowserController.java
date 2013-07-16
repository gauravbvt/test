package com.mindalliance.configuration;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
/**
 * Creates the appropriate browser instance and set the Webdriver of browser.
 */

public class BrowserController{
	public static WebDriver driver=null;
	public static String browserName=null;
				
	/**
	 * Initialize the browser by passing parameter (browser)
	 * @param  browser		Mozilla Firefox / Chrome / Internet Explorer
	 * @throws UIAutomationException 
	*/
	@SuppressWarnings("deprecation")
	public void initializeDriver() throws UIAutomationException	{
		String browser=GlobalVariables.configuration.getConfigData().get("Browser");
		try{
			switch (browser) {
			case "Mozilla Firefox":
//				driver = new FirefoxDriver();
				driver = new FirefoxDriver();
		      	GlobalVariables.configuration.setWebDriver(driver);
		    	
		    	//Maximize the Browser
		    	GlobalVariables.configuration.getWebDriver().manage().window().maximize();
				break;
			case "Internet Explorer":
				driver=null;
	            DesiredCapabilities.internetExplorer().setJavascriptEnabled(true);       
	            String IEDriverPath = GlobalVariables.configuration.getCurrentDir().getCanonicalPath().toString()+File.separator+"lib"+File.separator+"IEDriverServer.exe";
	            System.setProperty("webdriver.ie.driver", IEDriverPath);
	            driver = new InternetExplorerDriver(DesiredCapabilities.internetExplorer());
	            GlobalVariables.configuration.setWebDriver(driver);
				break;
			case "Chrome":
			   driver=null;
	 	       String chromeDriverPath = GlobalVariables.configuration.getCurrentDir().getCanonicalPath().toString()+File.separator+"lib"+File.separator+"chromedriver.exe";
	 	       System.setProperty("webdriver.chrome.driver", chromeDriverPath); 
	 	       DesiredCapabilities capabilities = DesiredCapabilities.chrome();
	 	       capabilities.setCapability("chrome.switches", Arrays.asList("--start-maximized"));
	 	       driver=new ChromeDriver(capabilities);
	 	       GlobalVariables.configuration.setWebDriver(driver);
	 	       break;
			default:
				break;
			}
		}
		catch(IOException io){
			throw new UIAutomationException("Driver's .exe not found in lib folder.");
		}
		catch (IllegalStateException ie) {
			throw new UIAutomationException("Driver's .exe not found in lib folder.");
		}
		
	}	

    /**
     * Enters URL with parameter
     * @param URL
     * @param title
     * @throws UIAutomationException
     */
	public void enterURL(String URL,String title) throws UIAutomationException{
		GlobalVariables.configuration.getWebDriver().get(URL);
    	
    	// Assertion : Check Title of Page
       	UIActions.waitForTitle(title,Integer.parseInt(GlobalVariables.configuration.getConfigData().get("TimeOutForFindingElementSeconds")));
       	System.out.println("Hiee///");
    }
     
}
