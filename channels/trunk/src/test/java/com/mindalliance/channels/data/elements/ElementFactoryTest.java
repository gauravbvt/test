// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.data.elements;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;


import com.mindalliance.channels.data.elements.resources.Person;
import com.mindalliance.channels.util.GUIDFactory;
import com.mindalliance.channels.util.GUIDFactoryImpl;


public class ElementFactoryTest {
    
    private ElementFactory factory ;   
    private GUIDFactory guidFactory;
    
    @Before
    public void createFactory() throws Exception {
        factory = new ElementFactory();
        guidFactory = new GUIDFactoryImpl( "bla" );
        factory.setGuidFactory( guidFactory );
    }

    /**
     * Test method for {@link ModelObjectFactory#getGuidFactory()}.
     */
    @Test
    public final void testGetGuidFactory() {
        assertSame( guidFactory, factory.getGuidFactory() );
    }
    @Test
    public final void createElement() {
    	Person person = (Person)factory.newInstance(Person.class);
    	assertNotNull(person);
    }

}
