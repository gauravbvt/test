/*
 * Copyright (C) 2011 Mind-Alliance Systems LLC.
 * All rights reserved.
 * Proprietary and Confidential.
 */

package com.mindalliance.channels.core.command;

import com.mindalliance.channels.core.model.Flow;
import com.mindalliance.channels.core.model.Identifiable;
import com.mindalliance.channels.core.model.InternalFlow;
import com.mindalliance.channels.core.model.Mappable;
import com.mindalliance.channels.core.model.ModelObject;
import com.mindalliance.channels.core.model.Node;
import com.mindalliance.channels.core.model.NotFoundException;
import com.mindalliance.channels.core.model.Segment;
import com.mindalliance.channels.core.model.SegmentObject;
import com.mindalliance.channels.core.query.QueryService;
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
 * The root of all command.
 */
public abstract class AbstractCommand implements Command {

    /**
     * Logger.
     */
    private static final Logger LOG = LoggerFactory.getLogger( AbstractCommand.class );

    /**
     * Arguments needed to execute (all serializable, self-contained or primitive objects, no model objects).
     */
    private Map<String, Object> arguments = new HashMap<String, Object>();

    /**
     * Ids of model objects that are involved by the command but need not be locked. Used to rule out conflicting undoes
     * and redoes in history. An id in the locking set is always in the conflict set.
     */
    private Set<Long> conflictSet = new HashSet<Long>();

    /**
     * Ids of model objects that must be locked for the duration of the execution of the command.
     */
    private Set<Long> lockingSet = new HashSet<Long>();

    /**
     * Whether the command should be remembered after its execution for undoing.
     */
    private boolean memorable = true;

    /**
     * Whether the command is undoable.
     * A constant property in most commands. Writable in MultiCommand.
     */
    private boolean undoable = true;

    /**
     * A preserved description for the actual subject of the change.
     */
    private String targetDescription = "";

    /**
     * Whether this is a top-most command (not some other command's sub-command).
     */
    private boolean top = true;

    /**
     * Undo command.
     */
    private Command undoCommand;

    /**
     * Name of what this command undoes.
     */
    private String undoes = "";

    /**
     * User name.
     */
    private String userName;

    //-------------------------------

    protected AbstractCommand() {
    }

    protected AbstractCommand( String userName ) {
        this.userName = userName;
    }

    //-------------------------------

    /**
     * Add identifiable to conflict set.
     *
     * @param identifiable an identifiable object
     */
    public void addConflicting( Identifiable identifiable ) {
        addConflicting( identifiable.getId() );
    }

    /**
     * Add identifiable to conflict set.
     *
     * @param id an id
     */
    public void addConflicting( long id ) {
        conflictSet.add( id );
    }

    /**
     * Add identifiables to conflict set.
     *
     * @param identifiables a list of identifiable object
     */
    public void addConflicting( List<? extends Identifiable> identifiables ) {
        for ( Identifiable identifiable : identifiables )
            conflictSet.add( identifiable.getId() );
    }

    /**
     * Add a unique value to a list at a given object's property.
     *
     * @param obj target object
     * @param property list property name
     * @param value value to add
     */
    @SuppressWarnings( "unchecked" )
    protected void addUniqueToProperty( Object obj, String property, Object value ) {
        List list = (List) getProperty( obj, property );
        if ( !list.contains( value) )
            list.add( value );
    }

    /**
     * Add a value to a list at a given object's property.
     *
     * @param obj target object
     * @param property list property name
     * @param value value to add
     */
    @SuppressWarnings( "unchecked" )
    protected void addToProperty( Object obj, String property, Object value ) {
        List list = (List) getProperty( obj, property );
        list.add( value );
    }


    @Override
    public boolean canDo( Commander commander ) {
        // Default
        return true;
    }

    /**
     * Set the description of the actual target of the command.
     *
     * @param modelObject a model object
     */
    protected void describeTarget( ModelObject modelObject ) {
        String description = "";
        if ( modelObject != null ) {
            description = "\"" + modelObject.getLabel() + "\"";
            if ( modelObject instanceof SegmentObject ) {
                description += " in segment \"" + ( (SegmentObject) modelObject ).getSegment().getLabel() + "\"";
            }
        }
        setTargetDescription( description );
    }

