package com.mindalliance.configuration;

import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.ElementNotVisibleException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
/**
 * The ElementController class contains the methods to find element on Web page and wait until the element is not found.
 * @author AFourTech
 */
public class ElementController {
	DataController dataController=new DataController();
	static{
		GlobalVariables.configuration.setAttrSearchList(new ArrayList<String>());
		GlobalVariables.configuration.getAttrSearchList().add("Xpath");
		GlobalVariables.configuration.getAttrSearchList().add("Id");
		GlobalVariables.configuration.getAttrSearchList().add("Name");
		GlobalVariables.configuration.getAttrSearchList().add("PartialLinkText");
		GlobalVariables.configuration.getAttrSearchList().add("LinkText");
		GlobalVariables.configuration.getAttrSearchList().add("ClassName");
	}
	
	/**
	 * Waits for the element to be load, enable or visible
	 * @param elementType id / xpath / linktext / partiallinktext / name
	 * @param elementValue Value of id / xpath / linktext / partiallinktext / name
	 * @return boolean
	 * @throws InterruptedException 
	 */
		
	 public boolean waitForElement(String elementType, String elementValue)
     {
		 
			 int timeOutForFindingElementSeconds = Integer.parseInt(GlobalVariables.configuration.getConfigData().get("TimeOutForFindingElementSeconds"   ));			 
	         int count = 0;
	         do
	         {	       
	        	 try{
	        	  	WebElement element = null;
	                 switch (elementType.toLowerCase())
	                 {
	                     case "xpath": element = GlobalVariables.configuration.getWebDriver().findElement(By.xpath(elementValue));
	                         break;
	                     case "id": element = GlobalVariables.configuration.getWebDriver().findElement(By.id(elementValue));
	                         break;
	                     case "name": element = GlobalVariables.configuration.getWebDriver().findElement(By.name(elementValue));
	                         break;
	                     case "classname": element = GlobalVariables.configuration.getWebDriver().findElement(By.className(elementValue));
	                         break;
	                     case "linktext": element = GlobalVariables.configuration.getWebDriver().findElement(By.linkText(elementValue));
	                     break;
	                     case "partiallinktext ": element = GlobalVariables.configuration.getWebDriver().findElement(By.partialLinkText(elementValue));
	                     break;
	                 }
	
	                 // Verify if element present or not
	                 if (null != element)
	                 {
	                     if (element.isDisplayed())
	                     {
	                         return true;
	                     }
	                 }
	        	 }
	        	 catch (Exception e)
	             {}
	        	 finally{
	            	 try{
	        			 count++;
	                     Thread.sleep(500);
	        		 }catch(Exception e1){
	        			 System.out.print("ERROR : "+e1);
	        		 }
	             }
	         }
	         while (count < timeOutForFindingElementSeconds);
	         return false;
     }

	
	/**
	 * Finds element on a page by passing parameters ( xpath / id ) and corresponding value
	 * @param  findBy		id / xpath / linktext / partiallinktext / name
	 * @param  value     	Value of id / xpath / linktext / partiallinktext / name
	 * @throws UIAutomationException 
	 */

	public  void findElementBy(final String findBy,final String value) throws UIAutomationException
	{
			final Configuration configuration=GlobalVariables.configuration;
			
			if(waitForElement(findBy,value))
			{
				if(findBy.equals("Xpath")) 
				{    
					configuration.setWebElement(configuration.getWebDriver().findElement(By.xpath(value)));
					
				} else if(findBy.equals("Id")) 
				{
					configuration.setWebElement(configuration.getWebDriver().findElement(By.id(value)));	/* Find Element by Id */
					
				} else if(findBy.equals("Name")) 
				{
					configuration.setWebElement(configuration.getWebDriver().findElement(By.name(value)));	/* Find Element by Name */
					
				} else if(findBy.equals("ClassName")) 
				{
					configuration.setWebElement(configuration.getWebDriver().findElement(By.className(value)));	/* Find Element by ClassName */
					
				} else if(findBy.equals("LinkText")) 
				{
					configuration.setWebElement(configuration.getWebDriver().findElement(By.linkText(value)));	/* Find Element by LinkText */
					
				} else if(findBy.equals("PartialLinkText")) 
				{
					configuration.setWebElement(configuration.getWebDriver().findElement(By.partialLinkText(value)));	/* Find Element by PartialLinkText */
				}
			}
			else{
					throw new UIAutomationException("Element with Attribute '"+ findBy +"' and value '"+ value +"' not found on the page.");
			}
	} 


	/**
	 * Checks whether an element is present on a particular page as defined in the <page.xml> 
	 * in ObjectRepository. This method is primarily used to check for a page element and 
	 * return a boolean value which is passed to the assertion for a particular page.
	 *
	 * @param  fileName 	The XML file to be read
	 * @param  elementName  GivenName in the XML file
	 * @param  findBys   	ArrayList for smart search
	 * @return true if element with 'elementName' found else returns false.
	 * @throws UIAutomationException 
	 */
	public boolean findElementSmart(String fileName,String elementName,ArrayList<?> findBys) throws UIAutomationException{
		String attributeValue="";
		String attributeName="";
		Boolean elementFound=false;
		for(int i=0;i<findBys.size();i++){
			attributeName=findBys.get(i).toString();
			if(attributeName.equals("")){
				throw new UIAutomationException("Attribute name in '"+fileName+"' is blank.");
			}
			attributeValue=dataController.getPageDataElements(fileName,elementName, attributeName);
			if(attributeValue.equals("")){
				continue;
			}
			if(attributeValue.startsWith("[")){
				attributeValue = attributeValue.substring(1, attributeValue.length()-1);
			}
			findElementBy(findBys.get(i).toString(),attributeValue);
			elementFound=true;
			break;
		}
		return elementFound;
	}

