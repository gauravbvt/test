package com.mindalliance.automationbvt;

import junit.framework.TestCase;

import org.junit.Test;
/**
 * Testcase ID: MABVT0002_VerifyOSInstalled
 * 	   Summary: Verify the Operating System name and version
 * @author afour
 * 
 */
public class MABVT0002_VerifyOSInstalled extends TestCase {

	@Test
	public void testVersifyODInstalled() {
		String osName = System.getProperty("os.name");
		System.out.println("The OS is:" + osName);

		String osVersion = System.getProperty("os.version");
		System.out.println("The OS version is:" + osVersion);

		String osArch = System.getProperty("os.arch");
		System.out.println("The OS architecture is: " + osArch);

		assertEquals("Windows 7", osName);
		assertEquals("6.1", osVersion);
		assertEquals("x86", osArch);
	}

}
