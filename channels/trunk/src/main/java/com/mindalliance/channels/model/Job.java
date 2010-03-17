package com.mindalliance.channels.model;

import com.mindalliance.channels.QueryService;
import com.mindalliance.channels.command.MappedObject;
import com.mindalliance.channels.geo.GeoLocatable;
import com.mindalliance.channels.geo.GeoLocation;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * A role possibly with jurisdiction and title assigned to an actor by an organization.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Mar 18, 2009
 * Time: 3:17:00 PM
 */
public class Job implements Serializable, Mappable, GeoLocatable {

    /**
     * An actor.
     */
    private Actor actor;

    /**
     * A role.
     */
    private Role role;

    /**
     * A title.
     */
    private String title = "";

    /**
     * A jurisdiction.
     */
    private Place jurisdiction;
    /**
     * The supervisor actor.
     */
    private Actor supervisor;

    /**
     * Primary key for persistence.
     */
    private long id;

    public Job() {
    }

    public Job( Actor actor, Role role, Place jurisdiction ) {
        this.actor = actor;
        this.role = role;
        this.jurisdiction = jurisdiction;
        title = "";
    }

    public String getActorName() {
        if ( actor == null ) return "";
        else return actor.getName();
    }

    public String getActorLastName() {
        if ( actor == null ) return "";
        else return actor.getLastName();
    }

    public void setActor( Actor actor ) {
        this.actor = actor;
    }

    public String getRoleName() {
        if ( role == null ) return "";
        else return role.getName();
    }

    public void setRole( Role role ) {
        this.role = role;
    }

    public String getJurisdictionName() {
        if ( jurisdiction == null ) return "";
        else return jurisdiction.getName();
    }

    public void setJurisdiction( Place jurisdiction ) {
        this.jurisdiction = jurisdiction;
    }

    public String getSupervisorName() {
        if ( supervisor == null ) return "";
        else return supervisor.getName();
    }

    public String getTitle() {
        return title;
    }

    public void setTitle( String title ) {
        this.title = ( title == null ? "" : title );
    }

    public Actor getActor() {
        return actor;
    }

    public Role getRole() {
        return role;
    }

    public Place getJurisdiction() {
        return jurisdiction;
    }

    public Actor getSupervisor() {
        return supervisor;
    }

    public void setSupervisor( Actor supervisor ) {
        this.supervisor = supervisor;
    }

    /**
     * Job as resource specifiction.
     *
     * @param organization an organization
     * @return a resource spec
     */
    public ResourceSpec resourceSpec( Organization organization ) {
        ResourceSpec resourceSpec = new ResourceSpec();
        resourceSpec.setActor( actor );
        resourceSpec.setRole( role );
        resourceSpec.setJurisdiction( jurisdiction );
        resourceSpec.setOrganization( organization );
        return resourceSpec;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return ( getActorName() )
                + ( getTitle().isEmpty() ? "" : " (" + title + " )" )
                + ( getRoleName().isEmpty() ? "" : " as " + getRoleName() )
                + ( getJurisdictionName().isEmpty() ? "" : " for " + getJurisdictionName() );
    }

    /**
     * Get descriptive text.
     *
     * @return a string
     */
    public String getLabel() {
        return toString();
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals( Object obj ) {
        if ( obj instanceof Job ) {
            Job job = (Job) obj;
            return actor == job.getActor()
                    && role == job.getRole()
                    && jurisdiction == job.getJurisdiction();
        } else {
            return false;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        int hash = 1;
        hash = hash * 31 + actor.hashCode();
        hash = hash * 31 + role.hashCode();
        if ( jurisdiction != null ) hash = hash * 31 + jurisdiction.hashCode();
        return hash;
    }

    /**
     * Extract a job from a resource spec.
     * Returns null if none implied.
     *
     * @param resourceSpec a resource spec
     * @return a job
     */
    public static Job from( ResourceSpec resourceSpec ) {
        Actor actor = resourceSpec.getActor();
        if ( actor == null ) return null;
        Role role = resourceSpec.getRole();
        if ( role == null ) role = Role.UNKNOWN;
        Place jurisdiction = resourceSpec.getJurisdiction();
        return new Job(
                actor,
                role,
                jurisdiction );
    }

    public long getId() {
        return id;
    }

    public void setId( long id ) {
        this.id = id;
    }

    /**
     * The job is sufficiently defined (actor and role are set).
     *
     * @return a boolean
     */
    public boolean isDefined() {
        return actor != null && role != null;
    }

    /**
     * {@inheritDoc}
     */
    public MappedObject map() {
        MappedObject mappedObject = new MappedObject( Job.class );
        mappedObject.set( "id", id );
        mappedObject.set( "actor", actor );
        mappedObject.set( "role", role );
        mappedObject.set( "jurisdiction", jurisdiction );
        mappedObject.set( "title", title );
        mappedObject.set( "supervisor", supervisor );
        return mappedObject;
    }

    /**
     * {@inheritDoc}
     */
    public GeoLocation geoLocate() {
        return jurisdiction != null ? jurisdiction.geoLocate() : null;
    }

    public List<? extends GeoLocatable> getImpliedGeoLocatables( QueryService queryService ) {
        List<Place> geoLocatables = new ArrayList<Place>();
        if ( jurisdiction != null )
            geoLocatables.addAll( queryService.listEntitiesNarrowingOrEqualTo( jurisdiction ) );
        return geoLocatables;
    }

    /**
     * {@inheritDoc}
     */
    public String getGeoMarkerLabel() {
        return jurisdiction != null ? jurisdiction.getGeoMarkerLabel() : "";
    }

    /**
     * Whether this references a model object.
     *
     * @param mo a model object
     * @return a boolean
     */
    public boolean references( ModelObject mo ) {
        return ModelObject.areIdentical( actor, mo )
                || ModelObject.areIdentical( role, mo )
                || ModelObject.areIdentical( jurisdiction, mo )
                || ModelObject.areIdentical( supervisor, mo );
    }

}
