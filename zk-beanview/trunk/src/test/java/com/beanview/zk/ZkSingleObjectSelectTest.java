package com.beanview.zk;


import org.zkoss.zk.ui.GenericRichlet;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zul.Button;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listitem;

import com.beanview.test.SinglePersonPicker;
import com.mindalliance.zk.beanview.ZkBeanViewPanel;

/**
 * @author $Author: wiverson $
 * @version $Revision: 1.1.1.1 $, $Date: 2006/09/19 04:41:57 $
 */

public class ZkSingleObjectSelectTest extends AbstractZkTest<ZkSingleObjectSelectTest.TestRichlet>
{
	public ZkSingleObjectSelectTest() {
		super(new TestRichlet());
	}
	
	private static class TestRichlet extends GenericRichlet {
		protected ZkBeanViewPanel<SinglePersonPicker> bean;
		protected Button updateObjectButton;

		public void service(Page page) {
			bean = new ZkBeanViewPanel<SinglePersonPicker>();
	
			bean.setDataObject(new SinglePersonPicker());
			bean.setPage(page);
			updateObjectButton = new Button();
			updateObjectButton.addEventListener("onClick", new EventListener() {
				public boolean isAsap() {
					return true;
				}
				public void onEvent(Event arg0) {
					bean.updateObjectFromPanel();
				}
			
			});
			updateObjectButton.setPage(page);
		}
	}

	Listbox prop;

	Listbox getProp(String in)
	{
		return (Listbox) richlet.bean.getPropertyComponent(in);
	}
	
	public void testAllPeople() throws Exception
	{
		prop = getProp("allPeople");
		assertNotNull(prop);
		
		selenium.click("id=" + prop.getUuid());
		Listitem item = (Listitem)prop.getItems().get(4);
		selenium.select("id=" + prop.getUuid(), "id=" + item.getUuid());

		updateObject();
		assertEquals(20, prop.getModel().getSize());
	}

	public void testFavoriteLastNameMPeople() throws Exception
	{
		prop = getProp("favoriteLastNameMPeople");
		assertNotNull(prop);
		updateObject();

		assertEquals(4, getCount(prop));
	}

	int getCount(Listbox prop)
	{
		return prop.getModel().getSize();
	}

	public void testPeopleByContext() throws Exception
	{
		prop = getProp("peopleByContext");
		assertNotNull(prop);
		updateObject();
		assertEquals(20, getCount(prop));
	}

	public void testPeopleByObjectMethod() throws Exception
	{
		prop = getProp("peopleByObjectMethod");
		assertNotNull(prop);
		updateObject();
		assertEquals(20, getCount(prop));
	}

	public void testPeopleLetterMByObjectMethod() throws Exception
	{
		prop = getProp("peopleLetterMByObjectMethod");
		assertNotNull(prop);
		updateObject();
		assertEquals(4, getCount(prop));
	}

	public void testPeopleByObjectMehodWithContext() throws Exception
	{
		prop = getProp("peopleByObjectMethodWithContext");
		assertNotNull(prop);
		updateObject();
		assertEquals(20, getCount(prop));
	}
	
    private void updateObject()  throws Exception {
        selenium.click("id=" + richlet.updateObjectButton.getUuid());
        Thread.sleep(500);
    }
}
