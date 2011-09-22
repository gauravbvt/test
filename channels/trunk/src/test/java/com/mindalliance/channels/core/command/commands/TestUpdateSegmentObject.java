package com.mindalliance.channels.core.command.commands;

import com.mindalliance.channels.AbstractChannelsTest;
import com.mindalliance.channels.core.command.Change;
import com.mindalliance.channels.core.command.Commander;
import com.mindalliance.channels.core.dao.User;
import com.mindalliance.channels.core.model.Part;
import com.mindalliance.channels.core.model.Segment;
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
        command = new UpdateSegmentObject( User.current().getUsername(), part, "description", "ipso lorem etc." );
        getCommander().reset();
    }

    @Test
    public void testCommand() throws Exception {
        Commander commander = getCommander();

        assertTrue( commander.canDo( command ) );
        String description = part.getDescription();
        Change change = commander.doCommand( command );
        assertTrue( change.isUpdated() );
        assertEquals( "ipso lorem etc.", change.getChangedPropertyValue( queryService ) );
        String newDescription = part.getDescription();
        assertFalse( description.equals( newDescription ) );
        assertTrue( commander.canUndo( User.current().getUsername() ) );
        change = commander.undo( User.current().getUsername() );
        assertTrue( change.isUpdated() );
        assertEquals( description, change.getChangedPropertyValue( queryService ) );
        newDescription = part.getDescription();
        assertEquals( description, newDescription );
        assertTrue( commander.canRedo( User.current().getUsername() ) );
        change = commander.redo( User.current().getUsername() );
        assertTrue( change.isUpdated() );
        assertEquals( "ipso lorem etc.", change.getChangedPropertyValue( queryService ) );
        newDescription = part.getDescription();
        assertFalse( description.equals( newDescription ) );
    }
}
