package com.beanview.zk;

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
public class MenuSelectionTest extends AbstractZkTest
{
    /* (non-Javadoc)
	 * @see com.beanview.zk.TestRichletInterface#service(org.zkoss.zk.ui.Page)
	 */
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
	
    private static ZkBeanViewPanel<Person> panel;
    private static Button updateObjectButton;
    private static Button updatePanelButton;
    
    String _favoriteColorProperty = "favoriteColor";
    String _favoriteColorComponent = _favoriteColorProperty + "_entry";

    /*
     * Test to check ZK popup menus
     */
    public void testSelectFieldBase()
    {
        System.out.println("Testing Echo popup menu base");

        Listbox selectField;
        selectField = (Listbox) panel.getPropertyComponent(_favoriteColorProperty);

        assertNotNull(selectField);
        Object selected = selectField.getSelectedItem().getValue();
        assertNotNull(selected);

        assertEquals(FavoriteColor.Violet, selected);
    }

     public void testSelectFieldUpdatePanelFromObject() throws Exception
    {
        System.out.println("Testing Echo popup menu panel from object");
        Person object = panel.getDataObject();
        object.setFavoriteColor(FavoriteColor.Indigo);

        Listbox selectField;
        selectField = (Listbox) panel.getPropertyComponent(_favoriteColorProperty);
        Listitem selected = selectField.getSelectedItem();

        updatePanel();

        selectField = (Listbox) panel.getPropertyComponent(_favoriteColorProperty);
        selected = selectField.getSelectedItem();
        assertNotNull(selected);

        assertEquals(FavoriteColor.Indigo, selected.getValue());
    }
    
    public void testSelectFieldUpdateObjectFromPanel() throws Exception
    {
        System.out.println("Testing Echo popup menu object from panel");

        Listbox selectField;        
        selectField = (Listbox) panel.getPropertyComponent(_favoriteColorProperty);
        Listitem item = (Listitem)selectField.getItems().get(0);

        selenium.select("id=" + selectField.getUuid(), "id=" + item.getUuid());

        updateObject();

        Person object = panel.getDataObject();
        
        assertEquals(FavoriteColor.Red, object.getFavoriteColor());

        object.setFavoriteColor(FavoriteColor.Green);
        updatePanel();
        
        selectField = (Listbox) panel.getPropertyComponent(_favoriteColorProperty);

        assertEquals(FavoriteColor.Green, selectField.getSelectedItem().getValue());
    }
    
    private void updateObject()  throws Exception {
        selenium.click("id=" + updateObjectButton.getUuid());
        Thread.sleep(500);
    }

    private void updatePanel() throws Exception {
        selenium.click("id=" + updatePanelButton.getUuid());
        Thread.sleep(500);
    }
    
    
}


