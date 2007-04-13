package com.beanview.zk;

import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zul.Button;

import junit.framework.TestCase;

import com.beanview.base.FieldCheckUtil;
import com.beanview.test.FavoriteColor;
import com.beanview.test.Person;
import com.mindalliance.zk.beanview.ZkBeanViewPanel;

public class ZkPanelTest extends AbstractZkTest
{

    /* (non-Javadoc)
	 * @see com.beanview.zk.AbstractZkTest#service(org.zkoss.zk.ui.Page)
	 */
	@Override
	public void service(Page page) {
        panel = new ZkBeanViewPanel<Person>();
        person = new Person();
        panel.setDataObject(person);
		panel.setPage(page);
		configButton = new Button();
		configButton.addEventListener("onClick", new EventListener() {
			public boolean isAsap() {
				return true;
			}
			public void onEvent(Event arg0) {
				panel.configure();
			}
		
		});
		configButton.setPage(page);
		
		errorButton = new Button();
		errorButton.addEventListener("onClick", new EventListener() {
			public boolean isAsap() {
				return true;
			}
			public void onEvent(Event arg0) {
		        String error = "Bad";
		        panel.setError("firstName", error);
			}
		
		});
		errorButton.setPage(page);
	}

	private static ZkBeanViewPanel<Person> panel;
    private static Person person;
    private static Button configButton;
    private static Button errorButton;

    /*
     * @see TestCase#setUp()
     */
    protected void setUp() throws Exception
    {
        super.setUp();
    }

    /*
     * Test method for 'com.beanview.zk.EchoBeanViewPanel.configure()'
     */
    public void testConfigure() throws Exception
    {
        System.out.println("TEST: Zk Configure");

        selenium.click("id=" + configButton.getUuid());
        Thread.sleep(500);

        FieldCheckUtil.checkFields(panel, person);
    }

    /*
     * Test method for 'com.beanview.zk.EchoBeanViewPanel.getProperty(String)'
     */
    public void testGetPropertyComponent()
    {
        System.out.println("TEST: Echo Get PropertyComponent");
        assertNotNull(panel.getPropertyComponent("bankAccountBalance"));
    }

    /*
     * Test method for 'com.beanview.zk.EchoBeanViewPanel.setError(String,
     * String)'
     */
    public void testSetError() throws Exception
    {
        System.out.println("TEST: Echo set error");
        selenium.click("id=" + errorButton.getUuid());
        Thread.sleep(500);
        assertEquals("Bad", panel.getError("firstName"));

    }

    
}
