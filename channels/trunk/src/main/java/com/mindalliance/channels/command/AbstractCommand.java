package com.mindalliance.channels.command;

import com.mindalliance.channels.Identifiable;
import com.mindalliance.channels.pages.Project;

import java.util.Map;
import java.util.Set;
import java.util.HashSet;
import java.util.HashMap;
import java.lang.reflect.InvocationTargetException;

import org.apache.commons.beanutils.BeanUtils;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Mar 3, 2009
 * Time: 6:41:54 PM
 */
public abstract class AbstractCommand implements Command {
    /**
     * User name
     */
    private String userName;

    /**
     * Arguments needed to execute (no model objects)
     */
    private Map<String, Object> arguments = new HashMap<String,Object>();
    /**
     * Ids of model objects that must be locked for the duration of the execution of the command.
     */
    private Set<Long> lockingSet = new HashSet<Long>();
    /**
     * The command's conflict set.
     */
    private Set<Long> conflictSet = new HashSet<Long>();

    public AbstractCommand() {
        userName = Project.getUserName();
    }

    public abstract String getName();

    public String getUserName() {
        return userName;
    }

    // For testing only
    public void setUserName( String userName ) {
        this.userName = userName;
    }

    public Map getArguments() {
        return arguments;
    }

    public void setArguments( Map<String, Object> arguments ) {
        this.arguments = arguments;
    }

    /**
     * Add an argument.
     *
     * @param key a string
     * @param val an object
     */
    protected void addArgument( String key, Object val ) {
        arguments.put( key, val );
    }

    /**
     * Get an argument by name.
     *
     * @param key a string
     * @return an argument
     */
    protected Object getArgument( String key ) {
        return arguments.get( key );
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
     * Add model object's id to locking set
     *
     * @param identifiable a ModelObject
     */
    protected void needLockOn( Identifiable identifiable ) {
        lockingSet.add( identifiable.getId() );
    }

    /**
     * Remove id from lockingSet.
     * Usually because the command deleted the model object with this id.
     * @param id a model object id
     */
    protected void ignoreLock( Long id ) {
        lockingSet.remove( id );
    }



    protected void addConflicting( Identifiable identifiable ) {
        conflictSet.add( identifiable.getId() );
    }

    public boolean isAuthorized() {
        // By default.
        return true;
    }

    /**
     * {@inheritDoc}
     */
    public String toString() {
        return getName();
    }

    /**
     * Get a model object's property value.
     *
     * @param obj       an object
     * @param property a property name
     * @return an object
     */
    protected Object getProperty( Object obj, String property ) {
        try {
            return BeanUtils.getProperty( obj, property );
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
     * @param obj       a model object
     * @param property a property name
     * @param value an object
     */
    protected void setProperty( Object obj, String property, Object value ) {
        try {
            BeanUtils.setProperty( obj, property, value );
        } catch ( IllegalAccessException e ) {
            throw new RuntimeException( e );
        } catch ( InvocationTargetException e ) {
            throw new RuntimeException( e );
        }
    }

}
