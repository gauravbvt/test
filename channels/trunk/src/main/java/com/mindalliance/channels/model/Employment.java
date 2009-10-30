package com.mindalliance.channels.model;

import com.mindalliance.channels.QueryService;
import com.mindalliance.channels.geo.GeoLocatable;
import com.mindalliance.channels.geo.GeoLocation;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * An actor's employment by an organization, or by none if organization and job are null.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Jun 1, 2009
 * Time: 3:36:42 PM
 */

/**
 * An actor's job in an organization (or with no job).
 */
public class Employment implements Serializable, GeoLocatable {
    /**
     * Actor.
     */
    private Actor actor;
    /**
     * Organization.
     */
    private Organization organization;
    /**
     * Job.
     */
    private Job job;

    public Employment( Actor actor ) {
        this.actor = actor;
    }

    public Employment( Organization organization ) {
        actor = Actor.UNKNOWN;
        this.organization = organization;
    }

    public Employment( Actor actor, Organization organization, Job job ) {
        this.actor = actor;
        this.organization = organization;
        this.job = job;
    }

    public Actor getActor() {
        if ( actor == null )
            return Actor.UNKNOWN;
        else
            return actor;
    }

    public Organization getOrganization() {
        if ( organization == null )
            return Organization.UNKNOWN;
        else
            return organization;
    }

    /**
     * Get role from job, if any.
     *
     * @return a role or null
     */
    public Role getRole() {
        if ( job != null ) {
            return job.getRole();
        } else {
            return null;
        }
    }

    /**
     * Get jurisdiction from job, if any.
     *
     * @return a place or null
     */
    public Place getJurisdiction() {
        if ( job != null ) {
            return job.getJurisdiction();
        } else {
            return null;
        }
    }

    /**
     * Return location of employing organization, if any.
     *
     * @return a place or null
     */
    public Place getLocation() {
        return getOrganization().getLocation();
    }

    public void setOrganization( Organization organization ) {
        this.organization = organization;
    }

    public Job getJob() {
        return job;
    }

    public void setJob( Job job ) {
        this.job = job;
    }

    /**
     * {@inheritDoc}
     */
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append( getActor().getName() );
        if ( getOrganization().isUnknown() ) {
            sb.append( " is unemployed" );
        } else {
            sb.append( " is employed by " );
            sb.append( getOrganization().getName() );
            sb.append( " as " );
            sb.append( getRole() );
            if ( job != null && job.getJurisdiction() != null ) {
                sb.append( " for " );
                sb.append( job.getJurisdiction().getName() );
            }
        }
        return sb.toString();
    }

    /**
     * {@inheritDoc}
     */
    public GeoLocation geoLocate() {
        return getOrganization().geoLocate();
    }

    /**
     * {@inheritDoc}
     */
    public List<? extends GeoLocatable> getImpliedGeoLocatables( QueryService queryService ) {
        List<Employment> geoLocatables = new ArrayList<Employment>();
        geoLocatables.add( this );
        return geoLocatables;
    }

    /**
     * {@inheritDoc}
     */
    public String getGeoMarkerLabel() {
        return getOrganization().getGeoMarkerLabel();
    }

    /**
     * {@inheritDoc}
     */
    public boolean equals( Object object ) {
        if ( !( object instanceof Employment ) ) return false;
        Employment other = (Employment) object;
        if ( getActor() != other.getActor() ) return false;
        if ( getOrganization() != other.getOrganization() ) return false;
        if ( job == null ) {
            if ( other.getJob() != null ) return false;
        } else if ( other.getJob() == null ) return false;
        return job.equals( other.getJob() );
    }

    /**
     * {@inheritDoc}
     */
    public int hashCode() {
        int hash = 1;
        hash = hash * 31 + getActor().hashCode();
        hash = hash * 31 + getRole().hashCode();
        if (job != null) hash = hash * 31 + job.hashCode();
        return hash;
    }

    /**
     * Employment has known actor and/or organization, and matches part.
     *
     * @param part a part
     * @return a boolean
     */
    public boolean playsPart( Part part ) {
        return !getActor().isUnknown()
                && !getOrganization().isUnknown()
                && ModelEntity.implies( getActor(), part.getActor() )
                && ModelEntity.implies( getRole(), part.getRole() )
                && ModelEntity.implies( getOrganization(), part.getOrganization() )
                && ModelEntity.implies( getJurisdiction(), part.getJurisdiction() );
    }
}
