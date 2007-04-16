package com.beanview.zk;

import org.zkoss.zk.ui.GenericRichlet;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zul.Box;
import org.zkoss.zul.Button;
import org.zkoss.zul.Window;

import com.beanview.BeanView;
import com.beanview.BeanViewGroup;
import com.beanview.test.Person;
import com.mindalliance.zk.beanview.ZkBeanViewPanel;

/**
 * @author $Author: wiverson $
 * @version $Revision: 1.1.1.1 $, $Date: 2006/09/19 04:41:57 $
 */

public class ZkBeanViewGroupTest extends AbstractZkTest<ZkBeanViewGroupTest.TestRichlet>
{
	public ZkBeanViewGroupTest() {
		super(new TestRichlet());
	}
	private static class TestRichlet extends GenericRichlet {

		protected Button updateObjectButton;
		protected Button updatePanelButton;
		protected Button updateErrorButton;
		
	    public void service(Page page)
	    {
	    	Window w = new Window("BeanViewZk " + new java.util.Date().toString(), "normal", false);
			w.setPage(page);
	        beans = new ZkBeanViewPanel[5];
	
	        for (int i = 0; i < beans.length; i++)
	        {
	            beans[i] = new ZkBeanViewPanel<Person>();
	        }

	        configBeans();
	        Box box = new Box();
	        for (int i = 0; i < beans.length; i++)
	        {
	        	box.appendChild(beans[i]);
	        }

	        w.appendChild(box);
			updateObjectButton = new Button();
			updateObjectButton.addEventListener("onClick", new EventListener() {
				public boolean isAsap() {
					return true;
				}
				public void onEvent(Event arg0) {
					bean_group.updateObjectFromPanels();
				}
			
			});
			w.appendChild(updateObjectButton);
			updateObjectButton.setPage(page);
			
			updatePanelButton = new Button();
			updatePanelButton.addEventListener("onClick", new EventListener() {
				public boolean isAsap() {
					return true;
				}
				public void onEvent(Event arg0) {
					bean_group.updatePanelsFromObject();
				}
			
			});
			w.appendChild(updatePanelButton);
			
			updateErrorButton = new Button();
			updateErrorButton.addEventListener("onClick", new EventListener() {
				public boolean isAsap() {
					return true;
				}
				public void onEvent(Event arg0) {

			       bean_group.getBeanView("firstName").setError("firstName", "Error!");
				}
			
			});
			w.appendChild(updateErrorButton);
	    }
	    
	    
	    @SuppressWarnings("unchecked")
	    protected ZkBeanViewPanel<Person> beans[];
	
	    BeanViewGroup<Person> bean_group;
	
	    public void configBeans()
	    {
	        Person newPerson = new Person();
	    	
	        for (int i = 0; i < beans.length; i++)
	        {
	            beans[i].setDataObject(newPerson);
	        }
	
	        beans[0].setSubView(new String[]
	        { "firstName" }, false, false);
	        beans[1].setSubView(new String[]
	        { "lastName" }, false, false);
	        beans[2].setSubView(new String[]
	        { "birthday" }, false, false);
	        beans[3].setSubView(new String[]
	        { "favoriteColor" }, false, false);
	        beans[4].setExcludeProperties(new String[]
	        { "firstName", "lastName", "birthday", "favoriteColor" });
	
	        bean_group = new BeanViewGroup<Person>();
	
	        for (int i = 0; i < beans.length; i++)
	        {
	            bean_group.addBeanView(beans[i]);
	        }
	        

	
	    }
	    protected void updateObject(){
	        try {
				selenium.click("id=" + updateObjectButton.getUuid());
				Thread.sleep(500);
			} catch (InterruptedException e) {
				fail();
			}
	    }

	    protected void updatePanel(){
	        try {
				selenium.click("id=" + updatePanelButton.getUuid());
				Thread.sleep(500);
			} catch (InterruptedException e) {
				fail();
			}
	    }
	    protected void updateError() {
	        try {
				selenium.click("id=" + updateErrorButton.getUuid());
				Thread.sleep(500);
			} catch (InterruptedException e) {
				fail();
			}
	    }
	}
    /*
     * Test method for 'com.beanview.BeanViewGroup.setDataObject(Object)'
     */
    public void testSetDataObject()
    {
        System.out.println("Test 'BeanViewGroup.setDataObject()'");

        Person testObject = new Person();
        testObject.setFirstName("Bob");

        richlet.bean_group.setDataObject(testObject);
        for (BeanView<Person> bean : richlet.beans)
            assertEquals(testObject, bean.getDataObject());
    }

    /*
     * Test method for 'com.beanview.BeanViewGroup.getBeanView(String)'
     */
    public void testGetBeanView()
    {
        System.out.println("Test 'BeanViewGroup.getBeanView(String)'");
        assertNotNull(richlet.bean_group.getBeanView("firstName"));
        assertNull(richlet.bean_group.getBeanView("invalid_property"));
    }

    /*
     * Test method for 'com.beanview.BeanViewGroup.getBeanViews()'
     */
    public void testGetBeanViews()
    {
        System.out.println("Test 'BeanViewGroup.getBeanViews()'");
        assertNotNull(richlet.bean_group.getBeanViews());
        assertEquals(5, richlet.bean_group.getBeanViews().size());
    }

    /*
     * Test method for 'com.beanview.BeanViewGroup.setContext(String, Object)'
     */
    public void testSetContext()
    {
        System.out.println("Test 'BeanViewGroup.setContext(String, Object)'");
        for (BeanView<Person> bean : richlet.beans)
            assertEquals(null, bean.getContext("test"));

        richlet.bean_group.setContext("test", "value");

        for (BeanView<Person> bean : richlet.beans)
            assertEquals("value", bean.getContext("test"));
    }

    /*
     * Test method for 'com.beanview.BeanViewGroup.hasErrors()'
     */
    public void testHasErrors()
    {
        System.out.println("Test 'BeanViewGroup.hasErrors()'");
        for (BeanView<Person> bean : richlet.beans)
            assertFalse(bean.hasErrors());

        assertNotNull(richlet.bean_group.getBeanView("firstName"));
        richlet.updateError();

        assertTrue(richlet.bean_group.hasErrors());
    }

    /*
     * Test method for 'com.beanview.BeanViewGroup.updateObjectFromPanels()'
     */
    public void testUpdateObjectFromPanels()
    {
        System.out.println("Test 'BeanViewGroup.updateObjectFromPanels()'");
        richlet.updateObject();
    }

    /*
     * Test method for 'com.beanview.BeanViewGroup.updatePanelsFromObject()'
     */
    public void testUpdatePanelsFromObject()
    {
        System.out.println("Test 'BeanViewGroup.updatePanelsFromObject()'");
        richlet.updatePanel();
    }
    

}
