package com.mindalliance.channels.command.commands;

import com.mindalliance.channels.AbstractChannelsTest;
import com.mindalliance.channels.Scenario;
import com.mindalliance.channels.Service;
import com.mindalliance.channels.ModelObject;
import com.mindalliance.channels.UserIssue;
import com.mindalliance.channels.Issue;
import com.mindalliance.channels.analysis.Analyst;
import com.mindalliance.channels.command.Commander;
import com.mindalliance.channels.command.Command;
import com.mindalliance.channels.command.CommandException;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Mar 9, 2009
 * Time: 8:27:16 PM
 */
public class TestAddRemoveIssue extends AbstractChannelsTest {

    private Commander commander;
    private Scenario scenario;
    private Service service;
    private Analyst analyst;

    protected void setUp() {
        super.setUp();
        service = project.getService();
        commander = project.getCommander();
        analyst = project.getAnalyst();
        scenario = service.createScenario();
     }

    protected void tearDown() {
        service.remove( scenario );
    }

    public void testAddRemoveIssue() throws CommandException {
        int count = countIssues( scenario );
        Command addIssue = new AddIssue( scenario );
        assertTrue( commander.canDo( addIssue ) );
        UserIssue issue = (UserIssue) commander.doCommand( addIssue );
        assertTrue( countIssues( scenario ) == count + 1);
        issue.setDescription("fubar");
        assertTrue( commander.canUndo());
        commander.undo();
        assertTrue( countIssues( scenario ) == count );
        assertTrue( commander.canRedo());
        commander.redo();
        assertTrue( countIssues( scenario ) == count + 1 );
        boolean found = false;
        for ( Issue i : analyst.listIssues( scenario, false )) {
            found = found || i.getDescription().equals("fubar");
        }
        assertTrue( found );
    }

    private int countIssues( ModelObject mo) {
        return analyst.listIssues( mo, false ).size();
    }
}
