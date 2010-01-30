package com.mindalliance.channels.command.commands;

import com.mindalliance.channels.AbstractChannelsTest;
import com.mindalliance.channels.QueryService;
import com.mindalliance.channels.command.Change;
import com.mindalliance.channels.command.Command;
import com.mindalliance.channels.model.Segment;

import java.io.IOException;
import java.util.List;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Mar 9, 2009
 * Time: 1:07:55 PM
 */
public class TestAddRemoveRestoreSegment extends AbstractChannelsTest {

    private Segment segment;
    private QueryService queryService;

    protected void setUp() throws IOException {
        super.setUp();
        queryService = app.getQueryService();
    }

    protected void tearDown() {
        if ( segment != null ) queryService.remove( segment );
    }

    public void testAddRemoveRestore() throws Exception {
        int count = countSegments();
        Command command = new AddSegment();
        assertTrue( commander.canDo( command ) );
        Change change = commander.doCommand( command );
        assertTrue( change.isAdded() );
        segment = (Segment) change.getSubject();
        assertTrue( countSegments() == count + 1 );
        assertFalse( commander.canUndo() );
    }

    @SuppressWarnings( {"UnusedDeclaration"} )
    private int countSegments() {
        int count = 0;
        List<Segment> segments = queryService.list( Segment.class );
        for ( Segment sc : segments ) count++;
        return count;
    }


}
