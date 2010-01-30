package com.mindalliance.channels.command.commands;

import com.mindalliance.channels.AbstractChannelsTest;
import com.mindalliance.channels.QueryService;
import com.mindalliance.channels.command.Change;
import com.mindalliance.channels.model.Flow;
import com.mindalliance.channels.model.Part;
import com.mindalliance.channels.model.Segment;

import java.io.IOException;
import java.util.Iterator;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Mar 7, 2009
 * Time: 7:42:08 PM
 */
public class TestAddRemovePart extends AbstractChannelsTest {

    private Part part;
    private Segment segment;
    private QueryService queryService;

    @Override
    protected void setUp() throws IOException {
        super.setUp();
        queryService = app.getQueryService();
        segment = queryService.createSegment();
        part = segment.getDefaultPart();
        Part other = queryService.createPart( segment );
        queryService.connect( part, other, "foo" );
        queryService.connect( queryService.createConnector( segment ), part, "bar" );
    }

    protected void tearDown() {
        queryService.remove( segment );
    }

    public void testRemoveAddPart() throws Exception {
        assertTrue( countParts() == 2 );
        assertTrue( countFlows() == 2 );

        RemovePart removePart = new RemovePart( part );
        assertTrue( commander.canDo( removePart ) );
        Change change = commander.doCommand( removePart );
        assertTrue( change.isRecomposed() );
        assertTrue( change.getSubject() instanceof Segment );
        assertTrue( countParts() == 1 );
        assertTrue( countFlows() == 1 );

        assertTrue( commander.canUndo() );
        assertTrue( commander.undo().isUnknown() );
        assertTrue( countParts() == 2 );
        assertTrue( countFlows() == 2 );

        assertFalse( commander.canUndo() );
        assertTrue( commander.canRedo() );
        assertTrue( commander.redo().isRecomposed() );
        assertTrue( countParts() == 1 );
        assertTrue( countFlows() == 1 );

        assertTrue( commander.canUndo() );
        commander.undo();
        assertTrue( countParts() == 2 );
        assertTrue( countFlows() == 2 );

        assertTrue( commander.canRedo() );
        commander.redo();
        assertTrue( countParts() == 1 );
        assertTrue( countFlows() == 1 );
    }

    private int countParts() {
        int count = 0;
        Iterator<Part> parts = segment.parts();
        while ( parts.hasNext() ) {
            parts.next();
            count++;
        }
        return count;
    }

    private int countFlows() {
        int count = 0;
        Iterator<Flow> flows = segment.flows();
        while ( flows.hasNext() ) {
            flows.next();
            count++;
        }
        return count;
    }

}
