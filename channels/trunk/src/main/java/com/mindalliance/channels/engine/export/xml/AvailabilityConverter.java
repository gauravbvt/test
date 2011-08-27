package com.mindalliance.channels.engine.export.xml;

import com.mindalliance.channels.core.model.Availability;
import com.mindalliance.channels.core.model.TimePeriod;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Nov 19, 2010
 * Time: 5:37:37 PM
 */
public class AvailabilityConverter extends AbstractChannelsConverter {

    public AvailabilityConverter( XmlStreamer.Context context ) {
        super( context );
    }

    /**
     * {@inheritDoc}
     */
    public boolean canConvert( Class aClass ) {
        return Availability.class.isAssignableFrom( aClass );
    }

    /**
     * {@inheritDoc}
     */
    public void marshal( Object object, HierarchicalStreamWriter writer, MarshallingContext context ) {
        Availability availability = (Availability) object;
        for ( int i = 0; i <= 6; i++ ) {
            TimePeriod period = availability.getTimePeriod( i );
            writer.startNode( "timePeriod" );
            writer.addAttribute( "dayOfWeek", "" + i );
            writer.addAttribute( "fromTime", "" + period.getFromTime() );
            writer.addAttribute( "toTime", "" + period.getToTime() );
            writer.endNode();
        }
    }

    /**
     * {@inheritDoc}
     */
    public Object unmarshal( HierarchicalStreamReader reader, UnmarshallingContext context ) {
        Availability availability = new Availability();
        while ( reader.hasMoreChildren() ) {
            reader.moveDown();
            String nodeName = reader.getNodeName();
            if ( nodeName.equals( "timePeriod" ) ) {
                int i = Integer.parseInt( reader.getAttribute( "dayOfWeek" ) );
                int fromTime = Integer.parseInt( reader.getAttribute( "fromTime" ) );
                int toTime = Integer.parseInt( reader.getAttribute( "toTime" ) );
                TimePeriod period = new TimePeriod( fromTime, toTime );
                availability.setTimePeriod( i, period );
            }
            reader.moveUp();
        }
        return availability;
    }

}
