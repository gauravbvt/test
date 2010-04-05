package com.mindalliance.channels.model;

import com.mindalliance.channels.AbstractChannelsTest;
import com.mindalliance.channels.dao.NotFoundException;
import com.mindalliance.channels.dao.PlanDao;
import com.mindalliance.channels.dao.PlanManager;
import org.junit.After;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

/**
 * Test a segment in isolation.
 */
@SuppressWarnings( { "OverlyLongMethod", "HardCodedStringLiteral" } )
public class TestSegment extends AbstractChannelsTest {

    /** The segment being tested. */
    private Segment segment;

    private PlanDao planDao;

    @Override
    @Before
    public void setUp() throws IOException {
        super.setUp();
        try {
            planDao = planManager.getDao( PlanManager.plan() );
        } catch ( NotFoundException ignored ) {
            fail();
        }
        segment = planDao.createSegment( null, null );
    }

    @After
    public void cleanUp() {
        planDao.remove( segment );
    }

    @Test
    public void testDescription() {
        assertEquals( Segment.DEFAULT_DESCRIPTION, segment.getDescription() );
        segment.setDescription( null );
        assertEquals( "", segment.getDescription() );

        String s = "Bla";
        segment.setDescription( s );
        assertSame( s, segment.getDescription() );
    }

    @Test
    public void testName() {
        assertEquals( Segment.DEFAULT_NAME, segment.getName() );

        segment.setName( null );
        assertEquals( "", segment.getName() );

        String s = "Bla";
        segment.setName( s );
        assertSame( s, segment.getName() );
    }

    @Test
    public void testEquals() {
        assertEquals( segment, segment );
        assertFalse( segment.equals( null ) );
        assertFalse( segment.equals( planDao.createSegment( null, null ) ) );
    }

    @Test
    public void testHashCode() {
        assertNotSame( segment.hashCode(), planDao.createSegment( null, null ).hashCode() );
    }

    @Test
    public void testNodes() {
        assertEquals( 1, segment.getNodeCount() );

        Node p1 = segment.getDefaultPart();
        Part p2 = planDao.createPart( segment, null );
        Part p3 = planDao.createPart( segment, null );

        assertSame( p1, segment.getNode( p1.getId() ) );
        assertSame( p2, segment.getNode( p2.getId() ) );
        assertSame( p3, segment.getNode( p3.getId() ) );

        Flow f1 = planDao.connect( p1, p2, "", null );
        Flow f2 = planDao.connect( p2, p3, "", null );

        assertEquals( 3, segment.getNodeCount() );
        assertSame( f1, p1.getFlow( f1.getId() ) );
        assertSame( f1, p2.getFlow( f1.getId() ) );
        assertSame( f2, p2.getFlow( f2.getId() ) );
        assertSame( f2, p3.getFlow( f2.getId() ) );

        segment.removeNode( p2, planDao );
        // node replaced in flows by connectors
        assertEquals( 2, segment.getNodeCount() );
        assertSame( p1, segment.getNode( p1.getId() ) );
        assertNull( segment.getNode( p2.getId() ) );
        assertSame( p3, segment.getNode( p3.getId() ) );

        assertNull( p1.getFlow( f1.getId() ) );
        assertNull( p3.getFlow( f2.getId() ) );
        assertNull( f1.getSource() );
        assertNull( f2.getSource() );
        assertNull( f1.getTarget() );
        assertNull( f2.getTarget() );
    }

    @Test
    public void testRemoveOnly() {
        Iterator<Node> nodes = segment.nodes();
        assertTrue( nodes.hasNext() );
        Node initial = nodes.next();
        assertSame( initial, segment.getNode( initial.getId() ) );

        segment.removeNode( initial, planDao );
        assertSame( initial, segment.getNode( initial.getId() ) );
    }

    @Test
    public void testConnect() {
        Part p1 = planDao.createPart( segment, null );
        Part p2 = planDao.createPart( segment, null );

        planDao.connect( p1, p2, "info", null );
        Part p3 = planDao.createPart( segment, null );
        Part p4 = planDao.createPart( segment, null );

        Flow f = planDao.connect( p3, p4, "info", null );
        assertSame( p3, f.getSource() );
        assertSame( p4, f.getTarget() );
        assertSame( f, p3.getFlow( f.getId() ) );
        assertSame( f, p4.getFlow( f.getId() ) );
        assertSame( f, p3.sends().next() );
        assertSame( f, p4.receives().next() );

        // we now allow flows of same name between same nodes
        Iterator<Flow> iterator = p3.sendsNamed( "info" );
        assertTrue( iterator.hasNext() );
        assertSame( f, iterator.next() );
        assertFalse( iterator.hasNext() );
        Flow f2 = planDao.connect( p3, p4, "info", null );

        iterator = p4.receivesNamed( "info" );
        assertTrue( iterator.hasNext() );
        assertSame( f, iterator.next() );
        assertTrue( iterator.hasNext() );
        assertSame( f2, iterator.next() );
        assertFalse( iterator.hasNext() );
    }

