package com.mindalliance.channels.command.commands;

import com.mindalliance.channels.AbstractChannelsTest;
import com.mindalliance.channels.command.Change;
import com.mindalliance.channels.command.Commander;
import com.mindalliance.channels.model.Segment;
import com.mindalliance.channels.query.QueryService;
import org.junit.Assert;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

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
    public void setUp() throws IOException {
        super.setUp();
        segment = queryService.getDefaultSegment();
        command = new UpdatePlanObject( segment, "description", "ipso lorem etc." );
        getCommander().reset();
    }

    @Test
    public void testCommand() throws Exception {
        Commander commander = getCommander();
        QueryService queryService = commander.getQueryService();
        assertTrue( commander.canDo( command ) );
        String description = segment.getDescription();
        Change change = commander.doCommand( command );
        assertTrue( change.isUpdated() );
        assertEquals( "ipso lorem etc.", change.getChangedPropertyValue( queryService ) );
        String newDescription = segment.getDescription();
        assertFalse( description.equals( newDescription ) );
        assertTrue( commander.canUndo() );
        change = commander.undo();
        assertTrue( change.isUpdated() );
        assertEquals( change.getChangedPropertyValue( queryService ), description );
        newDescription = segment.getDescription();
        Assert.assertEquals( description, newDescription );
        assertTrue( commander.canRedo() );
        change = commander.redo();
        assertTrue( change.isUpdated() );
        assertEquals( "ipso lorem etc.", change.getChangedPropertyValue( queryService ) );
        newDescription = segment.getDescription();
        assertFalse( description.equals( newDescription ) );
    }
}
