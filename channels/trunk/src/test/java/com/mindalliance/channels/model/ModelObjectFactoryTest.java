// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.


package com.mindalliance.channels.model;

import com.mindalliance.channels.model.ModelObjectFactory;
import com.mindalliance.channels.util.GUIDFactory;
import com.mindalliance.channels.util.GUIDFactoryImpl;

import junit.framework.TestCase;


/**
 * @author <a href="mailto:denis@mind-alliance.com">denis</a>
 * @version $Revision$
 */
public class ModelObjectFactoryTest extends TestCase {
    
    private ModelObjectFactory factory ;   
    private GUIDFactory guidFactory;
    
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        
        factory = new ModelObjectFactory();
        guidFactory = new GUIDFactoryImpl( "bla" );
    }

    /**
     * Test method for {@link ModelObjectFactory#getGuidFactory()}.
     */
    public final void testGetGuidFactory() {
        assertNull( factory.getGuidFactory() );
        factory.setGuidFactory( guidFactory );
        assertSame( guidFactory, factory.getGuidFactory() );
    }

}
