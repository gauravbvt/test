package com.beanview.base;

import com.beanview.BeanView;
import com.beanview.BeanViewGroup;
import com.beanview.test.Person;

import junit.framework.TestCase;

public abstract class BeanViewGroupTestBase extends TestCase
{
    @Override
    abstract protected void setUp() throws Exception;

    @SuppressWarnings("unchecked")
    protected BeanView<Person> beans[];

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
        beans[0].setSubView(new String[]
        { "lastName" }, false, false);
        beans[0].setSubView(new String[]
        { "birthday" }, false, false);
        beans[0].setSubView(new String[]
        { "favoriteColor" }, false, false);
        beans[0].setExcludeProperties(new String[]
        { "firstName", "lastName", "birthday", "favoriteColor" });

        bean_group = new BeanViewGroup<Person>();

        for (int i = 0; i < beans.length; i++)
        {
            bean_group.addBeanView(beans[i]);
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

        bean_group.setDataObject(testObject);
        for (BeanView<Person> bean : beans)
            assertEquals(testObject, bean.getDataObject());
    }

    /*
     * Test method for 'com.beanview.BeanViewGroup.getBeanView(String)'
     */
    public void testGetBeanView()
    {
        System.out.println("Test 'BeanViewGroup.getBeanView(String)'");
        assertNotNull(bean_group.getBeanView("firstName"));
        assertNull(bean_group.getBeanView("invalid_property"));
    }

    /*
     * Test method for 'com.beanview.BeanViewGroup.getBeanViews()'
     */
    public void testGetBeanViews()
    {
        System.out.println("Test 'BeanViewGroup.getBeanViews()'");
        assertNotNull(bean_group.getBeanViews());
        assertEquals(5, bean_group.getBeanViews().size());
    }

    /*
     * Test method for 'com.beanview.BeanViewGroup.setContext(String, Object)'
     */
    public void testSetContext()
    {
        System.out.println("Test 'BeanViewGroup.setContext(String, Object)'");
        for (BeanView<Person> bean : beans)
            assertEquals(null, bean.getContext("test"));

        bean_group.setContext("test", "value");

        for (BeanView<Person> bean : beans)
            assertEquals("value", bean.getContext("test"));
    }

    /*
     * Test method for 'com.beanview.BeanViewGroup.hasErrors()'
     */
    public void testHasErrors()
    {
        System.out.println("Test 'BeanViewGroup.hasErrors()'");
        for (BeanView<Person> bean : beans)
            assertFalse(bean.hasErrors());

        assertNotNull(bean_group.getBeanView("firstName"));
        bean_group.getBeanView("firstName").setError("firstName", "Error!");

        assertTrue(bean_group.hasErrors());
    }

    /*
     * Test method for 'com.beanview.BeanViewGroup.updateObjectFromPanels()'
     */
    public void testUpdateObjectFromPanels()
    {
        System.out.println("Test 'BeanViewGroup.updateObjectFromPanels()'");
        bean_group.updateObjectFromPanels();
    }

    /*
     * Test method for 'com.beanview.BeanViewGroup.updatePanelsFromObject()'
     */
    public void testUpdatePanelsFromObject()
    {
        System.out.println("Test 'BeanViewGroup.updatePanelsFromObject()'");
        bean_group.updatePanelsFromObject();
    }
}
