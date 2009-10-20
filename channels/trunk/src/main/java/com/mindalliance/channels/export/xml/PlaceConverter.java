package com.mindalliance.channels.export.xml;

import com.mindalliance.channels.geo.GeoLocation;
import com.mindalliance.channels.model.ModelEntity;
import com.mindalliance.channels.model.Place;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

/**
 * XStream place converter
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Jan 16, 2009
 * Time: 6:54:07 PM
 */
public class PlaceConverter extends EntityConverter {

    public PlaceConverter( XmlStreamer.Context context ) {
        super( context );
    }

    /**
     * {@inheritDoc}
     */
    public boolean canConvert( Class aClass ) {
        return Place.class.isAssignableFrom( aClass );
    }

    /**
     * {@inheritDoc}
     */
    protected Class<? extends ModelEntity> getEntityClass() {
        return Place.class;
    }

    /**
     * {@inheritDoc}
     */
    protected void writeSpecifics( ModelEntity entity,
                                   HierarchicalStreamWriter writer,
                                   MarshallingContext context ) {
        Place place = (Place) entity;
        Place within = place.getWithin();
        if ( within != null ) {
            writer.startNode( "within" );
            writer.addAttribute( "id", Long.toString( within.getId() ) );
            writer.setValue( within.getName() );
            writer.endNode();
        }
        if ( !place.getStreetAddress().isEmpty() ) {
            writer.startNode( "streetAddress" );
            writer.setValue( place.getStreetAddress() );
            writer.endNode();
        }
        if ( !place.getPostalCode().isEmpty() ) {
            writer.startNode( "postalCode" );
            writer.setValue( place.getPostalCode() );
            writer.endNode();
        }
        if ( place.getGeoname() != null ) {
            writer.startNode( "geoname" );
            writer.setValue( place.getGeoname() );
            writer.endNode();
        }
        if ( place.geoLocate() != null ) {
            writer.startNode( "geoLocation" );
            context.convertAnother( place.geoLocate() );
            writer.endNode();
        }
        if ( place.getGeoLocations( getQueryService() ) != null ) {
            for ( GeoLocation geoLoc : place.getGeoLocations( getQueryService() ) ) {
                writer.startNode( "alternateGeoLocation");
                context.convertAnother( geoLoc );
                writer.endNode();
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    protected void setSpecific(
            ModelEntity entity,
            String nodeName,
            HierarchicalStreamReader reader,
            UnmarshallingContext context ) {
        Place place = (Place) entity;
        if ( nodeName.equals( "within" ) ) {
            String id = reader.getAttribute( "id");
            place.setWithin( findOrCreate( Place.class, reader.getValue(), id ) );
        } else if (nodeName.equals("streetAddress")) {
            place.setStreetAddress( reader.getValue() );
        } else if ( nodeName.equals( "postalCode")) {
            place.setPostalCode( reader.getValue() );
        } else if ( nodeName.equals( "geoname")) {
            place.setGeoname( reader.getValue() );
        } else if ( nodeName.equals( "geoLocation")) {
            place.setGeoLocation( (GeoLocation)context.convertAnother( place, GeoLocation.class ));
        } else if ( nodeName.equals( "alternateGeoLocation")) {
            place.addGeoLocation( (GeoLocation)context.convertAnother( place, GeoLocation.class ));
        }
    }

}
