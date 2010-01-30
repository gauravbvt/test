package com.mindalliance.channels.dao;

import com.mindalliance.channels.DuplicateKeyException;
import com.mindalliance.channels.NotFoundException;
import com.mindalliance.channels.attachments.BitBucket;
import com.mindalliance.channels.export.DummyExporter;
import com.mindalliance.channels.model.Plan;
import com.mindalliance.channels.model.Segment;
import com.mindalliance.channels.model.User;
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
        PlanManager planManager = new PlanManager( new DummyExporter(), new SimpleIdGenerator() );
        planManager.afterPropertiesSet();
        Plan plan = planManager.getPlans().get( 0 );
        memory = planManager.getDao( plan );
        queryService = new DefaultQueryService( planManager, new BitBucket() );
        User test = new User();
        test.setPlan( plan );
//        queryService.setAddingSamples( true );
        queryService.afterPropertiesSet();
    }

    public void testInitial() {
        assertEquals( 2, getSegmentCount() );
        assertTrue( memory.list( Segment.class ).iterator().hasNext() );
        try {
            queryService.findSegment( "bla" );
            fail();
        } catch ( NotFoundException ignored ) {
            try {
                queryService.find( Segment.class, -1L );
                fail();
            } catch ( NotFoundException ignore ) {
            }
        }
    }

    private long getSegmentCount() {
        return (long) memory.list( Segment.class ).size();
    }

    public void testAddDelete() throws DuplicateKeyException, NotFoundException {
        long size = getSegmentCount();

        Segment s = queryService.createSegment();
        s.setName( "Bogus" );

        assertEquals( size + 1, getSegmentCount() );

        assertSame( s, queryService.findSegment( s.getName() ) );
        assertSame( s, queryService.find( Segment.class, s.getId() ) );

        memory.remove( s );
        try {
            queryService.findSegment( s.getName() );
            fail();
        } catch ( NotFoundException ignored ) {}
        try {
            memory.find( Segment.class, s.getId() );
            fail();
        } catch ( NotFoundException ignored ) {}

        for ( long i = size; i > 0 ; i-- )
            memory.remove( queryService.getDefaultSegment() );
        assertEquals( 1, getSegmentCount() );
        // last one not deleted
    }

    public void testAddTwice() throws DuplicateKeyException {
        Segment a = new Segment();
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
        assertNotNull( queryService.getDefaultSegment() );
    }

    public void testCreateSegment() throws NotFoundException {
        assertEquals( 2, getSegmentCount() );
        Segment s = queryService.createSegment();
        assertEquals( 3, getSegmentCount() );
        assertSame( s, memory.find( Segment.class, s.getId() ) );
    }

}
