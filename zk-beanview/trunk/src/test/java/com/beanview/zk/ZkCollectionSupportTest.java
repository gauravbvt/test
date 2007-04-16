package com.beanview.zk;

import org.zkoss.zk.ui.GenericRichlet;
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

public class ZkCollectionSupportTest extends AbstractZkTest<ZkCollectionSupportTest.TestRichlet>
{
	public ZkCollectionSupportTest() {
		super(new TestRichlet());
	}
	private static class TestRichlet extends GenericRichlet {
		private static ZkBeanViewPanel<PeoplePicker> bean;
	    private static Button updateObjectButton;
	
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
	    protected void updateObject() {
	        try {
				selenium.click("id=" + updateObjectButton.getUuid());
				Thread.sleep(500);
			} catch (InterruptedException e) {
				fail();
			}
	    }
	}
	
	SettableZkList prop;

	SettableZkList getProp(String in)
	{
		return (SettableZkList) richlet.bean.getPropertyComponent(in);
	}

	int getCount(SettableZkList in)
	{
		return prop.getModel().getSize();
	}

	public void testAllPeople()
	{
		prop = getProp("allPeople");
		assertNotNull(prop);

		richlet.updateObject();
		assertEquals(20, getCount(prop));
	}

	public void testFavoriteLastNameMPeople()
	{
		prop = getProp("favoriteLastNameMPeople");
		assertNotNull(prop);

		richlet.updateObject();
		assertEquals(4, getCount(prop));
	}

	public void testPeopleByContext()
	{
		prop = getProp("peopleByContext");
		assertNotNull(prop);

		richlet.updateObject();
		assertEquals(20, getCount(prop));
	}

	public void testPeopleByObjectMethod()
	{
		prop = getProp("peopleByObjectMethod");
		assertNotNull(prop);

		richlet.updateObject();
		assertEquals(20, getCount(prop));
	}

	public void testPeopleLetterMByObjectMethod()
	{
		prop = getProp("peopleLetterMByObjectMethod");
		assertNotNull(prop);

		richlet.updateObject();
		assertEquals(4, getCount(prop));
	}

	public void testPeopleByObjectMehodWithContext()
	{
		prop = getProp("peopleByObjectMethodWithContext");
		assertNotNull(prop);

		richlet.updateObject();
		assertEquals(20, getCount(prop));
	}
	


}
