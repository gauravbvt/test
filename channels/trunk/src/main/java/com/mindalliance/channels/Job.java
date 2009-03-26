package com.mindalliance.channels;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
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
     * An actor's name.
     */
    private String actorName = "";

    /**
     * A role's name.
     */
    private String roleName = "";

    /**
     * A title.
     */
    private String title = "";

    /**
     * A jurisdiction.
     */
    private String jurisdictionName = "";

    /**
     * Primary key for persistence.
     */
    private long id;

    public Job() {
    }

    public Job( String actorName, String roleName, String jurisdictionName ) {
        this.actorName = actorName;
        this.roleName = roleName;
        this.jurisdictionName = jurisdictionName;
        title = "";
    }

    public String getActorName() {
        return actorName;
    }

    public void setActorName( String actorName ) {
        this.actorName = actorName;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName( String roleName ) {
        this.roleName = roleName;
    }

    public String getJurisdictionName() {
        return jurisdictionName;
    }

    public void setJurisdictionName( String jurisdictionName ) {
        this.jurisdictionName = jurisdictionName;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle( String title ) {
        this.title = ( title == null ? "" : title );
    }

    /**
     * Job as resource specifiction.
     *
     * @param organization an organization
     * @param service a service
     * @return a resource spec
     */
    public ResourceSpec resourceSpec( Organization organization, Service service ) {
        ResourceSpec resourceSpec = new ResourceSpec();
        if ( actorName != null && !actorName.isEmpty() )
            resourceSpec.setActor( service.findOrCreate( Actor.class, actorName ) );
        if ( roleName != null && !roleName.isEmpty() )
            resourceSpec.setRole( service.findOrCreate( Role.class, roleName ) );
        if ( jurisdictionName != null && !jurisdictionName.isEmpty() )
            resourceSpec.setJurisdiction( service.findOrCreate( Place.class, jurisdictionName ) );
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
            return getActorName().equals( job.getActorName() )
                    && getRoleName().equals( job.getRoleName() )
                    && getJurisdictionName().equals( job.getJurisdictionName() )
                    && getTitle().equals( job.getTitle() );
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
        hash = hash * 31 + getActorName().hashCode();
        hash = hash * 31 + getRoleName().hashCode();
        hash = hash * 31 + getJurisdictionName().hashCode();
        hash = hash * 31 + getTitle().hashCode();
        return hash;
    }

    /**
     * Makes a job from a resource spec.
     *
     * @param resourceSpec a resource spec
     * @return a job
     */
    public static Job from( ResourceSpec resourceSpec ) {
        return new Job(
                resourceSpec.getActor().getName(),
                resourceSpec.getRole().getName(),
                resourceSpec.getJurisdiction().getName() );
    }

    @Id
    @GeneratedValue
    public long getId() {
        return id;
    }

    public void setId( long id ) {
        this.id = id;
    }
}
