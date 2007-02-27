// Copyright (C) 2006 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.util;

import com.mindalliance.channels.util.GUID;
import com.mindalliance.channels.util.GUIDFactoryImpl;

import junit.framework.TestCase;

/**
 * @author <a href="mailto:denis@mind-alliance.com">denis</a>
 * @version $Revision$
 */
public class GUIDFactoryImplTest extends TestCase {

    /**
     * Test method for {@link GUIDFactoryImpl#newGuid()}
     */
    public void testNewGuid_1() {
        GUIDFactoryImpl factory = new GUIDFactoryImpl( "bla" );

        GUID a = factory.newGuid();
        GUID b = factory.newGuid();

        assertNotSame( a, b );
        assertNotSame( a.hashCode(), b.hashCode() );
        assertFalse( a.toString().equals( b.toString() ) );
        assertFalse( a == b );
        assertFalse( a.equals( b ) );
        assertFalse( b.equals( a ) );

        assertFalse( b.equals( null ) );
        assertTrue( a.equals( a ) );
    }

    public void testNewGuid_2() {
        GUIDFactoryImpl factory = new GUIDFactoryImpl();
        try {
            factory.newGuid();
            fail( "Able to create a GUID without server id" );
        } catch ( IllegalStateException e ) {
            // OK
        }

    }

}
