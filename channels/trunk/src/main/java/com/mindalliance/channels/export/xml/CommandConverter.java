package com.mindalliance.channels.export.xml;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.json.JettisonMappedXmlDriver;
import com.thoughtworks.xstream.XStream;
import com.mindalliance.channels.command.AbstractCommand;

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
public class CommandConverter implements Converter {

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
            } else {
                LOG.warn( "Unknown element " + nodeName );
            }
            reader.moveUp();
        }
        return command;
    }

    private String toJSON( Object obj ) {
        jsonDriver.setMode( XStream.NO_REFERENCES );
        String json = jsonDriver.toXML( obj );
        return json;
    }

    private Object fromJSON( String json ) {
        Object result = jsonDriver.fromXML( json );
        return result;
    }
}
