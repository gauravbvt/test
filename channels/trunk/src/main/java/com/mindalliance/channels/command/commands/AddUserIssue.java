package com.mindalliance.channels.command.commands;

import com.mindalliance.channels.Commander;
import com.mindalliance.channels.QueryService;
import com.mindalliance.channels.command.AbstractCommand;
import com.mindalliance.channels.command.Change;
import com.mindalliance.channels.command.Command;
import com.mindalliance.channels.command.CommandException;
import com.mindalliance.channels.command.CommandUtils;
import com.mindalliance.channels.model.ModelObject;
import com.mindalliance.channels.model.User;
import com.mindalliance.channels.model.UserIssue;

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

    public AddUserIssue() {
    }

    public AddUserIssue( ModelObject modelObject ) {
        set( "modelObject", modelObject.getId() );
    }

    /**
     * {@inheritDoc}
     */
    public String getName() {
        return "add issue";
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings( "unchecked" )
    public Change execute( Commander commander ) throws CommandException {
        QueryService queryService = commander.getQueryService();
        Long priorId = (Long) get( "issue" );
        UserIssue issue = new UserIssue( commander.resolve(
                ModelObject.class,
                (Long) get( "modelObject" ) ) );
        // State is set when undoing a RemoveIssue
        Map<String, Object> state = (Map<String, Object>) get( "state" );
        issue.setReportedBy( User.current().getName() );
        if ( state != null ) {
            CommandUtils.initialize( issue, state );
            commander.getAttachmentManager().reattachAll( issue.getAttachmentTickets() );
        }
        queryService.add( issue, priorId );
        set( "issue", issue.getId() );
        return new Change( Change.Type.Added, issue );

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
    protected Command makeUndoCommand( Commander commander ) throws CommandException {
        UserIssue issue = commander.resolve( UserIssue.class, (Long) get( "issue" ) );
        return new RemoveIssue( issue );
    }

}
