package com.mindalliance.channels.dao;

import com.mindalliance.channels.Scenario;
import com.mindalliance.channels.DuplicateKeyException;
import com.mindalliance.channels.NotFoundException;
import junit.framework.TestCase;

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
        assertEquals( 2, memory.getScenarioCount() );
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
        final int size = memory.getScenarioCount();

        final Scenario s = memory.createScenario();
        s.setName( "Bogus" );

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

        for ( int i = size; i > 0 ; i-- )
            memory.removeScenario( memory.getDefaultScenario() );
        assertEquals( 1, memory.getScenarioCount() );
        // last one not deleted
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

    public void testDefault() {
        assertNotNull( memory.getDefaultScenario() );
    }

    public void testCreateScenario() throws NotFoundException {
        assertEquals( 2, memory.getScenarioCount() );
        Scenario s = memory.createScenario();
        assertEquals( 3, memory.getScenarioCount() );
        assertSame( s, memory.findScenario( s.getId() ) );
    }

}
