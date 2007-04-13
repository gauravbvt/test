package com.beanview.zk;

import junit.framework.TestCase;

import org.zkoss.zul.Textbox;

import com.beanview.test.Person;
import com.mindalliance.zk.beanview.ZkBeanViewPanel;

/**
 * @author $Author: wiverson $
 * @version $Revision: 1.1.1.1 $, $Date: 2006/09/19 04:41:57 $
 */

public class ZkPerformanceTest extends TestCase
{

    Person person1;
    Person person2;
    Person person3;

    ZkBeanViewPanel<Person> panel;

    /*
     * @see TestCase#setUp()
     */
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
        panel = new ZkBeanViewPanel<Person>();

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

    /*
     * Test method for
     * 'com.beanview.swing.SwingBeanViewPanel.updatePanelFromObject()'
     */
    public void testUpdatePanelFromObject()
    {
        System.out
                .print("Starting Performance Test Update Panel from Object...");
        panel = new ZkBeanViewPanel<Person>();
        panel.setDataObject(person1);

        long startTime = System.currentTimeMillis();

        for (int i = 1; i < 10000; i++)
        {
            Textbox property = (Textbox) panel
                    .getPropertyComponent("bankAccountBalance");
            property.setText(i + "");
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
        panel = new ZkBeanViewPanel<Person>();
        panel.setDataObject(person1);

        long startTime = System.currentTimeMillis();

        for (int i = 1; i < 10000; i++)
        {
            Textbox property = (Textbox) panel
                    .getPropertyComponent("bankAccountBalance");
            property.setText(i + "");
            panel.updateObjectFromPanel();
        }

        System.out.print("Time elapsed: ");
        System.out.println(System.currentTimeMillis() - startTime);
    }

}
