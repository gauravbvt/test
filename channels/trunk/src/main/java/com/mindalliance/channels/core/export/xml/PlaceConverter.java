package com.mindalliance.channels.core.export.xml;

import com.mindalliance.channels.core.model.Event;
import com.mindalliance.channels.core.model.GeoLocation;
import com.mindalliance.channels.core.model.ModelEntity;
import com.mindalliance.channels.core.model.Place;
import com.mindalliance.channels.core.model.PlaceReference;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

/**
 * XStream place converter.
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

    @Override
    public boolean canConvert( Class aClass ) {
        return Place.class.isAssignableFrom( aClass );
    }

    @Override
    protected Class<? extends ModelEntity> getEntityClass() {
        return Place.class;
    }

    @Override
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
        if ( place.getGeoLocation() != null ) {
            writer.startNode( "geoLocation" );
            context.convertAnother( place.getGeoLocation() );
            writer.endNode();
        }
        if ( place.isPlaceholder() ) {
            writer.startNode( "placeholder" );
            writer.setValue( Boolean.toString( true ) );
            writer.endNode();
        }
        if ( isPlanContext() ) {
            Place locale = getPlan().getLocale();
            if ( place.getMustBeContainedIn().isSet( locale ) ) {
                writer.startNode( "mustBeContainedIn" );
                writePlaceReference( writer, place.getMustBeContainedIn() );
                writer.endNode();
            }
            if ( place.getMustContain().isSet( locale ) ) {
                writer.startNode( "mustContain" );
                writePlaceReference( writer, place.getMustContain() );
                writer.endNode();
            }
        }
        if ( place.getGeoLocations() != null ) {
            for ( GeoLocation geoLoc : place.getGeoLocations() ) {
                writer.startNode( "alternateGeoLocation" );
                context.convertAnother( geoLoc );
                writer.endNode();
            }
        }
    }

    private void writePlaceReference( HierarchicalStreamWriter writer, PlaceReference placeRef ) {
        writer.addAttribute(
                "ref",
                placeRef.isPlanReferenced()
                        ? "plan"
                        : placeRef.isEventReferenced()
                        ? "event"
                        : "place" );
        if ( !placeRef.isPlanReferenced() ) {
            writer.addAttribute( "kind", placeRef.getReference().getKind().name() );
            writer.addAttribute( "id", Long.toString( placeRef.getReference().getId() ) );
            writer.setValue( placeRef.getReference().getName() );
        }
    }

    @Override
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
            place.setMustBeContainedIn( readPlaceReference( reader ) );
        } else if ( nodeName.equals( "mustContain" ) ) {
            place.setMustContain( readPlaceReference( reader ) );
        } else if ( nodeName.equals( "geoLocation" ) ) {
            place.setGeoLocation( (GeoLocation) context.convertAnother( place, GeoLocation.class ) );
        } else if ( nodeName.equals( "alternateGeoLocation" ) ) {
            place.addGeoLocation( (GeoLocation) context.convertAnother( place, GeoLocation.class ) );
        } else if ( nodeName.equals( "placeholder" ) ) {
            place.setPlaceholder( Boolean.parseBoolean( reader.getValue() ) );
        }
    }

    private PlaceReference readPlaceReference( HierarchicalStreamReader reader ) {
        PlaceReference placeRef = new PlaceReference();
        String ref = reader.getAttribute( "ref" );
        if ( ref == null ) ref = reader.getAttribute( "eventOrPlace" );   // todo - remove when conversion completed
        if ( ref.equals( "plan" ) ) {
            placeRef.setPlanReferenced( true );
        } else {
            ModelEntity.Kind kind = ModelEntity.Kind.valueOf( reader.getAttribute( "kind" ) );
            String id = reader.getAttribute( "id" );
            String name = reader.getValue();
            if ( ref.equals( "event" ) ) {
                placeRef.setEvent( findOrCreateType( Event.class, name, id ) );
            } else {
                if ( kind == ModelEntity.Kind.Actual )
                    placeRef.setPlace( findOrCreate( Place.class, name, id ) );
                else
                    placeRef.setPlace( findOrCreateType( Place.class, name, id ) );
            }
        }
        return placeRef;
    }


}
