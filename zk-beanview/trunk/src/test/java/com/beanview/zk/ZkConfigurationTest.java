package com.beanview.zk;



import org.zkoss.zk.ui.AbstractComponent;
import org.zkoss.zk.ui.Page;
import org.zkoss.zul.Textbox;

import com.beanview.test.ConfigurationTestObject;
import com.mindalliance.zk.beanview.ZkBeanViewPanel;


public class ZkConfigurationTest extends AbstractZkTest
{
	
	private static ZkBeanViewPanel bean;
	
	/* (non-Javadoc)
	 * @see com.beanview.zk.AbstractZkTest#service(org.zkoss.zk.ui.Page)
	 */
	@Override
	public void service(Page page) {
		bean = new ZkBeanViewPanel();
		ConfigurationTestObject temp = new ConfigurationTestObject();
		bean.setDataObject(temp);
	}

	/*
	 * @see TestCase#setUp()
	 */
	@SuppressWarnings("unchecked")
	protected void setUp() throws Exception
	{
		super.setUp();


	}

	/**
	 * The components are managed inside a Grid, this method is used to simplify
	 * this.
	 */
	AbstractComponent[] getComponents()
	{
		return (AbstractComponent[])((ZkBeanViewPanel) bean).getChildren().toArray(new AbstractComponent[0]);
	}

	/*public void testFirstNameLabel()
	{
		System.out.println("TEST: testFirstNameLabel");
		AbstractComponent[] components = getComponents();
		assertNotNull(components);

		boolean found = false;
		for (AbstractComponent component : components)
		{
			if (component instanceof Label) {
				if("Familiar Name".equals(((Label) component).getValue()))
					found = true;
			}
		}
		assertTrue(found);
		assertFalse(((Textbox) bean.getPropertyComponent("firstName"))
				.isDisabled());

	}

	public void testLastNameLabel()
	{
		if (!bean.getClass().equals(ZkBeanViewPanel.class))
			return;
		System.out.println("TEST: testLastNameLabel");
		Component[] components = this.getComponents();
		assertNotNull(components);

		boolean found = false;
		for (Component component : components)
		{
			if (component instanceof Label)
			{
				if("Last Name".equals(((Label) component).getValue()))
					found = true;
			}
		}
		assertTrue(found);
		assertFalse(((Textbox) bean.getPropertyComponent("lastName"))
				.isDisabled());
	}
*/
	public void testEditable()
	{
        System.out.println("TEST: testEditable");
        assertTrue(((Textbox) bean
                .getPropertyComponent("dontTouchThisField")).isDisabled());
	}

	public void testReadOnly()
	{
            System.out.println("TEST: testReadOnly");
            assertTrue(((Textbox) bean.getPropertyComponent("readOnly"))
                    .isDisabled());
	}

	
	public void testIgnore()
	{
		System.out.println("TEST: testIgnore");
		assertNull(bean.getPropertyComponent("ignoreThisField"));
	}
}
