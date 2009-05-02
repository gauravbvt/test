package com.mindalliance.channels.export.xml;

import com.mindalliance.channels.Exporter;
import com.mindalliance.channels.command.AbstractCommand;
import com.mindalliance.channels.command.Command;
import com.mindalliance.channels.dao.Journal;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Journal XML converter.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Mar 22, 2009
 * Time: 12:06:48 PM
 */
public class JournalConverter extends AbstractChannelsConverter {
    /**
     * Class logger.
     */
    public static final Logger LOG = LoggerFactory.getLogger( JournalConverter.class );

    public JournalConverter( Exporter exporter ) {
        super( exporter );
    }

    /**
     * {@inheritDoc}
     */
    public boolean canConvert( Class aClass ) {
        return Journal.class.isAssignableFrom( aClass );
    }

    /**
     * {@inheritDoc}
     */
    public void marshal(
            Object obj,
            HierarchicalStreamWriter writer,
            MarshallingContext context ) {
        Journal journal = (Journal) obj;
        for ( Command command : journal.getCommands() ) {
            writer.startNode( "command" );
            context.convertAnother( command );
            writer.endNode();
        }
    }

    /**
     * {@inheritDoc}
     */
    public Object unmarshal(
            HierarchicalStreamReader reader,
            UnmarshallingContext context ) {
        Journal journal = new Journal();
        while ( reader.hasMoreChildren() ) {
            reader.moveDown();
            String nodeName = reader.getNodeName();
            if ( nodeName.equals( "command" ) ) {
                Command command = (Command) context.convertAnother( journal, AbstractCommand.class );
                journal.addCommand( command );
            } else {
                LOG.warn( "Unknown element " + nodeName );
            }
            reader.moveUp();
        }
        return journal;
    }
}
