package com.mindalliance.automationbvt;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;

import junit.framework.TestCase;

import org.junit.Test;

public class MABVT0005_VerifyBrowsersInstalled extends TestCase {

	@Test
	public void testBrowserInstalled() throws InterruptedException, IOException {
		
		// Initialize Firefox browser
		if (Desktop.isDesktopSupported()) {
			Desktop dt = Desktop.getDesktop();
			if (dt.isSupported(Desktop.Action.BROWSE)) {
				File f = new File("C:\\Program Files\\Mozilla Firefox\\firefox.exe");
				if (f.exists()) {
					try {
						Process p = Runtime.getRuntime().exec("firefox.exe");
						Thread.sleep(2000);
						p.destroy();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} else {
					System.out.println("Firefox browser has not been installed!!!");
				}

			}

		}

		// Open and close Internet Explorer if installed on the system
		if (Desktop.isDesktopSupported()) {
			Desktop dt = Desktop.getDesktop();
			if (dt.isSupported(Desktop.Action.BROWSE)) {
				File f = new File("C:\\Program Files\\Internet Explorer\\iexplore.exe");
				if (f.exists()) {
					try {
						// dt.browse(f.toURI());
						Process p = Runtime.getRuntime().exec("iexplore.exe");
						Thread.sleep(3000);
						p.destroy();

					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} else {
					System.out.println("Internet Explorer browser has not been installed!!!");
				}

			}

		}
		
		// Open and close Chrome browser
		if (Desktop.isDesktopSupported()) {
			Desktop dt = Desktop.getDesktop();
			if (dt.isSupported(Desktop.Action.BROWSE)) {
				File f = new File("C:\\Users\\AFour_traini_1\\AppData\\Local\\Google\\Chrome\\Application\\Chrome.exe");
				if (f.exists()) {
					try {
						// dt.browse(f.toURI());
						Process p = Runtime.getRuntime().exec("Chrome.exe");
						Thread.sleep(3000);
						p.destroy();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} else {
					System.out.println("Chrome browser has not been installed!!!");
				}
			}

		}

	}

}
