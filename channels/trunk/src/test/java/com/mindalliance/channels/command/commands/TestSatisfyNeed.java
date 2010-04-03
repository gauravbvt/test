package com.mindalliance.channels.command.commands;

import com.mindalliance.channels.AbstractChannelsTest;
import com.mindalliance.channels.command.Command;
import com.mindalliance.channels.command.CommandException;
import com.mindalliance.channels.model.Connector;
import com.mindalliance.channels.model.Flow;
import com.mindalliance.channels.model.Part;
import com.mindalliance.channels.model.Role;
import com.mindalliance.channels.model.Segment;
import org.junit.After;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

import java.io.IOException;
import java.util.Iterator;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Mar 12, 2009
 * Time: 9:28:05 AM
 */
public class TestSatisfyNeed extends AbstractChannelsTest {

    private Segment segment;
    private Part source;
    private Part target;
    private Part otherTarget;
    private Flow flow;
    private Connector connector;

    @Override
    public void setUp() throws IOException {
        super.setUp();

        segment = queryService.createSegment();
        source = segment.getDefaultPart();
        source.setRole( queryService.findOrCreate( Role.class, "source" ) );
        source.setTask( "doing source things" );
        target = queryService.createPart( segment );
        target.setTask( "doing target things" );
        target.setRole( queryService.findOrCreate( Role.class, "target" ) );
        flow = queryService.connect( source, target, "info" );
        otherTarget = queryService.createPart( segment );
        otherTarget.setTask( "doing other things" );
        otherTarget.setRole( queryService.findOrCreate( Role.class, "other target" ) );
        connector = queryService.createConnector( segment );
        queryService.connect( connector, otherTarget, "info" );
    }

    @Override
    @After
    public void tearDown() {
        queryService.remove( segment );
        super.tearDown();
    }

    @Test
    public void testSatisfyNeed() throws CommandException {
        assertSame( 1, countFlows( source.sends() ) );
        assertSame( 1, countFlows( target.receives() ) );
        assertSame( 1, countFlows( otherTarget.receives() ) );
        Command satisfyNeed = new SatisfyNeed( flow, connector.getInnerFlow() );
        assertTrue( getCommander().canDo( satisfyNeed ) );
        assertTrue( getCommander().doCommand( satisfyNeed ).isAdded() );
        assertSame( 1, countFlows( source.sends() ) );
        assertSame( 0, countFlows( target.receives() ) );
        assertSame( 2, countFlows( otherTarget.receives() ) );
        assertTrue( getCommander().canUndo() );
        assertTrue( getCommander().undo().isUnknown() );
        assertSame( 1, countFlows( source.sends() ) );
        assertSame( 1, countFlows( target.receives() ) );
        assertSame( 1, countFlows( otherTarget.receives() ) );
        assertTrue( getCommander().canRedo() );
        assertTrue( getCommander().redo().isUnknown() );
        assertSame( 1, countFlows( source.sends() ) );
        assertSame( 0, countFlows( target.receives() ) );
        assertSame( 2, countFlows( otherTarget.receives() ) );
        assertTrue( getCommander().canUndo() );
    }

    private int countFlows( Iterator<Flow> flows ) {
        int count = 0;
        while ( flows.hasNext() ) {
            flows.next();
            count++;
        }
        return count;
    }
}
