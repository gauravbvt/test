package com.mindalliance.channels.dao;

import com.mindalliance.channels.Scenario;
import junit.framework.TestCase;

import java.util.Iterator;

public class TestMemory extends TestCase {

    private Memory memory ;

    public TestMemory() {
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        memory = new Memory();
    }

    public void testInitial() {
        assertTrue( memory.scenarios().hasNext() );
        try {
            memory.findScenario( "bla" );
            fail();
        } catch ( NotFoundException ignored ) {
            try {
                memory.findScenario( -1L );
                fail();
            } catch ( NotFoundException ignore ) {
            }
        }
    }

    public void testAddDelete() throws DuplicateKeyException, NotFoundException {
        final Scenario s = new Scenario();
        s.setName( "Bogus" );

        final int size = memory.getScenarioCount();
        memory.addScenario( s );
        assertEquals( size+1, memory.getScenarioCount() );

        assertSame( s, memory.findScenario( s.getName() ) );
        assertSame( s, memory.findScenario( s.getId() ) );

        memory.removeScenario( s );
        try {
            memory.findScenario( s.getName() );
            fail();
        } catch ( NotFoundException ignored ) {}
        try {
            memory.findScenario( s.getId() );
            fail();
        } catch ( NotFoundException ignored ) {}

        // Following should not complain
        memory.removeScenario( s );
    }

    public void testSort() throws DuplicateKeyException {
        final Scenario a = new Scenario();
        a.setName( "A" );

        final Scenario b = new Scenario();
        b.setName( "B" );

        memory.addScenario( b );
        memory.addScenario( a );
        final Iterator<Scenario> iterator = memory.scenarios();
        assertSame( a, iterator.next() );
        assertSame( b, iterator.next() );
    }

    public void testAddTwice() throws DuplicateKeyException {
        final Scenario a = new Scenario();
        a.setName( "Bogus" );
        memory.addScenario( a );
        try {
            memory.addScenario( a );
            fail();
        } catch ( DuplicateKeyException ignored ) {
            // success
        }
    }

    public void testAddSameName() throws DuplicateKeyException {
        final Scenario a = new Scenario();
        memory.addScenario( new Scenario() );
        try {
            memory.addScenario( new Scenario() );
            fail();
        } catch ( DuplicateKeyException ignored ) {
            // success
        }
    }

    public void testDefault() {
        assertNotNull( memory.getDefaultScenario() );
    }
}
