// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.data.elements;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

import com.mindalliance.channels.Channels;
import com.mindalliance.channels.data.elements.resources.Person;
import com.mindalliance.channels.data.reference.Type;

public class ElementFactoryTest {

    private ElementFactory factory;

    @Before
    public void createFactory() throws Exception {
        factory = Channels.getElementFactory();
        assertNotNull(factory);
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
        Person person = (Person) factory.newInstance( Person.class );
        assertNotNull( person );
        Type root = person.getTypeSet().getTypology().getRoot();
        assertEquals(root.getName(), person.getClass().getSimpleName());
        assertEquals(root.getName(), root.getTypology().getName());
    }

}
