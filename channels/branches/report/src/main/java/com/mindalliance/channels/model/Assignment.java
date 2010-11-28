package com.mindalliance.channels.model;

import com.mindalliance.channels.geo.GeoLocatable;
import com.mindalliance.channels.geo.GeoLocation;
import com.mindalliance.channels.query.QueryService;

import java.util.ArrayList;
import java.util.List;

/**
 * A match between an employment and a part.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Oct 28, 2009
 * Time: 9:55:15 AM
 */
public class Assignment implements GeoLocatable, Specable {
    /**
     * Employment playing a part.
     */
    private Employment employment;
    /**
     * Part that implies the assignment.
     */
    private Part part;

    public Assignment( Employment employment, Part part ) {
        assert part != null && employment != null;
        this.part = part;
        this.employment = employment;
    }

    public Part getPart() {
        return part;
    }

    public Employment getEmployment() {
        return employment;
    }

    public Actor getActor() {
        return employment.getActor();
    }

    public Role getRole() {
        return employment.getRole();
    }

    public Organization getOrganization() {
        return employment.getOrganization();
    }


    public Place getJurisdiction() {
        return employment.getJurisdiction();
    }

    /**
     * {@inheritDoc}
     */
    public GeoLocation geoLocate() {
        return part.geoLocate();
    }

    /**
     * {@inheritDoc}
     *
     * @param queryService
     */
    public String getGeoMarkerLabel( QueryService queryService ) {
        return employment.toString() + ", and is assigned to task \"" + part.getTask() + '\"';
    }

    /**
     * {@inheritDoc}
     */
    public List<? extends GeoLocatable> getImpliedGeoLocatables( QueryService queryService ) {
        List<GeoLocatable> geoLocatables = new ArrayList<GeoLocatable>();
        geoLocatables.addAll( employment.getImpliedGeoLocatables( queryService ) );
        geoLocatables.addAll( part.getImpliedGeoLocatables( queryService ) );
        return geoLocatables;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append( employment.toString() );
        sb.append( ", doing task \"" );
        sb.append( part.getTask() );
        sb.append( '\"' );
        return sb.toString();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals( Object obj ) {
        if ( !( obj instanceof Assignment ) ) return false;
        Assignment other = (Assignment) obj;
        return employment.equals( other.getEmployment() )
                && part.equals( other.getPart() );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        int hash = 1;
        hash = hash * 31 + employment.hashCode();
        hash = hash * 31 + part.hashCode();
        return hash;
    }

    /**
     * Get the known, assigned entity.
     *
     * @return a channelable
     */
    public Channelable getChannelable() {
        return getActor().isUnknown() ? getOrganization() : getActor();
    }

    /**
     * Whether an entity is used in defining the assignment.
     *
     * @param entity a model entity
     * @return a boolean
     */
    public boolean hasEntity( ModelEntity entity ) {
        return ModelObject.areIdentical( getActor(), entity )
                || ModelObject.areIdentical( getRole(), entity )
                || ModelObject.areIdentical( getOrganization(), entity )
                || ModelObject.areIdentical( getJurisdiction(), entity );
    }

    /**
     * Get known assignee, either an actor or an organization.
     *
     * @return a model entity
     */
    public ModelEntity getKnownAssignee() {
        return getActor().isUnknown() ? getOrganization() : getActor();
    }

    /**
     * Get where the task is executed, if specified.
     *
     * @return a place
     */
    public Place getLocation() {
        return getPart().getLocation();
    }
}
