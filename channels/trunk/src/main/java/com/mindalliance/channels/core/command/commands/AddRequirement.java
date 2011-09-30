package com.mindalliance.channels.core.command.commands;

import com.mindalliance.channels.core.command.AbstractCommand;
import com.mindalliance.channels.core.command.Change;
import com.mindalliance.channels.core.command.Command;
import com.mindalliance.channels.core.command.CommandException;
import com.mindalliance.channels.core.command.Commander;
import com.mindalliance.channels.core.dao.PlanDao;
import com.mindalliance.channels.core.model.Requirement;

import java.util.Map;

/**
 * Add requirement.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 9/29/11
 * Time: 9:23 PM
 */
public class AddRequirement extends AbstractCommand {

    public AddRequirement() {
        super( "daemon" );
    }

    public AddRequirement( String userName ) {
        super( userName );
    }

    @Override
    @SuppressWarnings( "unchecked" )
    public Change execute( Commander commander ) throws CommandException {
        PlanDao planDao = commander.getPlanDao();
        Long priorId = (Long) get( "requirement" );   // set when undoing a RemoveRequirement
        Requirement requirement = planDao.createRequirement( priorId );
        // State is set when undoing a RemoveRequirement
        Map<String, Object> state = (Map<String, Object>) get( "state" );
        if ( state != null )
            requirement.initFromMap( state, commander.getQueryService() );
        set( "requirement", requirement.getId() );
        describeTarget( requirement );
        return new Change( Change.Type.Added, requirement );
    }

    @Override
    protected Command makeUndoCommand( Commander commander ) throws CommandException {
        Requirement requirement = commander.resolve( Requirement.class, (Long) get( "requirement" ) );
        return new RemoveRequirement( getUserName(), requirement );
    }

    @Override
    public String getName() {
        return "add new requirement";
    }

    @Override
    public boolean isUndoable() {
        return true;
    }

}
