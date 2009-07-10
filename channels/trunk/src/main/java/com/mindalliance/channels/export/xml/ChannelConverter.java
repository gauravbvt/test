package com.mindalliance.channels.export.xml;

import com.mindalliance.channels.model.Channel;
import com.mindalliance.channels.model.Medium;
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
public class ChannelConverter extends AbstractChannelsConverter {

    public ChannelConverter( XmlStreamer.Context context ) {
        super( context );
    }

    public boolean canConvert( Class type ) {
        return Channel.class.isAssignableFrom( type );
    }

    public void marshal( Object source,
                         HierarchicalStreamWriter writer,
                         MarshallingContext context ) {
        Channel channel = (Channel) source;
        writer.startNode( "medium" );
        writer.setValue( channel.getMedium().name() );
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
                Medium medium = Medium.valueOf( reader.getValue() );
                channel.setMedium( medium );
            } else if ( nodeName.equals( "address" ) ) {
                channel.setAddress( reader.getValue() );
            }
            reader.moveUp();
        }
        return channel;
    }
}
