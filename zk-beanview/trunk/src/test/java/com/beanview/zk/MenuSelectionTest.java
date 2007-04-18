package com.beanview.zk;

import org.zkoss.zk.ui.GenericRichlet;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zul.Button;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listitem;

import com.beanview.test.FavoriteColor;
import com.beanview.test.Person;
import com.mindalliance.zk.beanview.ZkBeanViewPanel;

/**
 * Tests for ZK BeanView drop down menus
 */
public class MenuSelectionTest extends AbstractZkTest<MenuSelectionTest.TestRichlet>
{
	
	public MenuSelectionTest() {
		super(new TestRichlet());
	}

	public static class TestRichlet extends GenericRichlet {
	    protected ZkBeanViewPanel<Person> panel;
	    protected Button updateObjectButton;
	    protected Button updatePanelButton;
		public void service(Page page) {
	        panel = new ZkBeanViewPanel<Person>();
	        Person person = new Person();
	        person.setFavoriteColor(FavoriteColor.Violet);
	        panel.setDataObject(person);
			panel.setPage(page);
			updateObjectButton = new Button();
			updateObjectButton.addEventListener("onClick", new EventListener() {
				public boolean isAsap() {
					return true;
				}
				public void onEvent(Event arg0) {
					panel.updateObjectFromPanel();
				}
			
			});
			updateObjectButton.setPage(page);
			
			updatePanelButton = new Button();
			updatePanelButton.addEventListener("onClick", new EventListener() {
				public boolean isAsap() {
					return true;
				}
				public void onEvent(Event arg0) {
					panel.updatePanelFromObject();
				}
			
			});
			updatePanelButton.setPage(page);
		}
	}

    
    String _favoriteColorProperty = "favoriteColor";

    /*
     * Test to check ZK popup menus
     */
    public void testSelectFieldBase()
    {
        System.out.println("Testing ZK popup menu base");

        Listbox selectField;
        selectField = (Listbox) richlet.panel.getPropertyComponent(_favoriteColorProperty);

        assertNotNull(selectField);
        Object selected = selectField.getSelectedItem().getValue();
        assertNotNull(selected);

        assertEquals(FavoriteColor.Violet, selected);
    }

     public void testSelectFieldUpdatePanelFromObject() 
    {
        System.out.println("Testing ZK popup menu panel from object");
        Person object = (Person)richlet.panel.getDataObject();
        object.setFavoriteColor(FavoriteColor.Indigo);

        Listbox selectField;
        selectField = (Listbox) richlet.panel.getPropertyComponent(_favoriteColorProperty);
        Listitem selected = selectField.getSelectedItem();

        updatePanel();

        selectField = (Listbox) richlet.panel.getPropertyComponent(_favoriteColorProperty);
        selected = selectField.getSelectedItem();
        assertNotNull(selected);

        assertEquals(FavoriteColor.Indigo, selected.getValue());
    }
    
    public void testSelectFieldUpdateObjectFromPanel()
    {
        System.out.println("Testing ZK popup menu object from panel");

        Listbox selectField;        
        selectField = (Listbox) richlet.panel.getPropertyComponent(_favoriteColorProperty);
        Listitem item = (Listitem)selectField.getItems().get(0);

        selenium.select("id=" + selectField.getUuid(), "id=" + item.getUuid());

        updateObject();

        Person object = (Person)richlet.panel.getDataObject();
        
        assertEquals(FavoriteColor.Red, object.getFavoriteColor());

        object.setFavoriteColor(FavoriteColor.Green);
        updatePanel();
        
        selectField = (Listbox) richlet.panel.getPropertyComponent(_favoriteColorProperty);

        assertEquals(FavoriteColor.Green, selectField.getSelectedItem().getValue());
    }
    
    private void updateObject() {
        try {
			selenium.click("id=" + richlet.updateObjectButton.getUuid());
			Thread.sleep(500);
		} catch (Exception e) {
			fail();
		}
    }

    private void updatePanel() {
        try {
			selenium.click("id=" + richlet.updatePanelButton.getUuid());
			Thread.sleep(500);
		} catch (Exception e) {
			fail();
		}
    }
    
    
}


