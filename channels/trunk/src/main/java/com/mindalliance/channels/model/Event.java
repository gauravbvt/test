package com.mindalliance.channels.model;

import com.mindalliance.channels.attachments.Attachment;
import com.mindalliance.channels.geo.GeoLocatable;
import com.mindalliance.channels.geo.GeoLocation;

import javax.persistence.Entity;
import javax.persistence.Transient;
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
@Entity
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
    public static final Event UNKNOWN;

    static {
        UNKNOWN = new Event();
        UNKNOWN.setName( "(unknown)" );
        UNKNOWN.setId( 10000000L - 2 );
    }

    public Event() {

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
     @Transient
    public GeoLocation geoLocate() {
        return scope != null ? scope.geoLocate() : null;
    }

    /**
      * {@inheritDoc}
      */
     @Transient
    public String getGeoMarkerLabel() {
        return scope != null
                ? getName() + " in " + scope.getGeoMarkerLabel()
                : "";
    }

    /**
     * {@inheritDoc}
     */
    @Transient
    public List<Attachment.Type> getAttachmentTypes() {
        List<Attachment.Type> types = new ArrayList<Attachment.Type>();
        if ( !hasImage() )
            types.add( Attachment.Type.Image );
        types.addAll( super.getAttachmentTypes() );
        return types;
    }
   
}
