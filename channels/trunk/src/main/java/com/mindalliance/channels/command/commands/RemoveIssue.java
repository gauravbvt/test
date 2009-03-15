package com.mindalliance.channels.command.commands;

import com.mindalliance.channels.command.AbstractCommand;
import com.mindalliance.channels.command.Command;
import com.mindalliance.channels.command.Commander;
import com.mindalliance.channels.command.CommandException;
import com.mindalliance.channels.command.Change;
import com.mindalliance.channels.UserIssue;
import com.mindalliance.channels.Service;
import com.mindalliance.channels.NotFoundException;
import com.mindalliance.channels.ModelObject;

import java.util.HashMap;

/**
 * Remove issue command.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Mar 9, 2009
 * Time: 8:01:50 PM
 */
public class RemoveIssue extends AbstractCommand {

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
        Service service = commander.getService();
        try {
            final UserIssue issue = service.find( UserIssue.class, (Long) get( "issue" ) );
            addArgument( "modelObject", issue.getAbout().getId() );
            addArgument( "state", new HashMap<String, Object>() {
                {
                    put( "description", issue.getDescription() );
                    put( "remediation", issue.getRemediation() );
                    put( "severity", issue.getSeverity() );
                    put( "reportedBy", issue.getReportedBy() );
                }
            } );
            service.remove( issue );
            return new Change( Change.Type.Removed, issue );
        } catch ( NotFoundException e ) {
            throw new CommandException( "You need to refresh.", e );
        }
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
    protected Command doMakeUndoCommand( Commander commander ) throws CommandException {
        Service service = commander.getService();
        try {
            ModelObject modelObject = service.find(
                    ModelObject.class,
                    (Long) get( "modelObject" ) );
            AddUserIssue addIssue = new AddUserIssue( modelObject );
            addIssue.addArgument( "state", get( "state" ) );
            return addIssue;
        } catch ( NotFoundException e ) {
            throw new CommandException( "You need to refresh.", e );
        }
    }

}
