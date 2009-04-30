package com.mindalliance.channels.command.commands;

import com.mindalliance.channels.model.ModelObject;
import com.mindalliance.channels.model.UserIssue;
import com.mindalliance.channels.DataQueryObject;
import com.mindalliance.channels.command.AbstractCommand;
import com.mindalliance.channels.command.Change;
import com.mindalliance.channels.command.Command;
import com.mindalliance.channels.command.CommandException;
import com.mindalliance.channels.command.CommandUtils;
import com.mindalliance.channels.command.Commander;
import com.mindalliance.channels.Channels;

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
        DataQueryObject dqo = commander.getDqo();

        UserIssue issue = new UserIssue( commander.resolve(
                ModelObject.class,
                (Long) get( "modelObject" ) ) );
        Map<String, Object> state = (Map<String, Object>) get( "state" );
        issue.setReportedBy( Channels.getUserName() );
        if ( state != null ) {
            CommandUtils.initialize( issue, state );
        }
        dqo.add( issue );
        if ( get( "issue" ) != null )
            commander.mapId( (Long) get( "issue" ), issue.getId() );
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
    protected Command doMakeUndoCommand( Commander commander ) throws CommandException {
        UserIssue issue = commander.resolve( UserIssue.class, (Long) get( "issue" ) );
        return new RemoveIssue( issue );
    }

}
