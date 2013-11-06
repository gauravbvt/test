package com.mindalliance.channels.core.community;

import com.mindalliance.channels.core.model.ModelObject;
import com.mindalliance.channels.core.model.Place;

import java.io.Serializable;

/**
 * Copyright (C) 2008-2013 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 11/5/13
 * Time: 10:41 AM
 */
public class LocationBinding implements Serializable {

    private Place placeholder;
    private Place location;

    public LocationBinding( Place placeholder, Place location ) {
        this.location = location;
        this.placeholder = placeholder;
    }

    public LocationBinding( Place placeholder ) {
        this.placeholder = placeholder;
    }

    public LocationBinding( LocationBinding locationBinding ) {
        this.placeholder = locationBinding.getPlaceholder();
        this.location = locationBinding.getLocation();
    }

    public Place getLocation() {
        return location;
    }

    public Place getPlaceholder() {
        return placeholder;
    }

    public void setLocation( Place location ) {
        this.location = location;
    }

    public boolean isBound() {
        return location != null;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append( "Place \"" )
                .append( placeholder.getName() )
                .append( "\" is bound by " );
        if ( location == null ) {
            sb.append( "nothing" );
        } else {
            sb.append( "place \"" )
                    .append( location.getName() );
        }
        return sb.toString();
    }

    @Override
    public boolean equals( Object object ) {
        if ( object instanceof LocationBinding ) {
            LocationBinding other = (LocationBinding)object;
            return placeholder.equals( other.getPlaceholder() )
                    && ModelObject.areEqualOrNull( location, other.getLocation() );
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        int hash = 1;
        hash = hash * 31 + placeholder.hashCode();
        if ( location != null )
            hash = hash * 31 + location.hashCode();
        return hash;
    }

}
