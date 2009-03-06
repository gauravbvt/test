package com.mindalliance.channels.command.commands;

import com.mindalliance.channels.command.AbstractCommand;
import com.mindalliance.channels.command.CommandException;
import com.mindalliance.channels.command.Command;
import com.mindalliance.channels.ModelObject;
import com.mindalliance.channels.NotFoundException;
import com.mindalliance.channels.Service;

import java.util.HashMap;

/**
 * Command to update a modelobject contained in a project.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Mar 5, 2009
 * Time: 11:47:16 AM
 */
public class UpdateProjectObject extends AbstractCommand {

    public UpdateProjectObject(
            final ModelObject modelObject,
            final String property,
            final Object value ) {
        addConflicting( modelObject );
        needLockOn( modelObject );
        setArguments( new HashMap<String, Object>() {
            {
                put( "object", modelObject.getId() );
                put( "property", property );
                put( "value", value );
                put( "old", getProperty( modelObject, property ) );
                put( "type", modelObject.getClass().getSimpleName().toLowerCase() );
            }
        } );
    }

    /**
     * {@inheritDoc}
     */
    public String getName() {
        return "update " + getArgument( "type" );
    }

    /**
     * {@inheritDoc}
     */
    public Object execute( Service service ) throws CommandException {
        ModelObject modelObject = getModelObject( service );
        setProperty(
                modelObject,
                (String) getArgument( "property" ),
                getArgument( "value" )
        );
        return getArgument( "value" );
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
    public Command makeUndoCommand( Service service ) throws CommandException {
        ModelObject modelObject = getModelObject( service );
        String property = (String) getArgument( "property" );
        Object oldValue = getArgument( "old" );
        return new UpdateProjectObject( modelObject, property, oldValue );
    }

    private ModelObject getModelObject( Service service ) throws CommandException {
        try {
            return service.find( ModelObject.class, (Long) getArgument( "object" ) );
        } catch ( NotFoundException e ) {
            throw new CommandException( "You need to refresh", e );
        }
    }


}
