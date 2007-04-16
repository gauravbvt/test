package com.beanview.zk;

import org.zkoss.zk.ui.GenericRichlet;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zul.Button;

import com.beanview.base.FieldCheckUtil;
import com.beanview.test.Person;
import com.mindalliance.zk.beanview.ZkBeanViewPanel;

public class ZkPanelTest extends AbstractZkTest<ZkPanelTest.TestRichlet>
{

	public ZkPanelTest() {
		super(new TestRichlet());
	}
	
	protected static class TestRichlet extends GenericRichlet {
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
	
		protected ZkBeanViewPanel<Person> panel;
	    protected Person person;
	    protected Button configButton;
	    protected Button errorButton;

	}
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

        selenium.click("id=" + richlet.configButton.getUuid());
        Thread.sleep(500);

        FieldCheckUtil.checkFields(richlet.panel, richlet.person);
    }

    /*
     * Test method for 'com.beanview.zk.EchoBeanViewPanel.getProperty(String)'
     */
    public void testGetPropertyComponent()
    {
        System.out.println("TEST: Echo Get PropertyComponent");
        assertNotNull(richlet.panel.getPropertyComponent("bankAccountBalance"));
    }

    /*
     * Test method for 'com.beanview.zk.EchoBeanViewPanel.setError(String,
     * String)'
     */
    public void testSetError() throws Exception
    {
        System.out.println("TEST: Echo set error");
        selenium.click("id=" + richlet.errorButton.getUuid());
        Thread.sleep(500);
        assertEquals("Bad", richlet.panel.getError("firstName"));

    }

    
}
