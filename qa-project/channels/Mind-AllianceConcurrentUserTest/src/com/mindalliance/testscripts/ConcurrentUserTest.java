package com.mindalliance.testscripts;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import com.sun.jna.win32.*;


import com.mindalliance.globallibrary.GlobalStatic;

public class ConcurrentUserTest {

	public static void org()
	{
	
		/*GlobalStatic.oDriver=new ChromeDriver();
		GlobalStatic.oDriver.get("http://afourtech.mind-alliance.com");
		GlobalStatic.oElement = GlobalStatic.oDriver.findElement(By.name("j_username"));
		GlobalStatic.oElement.sendKeys("afourtech");
		GlobalStatic.oElement = GlobalStatic.oDriver.findElement(By.name("j_password"));
		GlobalStatic.oElement.sendKeys("afourtech");*/
		
		/*GlobalStatic.oDriver1=new InternetExplorerDriver ();
		GlobalStatic.oDriver.get("https://afourtech.mind-alliance.com/login.html");
		GlobalStatic.oElement = GlobalStatic.oDriver.findElement(By.name("j_username"));
		GlobalStatic.oElement.sendKeys("siddiqui");
		GlobalStatic.oElement = GlobalStatic.oDriver.findElement(By.name("j_password"));
		GlobalStatic.oElement.sendKeys("siddiqui");*/
		/*WebDriver oDriver=new ChromeDriver();
		oDriver.get("www.google.com");
		WebElement oElement=oDriver.findElement(By.name("q"));
		oElement.sendKeys("Naeem");
		oElement.submit();*/
		
		/*GlobalStatic.oDriver=new FirefoxDriver();
		GlobalStatic.oDriver.get("http://afourtech.mind-alliance.com");
		GlobalStatic.oElement = GlobalStatic.oDriver.findElement(By.name("j_username"));
		GlobalStatic.oElement.sendKeys("afourtech");
		GlobalStatic.oElement = GlobalStatic.oDriver.findElement(By.name("j_password"));
		GlobalStatic.oElement.sendKeys("afourtech");
		GlobalStatic.oElement.submit();*/
		
		/*GlobalStatic.oDriver1=new FirefoxDriver();
		GlobalStatic.oDriver1.get("http://afourtech.mind-alliance.com");
		GlobalStatic.oElement1 = GlobalStatic.oDriver1.findElement(By.name("j_username"));
		GlobalStatic.oElement1.sendKeys("siddiqui");
		GlobalStatic.oElement1 = GlobalStatic.oDriver1.findElement(By.name("j_password"));
		GlobalStatic.oElement1.sendKeys("siddiqui");
		GlobalStatic.oElement1.submit();*/
	}
	
}
