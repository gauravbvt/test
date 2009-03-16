package com.mindalliance.channels.command;

import org.apache.commons.beanutils.PropertyUtils;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

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
    private final class Link {
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

    public MultiCommand() {}

    public MultiCommand( String name ) {
        this.name = name;
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

    /**
     * {@inheritDoc}
     */
    public Change execute( Commander commander ) throws CommandException {
        for ( Command command : commands ) {
            try {
                Change change = commander.doCommand( command );
                for ( Link link : links ) link.process( command, change.getSubject() );
                executed.add( command );
            }
            catch ( CommandException e ) {
                e.printStackTrace();
                // ignore
            }
        }
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
    protected Command doMakeUndoCommand( Commander commander ) throws CommandException {
        MultiCommand undoMulti = new MultiCommand();
        // Add undoes of executed commands in reverse order of their execution.
        for ( int i = executed.size() - 1; i >= 0; i-- ) {
            Command command = executed.get( i );
            Command undoCommand = command.makeUndoCommand( commander );
            undoMulti.addCommand( undoCommand );
            // TODO - what about links?
        }
        return undoMulti;
    }

    /**
     * Add a command to the batch.
     *
     * @param command a Command
     */
    public void addCommand( Command command ) {
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
