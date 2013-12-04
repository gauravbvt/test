package com.mindalliance.automationbvt;

import junit.framework.TestCase;

import org.junit.Test;
/**
 * Testcase ID: MABVT0003_VerifyJaveVersion
 * 	   Summary: Verify the Java version 
 * @author afour
 * 
 */
public class MABVT0003_VerifyJavaVersion extends TestCase {

	@Test
	public void testMABVT0003_VerifyJavaVersion() {
		String javaVersion=System.getProperty("java.version");
		System.out.println("The Java version is :"+javaVersion);
		
		assertEquals("1.7.0_21",javaVersion);
	}

}
