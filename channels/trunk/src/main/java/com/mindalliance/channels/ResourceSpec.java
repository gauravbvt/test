package com.mindalliance.channels;

import com.mindalliance.channels.pages.Project;
import com.mindalliance.channels.util.SemMatch;

import javax.persistence.CascadeType;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
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
public class ResourceSpec extends ModelObject implements Channelable {

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
    private List<Channel> channels = new ArrayList<Channel>();

    public ResourceSpec() {
    }

    public ResourceSpec( ResourceSpec resourceSpec ) {
        actor = resourceSpec.getActor();
        role = resourceSpec.getRole();
        organization = resourceSpec.getOrganization();
        jurisdiction = resourceSpec.getJurisdiction();
    }

    public ResourceSpec( Part part ) {
        actor = part.getActor();
        role = part.getRole();
        organization = part.getOrganization();
        jurisdiction = part.getJurisdiction();
        channels = new ArrayList<Channel>();
    }

    /**
     * Resource factory
     *
     * @param entity an entity
     * @return a new resource spec
     */
    public static ResourceSpec with( ModelObject entity ) {
        assert entity.isEntity();
        ResourceSpec resourceSpec = new ResourceSpec();
        if ( entity instanceof Actor )
            resourceSpec.setActor( (Actor) entity );
        else if ( entity instanceof Organization )
            resourceSpec.setOrganization( (Organization) entity );
        else if ( entity instanceof Role )
            resourceSpec.setRole( (Role) entity );
        else if ( entity instanceof Place )
            resourceSpec.setJurisdiction( (Place) entity );
        else {
            throw new IllegalArgumentException( "Not an entity: " + entity );
        }
        return resourceSpec;
    }

    @ManyToOne( cascade = CascadeType.PERSIST )
    public Actor getActor() {
        return actor;
    }

    public void setActor( Actor actor ) {
        this.actor = actor;
    }

    @ManyToOne( cascade = CascadeType.PERSIST )
    public Role getRole() {
        return role;
    }

    public void setRole( Role role ) {
        this.role = role;
    }

    @ManyToOne( cascade = CascadeType.PERSIST )
    public Organization getOrganization() {
        return organization;
    }

    public void setOrganization( Organization organization ) {
        this.organization = organization;
    }

    @ManyToOne( cascade = CascadeType.PERSIST )
    public Place getJurisdiction() {
        return jurisdiction;
    }

    public void setJurisdiction( Place jurisdiction ) {
        this.jurisdiction = jurisdiction;
    }

    public List<Channel> getChannels() {
        return channels;
    }

    public void setChannels( List<Channel> channels ) {
        this.channels = channels;
    }

    public List<Channel> getEffectiveChannels() {
        return getChannels();
    }

    /**
     * Add a channel to the contact info
     *
     * @param addedChannels a Channel
     */
    public void addChannels( Collection<Channel> addedChannels ) {
        for ( Channel c : addedChannels ) {
            if ( !channels.contains( c ) ) channels.add( c );
        }
    }

    /**
     * {@inheritDoc
     */
    @Transient
    public String getChannelsString() {
        return Channel.toString( allChannels() );
    }

    /**
     * {@inheritDoc}
     */
    @Override
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
    @Override
    public int hashCode() {
        int hash = 1;
        if ( actor != null ) hash = hash * 31 + actor.hashCode();
        if ( role != null ) hash = hash * 31 + role.hashCode();
        if ( organization != null ) hash = hash * 31 + organization.hashCode();
        if ( jurisdiction != null ) hash = hash * 31 + jurisdiction.hashCode();
        return hash;
    }

    /**
     * {@inheritDoc}
     */
    public void addChannel( Channel channel ) {
        channels.add( channel );
    }

