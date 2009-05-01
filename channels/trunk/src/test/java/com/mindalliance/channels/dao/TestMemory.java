package com.mindalliance.channels.dao;

import com.mindalliance.channels.DuplicateKeyException;
import com.mindalliance.channels.NotFoundException;
import com.mindalliance.channels.model.Scenario;
import com.mindalliance.channels.query.DefaultQueryService;
import junit.framework.TestCase;

public class TestMemory extends TestCase {

    private Memory memory ;

    private DefaultQueryService queryService;

    public TestMemory() {
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        memory = new Memory();
        queryService = new DefaultQueryService();
        queryService.setAddingSamples( true );
        queryService.setDao( memory );
        queryService.initialize();
    }

    public void testInitial() {
        assertEquals( 2, memory.getScenarioCount() );
        assertTrue( memory.list( Scenario.class ).iterator().hasNext() );
        try {
            queryService.findScenario( "bla" );
            fail();
        } catch ( NotFoundException ignored ) {
            try {
                queryService.find( Scenario.class, -1L );
                fail();
            } catch ( NotFoundException ignore ) {
            }
        }
    }

    public void testAddDelete() throws DuplicateKeyException, NotFoundException {
        long size = memory.getScenarioCount();

        Scenario s = queryService.createScenario();
        s.setName( "Bogus" );

        assertEquals( size + 1, memory.getScenarioCount() );

        assertSame( s, queryService.findScenario( s.getName() ) );
        assertSame( s, queryService.find( Scenario.class, s.getId() ) );

        memory.remove( s );
        try {
            queryService.findScenario( s.getName() );
            fail();
        } catch ( NotFoundException ignored ) {}
        try {
            memory.find( Scenario.class, s.getId() );
            fail();
        } catch ( NotFoundException ignored ) {}

        for ( long i = size; i > 0 ; i-- )
            memory.remove( queryService.getDefaultScenario() );
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
        assertNotNull( queryService.getDefaultScenario() );
    }

    public void testCreateScenario() throws NotFoundException {
        assertEquals( 2, memory.getScenarioCount() );
        Scenario s = queryService.createScenario();
        assertEquals( 3, memory.getScenarioCount() );
        assertSame( s, memory.find( Scenario.class, s.getId() ) );
    }

}
