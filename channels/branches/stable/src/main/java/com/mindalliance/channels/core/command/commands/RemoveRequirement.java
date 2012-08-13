package com.mindalliance.channels.core.command.commands;

import com.mindalliance.channels.core.command.AbstractCommand;
import com.mindalliance.channels.core.command.Change;
import com.mindalliance.channels.core.command.Command;
import com.mindalliance.channels.core.command.CommandException;
import com.mindalliance.channels.core.command.Commander;
import com.mindalliance.channels.core.dao.PlanDao;
import com.mindalliance.channels.core.model.Requirement;

/**
 * Remove requirement command.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 9/29/11
 * Time: 10:28 PM
 */
public class RemoveRequirement extends AbstractCommand {

    public RemoveRequirement() {
        super( "daemon" );
    }

    public RemoveRequirement( String userName, Requirement requirement ) {
        super( userName );
        needLockOn( requirement );
        set( "requirement", requirement.getId() );
    }

    @Override
    public Change execute( Commander commander ) throws CommandException {
        PlanDao planDao = commander.getPlanDao();
        Requirement requirement = commander.resolve( Requirement.class, (Long) get( "requirement" ) );
        describeTarget( requirement );
        set( "state", requirement.mapState() );
        planDao.remove( requirement );
        releaseAnyLockOn( commander, requirement );
        ignoreLock( (Long) get( "requirement" ) );
        return new Change( Change.Type.Removed, requirement );
    }

    @Override
    protected Command makeUndoCommand( Commander commander ) throws CommandException {
        AddRequirement addRequirement = new AddRequirement( getUserName() );
        addRequirement.set( "requirement", get( "requirement" ) );
        addRequirement.set( "state", get( "state" ) );
        return addRequirement;
    }

    @Override
    public String getName() {
        return "remove requirement";
    }

    @Override
    public boolean isUndoable() {
        return true;
    }
}