    /**
     * {@inheritDoc}
     */
    public void removeChannel( Channel channel ) {
        channels.remove( channel );
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
                && part.getJurisdiction() == null );
    }

    /**
     * Gets the actor's name or an empty string if no actor
     *
     * @return a string
     */
    @Transient
    public String getActorName() {
        return actor == null ? "" : actor.getName();
    }

    /**
     * Gets the organization's name or an empty string if no organization
     *
     * @return a string
     */
    @Transient
    public String getOrganizationName() {
        return organization == null ? "" : organization.getName();
    }

    /**
     * Gets the jurisdiction's name or an empty string if no jurisdiction
     *
     * @return a string
     */
    @Transient
    public String getJurisdictionName() {
        return jurisdiction == null ? "" : jurisdiction.getName();
    }

    /**
     * Resource is an unqualified actor
     *
     * @return a boolean
     */
    @Transient
    public boolean isActorOnly() {
        return actor != null && role == null && organization == null && jurisdiction == null;
    }

    /**
     * Resource is an unqualified role
     *
     * @return a boolean
     */
    @Transient
    public boolean isRoleOnly() {
        return actor == null && role != null && organization == null && jurisdiction == null;
    }

    /**
     * Resource is an unqualified organization
     *
     * @return a boolean
     */
    @Transient
    public boolean isOrganizationOnly() {
        return actor == null && role == null && organization != null && jurisdiction == null;
    }

    /**
     * Resource is an unqualified jurisdiction
     *
     * @return a boolean
     */
    @Transient
    public boolean isJurisdictionOnly() {
        return actor == null && role == null && organization == null && jurisdiction != null;
    }

    /**
     * Resource is not qualified by an actor
     *
     * @return a boolean
     */
    @Transient
    public boolean isAnyActor() {
        return actor == null;
    }

    /**
     * Resource is not qualified by an organization
     *
     * @return a boolean
     */
    @Transient
    public boolean isAnyOrganization() {
        return organization == null;
    }

    /**
     * Resource is not qualified by a role
     *
     * @return a boolean
     */
    @Transient
    public boolean isAnyRole() {
        return role == null;
    }

    /**
     * Resource is not qualified by a jurisdiction
     *
     * @return a boolean
     */
    @Transient
    public boolean isAnyJurisdiction() {
        return jurisdiction == null;
    }

    /**
     * Resource is anyone?
     *
     * @return a boolean
     */
    @Transient
    public boolean isAnyone() {
        return isAnyActor() && isAnyRole() && isAnyOrganization() /*&& isAnyJurisdiction()*/;
    }

    /**
     * Composes a name for the resource spec
     *
     * @return a string
     */
    @Transient
    public String getName() {
        StringBuilder sb = new StringBuilder();
        if ( !isAnyRole() ) {
            sb.append( isAnyActor() ? "Any " : actor.getName() + " as " );
            sb.append( role.getName() );
        } else {
            sb.append( isAnyActor() ? "Anyone" : actor.getName() );
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
    @Override
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
        /*
        if ( !other.isAnyOrganization()
                && ( organization == null || !organization.isSameOrWithin( other.getOrganization() ) ) )
            return false;

         */
        if ( organization != other.getOrganization() && !other.isAnyOrganization() )
            return false;
        // Todo compare inclusion, not equality
        return !( jurisdiction != other.getJurisdiction() && !other.isAnyJurisdiction() );
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
            Service service = Project.service();
            List<ResourceSpec> resources = service.findAllResourcesNarrowingOrEqualTo( this );
            List<ResourceSpec> others = service.findAllResourcesNarrowingOrEqualTo( other );
            return !Collections.disjoint( resources, others );
        }
    }

    /**
     * Return most specific entity composing this resource specification
     *
     * @return an entity
     */
    public ModelObject mostSpecificEntity() {
        ModelObject mostSpecific = null;
        if ( actor != null ) mostSpecific = actor;
        else if ( jurisdiction != null ) mostSpecific = jurisdiction;
        else if ( role != null ) mostSpecific = role;
        else if ( organization != null ) mostSpecific = organization;
        return mostSpecific;
    }

    /**
     * Whether the resource is defined by a single entity
     *
     * @return a boolean
     */
    @Transient
    public boolean isEntityOnly() {
        int count = 0;
        if ( actor != null ) count++;
        if ( role != null ) count++;
        if ( organization != null ) count++;
        if ( jurisdiction != null ) count++;
        return count == 1;
    }

    /**
     * Whether the resource spec is not solely implied by one or more parts
     *
     * @return a boolean
     */
    @Transient
    public boolean isPredefined() {
        return isEntityOnly() || isPermanent();
    }

    /**
     * Whether the resource spec exists independently of parts
     *
     * @return a boolean
     */
    @Transient
    public boolean isPermanent() {
        return Project.service().isPermanent( this );
    }

    /**
     * String describing the resource spec's origin
     *
     * @return either "a priori" or "from tasks"
     */
    @Transient
    public String getKind() {
        return isPredefined() ? "added" : "from tasks";
    }

    /**
     * {@inheritDoc}
     */
    public List<Channel> allChannels() {
        Service service = Project.service();
        List<Channel> allChannels = new ArrayList<Channel>();
        // Unspecified resources have no channels
        if ( !this.isAnyone() ) {
            List<ResourceSpec> channelables = service.findAllResourcesBroadeningOrEqualTo( this );
            // If resource spec has an actor, include the channel of the more specific resources as well
            if ( !isAnyActor() ) {
                channelables.addAll( service.findAllResourcesNarrowingOrEqualTo( this ) );
            }
            Collections.sort( channelables, new Comparator<ResourceSpec>() {
                /**{@inheritDoc} */
                public int compare( ResourceSpec rs1, ResourceSpec rs2 ) {
                    int val1 = rs1.specificity();
                    int val2 = rs2.specificity();
                    // put higher specificity first
                    return val1 == val2 ? 0 : ( val1 > val2 ? -1 : 1 );
                }
            } );
            for ( ResourceSpec resourceSpec : channelables ) {
                List<Channel> resourceChannels = resourceSpec.getChannels();
                for ( Channel resourceChannel : resourceChannels ) {
                    if ( !allChannels.contains( resourceChannel ) )
                        allChannels.add( resourceChannel );
                }
            }
        }
        return allChannels;
    }

    // The higher the specificity, the higher the value
    public int specificity() {
        int val = 0;
        if ( !isAnyActor() ) val += 100;
        if ( !isAnyRole() ) val += 10;
        if ( !isAnyJurisdiction() ) val += 5;
        if ( !isAnyOrganization() ) val += 1;
        return val;
    }
}
