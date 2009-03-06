package com.mindalliance.channels.command.commands;

import com.mindalliance.channels.AbstractChannelsTest;
import com.mindalliance.channels.Scenario;
import com.mindalliance.channels.command.Commander;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Mar 5, 2009
 * Time: 12:49:12 PM
 */
public class TestUpdateProjectObject extends AbstractChannelsTest {

    private UpdateProjectObject command;
    private Commander  commander;
    private Scenario scenario;

    protected void setUp() {
         super.setUp();
         commander = project.getCommander();
         scenario = project.getService().getDefaultScenario();
         command = new UpdateProjectObject( scenario, "description", "ipso lorem etc." );
         commander.reset();
     }

       public void testCommand() throws Exception {
        assertTrue( commander.canDo( command ) );
        String description = scenario.getDescription();
        commander.doCommand( command );
        String newDescription = scenario.getDescription();
        assertFalse( description.equals( newDescription ) );
        assertTrue( commander.canUndo() );
        commander.undo();
        newDescription = scenario.getDescription();
        assertTrue( description.equals( newDescription ) );
        assertTrue( commander.canRedo() );
        commander.redo();
        newDescription = scenario.getDescription();
        assertFalse( description.equals( newDescription ) );
    }

}
