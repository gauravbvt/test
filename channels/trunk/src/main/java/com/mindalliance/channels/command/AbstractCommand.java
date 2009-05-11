package com.mindalliance.channels.command;

import com.mindalliance.channels.Commander;
import com.mindalliance.channels.NotFoundException;
import com.mindalliance.channels.model.Identifiable;
import com.mindalliance.channels.model.Mappable;
import com.mindalliance.channels.model.ModelObject;
import com.mindalliance.channels.model.User;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Mar 3, 2009
 * Time: 6:41:54 PM
 */
public abstract class AbstractCommand implements Command {

    /**
     * Logger.
     */
    private static final Logger LOG = LoggerFactory.getLogger( AbstractCommand.class );
    /**
     * User name
     */
    private String userName;

    /**
     * Arguments needed to execute (all serializable, self-contained or primitive objects, no model objects)
     */
    private Map<String, Object> arguments = new HashMap<String, Object>();
    /**
     * Ids of model objects that must be locked for the duration of the execution of the command.
     */
    private Set<Long> lockingSet = new HashSet<Long>();
    /**
     * The command's conflict set.
     */
    private Set<Long> conflictSet = new HashSet<Long>();
    /**
     * Whether the command should be remembered after its execution for undoing.
     */
    private boolean memorable = true;
    /**
     * Optionally preset undo command.
     */
    private Command undoCommand;

    public AbstractCommand() {
        userName = User.current().getName();
    }

    public String getUserName() {
        return userName;
    }

    // For testing only
    public void setUserName( String userName ) {
        this.userName = userName;
    }

    /**
     * {@inheritDoc}
     */
    public Map<String, Object> getArguments() {
        // Get a copy of the arguments
        Map<String, Object> args = new HashMap<String, Object>();
        args.putAll( arguments );
        return args;
    }

    @SuppressWarnings( "unchecked" )
    public void setArguments( Map args ) {
        arguments = args;
    }

    /**
     * {@inheritDoc}
     */
    public Object get( String key ) {
        Object value = arguments.get( key );
        assert !( value instanceof ModelObjectRef || value instanceof ModelObject );
        return value;
    }

    /**
     * {@inheritDoc}
     */
    public Object get( String key, Commander commander ) throws CommandException {
        Object value = arguments.get( key );
        if ( value instanceof ModelObjectRef ) {
            ModelObjectRef moRef = (ModelObjectRef) value;
            try {
                value = moRef.resolve( commander );
            } catch ( NotFoundException e ) {
                throw new CommandException( " Can't dereference " + moRef, e );
            }
        } else if ( value instanceof MappedObject ) {
            value = ( (MappedObject) value).fromMap( commander );
        }
        return value;
    }

    /**
     * {@inheritDoc}
     */
    public void set( String key, Object value ) {
        Object val;
        if ( value instanceof ModelObject )
            val = new ModelObjectRef( (ModelObject) value );
        else if ( value instanceof Mappable ) {
            val = ( (Mappable) value ).map();
        } else {
            val = value;
        }
        arguments.put( key, val );
    }

    public Set<Long> getLockingSet() {
        return lockingSet;
    }

    public void setLockingSet( Set<Long> lockingSet ) {
        this.lockingSet = lockingSet;
    }

    public Set<Long> getConflictSet() {
        return conflictSet;
    }

    public void setConflictSet( Set<Long> conflictSet ) {
        this.conflictSet = conflictSet;
    }

    /**
     * Add identifiable's id to locking set
     *
     * @param identifiable an identifiable object
     */
    protected void needLockOn( Identifiable identifiable ) {
        lockingSet.add( identifiable.getId() );
    }

    /**
     * Add identifiable's id to locking set
     *
     * @param identifiables a list of identifiable objects
     */
    protected void needLocksOn( List<Identifiable> identifiables ) {
        for ( Identifiable identifiable : identifiables ) {
            needLockOn( identifiable );
        }
    }

    /**
     * Remove id from lockingSet.
     * Usually because the command deleted the model object with this id.
     *
     * @param id a model object id
     */
    protected void ignoreLock( Long id ) {
        lockingSet.remove( id );
    }

