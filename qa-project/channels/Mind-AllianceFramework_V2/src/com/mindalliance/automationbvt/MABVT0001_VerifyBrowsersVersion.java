package com.mindalliance.automationbvt;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.junit.Test;

public class MABVT0001_VerifyBrowsersVersion extends TestCase{

	@Test
	public void testVersions() throws Exception {
//		System.out.println(getIEVersion());
		System.out.println(getFirefoxVersion());
	}
	
	/**
	 * This method identifies IE version
	 * @return
	 * @throws IOException
	 */
	public static String getIEVersion() throws IOException {
		  ArrayList<String> output = new ArrayList<String>();
		  Process p = Runtime.getRuntime().exec("reg query \"HKLM\\Software\\Microsoft\\Internet Explorer\" /v Version");
		  BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()),8*1024);
		  String s = null;
		  while ((s = stdInput.readLine()) != null){
		   output.add(s);
		  }
		  String internet_explorer_value = (output.get(2));
		  String version = internet_explorer_value.trim().split("   ")[2];
		  System.out.print("Actual Internet Explorer Version is "+version+ " and Expected is ");
		  Assert.assertEquals(" 9.11.9600.16518", version);
		  return version;
		 }
	
	/**
	 * This method will identify Firefox version
	 * @return version
	 * @throws IOException
	 */
	public static String getFirefoxVersion() throws IOException {
		  ArrayList<String> output = new ArrayList<String>();
		  Process p = Runtime.getRuntime().exec("reg query \"HKLM\\Software\\Mozilla\\Mozilla Firefox\" /v CurrentVersion");
		  BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));
		  String s = null;
		  while ((s = stdInput.readLine()) != null){
		   output.add(s);
		  }
		  String internet_explorer_value = (output.get(2));
		  String version = internet_explorer_value.trim().split("   ")[2];
		  System.out.print("Actual Firefox Version is"+version+ "and Expected is ");
		  Assert.assertEquals(" 20.0.1 (en-US)", version);
		  return version;
		 }
}