    @Override
    public boolean forcesSnapshot() {
        return false;
    }

    @Override
    public Object get( String key ) {
        Object value = arguments.get( key );
        assert !( value instanceof ModelObject );
        return value;
    }

    /**
     * Get the value of named argument, allowing for resolution of ModelObjectRef values.
     *
     * @param commander a commander
     * @return an object
     * @throws CommandException if getting argument fails
     */
    public Object get( String key, Commander commander ) throws CommandException {
        Object value = arguments.get( key );
        if ( value instanceof ModelObjectRef ) {
            ModelObjectRef moRef = (ModelObjectRef) value;
            value = moRef.resolve( commander.getCommunityService() );
        } else if ( value instanceof MappedObject ) {
            value = ( (MappedObject) value ).fromMap( commander );
        }
        return value;
    }

    @Override
    public Map<String, Object> getArguments() {
        // Get a copy of the arguments
        Map<String, Object> args = new HashMap<String, Object>();
        args.putAll( arguments );
        return args;
    }

    @Override
    public String getLabel( Commander commander ) throws CommandException {
        // Default
        return getTitle();
    }

    /**
     * Get a model object's property value.
     *
     * @param obj an object
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

    @Override
    public String getTitle() {
        return StringUtils.capitalize( getName() );
    }

    /**
     * Make an undo command if one not already set.
     *
     * @param commander a a commander
     * @return a command
     * @throws CommandException if failed to make undo command
     */
    @Override
    public Command getUndoCommand( Commander commander ) throws CommandException {
        if ( undoCommand == null )
            try {
                Command undo = makeUndoCommand( commander );
                undo.setUndoes( getName() );
                undo.setMemorable( isMemorable() );
                undo.setTop( isTop() );
                undoCommand = undo;
                undoCommand.setTargetDescription( getTargetDescription() );
            } catch ( RuntimeException e ) {
                LOG.warn( "Runtime exception while making undo command.", e );
                throw new CommandException( "Runtime exception while making undo command.", e );
            }
        return undoCommand;
    }

    @Override
    public String getUndoes( Commander commander ) {
        try {
            return getUndoCommand( commander ).getName();
        } catch ( CommandException e ) {
            LOG.warn( "Can't make undo command.", e );
            return "?";
        }
    }

    /**
     * Remove id from lockingSet. Usually because the command deleted the model object with this id.
     *
     * @param id a model object id
     */
    protected void ignoreLock( Long id ) {
        lockingSet.remove( id );
    }

    protected void ignoreLocksOn( List<? extends Identifiable> identifiables ) {
        for ( Identifiable identifiable : identifiables ) {
            ignoreLock( identifiable.getId() );
        }
    }

    @Override
    public boolean isAuthorized() {
        // By default.
        return true;
    }

    @Override
    public boolean isSegmentSpecific() {
        return true;
    }

    /**
     * Make an undo command.
     *
     * @param commander a a commander
     * @return a command
     * @throws CommandException if failed to make undo command
     */
    protected abstract Command makeUndoCommand( Commander commander ) throws CommandException;

    /**
     * Add identifiable's id to locking set.
     *
     * @param identifiable an identifiable object
     */
    protected void needLockOn( Identifiable identifiable ) {
        lockingSet.add( identifiable.getId() );
        conflictSet.add( identifiable.getId() );
    }

    /**
     * Add identifiable's id to locking set.
     *
     * @param identifiables a list of identifiable objects
     */
    protected void needLocksOn( List<? extends Identifiable> identifiables ) {
        for ( Identifiable identifiable : identifiables ) {
            needLockOn( identifiable );
        }
    }

    @Override
    public boolean noLockRequired() {
        return false;
    }

    /**
     * Release any lock on an identifiable and remove from conflict set.
     *
     * @param commander a commander
     * @param identifiable an identifiable
     */
    protected void releaseAnyLockOn( Commander commander, Identifiable identifiable ) {
        conflictSet.remove( identifiable.getId() );
        commander.releaseAnyLockOn( userName, identifiable );
    }

