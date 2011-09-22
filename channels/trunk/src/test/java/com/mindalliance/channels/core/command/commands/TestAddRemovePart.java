package com.mindalliance.channels.core.command.commands;

import com.mindalliance.channels.AbstractChannelsTest;
import com.mindalliance.channels.core.command.Change;
import com.mindalliance.channels.core.dao.User;
import com.mindalliance.channels.core.model.Flow;
import com.mindalliance.channels.core.model.Part;
import com.mindalliance.channels.core.model.Segment;
import org.junit.After;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

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

    @Override
    public void setUp() throws IOException {
        super.setUp();
        segment = queryService.createSegment();
        part = segment.getDefaultPart();
        Part other = queryService.createPart( segment );
        queryService.connect( part, other, "foo" );
        queryService.connect( queryService.createConnector( segment ), part, "bar" );
    }

    @After
    public void cleanup() {
        queryService.remove( segment );
    }

    @Test
    public void testRemoveAddPart() {
        assertSame( 2, countParts() );
        assertSame( 2, countFlows() );

        RemovePart removePart = new RemovePart( User.current().getUsername(), part );
        assertTrue( getCommander().canDo( removePart ) );
        Change change = getCommander().doCommand( removePart );
        assertTrue( change.isRecomposed() );
        assertTrue( change.getSubject( getCommander().getQueryService() ) instanceof Segment );
        assertSame( 1, countParts() );
        assertSame( 1, countFlows() );

        assertTrue( getCommander().canUndo( User.current().getUsername() ) );
        assertTrue( getCommander().undo( User.current().getUsername() ).isUnknown() );
        assertSame( 2, countParts() );
        assertSame( 2, countFlows() );

        assertFalse( getCommander().canUndo( User.current().getUsername() ) );
        assertTrue( getCommander().canRedo( User.current().getUsername() ) );
        assertTrue( getCommander().redo( User.current().getUsername() ).isUnknown() );
        assertSame( 1, countParts() );
        assertSame( 1, countFlows() );

        assertTrue( getCommander().canUndo( User.current().getUsername() ) );
        getCommander().undo( User.current().getUsername() );
        assertSame( 2, countParts() );
        assertSame( 2, countFlows() );

        assertTrue( getCommander().canRedo( User.current().getUsername() ) );
        getCommander().redo( User.current().getUsername() );
        assertSame( 1, countParts() );
        assertSame( 1, countFlows() );
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
