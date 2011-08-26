package com.mindalliance.channels.core.export.xml;

import com.mindalliance.channels.core.model.Event;
import com.mindalliance.channels.core.model.EventTiming;
import com.mindalliance.channels.core.model.Level;
import com.mindalliance.channels.core.model.ModelEntity;
import com.mindalliance.channels.core.model.Phase;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 1/11/11
 * Time: 2:03 PM
 */
public class EventTimingConverter  extends AbstractChannelsConverter {

    protected EventTimingConverter( XmlStreamer.Context context ) {
        super( context );
    }

    @Override
    /**
     * {@inheritDoc}
     */
    public boolean canConvert( Class aClass ) {
        return EventTiming.class.isAssignableFrom( aClass );
    }

    @Override
    /**
     * {@inheritDoc}
     */
    public void marshal( Object source, HierarchicalStreamWriter writer, MarshallingContext context ) {
        EventTiming eventTiming = (EventTiming) source;
        // Event
        Event event = eventTiming.getEvent();
        writer.startNode( "event" );
        writer.addAttribute( "id", Long.toString( event.getId() ) );
        writer.addAttribute( "kind", event.getKind().name() );
        writer.setValue( event.getName() );
        writer.endNode();
        // Phase timing
        if ( eventTiming.getTiming() != null ) {
            writer.startNode( "timing" );
            writer.setValue( eventTiming.getTiming().toString() );
            writer.endNode();
        }
        // Level
        if ( eventTiming.getEventLevel() != null ) {
            writer.startNode( "level" );
            writer.setValue( eventTiming.getEventLevel().toString() );
            writer.endNode();
        }
    }

    @Override
    /**
     * {@inheritDoc}
     */
    public Object unmarshal( HierarchicalStreamReader reader, UnmarshallingContext context ) {
        Event event = null;
        Phase.Timing timing = null;
        Level eventLevel = null;
        while ( reader.hasMoreChildren() ) {
            reader.moveDown();
            String nodeName = reader.getNodeName();
            if ( nodeName.equals( "event" ) ) {
                Long id = Long.parseLong( reader.getAttribute( "id" ) );
                String kind = reader.getAttribute( "kind" );
                event = getEntity(
                        Event.class,
                        reader.getValue(),
                        id,
                        ModelEntity.Kind.valueOf( kind ),
                        context );
            } else if ( nodeName.equals( "timing" ) ) {
                timing = Phase.Timing.valueOf( reader.getValue() );
            } else if ( nodeName.equals( "level" ) ) {
                eventLevel = Level.valueOf( reader.getValue() );
            }
            reader.moveUp();
        }
        // event, phase or eventLevel must not be null.
        return new EventTiming( timing, event, eventLevel );
    }

}

