package com.mindalliance.channels;

import javax.persistence.Entity;
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

    public Job() {
    }

    public Job( Actor actor, Role role ) {
        this.actor = actor;
        this.role = role;
    }

    public Actor getActor() {
        return actor;
    }

    public void setActor( Actor actor ) {
        this.actor = actor;
    }

    public Role getRole() {
        return role;
    }

    public void setRole( Role role ) {
        this.role = role;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle( String title ) {
        this.title = title;
    }

    public Place getJurisdiction() {
        return jurisdiction;
    }

    public void setJurisdiction( Place jurisdiction ) {
        this.jurisdiction = jurisdiction;
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
    public boolean equals( Object obj ) {
        if ( obj instanceof Job ) {
            Job job = (Job) obj;
            return actor == job.getActor()
                    && role == job.getRole()
                    && jurisdiction == job.getJurisdiction()
                    && title.equals( job.getTitle() );
        } else {
            return false;
        }
    }

    /**
     * {@inheritDoc}
     */
    public int hashCode() {
        int hash = 1;
        if ( actor != null ) hash = hash * 31 + actor.hashCode();
        if ( role != null ) hash = hash * 31 + role.hashCode();
        if ( jurisdiction != null ) hash = hash * 31 + jurisdiction.hashCode();
        if ( title != null ) hash = hash * 31 + title.hashCode();
        return hash;
    }
}
