package com.mindalliance.channels.dao;

import com.mindalliance.channels.DuplicateKeyException;
import com.mindalliance.channels.NotFoundException;
import com.mindalliance.channels.model.Scenario;
import com.mindalliance.channels.query.DataQueryObjectImpl;
import junit.framework.TestCase;

public class TestMemory extends TestCase {

    private Memory memory ;

    private DataQueryObjectImpl dqo;

    public TestMemory() {
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        memory = new Memory();
        dqo = new DataQueryObjectImpl();
        dqo.setAddingSamples( true );
        dqo.setDao( memory );
        dqo.initialize();
    }

    public void testInitial() {
        assertEquals( 2, memory.getScenarioCount() );
        assertTrue( memory.list( Scenario.class ).iterator().hasNext() );
        try {
            dqo.findScenario( "bla" );
            fail();
        } catch ( NotFoundException ignored ) {
            try {
                dqo.find( Scenario.class, -1L );
                fail();
            } catch ( NotFoundException ignore ) {
            }
        }
    }

    public void testAddDelete() throws DuplicateKeyException, NotFoundException {
        long size = memory.getScenarioCount();

        Scenario s = dqo.createScenario();
        s.setName( "Bogus" );

        assertEquals( size + 1, memory.getScenarioCount() );

        assertSame( s, dqo.findScenario( s.getName() ) );
        assertSame( s, dqo.find( Scenario.class, s.getId() ) );

        memory.remove( s );
        try {
            dqo.findScenario( s.getName() );
            fail();
        } catch ( NotFoundException ignored ) {}
        try {
            memory.find( Scenario.class, s.getId() );
            fail();
        } catch ( NotFoundException ignored ) {}

        for ( long i = size; i > 0 ; i-- )
            memory.remove( dqo.getDefaultScenario() );
        assertEquals( 1, memory.getScenarioCount() );
        // last one not deleted
    }

    public void testAddTwice() throws DuplicateKeyException {
        Scenario a = new Scenario();
        a.setName( "Bogus" );
        memory.add( a );
        try {
            memory.add( a );
            fail();
        } catch ( DuplicateKeyException ignored ) {
            // success
        }
    }

    public void testDefault() {
        assertNotNull( dqo.getDefaultScenario() );
    }

    public void testCreateScenario() throws NotFoundException {
        assertEquals( 2, memory.getScenarioCount() );
        Scenario s = dqo.createScenario();
        assertEquals( 3, memory.getScenarioCount() );
        assertSame( s, memory.find( Scenario.class, s.getId() ) );
    }

}
