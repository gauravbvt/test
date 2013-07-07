package com.mindalliance.channels.core.community.protocols;

import com.mindalliance.channels.core.community.Agency;
import com.mindalliance.channels.core.community.Agent;
import com.mindalliance.channels.core.community.CommunityService;
import com.mindalliance.channels.core.model.Actor;
import com.mindalliance.channels.core.model.Employment;
import com.mindalliance.channels.core.model.Place;
import com.mindalliance.channels.core.model.Role;

import java.io.Serializable;

/**
 * An agent employed by an agency.
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 1/3/13
 * Time: 2:33 PM
 */
public class CommunityEmployment implements Serializable {

    private Agent agent;
    private Employment employment;
    private Agent supervisor;
    private Agency employer;

    public CommunityEmployment( Employment employment,
                                Agent agent,
                                Agency employer,
                                CommunityService communityService ) {
        this.employment = employment;
        this.agent = agent;
        this.employer = employer;
        init( communityService );
    }

    private void init( CommunityService communityService ) {
        Actor supervisorActor = employment.getSupervisor();
        if ( supervisorActor != null ) {
            if ( employer.isFixedOrganization() ) {
                supervisor = new Agent( supervisorActor );
            } else {
                supervisor = new Agent( supervisorActor, agent.getOrganizationParticipation(), communityService );
            }
        }
    }

    public Agent getAgent() {
        return agent;
    }

    public Employment getEmployment() {
        return employment;
    }

    public Agent getSupervisor() {
        return supervisor;
    }

    public String getTitle() {
        return employment.getTitle();
    }

    public Agency getEmployer() {
        return employer;
    }

    public Place getJurisdiction() {
        return employment.getJurisdiction();
    }

    public Role getRole() {
        return employment.getRole();
    }

    public Boolean isConfirmed() {
        return employment.isConfirmed();
    }

    public String getLabel() {
        StringBuilder sb = new StringBuilder();
        sb.append( getTitle() );
        sb.append( getEmployer().getName() );
        if ( getJurisdiction() != null ) {
            sb.append( " for " );
            sb.append( getJurisdiction().getName() );
        }
        return sb.toString();
    }


    ///////////////////////

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append( agent.getName() );
        sb.append( ", " );
        sb.append( getTitle() );
        sb.append( " at " );
        sb.append( getEmployer().getName() );
        if ( getJurisdiction() != null ) {
            sb.append( " for " );
            sb.append( getJurisdiction().getName() );
        }
        return sb.toString();
    }


    public boolean equals( Object obj ) {
        if ( obj instanceof CommunityEmployment ) {
            CommunityEmployment other = (CommunityEmployment) obj;
            return employment.equals( other.getEmployment() )
                    && agent.equals( other.getAgent() )
                    && employer.equals( other.getEmployer() );
        } else {
            return false;
        }
    }

    public int hashCode() {
        int hash = 1;
        hash = hash * 31 + agent.hashCode();
        hash = hash * 31 + employer.hashCode();
        hash = hash * 31 + employment.hashCode();
        return hash;
    }

}
