// Copyright (C) 2006 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.registry;

import com.mindalliance.channels.Agencies;
import com.mindalliance.channels.AgenciesListener;
import com.mindalliance.channels.Agency;
import com.mindalliance.channels.Model;
import com.mindalliance.channels.registry.AgenciesImpl;

import junit.framework.TestCase;

/**
 * Unit tests for Agencies.
 * @author <a href="mailto:denis@mind-alliance.com">denis</a>
 * @version $Revision$
 */
public class AgenciesImplTest extends TestCase {

    private final class TestAgenciesListener implements AgenciesListener {

        private Agency last;
        private boolean added;
        private boolean removed;
        private int called;

        private TestAgenciesListener() {
        }

        public void reset() {
            this.last = null;
            this.added = false;
            this.removed = false;
            this.called = 0;
        }

        public void addedAgency( Agency agency ) {
            this.last = agency;
            this.added = true;
            this.called++;
        }

        public void removedAgency( Agency agency ) {
            this.last = agency;
            this.removed = true;
            this.called++;
        }

        public boolean isAdded() {
            return this.added;
        }

        public Agency getLast() {
            return this.last;
        }

        public boolean isRemoved() {
            return this.removed;
        }

        public int getCalled() {
            return this.called;
        }
    }

    private AgenciesImpl agencies;
    private TestAgenciesListener listener;

    protected Agency createAgency() {
        return new Agency() {

            public Agencies getCollaboratingAgencies() {
                return null;
            }

            public Model getIntegratedModel() {
                return null;
            }

            public Model getLocalModel() {
                return null;
            }

            public String getName() {
                return null;
            }

            public String getShortName() {
                return null;
            }
        };
    }

    /* (non-Javadoc)
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();

        this.agencies = new AgenciesImpl();
        this.listener = new TestAgenciesListener();
    }

    /* (non-Javadoc)
     * @see junit.framework.TestCase#tearDown()
     */
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    /**
     * Test method for {@link AgenciesImpl#addAgency(Agency)}.
     */
    public void testAddAgency_1() {
        this.agencies.addAgenciesListener( this.listener );
        assertEquals( 0, this.agencies.getAgencies().size() );

        Agency a = createAgency();
        this.agencies.addAgency( a );
        assertEquals( 1, this.agencies.getAgencies().size() );
        assertSame( a, this.agencies.getAgencies().iterator().next() );
        assertTrue( this.listener.isAdded() );
        assertSame( a, this.listener.getLast() );

        this.listener.reset();
        this.agencies.addAgency( a );
        assertEquals( 1, this.agencies.getAgencies().size() );
        assertSame( a, this.agencies.getAgencies().iterator().next() );
        assertFalse( this.listener.isAdded() );
        assertNull( this.listener.getLast() );

        Agency b = createAgency();
        this.agencies.addAgency( b );
        assertEquals( 2, this.agencies.getAgencies().size() );
        assertTrue( this.agencies.getAgencies().contains( a ) );
        assertTrue( this.agencies.getAgencies().contains( b ) );
        assertTrue( this.listener.isAdded() );
        assertSame( b, this.listener.getLast() );
    }

    /**
     * Test method for {@link AgenciesImpl#addAgency(Agency)}.
     */
    public void testAddAgency_2() {
        assertEquals( 0, this.agencies.getAgencies().size() );

        Agency a = createAgency();
        this.agencies.addAgency( a );
        assertEquals( 1, this.agencies.getAgencies().size() );
        assertSame( a, this.agencies.getAgencies().iterator().next() );

        this.listener.reset();
        this.agencies.addAgency( a );
        assertEquals( 1, this.agencies.getAgencies().size() );
        assertSame( a, this.agencies.getAgencies().iterator().next() );

        Agency b = createAgency();
        this.agencies.addAgency( b );
        assertEquals( 2, this.agencies.getAgencies().size() );
        assertTrue( this.agencies.getAgencies().contains( a ) );
        assertTrue( this.agencies.getAgencies().contains( b ) );
    }

    /**
     * Test method for {@link AgenciesImpl#removeAgency(Agency)}.
     */
    public void testRemoveAgency() {
        Agency a = createAgency();

        this.agencies.removeAgency( a );
        this.agencies.addAgency( a );
        this.agencies.removeAgency( a );

        assertEquals( 0, this.agencies.getAgencies().size() );
    }

    /**
     * Test method for
     * {@link AgenciesImpl#addAgenciesListener(AgenciesListener)}.
     */
    public void testAddAgenciesListener() {
        this.agencies.addAgenciesListener( this.listener );
        TestAgenciesListener other = new TestAgenciesListener();

        this.agencies.addAgenciesListener( other );

        assertFalse( this.listener.isAdded() );
        assertFalse( other.isAdded() );

        Agency a = createAgency();
        this.agencies.addAgency(  a );

        assertTrue( this.listener.isAdded() );
        assertEquals( 1, this.listener.getCalled() );
        assertTrue( other.isAdded() );
        assertEquals( 1, other.getCalled() );
    }

    /**
     * Test method for
     * {@link AgenciesImpl#removeAgenciesListener(AgenciesListener)}.
     */
    public void testRemoveAgenciesListener() {
        this.agencies.removeAgenciesListener( this.listener );

        this.agencies.addAgenciesListener( this.listener );
        TestAgenciesListener other = new TestAgenciesListener();

        this.agencies.addAgenciesListener( other );

        Agency a = createAgency();
        this.agencies.addAgency(  a );

        assertTrue( this.listener.isAdded() );
        assertTrue( other.isAdded() );
        assertEquals( 1, other.getCalled() );
        assertEquals( 1, this.listener.getCalled() );

        this.listener.reset();
        other.reset();

        this.agencies.removeAgenciesListener( other );
        this.agencies.removeAgency( a );

        assertTrue( this.listener.isRemoved() );
        assertSame( a, this.listener.getLast() );
        assertNull( other.getLast() );
        assertEquals( 0, other.getCalled() );
        assertEquals( 1, this.listener.getCalled() );
    }
}
