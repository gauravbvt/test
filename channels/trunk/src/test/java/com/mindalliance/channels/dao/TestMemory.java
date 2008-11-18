package com.mindalliance.channels.dao;

import com.mindalliance.channels.model.Scenario;
import com.mindalliance.channels.DuplicateKeyException;
import junit.framework.TestCase;

import java.util.Iterator;

/**
 * ...
 */
public class TestMemory extends TestCase {

    private Memory memory = null;

    @Override
    protected void setUp() {
        memory = new Memory();
    }

    public void testInitial() {
        assertFalse( memory.scenarios().hasNext() );
        assertNull( memory.findScenario( "bla" ) );
        assertNull( memory.findScenario( 0L ) );
    }

    public void testAddDelete() throws DuplicateKeyException {
        Scenario s = new Scenario();
        memory.addScenario( s );

        assertSame( s, memory.findScenario( s.getName() ) );
        assertSame( s, memory.findScenario( s.getId() ) );
        Iterator<Scenario> iterator = memory.scenarios();
        assertTrue( iterator.hasNext() );
        assertSame( s, iterator.next() );
        assertFalse( iterator.hasNext() );

        memory.removeScenario( s );
        assertNull( memory.findScenario( s.getName() ) );
        assertNull( memory.findScenario( s.getId() ) );
        assertFalse( memory.scenarios().hasNext() );
    }

    public void testSort() throws DuplicateKeyException {
        Scenario a = new Scenario();
        a.setName( "A" );

        Scenario b = new Scenario();
        b.setName( "B" );

        memory.addScenario( b );
        memory.addScenario( a );
        Iterator<Scenario> iterator = memory.scenarios();
        assertSame( a, iterator.next() );
        assertSame( b, iterator.next() );
    }

    public void testAddTwice() throws DuplicateKeyException {
        Scenario a = new Scenario();
        memory.addScenario( a );
        try {
            memory.addScenario( a );
            fail();
        } catch ( DuplicateKeyException ignored ) {
            // success
        }
    }

    public void testAddSameName() throws DuplicateKeyException {
        Scenario a = new Scenario();
        memory.addScenario( a );
        try {
            memory.addScenario( new Scenario() );
            fail();
        } catch ( DuplicateKeyException ignored ) {
            // success
        }
    }
}
