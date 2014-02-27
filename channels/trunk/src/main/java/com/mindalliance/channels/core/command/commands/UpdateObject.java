/*
 * Copyright (C) 2011 Mind-Alliance Systems LLC.
 * All rights reserved.
 * Proprietary and Confidential.
 */

package com.mindalliance.channels.core.command.commands;

import com.mindalliance.channels.core.command.AbstractCommand;
import com.mindalliance.channels.core.command.Change;
import com.mindalliance.channels.core.command.Command;
import com.mindalliance.channels.core.command.CommandException;
import com.mindalliance.channels.core.command.Commander;
import com.mindalliance.channels.core.community.CommunityService;
import com.mindalliance.channels.core.model.Identifiable;
import com.mindalliance.channels.core.model.ModelObject;
import com.mindalliance.channels.core.model.SegmentObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;


/**
 * Abstract property update command.
 */
public abstract class UpdateObject extends AbstractCommand {

    /**
     * Logger.
     */
    private static final Logger LOG = LoggerFactory.getLogger( AbstractCommand.class );


    /**
     * Kinds of updates.
     */
    public enum Action {
        /**
         * Set value.
         */
        Set,
        /**
         * Add unique value to list.
         */
        AddUnique,
        /**
         * Add value to list - allow repetition.
         */
        Add,
        /**
         * Remove value from list.
         */
        Remove,
        /**
         * Remove value from list except if the last one.
         */
        RemoveExceptLast,
        /**
         * Move value in list.
         */
        Move

    }

    //-------------------------------
    protected UpdateObject() {
        super( "daemon" );
    }

    protected UpdateObject( String userName ) {
        super( userName );
    }

    protected UpdateObject( String userName, Identifiable identifiable, String property, Object value ) {
        this( userName, identifiable, property, value, Action.Set );
    }

    protected UpdateObject( String userName, Identifiable identifiable, String property, Object value, Action action ) {
        this( userName );
        if ( identifiable instanceof ModelObject )
            needLockOn( identifiable );
        set( "action", action.toString() );
        set( "class", identifiable.getClass().getCanonicalName() );
        set( "object", identifiable.getId() );
        set( "property", property );
        set( "value", value );
        if ( action == Action.Set )
            set( "old", getProperty( identifiable, property ) );
        set( "type", identifiable.getTypeName() );
    }

    //-------------------------------

    /**
     * Create undo command instance.
     *
     * @param identifiable an identifiable
     * @param property     the name of a property
     * @param value        an object
     * @param action       either Set, Add or Remove
     * @return a command
     */
    protected abstract UpdateObject createUndoCommand( Identifiable identifiable, String property, Object value,
                                                       Action action );

    @Override
    public Change execute( Commander commander ) throws CommandException {
        CommunityService communityService = commander.getCommunityService();
        Identifiable identifiable = getIdentifiable( commander );
        switch ( action() ) {
            case Set:
                setProperty( identifiable, (String) get( "property" ), get( "value", commander ) );
                break;
            case AddUnique:
                addUniqueToProperty( identifiable, (String) get( "property" ), get( "value", commander ) );
                break;
            case Add:
                addToProperty( identifiable, (String) get( "property" ), get( "value", commander ) );
                break;
            case Remove:
                removeFromProperty( identifiable, (String) get( "property" ), get( "value", commander ) );
                break;
            case RemoveExceptLast:
                removeExceptLastFromProperty( identifiable, (String) get( "property" ), get( "value", commander ) );
                break;
            case Move:
                moveInProperty( identifiable, (String) get( "property" ), get( "value", commander ) );
                break;
            default:
                throw new IllegalArgumentException( "Unknown action " + action() );
        }
        if ( identifiable instanceof ModelObject ) {
            ModelObject mo = (ModelObject) identifiable;
            communityService.update( mo );  // todo - does nothing - why?
            describeTarget( mo );
        } else {
            setTargetDescription( identifiable.toString() );
        }
        return new Change( Change.Type.Updated, identifiable, (String) get( "property" ) );
    }

    /**
     * Move given element within list as property value of identifiable. By default move to top.
     *
     * @param identifiable an identifiable
     * @param property     a property path
     * @param element      an object
     * @throws CommandException if fails
     */
    @SuppressWarnings("unchecked")
    private void moveInProperty( Identifiable identifiable, String property, Object element ) throws CommandException {
        List list = (List) getProperty( identifiable, property );
        int currentIndex = list.indexOf( element );
        if ( currentIndex == -1 )
            throw new CommandException( "Can't move missing element." );
        set( "oldIndex", Integer.valueOf( currentIndex ) );
        Integer toIndex = (Integer) get( "index" );
        if ( toIndex == null )
            toIndex = 0;
        list.remove( currentIndex );
        list.add( toIndex, element );
    }

    private Action action() {
        return Action.valueOf( (String) get( "action" ) );
    }

    /**
     * Retrieve target of command.
     *
     * @param commander a commander
     * @return an identifiable
     * @throws CommandException if fails
     */
    protected abstract Identifiable getIdentifiable( Commander commander ) throws CommandException;

    @Override
    public String getName() {
        return "update " + getObjectTypeName( (String) get( "type" ) );
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

    @Override
    public boolean isUndoable() {
        return true;
    }

    /**
     * Create the appropriate UpdateObject command.
     *
     * @param userName     the user updating the object
     * @param identifiable an identifiable
     * @param property     a string
     * @param value        an object
     * @param action       Set, Add or Remove
     * @return an UpdateObject command
     */
    public static UpdateObject makeCommand( String userName, Identifiable identifiable, String property, Object value,
                                            Action action ) throws CommandException {
        try {
        return identifiable instanceof SegmentObject ?
                new UpdateSegmentObject( userName, identifiable, property, value, action ) :
                new UpdatePlanObject( userName, identifiable, property, value, action );
        } catch ( Exception e ) {
            LOG.error( "Failed to make command ", e );
            throw new CommandException( "Failed to make update object command on "
                    + identifiable
                    + " with "
                    + property );
        }
    }

    @Override
    protected Command makeUndoCommand( Commander commander ) throws CommandException {
        Identifiable identifiable = getIdentifiable( commander );
        String property = (String) get( "property" );
        Object value;
        switch ( action() ) {
            case Set:
                Object oldValue = get( "old", commander );
                return createUndoCommand( identifiable, property, oldValue, Action.Set );
            case AddUnique:
            case Add:
                value = get( "value", commander );
                return createUndoCommand( identifiable, property, value, Action.Remove );
            case Remove:
            case RemoveExceptLast:
                value = get( "value", commander );
                return createUndoCommand( identifiable, property, value, Action.AddUnique );
            case Move:
                value = get( "value", commander );
                Command command = createUndoCommand( identifiable, property, value, Action.Move );
                Integer oldIndex = (Integer) get( "oldIndex" );
                if ( oldIndex != null )
                    command.set( "index", oldIndex );
                return command;
            default:
                throw new RuntimeException( "Unknown action " + action() );
        }
    }
}
