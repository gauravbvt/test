package com.mindalliance.configuration;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import junit.framework.Assert;

import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoAlertPresentException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * The UIActions class contains all the methods that deal with some action being 
 * performed in the UI throughout the application. 
 * @author AFourTech
 */
public class UIActions {
	
	public static void scrollDown(){
		
			for(int i=0;i<35; i++){
				GlobalVariables.configuration.getWebElement().sendKeys(Keys.ARROW_DOWN);
			}
	}
	/**
	 *  Clicks on a particular page element. The page element is obtained from
	 *  the configuration object.
	 */
	public static void click(){
		GlobalVariables.configuration.getWebElement().click();
	}
	/**
	 * Clicks on particular page element
	 * @param fileName
	 * @param elementName
	 * @param findBys
	 * @param notFound
	 * @throws UIAutomationException
	 */
	public static void click(String fileName,String elementName,ArrayList<?> findBys, String notFound) throws UIAutomationException{
		int count =0;
		int timeout=Integer.parseInt(GlobalVariables.configuration.getConfigData().get("TimeOutForFindingElementSecondsForClick"));
		do{
			try
			{
				ElementController elementController=new ElementController();
				elementController.requireElementSmart(fileName, elementName, GlobalVariables.configuration.getAttrSearchList(), elementName);
				UIActions.click();
				break;
			}
			catch(Exception e){
				try {
					Thread.sleep(500);
				} catch (Exception e1){}
				count++;
			}
		}while(count>=timeout);
		if(count>=timeout){
			throw new UIAutomationException("Unable to click element. "+ elementName);
		}
		
	}
	/**
	 * Checks element is enable
	 * @param fileName
	 * @param elementName
	 * @param findBys
	 * @param notFound
	 * @return
	 * @throws UIAutomationException
	 */
	public static boolean checkEnable(String fileName,String elementName,ArrayList<?> findBys, String notFound) throws UIAutomationException{
		ElementController elementController=new ElementController();
		elementController.requireElementSmart(fileName, elementName, GlobalVariables.configuration.getAttrSearchList(), elementName);
		WebElement enabled=GlobalVariables.configuration.getWebElement();
		return enabled.isEnabled();
	}
	
	/**
	 * Enters value in the specified text box. The text box element is obtained from
	 * the configuration object.
	 * @param  value	String value to be entered in the text box
	*/
	public static void enterValueInTextBox(String value,String fileName,String elementName,ArrayList<?> findBys, String notFound) throws UIAutomationException{
		ElementController elementController=new ElementController();
		elementController.requireElementSmart(fileName, elementName, GlobalVariables.configuration.getAttrSearchList(), elementName);
		GlobalVariables.configuration.getWebElement().sendKeys(value);
	}
	/**
	 * Clears the text box by deleting the previous value in the text box. 
	 * @return void
	 * @throws UIAutomationException 
	 */	
	public static void clearTextBox(String fileName,String elementName,ArrayList<?> findBys, String notFound) throws UIAutomationException{
		ElementController elementController=new ElementController();
		elementController.requireElementSmart(fileName, elementName, GlobalVariables.configuration.getAttrSearchList(), elementName);
		GlobalVariables.configuration.getWebElement().clear();
	}
	
	/**
	 * Gets the text value of WebElement
	 */
	public static String getText(String fileName,String elementName,ArrayList<?> findBys, String notFound) throws UIAutomationException
	{
		
		String text = null; 
		int count =0;
		int timeout=Integer.parseInt(GlobalVariables.configuration.getConfigData().get("TimeOutForFindingElementSecondsForClick"));
		do{
			try
			{
				ElementController elementController=new ElementController();
				elementController.requireElementSmart(fileName, elementName, GlobalVariables.configuration.getAttrSearchList(), elementName);
				text = GlobalVariables.configuration.getWebElement().getText();
				break;
			}
			catch(Exception e){
				try {
					Thread.sleep(500);
				} catch (Exception e1){}
				count++;
			}
		}while(count>=timeout);
		if(count>=timeout){
			throw new UIAutomationException("Unable to click element. "+ elementName);
		}
		return text;		
	}
	
