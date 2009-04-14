package com.mindalliance.channels;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Transient;
import java.io.Serializable;

/**
 * A role possibly with jurisdiction and title assigned to an actor by an organization.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Mar 18, 2009
 * Time: 3:17:00 PM
 */
@Entity
public class Job implements Serializable {

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
        hash = hash * 31 + jurisdiction.hashCode();
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
        Role role = resourceSpec.getRole();
        Place jurisdiction = resourceSpec.getJurisdiction();
        Organization organization = resourceSpec.getOrganization();
        if ( actor == null || role == null || organization == null )
            return null;
        else
            return new Job(
                    actor,
                    role,
                    jurisdiction );
    }

    @Id
    @GeneratedValue
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
    @Transient
    public boolean isDefined() {
        return actor != null && role != null;
    }
}
