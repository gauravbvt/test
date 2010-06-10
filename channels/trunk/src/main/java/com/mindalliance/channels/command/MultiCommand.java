package com.mindalliance.channels.command;

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
 * A command composed of other commands.
 * A best effort is made at executing as many of the component commands as possible.
 * Not undoable even though the component commands may be
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Mar 6, 2009
 * Time: 9:30:58 AM
 */
public class MultiCommand extends AbstractCommand {
    /**
     * Logger.
     */
    private static final Logger LOG = LoggerFactory.getLogger( MultiCommand.class );
    /**
     * The command's name.
     */
    private String name = "multiple commands";
    /**
     * Name of what this command undoes.
     */
    private String undoes = "";
    /**
     * A list of commands.
     */
    private List<Command> commands = new ArrayList<Command>();
    /**
     * Links between commands
     */
    private List<Link> links = new ArrayList<Link>();
    /**
     * List of executed sub-commands.
     */
    private List<Command> executed = new ArrayList<Command>();

    /**
     * A source-sink link between two commands.
     */
    private final class Link implements Serializable {
        /**
         * Command which result is used as argument value in sink command.
         */
        private Command sourceCommand;
        /**
         * Property path into source command's result. Null if result itself is to be used.
         */
        private String resultProperty;
        /**
         * Command with an argument to be set from the result of
         * the source command's prior execution.
         */
        private Command sinkCommand;
        /**
         * Name of argument to set in sink command.
         */
        private String argumentName;

        private Link() {
        }

        /**
         * Process result from command if applicable.
         *
         * @param command a command
         * @param result  an object
         */
        private void process( Command command, Object result ) {
            if ( result != null && command == sourceCommand ) {
                Object value;
                try {
                    value = resultProperty != null
                            ? PropertyUtils.getProperty( result, resultProperty )
                            : result;
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

    public MultiCommand() {
        setTop( false );
    }

    public MultiCommand( String name ) {
        this();
        this.name = name;
    }

    /**
     * {@inheritDoc}
     */
    public Set<Long> getLockingSet() {
        Set<Long> lockingSet = new HashSet<Long>();
        for ( Command command : commands ) {
            lockingSet.addAll( command.getLockingSet() );
        }
        return lockingSet;
    }

    /**
     * {@inheritDoc}
     */
    public Set<Long> getConflictSet() {
        Set<Long> conflictSet = new HashSet<Long>();
        for ( Command command : commands ) {
            conflictSet.addAll( command.getConflictSet() );
        }
        return conflictSet;
    }

    /**
     * {@inheritDoc}
     */
    public String getName() {
        return name;
    }

    public void setName( String name ) {
        this.name = name;
    }

    public String getUndoes() {
        return undoes;
    }

    public void setUndoes( String undoes ) {
        this.undoes = undoes;
    }

    public List<Command> getCommands() {
        return commands;
    }

    /**
     * {@inheritDoc}
     */
    public Change execute( Commander commander ) throws CommandException {
        for ( Command command : commands ) {
            try {
                LOG.info( "--- sub-command --" );
                Change change = commander.doCommand( command ); // TODO -- command journaled here
                for ( Link link : links ) link.process( command, change.getSubject( commander.getQueryService() ) ); // without benefit of link processing
                executed.add( command );
            }
            catch ( CommandException e ) {
                LOG.warn( " Execution failed", e );
                // ignore
            }
        }
        LOG.info( "END multicommand " + getName() );
        return new Change();
    }

    /**
     * {@inheritDoc}
     */
    public boolean noLockRequired() {
        // A multi-command requires no lock of its own; its sub-commands might.
        return true;
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
    protected Command makeUndoCommand( Commander commander ) throws CommandException {
        MultiCommand undoMulti = new MultiCommand( getUndoes() );
        undoMulti.setUndoes( getName() );
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
     * Add a link between commands
     *
     * @param sourceCommand  the source command
     * @param resultProperty the property path applied to result of source command execution
     * @param sinkCommand    the sink command
     * @param argumentName   the name of the argument to be set in sink command
     */
    public void addLink(
            Command sourceCommand,
            String resultProperty,
            Command sinkCommand,
            String argumentName ) {
        Link link = new Link();
        link.sourceCommand = sourceCommand;
        link.resultProperty = resultProperty;
        link.sinkCommand = sinkCommand;
        link.argumentName = argumentName;
        links.add( link );
    }
}
