package com.mindalliance.channels.core.command.commands;

import com.mindalliance.channels.AbstractChannelsTest;
import com.mindalliance.channels.core.command.Change;
import com.mindalliance.channels.core.command.Command;
import com.mindalliance.channels.core.dao.User;
import com.mindalliance.channels.core.model.Segment;
import org.junit.After;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
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
    public void testAddRemoveRestore() {
        int count = countSegments();
        Command command = new AddSegment( User.current().getUsername() );
        assertTrue( getCommander().canDo( command ) );
        Change change = getCommander().doCommand( command );
        assertTrue( change.isAdded() );
        segment = (Segment) change.getSubject( getCommander().getQueryService() );
        assertSame( count + 1, countSegments() );
        assertFalse( getCommander().canUndo( User.current().getUsername() ) );
    }

    @SuppressWarnings( {"UnusedDeclaration"} )
    private int countSegments() {
        int count = 0;
        List<Segment> segments = queryService.list( Segment.class );
        for ( Segment sc : segments ) count++;
        return count;
    }


}
