package com.mindalliance.channels.model;

import com.mindalliance.channels.query.DataQueryObjectImpl;
import com.mindalliance.channels.dao.Memory;
import com.mindalliance.channels.model.Actor;
import com.mindalliance.channels.model.Connector;
import com.mindalliance.channels.model.ExternalFlow;
import com.mindalliance.channels.model.Flow;
import com.mindalliance.channels.model.Part;
import com.mindalliance.channels.model.Scenario;
import com.mindalliance.channels.DataQueryObject;
import junit.framework.TestCase;

/**
 * ...
 */
@SuppressWarnings( { "HardCodedStringLiteral" } )
public class TestExternalFlow extends TestCase {

    private DataQueryObject dqo;
    private Scenario s1;
    private Scenario s2;

    private Part s1p1;
    private Part s1p2;

    public TestExternalFlow() {
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        dqo = new DataQueryObjectImpl( new Memory() );
        s1 = dqo.createScenario();
        s1p1 = s1.getDefaultPart();
        s1p1.setActor( new Actor( "p1" ) );
        s1p2 = dqo.createPart( s1 );
        s1p2.setActor( new Actor( "p2" ) );

        s2 = dqo.createScenario();

        // S2 "included" in S1
        Part s2Part = s2.getDefaultPart();
        s2Part.setActor( new Actor( "p3" ) );
        s2Part.createOutcome( dqo );
        s2Part.createRequirement( dqo );

        dqo.connect( s1p1, s2.inputs().next(), "" );
        dqo.connect( s2.outputs().next(), s1p2, "" );
    }

    public void testConstructor() {
        // test degenerate cases
        try {
            Part p = s2.getDefaultPart();
            new ExternalFlow( p, p.outcomes().next().getTarget(), "" );
            fail();
        } catch ( IllegalArgumentException ignored ) {}

        try {
            new ExternalFlow( s1p1, s1p2, "" );
            fail();
        } catch ( IllegalArgumentException ignored ) {}
    }

    public void testInitial() {
        Flow f1 = s1p1.outcomes().next();
        assertFalse( f1.isInternal() );
        assertEquals( "p1 notify p3 of something", f1.toString() );
        assertTrue( s2.inputs().next().externalFlows().hasNext() );

        Flow f2 = s1p2.requirements().next();
        assertFalse( f2.isInternal() );
        assertEquals( "p3 notify p2 of something", f2.toString() );
        assertTrue( s2.outputs().next().externalFlows().hasNext() );
    }

    /**
     * Remove an external flow.
     */
    public void testRemove1() {
        // output
        Flow f = s1p1.outcomes().next();
        f.disconnect();

        assertNull( f.getSource() );
        assertNull( f.getTarget() );
        assertFalse( s1p1.outcomes().hasNext() );
        assertFalse( s2.inputs().next().externalFlows().hasNext() );

        // input
        Flow f2 = s1p2.requirements().next();
        f2.disconnect();

        assertNull( f2.getSource() );
        assertNull( f2.getTarget() );
        assertFalse( s1p2.requirements().hasNext() );
        assertFalse( s2.outputs().next().externalFlows().hasNext() );
    }

    /**
     * Remove a connected connector.
     */
    public void testRemove2() {
        Part p3 = s2.getDefaultPart();
        Flow f1 = p3.outcomes().next();
        Connector c1 = (Connector) f1.getTarget();

        f1.disconnect();

        assertFalse( c1.requirements().hasNext() );
        assertFalse( c1.externalFlows().hasNext() );
        assertFalse( s1p2.requirements().hasNext() );

        Flow f2 = p3.requirements().next();
        Connector c2 = (Connector) f2.getSource();

        f2.disconnect();

        assertFalse( c2.outcomes().hasNext() );
        assertFalse( c2.externalFlows().hasNext() );
        assertFalse( s1p1.outcomes().hasNext() );

    }
}
