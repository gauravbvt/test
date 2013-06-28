package com.mindalliance.channels.core.community.protocols;

import com.mindalliance.channels.core.community.Agency;
import com.mindalliance.channels.core.community.Agent;
import com.mindalliance.channels.core.community.CommunityService;
import com.mindalliance.channels.core.model.AssignedLocation;
import com.mindalliance.channels.core.model.Assignment;
import com.mindalliance.channels.core.model.EventPhase;
import com.mindalliance.channels.core.model.EventTiming;
import com.mindalliance.channels.core.model.Part;
import com.mindalliance.channels.core.model.Place;
import com.mindalliance.channels.core.model.Role;
import com.mindalliance.channels.core.model.Subject;

import java.io.Serializable;
import java.util.List;

/**
 * An assignment in a plan community.
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 1/3/13
 * Time: 3:55 PM
 */
public class CommunityAssignment implements Serializable {

    private CommunityEmployment employment;
    private Part part;

    public CommunityAssignment( CommunityEmployment employment, Part part ) {
        this.employment = employment;
        this.part = part;
    }

    public CommunityEmployment getEmployment() {
        return employment;
    }

    public Part getPart() {
        return part;
    }

    public Agency getAgency() {
        return employment.getEmployer();
    }

    /**
     * Get where the task is executed, if known from specification, else null.
     *
     * @return a place
     */
    public Place getLocation( CommunityService communityService ) {
        AssignedLocation assignedLocation = part.getLocation();
        if ( assignedLocation.isNamed() )
            return assignedLocation.getNamedPlace();
        else if ( assignedLocation.isAgentJurisdiction() )
            return getEmployment().getJurisdiction();
        else if ( assignedLocation.isOrganizationJurisdiction() )
            return getAgency().getJurisdiction( communityService );
        else
            return null;
    }

    public boolean isOngoing() {
        return getPart().isOngoing();
    }

    public boolean isInitiatedByEventPhase() {
        return getPart().isStartsWithSegment();
    }

    public EventPhase getEventPhase() {
        return getPart().getSegment().getEventPhase();
    }

    public List<EventTiming> getEventPhaseContext() {
        return getPart().getSegment().getContext();
    }


    public Agent getAgent() {
        return employment.getAgent();
    }

    public Agent getSupervisor() {
        return employment.getSupervisor();
    }

    public Role getRole() {
        return employment.getRole();
    }

    public Place getJurisdiction() {
        return employment.getJurisdiction();
    }

    public Subject getCommunicatedLocation() {
        AssignedLocation assignedLocation = getPart().getLocation();
        return assignedLocation.isCommunicated()
                ? assignedLocation.getSubject()
                : null;
    }

    ////////////////////

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
        if ( !( obj instanceof CommunityAssignment ) ) return false;
        CommunityAssignment other = (CommunityAssignment) obj;
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

    public Assignment getAssignment() {
        return new Assignment( employment.getEmployment(), part );
    }

    //////////////////
}
