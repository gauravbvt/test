package com.mindalliance.channels.command.commands;

import com.mindalliance.channels.AbstractChannelsTest;
import com.mindalliance.channels.model.Scenario;
import com.mindalliance.channels.model.Part;
import com.mindalliance.channels.Commander;
import com.mindalliance.channels.command.Change;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Mar 4, 2009
 * Time: 7:32:44 PM
 */
public class TestUpdateScenarioObject extends AbstractChannelsTest {

    private UpdateScenarioObject command;
    private Commander commander;
    private Part part;

    protected void setUp() {
        super.setUp();
        commander = app.getCommander();
        Scenario scenario = app.getQueryService().getDefaultScenario();
        part = scenario.getDefaultPart();
        command = new UpdateScenarioObject( part, "description", "ipso lorem etc." );
        commander.reset();
    }

    public void testCommand() throws Exception {
        assertTrue( commander.canDo( command ) );
        String description = part.getDescription();
        Change change = commander.doCommand( command );
        assertTrue( change.isUpdated() );
        assertTrue( change.getChangedPropertyValue().equals( "ipso lorem etc." ) );
        String newDescription = part.getDescription();
        assertFalse( description.equals( newDescription ) );
        assertTrue( commander.canUndo() );
        change = commander.undo();
        assertTrue( change.isUpdated() );
        assertTrue( change.getChangedPropertyValue().equals( description ) );
        newDescription = part.getDescription();
        assertTrue( description.equals( newDescription ) );
        assertTrue( commander.canRedo() );
        change = commander.redo();
        assertTrue( change.isUpdated() );
        assertTrue( change.getChangedPropertyValue().equals( "ipso lorem etc." ) );
        newDescription = part.getDescription();
        assertFalse( description.equals( newDescription ) );
    }
}
