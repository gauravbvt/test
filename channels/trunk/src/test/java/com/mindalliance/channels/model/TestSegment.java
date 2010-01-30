package com.mindalliance.channels.model;

import com.mindalliance.channels.attachments.BitBucket;
import com.mindalliance.channels.dao.PlanManager;
import com.mindalliance.channels.dao.SimpleIdGenerator;
import com.mindalliance.channels.export.DummyExporter;
import com.mindalliance.channels.query.DefaultQueryService;
import junit.framework.TestCase;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

/**
 * Test a segment in isolation.
 */
@SuppressWarnings( { "OverlyLongMethod", "HardCodedStringLiteral" } )
public class TestSegment extends TestCase {

    /** The segment being tested. */
    private Segment segment;
    private DefaultQueryService queryService;

    public TestSegment() {
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        PlanManager planManager = new PlanManager( new DummyExporter(), new SimpleIdGenerator() );
        planManager.afterPropertiesSet();
        User user = new User();
        user.setPlan( planManager.getPlans().get( 0 ) );

        queryService = new DefaultQueryService( planManager, new BitBucket() );
        queryService.afterPropertiesSet();
        segment = queryService.createSegment();
    }

    public void testDescription() {
        assertEquals( Segment.DEFAULT_DESCRIPTION, segment.getDescription() );
        segment.setDescription( null );
        assertEquals( "", segment.getDescription() );

        String s = "Bla";
        segment.setDescription( s );
        assertSame( s, segment.getDescription() );
    }

    public void testName() {
        assertEquals( Segment.DEFAULT_NAME, segment.getName() );

        segment.setName( null );
        assertEquals( "", segment.getName() );

        String s = "Bla";
        segment.setName( s );
        assertSame( s, segment.getName() );
    }

    public void testEquals() {
        assertEquals( segment, segment );
        assertFalse( segment.equals( null ) );
        assertFalse( segment.equals( queryService.createSegment() ) );
    }

    public void testHashCode() {
        assertNotSame( segment.hashCode(), queryService.createSegment().hashCode() );
    }

    public void testNodes() {
        assertEquals( 1, segment.getNodeCount() );

        Node p1 = segment.getDefaultPart();
        Part p2 = queryService.createPart( segment );
        Part p3 = queryService.createPart( segment );

        assertSame( p1, segment.getNode( p1.getId() ) );
        assertSame( p2, segment.getNode( p2.getId() ) );
        assertSame( p3, segment.getNode( p3.getId() ) );

        Flow f1 = queryService.connect( p1, p2, "" );
        Flow f2 = queryService.connect( p2, p3, "" );

        assertEquals( 3, segment.getNodeCount() );
        assertSame( f1, p1.getFlow( f1.getId() ) );
        assertSame( f1, p2.getFlow( f1.getId() ) );
        assertSame( f2, p2.getFlow( f2.getId() ) );
        assertSame( f2, p3.getFlow( f2.getId() ) );

        segment.removeNode( p2 );
        // node replaced in flows by connectors
        assertEquals( 4, segment.getNodeCount() );
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

    public void testRemoveOnly() {
        Iterator<Node> nodes = segment.nodes();
        assertTrue( nodes.hasNext() );
        Node initial = nodes.next();
        assertSame( initial, segment.getNode( initial.getId() ) );

        segment.removeNode( initial );
        assertSame( initial, segment.getNode( initial.getId() ) );
    }

    public void testConnect() {
        Part p1 = queryService.createPart( segment );
        Part p2 = queryService.createPart( segment );

        queryService.connect( p1, p2, "" );
        Part p3 = queryService.createPart( segment );
        Part p4 = queryService.createPart( segment );

        Flow f = queryService.connect( p3, p4, "" );
        assertSame( p3, f.getSource() );
        assertSame( p4, f.getTarget() );
        assertSame( f, p3.getFlow( f.getId() ) );
        assertSame( f, p4.getFlow( f.getId() ) );
        assertSame( f, p3.outcomes().next() );
        assertSame( f, p4.requirements().next() );

        // we now allow flows of same name between same nodes
        Iterator<Flow> iterator = p3.outcomesNamed( "" );
        assertTrue( iterator.hasNext() );
        assertSame( f, iterator.next() );
        assertFalse( iterator.hasNext() );
        Flow f2 = queryService.connect( p3, p4, "" );

        iterator = p4.requirementsNamed( "" );
        assertTrue( iterator.hasNext() );
        assertSame( f, iterator.next() );
        assertTrue( iterator.hasNext() );
        assertSame( f2, iterator.next() );
        assertFalse( iterator.hasNext() );
    }

    public void testDisconnect() {
        Part p1 = queryService.createPart( segment );
        Part p2 = queryService.createPart( segment );

        Flow f = queryService.connect( p1, p2, "" );

        assertSame( f, p1.getFlow( f.getId() ) );
        assertSame( f, p2.getFlow( f.getId() ) );
        f.disconnect();
        assertNull( p1.getFlow( f.getId() ) );
        assertNull( p2.getFlow( f.getId() ) );
    }

    public void testFlows() {
        Part bob = queryService.createPart( segment );
        Part sue = queryService.createPart( segment );
        Part joe = queryService.createPart( segment );

        Flow f1 = queryService.connect( bob, sue, "" );
        Flow f2 = queryService.connect( bob, joe, "" );
        Flow f3 = queryService.connect( sue, joe, "" );
        Flow f4 = queryService.connect( joe, bob, "" );

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

    public void testDeleteLast() {
        Part dp = segment.getDefaultPart();

        Iterator<Node> nodes = segment.nodes();
        assertSame( dp, nodes.next() );
        assertFalse( nodes.hasNext() );

        segment.removeNode( dp );
        Iterator<Node> nodes2 = segment.nodes();
        assertSame( dp, nodes2.next() );
        assertFalse( nodes2.hasNext() );

        Flow out = dp.createOutcome( queryService );
        Connector c1 = (Connector) out.getTarget();
        Flow in  = dp.createRequirement( queryService );
        Connector c2 = (Connector) in.getSource();
        segment.removeNode( dp );

        assertSame( dp, segment.getNode( dp.getId() ) );
        assertSame( c1, segment.getNode( c1.getId() ) );
        assertSame( c2, segment.getNode( c2.getId() ) );
        Part p2 = queryService.createPart( segment );
        segment.removeNode( dp );
        assertSame( p2, segment.getNode( p2.getId() ) );
        assertNull( segment.getNode( dp.getId() ) );
        assertNull( segment.getNode( c1.getId() ) );
        assertNull( segment.getNode( c2.getId() ) );

        segment.removeNode( p2 );
        assertSame( p2, segment.getNode( p2.getId() ) );
    }

    public void testFindRoles() {
        Organization org = queryService.findOrCreate( Organization.class, "Building" );

        Part p1 = queryService.createPart( segment );
        Role r1 = queryService.findOrCreate( Role.class, "Janitor" );
        p1.setRole( r1 );

        Part p2 = queryService.createPart( segment );
        Role r2 = queryService.findOrCreate( Role.class, "Plumber" );
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
