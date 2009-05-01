package com.mindalliance.channels.model;

import com.mindalliance.channels.query.DefaultQueryService;
import com.mindalliance.channels.dao.Memory;
import com.mindalliance.channels.model.Connector;
import com.mindalliance.channels.model.Flow;
import com.mindalliance.channels.model.Node;
import com.mindalliance.channels.model.Organization;
import com.mindalliance.channels.model.Part;
import com.mindalliance.channels.model.Role;
import com.mindalliance.channels.model.Scenario;
import com.mindalliance.channels.QueryService;
import junit.framework.TestCase;

import java.util.HashSet;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.List;

/**
 * Test a scenario in isolation.
 */
@SuppressWarnings( { "OverlyLongMethod", "HardCodedStringLiteral" } )
public class TestScenario extends TestCase {

    /** The scenario being tested. */
    private Scenario scenario;
    private QueryService queryService;

    public TestScenario() {
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        queryService = new DefaultQueryService( new Memory() );
        scenario = queryService.createScenario();
    }

    public void testDescription() {
        assertEquals( Scenario.DEFAULT_DESCRIPTION, scenario.getDescription() );
        scenario.setDescription( null );
        assertEquals( "", scenario.getDescription() );

        String s = "Bla";
        scenario.setDescription( s );
        assertSame( s, scenario.getDescription() );
    }

    public void testName() {
        assertEquals( Scenario.DEFAULT_NAME, scenario.getName() );

        scenario.setName( null );
        assertEquals( "", scenario.getName() );

        String s = "Bla";
        scenario.setName( s );
        assertSame( s, scenario.getName() );
    }

    public void testEquals() {
        assertEquals( scenario, scenario );
        assertFalse( scenario.equals( null ) );
        assertFalse( scenario.equals( queryService.createScenario() ) );
    }

    public void testHashCode() {
        assertNotSame( scenario.hashCode(), queryService.createScenario().hashCode() );
    }

    public void testNodes() {
        assertEquals( 1, scenario.getNodeCount() );

        Node p1 = scenario.getDefaultPart();
        Part p2 = queryService.createPart( scenario );
        Part p3 = queryService.createPart( scenario );

        assertSame( p1, scenario.getNode( p1.getId() ) );
        assertSame( p2, scenario.getNode( p2.getId() ) );
        assertSame( p3, scenario.getNode( p3.getId() ) );

        Flow f1 = queryService.connect( p1, p2, "" );
        Flow f2 = queryService.connect( p2, p3, "" );

        assertEquals( 3, scenario.getNodeCount() );
        assertSame( f1, p1.getFlow( f1.getId() ) );
        assertSame( f1, p2.getFlow( f1.getId() ) );
        assertSame( f2, p2.getFlow( f2.getId() ) );
        assertSame( f2, p3.getFlow( f2.getId() ) );

        scenario.removeNode( p2 );
        // node replaced in flows by connectors
        assertEquals( 4, scenario.getNodeCount() );
        assertSame( p1, scenario.getNode( p1.getId() ) );
        assertNull( scenario.getNode( p2.getId() ) );
        assertSame( p3, scenario.getNode( p3.getId() ) );

        assertNull( p1.getFlow( f1.getId() ) );
        assertNull( p3.getFlow( f2.getId() ) );
        assertNull( f1.getSource() );
        assertNull( f2.getSource() );
        assertNull( f1.getTarget() );
        assertNull( f2.getTarget() );
    }

    public void testRemoveOnly() {
        Iterator<Node> nodes = scenario.nodes();
        assertTrue( nodes.hasNext() );
        Node initial = nodes.next();
        assertSame( initial, scenario.getNode( initial.getId() ) );

        scenario.removeNode( initial );
        assertSame( initial, scenario.getNode( initial.getId() ) );
    }

    public void testConnect() {
        Part p1 = queryService.createPart( scenario );
        Part p2 = queryService.createPart( scenario );

        queryService.connect( p1, p2, "" );
        Part p3 = queryService.createPart( scenario );
        Part p4 = queryService.createPart( scenario );

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
        Part p1 = queryService.createPart( scenario );
        Part p2 = queryService.createPart( scenario );

        Flow f = queryService.connect( p1, p2, "" );

        assertSame( f, p1.getFlow( f.getId() ) );
        assertSame( f, p2.getFlow( f.getId() ) );
        f.disconnect();
        assertNull( p1.getFlow( f.getId() ) );
        assertNull( p2.getFlow( f.getId() ) );
    }

    public void testFlows() {
        Part bob = queryService.createPart( scenario );
        Part sue = queryService.createPart( scenario );
        Part joe = queryService.createPart( scenario );

        Flow f1 = queryService.connect( bob, sue, "" );
        Flow f2 = queryService.connect( bob, joe, "" );
        Flow f3 = queryService.connect( sue, joe, "" );
        Flow f4 = queryService.connect( joe, bob, "" );

        Set<Flow> fs = new HashSet<Flow>();
        fs.add( f1 );
        fs.add( f2 );
        fs.add( f3 );
        fs.add( f4 );

        Iterator<Flow> flows = scenario.flows();
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
            scenario.flows().remove();
            fail();
        } catch ( UnsupportedOperationException ignored ) {}
    }

    public void testDeleteLast() {
        Part dp = scenario.getDefaultPart();

        Iterator<Node> nodes = scenario.nodes();
        assertSame( dp, nodes.next() );
        assertFalse( nodes.hasNext() );

        scenario.removeNode( dp );
        Iterator<Node> nodes2 = scenario.nodes();
        assertSame( dp, nodes2.next() );
        assertFalse( nodes2.hasNext() );

        Flow out = dp.createOutcome( queryService );
        Connector c1 = (Connector) out.getTarget();
        Flow in  = dp.createRequirement( queryService );
        Connector c2 = (Connector) in.getSource();
        scenario.removeNode( dp );

        assertSame( dp, scenario.getNode( dp.getId() ) );
        assertSame( c1, scenario.getNode( c1.getId() ) );
        assertSame( c2, scenario.getNode( c2.getId() ) );
        Part p2 = queryService.createPart( scenario );
        scenario.removeNode( dp );
        assertSame( p2, scenario.getNode( p2.getId() ) );
        assertNull( scenario.getNode( dp.getId() ) );
        assertNull( scenario.getNode( c1.getId() ) );
        assertNull( scenario.getNode( c2.getId() ) );

        scenario.removeNode( p2 );
        assertSame( p2, scenario.getNode( p2.getId() ) );
    }

    public void testFindRoles() {
        Organization org = queryService.findOrCreate( Organization.class, "Building" );

        Part p1 = queryService.createPart( scenario );
        Role r1 = queryService.findOrCreate( Role.class, "Janitor" );
        p1.setRole( r1 );

        Part p2 = queryService.createPart( scenario );
        Role r2 = queryService.findOrCreate( Role.class, "Plumber" );
        p2.setRole( r2 );

        List<Role> roleList = scenario.findRoles( org );
        assertEquals( 0, roleList.size() );

        p1.setOrganization( org );
        p2.setOrganization( org );

        List<Role> roleList2 = scenario.findRoles( org );
        assertEquals( 2, roleList2.size() );
        assertTrue( roleList2.contains( r1 ) );
        assertTrue( roleList2.contains( r2 ) );
    }
}
