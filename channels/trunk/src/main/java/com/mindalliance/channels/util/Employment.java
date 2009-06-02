package com.mindalliance.channels.util;

import com.mindalliance.channels.model.Actor;
import com.mindalliance.channels.model.Job;
import com.mindalliance.channels.model.Organization;

import java.io.Serializable;

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
public class Employment implements Serializable {

    private Actor actor;
    private Organization organization;
    private Job job;

    public Employment( Actor actor ) {
        this.actor = actor;
    }

    public Employment( Actor actor, Organization organization, Job job ) {
        this.actor = actor;
        this.organization = organization;
        this.job = job;
    }

    public Actor getActor() {
        return actor;
    }

    public Organization getOrganization() {
        return organization;
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
        sb.append( actor.getName() );
        if ( organization == null ) {
            sb.append( " is unemployed" );
        } else {
            assert job != null;
            sb.append( " is employed by " );
            sb.append( organization.getName() );
            sb.append( " as " );
            sb.append( job.getRole() );
            if ( job.getJurisdiction() != null ) {
                sb.append( " for " );
                sb.append( job.getJurisdiction().getName() );
            }
        }
        return sb.toString();
    }
}
