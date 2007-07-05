// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.data.elements;

import java.beans.PropertyVetoException;

import org.junit.Before;
import org.junit.Test;

import com.mindalliance.channels.AbstractSecurityTest;
import com.mindalliance.channels.UserExistsException;
import com.mindalliance.channels.data.elements.resources.Person;
import com.mindalliance.channels.data.reference.Type;
import com.mindalliance.channels.data.reference.Typology.Category;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class ElementFactoryTest extends AbstractSecurityTest {

    private ElementFactory factory;

    @Before
    public void setUp() throws PropertyVetoException, UserExistsException {
        super.setUp();
        factory = new ElementFactory();
        factory.setGuidFactory( guidFactory );
        factory.setTypology(
            system.getLibraryService().getTypologies().iterator().next() );
    }

    /**
     * Test method for {@link ModelObjectFactory#getGuidFactory()}.
     */
    @Test
    public final void testGetGuidFactory() {
        assertNotNull( factory.getGuidFactory() );
    }

    @Test
    public final void createElement() {
        Person person = (Person) factory.newInstance( Person.class, Category.Any );
        assertNotNull( person );
        Type root = person.getTypeSet().getTypology().getRoot();
        assertEquals("", root.getName());
    }

}
