package com.mindalliance.channels.model;

import com.mindalliance.channels.query.QueryService;

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

/** An actor's job in an organization (or with no job). */
public class Employment implements GeoLocatable, Specable {

    /** Actor. */
    private Actor actor;

    /** Organization. */
    private Organization organization;

    /** Job. */
    private Job job;

    public Employment( Actor actor ) {
        this.actor = actor;
    }

    public Employment( Organization organization ) {
        actor = Actor.UNKNOWN;
        this.organization = organization;
    }

    public Employment( Organization organization, Job job ) {
        this.organization = organization;
        this.job = job;
        actor = job.getActor();
    }

    public Employment( Actor actor, Organization organization, Job job ) {
        this.actor = actor;
        this.organization = organization;
        this.job = job;
    }

    @Override
    public Actor getActor() {
        return actor == null ? Actor.UNKNOWN : actor;
    }

    @Override
    public Organization getOrganization() {
        return organization == null ? Organization.UNKNOWN : organization;
    }

    /**
     * Get role from job, if any.
     *
     * @return a role or null
     */
    @Override
    public Role getRole() {
        return job == null ? null : job.getRole();
    }

    /**
     * Get jurisdiction from job, if any.
     *
     * @return a place or null
     */
    @Override
    public Place getJurisdiction() {
        return job == null ? null : job.getJurisdiction();
    }

    /**
     * Get supervisor from job, if any.
     *
     * @return an actor or null
     */
    public Actor getSupervisor() {
        return job == null ? null : job.getSupervisor();
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

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append( getActor().getName() );
        if ( getOrganization().isUnknown() )
            sb.append( ", unemployed" );
        else {
            sb.append( ", " );
            sb.append( getRole() );
            sb.append( " at " );
            sb.append( getOrganization().getName() );
            if ( job != null && job.getJurisdiction() != null ) {
                sb.append( " for " );
                sb.append( job.getJurisdiction().getName() );
            }
        }
        return sb.toString();
    }

    @Override
    public Place getPlaceBasis() {
        return getOrganization().getPlaceBasis();
    }

    @Override
    public List<? extends GeoLocatable> getImpliedGeoLocatables( QueryService queryService ) {
        List<Employment> geoLocatables = new ArrayList<Employment>();
        geoLocatables.add( this );
        return geoLocatables;
    }

    @Override
    public String getGeoMarkerLabel( QueryService queryService ) {
        return getOrganization().getGeoMarkerLabel( queryService );
    }

    public boolean equals( Object obj ) {
        if ( obj instanceof Employment ) {
            Employment other = (Employment) obj;

            if ( getActor() == other.getActor() && getOrganization() == other.getOrganization() )
                return job == null ? other.getJob() == null : job.equals( other.getJob() );
        }

        return false;
    }

    public int hashCode() {
        int hash = 1;
        hash = hash * 31 + getActor().hashCode();
        if ( job != null )
            hash = hash * 31 + job.hashCode();
        return hash;
    }
}
