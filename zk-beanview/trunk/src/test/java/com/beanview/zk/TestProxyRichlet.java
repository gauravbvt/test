/**
 * 
 */
package com.beanview.zk;

import org.zkoss.zk.ui.GenericRichlet;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.Richlet;

/**
 * @author dfeeney
 *
 */
public class TestProxyRichlet extends GenericRichlet {

	private static Richlet proxied;
	
	/* (non-Javadoc)
	 * @see org.zkoss.zk.ui.Richlet#service(org.zkoss.zk.ui.Page)
	 */
	public void service(Page page) {
		proxied.service(page);
	}
	
	public static Richlet getProxied() {
		return proxied;
	}
	

	public static void setProxied(Richlet proxied) {
		TestProxyRichlet.proxied = proxied;
	}
}
