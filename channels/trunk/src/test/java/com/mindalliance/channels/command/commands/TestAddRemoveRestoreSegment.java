package com.mindalliance.channels.command.commands;

import com.mindalliance.channels.AbstractChannelsTest;
import com.mindalliance.channels.command.Change;
import com.mindalliance.channels.command.Command;
import com.mindalliance.channels.model.Segment;
import org.junit.After;
import static org.junit.Assert.*;
import org.junit.Test;

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

    @After
    public void cleanup() {
        if ( segment != null ) queryService.remove( segment );
    }

    @Test
    public void testAddRemoveRestore() throws Exception {
        int count = countSegments();
        Command command = new AddSegment();
        assertTrue( getCommander().canDo( command ) );
        Change change = getCommander().doCommand( command );
        assertTrue( change.isAdded() );
        segment = (Segment) change.getSubject();
        assertSame( count + 1, countSegments() );
        assertFalse( getCommander().canUndo() );
    }

    @SuppressWarnings( {"UnusedDeclaration"} )
    private int countSegments() {
        int count = 0;
        List<Segment> segments = queryService.list( Segment.class );
        for ( Segment sc : segments ) count++;
        return count;
    }


}