    /**
     * Add identifiable to conflict set.
     * Also add to locking set.
     *
     * @param identifiable an identifiable object
     */
    public void addConflicting( Identifiable identifiable ) {
        conflictSet.add( identifiable.getId() );
        needLockOn( identifiable );
    }

    /**
     * {@inheritDoc}
     */
    public boolean isAuthorized() {
        // By default.
        return true;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isMemorable() {
        return memorable;
    }

    /**
     * {@inheritDoc}
     */
    public void setMemorable( boolean memorable ) {
        this.memorable = memorable;
    }

    /**
     * {@inheritDoc}
     */
    public void setUndoCommand( Command undoCommand ) {
        this.undoCommand = undoCommand;
    }

    /**
     * {@inheritDoc}
     */
    public String toString() {
        return getName();
    }

    /**
     * {@inheritDoc}
     */
    public String getTitle() {
        return StringUtils.capitalize( getName() );
    }

    /**
     * {@inheritDoc}
     */
    public boolean noLockRequired() {
        return false;
    }

    /**
     * Make an undo command if one not already set.
     *
     * @param commander a a commander
     * @return a command
     * @throws CommandException if failed to make undo command
     */
    public Command makeUndoCommand( Commander commander ) throws CommandException {
        if ( undoCommand != null )
            return undoCommand;
        else
            try {
                return doMakeUndoCommand( commander );
            }
            catch ( RuntimeException e ) {
                LOG.warn( "Runtime exception while making undo command.", e );
                throw new CommandException( "Runtime exception while making undo command.", e );
            }
    }

    /**
     * Make an undo command.
     *
     * @param commander a a commander
     * @return a command
     * @throws CommandException if failed to make undo command
     */
    protected abstract Command doMakeUndoCommand( Commander commander ) throws CommandException;

    /**
     * Get a model object's property value.
     *
     * @param obj      an object
     * @param property a property name
     * @return an object
     */
    protected Object getProperty( Object obj, String property ) {
        try {
            return PropertyUtils.getProperty( obj, property );
        } catch ( IllegalAccessException e ) {
            throw new RuntimeException( e );
        } catch ( InvocationTargetException e ) {
            throw new RuntimeException( e );
        } catch ( NoSuchMethodException e ) {
            throw new RuntimeException( e );
        }
    }

    /**
     * Set a model object's property value.
     *
     * @param obj      a model object
     * @param property a property name
     * @param value    an object
     */
    protected void setProperty( Object obj, String property, Object value ) {
        try {
            PropertyUtils.setProperty( obj, property, value );
        } catch ( IllegalAccessException e ) {
            throw new RuntimeException( e );
        } catch ( InvocationTargetException e ) {
            throw new RuntimeException( e );
        } catch ( NoSuchMethodException e ) {
            throw new RuntimeException( e );
        } catch ( RuntimeException e ) {
            throw e;
        }
    }

    /**
     * Add a value to a list at a given object's property.
     *
     * @param obj      target object
     * @param property list property name
     * @param value    value to add
     */
    @SuppressWarnings( "unchecked" )
    protected void addToProperty( Object obj, String property, Object value ) {
        List list = (List) getProperty( obj, property );
        list.add( value );
    }

    /**
     * Remove a value from a list at a given object's property.
     *
     * @param obj      target object
     * @param property list property name
     * @param value    value to remove
     */
    @SuppressWarnings( "unchecked" )
    protected void removeFromProperty( Object obj, String property, Object value ) {
        List list = (List) getProperty( obj, property );
        list.remove( value );
    }

    /**
     * {@inheritDoc}
     */
    public String getUndoes( Commander commander ) {
        try {
            return makeUndoCommand( commander ).getName();
        } catch ( CommandException e ) {
            LOG.warn( "Can't make undo command.", e );
            return "?";
        }
    }

    /**
     * {@inheritDoc}
     */
    public boolean isScenarioSpecific() {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    public boolean canDo( Commander commander ) {
        // Default
        return true;
    }

}
