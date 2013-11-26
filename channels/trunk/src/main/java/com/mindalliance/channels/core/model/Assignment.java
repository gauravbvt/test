package com.mindalliance.channels.core.model;

import java.util.ArrayList;
import java.util.List;

/**
 * A match between an employment and a part.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Oct 28, 2009
 * Time: 9:55:15 AM
 */
public class Assignment implements GeoLocatable, Specable, Identifiable {
    /**
     * Employment playing a part.
     */
    private Employment employment;
    /**
     * Part that implies the assignment.
     */
    private Part part;

    public Assignment( Assignment assignment ) {
        part = assignment.getPart();
        employment = new Employment( assignment.getEmployment() );
    }

    public Assignment( Employment employment, Part part ) {
        assert part != null && employment != null;
        this.part = part;
        this.employment = employment;
    }

    public Part getPart() {
        return part;
    }

    public Employment getEmployment() {
        return employment;
    }

    @Override
    public Actor getActor() {
        return employment.getActor();
    }

    @Override
    public Role getRole() {
        return employment.getRole();
    }

    @Override
    public Organization getOrganization() {
        return employment.getOrganization();
    }


    @Override
    public Place getJurisdiction() {
        return employment.getJurisdiction();
    }

    @Override
    public Place getPlaceBasis() {
        return part.getPlaceBasis();
    }

    @Override
    public String getGeoMarkerLabel() {
        return employment.toString() + ", and is assigned to task \"" + part.getTask() + '\"';
    }

    @Override
    public List<? extends GeoLocatable> getImpliedGeoLocatables() {
        List<GeoLocatable> geoLocatables = new ArrayList<GeoLocatable>();
        geoLocatables.addAll( employment.getImpliedGeoLocatables() );
        geoLocatables.addAll( part.getImpliedGeoLocatables() );
        return geoLocatables;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append( employment.toString() );
        sb.append( ", doing task \"" );
        sb.append( part.getTask() );
        sb.append( '\"' );
        return sb.toString();
    }

    @Override
    public boolean equals( Object obj ) {
        if ( !( obj instanceof Assignment ) ) return false;
        Assignment other = (Assignment) obj;
        return employment.equals( other.getEmployment() )
                && part.equals( other.getPart() );
    }

    @Override
    public int hashCode() {
        int hash = 1;
        hash = hash * 31 + employment.hashCode();
        hash = hash * 31 + part.hashCode();
        return hash;
    }

    /**
     * Get the known, assigned entity.
     *
     * @return a channelable
     */
    public Channelable getChannelable() {
        return getActor().isUnknown() ? getOrganization() : getActor();
    }

    @Override
    public String getKindLabel() {
        return getTypeName();
    }

    /**
     * Whether an entity is used in defining the assignment.
     *
     * @param entity a model entity
     * @return a boolean
     */
    public boolean hasEntity( ModelEntity entity ) {
        return ModelObject.areIdentical( getActor(), entity )
                || ModelObject.areIdentical( getRole(), entity )
                || ModelObject.areIdentical( getOrganization(), entity )
                || ModelObject.areIdentical( getJurisdiction(), entity );
    }

    public boolean hasEntityOrBroader( ModelEntity entity, Place locale ) {
        return entity.narrowsOrEquals( getActor(), locale )
                || entity.narrowsOrEquals( getRole(), locale )
                || entity.narrowsOrEquals( getOrganization(), locale )
                || entity.narrowsOrEquals( getJurisdiction(), locale );
    }


    /**
     * Get known assignee, either an actor or an organization.
     *
     * @return a model entity
     */
    public ModelEntity getKnownAssignee() {
        return getActor().isUnknown() ? getOrganization() : getActor();
    }

    /**
     * Get where the task is executed, if known from specification, else null.
     *
     * @return a place
     */
    public Place getLocation() {
        AssignedLocation assignedLocation = part.getLocation();
        if ( assignedLocation.isNamed() )
            return assignedLocation.getNamedPlace();
        else if ( assignedLocation.isAgentJurisdiction() )
            return getEmployment().getJurisdiction();
        else if ( assignedLocation.isOrganizationJurisdiction() )
            return getOrganization().getJurisdiction();
        else
            return null;
    }

