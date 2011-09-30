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
import com.mindalliance.channels.core.model.UserIssue;
import com.mindalliance.channels.core.query.QueryService;

/**
 * Remove issue command.
 */
public class RemoveIssue extends AbstractCommand {

    public RemoveIssue() {
        super( "daemon" );
    }

    public RemoveIssue( String userName, UserIssue issue ) {
        super( userName );
        needLockOn( issue );
        needLockOn( issue.getAbout() );
        set( "issue", issue.getId() );
        set( "modelObject", issue.getAbout().getId() );
        set( "state", issue.mapState() );
    }

    @Override
    public String getName() {
        return "remove issue";
    }

    @Override
    public Change execute( Commander commander ) throws CommandException {
        QueryService queryService = commander.getQueryService();
        UserIssue issue = commander.resolve( UserIssue.class, (Long) get( "issue" ) );
        describeTarget( issue );
        queryService.remove( issue );
        releaseAnyLockOn( commander, issue );
        return new Change( Type.Removed, issue );
    }

    @Override
    public boolean isUndoable() {
        return true;
    }

    @Override
    protected Command makeUndoCommand( Commander commander ) throws CommandException {
/*
        ModelObject modelObject = commander.resolve(
                ModelObject.class,
                (Long) get( "modelObject" ) );
*/
        AddUserIssue addIssue = new AddUserIssue( getUserName(), (Long) get( "modelObject" ) );
        addIssue.set( "issue", get( "issue" ) );
        addIssue.set( "state", get( "state" ) );
        return addIssue;
    }
}
