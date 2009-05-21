package com.mindalliance.channels.command.commands;

import com.mindalliance.channels.Commander;
import com.mindalliance.channels.QueryService;
import com.mindalliance.channels.command.AbstractCommand;
import com.mindalliance.channels.command.Change;
import com.mindalliance.channels.command.Command;
import com.mindalliance.channels.command.CommandException;
import com.mindalliance.channels.model.ModelObject;
import com.mindalliance.channels.model.UserIssue;

import java.util.HashMap;
import java.util.Map;

/**
 * Remove issue command.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Mar 9, 2009
 * Time: 8:01:50 PM
 */
public class RemoveIssue extends AbstractCommand {

    public RemoveIssue() {
    }

    public RemoveIssue( UserIssue issue ) {
        addConflicting( issue );
        set( "issue", issue.getId() );
    }

    /**
     * {@inheritDoc}
     */
    public String getName() {
        return "remove issue";
    }

    /**
     * {@inheritDoc}
     */
    public Change execute( Commander commander ) throws CommandException {
        QueryService queryService = commander.getQueryService();
        UserIssue issue = commander.resolve( UserIssue.class, (Long) get( "issue" ) );
        set( "modelObject", issue.getAbout().getId() );
        Map<String, Object> state = new HashMap<String, Object>();
        state.put( "description", issue.getDescription() );
        state.put( "remediation", issue.getRemediation() );
        state.put( "severity", issue.getSeverity() );
        state.put( "reportedBy", issue.getReportedBy() );
        set( "state", state );
        queryService.remove( issue );
        commander.releaseAnyLockOn( issue );
        return new Change( Change.Type.Removed, issue );
    }

    /**
     * {@inheritDoc}
     */
    public boolean isUndoable() {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Command doMakeUndoCommand( Commander commander ) throws CommandException {
        ModelObject modelObject = commander.resolve(
                ModelObject.class,
                (Long) get( "modelObject" ) );
        AddUserIssue addIssue = new AddUserIssue( modelObject );
        addIssue.set( "issue", get( "issue" ) );
        addIssue.set( "state", get( "state" ) );
        return addIssue;
    }
}
