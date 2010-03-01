package com.mindalliance.channels.command.commands;

import com.mindalliance.channels.AbstractChannelsTest;
import com.mindalliance.channels.QueryService;
import com.mindalliance.channels.command.Command;
import com.mindalliance.channels.command.CommandException;
import com.mindalliance.channels.model.Connector;
import com.mindalliance.channels.model.Flow;
import com.mindalliance.channels.model.Part;
import com.mindalliance.channels.model.Role;
import com.mindalliance.channels.model.Segment;

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
    private QueryService queryService;
    Part source;
    Part target;
    Part otherTarget;
    Flow flow;
    Connector connector;

    @Override
    protected void setUp() throws IOException {
        super.setUp();
        queryService = app.getQueryService();
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

    protected void tearDown() {
        queryService.remove( segment );
    }

    public void testSatisfyNeed() throws CommandException {
        assertTrue( countFlows( source.sends() ) == 1 );
        assertTrue( countFlows( target.receives() ) == 1 );
        assertTrue( countFlows( otherTarget.receives() ) == 1 );
        Command satisfyNeed = new SatisfyNeed( flow, connector.getInnerFlow() );
        assertTrue( commander.canDo( satisfyNeed ) );
        assertTrue( commander.doCommand( satisfyNeed ).isAdded() );
        assertTrue( countFlows( source.sends() ) == 1 );
        assertTrue( countFlows( target.receives() ) == 0 );
        assertTrue( countFlows( otherTarget.receives() ) == 2 );
        assertTrue( commander.canUndo() );
        assertTrue( commander.undo().isUnknown() );
        assertTrue( countFlows( source.sends() ) == 1 );
        assertTrue( countFlows( target.receives() ) == 1 );
        assertTrue( countFlows( otherTarget.receives() ) == 1 );
        assertTrue( commander.canRedo() );
        assertTrue( commander.redo().isUnknown() );
        assertTrue( countFlows( source.sends() ) == 1 );
        assertTrue( countFlows( target.receives() ) == 0 );
        assertTrue( countFlows( otherTarget.receives() ) == 2 );
        assertTrue( commander.canUndo() );
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
