package com.mindalliance.channels;

import com.mindalliance.channels.pages.Project;
import com.mindalliance.channels.util.SemMatch;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Collections;

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
public class ResourceSpec implements Serializable {

    /**
     * Used in calculating hashCode
     */
    private static int SEED = 31;

    /**
     * The resource spec's actor
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
    private Place jurisdiction;
    /**
     * Channels used to reach actor
     */
    private List<String> channels;

    public ResourceSpec() {
    }

    public ResourceSpec( Part part ) {
        actor = part.getActor();
        role = part.getRole();
        organization = part.getOrganization();
        jurisdiction = part.getJurisdiction();
        channels = new ArrayList<String>();
    }

    /**
     * Resource factory
     *
     * @param entity an entity
     * @return a new resource spec
     */
    public static ResourceSpec with( Entity entity ) {
        ResourceSpec resourceSpec = new ResourceSpec();
        if ( entity instanceof Actor )
            resourceSpec.setActor( (Actor) entity );
        if ( entity instanceof Organization )
            resourceSpec.setOrganization( (Organization) entity );
        if ( entity instanceof Role )
            resourceSpec.setRole( (Role) entity );
        if ( entity instanceof Place )
            resourceSpec.setJurisdiction( (Place) entity );
        return resourceSpec;
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

    public Place getJurisdiction() {
        return jurisdiction;
    }

    public void setJurisdiction( Place jurisdiction ) {
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
        if ( !( obj instanceof ResourceSpec ) ) return false;
        ResourceSpec resourceSpec = (ResourceSpec) obj;
        return actor == resourceSpec.getActor()
                && role == resourceSpec.getRole()
                && organization == resourceSpec.getOrganization()
                && SemMatch.samePlace( jurisdiction, resourceSpec.getJurisdiction() );
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
                && part.getOrganization() == null
                && part.getJurisdiction() == null);
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
     *
     * @return a boolean
     */
    public boolean isActorOnly() {
        return actor != null && role == null && organization == null && jurisdiction == null;
    }

    /**
     * Resource is an unqualified role
     *
     * @return a boolean
     */
    public boolean isRoleOnly() {
        return actor == null && role != null && organization == null && jurisdiction == null;
    }

    /**
     * Resource is an unqualified organization
     *
     * @return a boolean
     */
    public boolean isOrganizationOnly() {
        return actor == null && role == null && organization != null && jurisdiction == null;
    }

    /**
     * Resource is an unqualified jurisdiction
     *
     * @return a boolean
     */
    public boolean isJurisdictionOnly() {
        return actor == null && role == null && organization == null && jurisdiction != null;
    }

    /**
     * Resource is not qualified by an actor
     *
     * @return a boolean
     */
    public boolean isAnyActor() {
        return actor == null;
    }

    /**
     * Resource is not qualified by an organization
     *
     * @return a boolean
     */
    public boolean isAnyOrganization() {
        return organization == null;
    }

    /**
     * Resource is not qualified by a role
     *
     * @return a boolean
     */
    public boolean isAnyRole() {
        return role == null;
    }

    /**
     * Resource is not qualified by a jurisdiction
     *
     * @return a boolean
     */
    public boolean isAnyJurisdiction() {
        return jurisdiction == null;
    }

    /**
     * Resource is qualified by an actor, role, organization or jurisdiction?
     *
     * @return a boolean
     */
    public boolean isEmpty() {
        return isAnyActor() && isAnyRole() && isAnyOrganization() && isAnyJurisdiction();
    }

    /**
     * Composes a name for the resource spec
     *
     * @return a string
     */
    public String getName() {
        StringBuilder sb = new StringBuilder();
        sb.append( isAnyActor() ? "Anyone" : actor.getName() );
        if ( !isAnyRole() ) {
            sb.append( " as " );
            sb.append( role.getName() );
        }
        if ( !isAnyOrganization() ) {
            sb.append( " for " );
            sb.append( organization.getName() );
        }
        if ( !isAnyJurisdiction() ) {
            sb.append( " in " );
            sb.append( jurisdiction.getName() );
        }
        return sb.toString();
    }

    /**
     * {@inheritDoc}
     */
    public String toString() {
        return getName();
    }

    /**
     * Is this resource spec generalized by another resource spec?
     *
     * @param other a resource spec
     * @return a boolean
     */
    public boolean narrowsOrEquals( ResourceSpec other ) {
        if ( equals( other ) ) return true;
        if ( actor != other.getActor() && !other.isAnyActor() )
            return false;
        if ( role != other.getRole() && !other.isAnyRole() )
            return false;
        // Todo compare inclusion
        if ( organization != other.getOrganization() && !other.isAnyOrganization() )
            return false;
        // Todo compare inclusion, not equality
        if ( jurisdiction != other.getJurisdiction() && !other.isAnyJurisdiction() )
            return false;
        return true;
    }

    /**
     * Do two resource specs have common, known elements?
     *
     * @param other another resource spec
     * @return a boolean
     */
    public boolean intersects( ResourceSpec other ) {
        // Try cheap tests
        if ( narrowsOrEquals( other ) || other.narrowsOrEquals( this ) ) {
            return true;
        } else {
            // Try expensive test
            Dao dao = Project.getProject().getDao();
            List<ResourceSpec> resources = dao.findAllResourcesNarrowingOrEqualTo( this );
            List<ResourceSpec> others = dao.findAllResourcesNarrowingOrEqualTo( other );
            return !Collections.disjoint( resources, others );
        }
    }
}
