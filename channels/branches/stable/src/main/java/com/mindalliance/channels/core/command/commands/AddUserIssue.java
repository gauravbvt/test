/*
 * Copyright (C) 2011 Mind-Alliance Systems LLC.
 * All rights reserved.
 * Proprietary and Confidential.
 */

package com.mindalliance.channels.core.command.commands;

import com.mindalliance.channels.core.command.AbstractCommand;
import com.mindalliance.channels.core.command.Change;
import com.mindalliance.channels.core.command.Change.Type;
import com.mindalliance.channels.core.command.Command;
import com.mindalliance.channels.core.command.CommandException;
import com.mindalliance.channels.core.command.Commander;
import com.mindalliance.channels.core.model.ModelObject;
import com.mindalliance.channels.core.model.UserIssue;
import com.mindalliance.channels.core.query.QueryService;

import java.util.Map;

/**
 * Adds a user issue to a model object.
 */
public class AddUserIssue extends AbstractCommand {

    public AddUserIssue() {
        super( "daemon" );
    }

    public AddUserIssue( String userName, ModelObject modelObject ) {
        super( userName );
        addConflicting( modelObject );
        set( "modelObject", modelObject.getId() );
    }

    public AddUserIssue( String userName, long aboutId ) {
        super( userName );
        addConflicting( aboutId );
        set( "modelObject", aboutId );
    }

    @Override
    public String getName() {
        return "new issue";
    }

    @Override
    @SuppressWarnings( "unchecked" )
    public Change execute( Commander commander ) throws CommandException {
        QueryService queryService = commander.getQueryService();
        Long priorId = (Long) get( "issue" );
        UserIssue issue = new UserIssue( commander.resolve(
                ModelObject.class,
                (Long) get( "modelObject" ) ) );
        // State is set when undoing a RemoveIssue
        Map<String, Object> state = (Map<String, Object>) get( "state" );
        issue.setReportedBy( getUserName() );
        if ( state != null )
            issue.initFromMap( state, queryService );
        queryService.add( issue, priorId );
        set( "issue", issue.getId() );
        describeTarget( issue );                
        return new Change( Type.Added, issue );

    }

    @Override
    public boolean isUndoable() {
        return true;
    }

    @Override
    protected Command makeUndoCommand( Commander commander ) throws CommandException {
        UserIssue issue = commander.resolve( UserIssue.class, (Long) get( "issue" ) );
        return new RemoveIssue( getUserName(), issue );
    }

}
