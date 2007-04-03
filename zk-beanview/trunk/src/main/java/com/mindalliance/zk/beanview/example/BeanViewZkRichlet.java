package com.mindalliance.zk.beanview.example;

import org.zkoss.zk.ui.GenericRichlet;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zul.Button;
import org.zkoss.zul.Window;

import com.mindalliance.zk.beanview.ZkBeanViewPanel;

/**
 * A very simple BeanView example.  This just creates an editor panel with all of the properties for
 * the Person bean. Then it creates two instances of the Person bean and associates the first with the panel.
 * The 'Update' button will update the current instance with the panel's contents.  The 'Toggle' button will 
 * switch the instance associated with the panel.
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
		final ZkBeanViewPanel<Person> panel;

		final Person person1 = new Person();
		final Person person2 = new Person();
		panel = new ZkBeanViewPanel<Person>();
		panel.setDataObject(person1);
		panel.setError("firstName", "Sample error");

		panel.setParent(w);
		
		Button updateButton = new Button();
		updateButton.setLabel("Update");
		updateButton.addEventListener("onClick", new EventListener() {
			public boolean isAsap() {
				return false;
			}
			public void onEvent(Event arg0) {
				panel.updateObjectFromPanel();
			}
		});
		
		
		Button toggleButton = new Button();
		toggleButton.setLabel("Toggle");
		toggleButton.addEventListener("onClick", new EventListener() {
			public boolean isAsap() {
				return false;
			}
			public void onEvent(Event arg0) {
				if (panel.getDataObject().equals(person1)){
					panel.setDataObject(person2);
				} else {
					panel.setDataObject(person1);
				}
			}
		});
		updateButton.setParent(w);
		toggleButton.setParent(w);
	}

}
