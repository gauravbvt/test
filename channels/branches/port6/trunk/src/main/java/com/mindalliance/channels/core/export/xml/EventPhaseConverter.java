package com.mindalliance.channels.core.export.xml;

import com.mindalliance.channels.core.model.Event;
import com.mindalliance.channels.core.model.EventPhase;
import com.mindalliance.channels.core.model.Level;
import com.mindalliance.channels.core.model.ModelEntity;
import com.mindalliance.channels.core.model.Phase;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

/**
 * Event phase converter.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 1/11/11
 * Time: 10:14 AM
 */
public class EventPhaseConverter extends AbstractChannelsConverter {

    protected EventPhaseConverter( XmlStreamer.Context context ) {
        super( context );
    }

    @Override
    /**
     * {@inheritDoc}
     */
    public boolean canConvert( Class aClass ) {
        return EventPhase.class.isAssignableFrom( aClass );
    }

    @Override
    /**
     * {@inheritDoc}
     */
    public void marshal( Object source, HierarchicalStreamWriter writer, MarshallingContext context ) {
        EventPhase eventPhase = (EventPhase) source;
        // Event
        Event event = eventPhase.getEvent();
        writer.startNode( "event" );
        writer.addAttribute( "id", Long.toString( event.getId() ) );
        writer.addAttribute( "kind", event.getKind().name() );
        writer.setValue( event.getName() );
        writer.endNode();
        // Phase
        Phase phase = eventPhase.getPhase();
        writer.startNode( "phase" );
        writer.addAttribute( "id", Long.toString( phase.getId() ) );
        writer.addAttribute( "kind", phase.getKind().name() );
        writer.setValue( phase.getName() );
        writer.endNode();
        // Level
        if ( eventPhase.getEventLevel() != null ) {
            writer.startNode( "level" );
            writer.setValue( eventPhase.getEventLevel().toString() );
            writer.endNode();
        }
    }

    @Override
    /**
     * {@inheritDoc}
     */
    public Object unmarshal( HierarchicalStreamReader reader, UnmarshallingContext context ) {
        Event event = null;
        Phase phase = null;
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
            } else if ( nodeName.equals( "phase" ) ) {
                Long id = Long.parseLong( reader.getAttribute( "id" ) );
                String kind = reader.getAttribute( "kind" );
                phase = getEntity(
                        Phase.class,
                        reader.getValue(),
                        id,
                        ModelEntity.Kind.valueOf( kind ),
                        context );
            } else if ( nodeName.equals( "level" ) ) {
                eventLevel = Level.valueOf( reader.getValue() );
            }
            reader.moveUp();
        }
        // event, phase or eventLevel must not be null.
        return new EventPhase( event, phase, eventLevel );
    }

}
