package com.mindalliance.testscripts;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;

import com.mindalliance.globallibrary.ApplicationFunctionLibrary;
import com.mindalliance.globallibrary.GlobalVariables;

public class UpdateParticipants 
{
	public UpdateParticipants()
	{
		String sChannelURL = "http://192.168.1.126:8080/";
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
			
			// Click on 'Information Sharing for Participants' link
			GlobalVariables.iStepNo++ ;
			GlobalVariables.sDescription = "Navigated to Participants";
			GlobalVariables.oDriver.findElement(By.linkText("Information sharing guidelines for all participants")).click();
			// WebElement Synchronization
			Thread.currentThread();
			Thread.sleep(3000);
			
			// Click on Assign pop up menu to assign user to agent
			GlobalVariables.iStepNo++;
			GlobalVariables.sDescription="Assign";
			int countUsers = -2 , countPlanners = 0;
			ApplicationFunctionLibrary.MouseOverAndClick("/html/body/div/div[2]/div[2]/table/tbody/tr[2]/td[2]/ul/li/a", "Assign");
			List<WebElement> trs = GlobalVariables.oElement.findElements(By.tagName("ul"));
			List<WebElement> tds;	
			for(WebElement tr: trs)
			{
				tds = tr.findElements(By.tagName("li"));
				for(WebElement td: tds)
				{
					if(td.getText().contains("planner")){
						GlobalVariables.oDriver.findElement(By.partialLinkText("Planner"+(countUsers+1)));
						countPlanners++;
					}
				}
				countUsers++;
			}
			System.out.println("Total Planners: " + countPlanners);
		}
		catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}
    public static void main(String args[]) {
		try {
			new UpdateParticipants();
		} 
		catch (Exception oException) {
			// TODO Auto-generated catch block
			oException.printStackTrace();
		}
	}
}