    /**
     * Remove a value from a list at a given object's property if not the last value.
     *
     * @param obj target object
     * @param property list property name
     * @param value value to remove
     */
    @SuppressWarnings( "unchecked" )
    protected void removeExceptLastFromProperty( Object obj, String property, Object value ) {
        List list = (List) getProperty( obj, property );
        if ( list.size() > 1 )
            list.remove( value );
    }

    /**
     * Remove a value from a list at a given object's property.
     *
     * @param obj target object
     * @param property list property name
     * @param value value to remove
     */
    @SuppressWarnings( "unchecked" )
    protected void removeFromProperty( Object obj, String property, Object value ) {
        List list = (List) getProperty( obj, property );
        list.remove( value );
    }

    /**
     * Find flow in segment given id.
     *
     * @param id a long
     * @param segment a segment
     * @return a flow
     * @throws CommandException if not found
     */
    public static Flow resolveFlow( Long id, Segment segment ) throws CommandException {
        try {
            if ( id != null && segment != null ) {
                return segment.findFlow( id );
            } else {
                throw new NotFoundException();
            }
        } catch ( NotFoundException e ) {
            throw new CommandException( "Can't find flow", e );
        }
    }

    /**
     * Resolve a node from an id.
     *
     * @param id a long
     * @param segment a segment in context
     * @param queryService a query service
     * @return a node
     * @throws CommandException if not found
     */
    public static Node resolveNode( Long id, Segment segment, QueryService queryService ) throws CommandException {
        Node node;
        // null id represents a local connector
        if ( id != null ) {
            ModelObject mo;
            try {
                mo = queryService.find( ModelObject.class, id );
            } catch ( NotFoundException e ) {
                throw new CommandException( "You need to refresh.", e );
            }
            // How external an connector is captured
            if ( mo instanceof InternalFlow ) {
                InternalFlow internalFlow = (InternalFlow) mo;
                assert internalFlow.hasConnector();
                node = internalFlow.getSource().isConnector() ? internalFlow.getSource() : internalFlow.getTarget();
            } else {
                node = segment.getNode( id );
            }
        } else {
            node = queryService.createConnector( segment );
        }
        return node;
    }

    @Override
    public void set( String argumentName, Object value ) {
        arguments.put( argumentName,
                       value instanceof ModelObject ?
                       new ModelObjectRef( (ModelObject) value ) :
                       value instanceof Mappable ? new MappedObject( (Mappable) value ) : value );
    }

    /**
     * Set a model object's property value.
     *
     * @param obj a model object
     * @param property a property name
     * @param value an object
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
        }
    }

    @Override
    public String toString() {
        return getName();
    }

    //-------------------------------
    @Override
    public Set<Long> getConflictSet() {
        return conflictSet;
    }

    public void setConflictSet( Set<Long> conflictSet ) {
        this.conflictSet = conflictSet;
    }

    @Override
    public Set<Long> getLockingSet() {
        return lockingSet;
    }

    public void setLockingSet( Set<Long> lockingSet ) {
        this.lockingSet = lockingSet;
    }

    @Override
    public String getTargetDescription() {
        return targetDescription;
    }

    @Override
    public void setTargetDescription( String subjectDescription ) {
        this.targetDescription = subjectDescription;
    }

    @Override
    public String getUndoes() {
        return undoes;
    }

    @Override
    public void setUndoes( String undoes ) {
        this.undoes = undoes;
    }

    @Override
    public boolean triggersAfterCommand() {
        return true; // default
    }

    @Override
    public String getUserName() {
        return userName;
    }

    // For testing only
    public void setUserName( String userName ) {
        this.userName = userName;
    }

    public boolean isUndoable() {
        return undoable;
    }

    public void makeUndoable( boolean undoable ) {
        this.undoable = undoable;
    }

    @Override
    public boolean isMemorable() {
        return memorable;
    }

    @Override
    public void setMemorable( boolean memorable ) {
        this.memorable = memorable;
    }

    @Override
    public boolean isTop() {
        return top;
    }

    @Override
    public void setTop( boolean top ) {
        this.top = top;
    }

    @Override
    @SuppressWarnings( "unchecked" )
    public void setArguments( Map args ) {
        arguments = args;
    }
}
