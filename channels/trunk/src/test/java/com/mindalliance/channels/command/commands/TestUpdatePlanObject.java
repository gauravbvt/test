package com.mindalliance.channels.command.commands;

import com.mindalliance.channels.AbstractChannelsTest;
import com.mindalliance.channels.model.Scenario;
import com.mindalliance.channels.Commander;
import com.mindalliance.channels.command.Change;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Mar 5, 2009
 * Time: 12:49:12 PM
 */
public class TestUpdatePlanObject extends AbstractChannelsTest {

    private UpdatePlanObject command;
    private Commander commander;
    private Scenario scenario;

    protected void setUp() {
        super.setUp();
        commander = app.getCommander();
        scenario = app.getQueryService().getDefaultScenario();
        command = new UpdatePlanObject( scenario, "description", "ipso lorem etc." );
        commander.reset();
    }

    public void testCommand() throws Exception {
        assertTrue( commander.canDo( command ) );
        String description = scenario.getDescription();
        Change change = commander.doCommand( command );
        assertTrue( change.isUpdated() );
        assertTrue( change.getChangedPropertyValue().equals( "ipso lorem etc." ) );
        String newDescription = scenario.getDescription();
        assertFalse( description.equals( newDescription ) );
        assertTrue( commander.canUndo() );
        change = commander.undo();
        assertTrue( change.isUpdated() );
        assertTrue( change.getChangedPropertyValue().equals( description ) );
        newDescription = scenario.getDescription();
        assertTrue( description.equals( newDescription ) );
        assertTrue( commander.canRedo() );
        change = commander.redo();
        assertTrue( change.isUpdated() );
        assertTrue( change.getChangedPropertyValue().equals( "ipso lorem etc." ) );
        newDescription = scenario.getDescription();
        assertFalse( description.equals( newDescription ) );
    }

}
