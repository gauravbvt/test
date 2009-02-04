package com.mindalliance.channels.export.xml;

import com.mindalliance.channels.Channel;
import com.mindalliance.channels.pages.Project;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

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

    public boolean canConvert( Class type ) {
        return Channel.class.isAssignableFrom( type );
    }

    public void marshal( Object source,
                         HierarchicalStreamWriter writer,
                         MarshallingContext context ) {
        Channel channel = (Channel) source;
        writer.startNode( "medium" );
        writer.setValue( channel.getMedium().getName() );
        writer.endNode();
        writer.startNode( "address" );
        writer.setValue( channel.getAddress() );
        writer.endNode();

    }

    public Object unmarshal( HierarchicalStreamReader reader, UnmarshallingContext context ) {
        Channel channel = new Channel();
        while ( reader.hasMoreChildren() ) {
            reader.moveDown();
            String nodeName = reader.getNodeName();
            if ( nodeName.equals( "medium" ) ) {
                channel.setMedium( Project.service().mediumNamed( reader.getValue() ) );
            } else if ( nodeName.equals( "address" ) ) {
                channel.setAddress( reader.getValue() );
            }
            reader.moveUp();
        }
        return channel;
    }
}
