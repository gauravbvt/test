// Copyright (C) 2010 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.command;

import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.internal.verification.Times;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.After;
import static org.junit.Assert.*;
import com.mindalliance.channels.query.QueryService;
import com.mindalliance.channels.model.ModelObject;
import com.mindalliance.channels.model.NotFoundException;
import com.mindalliance.channels.model.Event;

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
        lockManager = new DefaultLockManager();
        lockManager.setQueryService( queryService );
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
