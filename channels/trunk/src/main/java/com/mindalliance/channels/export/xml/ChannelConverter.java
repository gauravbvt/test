package com.mindalliance.channels.export.xml;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.converters.ConversionException;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.mindalliance.channels.Channel;
import com.mindalliance.channels.NotFoundException;
import com.mindalliance.channels.pages.Project;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Feb 2, 2009
 * Time: 3:27:22 PM
 */
public class ChannelConverter implements Converter {

    public ChannelConverter() {
    }

    /**
     * {@inheritDoc}
     */
    public boolean canConvert( Class aClass ) {
        return Channel.class.isAssignableFrom( aClass );
    }

    /**
     * {@inheritDoc}
     */
    public void marshal( Object object,
                         HierarchicalStreamWriter writer,
                         MarshallingContext context ) {
        Channel channel = (Channel) object;
        writer.startNode( "medium" );
        writer.setValue( channel.getMedium().getName() );
        writer.endNode();
        writer.startNode( "address" );
        writer.setValue( channel.getAddress() );
        writer.endNode();

    }

    /**
     * {@inheritDoc}
     */
    public Object unmarshal( HierarchicalStreamReader reader, UnmarshallingContext context ) {
        Channel channel;
        channel = new Channel();
        while ( reader.hasMoreChildren() ) {
            reader.moveDown();
            String nodeName = reader.getNodeName();
            if ( nodeName.equals( "medium" ) ) {
                try {
                    channel.setMedium( Project.service().mediumNamed( reader.getValue() ) );
                } catch ( NotFoundException e ) {
                    throw new ConversionException( e );
                }
            } else if ( nodeName.equals( "address" ) ) {
                channel.setAddress( reader.getValue() );
            }
            reader.moveUp();
        }
        return channel;
    }
}
