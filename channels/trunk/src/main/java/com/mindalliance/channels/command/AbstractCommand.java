package com.mindalliance.channels.command;

import com.mindalliance.channels.ModelObject;
import com.mindalliance.channels.NotFoundException;

import java.util.Map;
import java.util.List;
import java.util.Set;
import java.util.HashSet;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Mar 3, 2009
 * Time: 6:41:54 PM
 */
public abstract class AbstractCommand implements Command {

    /**
     * Arguments needed to execute (no model objects)
     */
    private Map<String, Object> arguments;
    /**
     * Ids of model objects that must be locked for the duration of the execution of the command.
     */
    private Set<Long> lockingSet = new HashSet<Long>();

    public AbstractCommand() {
    }

    public abstract String getName();

    public Map getArguments() {
        return arguments;
    }

    public void setArguments( Map<String, Object> arguments ) {
        this.arguments = arguments;
    }

    /**
     * Add an argument.
     * @param key a string
     * @param val an object
     */
    protected void addArgument( String key, Object val ) {
        arguments.put( key, val );
    }

    /**
     * Get an argument by name.
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

    /**
     * Add model object's id to locking set
     * @param mo a ModelObject
     */
    protected void needLockOn( ModelObject mo ) {
        lockingSet.add( mo.getId() );
    }

    public boolean isAuthorized() {
        // By default.
        return true;
    }

    public abstract Object execute() throws NotFoundException;

    public abstract Command makeUndoCommand();
}