	/**
	 * Waits for a particular element to be visible/enable
	 * @param  locator	Element to search for visibility
	 * @param  pageNo 	Page number to search for visibility
	 * @return Object of WebElement if it is enabled else throws ElementNotVisibleException exception
	 */
	public static ExpectedCondition<WebElement> visibilityOfElementLocated(final By locator, final int pageNo)
	{
			return new ExpectedCondition<WebElement>()
			{
				public WebElement apply(WebDriver driver){
				WebElement toReturn = driver.findElement(locator);
				if (toReturn.isEnabled()){
						return toReturn;
				}
				else{
						throw new ElementNotVisibleException("Page '"+pageNo+"' Not Found.");
				}
			}
		};
	}
	
    /**
     * Searches an element with 'elementName' in the application
     * @param fileName  	The name of xml file from which to search an element
     * @param givenName 	The GivenName in xml file
     * @param elementName 	The name of the element to be searched
     * @param notFound 		The name of the element which is not found
     * @return true if element with 'elementName' present in the application else return false
     * @throws UIAutomationException 
     */
    public boolean searchListFromTables(String fileName,String givenName,String elementName,String notFound) throws UIAutomationException{
    	GlobalVariables.configuration.setDifference(0);
    	boolean elementPresent=false;
  		requireElementSmart(fileName,givenName,GlobalVariables.configuration.getAttrSearchList(),notFound);
        	List<WebElement> links =  GlobalVariables.configuration.getWebElement().findElements(By.tagName("a"));
        	if(links.isEmpty()){
        		throw new UIAutomationException("Tag Name 'a' not found in Table.");
        	} 
        	else { 
        		for(WebElement link: links){
        			if(link.getText().length()!=0){
        				GlobalVariables.configuration.setDifference(link.getText().compareTo(elementName));
        				
        				if(link.getText().toLowerCase().compareTo(elementName.toLowerCase()) < 0 ){
        					continue;
        				} else if(link.getText().toLowerCase().compareTo(elementName.toLowerCase())==0){
        					elementPresent=true;
        					break;
        				} else if(link.getText().toLowerCase().compareTo(elementName.toLowerCase()) >  0 ){
        					elementPresent=false;
        					break;
        				}
        			}
        		}
        	}
        return elementPresent;
    }

    /**
     * Finds an expected element on a page as defined in the <page.xml> in ObjectRepository and
     * throws an exception if the element is not found. This method is called when we are 
     * expecting a certain element and we want to perform an operation on that element.
     * @param fileName 		The xml file to be read
	 * @param elementName  	GivenName in the xml file
	 * @param findBys   	ArrayList for smart search
     * @param notFound 		The name of the element which is not found
     * @return void
     * @throws UIAutomationException 
     */
    //  In future this method will be modified to return WebElement object
    public void requireElementSmart(String fileName,String elementName,ArrayList<?> findBys, String notFound) throws UIAutomationException  {
          Boolean elementFound=findElementSmart(fileName, elementName, findBys);
          if(elementFound==false){
              throw new UIAutomationException("Element '"+ notFound +"' with AttributeNames'"+ findBys.toString()+" and their respective values not found on the page.");
          }
     }
       
	/**
	 * Finds element on a page by passing parameters ( xpath / id ) and corresponding value
	 * @param  findBy		id / xpath / linktext / partiallinktext / name
	 * @param  value     	Value of id / xpath / linktext / partiallinktext / name
	 * @param  elementName 	Name of element to be searched for
	 * @throws UIAutomationException 
	 */
	public void findElementBy(final String findBy,final String value,String elementName) throws UIAutomationException{
			
			findElementBy(findBy, value);
	}
	/**
	 * Finds element on a page by passing parameters ( xpath / id ) and corresponding value
	 *
	 * @param  findBy		id / xpath / linktext / partiallinktext / name
	 * @param  value     	Value of id / xpath / linktext / partiallinktext / name
	 */
	public void findElements(final String findBy,final String value)  throws UIAutomationException
	{
		
		final Configuration configuration=GlobalVariables.configuration;
		if(findBy.equals("Xpath")) 
		{     
			configuration.setWebElements(configuration.getWebDriver().findElements(By.xpath(value)));
			
		} else if(findBy.equals("Id")) 
		{
			configuration.setWebElements(configuration.getWebDriver().findElements(By.id(value)));
		} else if(findBy.equals("Name")) 
		{
			configuration.setWebElements(configuration.getWebDriver().findElements(By.name(value)));
		} else if(findBy.equals("ClassName")) 
		{
			configuration.setWebElements(configuration.getWebDriver().findElements(By.className(value)));
		} else if(findBy.equals("LinkText")) 
		{
			configuration.setWebElements(configuration.getWebDriver().findElements(By.linkText(value)));
		} else if(findBy.equals("PartialLinkText")) 
		{
			configuration.setWebElements(configuration.getWebDriver().findElements(By.partialLinkText(value)));
		}
		
	} 
	
	/**
	 * Returns Xpath count for 'value' from the element.
	 * 
	 * @param findBy
	 *            id / xpath / linktext / partiallinktext / name
	 * @param value
	 *            Value of id / xpath / linktext / partiallinktext / name
	 * @param elementName
	 *            GivenName in the xml file
	 * @return int
	 * @throws UIAutomationException
	 */
	 public int getXPathCount(final String value) throws UIAutomationException
	 {
		   List<WebElement> webElementList = Configuration.getConfigurationObject().getWebDriver().findElements(By.xpath(value));
		   return webElementList.size(); 
	 }

	
}
