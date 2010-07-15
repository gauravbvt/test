package com.mindalliance.channels.dao;

import com.mindalliance.channels.AbstractChannelsTest;
import com.mindalliance.channels.model.NotFoundException;
import com.mindalliance.channels.model.Segment;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;

public class TestPlanDao extends AbstractChannelsTest {

    @Autowired
    private PlanManager planManager;

    private PlanDao planDao;

    public TestPlanDao() {
    }

    @Override
    public void setUp() throws IOException {
        super.setUp();
        planDao = planManager.getDao( User.current().getPlan() );
    }

    @Test
    public void testInitial() {
        assertEquals( 1, getSegmentCount() );
        assertTrue( planDao.list( Segment.class ).iterator().hasNext() );
        try {
            planDao.findSegment( "bla" );
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
        return (long) planDao.list( Segment.class ).size();
    }

    @Test
    public void testAddDelete() throws DuplicateKeyException, NotFoundException {
        long size = getSegmentCount();

        Segment s = queryService.createSegment();
        s.setName( "Bogus" );

        assertEquals( size + 1, getSegmentCount() );

        assertSame( s, planDao.findSegment( s.getName() ) );
        assertSame( s, queryService.find( Segment.class, s.getId() ) );

        planDao.remove( s );
        try {
            planDao.findSegment( s.getName() );
            fail();
        } catch ( NotFoundException ignored ) {}
        try {
            planDao.find( Segment.class, s.getId() );
            fail();
        } catch ( NotFoundException ignored ) {}

        for ( long i = size; i > 0 ; i-- )
            planDao.remove( queryService.getDefaultSegment() );
        assertEquals( 1, getSegmentCount() );
        // last one not deleted
    }

    @Test
    public void testAddTwice() throws DuplicateKeyException {
        Segment a = new Segment();
        a.setName( "Bogus" );
        planDao.add( a, 0L );
        try {
            planDao.add( a, 0L );
            fail();
        } catch ( DuplicateKeyException ignored ) {
            // success
        }
    }

    @Test
    public void testDefault() {
        assertNotNull( queryService.getDefaultSegment() );
    }

    @Test
    public void testCreateSegment() throws NotFoundException {
        assertEquals( 1, getSegmentCount() );
        Segment s = queryService.createSegment();
        assertEquals( 2, getSegmentCount() );
        assertSame( s, planDao.find( Segment.class, s.getId() ) );
    }

}
