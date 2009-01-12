package com.mindalliance.channels.analysis.profiling;

import com.mindalliance.channels.Actor;
import com.mindalliance.channels.Role;
import com.mindalliance.channels.Organization;
import com.mindalliance.channels.Part;
import com.mindalliance.channels.Jurisdiction;
import com.mindalliance.channels.util.SemMatch;

import java.io.Serializable;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;

/**
 * A Job is an actor in a role for an organization with a jurisdiction.
 * Actor, role, organization (any two), and jurisdiction may be null.
 * A Job has a number a channels with to communicate with the actor in this job.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Jan 8, 2009
 * Time: 8:26:05 PM
 */
public class Job implements Serializable {

    /**
     * Used in calculating hashCode
     */
    private static int SEED = 31;

    /**
     * The actor with the job
     */
    private Actor actor;
    /**
     * Role played
     */
    private Role role;
    /**
     * With organization
     */
    private Organization organization;
    // TODO -- should be a Jurisdiction, not a String
    /**
     * In jurisdiction
     */
    private Jurisdiction jurisdiction;
    /**
     * Channels used to reach actor in job
     */
    private List<String> channels;

    public Job() {
    }

    public Job( Part part ) {
        assert hasJob( part );
        actor = part.getActor();
        role = part.getRole();
        organization = part.getOrganization();
        jurisdiction = part.getJurisdiction();
        channels = new ArrayList<String>();
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

    public Organization getOrganization() {
        return organization;
    }

    public void setOrganization( Organization organization ) {
        this.organization = organization;
    }

    public Jurisdiction getJurisdiction() {
        return jurisdiction;
    }

    public void setJurisdiction( Jurisdiction jurisdiction ) {
        this.jurisdiction = jurisdiction;
    }

    public List<String> getChannels() {
        return channels;
    }

    public void setChannels( List<String> channels ) {
        this.channels = channels;
    }

    /**
     * Get string collating channels
     * @return channels as string
     */
    public String getChannelsString() {
        StringBuilder sb = new StringBuilder();
        Iterator<String> iter = channels.iterator();
        while ( iter.hasNext() ) {
            String channel = iter.next();
            sb.append( channel );
            if ( iter.hasNext() ) sb.append( ", " );
        }
        return sb.toString();
    }

    /**
      * {@inheritDoc}
      */
    public boolean equals( Object obj ) {
        if ( !( obj instanceof Job ) ) return false;
        Job job = (Job) obj;
        return actor == job.getActor()
                && role == job.getRole()
                && organization == job.getOrganization()
                && SemMatch.sameLocation( jurisdiction, job.getJurisdiction() );
    }

    /**
      * {@inheritDoc}
      */
    public int hashCode() {
        int hash = 1;
        if ( actor != null ) hash = hash * SEED + actor.hashCode();
        if ( role != null ) hash = hash * SEED + role.hashCode();
        if ( organization != null ) hash = hash * SEED + organization.hashCode();
        if ( jurisdiction != null ) hash = hash * SEED + jurisdiction.hashCode();
        return hash;
    }

    /**
     * Add channel to list if not redundant
     * @param ch string channel to add
     */
    public void addChannel( String ch ) {
        boolean contained = false;
        for ( String channel : channels ) {
            if ( SemMatch.same( channel, ch ) ) contained = true;
        }
        if ( !contained ) channels.add( ch );
    }

    /**
     * A part describes a Job is not all of actor, roel and organization are null.
     * @param part a part
     * @return boolean whether the part has a job
     */
    public static boolean hasJob( Part part ) {
        return !(part.getActor() == null
                && part.getRole() == null
                && part.getOrganization() == null);
    }

    /**
     * Gets the actor's name or an empty string if no actor
     * @return a string
     */
    public String getActorName() {
        return (actor == null) ? "" : actor.getName();
    }

    /**
     * Gets the organization's name or an empty string if no organization
     * @return a string
     */
    public String getOrganizationName() {
        return (organization == null) ? "" : organization.getName();
    }

    /**
     * Gets the jurisdiction's name or an empty string if no jurisdiction
     * @return a string
     */
    public String getJurisdictionName() {
        return (jurisdiction == null) ? "" : jurisdiction.getName();
    }
}
