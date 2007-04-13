package com.beanview.zk;

import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zul.Button;

import com.beanview.test.PeoplePicker;
import com.mindalliance.zk.beanview.ZkBeanViewPanel;
import com.mindalliance.zk.component.SettableZkList;

/**
 * @author $Author: wiverson $
 * @version $Revision: 1.1.1.1 $, $Date: 2006/09/19 04:41:57 $
 */

public class ZkCollectionSupportTest extends AbstractZkTest
{
	private static ZkBeanViewPanel<PeoplePicker> bean;
    private static Button updateObjectButton;
    
	/* (non-Javadoc)
	 * @see com.beanview.zk.AbstractZkTest#service(org.zkoss.zk.ui.Page)
	 */
	@Override
	public void service(Page page) {
		bean = new ZkBeanViewPanel<PeoplePicker>();

		bean.setDataObject(new PeoplePicker());
		updateObjectButton = new Button();
		updateObjectButton.addEventListener("onClick", new EventListener() {
			public boolean isAsap() {
				return true;
			}
			public void onEvent(Event arg0) {
				updateObject();
			}
		
		});
		updateObjectButton.setPage(page);
		
	}

	SettableZkList prop;

	SettableZkList getProp(String in)
	{
		return (SettableZkList) bean.getPropertyComponent(in);
	}

	int getCount(SettableZkList in)
	{
		return prop.getModel().getSize();
	}

	public void testAllPeople()
	{
		prop = getProp("allPeople");
		assertNotNull(prop);

		updateObject();
		assertEquals(20, getCount(prop));
	}

	public void testFavoriteLastNameMPeople()
	{
		prop = getProp("favoriteLastNameMPeople");
		assertNotNull(prop);

		updateObject();
		assertEquals(4, getCount(prop));
	}

	public void testPeopleByContext()
	{
		prop = getProp("peopleByContext");
		assertNotNull(prop);

		updateObject();
		assertEquals(20, getCount(prop));
	}

	public void testPeopleByObjectMethod()
	{
		prop = getProp("peopleByObjectMethod");
		assertNotNull(prop);

		updateObject();
		assertEquals(20, getCount(prop));
	}

	public void testPeopleLetterMByObjectMethod()
	{
		prop = getProp("peopleLetterMByObjectMethod");
		assertNotNull(prop);

		updateObject();
		assertEquals(4, getCount(prop));
	}

	public void testPeopleByObjectMehodWithContext()
	{
		prop = getProp("peopleByObjectMethodWithContext");
		assertNotNull(prop);

		updateObject();
		assertEquals(20, getCount(prop));
	}
	
    private void updateObject() {
        try {
			selenium.click("id=" + updateObjectButton.getUuid());
			Thread.sleep(500);
		} catch (InterruptedException e) {
			fail();
		}
    }

}
