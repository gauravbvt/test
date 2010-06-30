package com.mindalliance.channels.command.commands;

import com.mindalliance.channels.AbstractChannelsTest;
import com.mindalliance.channels.command.Change;
import com.mindalliance.channels.command.Commander;
import com.mindalliance.channels.model.Part;
import com.mindalliance.channels.model.Segment;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

import java.io.IOException;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Mar 4, 2009
 * Time: 7:32:44 PM
 */
public class TestUpdateSegmentObject extends AbstractChannelsTest {

    private UpdateSegmentObject command;
    private Part part;

    @Override
    public void setUp() throws IOException {
        super.setUp();

        Segment segment = queryService.getDefaultSegment();
        part = segment.getDefaultPart();
        command = new UpdateSegmentObject( part, "description", "ipso lorem etc." );
        getCommander().reset();
    }

    @Test
    public void testCommand() {
        Commander commander = getCommander();

        assertTrue( commander.canDo( command ) );
        String description = part.getDescription();
        Change change = commander.doCommand( command );
        assertTrue( change.isUpdated() );
        assertEquals( "ipso lorem etc.", change.getChangedPropertyValue( queryService ) );
        String newDescription = part.getDescription();
        assertFalse( description.equals( newDescription ) );
        assertTrue( commander.canUndo() );
        change = commander.undo();
        assertTrue( change.isUpdated() );
        assertEquals( description, change.getChangedPropertyValue( queryService ) );
        newDescription = part.getDescription();
        assertEquals( description, newDescription );
        assertTrue( commander.canRedo() );
        change = commander.redo();
        assertTrue( change.isUpdated() );
        assertEquals( "ipso lorem etc.", change.getChangedPropertyValue( queryService ) );
        newDescription = part.getDescription();
        assertFalse( description.equals( newDescription ) );
    }
}
