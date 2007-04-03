/**
 * 
 */
package com.mindalliance.zk.beanview.example;



import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zul.Box;
import org.zkoss.zul.Button;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Row;
import org.zkoss.zul.Rows;

import com.beanview.BeanViewGroup;
import com.mindalliance.zk.beanview.ZkBeanViewPanel;

/**
 * An example ZK BeanView panel that illustrates the use of BeanViewGroups for
 * associating multiple ZkBeanViewPanel's with a single bean instance.  This is 
 * handy for customizing the layout of a bean editor.  Each ZkBeanViewPanel is constrained
 * to edit a subset of the properties of the bean to be edited.
 *
 */
public class ComplexPanel extends Box {

	String[] values = {};

	Button updateButton = new Button();

	Button togglePersonButton = new Button();

	private static final long serialVersionUID = 1L;

	BeanViewGroup<HouseListing> beanviewGroup = new BeanViewGroup<HouseListing>();

	// "addressLine1", "addressLine2"
	ZkBeanViewPanel<HouseListing> addressPanel = new ZkBeanViewPanel<HouseListing>();

	// "agentId"
	ZkBeanViewPanel<HouseListing> agentIdPanel = new ZkBeanViewPanel<HouseListing>();

	// "market", "datePlacedOnMarket", "recentlyListed"
	ZkBeanViewPanel<HouseListing> marketInfoPanel = new ZkBeanViewPanel<HouseListing>();

	// "briefDescription", "houseType"
	ZkBeanViewPanel<HouseListing> descriptiveInfoPanel = new ZkBeanViewPanel<HouseListing>();

	// "offerPending"
	ZkBeanViewPanel<HouseListing> offerPendingPanel = new ZkBeanViewPanel<HouseListing>();

	// "referenceNumber"
	ZkBeanViewPanel<HouseListing> referenceNumberPanel = new  ZkBeanViewPanel<HouseListing>();

	HouseListing listing1 = new HouseListing();

	HouseListing listing2 = new HouseListing();

	public ComplexPanel() {
		super();
		setUpBeanViewPanels();
		initButtons();
		Grid grid = new Grid();
		Rows rows = new Rows();
		Row row = new Row();
		row.appendChild(addressPanel);
		row.appendChild(agentIdPanel);
		rows.appendChild(row);
		//this.appendChild(addressPanel);
		//this.appendChild(agentIdPanel);
		row = new Row();
		row.appendChild(marketInfoPanel);
		row.appendChild(descriptiveInfoPanel);
		rows.appendChild(row);
		row = new Row();
		row.appendChild(offerPendingPanel);
		row.appendChild(referenceNumberPanel);
		rows.appendChild(row);
		grid.appendChild(rows);
		this.appendChild(grid);
		
		this.appendChild(updateButton);
		this.appendChild(togglePersonButton);

	}

	@SuppressWarnings("unchecked")
	public void setUpBeanViewPanels()
	{
		addressPanel.setSubView(new String[]
		{ "addressLine1", "addressLine2" }, false, false);

		agentIdPanel.setSubView(new String[]
		{ "agentId" }, false, false);

		marketInfoPanel.setSubView(new String[]
		{ "market", "datePlacedOnMarket", "recentlyListed" }, false, false);

		descriptiveInfoPanel.setSubView(new String[]
		{ "briefDescription", "houseType" }, false, false);

		offerPendingPanel.setSubView(new String[]
		{ "offerPending" }, false, false);

		referenceNumberPanel.setSubView(new String[]
		{ "referenceNumber" }, false, false);

		beanviewGroup.addBeanView(addressPanel);
		beanviewGroup.addBeanView(agentIdPanel);
		beanviewGroup.addBeanView(marketInfoPanel);
		beanviewGroup.addBeanView(descriptiveInfoPanel);
		beanviewGroup.addBeanView(offerPendingPanel);
		beanviewGroup.addBeanView(referenceNumberPanel);
		beanviewGroup.setDataObject(listing1);

//		Textbox agentIdEntry = (Textbox) agentIdPanel.getFellow("agentId.entry");
//		if (agentIdEntry != null)
//			agentIdEntry.setRows(30);
	}

	void initButtons()
	{
		updateButton.setLabel("Update");
		updateButton.addEventListener("onClick", new EventListener()
		{
			public boolean isAsap() {
				return false;
			}
			public void onEvent(Event arg0) {
				beanviewGroup.updateObjectFromPanels();
			}
		});

		togglePersonButton.setLabel("Toggle");
		togglePersonButton.addEventListener("onClick", new EventListener()
		{
			public boolean isAsap() {
				return false;
			}
			boolean houseListingSelected = true;

			public void onEvent(Event arg0) {
				if (houseListingSelected)
				{
					beanviewGroup.setDataObject(listing2);
				} else
				{
					beanviewGroup.setDataObject(listing1);
				}
				houseListingSelected = !houseListingSelected;
				beanviewGroup.updatePanelsFromObject();
			}
		});

	}
}