    @Test
    public void testDisconnect() {
        Part p1 = planDao.createPart( segment, null );
        Part p2 = planDao.createPart( segment, null );

        Flow f = planDao.connect( p1, p2, "", null );

        assertSame( f, p1.getFlow( f.getId() ) );
        assertSame( f, p2.getFlow( f.getId() ) );
        f.disconnect( planDao );
        assertNull( p1.getFlow( f.getId() ) );
        assertNull( p2.getFlow( f.getId() ) );
    }

    @Test
    public void testFlows() {
        Part bob = planDao.createPart( segment, null );
        Part sue = planDao.createPart( segment, null );
        Part joe = planDao.createPart( segment, null );

        Flow f1 = planDao.connect( bob, sue, "", null );
        Flow f2 = planDao.connect( bob, joe, "", null );
        Flow f3 = planDao.connect( sue, joe, "", null );
        Flow f4 = planDao.connect( joe, bob, "", null );

        Set<Flow> fs = new HashSet<Flow>();
        fs.add( f1 );
        fs.add( f2 );
        fs.add( f3 );
        fs.add( f4 );

        Iterator<Flow> flows = segment.flows();
        assertTrue( flows.hasNext() );
        assertTrue( fs.contains( flows.next() ) );
        assertTrue( fs.contains( flows.next() ) );
        assertTrue( fs.contains( flows.next() ) );
        assertTrue( fs.contains( flows.next() ) );

        assertFalse( flows.hasNext() );

        try {
            flows.next();
            fail();
        } catch ( NoSuchElementException ignored ) {}

        try {
            segment.flows().remove();
            fail();
        } catch ( UnsupportedOperationException ignored ) {}
    }

    @Test
    public void testDeleteLast() {
        Part dp = segment.getDefaultPart();

        Iterator<Node> nodes = segment.nodes();
        assertSame( dp, nodes.next() );
        assertFalse( nodes.hasNext() );

        segment.removeNode( dp, planDao );
        Iterator<Node> nodes2 = segment.nodes();
        assertSame( dp, nodes2.next() );
        assertFalse( nodes2.hasNext() );

        Flow out = dp.createSend( planDao );
        Connector c1 = (Connector) out.getTarget();
        Flow in  = dp.createReceive( planDao );
        Connector c2 = (Connector) in.getSource();
        segment.removeNode( dp, planDao );

        assertSame( dp, segment.getNode( dp.getId() ) );
        assertSame( c1, segment.getNode( c1.getId() ) );
        assertSame( c2, segment.getNode( c2.getId() ) );
        Part p2 = planDao.createPart( segment, null );
        segment.removeNode( dp, planDao );
        assertSame( p2, segment.getNode( p2.getId() ) );
        assertNull( segment.getNode( dp.getId() ) );
        assertNull( segment.getNode( c1.getId() ) );
        assertNull( segment.getNode( c2.getId() ) );

        segment.removeNode( p2, planDao );
        assertSame( p2, segment.getNode( p2.getId() ) );
    }

    @Test
    public void testFindRoles() {
        Organization org = planDao.findOrCreate( Organization.class, "Building", null );

        Part p1 = planDao.createPart( segment, null );
        Role r1 = planDao.findOrCreate( Role.class, "Janitor", null );
        p1.setRole( r1 );

        Part p2 = planDao.createPart( segment, null );
        Role r2 = planDao.findOrCreate( Role.class, "Plumber", null );
        p2.setRole( r2 );

        List<Role> roleList = segment.findRoles( org );
        assertEquals( 0, roleList.size() );

        p1.setOrganization( org );
        p2.setOrganization( org );

        List<Role> roleList2 = segment.findRoles( org );
        assertEquals( 2, roleList2.size() );
        assertTrue( roleList2.contains( r1 ) );
        assertTrue( roleList2.contains( r2 ) );
    }
}
