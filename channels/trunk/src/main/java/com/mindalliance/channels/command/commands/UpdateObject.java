package com.mindalliance.channels.command.commands;

import com.mindalliance.channels.Identifiable;
import com.mindalliance.channels.ModelObject;
import com.mindalliance.channels.Service;
import com.mindalliance.channels.ScenarioObject;
import com.mindalliance.channels.command.AbstractCommand;
import com.mindalliance.channels.command.Command;
import com.mindalliance.channels.command.CommandException;
import com.mindalliance.channels.command.Commander;
import com.mindalliance.channels.command.Change;

import java.util.HashMap;
import java.util.List;

/**
 * Abstract property update command.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Mar 12, 2009
 * Time: 5:39:54 PM
 */
public abstract class UpdateObject extends AbstractCommand {
    /**
     * The kind of update.
     */
    private Action action = Action.Set;

    /**
     * Kinds of updates.
     */
    public enum Action {
        /**
         * Set value.
         */
        Set,
        /**
         * Add value to list.
         */
        Add,
        /**
         * Remove value from list.
         */
        Remove,
        /**
         * Move value in list.
         */
        Move

    }

    public UpdateObject( Identifiable identifiable, String property, Object value ) {
        this( identifiable, property, value, Action.Set );
    }

    public UpdateObject(
            final Identifiable identifiable,
            final String property,
            final Object value,
            final Action action ) {
        this.action = action;
        addConflicting( identifiable );
        setArguments( new HashMap<String, Object>() {
            {
                put( "object", identifiable.getId() );
                put( "property", property );
                put( "value", value );
                if ( action == Action.Set ) put( "old", getProperty( identifiable, property ) );
                put( "type", identifiable.getClass().getSimpleName().toLowerCase() );
            }
        } );
    }

    /**
     * Create the appropriate UpdateObject command.
     *
     * @param identifiable an identifiable
     * @param property     a string
     * @param value        an object
     * @param action       Set, Add or Remove
     * @return an UpdateObject command
     */
    public static UpdateObject makeCommand(
            Identifiable identifiable,
            String property,
            Object value,
            Action action ) {
        if ( identifiable instanceof ScenarioObject ) {
            return new UpdateScenarioObject( identifiable, property, value, action );
        } else {
            return new UpdateProjectObject( identifiable, property, value, action );
        }
    }

    /**
     * {@inheritDoc}
     */
    public String getName() {
        switch ( action ) {
            case Set:  return "setting to " + get( "type" );
            case Add: return "adding to " + get( "type" );
            case Remove: return "removing from " + get( "type" );
            case Move: return "moving " + get( "type" );
            default: throw new IllegalArgumentException( "Unknown action " + action );
        }
    }

    /**
     * {@inheritDoc}
     */
    public Change execute( Commander commander ) throws CommandException {
        Service service = commander.getService();
        Identifiable identifiable = getIdentifiable( service );
        switch ( action ) {
            case Set:
                setProperty(
                        identifiable,
                        (String) get( "property" ),
                        get( "value" )
                );
                break;
            case Add:
                addToProperty(
                        identifiable,
                        (String) get( "property" ),
                        get( "value" )
                );
                break;
            case Remove:
                removeFromProperty(
                        identifiable,
                        (String) get( "property" ),
                        get( "value" )
                );
                break;
            case Move:
                moveInProperty(
                        identifiable,
                        (String) get( "property" ),
                        get( "value" )
                );
                break;
            default:
                throw new IllegalArgumentException( "Unknown action " + action );
        }
        if ( identifiable instanceof ModelObject ) service.update( (ModelObject) identifiable );
        return new Change( Change.Type.Updated, identifiable, (String) get( "property" ) );
    }

    /**
     * Move given element within list as property value of identifiable.
     * By default move to top.
     *
     * @param identifiable an identifiable
     * @param property     a property path
     * @param element      an object
     * @throws com.mindalliance.channels.command.CommandException
     *          if fails
     */
    @SuppressWarnings( "unchecked" )
    private void moveInProperty(
            Identifiable identifiable,
            String property,
            Object element ) throws CommandException {
        List list = (List) getProperty( identifiable, property );
        int currentIndex = list.indexOf( element );
        if ( currentIndex == -1 ) throw new CommandException( "Can't move missing element." );
        set( "oldIndex", new Integer( currentIndex ) );
        Integer toIndex = (Integer) get( "index" );
        if ( toIndex == null ) toIndex = 0;
        list.remove( currentIndex );
        list.add( toIndex, element );
    }

    /**
     * Retrieve target of command.
     *
     * @param service a service
     * @return an identifiable
     * @throws CommandException if fails
     */
    protected abstract Identifiable getIdentifiable( Service service ) throws CommandException;

    /**
     * {@inheritDoc}
     */
    public boolean isUndoable() {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    protected Command doMakeUndoCommand( Commander commander ) throws CommandException {
        Identifiable identifiable = getIdentifiable( commander.getService() );
        String property = (String) get( "property" );
        Object value;
        switch ( action ) {
            case Set:
                Object oldValue = get( "old" );
                return createUndoCommand( identifiable, property, oldValue, Action.Set );
            case Add:
                value = get( "value" );
                return createUndoCommand( identifiable, property, value, Action.Remove );
            case Remove:
                value = get( "value" );
                return createUndoCommand( identifiable, property, value, Action.Add );
            case Move:
                value = get( "value" );
                Command command = createUndoCommand( identifiable, property, value, Action.Move );
                Integer oldIndex = (Integer) get( "oldIndex" );
                if ( oldIndex != null ) command.set( "index", oldIndex );
                return command;
            default:
                throw new RuntimeException( "Unknown action " + action );
        }
    }

    /**
     * Create undo command instance.
     *
     * @param identifiable an identifiable
     * @param property     the name of a property
     * @param value        an object
     * @param action       either Set, Add or Remove
     * @return a command
     */
    protected abstract UpdateObject createUndoCommand(
            Identifiable identifiable,
            String property,
            Object value,
            Action action );

}
