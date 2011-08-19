package com.mindalliance.testscripts;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;

import com.mindalliance.globallibrary.GlobalVariables;
/**
 * Summary: Update Planners information under Admin web-page
 * @author AfourTech
 *
 */

public class UpdatePlanner 
{
    public UpdatePlanner() {
    		String sChannelURL = "http://192.168.1.126:8081/";
    		String sUserName = "jf";
    		String sPassword = "Mind-Alliance";
		try {
				GlobalVariables.oDriver = new FirefoxDriver();
				GlobalVariables.oDriver.get(sChannelURL);
				GlobalVariables.oElement = GlobalVariables.oDriver.findElement(By.name("j_username"));
				GlobalVariables.oElement.sendKeys(sUserName);
				GlobalVariables.oElement = GlobalVariables.oDriver.findElement(By.name("j_password"));
				GlobalVariables.oElement.sendKeys(sPassword);
				GlobalVariables.oDriver.findElement(By.name("_spring_security_remember_me")).click();
				GlobalVariables.oDriver.findElement(By.xpath("/html/body/div/div[2]/form/div[6]/input")).click();
				// WebElement Synchronization
				Thread.currentThread();
				Thread.sleep(5000);
				
				// Click on 'Channel Administration' link
				GlobalVariables.iStepNo++ ;
				GlobalVariables.sDescription = "Navigated to Channel Administration";
				GlobalVariables.oDriver.findElement(By.linkText("Channels administration")).click();
				// WebElement Synchronization
				Thread.currentThread();
				Thread.sleep(3000);				
				// get list under Actions pop up menu
				int countUsers = 1 , countPlanners = 0;
				GlobalVariables.oElement = GlobalVariables.oDriver.findElement(By.xpath("//body/div/div[2]/div/form/table[7]/tbody"));
				List<WebElement> trs = GlobalVariables.oElement.findElements(By.tagName("tr"));
				List<WebElement> tds;
				for(WebElement tr: trs)
				{
					tds = tr.findElements(By.tagName("td"));
					for(WebElement td: tds)
					{
						if(td.getText().contains("planner")){
							GlobalVariables.oElement = GlobalVariables.oDriver.findElement(By.name("item:"+ (countUsers - 1) +":group:fullName"));
							for (int i = 0; i <= 8; i++)
								GlobalVariables.oElement.sendKeys(Keys.BACK_SPACE);
							GlobalVariables.oElement.sendKeys("Planner"+(countUsers - 5));
							GlobalVariables.oElement = GlobalVariables.oDriver.findElement(By.name("item:" + (countUsers - 1) + ":group:password"));
							GlobalVariables.oElement.sendKeys("@test123");
							GlobalVariables.oDriver.findElement(By.xpath("//body/div/div[2]/div/form/table[7]/tbody/tr[" + (countUsers) + "]/td[6]/input")).click();
							countPlanners++;
						}
					}
					countUsers++;
				}
				System.out.println("Total Planners: " + countPlanners);
				// WebElement Synchronization
				Thread.currentThread();
				Thread.sleep(2000);
				GlobalVariables.oDriver.findElement(By.name("Submit")).submit();
				// WebElement Synchronization
				Thread.currentThread();
				Thread.sleep(2000);
				GlobalVariables.oDriver.findElement(By.className("logout")).click();
				// GlobalVariables.oDriver.findElement(By.xpath("/html/body/div/div/div/div[2]/div/ul/li/a")).click();
				// WebElement Synchronization
				Thread.currentThread();
				Thread.sleep(2000);
				GlobalVariables.oDriver.quit();
	} 
		catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}
    public static void main(String args[]) {
		try {
			new UpdatePlanner();
		} 
		catch (Exception oException) {
			// TODO Auto-generated catch block
			oException.printStackTrace();
		}
	}
}