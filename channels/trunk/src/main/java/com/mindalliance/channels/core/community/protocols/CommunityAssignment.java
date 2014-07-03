package com.mindalliance.channels.core.community.protocols;

import com.mindalliance.channels.core.community.Agency;
import com.mindalliance.channels.core.community.Agent;
import com.mindalliance.channels.core.community.CommunityService;
import com.mindalliance.channels.core.model.Actor;
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

    private CommunityEmployment communityEmployment;
    private Part part;

    public CommunityAssignment( CommunityEmployment communityEmployment, Part part ) {
        this.communityEmployment = communityEmployment;
        this.part = part;
    }

    public CommunityEmployment getCommunityEmployment() {
        return communityEmployment;
    }

    public Part getPart() {
        return part;
    }

    public Agency getAgency() {
        return communityEmployment.getEmployer();
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
            return getCommunityEmployment().getJurisdiction();
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
        return communityEmployment.getAgent();
    }

    public Agent getSupervisor() {
        return communityEmployment.getSupervisor();
    }

    public Role getRole() {
        return communityEmployment.getRole();
    }

    public Place getJurisdiction() {
        return communityEmployment.getJurisdiction();
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
        sb.append( communityEmployment.toString() );
        sb.append( ", doing task \"" );
        sb.append( part.getTaskLabel
                () );
        sb.append( '\"' );
        return sb.toString();
    }


    @Override
    public boolean equals( Object obj ) {
        if ( !( obj instanceof CommunityAssignment ) ) return false;
        CommunityAssignment other = (CommunityAssignment) obj;
        return communityEmployment.equals( other.getCommunityEmployment() )
                && part.equals( other.getPart() );
    }

    @Override
    public int hashCode() {
        int hash = 1;
        hash = hash * 31 + communityEmployment.hashCode();
        hash = hash * 31 + part.hashCode();
        return hash;
    }

    public Actor getActor() {
        return communityEmployment.getAgent().getActor();
    }

    public Assignment getAssignment() {
        return new Assignment( communityEmployment.getEmployment(), part );
    }


    //////////////////
}
