package com.beanview.base;

import java.util.Collection;
import java.util.HashMap;

import com.beanview.test.Person;
import com.beanview.util.CollectionCreator;

import junit.framework.TestCase;

/**
 * This tests a utility class for the creation of a collection.
 * 
 * @author $Author: wiverson $
 * @version $Revision: 1.1.1.1 $, $Date: 2006/09/19 04:41:57 $
 */
public class CollectionCreatorTest extends TestCase
{
    /*
     * Test method for
     * 'com.beanview.util.CollectionCreator.newEmptyCollection(Collection)'
     */
    public void testNewEmptyCollection()
    {
        HashMap<String, Person> testTarget = new HashMap<String, Person>();
        Person person1 = new Person();
        testTarget.put("1", person1);

        Collection temp = testTarget.keySet();

        Collection result = CollectionCreator.newEmptyCollection(temp);
        assertNotNull(result);
        assertEquals(0, result.size());
    }

}
