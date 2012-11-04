package com.mindalliance.channels.core.export.xml;

import com.mindalliance.channels.core.model.Channel;
import com.mindalliance.channels.core.model.InfoFormat;
import com.mindalliance.channels.core.model.ModelEntity;
import com.mindalliance.channels.core.model.TransmissionMedium;
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
        // medium
        writer.startNode( "medium" );
        TransmissionMedium medium = channel.getMedium();
        writer.addAttribute( "id", "" + medium.getId() );
        writer.addAttribute( "kind", medium.isType() ? "Type" : "Actual" );
        writer.setValue( medium.getName() );
        writer.endNode();
        // address
        writer.startNode( "address" );
        writer.setValue( channel.getAddress() );
        writer.endNode();
        // format
        InfoFormat format = channel.getFormat();
        if ( format != null ) {
            writer.startNode( "format" );
            writer.addAttribute( "id", "" + format.getId() );
            writer.addAttribute( "kind", format.isType() ? "Type" : "Actual" );
            writer.setValue( format.getName() );
            writer.endNode();
        }
    }

    public Object unmarshal( HierarchicalStreamReader reader, UnmarshallingContext context ) {
        Channel channel = new Channel();
        while ( reader.hasMoreChildren() ) {
            reader.moveDown();
            String nodeName = reader.getNodeName();
            if ( nodeName.equals( "medium" ) ) {
                String id = reader.getAttribute( "id" );
                ModelEntity.Kind kind = kind( reader.getAttribute( "kind" ) );
                String name = reader.getValue();
                TransmissionMedium medium;
                medium = getEntity(
                        TransmissionMedium.class,
                        name,
                        id == null ? null : Long.parseLong( id ),
                        kind,
                        context );
                channel.setMedium( medium );
            } else if ( nodeName.equals( "address" ) ) {
                channel.setAddress( reader.getValue() );
            } else if ( nodeName.equals( "format" ) ) {
                String id = reader.getAttribute( "id" );
                ModelEntity.Kind kind = kind( reader.getAttribute( "kind" ) );
                String name = reader.getValue();
                InfoFormat format;
                format = getEntity(
                        InfoFormat.class,
                        name,
                        id == null ? null : Long.parseLong( id ),
                        kind,
                        context );
                channel.setFormat( format );
            }
            reader.moveUp();
        }
        return channel;
    }
}
