package com.mindalliance.channels.export.xml;

import com.mindalliance.channels.model.Channel;
import com.mindalliance.channels.model.ModelEntity;
import com.mindalliance.channels.model.TransmissionMedium;
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
        writer.addAttribute( "id", "" + channel.getMedium().getId() );
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
                String id = reader.getAttribute( "id" );
                String name = reader.getValue();
                TransmissionMedium medium;
                if ( id != null )
                    medium = getEntity(
                            TransmissionMedium.class,
                            name,
                            Long.parseLong( id ),
                            ModelEntity.Kind.Actual,
                            context );
                else  // TODO - remove after transition
                    medium = getQueryService().findOrCreate( TransmissionMedium.class, name );
                channel.setMedium( medium );
            } else if ( nodeName.equals( "address" ) ) {
                channel.setAddress( reader.getValue() );
            }
            reader.moveUp();
        }
        return channel;
    }
}
