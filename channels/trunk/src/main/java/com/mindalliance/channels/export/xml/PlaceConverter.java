package com.mindalliance.channels.export.xml;

import com.mindalliance.channels.geo.GeoLocation;
import com.mindalliance.channels.model.Event;
import com.mindalliance.channels.model.ModelEntity;
import com.mindalliance.channels.model.Place;
import com.mindalliance.channels.model.PlaceReference;
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
        if ( place.getMustBeContainedIn().isSet() ) {
            PlaceReference placeRef = place.getMustBeContainedIn();
            writer.startNode( "mustBeContainedIn" );
            writer.addAttribute( "eventOrPlace", placeRef.isEventReferenced() ? "event" : "place" );
            writer.addAttribute( "kind", placeRef.getReference().getKind().name() );
            writer.addAttribute( "id", Long.toString( placeRef.getReference().getId() ) );
            writer.setValue( placeRef.getReference().getName() );
            writer.endNode();
        }
        if ( place.getMustContain().isSet() ) {
            PlaceReference placeRef = place.getMustContain();
            writer.startNode( "mustContain" );
            writer.addAttribute( "eventOrPlace", placeRef.isEventReferenced() ? "event" : "place" );
            writer.addAttribute( "kind", placeRef.getReference().getKind().name() );
            writer.addAttribute( "id", Long.toString( placeRef.getReference().getId() ) );
            writer.setValue( placeRef.getReference().getName() );
            writer.endNode();
        }
        if ( place.getGeoLocations() != null ) {
            for ( GeoLocation geoLoc : place.getGeoLocations() ) {
                writer.startNode( "alternateGeoLocation" );
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
            String id = reader.getAttribute( "id" );
            place.setWithin( findOrCreate( Place.class, reader.getValue(), id ) );
        } else if ( nodeName.equals( "streetAddress" ) ) {
            place.setStreetAddress( reader.getValue() );
        } else if ( nodeName.equals( "postalCode" ) ) {
            place.setPostalCode( reader.getValue() );
        } else if ( nodeName.equals( "geoname" ) ) {
            place.setGeoname( reader.getValue() );
        } else if ( nodeName.equals( "mustBeContainedIn" ) ) {
            String eventOrPlace = reader.getAttribute( "eventOrPlace" );
            ModelEntity.Kind kind = ModelEntity.Kind.valueOf( reader.getAttribute( "kind" ) );
            String id = reader.getAttribute( "id" );
            String name = reader.getValue();
            PlaceReference placeRef = new PlaceReference();
            if ( eventOrPlace.equals( "event" ) ) {
                placeRef.setEvent( findOrCreateType( Event.class, name, id ) );
            } else {
                if ( kind == ModelEntity.Kind.Actual )
                    placeRef.setPlace( findOrCreate( Place.class, name, id ) );
                else
                    placeRef.setPlace( findOrCreateType( Place.class, name, id ) );
            }
            place.setMustBeContainedIn( placeRef );
        } else if ( nodeName.equals( "mustContain" ) ) {
            String eventOrPlace = reader.getAttribute( "eventOrPlace" );
            ModelEntity.Kind kind = ModelEntity.Kind.valueOf( reader.getAttribute( "kind" ) );
            String id = reader.getAttribute( "id" );
            String name = reader.getValue();
            PlaceReference placeRef = new PlaceReference();
            if ( eventOrPlace.equals( "event" ) ) {
                placeRef.setEvent( findOrCreateType( Event.class, name, id ) );
            } else {
                if ( kind == ModelEntity.Kind.Actual )
                    placeRef.setPlace( findOrCreate( Place.class, name, id ) );
                else
                    placeRef.setPlace( findOrCreateType( Place.class, name, id ) );
            }
            place.setMustContain( placeRef );
        } else if ( nodeName.equals( "geoLocation" ) ) {
            place.setGeoLocation( (GeoLocation) context.convertAnother( place, GeoLocation.class ) );
        } else if ( nodeName.equals( "alternateGeoLocation" ) ) {
            place.addGeoLocation( (GeoLocation) context.convertAnother( place, GeoLocation.class ) );
        }
    }

}
