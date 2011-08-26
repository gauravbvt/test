package com.mindalliance.channels.engine.command.commands;

import com.mindalliance.channels.AbstractChannelsTest;
import com.mindalliance.channels.engine.command.Command;
import com.mindalliance.channels.core.model.Connector;
import com.mindalliance.channels.core.model.Flow;
import com.mindalliance.channels.core.model.Part;
import com.mindalliance.channels.core.model.Role;
import com.mindalliance.channels.core.model.Segment;
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
    private Flow need;
    private Flow capability;

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
        Connector t_connector = queryService.createConnector( segment );
        capability = queryService.connect( source, t_connector, "info" );
        Connector s_connector = queryService.createConnector( segment );
        need =queryService.connect( s_connector, target, "info" );
    }

    @Override
    @After
    public void tearDown() {
        queryService.remove( segment );
        super.tearDown();
    }

    @Test
    public void testSatisfyNeed() {
        assertSame( 1, countFlows( source.sends() ) );
        assertSame( 1, countFlows( target.receives() ) );
        Command satisfyNeed = new SatisfyNeed( need, capability, true, true );
        assertTrue( getCommander().canDo( satisfyNeed ) );
        assertTrue( getCommander().doCommand( satisfyNeed ).isAdded() );
        assertSame( 2, countFlows( source.sends() ) );
        assertSame( 2, countFlows( target.receives() ) );
        assertTrue( getCommander().canUndo() );
        assertTrue( getCommander().undo().isUnknown() );
        assertSame( 1, countFlows( source.sends() ) );
        assertSame( 1, countFlows( target.receives() ) );
        assertTrue( getCommander().canRedo() );
        assertTrue( getCommander().redo().isUnknown() );
        assertSame( 2, countFlows( source.sends() ) );
        assertSame( 2, countFlows( target.receives() ) );
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
