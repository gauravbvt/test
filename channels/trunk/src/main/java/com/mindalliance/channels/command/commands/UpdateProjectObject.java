package com.mindalliance.channels.command.commands;

import com.mindalliance.channels.command.CommandException;
import com.mindalliance.channels.ModelObject;
import com.mindalliance.channels.NotFoundException;
import com.mindalliance.channels.Service;
import com.mindalliance.channels.Identifiable;

/**
 * Command to update a modelobject contained in a project.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Mar 5, 2009
 * Time: 11:47:16 AM
 */
public class UpdateProjectObject extends UpdateObject {

    public UpdateProjectObject(
            final Identifiable identifiable,
            final String property,
            final Object value) {
        this( identifiable, property, value, Action.Set );
    }

    public UpdateProjectObject(
            final Identifiable identifiable,
            final String property,
            final Object value,
            final Action action) {
        super( identifiable, property, value, action );
    }

    /**
     * {@inheritDoc}
     */
    protected Identifiable getIdentifiable( Service service ) throws CommandException {
        try {
            return service.find( ModelObject.class, (Long) get( "object" ) );
        } catch ( NotFoundException e ) {
            throw new CommandException( "You need to refresh", e );
        }
    }

    /**
     * {@inheritDoc}
     */
    protected UpdateObject createUndoCommand( Identifiable identifiable, String property, Object value, Action action ) {
        return new UpdateProjectObject( identifiable, property, value, action);
    }


}
