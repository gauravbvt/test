package com.mindalliance.channels.core.export.xml;

import com.mindalliance.channels.core.model.Cycle;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

/**
 * Copyright (C) 2008-2013 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 4/23/14
 * Time: 3:26 PM
 */
public class CycleConverter extends AbstractChannelsConverter {

    public CycleConverter( XmlStreamer.Context context ) {
        super( context );
    }

    @Override
    public void marshal( Object object,
                         HierarchicalStreamWriter writer,
                         MarshallingContext context ) {
        Cycle cycle = (Cycle)object;
        writer.startNode("timeUnit");
        writer.setValue( cycle.getTimeUnit().name() );
        writer.endNode();
        if ( cycle.getSkip() > 1 ) {
            writer.startNode( "skip" );
            writer.setValue( Integer.toString( cycle.getSkip() ) );
            writer.endNode();
        }
       for ( int index : cycle.getTrancheIndices() ) {
            writer.startNode( "trancheIndex" );
           writer.setValue( Integer.toString( index ));
           writer.endNode();
        }
    }

    @Override
    public Object unmarshal( HierarchicalStreamReader reader,
                             UnmarshallingContext context ) {
        Cycle cycle = new Cycle();
        while ( reader.hasMoreChildren() ) {
            reader.moveDown();
            String nodeName = reader.getNodeName();
            if ( nodeName.equals( "timeUnit") ) {
                cycle.setTimeUnit( Cycle.TimeUnit.valueOf( reader.getValue() ) );
            } else if ( nodeName.equals( "skip" ) ) {
                cycle.setSkip( Integer.getInteger( reader.getValue() ));
            } else if ( nodeName.equals( "trancheIndex") ) {
                cycle.addTrancheIndex( Integer.getInteger( reader.getValue() ) );
            }
            reader.moveUp();
        }
        return cycle;
    }

    @Override
    public boolean canConvert( Class aClass ) {
        return Cycle.class.isAssignableFrom( aClass );
        }
}
