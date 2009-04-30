package com.mindalliance.channels.model;

import com.mindalliance.channels.util.SemMatch;

/**
 * A Resource is an actor in a role for an organization with a jurisdiction.
 * Actor, role, organization (any two), and jurisdiction may be null.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Jan 8, 2009
 * Time: 8:26:05 PM
 */

public class ResourceSpec extends ModelObject {   // TODO - remove extends ModelObject

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

    /**
     * In jurisdiction
     */
    private Place jurisdiction;

    public ResourceSpec() {
    }

    public ResourceSpec( ResourceSpec resourceSpec ) {
        actor = resourceSpec.getActor();
        role = resourceSpec.getRole();
        organization = resourceSpec.getOrganization();
        jurisdiction = resourceSpec.getJurisdiction();
    }

    public ResourceSpec( Part part ) {
        fromPart( part );
    }

    public ResourceSpec( Channelable channelable ) {
        if ( channelable instanceof Actor )
            actor = (Actor) channelable;
        else if ( channelable instanceof Organization )
            organization = (Organization) channelable;
        else if ( channelable instanceof Flow ) {
            fromPart( ( (Flow) channelable ).getContactedPart() );
        } else {
            throw new IllegalArgumentException( "Not a channelable: " + channelable );
        }
    }

    private void fromPart( Part part ) {
        if ( part != null ) {
            actor = part.getActor();
            role = part.getRole();
            organization = part.getOrganization();
            jurisdiction = part.getJurisdiction();
        }
    }

    /**
     * Resource factory.
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
    public String getActorName() {
        return actor == null ? "" : actor.getName();
    }

    /**
     * Gets the organization's name or an empty string if no organization
     *
     * @return a string
     */
    public String getOrganizationName() {
        return organization == null ? "" : organization.getName();
    }

    /**
     * Gets the jurisdiction's name or an empty string if no jurisdiction
     *
     * @return a string
     */
    public String getJurisdictionName() {
        return jurisdiction == null ? "" : jurisdiction.getName();
    }

    /**
     * Resource is an unqualified actor
     *
     * @return a boolean
     */
    public boolean isActor() {
        return actor != null;
    }

    /**
     * Resource is an unqualified role
     *
     * @return a boolean
     */
    public boolean isRole() {
        return actor == null && role != null;
    }

    /**
     * Resource is an unqualified organization
     *
     * @return a boolean
     */
    public boolean isOrganization() {
        return actor == null && role == null && organization != null;
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
     * Resource is anyone?
     *
     * @return a boolean
     */
    public boolean isAnyone() {
        return isAnyActor() && isAnyRole() && isAnyOrganization() /*&& isAnyJurisdiction()*/;
    }

    /**
     * Composes a name for the resource spec
     *
     * @return a string
     */
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
     * Whether this resource spec matches another.
     * @param other a resource spec
     * @param precisely a boolean - true -> must be equal, false -> must be equal or more narrow
     * @return a boolean
     */
    public boolean matches( ResourceSpec other, boolean precisely ) {
        return precisely
                ? equals( other )
                : narrowsOrEquals( other );
    }

    /**
     * Is this resource spec generalized by another resource spec?
     *
     * @param other a resource spec
     * @return a boolean
     */
    public boolean narrowsOrEquals( ResourceSpec other ) {
        if ( other.isAnyone() ) return false;
        if ( equals( other ) ) return true;
        if ( actor != other.getActor() && !other.isAnyActor() )
            return false;
        if ( role != other.getRole() && !other.isAnyRole() )
            return false;
        // Todo compare inclusion
        /*
        if ( !other.isAnyOrganization()
                && ( organization == null
                || !organization.isSameOrWithin( other.getOrganization() ) ) )
            return false;

         */
        if ( organization != other.getOrganization() && !other.isAnyOrganization() )
            return false;
        // Todo compare inclusion, not equality
        return !( jurisdiction != other.getJurisdiction() && !other.isAnyJurisdiction() );
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
    public boolean isEntityOnly() {
        int count = 0;
        if ( actor != null ) count++;
        if ( role != null ) count++;
        if ( organization != null ) count++;
        if ( jurisdiction != null ) count++;
        return count == 1;
    }

    /**
     * The higher the specificity, the higher the value.
     *
     * @return a specificity value
     */
    public int specificity() {
        int val = 0;
        if ( !isAnyActor() ) val += 100;
        if ( !isAnyRole() ) val += 10;
        if ( !isAnyJurisdiction() ) val += 5;
        if ( !isAnyOrganization() ) val += 1;
        return val;
    }

    /**
     * Is a job specified (actor and role set)?
     *
     * @return a boolean
     */
    public boolean hasJob() {
        return actor != null && role != null;
    }

    /**
     * WHether the source spec contains an entity in its definition.
     * @param entity an entity
     * @return a boolean
     */
    public boolean hasEntity( ModelObject entity ) {
        assert entity.isEntity();
        if (entity instanceof Actor) {
            return actor == entity;
        } else if (entity instanceof Role) {
            return role == entity;
        } else if (entity instanceof Organization) {
            return organization == entity;
        } else  {
            return jurisdiction == entity;
        }
    }
}
