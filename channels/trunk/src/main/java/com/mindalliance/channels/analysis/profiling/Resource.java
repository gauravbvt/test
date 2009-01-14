package com.mindalliance.channels.analysis.profiling;

import com.mindalliance.channels.Actor;
import com.mindalliance.channels.Jurisdiction;
import com.mindalliance.channels.Organization;
import com.mindalliance.channels.Part;
import com.mindalliance.channels.Resourceable;
import com.mindalliance.channels.Role;
import com.mindalliance.channels.util.SemMatch;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * A Resource is an actor in a role for an organization with a jurisdiction.
 * Actor, role, organization (any two), and jurisdiction may be null.
 * A Resource has a number of channels that can be used to communicate with it.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Jan 8, 2009
 * Time: 8:26:05 PM
 */
public class Resource implements Serializable {

    /**
     * Used in calculating hashCode
     */
    private static int SEED = 31;

    /**
     * The resource's actor
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
     * Channels used to reach actor
     */
    private List<String> channels;

    public Resource() {
    }

    public Resource( Part part ) {
        actor = part.getActor();
        role = part.getRole();
        organization = part.getOrganization();
        jurisdiction = part.getJurisdiction();
        channels = new ArrayList<String>();
    }

    public static Resource with ( Resourceable resourceable ) {
        Resource resource = new Resource();
        if ( resourceable instanceof Actor ) resource.setActor( (Actor)resourceable );
        if ( resourceable instanceof Organization ) resource.setOrganization( (Organization)resourceable );
        if ( resourceable instanceof Role ) resource.setRole( (Role)resourceable );
        if ( resourceable instanceof Jurisdiction ) resource.setJurisdiction( (Jurisdiction)resourceable );
        return resource;
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
     *
     * @return channels as string
     */
    public String getChannelsString() {
        if ( channels.isEmpty() ) {
            return "(No channel)";
        } else {
            StringBuilder sb = new StringBuilder();
            Iterator<String> iter = channels.iterator();
            while ( iter.hasNext() ) {
                String channel = iter.next();
                sb.append( channel );
                if ( iter.hasNext() ) sb.append( ", " );
            }
            return sb.toString();
        }
    }

    /**
     * {@inheritDoc}
     */
    public boolean equals( Object obj ) {
        if ( !( obj instanceof Resource ) ) return false;
        Resource resource = (Resource) obj;
        return actor == resource.getActor()
                && role == resource.getRole()
                && organization == resource.getOrganization()
                && SemMatch.sameLocation( jurisdiction, resource.getJurisdiction() );
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
     *
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
     * A part describes a Resource is not all of actor, roel and organization are null.
     *
     * @param part a part
     * @return boolean whether the part describes a resource
     */
    public static boolean hasResource( Part part ) {
        return !( part.getActor() == null
                && part.getRole() == null
                && part.getOrganization() == null );
    }

    /**
     * Gets the actor's name or an empty string if no actor
     *
     * @return a string
     */
    public String getActorName() {
        return ( actor == null ) ? "" : actor.getName();
    }

    /**
     * Gets the organization's name or an empty string if no organization
     *
     * @return a string
     */
    public String getOrganizationName() {
        return ( organization == null ) ? "" : organization.getName();
    }

    /**
     * Gets the jurisdiction's name or an empty string if no jurisdiction
     *
     * @return a string
     */
    public String getJurisdictionName() {
        return ( jurisdiction == null ) ? "" : jurisdiction.getName();
    }

    /**
     * Resource is an unqualified actor
     * @return a boolean
     */
    public boolean isActorOnly() {
        return actor != null && role == null && organization == null && jurisdiction == null;
    }

    /**
     * Resource is an unqualified role
     * @return a boolean
     */
    public boolean isRoleOnly() {
        return actor == null && role != null && organization == null && jurisdiction == null;
    }

    /**
     * Resource is an unqualified organization
     * @return a boolean
     */
    public boolean isOrganizationOnly() {
        return actor == null && role == null && organization != null && jurisdiction == null;
    }

    /**
     * Resource is an unqualified jurisdiction
     * @return a boolean
     */
    public boolean isJurisdictionOnly() {
        return actor == null && role == null && organization == null && jurisdiction != null;
    }

    /**
     * Resource is qualified by an actor
     * @return a boolean
     */
    public boolean hasActor() {
        return actor != null;
    }

    /**
     * Resource is qualified by an organization
     * @return a boolean
     */
    public boolean hasOrganization() {
        return organization != null;
    }

    /**
     * Resource is qualified by a role
     * @return a boolean
     */
    public boolean hasRole() {
        return role != null;
    }

    /**
     * Resource is qualified by a jurisdiction
     * @return a boolean
     */
    public boolean hasJurisdiction() {
        return jurisdiction != null;
    }
}
