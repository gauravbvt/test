package com.mindalliance.channels.core.community.protocols;

import com.mindalliance.channels.core.community.Agency;
import com.mindalliance.channels.core.community.Agent;
import com.mindalliance.channels.core.community.CommunityService;
import com.mindalliance.channels.core.model.Actor;
import com.mindalliance.channels.core.model.Employment;
import com.mindalliance.channels.core.model.Job;
import com.mindalliance.channels.core.model.Place;
import com.mindalliance.channels.core.model.Role;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;

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
    private Job job;
    private Agent supervisor;

    public CommunityEmployment( Job job,
                                Agent agent,
                                CommunityService communityService ) {
        this.job = job;
        this.agent = agent;
        init( communityService );
    }

    private void init( CommunityService communityService ) {
        Actor supervisorActor = job.getSupervisor();
        if ( supervisorActor != null ) {
            supervisor = (Agent) CollectionUtils.find(
                    communityService.getParticipationManager().findAllSupervisorsOf( agent, communityService ),
                    new Predicate() {
                        @Override
                        public boolean evaluate( Object object ) {
                            return ( (Agent) object ).getActor().equals( job.getSupervisor().getActor() );
                        }
                    }
            );
        }
    }

    public Agent getAgent() {
        return agent;
    }

    public Job getJob() {
        return job;
    }

    public Agent getSupervisor() {
        return supervisor;
    }

    public String getTitle() {
        return job.getTitle();
    }

    public Agency getEmployer() {
        return agent.getAgency();
    }

    public Place getJurisdiction() {
        return job.getJurisdiction();
    }

    public Role getRole() {
        return job.getRole();
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
/*
        sb.append( agent.getName() );
        sb.append( ", " );
*/
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
            return job.equals( other.getJob() )
                    && agent.equals( other.getAgent() );
        } else {
            return false;
        }
    }

    public int hashCode() {
        int hash = 1;
        hash = hash * 31 + agent.hashCode();
        hash = hash * 31 + job.hashCode();
        return hash;
    }

    public Employment getEmployment() {
        return new Employment( agent.getActor(), agent.getAgency().findParticipatedAsOrganizationWithJob( job ), job );
    }
}
