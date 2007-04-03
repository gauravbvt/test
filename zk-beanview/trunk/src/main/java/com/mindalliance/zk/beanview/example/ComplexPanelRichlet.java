/**
 * 
 */
package com.mindalliance.zk.beanview.example;

import org.zkoss.zk.ui.GenericRichlet;
import org.zkoss.zk.ui.Page;
import org.zkoss.zul.Window;

/**
 * @author dfeeney
 *
 */
public class ComplexPanelRichlet extends GenericRichlet {

	/* (non-Javadoc)
	 * @see org.zkoss.zk.ui.Richlet#service(org.zkoss.zk.ui.Page)
	 */
	public void service(Page page) {
		page.setTitle("BeanView Complex example");
		Window w = new Window("BeanViewZk " + new java.util.Date().toString(), "normal", false);
		w.setPage(page);

		ComplexPanel panel = new ComplexPanel();
		panel.setParent(w);
	}

}
