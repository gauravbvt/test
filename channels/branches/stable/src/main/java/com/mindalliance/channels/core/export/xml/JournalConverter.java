/*
 * Copyright (C) 2011 Mind-Alliance Systems LLC.
 * All rights reserved.
 * Proprietary and Confidential.
 */

package com.mindalliance.channels.core.export.xml;

import com.mindalliance.channels.core.command.AbstractCommand;
import com.mindalliance.channels.core.command.Command;
import com.mindalliance.channels.core.dao.Journal;
import com.mindalliance.channels.core.dao.JournalCommand;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Journal XML converter.
 */
public class JournalConverter extends AbstractChannelsConverter {

    /**
     * Class logger.
     */
    public static final Logger LOG = LoggerFactory.getLogger( JournalConverter.class );

    //-------------------------------
    public JournalConverter( XmlStreamer.Context context ) {
        super( context );
    }

    //-------------------------------
    @Override
    public boolean canConvert( Class type ) {
        return Journal.class.isAssignableFrom( type );
    }

    @Override
    public void marshal( Object source, HierarchicalStreamWriter writer, MarshallingContext context ) {
        Journal journal = (Journal) source;
        for ( JournalCommand command : journal.getCommands() ) {
            writer.startNode( "command" );
            context.convertAnother( command );
            writer.endNode();
        }
    }

    @Override
    public Object unmarshal( HierarchicalStreamReader reader, UnmarshallingContext context ) {
        Journal journal = new Journal();
        while ( reader.hasMoreChildren() ) {
            reader.moveDown();
            String nodeName = reader.getNodeName();
            if ( nodeName.equals( "command" ) ) {
                Command command = (Command) context.convertAnother( journal, AbstractCommand.class );
                journal.addCommand( command );
            } else {
                LOG.debug( "Unknown element " + nodeName );
            }
            reader.moveUp();
        }
        return journal;
    }
}
