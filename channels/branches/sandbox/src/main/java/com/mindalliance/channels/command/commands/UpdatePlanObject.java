package com.mindalliance.channels.command.commands;

import com.mindalliance.channels.command.CommandException;
import com.mindalliance.channels.command.Commander;
import com.mindalliance.channels.model.Identifiable;
import com.mindalliance.channels.model.ModelObject;

/**
 * Command to update a modelobject contained in a plan.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Mar 5, 2009
 * Time: 11:47:16 AM
 */
public class UpdatePlanObject extends UpdateObject {

    public UpdatePlanObject() {
    }

    public UpdatePlanObject(
            final Identifiable identifiable,
            final String property,
            final Object value ) {
        this( identifiable, property, value, Action.Set );
    }

    public UpdatePlanObject(
            final Identifiable identifiable,
            final String property,
            final Object value,
            final Action action ) {
        super( identifiable, property, value, action );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings( "unchecked" )
    protected Identifiable getIdentifiable( Commander commander ) throws CommandException {
        String className = (String)get( "class" );
        Class<? extends ModelObject> clazz = null;
        try {
            clazz = (Class<? extends ModelObject>)getClass().getClassLoader().loadClass( className );
        } catch ( ClassNotFoundException e ) {
            throw new CommandException( "Invalid class name", e );
        }
        return commander.resolve( clazz, (Long) get( "object" ) );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected UpdateObject createUndoCommand(
            Identifiable identifiable, String property, Object value, Action action ) {
        return new UpdatePlanObject( identifiable, property, value, action );
    }


}
