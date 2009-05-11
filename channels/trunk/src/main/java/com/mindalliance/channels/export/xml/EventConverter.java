package com.mindalliance.channels.export.xml;

import com.mindalliance.channels.Exporter;
import com.mindalliance.channels.model.Event;
import com.mindalliance.channels.model.ModelObject;
import com.mindalliance.channels.model.Place;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Plan event converter.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: May 5, 2009
 * Time: 10:01:12 AM
 */
public class EventConverter extends EntityConverter {

    /**
     * Class logger.
     */
    private static final Logger LOG = LoggerFactory.getLogger( EventConverter.class );


    public EventConverter( Exporter exporter ) {
        super( exporter );
    }

    /**
     * {@inheritDoc}
     */
    public boolean canConvert( Class aClass ) {
        return Event.class.isAssignableFrom( aClass );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    ModelObject findOrMakeEntity( String name ) {
        return getQueryService().findOrCreate( Event.class, name );
    }

    /**
     * {@inheritDoc}
     */
    protected void writeSpecifics( ModelObject entity,
                                   HierarchicalStreamWriter writer,
                                   MarshallingContext context ) {
        Event event = (Event) entity;
        Place scope = event.getScope();
        if ( scope != null && !scope.getName().trim().isEmpty() ) {
            writer.startNode( "scope" );
            writer.setValue( scope.getName() );
            writer.endNode();
        }
        if ( event.isSelfTerminating() ) {
            writer.startNode( "self-terminating" );
            writer.setValue( "" + event.isSelfTerminating() );
            writer.endNode();
        }
    }

    /**
     * {@inheritDoc}
     */
    protected void setSpecific( ModelObject entity,
                                String nodeName,
                                HierarchicalStreamReader reader,
                                UnmarshallingContext context ) {
        Event event = (Event) entity;
        if ( nodeName.equals( "scope" ) ) {
            event.setScope( getQueryService().findOrCreate( Place.class, reader.getValue() ) );
        } else if ( nodeName.equals( "self-terminating" ) ) {
            event.setSelfTerminating( reader.getValue().equals("true") );
        } else {
            LOG.warn( "Unknown element " + nodeName );
        }
    }

}
