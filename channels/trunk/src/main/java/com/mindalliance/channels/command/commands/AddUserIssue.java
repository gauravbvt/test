package com.mindalliance.channels.command.commands;

import com.mindalliance.channels.command.AbstractCommand;
import com.mindalliance.channels.command.Command;
import com.mindalliance.channels.command.Commander;
import com.mindalliance.channels.command.CommandException;
import com.mindalliance.channels.command.CommandUtils;
import com.mindalliance.channels.command.Change;
import com.mindalliance.channels.ModelObject;
import com.mindalliance.channels.UserIssue;
import com.mindalliance.channels.Service;
import com.mindalliance.channels.NotFoundException;
import com.mindalliance.channels.pages.Project;

import java.util.Map;

/**
 * Adds a user issue to a model object.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Mar 9, 2009
 * Time: 7:44:31 PM
 */
public class AddUserIssue extends AbstractCommand {

    public AddUserIssue( ModelObject modelObject ) {
        addArgument( "modelObject", modelObject.getId() );
    }

    /**
     * {@inheritDoc
     */
    public String getName() {
        return "add issue";
    }

    /**
     * {@inheritDoc
     */
    @SuppressWarnings( "unchecked" )
    public Change execute( Commander commander ) throws CommandException {
        Service service = commander.getService();
        try {
            UserIssue issue = new UserIssue( service.find( ModelObject.class, (Long) get( "modelObject" ) ) );
            Map<String, Object> state = (Map<String, Object>) get( "state" );
            issue.setReportedBy( Project.getUserName() );
            if ( state != null ) {
                CommandUtils.initialize( issue, state );
            }
            service.add( issue );
            addArgument( "issue", issue.getId() );
            return new Change( Change.Type.Added, issue );
        } catch ( NotFoundException e ) {
            throw new CommandException( "You need to refresh.", e );
        }
    }

    /**
     * {@inheritDoc
     */
    public boolean isUndoable() {
        return true;
    }

    /**
     * {@inheritDoc
     */
    protected Command doMakeUndoCommand( Commander commander ) throws CommandException {
        Service service = commander.getService();
        try {
            UserIssue issue = service.find( UserIssue.class, (Long) get( "issue" ) );
            return new RemoveIssue( issue );
        } catch ( NotFoundException e ) {
            throw new CommandException( "You need to refresh.", e );
        }
    }

}
