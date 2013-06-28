/*
 * Copyright (C) 2011 Mind-Alliance Systems LLC.
 * All rights reserved.
 * Proprietary and Confidential.
 */

package com.mindalliance.channels.core.command;

import com.mindalliance.channels.core.community.CommunityService;
import org.apache.commons.beanutils.PropertyUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * A command composed of other commands. A best effort is made at executing as many of the component commands as
 * possible. Not undoable even though the component commands may be.
 */
public class MultiCommand extends AbstractCommand {

    /**
     * Logger.
     */
    private static final Logger LOG = LoggerFactory.getLogger( MultiCommand.class );

    /**
     * A list of commands.
     */
    private List<Command> commands = new ArrayList<Command>();

    /**
     * List of executed sub-commands.
     */
    private List<Command> executed = new ArrayList<Command>();

    /**
     * Links between commands.
     */
    private List<Link> links = new ArrayList<Link>();

    /**
     * The command's name.
     */
    private String name = "multiple commands";

    private Change change = new Change(); // Change to be returned on end of execution

    //-------------------------------

    public MultiCommand() {
        super();
    }

    public MultiCommand( String userName ) {
        super( userName );
    }

    public MultiCommand( String userName, String name ) {
        this( userName );
        this.name = name;
    }

    public Change getChange() {
        return change;
    }

    public void setChange( Change change ) {
        this.change = change;
    }

    //-------------------------------
    /**
     * Add a command to the batch.
     *
     * @param command a Command
     */
    public void addCommand( Command command ) {
        command.setTop( false );
        commands.add( command );
        command.setMemorable( false );
    }

    /**
     * Add a link between commands.
     *
     * @param sourceCommand the source command
     * @param resultProperty the property path applied to result of source command execution
     * @param sinkCommand the sink command
     * @param argumentName the name of the argument to be set in sink command
     */
    public void addLink( Command sourceCommand, String resultProperty, Command sinkCommand, String argumentName ) {
        Link link = new Link();
        link.sourceCommand = sourceCommand;
        link.resultProperty = resultProperty;
        link.sinkCommand = sinkCommand;
        link.argumentName = argumentName;
        links.add( link );
    }

    @Override
    public Change execute( Commander commander ) throws CommandException {
        CommunityService communityService = commander.getCommunityService();
        for ( Command command : commands ) {
            LOG.info( "--- sub-command --" );
            Change change = commander.doCommand( command );
            for ( Link link : links )
                link.process( command, change, communityService ); // without benefit of link processing
            executed.add( command );
        }
        LOG.info( "END multicommand " + getName() );
        return getChange();
    }

    @Override
    protected Command makeUndoCommand( Commander commander ) throws CommandException {
        MultiCommand undoMulti = new MultiCommand( getUserName(), getUndoes() );
        // Add undoes of executed commands in reverse order of their execution.
        for ( int i = executed.size() - 1; i >= 0; i-- ) {
            Command command = executed.get( i );
            if ( command.isUndoable() ) {
                Command undoCommand = command.getUndoCommand( commander );
                undoMulti.addCommand( undoCommand );
            }
        }
        return undoMulti;
    }

    @Override
    public boolean noLockRequired() {
        // A multi-command requires no lock of its own; its sub-commands might.
        return true;
    }

    //-------------------------------
    public List<Command> getCommands() {
        return commands;
    }

    @Override
    public Set<Long> getConflictSet() {
        Set<Long> conflictSet = new HashSet<Long>();
        for ( Command command : commands ) {
            conflictSet.addAll( command.getConflictSet() );
        }
        return conflictSet;
    }

    @Override
    public Set<Long> getLockingSet() {
        Set<Long> lockingSet = new HashSet<Long>();
        for ( Command command : commands ) {
            lockingSet.addAll( command.getLockingSet() );
        }
        return lockingSet;
    }

    @Override
    public String getName() {
        return name;
    }

    public void setName( String name ) {
        this.name = name;
    }

    public boolean isEmpty() {
        return commands.isEmpty();
    }

    //===============================

    /**
     * A source-sink link between two commands.
     */
    private final class Link implements Serializable {

        /**
         * Name of argument to set in sink command.
         */
        private String argumentName;

        /**
         * Property path into source command's result. Null if result itself is to be used.
         */
        private String resultProperty;

        /**
         * Command with an argument to be set from the result of the source command's prior execution.
         */
        private Command sinkCommand;

        /**
         * Command which result is used as argument value in sink command.
         */
        private Command sourceCommand;

        //-------------------------------
        private Link() {
        }

        //-------------------------------
        /**
         * Process result from command if applicable.
         *
         * @param command a command
         * @param change a change
         * @param communityService a query service
         */
        private void process( Command command, Change change, CommunityService communityService ) {
            if ( command == sourceCommand ) {
                Object result = change.getSubject( communityService );
                if ( result != null ) {
                    Object value;
                    try {
                        value = resultProperty != null ? PropertyUtils.getProperty( result, resultProperty ) : result;
                    } catch ( IllegalAccessException e ) {
                        throw new RuntimeException( e );
                    } catch ( InvocationTargetException e ) {
                        throw new RuntimeException( e );
                    } catch ( NoSuchMethodException e ) {
                        throw new RuntimeException( e );
                    }
                    sinkCommand.set( argumentName, value );
                }
            }
        }
    }
}
