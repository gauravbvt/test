package com.mindalliance.channels.model;

import org.apache.commons.lang.StringUtils;

/**
 * A Resource is an actor in a role for an organization with a jurisdiction.
 * Actor, role, organization (any two), and jurisdiction may be null.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Jan 8, 2009
 * Time: 8:26:05 PM
 */

public final class ResourceSpec extends ModelObject implements Specable {
    // TODO - remove extends ModelObject

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

    public ResourceSpec( Specable specable ) {
        if ( specable != null ) {
            actor = specable.getActor();
            role = specable.getRole();
            organization = specable.getOrganization();
            jurisdiction = specable.getJurisdiction();
        }
    }

    public ResourceSpec() {
        this( null );
    }

    public ResourceSpec( Actor actor, Role role, Organization organization, Place jurisdiction ) {
        this.actor = actor;
        this.role = role;
        this.organization = organization;
        this.jurisdiction = jurisdiction;
    }


    public Actor getActor() {
        return actor;
    }

    public Role getRole() {
        return role;
    }

    public Organization getOrganization() {
        return organization;
    }

    public Place getJurisdiction() {
        return jurisdiction;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals( Object obj ) {
        if ( obj == null || !Specable.class.isAssignableFrom( obj.getClass() ) )
            return false;

        Specable specable = (Specable) obj;
        return ModelObject.areEqualOrNull( actor, specable.getActor() )
                && ModelObject.areEqualOrNull( role, specable.getRole() )
                && ModelObject.areEqualOrNull( organization, specable.getOrganization() )
                && Place.samePlace( jurisdiction, specable.getJurisdiction() );
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
        return actor == null ? Actor.UnknownName : actor.getName();
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
     * @param plan      the plan to search
     * @return a boolean
     */
    public boolean matchesOrSubsumedBy( Specable other, boolean precisely, Plan plan ) {
        if ( precisely )
            return matches( actor, other.getActor(), Actor.UNKNOWN )
                    && matches( role, other.getRole(), Role.UNKNOWN )
                    && matches( organization, other.getOrganization(), Organization.UNKNOWN )
                    && Place.samePlace( jurisdiction, other.getJurisdiction() );

        else
            return subsumedBy( other, plan );
    }

    /**
     * Whether this resource spec is matched by another.
     *
     * @param other     a resource spec
     * @param precisely a boolean - true -> must be equal, false -> must be equal or more narrow
     * @param plan      the plan to search
     * @return a boolean
     */
    public boolean matchesOrSubsumes( Specable other, boolean precisely, Plan plan ) {
        if ( precisely )
            return matches( other.getActor(), actor, Actor.UNKNOWN )
                    && matches( other.getRole(), role, Role.UNKNOWN )
                    && matches( other.getOrganization(), organization, Organization.UNKNOWN )
                    && Place.samePlace( other.getJurisdiction(), jurisdiction );

        else
            return subsumes( other, plan );
    }

    private static boolean matches( ModelObject object, ModelObject other, ModelObject unknown ) {
        return object == null ? other == null || unknown.equals( other )
                : other == null ? unknown.equals( object )
                : object.equals( other );
    }

    /**
     * Whether this resource spec and another could be describing the same resource.
     *
     * @param other a resource spec
     * @param plan  the plan to search
     * @return a boolean
     */
    private boolean subsumedBy( Specable other, Plan plan ) {
        return ModelEntity.subsumedBy( actor, other.getActor(), plan )
                && ModelEntity.subsumedBy( role, other.getRole(), plan )
                && ModelEntity.subsumedBy( jurisdiction, other.getJurisdiction(), plan )
                && ModelEntity.subsumedBy( organization, other.getOrganization(), plan );
    }

    /**
     * Whether another spec could be describing could be describing this.
     *
     * @param other a resource spec
     * @param plan  the plan to search
     * @return a boolean
     */
    private boolean subsumes( Specable other, Plan plan ) {
        return ModelEntity.subsumedBy( other.getActor(), actor, plan )
                && ModelEntity.subsumedBy( other.getRole(), role, plan )
                && ModelEntity.subsumedBy( other.getJurisdiction(), jurisdiction, plan )
                && ModelEntity.subsumedBy( other.getOrganization(), organization, plan );
    }

    /**
     * Is this resource spec generalized by another resource spec?
     *
     * @param other a resource spec
     * @param plan  the plan to search
     * @return a boolean
     */
    public boolean narrowsOrEquals( Specable other, Plan plan ) {
        Actor oa = other.getActor();
        Role or = other.getRole();
        Organization oo = other.getOrganization();
        Place oj = other.getJurisdiction();

        boolean aoa = oa == null || Actor.UNKNOWN.equals( oa );
        boolean aor = or == null || Role.UNKNOWN.equals( or );
        boolean aoo = oo == null || Organization.UNKNOWN.equals( oo );
        boolean aoj = oj == null || Place.UNKNOWN.equals( oj );

        if ( aoa && aor && aoo && aoj )
            return false;

        if ( equals( other ) )
            return true;

        return ( aoa || actor != null && actor.narrowsOrEquals( oa, plan ) )
                && ( aor || role != null && role.narrowsOrEquals( or, plan ) )
                && ( aoo || organization != null && organization.narrowsOrEquals( oo, plan ) )
                && ( aoj || jurisdiction != null && jurisdiction.narrowsOrEquals( oj, plan ) );
    }

    /**
     * Is a job specified (actor and organization set)?
     *
     * @return a boolean
     */
    public boolean hasJob() {
        return actor != null && actor.isActual()
                && organization != null && organization.isActual();
    }

    /**
     * Whether this resource spec references the entity or an entity that broadens it.
     *
     * @param entity an entity
     * @param plan   the plan to search
     * @return a boolean
     */
    public boolean hasEntityOrBroader( ModelEntity entity, Plan plan ) {
        return entity.narrowsOrEquals( actor, plan )
                || entity.narrowsOrEquals( role, plan )
                || entity.narrowsOrEquals( organization, plan )
                || entity.narrowsOrEquals( jurisdiction, plan );
    }

    /**
     * Whether the source spec contains an entity in its definition.
     *
     * @param entity an entity
     * @return a boolean
     */
    public boolean hasEntity( ModelEntity entity ) {
        return entity.isEquivalentToOrIsA( actor, Actor.class )
                || entity.isEquivalentToOrIsA( role, Role.class )
                || entity.isEquivalentToOrIsA( organization, Organization.class )
                || entity.isEquivalentToOrIsA( jurisdiction, Place.class );
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
     * @param plan the plan to search
     * @return a job or null
     */
    public Job getJob( Plan plan ) {
        if ( organization != null )
            for ( Job job : organization.getJobs() )
                if ( narrowsOrEquals( job.resourceSpec( organization ), plan ) )
                    return job;

        return null;
    }

    /**
     * Make display string with max length.
     *
     * @param maxLength an int
     * @return a string
     */
    public String displayString( int maxLength ) {
        StringBuilder sb = new StringBuilder();
        if ( isAnyActor() ) {
            if ( !isAnyRole() )
                sb.append( role.getName() );

            if ( !isAnyOrganization() ) {
                if ( sb.length() > 0 )
                    sb.append( ", " );
                sb.append( organization.getName() );
            }
            if ( !isAnyJurisdiction() ) {
                if ( sb.length() > 0 )
                    sb.append( ", " );
                sb.append( jurisdiction.getName() );
            }
        } else
            sb.append( actor.getName() );

        return StringUtils.abbreviate( sb.toString(), maxLength );
    }
}
