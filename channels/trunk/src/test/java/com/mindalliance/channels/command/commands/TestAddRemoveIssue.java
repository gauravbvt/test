package com.mindalliance.channels.command.commands;

import com.mindalliance.channels.AbstractChannelsTest;
import com.mindalliance.channels.Analyst;
import com.mindalliance.channels.QueryService;
import com.mindalliance.channels.command.Change;
import com.mindalliance.channels.command.Command;
import com.mindalliance.channels.model.Issue;
import com.mindalliance.channels.model.ModelObject;
import com.mindalliance.channels.model.Segment;
import com.mindalliance.channels.model.UserIssue;

import java.io.IOException;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Mar 9, 2009
 * Time: 8:27:16 PM
 */
public class TestAddRemoveIssue extends AbstractChannelsTest {

    private Segment segment;
    private QueryService queryService;
    private Analyst analyst;

    @Override
    protected void setUp() throws IOException {
        super.setUp();
        queryService = app.getQueryService();
        analyst = app.getAnalyst();
        segment = queryService.createSegment();
    }

    @Override
    protected void tearDown() throws Exception {
        queryService.remove( segment );
        super.tearDown();
    }

    public void testAddRemoveIssue() throws Exception {
        int count = countIssues( segment );
        Command addIssue = new AddUserIssue( segment );
        assertTrue( commander.canDo( addIssue ) );
        Change change = commander.doCommand( addIssue );
        assertTrue( change.isAdded() );
        UserIssue issue = (UserIssue) change.getSubject();
        assertTrue( countIssues( segment ) == count + 1 );
        issue.setDescription( "fubar" );
        assertTrue( commander.canUndo() );
        assertTrue( commander.undo().isRemoved() );
        assertTrue( countIssues( segment ) == count );
        assertTrue( commander.canRedo() );
        assertTrue( commander.redo().isAdded() );
        assertTrue( countIssues( segment ) == count + 1 );
        boolean found = false;
        for ( Issue i : analyst.listIssues( segment, false ) ) {
            found = found || i.getDescription().equals( "fubar" );
        }
        assertTrue( found );
    }

    private int countIssues( ModelObject mo ) {
        return analyst.listIssues( mo, false ).size();
    }
}
