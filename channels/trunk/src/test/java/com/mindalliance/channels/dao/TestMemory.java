package com.mindalliance.channels.dao;

import com.mindalliance.channels.DuplicateKeyException;
import com.mindalliance.channels.NotFoundException;
import com.mindalliance.channels.Scenario;
import com.mindalliance.channels.service.ChannelsServiceImpl;
import junit.framework.TestCase;

public class TestMemory extends TestCase {

    private Memory memory ;

    private ChannelsServiceImpl service;

    public TestMemory() {
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        memory = new Memory();
        service = new ChannelsServiceImpl();
        ChannelsServiceImpl.registerDefaultMedia( service );
        service.setAddingSamples( true );
        service.setDao( memory );
    }

    public void testInitial() {
        assertEquals( 2, memory.getScenarioCount() );
        assertTrue( memory.iterate( Scenario.class ).hasNext() );
        try {
            service.findScenario( "bla" );
            fail();
        } catch ( NotFoundException ignored ) {
            try {
                service.find( Scenario.class, -1L );
                fail();
            } catch ( NotFoundException ignore ) {
            }
        }
    }

    public void testAddDelete() throws DuplicateKeyException, NotFoundException {
        final int size = memory.getScenarioCount();

        final Scenario s = service.createScenario();
        s.setName( "Bogus" );

        assertEquals( size+1, memory.getScenarioCount() );

        assertSame( s, service.findScenario( s.getName() ) );
        assertSame( s, service.find( Scenario.class, s.getId() ) );

        memory.remove( s );
        try {
            service.findScenario( s.getName() );
            fail();
        } catch ( NotFoundException ignored ) {}
        try {
            memory.find( Scenario.class, s.getId() );
            fail();
        } catch ( NotFoundException ignored ) {}

        for ( int i = size; i > 0 ; i-- )
            memory.remove( service.getDefaultScenario() );
        assertEquals( 1, memory.getScenarioCount() );
        // last one not deleted
    }

    public void testAddTwice() throws DuplicateKeyException {
        final Scenario a = new Scenario();
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
        assertNotNull( service.getDefaultScenario() );
    }

    public void testCreateScenario() throws NotFoundException {
        assertEquals( 2, memory.getScenarioCount() );
        Scenario s = service.createScenario();
        assertEquals( 3, memory.getScenarioCount() );
        assertSame( s, memory.find( Scenario.class, s.getId() ) );
    }

}
