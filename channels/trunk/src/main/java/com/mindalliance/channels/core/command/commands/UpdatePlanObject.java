/*
 * Copyright (C) 2011 Mind-Alliance Systems LLC.
 * All rights reserved.
 * Proprietary and Confidential.
 */

package com.mindalliance.channels.core.command.commands;

import com.mindalliance.channels.core.command.CommandException;
import com.mindalliance.channels.core.command.Commander;
import com.mindalliance.channels.core.model.Identifiable;
import com.mindalliance.channels.core.model.ModelObject;

/**
 * Command to update a modelobject contained in a plan.
 */
public class UpdatePlanObject extends UpdateObject {

    public UpdatePlanObject() {
    }

    public UpdatePlanObject( String userName ) {
        super( userName );
    }

    public UpdatePlanObject( String userName, Identifiable identifiable, String property, Object value ) {
        this( userName, identifiable, property, value, Action.Set );
    }

    public UpdatePlanObject( String userName, Identifiable identifiable, String property, Object value,
                             Action action ) {
        super( userName, identifiable, property, value, action );
    }

    @Override
    @SuppressWarnings( "unchecked" )
    protected Identifiable getIdentifiable( Commander commander ) throws CommandException {
        String className = (String) get( "class" );
        Class<? extends ModelObject> clazz = null;
        try {
            clazz = (Class<? extends ModelObject>) getClass().getClassLoader().loadClass( className );
        } catch ( ClassNotFoundException e ) {
            throw new CommandException( "Invalid class name", e );
        }
        return commander.resolve( clazz, (Long) get( "object" ) );
    }

    @Override
    protected UpdateObject createUndoCommand( Identifiable identifiable, String property, Object value,
                                              Action action ) {
        return new UpdatePlanObject( getUserName(), identifiable, property, value, action );
    }
}
