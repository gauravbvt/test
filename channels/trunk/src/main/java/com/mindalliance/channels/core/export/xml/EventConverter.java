package com.mindalliance.channels.core.export.xml;

import com.mindalliance.channels.core.export.xml.XmlStreamer.Context;
import com.mindalliance.channels.core.model.Event;
import com.mindalliance.channels.core.model.ModelEntity;
import com.mindalliance.channels.core.model.ModelEntity.Kind;
import com.mindalliance.channels.core.model.Place;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Plan event converter. Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved. Proprietary and Confidential.
 * User: jf Date: May 5, 2009 Time: 10:01:12 AM
 */
public class EventConverter extends EntityConverter {

    /**
     * Class logger.
     */
    private static final Logger LOG = LoggerFactory.getLogger( EventConverter.class );

    public EventConverter( Context context ) {
        super( context );
    }

    @Override
    public boolean canConvert( Class type ) {
        return Event.class.isAssignableFrom( type );
    }

    @Override
    protected Class<? extends ModelEntity> getEntityClass() {
        return Event.class;
    }

    @Override
    protected void writeSpecifics(
            ModelEntity entity, HierarchicalStreamWriter writer, MarshallingContext context ) {
        Event event = (Event) entity;
        Place scope = event.getScope();
        if ( scope != null && !scope.getName().trim().isEmpty() && scope.isActual() ) { // drop place types
            writer.startNode( "scope" );
            writer.addAttribute( "id", Long.toString( scope.getId() ) );
            writer.addAttribute( "kind", scope.getKind().name() );
            writer.setValue( scope.getName() );
            writer.endNode();
        }
        if ( event.isSelfTerminating() ) {
            writer.startNode( "self-terminating" );
            writer.setValue( String.valueOf( event.isSelfTerminating() ) );
            writer.endNode();
        }
    }

    @Override
    protected void setSpecific(
            ModelEntity entity, String nodeName, HierarchicalStreamReader reader, UnmarshallingContext context ) {

        Event event = (Event) entity;
        if ( "scope".equals( nodeName ) ) {
            String id = reader.getAttribute( "id" );
            String kindName = reader.getAttribute( "kind" );
            String name = reader.getValue();
            if ( kindName != null && kindName.equals( Kind.Actual.name() ) ) {
                Place scope = findOrCreate( Place.class, name, id );
                event.setScope( scope );
            }
        } else if ( "self-terminating".equals( nodeName ) )
            event.setSelfTerminating( "true".equals( reader.getValue() ) );
        else
            LOG.debug( "Unknown element " + nodeName );
    }
}
