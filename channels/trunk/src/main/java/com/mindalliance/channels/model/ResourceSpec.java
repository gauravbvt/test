package com.mindalliance.channels.model;

import com.mindalliance.channels.util.Matcher;

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
    public static ResourceSpec with( ModelEntity entity ) {
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
            throw new IllegalArgumentException( "Not a resource entity: " + entity );
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
        return ModelObject.areEqualOrNull( actor, resourceSpec.getActor() )
                && ModelObject.areEqualOrNull( role, resourceSpec.getRole() )
                && ModelObject.areEqualOrNull( organization, resourceSpec.getOrganization() )
                && Matcher.samePlace( jurisdiction, resourceSpec.getJurisdiction() );
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
     * Gets the actor's name or an empty string if no actor
     *
     * @return a string
     */
    public String getActorName() {
        return actor == null ? Actor.UNKNOWN.getName() : actor.getName();
    }

    /**
     * Gets the organization's name or an empty string if no organization
     *
     * @return a string
     */
    public String getOrganizationName() {
        return organization == null ? Organization.UNKNOWN.getName() : organization.getName();
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
        return actor == null || actor.equals( Actor.UNKNOWN );
    }


    /**
     * Resource is not qualified by an organization
     *
     * @return a boolean
     */
    public boolean isAnyOrganization() {
        return organization == null || organization.equals( Organization.UNKNOWN );
    }

    /**
     * Resource is not qualified by a role
     *
     * @return a boolean
     */
    public boolean isAnyRole() {
        return role == null || role.equals( Role.UNKNOWN );
    }

    /**
     * Resource is not qualified by a jurisdiction
     *
     * @return a boolean
     */
    public boolean isAnyJurisdiction() {
        return jurisdiction == null || jurisdiction.equals( Place.UNKNOWN );
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
     * {@inheritDoc}
     */
    @Override
    public String getName() {
        StringBuilder sb = new StringBuilder();
        if ( isAnyRole() )
            sb.append( isAnyActor() ? "someone" : actor.getName() );
        else {
            sb.append( isAnyActor() ? "any " : actor.getName() + " as " );
            sb.append( role.getName() );
        }
        if ( !isAnyOrganization() ) {
            sb.append( " from " );
            sb.append( organization.getName() );
        }
        if ( !isAnyJurisdiction() ) {
            sb.append( " for " );
            sb.append( jurisdiction.getName() );
        }
        return sb.toString();
    }

    /**
     * Return the title of a report page on this spec.
     *
     * @return a concise title for a report page
     */
    public String getReportTitle() {
        return actor != null ? actor.getName()
                : role != null ? role.getName()
                : organization != null ? organization.getName()
                : "Someone";
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
     *
     * @param other     a resource spec
     * @param precisely a boolean - true -> must be equal, false -> must be equal or more narrow
     * @return a boolean
     */
    public boolean matches( ResourceSpec other, boolean precisely ) {
        if ( precisely ) {

            boolean sameActor = ModelObject.areEqualOrNull( actor, other.getActor() )
                    || actor == null && Actor.UNKNOWN.equals( other.getActor() )
                    || other.getActor() == null && Actor.UNKNOWN.equals( actor );

            boolean sameRole = ModelObject.areEqualOrNull( role, other.getRole() )
                    || role == null && Role.UNKNOWN.equals( other.getRole() )
                    || other.getRole() == null && Role.UNKNOWN.equals( role );

            boolean sameOrganization = ModelObject.areEqualOrNull( organization, other.getOrganization() )
                    || organization == null && Organization.UNKNOWN.equals( other.getOrganization() )
                    || other.getOrganization() == null && Organization.UNKNOWN.equals( organization );

            return sameActor
                    && sameRole
                    && sameOrganization
                    && Matcher.samePlace( jurisdiction, other.getJurisdiction() );
        } else
            return narrowsOrEquals( other );
    }

    /**
     * Is this resource spec generalized by another resource spec?
     *
     * @param other a resource spec
     * @return a boolean
     */
    public boolean narrowsOrEquals( ResourceSpec other ) {
        // Assumes that instance equality works for entities (two instances are necessarily non equal)
        if ( other.isAnyone() ) return false;
        if ( equals( other ) ) return true;
        if ( !ModelEntity.implies( actor, other.getActor() ) && !other.isAnyActor() )
            return false;
        if ( !ModelEntity.implies( role, other.getRole() ) && !other.isAnyRole() )
            return false;
        // Todo compare inclusion
        /*
        if ( !other.isAnyOrganization()
                && ( organization == null
                || !organization.isSameOrWithin( other.getOrganization() ) ) )
            return false;

         */

        if ( !ModelEntity.implies( organization, other.getOrganization() )
                && !other.isAnyOrganization() )
            return false;
        if ( other.getJurisdiction() == null
                || ModelEntity.implies( jurisdiction, other.getJurisdiction() )
                || Matcher.samePlace( jurisdiction, other.getJurisdiction() )
                || Matcher.within( jurisdiction, other.getJurisdiction() ) )
            return true;
        else
            return false;
    }

    /**
     *     public boolean narrowsOrEquals( ResourceSpec other ) {
        // Assumes that instance equality works for entities (two instances are necessarily non equal)
        if ( other.isAnyone() ) return false;
        if ( equals( other ) ) return true;
        if ( !ModelObject.areEqualOrNull( actor, other.getActor() ) && !other.isAnyActor() )
            return false;
        if ( !ModelObject.areEqualOrNull( role, other.getRole() ) && !other.isAnyRole() )
            return false;
        // Todo compare inclusion
        ======================
        if ( !other.isAnyOrganization()
                && ( organization == null
                || !organization.isSameOrWithin( other.getOrganization() ) ) )
            return false;

        ==========================

        if ( !ModelObject.areEqualOrNull( organization, other.getOrganization() )
                && !other.isAnyOrganization() )
            return false;
        if ( other.getJurisdiction() == null
                || Matcher.samePlace( jurisdiction, other.getJurisdiction() )
                || Matcher.within( jurisdiction, other.getJurisdiction() ) )
            return true;
        else
            return false;
    }

     */

    /**
     * Return most specific entity composing this resource specification
     *
     * @return an entity
     */
    public ModelEntity mostSpecificEntity() {
        ModelEntity mostSpecific = null;
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
        return actor != null && actor.isActual() && role != null;
    }

    /**
     * Whether the source spec contains an entity in its definition.
     *
     * @param entity an entity
     * @return a boolean
     */
    public boolean hasOrImpliesEntity( ModelEntity entity ) {
        if ( entity instanceof Actor ) {
            return actor != null && ( actor.equals( entity ) || actor.hasTag( entity ) )
                    || actor == null && entity.equals( Actor.UNKNOWN );
        } else if ( entity instanceof Role ) {
            return role != null && ( role.equals( entity ) || role.hasTag( entity ) )
                    || role == null && entity.equals( Role.UNKNOWN );
        } else if ( entity instanceof Organization ) {
            return organization != null && ( organization.equals( entity ) || organization.hasTag( entity ) )
                    || organization == null && entity.equals( Organization.UNKNOWN );
        } else {
            return jurisdiction != null && ( jurisdiction.equals( entity ) || jurisdiction.hasTag( entity ) )
                    || jurisdiction == null && entity.equals( Place.UNKNOWN );
        }
    }

    @Override
    public String getDescription() {
        return actor != null ? actor.getDescription()
                : role != null ? role.getDescription()
                : organization == null ? ""
                : organization.getDescription();
    }

    /**
     * Find the first job that fits this resource spec.
     *
     * @return a job or null
     */
    public Job getJob() {
        if ( organization != null )
            for ( Job job : organization.getJobs() )
                if ( narrowsOrEquals( job.resourceSpec( organization ) ) )
                    return job;

        return null;
    }
}
