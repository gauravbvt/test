// Copyright (C) 2010 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.core.command;

import com.mindalliance.channels.core.model.Event;
import com.mindalliance.channels.core.model.ModelObject;
import com.mindalliance.channels.core.model.NotFoundException;
import com.mindalliance.channels.core.query.QueryService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.internal.verification.Times;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

/**
 * Remaining tests for coverage.
 */

public class TestDefaultLockManager {

    @Mock
    private QueryService queryService;

    private DefaultLockManager lockManager;

    @Before
    public void setUp() {
        initMocks( this );
        lockManager = new DefaultLockManager( );
    }

    @Test
    public void testRelease() throws LockingException, NotFoundException {
        when( queryService.find( ModelObject.class, 123 ) ).thenReturn( new Event() );

        Lock lock = lockManager.lock( "bob", 123 );
        assertNotNull( lock );
        assertEquals( "bob", lock.getUserName() );
        assertEquals( 123, lock.getId() );
        assertEquals( "Lock: 123,bob," + lock.getDate(), lock.toString() );

        try {
            lockManager.release( "fred", 123 );
            fail();
        } catch ( LockingException e ) {
            // ok
        }

        verify( queryService, new Times( 2 ) ).find( ModelObject.class, 123 );
    }

    @Test
    public void testIsLocked() throws NotFoundException, LockingException {
        when( queryService.find( ModelObject.class, 123 ) ).thenReturn( new Event() );

        assertFalse( lockManager.isLocked( 123 ) );
        assertNull( lockManager.getLockUser( 123 ) );

        lockManager.lock( "bob", 123 );
        assertTrue( lockManager.isLocked( 123 ) );
        assertEquals( "bob", lockManager.getLockUser( 123 ) );
        verify( queryService, new Times( 3 ) ).find( ModelObject.class, 123 );
    }

    @Test
    public void testRequestLock() throws NotFoundException {
        when( queryService.find( ModelObject.class, 123 ) ).thenReturn( new Event() );

        assertTrue( lockManager.requestLock( "bob", 123L ) );
        assertFalse( lockManager.requestLock( "bill", 123L ) );

        assertFalse( lockManager.requestRelease( "bill", 123L ) );
        assertTrue( lockManager.requestRelease( "bob", 123L ) );
        verify( queryService, new Times( 4 ) ).find( ModelObject.class, 123 );
    }

    @Test
    public void testRequestLock2() throws NotFoundException {
        when( queryService.find( ModelObject.class, 123 ) ).thenThrow( new NotFoundException() );

        assertFalse( lockManager.requestLock( "bill", 123L ) );
        verify( queryService, new Times( 1 ) ).find( ModelObject.class, 123 );
    }
}
