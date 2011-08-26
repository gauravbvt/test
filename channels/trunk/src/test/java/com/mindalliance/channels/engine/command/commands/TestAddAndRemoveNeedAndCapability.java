package com.mindalliance.channels.engine.command.commands;

import com.mindalliance.channels.AbstractChannelsTest;
import com.mindalliance.channels.engine.command.Change;
import com.mindalliance.channels.engine.command.Commander;
import com.mindalliance.channels.core.model.Flow;
import com.mindalliance.channels.core.model.Part;
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
 * Date: Mar 9, 2009
 * Time: 12:46:08 PM
 */
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
        Change change = commander.doCommand( new AddNeed( part ) );
        Flow need = (Flow) change.getSubject( commander.getQueryService() );
        assertTrue( change.isAdded() );
        assertSame( part, need.getTarget() );
        assertSame( 1, countNeeds( part ) );
        assertTrue( commander.canUndo() );
        assertTrue( commander.undo().isRemoved() );
        assertSame( 0, countNeeds( part ) );
        assertTrue( commander.canRedo() );
        assertTrue( commander.redo().isAdded() );
        assertSame( 1, countNeeds( part ) );
    }

    @Test
    public void testAddRemoveCapability() {
        Commander commander = getCommander();
        Change change = commander.doCommand( new AddCapability( part ) );
        assertTrue( change.isAdded() );
        Flow capability = (Flow) change.getSubject( commander.getQueryService() );
        assertSame( part, capability.getSource() );
        assertSame( 1, countCapabilities( part ) );
        assertTrue( commander.canUndo() );
        assertTrue( commander.undo().isRemoved() );
        assertSame( 0, countCapabilities( part ) );
        assertTrue( commander.canRedo() );
        assertTrue( commander.redo().isAdded() );
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
