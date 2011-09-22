/*
 * Copyright (C) 2011 Mind-Alliance Systems LLC.
 * All rights reserved.
 * Proprietary and Confidential.
 */

package com.mindalliance.channels.core.command.commands;

import com.mindalliance.channels.AbstractChannelsTest;
import com.mindalliance.channels.core.command.Change;
import com.mindalliance.channels.core.command.Commander;
import com.mindalliance.channels.core.dao.User;
import com.mindalliance.channels.core.model.Flow;
import com.mindalliance.channels.core.model.Part;
import com.mindalliance.channels.core.model.Segment;
import org.junit.After;
import org.junit.Test;

import java.io.IOException;
import java.util.Iterator;

import static org.junit.Assert.*;

public class TestAddAndRemoveNeedAndCapability extends AbstractChannelsTest {

    private Segment segment;
    private Part part;

    @Override
    public void setUp() throws IOException {
        super.setUp();
        segment = queryService.createSegment();
        part = segment.getDefaultPart();
    }

    @Override
    @After
    public void tearDown() {
        queryService.remove( segment );
        super.tearDown();
    }

    @Test
    public void testAddRemoveNeed() {
        Commander commander = getCommander();
        Change change = commander.doCommand( new AddNeed( User.current().getUsername(), part ) );
        Flow need = (Flow) change.getSubject( commander.getQueryService() );
        assertTrue( change.isAdded() );
        assertSame( part, need.getTarget() );
        assertSame( 1, countNeeds( part ) );
        assertTrue( commander.canUndo( User.current().getUsername() ) );
        assertTrue( commander.undo( User.current().getUsername() ).isRemoved() );
        assertSame( 0, countNeeds( part ) );
        assertTrue( commander.canRedo( User.current().getUsername() ) );
        assertTrue( commander.redo( User.current().getUsername() ).isAdded() );
        assertSame( 1, countNeeds( part ) );
    }

    @Test
    public void testAddRemoveCapability() {
        Commander commander = getCommander();
        Change change = commander.doCommand( new AddCapability( User.current().getUsername(), part ) );
        assertTrue( change.isAdded() );
        Flow capability = (Flow) change.getSubject( commander.getQueryService() );
        assertSame( part, capability.getSource() );
        assertSame( 1, countCapabilities( part ) );
        assertTrue( commander.canUndo( User.current().getUsername() ) );
        assertTrue( commander.undo( User.current().getUsername() ).isRemoved() );
        assertSame( 0, countCapabilities( part ) );
        assertTrue( commander.canRedo( User.current().getUsername() ) );
        assertTrue( commander.redo( User.current().getUsername() ).isAdded() );
        assertSame( 1, countCapabilities( part ) );
    }

    private int countNeeds( Part part ) {
        int count = 0;
        Iterator<Flow> needs = part.receives();
        while ( needs.hasNext() ) {
            needs.next();
            count++;
        }
        return count;
    }

    private int countCapabilities( Part part ) {
        int count = 0;
        Iterator<Flow> sends = part.sends();
        while ( sends.hasNext() ) {
            sends.next();
            count++;
        }
        return count;
    }
}
