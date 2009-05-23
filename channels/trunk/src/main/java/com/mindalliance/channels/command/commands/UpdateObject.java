package com.mindalliance.channels.command.commands;

import com.mindalliance.channels.Commander;
import com.mindalliance.channels.QueryService;
import com.mindalliance.channels.command.AbstractCommand;
import com.mindalliance.channels.command.Change;
import com.mindalliance.channels.command.Command;
import com.mindalliance.channels.command.CommandException;
import com.mindalliance.channels.model.Identifiable;
import com.mindalliance.channels.model.ModelObject;
import com.mindalliance.channels.model.ScenarioObject;

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

    public UpdateObject() {
    }

    public UpdateObject( Identifiable identifiable, String property, Object value ) {
        this( identifiable, property, value, Action.Set );
    }

    public UpdateObject(
            final Identifiable identifiable,
            final String property,
            final Object value,
            final Action action ) {
        addConflicting( identifiable );
        set( "action", action.toString() );
        set( "object", identifiable.getId() );
        set( "property", property );
        set( "value", value );
        if ( action == Action.Set ) set( "old", getProperty( identifiable, property ) );
        set( "type", identifiable.getClass().getSimpleName().toLowerCase() );
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
            return new UpdatePlanObject( identifiable, property, value, action );
        }
    }

    /**
     * {@inheritDoc}
     */
    public String getName() {
        return "updating " + getObjectTypeName( (String) get( "type" ) );
    }

    /**
     * Get the type of object updated.
     *
     * @param type a raw type name
     * @return a string
     */
    protected String getObjectTypeName( String type ) {
        return type;
    }

    /**
     * {@inheritDoc}
     */
    public Change execute( Commander commander ) throws CommandException {
        QueryService queryService = commander.getQueryService();
        Identifiable identifiable = getIdentifiable( commander );
        switch ( action() ) {
            case Set:
                setProperty(
                        identifiable,
                        (String) get( "property" ),
                        get( "value", commander )
                );
                break;
            case Add:
                addToProperty(
                        identifiable,
                        (String) get( "property" ),
                        get( "value", commander )
                );
                break;
            case Remove:
                removeFromProperty(
                        identifiable,
                        (String) get( "property" ),
                        get( "value", commander )
                );
                break;
            case Move:
                moveInProperty(
                        identifiable,
                        (String) get( "property" ),
                        get( "value", commander )
                );
                break;
            default:
                throw new IllegalArgumentException( "Unknown action " + action() );
        }
        if ( identifiable instanceof ModelObject ) queryService.update( (ModelObject) identifiable );
        return new Change( Change.Type.Updated, identifiable, (String) get( "property" ) );
    }

    private Action action() {
        return Action.valueOf( (String) get( "action" ) );
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
     * @param commander a commander
     * @return an identifiable
     * @throws CommandException if fails
     */
    protected abstract Identifiable getIdentifiable( Commander commander ) throws CommandException;

    /**
     * {@inheritDoc}
     */
    public boolean isUndoable() {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    protected Command makeUndoCommand( Commander commander ) throws CommandException {
        Identifiable identifiable = getIdentifiable( commander );
        String property = (String) get( "property" );
        Object value;
        switch ( action() ) {
            case Set:
                Object oldValue = get( "old", commander );
                return createUndoCommand( identifiable, property, oldValue, Action.Set );
            case Add:
                value = get( "value", commander );
                return createUndoCommand( identifiable, property, value, Action.Remove );
            case Remove:
                value = get( "value", commander );
                return createUndoCommand( identifiable, property, value, Action.Add );
            case Move:
                value = get( "value", commander );
                Command command = createUndoCommand( identifiable, property, value, Action.Move );
                Integer oldIndex = (Integer) get( "oldIndex" );
                if ( oldIndex != null ) command.set( "index", oldIndex );
                return command;
            default:
                throw new RuntimeException( "Unknown action " + action() );
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
