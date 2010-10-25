package com.mindalliance.channels.model;

import com.mindalliance.channels.geo.GeoLocatable;
import com.mindalliance.channels.geo.GeoLocation;
import com.mindalliance.channels.query.QueryService;

import java.util.ArrayList;
import java.util.List;

/**
 * A plan event.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Apr 30, 2009
 * Time: 3:49:53 PM
 */
public class Event extends ModelEntity implements GeoLocatable {
    /**
     * Where the event is considered to occur.
     * Null means that its scope is universal.
     */
    private Place scope;
    /**
     * Does this even self-terminate?
     */
    private boolean selfTerminating;

    /**
     * Bogus event used to signify that the event is not known...
     */
    public static Event UNKNOWN;

    /**
     * Name of unknown event.
     */
    public static String UnknownName = "(unknown)";

    public Event() {

    }

    public Event( String name ) {
        super( name );
    }

    public Place getScope() {
        return scope;
    }

    public void setScope( Place scope ) {
        this.scope = scope;
    }

    public boolean isSelfTerminating() {
        return selfTerminating;
    }

    public void setSelfTerminating( boolean selfTerminating ) {
        this.selfTerminating = selfTerminating;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean validates( ModelEntity entity, Place locale ) {
        Event event = (Event) entity;
        return super.validates( event, locale )
            && selfTerminating == event.isSelfTerminating()
            && ModelEntity.implies( event.getScope(), scope, locale );
    }

    /**
     * {@inheritDoc}
     */
    public List<? extends GeoLocatable> getImpliedGeoLocatables( QueryService queryService ) {
        List<Event> geoLocatables = new ArrayList<Event>();
        geoLocatables.add( this );
        return geoLocatables;
    }

    /**
     * {@inheritDoc}
     */
    public GeoLocation geoLocate() {
        return scope != null ? scope.geoLocate() : null;
    }

    /**
     * {@inheritDoc}
     * @param queryService
     */
    public String getGeoMarkerLabel( QueryService queryService ) {
        return scope != null
                ? getName() + " in " + scope.getGeoMarkerLabel( queryService )
                : "";
    }

    /**
     * {@inheritDoc}
     */
    public List<Attachment.Type> getAttachmentTypes() {
        List<Attachment.Type> types = new ArrayList<Attachment.Type>();
        if ( !hasImage() )
            types.add( Attachment.Type.Image );
        types.addAll( super.getAttachmentTypes() );
        return types;
    }

    /**
     * {@inheritDoc}
     */
    public boolean references( ModelObject mo ) {
        return super.references( mo )
                || ModelObject.areIdentical( scope, mo );
    }
}
