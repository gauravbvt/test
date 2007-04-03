/**
 * 
 */
package com.mindalliance.zk.beanview.example;



import org.zkoss.zk.ui.GenericRichlet;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zul.Button;
import org.zkoss.zul.Window;

import com.mindalliance.zk.beanview.ZkBeanViewPanel;

/**
 * A BeanView example illustrating the use of a picker bean to choose 
 * one or more instances of another bean from a filtered list.  The lists are
 * filtered using PropertyOption annotations on the PeoplePicker bean.
 *
 */
public class CollectionZkRichlet extends GenericRichlet {
	String[] values = {};

	Button updateButton = new Button();

	Button togglePersonButton = new Button();

	Button singlePersonPickerButton = new Button();

	Button multiplePersonPickerButton = new Button();

	private static final long serialVersionUID = 1L;

	ZkBeanViewPanel<SinglePersonPicker> panel1 = new ZkBeanViewPanel<SinglePersonPicker>();

	SinglePersonPicker picker1 = new SinglePersonPicker();

	SinglePersonPicker picker2 = new SinglePersonPicker();

	ZkBeanViewPanel<PeoplePicker> panel2 = new ZkBeanViewPanel<PeoplePicker>();

	PeoplePicker peoplePicker1 = new PeoplePicker();

	PeoplePicker peoplePicker2 = new PeoplePicker();

	@SuppressWarnings("unchecked")
	public void setUpBeanViewPanels()
	{
		panel1.setDataObject(picker1);
		panel2.setDataObject(peoplePicker1);
	}

	void initButtons()
	{
		updateButton.setLabel("Update");
		updateButton.addEventListener("onClick", new EventListener() {

			public boolean isAsap() {
				return false;
			}

			public void onEvent(Event arg0) {
				panel2.updateObjectFromPanel();
			}
		});

		togglePersonButton.setLabel("Toggle");
		togglePersonButton.addEventListener("onClick", new EventListener() {
			boolean picker1Selected = true;
			public boolean isAsap() {
				return false;
			}

			public void onEvent(Event arg0) {
				if (picker1Selected)
				{
					panel2.setDataObject(peoplePicker1);
				} else
				{
					panel2.setDataObject(peoplePicker2);
				}
				picker1Selected = !picker1Selected;
				panel2.updatePanelFromObject();
			}
		});

	}
	/* (non-Javadoc)
	 * @see org.zkoss.zk.ui.Richlet#service(org.zkoss.zk.ui.Page)
	 */
	public void service(Page page) {
		page.setTitle("BeanView Collection example");
		Window w = new Window("BeanViewZk " + new java.util.Date().toString(), "normal", false);
		w.setPage(page);
		updateButton = new Button();

		togglePersonButton = new Button();

		singlePersonPickerButton = new Button();

		multiplePersonPickerButton = new Button();

		panel2 = new ZkBeanViewPanel<PeoplePicker>();
		initButtons();
		setUpBeanViewPanels();
		panel2.setParent(w);
		updateButton.setParent(w);
		togglePersonButton.setParent(w);
	}

}
