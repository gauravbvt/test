/**
 * 
 */
package com.mindalliance.zk.beanview.example;

import org.zkoss.zk.ui.GenericRichlet;
import org.zkoss.zk.ui.Page;
import org.zkoss.zul.Window;

import com.beanview.echo.EchoBeanViewPanel;
import com.mindalliance.zk.beanview.ZkBeanViewPanel;

/**
 * @author dfeeney
 *
 */
public class BeanViewZkRichlet extends GenericRichlet {

	/* (non-Javadoc)
	 * @see org.zkoss.zk.ui.Richlet#service(org.zkoss.zk.ui.Page)
	 */
	public void service(Page page) {
		page.setTitle("BeanView example");
		Window w = new Window("BeanViewZk " + new java.util.Date().toString(), "normal", false);
		w.setPage(page);
		ZkBeanViewPanel<Person> panel;
		Person person;

		person = new Person();
		panel = new ZkBeanViewPanel<Person>();
		panel.setDataObject(person);
		panel.setError("firstName", "Sample error");

		panel.setParent(w);
	}

}
