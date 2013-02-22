package com.mindalliance.channels.core.model;

import com.mindalliance.channels.core.community.CommunityService;
import com.mindalliance.channels.core.query.PlanService;
import org.apache.commons.lang.StringUtils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * A Resource is an actor in a role for an organization with a jurisdiction.
 * Actor, role, organization (any two), and jurisdiction may be null.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Jan 8, 2009
 * Time: 8:26:05 PM
 */

public class ResourceSpec extends ModelObject implements Specable {
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
        if ( obj == null || !getClass().isAssignableFrom( obj.getClass() ) )
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
     * @return a boolean
     */
    public boolean isAnyJurisdiction() {
        return jurisdiction == null || jurisdiction.equals( Place.UNKNOWN );
    }

    /**
     * Resource is anyone?
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
            sb.append( " at " );
            sb.append( organization.getName() );
        }
        if ( !isAnyJurisdiction() ) {
            sb.append( " for " );
            sb.append( jurisdiction.getName() );
        }
        return sb.toString();
    }

    /**
     * Get a report-formatted description.
     * @return a string
     * @param prefix a/any/all or nothing
     */
    public String getReportSource( String prefix ) {
        StringBuilder sb = new StringBuilder();
        boolean showActor = isActor() && actor.isSingularParticipation() && !actor.isUnknown();
        if ( isAnyRole() )
            sb.append( showActor ? actor.getName() : "someone" );
        else {
            sb.append( showActor ? "the " : prefix );
            sb.append( role.getName() );
        }
        if ( !isAnyOrganization() ) {
            sb.append( " at " );
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
     * @param locale    the default location
     * @return a boolean
     */
    public boolean matchesOrSubsumedBy( Specable other, boolean precisely, Place locale ) {
        if ( precisely )
            return matches( actor, other.getActor(), Actor.UNKNOWN )
                && matches( role, other.getRole(), Role.UNKNOWN )
                && matches( organization, other.getOrganization(), Organization.UNKNOWN )
                && Place.samePlace( jurisdiction, other.getJurisdiction() );

        else
            return subsumedBy( actor, other.getActor(), locale )
                && subsumedBy( role, other.getRole(), locale )
                && subsumedBy( jurisdiction, other.getJurisdiction(), locale )
                && subsumedBy( organization, other.getOrganization(), locale );
    }

    /**
     * Whether this resource spec is matched by another.
     *
     * @param other     a resource spec
     * @param precisely a boolean - true -> must be equal, false -> must be equal or more narrow
     * @param locale    the default location
     * @return a boolean
     */
    public boolean matchesOrSubsumes( Specable other, boolean precisely, Place locale ) {
        if ( precisely )
            return matches( other.getActor(), actor, Actor.UNKNOWN )
                && matches( other.getRole(), role, Role.UNKNOWN )
                && matches( other.getOrganization(), organization, Organization.UNKNOWN )
                && Place.samePlace( other.getJurisdiction(), jurisdiction );

        else
            return subsumedBy( other.getActor(), actor, locale )
                && subsumedBy( other.getRole(), role, locale )
                && subsumedBy( other.getJurisdiction(), jurisdiction, locale )
                && subsumedBy( other.getOrganization(), organization, locale );
    }

    private static boolean matches( ModelObject object, ModelObject other, ModelObject unknown ) {
        return object == null ? other == null || unknown.equals( other )
             : other == null  ? unknown.equals( object )
                              : object.equals( other );
    }

    /**
     * Whether either entity is null or the first entity is subsumed by the other
     * (it narrows or equals the other).
     *
     * @param entity an entity
     * @param other  an entity
     * @param locale the default location
     * @return a boolean
     */
    private static boolean subsumedBy(
            ModelEntity entity, ModelEntity other, Place locale ) {
        return entity == null || entity.narrowsOrEquals( other, locale );
    }

    /**
     * Is this resource spec generalized by another resource spec?
     *
     * @param other a resource spec
     * @param locale the default location
     * @return a boolean
     */
    public boolean narrowsOrEquals( Specable other, Place locale ) {
        Actor oA = other.getActor();
        Role oR = other.getRole();
        Organization oO = other.getOrganization();
        Place oJ = other.getJurisdiction();

        boolean anyOtherA = oA == null ;
        boolean anyOtherR = oR == null ;
        boolean anyOtherO = oO == null ;

        if ( anyOtherA && anyOtherR && anyOtherO && oJ == null )
            return false;

        if ( equals( other ) )
            return true;

        boolean anyOtherJ = oJ == null || oJ.isUnknown() && jurisdiction == null ;

        return ( anyOtherA || actor != null && actor.narrowsOrEquals( oA, locale ) )
            && ( anyOtherR || role != null && role.narrowsOrEquals( oR, locale ) )
            && ( anyOtherO || organization != null && organization.narrowsOrEquals( oO, locale ) )
            && ( anyOtherJ || jurisdiction != null && jurisdiction.narrowsOrEquals( oJ, locale ) );
    }

    public boolean narrows( Specable other, Place locale ) {
        return narrowsOrEquals( other, locale ) && !equals( other );
    }

    /**
     * Whether this resource spec references the entity or an entity that broadens it.
     *
     * @param entity an entity
     * @param locale the default location
     * @return a boolean
     */
    public boolean hasEntityOrBroader( ModelEntity entity, Place locale ) {
        return entity.narrowsOrEquals( actor, locale )
            || entity.narrowsOrEquals( role, locale )
            || entity.narrowsOrEquals( organization, locale )
            || entity.narrowsOrEquals( jurisdiction, locale );
    }

    /**
     * Whether this resource spec references the entity or an entity that broadens it.
     *
     * @param entity an entity
     * @param locale the default location
     * @return a boolean
     */
    public boolean hasEntityOrNarrower( ModelEntity entity, Place locale ) {
        return actor != null && actor.narrowsOrEquals( entity, locale )
            || role != null && role.narrowsOrEquals( entity, locale )
            || organization != null && organization.narrowsOrEquals( entity, locale )
            || jurisdiction != null && jurisdiction.narrowsOrEquals( entity, locale );
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
        return actor != null        ? actor.getDescription()
             : role != null         ? role.getDescription()
             : organization == null ? ""
                                    : organization.getDescription();
    }

    /**
     * Find the first job that fits this resource spec.
     *
     * @param locale the default location
     * @return a job or null
     */
    public Job getJob( Place locale ) {
        if ( organization != null )
            for ( Job job : organization.getJobs() )
                if ( narrowsOrEquals( job.resourceSpec( organization ), locale ) )
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

    public void setActor( Actor actor ) {
        this.actor = actor;
    }

    public void setRole( Role role ) {
        this.role = role;
    }

    public void setOrganization( Organization organization ) {
        this.organization = organization;
    }

    public void setJurisdiction( Place jurisdiction ) {
        this.jurisdiction = jurisdiction;
    }

    @Override
    public Map<String, Object> mapState() {
        Map<String,Object>state = new HashMap<String,Object>(  );
        if ( getActor() != null )
             state.put( "actor", Arrays.asList( getActor().getName(), getActor().isType() ) );
         if ( getRole() != null )
             state.put( "role", Arrays.asList( getRole().getName(), getRole().isType() ) );
         if ( getOrganization() != null )
             state.put( "organization", Arrays.asList( getOrganization().getName(), getOrganization().isType() ) );
         if ( getJurisdiction() != null )
             state.put( "jurisdiction", Arrays.asList( getJurisdiction().getName(), getJurisdiction().isType() ) );
        return state;
    }

    @Override
    public void initFromMap( Map<String,Object>state, CommunityService communityService ) {
        PlanService planService = communityService.getPlanService();
        if ( state.get( "actor" ) != null )
            setActor( planService.retrieveEntity( Actor.class, state, "actor" ) );
        else
            setActor( null );
        if ( state.get( "role" ) != null )
            setRole( planService.retrieveEntity( Role.class, state, "role" ) );
        else
            setRole( null );
        if ( state.get( "organization" ) != null )
            setOrganization( planService.retrieveEntity( Organization.class, state, "organization" ) );
        else
            setOrganization( null );
        if ( state.get( "jurisdiction" ) != null )
            setJurisdiction( planService.retrieveEntity( Place.class, state, "jurisdiction" ) );
        else
            setJurisdiction( null );
    }

    @Override
    public boolean isSegmentObject() {
        return false;
    }

    @SuppressWarnings( "unchecked" )
    public <T extends ModelEntity> T getEntity( Class<T> aClass ) {
       if ( aClass.isAssignableFrom( Actor.class )) {
           return (T)getActor();
       } else if ( aClass.isAssignableFrom( Role.class )) {
           return (T)getRole();
       } else if ( aClass.isAssignableFrom( Organization.class )) {
           return (T)getOrganization();
       } else if ( aClass.isAssignableFrom( Place.class )) {
           return (T)getJurisdiction();
       } else {
           return null;
       }
    }
}
