package com.mindalliance.channels.api.entities;

import com.mindalliance.channels.core.community.Agent;
import com.mindalliance.channels.core.community.protocols.CommunityEmployment;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 12/1/11
 * Time: 10:27 AM
 */
@XmlType( propOrder = {"agentName", "agencyName", "supervisorName", "actorId", "organizationId", "title",
        "roleId", "jurisdictionId", "supervisorId", "confirmed"} )
public class EmploymentData implements Serializable {

    private CommunityEmployment employment;

    public EmploymentData() {
        // required
    }

    public EmploymentData( CommunityEmployment employment ) {
        this.employment = employment;
    }

    @XmlElement
    public String getAgentName() {
        return employment.getAgent().getName();
    }

    @XmlElement
    public String getAgencyName() {
        return employment.getEmployer().getName();
    }

    @XmlElement
    public String getSupervisorName() {
        Agent supervisor = employment.getSupervisor();
        return supervisor == null
                ? null
                : supervisor.getName();
    }

    @XmlElement( name = "agentId" )
    public Long getActorId() {
        return employment.getAgent().getActorId();
    }

    @XmlElement
    public String getTitle() {
        return !employment.getTitle().isEmpty()
                ? employment.getTitle()
                : "(no title)";
    }

    @XmlElement
    public Long getRoleId() {
        return employment.getRole().getId();
    }

    @XmlElement
    public Long getJurisdictionId() {
        return employment.getJurisdiction() != null
                ? employment.getJurisdiction().getId()
                : null;
    }

    @XmlElement
    public Long getSupervisorId() {
        return employment.getSupervisor() != null
                ? employment.getSupervisor().getActorId()
                : null;
    }

    @XmlElement
    public Boolean getConfirmed() {
        return employment.isConfirmed();
    }

    @XmlElement
    public Long getOrganizationId() {
        return employment.getEmployer().getOrganizationId();
    }

    public Set<Long> allActorIds() {
        Set<Long> ids = new HashSet<Long>();
        ids.add( employment.getAgent().getActorId() );
        if ( getSupervisorId() != null )
            ids.add( getSupervisorId() );
        return ids;
    }

    public CommunityEmployment getEmployment() {
        return employment;
    }

    public String getLabel() {
        Agent agent = employment.getAgent();
        if ( agent.isFromOrganizationParticipation() ) {
            return agent.getName();
        } else {
            return agent.getName() + " for " + employment.getEmployer().getName();
        }
    }

   @Override
    public boolean equals( Object object ) {
        return object instanceof EmploymentData
                && employment.equals( ( (EmploymentData) object ).getEmployment() );
    }

    @Override
    public int hashCode() {
        return employment.hashCode();
    }
}
