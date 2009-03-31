package com.mindalliance.channels.command.commands;

import com.mindalliance.channels.ModelObject;
import com.mindalliance.channels.DataQueryObject;
import com.mindalliance.channels.UserIssue;
import com.mindalliance.channels.command.AbstractCommand;
import com.mindalliance.channels.command.Change;
import com.mindalliance.channels.command.Command;
import com.mindalliance.channels.command.CommandException;
import com.mindalliance.channels.command.Commander;

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
        addArgument( "issue", issue.getId() );
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
        DataQueryObject dqo = commander.getDqo();

        UserIssue issue = commander.resolve( UserIssue.class, (Long) get( "issue" ) );
        addArgument( "modelObject", issue.getAbout().getId() );
        Map<String, Object> state = new HashMap<String, Object>();
        state.put( "description", issue.getDescription() );
        state.put( "remediation", issue.getRemediation() );
        state.put( "severity", issue.getSeverity() );
        state.put( "reportedBy", issue.getReportedBy() );
        addArgument( "state", state );
        dqo.remove( issue );
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
        addIssue.addArgument( "state", get( "state" ) );
        return addIssue;
    }
}
