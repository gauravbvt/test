package com.beanview.zk;

import java.util.ArrayList;
import java.util.Collection;

import org.zkoss.zk.ui.GenericRichlet;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zul.Button;

import com.beanview.annotation.PropertyOptions;
import com.mindalliance.zk.beanview.ZkBeanViewPanel;

public class ArrayListZkTest extends AbstractZkTest<ArrayListZkTest.TestRichlet> 
{
	public ArrayListZkTest() {
		super(new TestRichlet());
	}
	
    static public class TestRichlet extends GenericRichlet {
    	private ZkBeanViewPanel<ArrayListTestPerson> test;
        private Button updateObjectButton;
        private Button updatePanelButton;

		public void service(Page page) {
	
	        test = new ZkBeanViewPanel<ArrayListTestPerson>();
	        test.setPage(page);
	        ArrayListTestPerson testObj = new ArrayListTestPerson(1,
	                "Robert Indigo", "BI");
	        test.setDataObject(testObj);
	        
			updateObjectButton = new Button();
			updateObjectButton.addEventListener("onClick", new EventListener() {
				public boolean isAsap() {
					return true;
				}
				public void onEvent(Event arg0) {
					test.updateObjectFromPanel();
				}
			
			});
			updateObjectButton.setPage(page);
			
			updatePanelButton = new Button();
			updatePanelButton.addEventListener("onClick", new EventListener() {
				public boolean isAsap() {
					return true;
				}
				public void onEvent(Event arg0) {
					test.updatePanelFromObject();
				}
			
			});
			updatePanelButton.setPage(page);
		}
    }
	@SuppressWarnings("unchecked")
    protected void setUp() throws Exception
    {
        super.setUp();
    }
    public void testArrayListTestPerson()
    {


        assertNotNull(richlet.test.getDataObject());

        updateObject();
        updatePanel(); 
    }

    
    private void updateObject(){
        try {
			selenium.click("id=" + richlet.updateObjectButton.getUuid());
			Thread.sleep(500);
		} catch (InterruptedException e) {
			fail();
		}
    }

    private void updatePanel(){
        try {
			selenium.click("id=" + richlet.updatePanelButton.getUuid());
			Thread.sleep(500);
		} catch (InterruptedException e) {
			fail();
		}
    }
    protected static class ArrayListTestPerson
    {

        private int id = 0;

        private String name = "";

        private String initials = "";

        public Collection<String> nicknames = new ArrayList<String>();

        public Collection<String> possibleNicknames()
        {
            Collection<String> nicks = new ArrayList<String>();
            nicks.add("Bobby");
            nicks.add("Robby");
            nicks.add("Bob");
            return nicks;
        }

        @PropertyOptions(options = "possibleNicknames")
        public Collection<String> getNicknames()
        {
            return nicknames;
        }

        public void setNicknames(Collection<String> inNick)
        {
            this.nicknames = inNick;
        }

        public ArrayListTestPerson(int inId, String inName, String inInitials)
        {
            this.id = inId;
            this.name = inName;
            this.initials = inInitials;
        }

        public String getName()
        {
            return name;
        }

        public void setName(String name)
        {
            this.name = name;
        }

        public int getId()
        {
            return id;
        }

        public String getInitials()
        {
            return initials;
        }

        public void setInitials(String initials)
        {
            this.initials = initials;
        }
    }
    
    
}