    public Specable getSpecableActor() {
        Actor actor = getActor();
        Role role = getRole();
        return !actor.isUnknown() && actor.isActual() || role == null ? actor : role;
    }

    public <T extends ModelEntity> T getActualEntityAssigned( Class<T> entityClass ) {
        if ( entityClass.isAssignableFrom( Organization.class ) ) {
            return (T) getOrganization();
        } else if ( entityClass.isAssignableFrom( Actor.class ) ) {
            return (T) getActor();
        } else throw new IllegalArgumentException();
    }

    public <T extends ModelEntity> List<T> getEntityTypesAssigned( Class<T> entityClass ) {
        List<T> results = new ArrayList<T>();
        if ( entityClass.isAssignableFrom( Organization.class ) ) {
            Organization org = getOrganization();
            if ( org != null ) {
                if ( org.isType() ) results.add( (T) org );
                results.addAll( (List<T>) org.getAllTypes() );
            }
        } else if ( entityClass.isAssignableFrom( Actor.class ) ) {
            Actor actor = getActor();
            if ( actor != null ) {
                if ( actor.isType() ) results.add( (T) actor );
                results.addAll( (List<T>) actor.getAllTypes() );
            }
        } else if ( entityClass.isAssignableFrom( Role.class ) ) {
            Role role = getRole();
            if ( role != null ) {
                results.add( (T) role );
                results.addAll( (List<T>) getRole().getAllTypes() );
            }
        } else throw new IllegalArgumentException();
        return results;
    }

    public void setEmployment( Employment employment ) {
        this.employment = employment;
    }

    public String getFullTitle( String sep ) {
        String label = "";
        if ( getActor() != null && !getActor().isUnknown() ) {
            label += getActor().getName();
        }
        if ( getRole() != null ) {
            if ( !label.isEmpty() ) label += sep;
            if ( !label.isEmpty() ) label += "as ";
            label += getRole().getName();
        }
        if ( getJurisdiction() != null ) {
            if ( !label.isEmpty() ) label += ( sep + "for " );
            label += getJurisdiction().getName();
        }
        if ( getOrganization() != null ) {
            if ( !label.isEmpty() ) label += ( sep + "at " );
            label += getOrganization().getName();
        }
        if ( !label.isEmpty() ) label += sep;
        label += part.getTask();
        if ( part.isRepeating() ) {
            label += " (every " + part.getRepeatsEvery().toString() + ")";
        }
        return label;
    }
    // Identifiable

    @Override
    public long getId() {
        return part.getId();
    }

    @Override
    public String getDescription() {
        return part.getDescription();
    }

    @Override
    public String getTypeName() {
        return getClass().getSimpleName();
    }

    @Override
    public boolean isModifiableInProduction() {
        return false;
    }

    @Override
    public String getClassLabel() {
        return getClass().getSimpleName();
    }

    @Override
    public String getName() {
        return part.getTask();
    }

    public boolean involves( Specable focusEntity ) {
        return getActor().equals( focusEntity ) || getOrganization().equals( focusEntity );
    }

    /**
     * Assignment overrides another.
     *
     * @param other an assignment
     * @param locale a place
     * @return a boolean
     */
    public boolean overrides( Assignment other, Place locale ) {
        return employment.equals( other.employment )
                && part.matchesTaskOf( other.getPart(), locale )
                && part.resourceSpec().narrows( other.getPart().resourceSpec(), locale );
    }

    public boolean isProhibited() {
        return part.isProhibited();
    }

    public ResourceSpec getResourceSpec() {
        return new ResourceSpec( getActor(), getRole(), getOrganization(), getJurisdiction() );
    }

    public EventPhase getEventPhase() {
        return getPart().getSegment().getEventPhase();
    }

    public List<EventTiming> getEventPhaseContext() {
        return getPart().getSegment().getContext();
    }

    public boolean isOngoing() {
        return getPart().isOngoing();
    }

    public boolean isInitiatedByEventPhase() {
        return getPart().isStartsWithSegment();
    }

    public Actor getSupervisor() {
        return employment.getSupervisor();
    }

    public Subject getCommunicatedLocation() {
        AssignedLocation assignedLocation = getPart().getLocation();
        return assignedLocation.isCommunicated()
                ? assignedLocation.getSubject()
                : null;
    }
}
