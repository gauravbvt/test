package com.beanview.base;

import com.beanview.BeanView;
import com.beanview.test.Person;

import junit.framework.TestCase;

/**
 * @author $Author: wiverson $
 * @version $Revision: 1.1.1.1 $, $Date: 2006/09/19 04:41:57 $
 */
abstract public class PerformanceTestBase extends TestCase
{

    protected Person person1;
    protected Person person2;
    protected Person person3;

    protected BeanView<Person> panel;

    
    /*
     * @see TestCase#setUp()
     */
    @SuppressWarnings("unchecked")
    protected void setUp() throws Exception
    {
        super.setUp();

        person1 = new Person("Person1", "One");
        person2 = new Person("Person2", "Two");
        person3 = new Person("Person3", "Three");
    }

    
    /*
     * Test method for 'com.beanview.swing.SwingBeanViewPanel.configure()'
     */
    public void testConfigure()
    {
        System.out.print("Starting Performance Test Configure...");
        assertNotNull(panel);
        
        long startTime = System.currentTimeMillis();

        for (int i = 1; i < 10000; i++)
        {
            if (i % 3 == 0)
                panel.setDataObject(person1);
            else if (i % 2 == 0)
                panel.setDataObject(person2);
            else
                panel.setDataObject(person3);
        }

        System.out.print("Time elapsed: ");
        System.out.println(System.currentTimeMillis() - startTime);
    }

    abstract public void updateComponent(int newValue);
    
    /*
     * Test method for
     * 'com.beanview.swing.SwingBeanViewPanel.updatePanelFromObject()'
     */
    public void testUpdatePanelFromObject()
    {
        System.out
                .print("Starting Performance Test Update Panel from Object...");
        panel.setDataObject(person1);

        long startTime = System.currentTimeMillis();

        for (int i = 1; i < 10000; i++)
        {
            person1.setBankAccountBalance(person1.getBankAccountBalance() + 1);
            panel.updatePanelFromObject();
        }

        System.out.print("Time elapsed: ");
        System.out.println(System.currentTimeMillis() - startTime);

    }

    /*
     * Test method for
     * 'com.beanview.swing.SwingBeanViewPanel.updateObjectFromPanel()'
     */
    public void testUpdateObjectFromPanel()
    {
        System.out
                .print("Starting Performance Test Update Object from Panel...");
        panel.setDataObject(person1);

        long startTime = System.currentTimeMillis();

        for (int i = 1; i < 10000; i++)
        {
            this.updateComponent(i);            
            panel.updateObjectFromPanel();
        }

        System.out.print("Time elapsed: ");
        System.out.println(System.currentTimeMillis() - startTime);
    }

}
