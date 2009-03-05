package com.mindalliance.channels.command.commands;

import com.mindalliance.channels.AbstractChannelsTest;
import com.mindalliance.channels.Scenario;
import com.mindalliance.channels.Part;
import com.mindalliance.channels.command.Commander;

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
        commander = project.getCommander();
        Scenario scenario = project.getService().getDefaultScenario();
        part = scenario.getDefaultPart();
        command = new UpdateScenarioObject( part, "description", "ipso lorem etc." );
        commander.reset();
    }

    public void testCommand() throws Exception {
        assertTrue( commander.canDo( command ) );
        String description = part.getDescription();
        commander.doCommand( command );
        String newDescription = part.getDescription();
        assertFalse( description.equals( newDescription ) );
        assertTrue( commander.canUndo() );
        commander.undo();
        newDescription = part.getDescription();
        assertTrue( description.equals( newDescription ) );
        assertTrue( commander.canRedo() );
        commander.redo();
        newDescription = part.getDescription();
        assertFalse( description.equals( newDescription ) );
    }
}
