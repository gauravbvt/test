package com.mindalliance.channels.export.xml;

import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.json.JettisonMappedXmlDriver;
import com.thoughtworks.xstream.XStream;
import com.mindalliance.channels.command.AbstractCommand;
import com.mindalliance.channels.command.MultiCommand;
import com.mindalliance.channels.command.Command;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Command XML converter.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Mar 22, 2009
 * Time: 12:07:35 PM
 */
public class CommandConverter extends AbstractChannelsConverter {

    /**
     * Class logger.
     */
    public static final Logger LOG = LoggerFactory.getLogger( CommandConverter.class );

    private XStream jsonDriver;

    public CommandConverter() {
        jsonDriver = new XStream( new JettisonMappedXmlDriver() );
    }

    /**
     * {@inheritDoc}
     */
    public boolean canConvert( Class aClass ) {
        return AbstractCommand.class.isAssignableFrom( aClass );
    }

    /**
     * {@inheritDoc}
     */
    public void marshal(
            Object obj,
            HierarchicalStreamWriter writer,
            MarshallingContext context ) {
        AbstractCommand command = (AbstractCommand) obj;
        writer.addAttribute( "class", command.getClass().getName() );
        writer.startNode( "userName" );
        writer.setValue( command.getUserName() );
        writer.endNode();
        writer.startNode( "memorable" );
        writer.setValue( "" + command.isMemorable() );
        writer.endNode();
        // Arguments arguments = new Arguments( command.getArguments() );
        writer.startNode( "arguments" );
        writer.setValue( toJSON( command.getArguments() ) );
        writer.endNode();
        if ( command instanceof MultiCommand ) {
            writer.startNode( "multi" );
            MultiCommand multi = (MultiCommand) command;
            writer.startNode( "name" );
            writer.setValue( multi.getName() );
            writer.endNode();
            writer.startNode( "undoes" );
            writer.setValue( multi.getUndoes() );
            writer.endNode();
            for ( Command subCommand : multi.getCommands() ) {
                writer.startNode( "command" );
                context.convertAnother( subCommand );
                writer.endNode();
            }
            writer.endNode();
        }
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings( "unchecked" )
    public Object unmarshal(
            HierarchicalStreamReader reader,
            UnmarshallingContext context ) {
        String commandClass = reader.getAttribute( "class" );
        AbstractCommand command;
        try {
            command = (AbstractCommand) Class.forName( commandClass ).newInstance();
        } catch ( InstantiationException e ) {
            throw new RuntimeException( e );
        } catch ( IllegalAccessException e ) {
            throw new RuntimeException( e );
        } catch ( ClassNotFoundException e ) {
            throw new RuntimeException( e );
        }
        while ( reader.hasMoreChildren() ) {
            reader.moveDown();
            String nodeName = reader.getNodeName();
            if ( nodeName.equals( "userName" ) ) {
                command.setUserName( reader.getValue() );
            } else if ( nodeName.equals( "memorable" ) ) {
                command.setMemorable( reader.getValue().equals( "true" ) );
            } else if ( nodeName.equals( "arguments" ) ) {
                // Arguments arguments = (Arguments) fromJSON( reader.getValue() );
                // command.setArguments( arguments.getValue() );
                command.setArguments( (Map) fromJSON( reader.getValue() ) );
            } else if ( nodeName.equals( "multi" ) ) {
                MultiCommand multi = (MultiCommand) command;
                while( reader.hasMoreChildren() ) {
                    reader.moveDown();
                    String multiNodeName = reader.getNodeName();
                    if (multiNodeName.equals("name")) {
                        multi.setName(reader.getValue());
                    } else if ( multiNodeName.equals( "undoes" )) {
                        multi.setUndoes( reader.getValue() );
                    } else if (multiNodeName.equals("command")) {
                        Command subCommand = (Command)context.convertAnother( command, AbstractCommand.class );
                        multi.addCommand( subCommand );
                    } else {
                      LOG.warn( "Unknown element " + multiNodeName );
                    }
                    reader.moveUp();
                }
            } else {
                LOG.warn( "Unknown element " + nodeName );
            }
            reader.moveUp();
        }
        return command;
    }

    private String toJSON( Object obj ) {
        jsonDriver.setMode( XStream.NO_REFERENCES );
        return jsonDriver.toXML( obj );
    }

    private Object fromJSON( String json ) {
        return jsonDriver.fromXML( json );
    }
}
