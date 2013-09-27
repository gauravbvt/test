package com.mindalliance.channels.core.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
     * The job is assigned de facto from being hired in another job.
     */
    private boolean linked; // default is false i.e. it is a primary job, one to which users can be explicitly assigned.

    /**
     * Primary key for persistence.
     */
    private long id;

    public Job() {
    }

    public Job( Job job ) {
        actor = job.getActor();
        role = job.getRole();
        jurisdiction = job.getJurisdiction();
        title = job.getTitle();
        supervisor = job.getSupervisor();
        linked = job.isLinked();
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
        return title == null ? getRoleName() : title;
    }

    public String getRawTitle() {
        return title == null ? "" : title;
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

    public boolean isLinked() {
        return linked;
    }

    public void setLinked( boolean linked ) {
        this.linked = linked;
    }

    public boolean isPrimary() {
        return !isLinked();
    }

    /**
     * Job as resource specifiction.
     *
     * @param organization an organization
     * @return a resource spec
     */
    public ResourceSpec resourceSpec( Organization organization ) {
        return new ResourceSpec( actor, role, organization, jurisdiction );
    }

    @Override
    public String toString() {
        return ( getActorName() )
                + ( getTitle().isEmpty() ? "" : " (" + title + ")" )
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


    @Override
    public boolean equals( Object obj ) {
        if ( obj instanceof Job ) {
            Job job = (Job) obj;
            return ModelEntity.areEqualOrNull( actor, job.getActor() )
                    && ModelEntity.areEqualOrNull( role, job.getRole() )
                    && ModelEntity.areEqualOrNull( jurisdiction, job.getJurisdiction() )
                    && ModelEntity.areEqualOrNull( supervisor, job.getSupervisor() )
                    && title.equals( job.getTitle() );
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        int hash = 1;
        if ( actor != null ) hash = hash * 31 + actor.hashCode();
        if ( role != null ) hash = hash * 31 + role.hashCode();
        if ( jurisdiction != null ) hash = hash * 31 + jurisdiction.hashCode();
        if ( supervisor != null ) hash = hash * 31 + supervisor.hashCode();
        hash = hash * 31 + title.hashCode();
        return hash;
    }

    /**
     * Extract a job from a resource spec.
     * Returns null if none implied.
     *
     * @param specable a resource spec
     * @return a job
     */
    public static Job from( Specable specable ) {
        Actor actor = specable.getActor();
        if ( actor == null ) return null;
        Role role = specable.getRole();
        if ( role == null ) role = Role.UNKNOWN;
        Place jurisdiction = specable.getJurisdiction();
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

    @Override
    public void map( Map<String, Object> map ) {
        map.put( "id", id );
        map.put( "actor", actor );
        map.put( "role", role );
        map.put( "jurisdiction", jurisdiction );
        map.put( "title", title );
        map.put( "supervisor", supervisor );
    }

    @Override
    public Place getPlaceBasis() {
        return jurisdiction == null ? null : jurisdiction.getPlaceBasis();
    }

    @Override
    public List<? extends GeoLocatable> getImpliedGeoLocatables() {
        return new ArrayList<Place>();
    }

    @Override
    public String getGeoMarkerLabel() {
        return jurisdiction == null ? ""
                : jurisdiction.getGeoMarkerLabel();
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

    public boolean narrowsOrEquals( Job other, Place locale ) {
        return ( other.getActor() == null || other.getActor().isUnknown()
                || ( getActor() != null && getActor().narrowsOrEquals( other.getActor(), locale ) ) )
                && ( other.getActor() == null || other.getRole().isUnknown()
                || ( getRole() != null && getRole().narrowsOrEquals( other.getRole(), locale ) ) )
                && ( other.getJurisdiction() == null || other.getJurisdiction().isUnknown()
                || ( getJurisdiction() != null && getJurisdiction().narrowsOrEquals( other.getJurisdiction(), locale ) ) );
    }
}
