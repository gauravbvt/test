package com.mindalliance.channels.command.commands;

import com.mindalliance.channels.AbstractChannelsTest;
import com.mindalliance.channels.command.Change;
import com.mindalliance.channels.model.Segment;

import java.io.IOException;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Mar 5, 2009
 * Time: 12:49:12 PM
 */
public class TestUpdatePlanObject extends AbstractChannelsTest {

    private UpdatePlanObject command;
    private Segment segment;

    @Override
    protected void setUp() throws IOException {
        super.setUp();
        segment = app.getQueryService().getDefaultSegment();
        command = new UpdatePlanObject( segment, "description", "ipso lorem etc." );
        commander.reset();
    }

    public void testCommand() throws Exception {
        assertTrue( commander.canDo( command ) );
        String description = segment.getDescription();
        Change change = commander.doCommand( command );
        assertTrue( change.isUpdated() );
        assertTrue( change.getChangedPropertyValue().equals( "ipso lorem etc." ) );
        String newDescription = segment.getDescription();
        assertFalse( description.equals( newDescription ) );
        assertTrue( commander.canUndo() );
        change = commander.undo();
        assertTrue( change.isUpdated() );
        assertTrue( change.getChangedPropertyValue().equals( description ) );
        newDescription = segment.getDescription();
        assertTrue( description.equals( newDescription ) );
        assertTrue( commander.canRedo() );
        change = commander.redo();
        assertTrue( change.isUpdated() );
        assertTrue( change.getChangedPropertyValue().equals( "ipso lorem etc." ) );
        newDescription = segment.getDescription();
        assertFalse( description.equals( newDescription ) );
    }

}