	public static Set<String> getHandles(){
		return GlobalVariables.configuration.getWebDriver().getWindowHandles();
	}
	/**
	 * Switch to new window
	 * @param handle
	 */
	public static void switchToNewwindow(String handle){
		GlobalVariables.configuration.getWebDriver().switchTo().window(handle);
	}
	/**
	 * Returns List of WebElement if it is present
	 * @param xPath
	 * @return
	 */
	public static List<WebElement> getElements(String xPath){
	   List<WebElement> userList  ;
	   userList=GlobalVariables.configuration.getWebDriver().findElements(By.xpath(xPath));
	   return userList;
		
	}
	/**
	 * Select & Click value in dropdown list by Text 
	 * @throws UIAutomationException
	 */
	public static void selectByTextAndClick(String text) throws UIAutomationException {
		List<WebElement> options = Configuration.getConfigurationObject().getSelect().getOptions();
		List<WebElement> optionsList=Configuration.getConfigurationObject().getWebElement().findElements(By.tagName("option"));
		String optiontext = "";
		
		int count = options.size();
		for (int option = 0; option < count; option++) {
			optiontext = options.get(option).getText();
			if (optiontext.equals(text)) {
				Configuration.getConfigurationObject().getSelect().selectByIndex(option);
				optionsList.get(option).click();
				optionsList.get(option).click();
				
				enterKey(Keys.ENTER);				
				break;
			}
		}
	}
	/**
	 * Selects value from dropdown
	 * @param text
	 * @throws UIAutomationException
	 */
	public static void selectByText(String text) throws UIAutomationException {
		List<WebElement> options = Configuration.getConfigurationObject().getSelect().getOptions();
		String optiontext = "";
		int count = options.size();
		int option=0;
		for (option = 0; option < count; option++) {
			optiontext = options.get(option).getText();
			if (optiontext.equals(text)) {
				Configuration.getConfigurationObject().getSelect().selectByIndex(option);
				break;
			}
		}		
	}
	
	 
	/**
	 * Checks whether the Alert dialog box is displayed or not else throws
	 * NoAlertPresentException.
	 * 
	 * @param webDriver
	 *            Object of WebDriver used to switch to Alert dialog box.
	 * 
	 * @return Object of Alert
	 */
	public static ExpectedCondition<Alert> alertAvailable() {
		return new ExpectedCondition<Alert>() {
			private Alert alert = null;

			@Override
			public Alert apply(WebDriver webDriver) {
				try {
					alert = webDriver.switchTo().alert();
				} catch (NoAlertPresentException nap) {
					try {
						Reporting.getScreenShot("AlertDialogBoxNotFound");
					} catch (UIAutomationException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					GlobalVariables.configuration.getWebDriver().quit();
					Assert.fail("Alert Dialog Box not found. See Screenshot in Reports directory. "
							+ nap.getMessage());
				}
				return alert;
			}
		};
	}

	/** 
	 * Verifies alert text begins with alertPrefix and accepts if it is. 
	 * If not starting with alertPrefix, assertion fails with assertMessage.
	 * @param assertMessage		Message for assertion
	 * @param alertPrefix		String value to be asserted against Alert Dialog box text
	 */
	public static void assertAlert(String assertMessage) {
		int alertWaitTimeoutSeconds = Integer.parseInt(GlobalVariables.configuration.getConfigData().get("TimeOutForFindingElementSeconds"));

		Wait<WebDriver> wait = new WebDriverWait(GlobalVariables.configuration.getWebDriver(),alertWaitTimeoutSeconds);
		Alert alert = wait.until(alertAvailable());

		String alertText = alert.getText();

		Assert.assertEquals(assertMessage,assertMessage,alertText.substring(0,Math.min(alertText.length(), assertMessage.length())));
		alert.accept();

		// Checks whether the Alert dialog box is displayed or not
		boolean alertFound = true;
		alertText = "";
		try {
			alert = GlobalVariables.configuration.getWebDriver().switchTo().alert();
			alertText = alert.getText(); 
		} catch (NoAlertPresentException nape) {
			alertFound = false;
		}

		Assert.assertSame("Alert Dialog box (" + alertText + ") Disappears.",false, alertFound);
	}
	
	/**
	 * Press keys e.g. TAB, ENTER, SPACE 
	 * @param keyName
	 */
	public static void enterKey(Keys keyName){
		GlobalVariables.configuration.getWebElement().sendKeys(keyName);
		}
	
	public static void enterKey(Keys keyName,String fileName,String elementName,ArrayList<?> findBys, String notFound) throws UIAutomationException{
		ElementController elementController=new ElementController();
		elementController.requireElementSmart(fileName, elementName, GlobalVariables.configuration.getAttrSearchList(), elementName);
		GlobalVariables.configuration.getWebElement().sendKeys(keyName);
		}
	/**
	 * Waits for page Title
	 * @param titleSuffix 		A portion of the title of the next page to be loaded to determine that the page has been loaded.
	 * @param pageLoadTimeout 	The wait time for the page to load.  
	 * @throws UIAutomationException 
	 * 
	 */
	public static void waitForTitle(final String titleSuffix, long pageLoadTimeout) throws UIAutomationException
	{
		long startTimeMilliSecs = System.currentTimeMillis(); 
		try{			
			int cnt	=	0;
			do{	
				try{
					String title	=	GlobalVariables.configuration.getWebDriver().getTitle();
					if(title.contains(titleSuffix)){
						break;
					}
				}
				catch(Exception e){}				
				cnt++;
	            Thread.sleep(500);
	            if(cnt==pageLoadTimeout){
	            	throw new UIAutomationException("Page with the title '"+titleSuffix+"' not found.");
	            }
				
			}while(cnt < pageLoadTimeout);
			System.out.println("URL " + titleSuffix + " load time: " + Reporting.actionTime(startTimeMilliSecs));
		}catch (InterruptedException ce){			
			throw new UIAutomationException("Unable to click to open new page");
		}	
		
	}
	/**
	 * Gets title of page
	 * @return
	 */
	public static String getTitle(){
		return GlobalVariables.configuration.getWebDriver().getTitle();
		
	}
}
