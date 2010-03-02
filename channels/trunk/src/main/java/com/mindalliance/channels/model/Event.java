package com.mindalliance.channels.model;

import com.mindalliance.channels.QueryService;
import com.mindalliance.channels.attachments.Attachment;
import com.mindalliance.channels.geo.GeoLocatable;
import com.mindalliance.channels.geo.GeoLocation;

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
    private static String UnknownName = "(unknown)";

    public Event() {

    }

    public Event( String name ) {
        super( name );
    }

    /**
     * Create immutables.
     *
     * @param queryService a query service
     */
    public static void createImmutables( QueryService queryService ) {
        UNKNOWN = queryService.findOrCreate( Event.class, UnknownName );
        UNKNOWN.makeImmutable();
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
    public boolean isDefinedUsing( ModelEntity entity ) {
        return super.isDefinedUsing( entity )
                || ModelEntity.isEquivalentToOrDefinedUsing( getScope(), entity );
    }

    /**
     * {@inheritDoc}
     */
    protected boolean meetsTypeRequirementTests( ModelEntity entityType ) {
        return isSelfTerminating() == ( (Event) entityType ).isSelfTerminating()
                && ModelEntity.implies( getScope(), ( (Event) entityType ).getScope() );
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
     */
    public String getGeoMarkerLabel() {
        return scope != null
                ? getName() + " in " + scope.getGeoMarkerLabel()
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
