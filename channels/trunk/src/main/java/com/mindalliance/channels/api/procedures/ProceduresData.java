package com.mindalliance.channels.api.procedures;

import com.mindalliance.channels.api.directory.ContactData;
import com.mindalliance.channels.api.entities.EmploymentData;
import com.mindalliance.channels.api.plan.PlanIdentifierData;
import com.mindalliance.channels.core.community.CommunityService;
import com.mindalliance.channels.core.community.participation.Agent;
import com.mindalliance.channels.core.community.participation.UserParticipation;
import com.mindalliance.channels.core.community.protocols.CommunityAssignment;
import com.mindalliance.channels.core.community.protocols.CommunityAssignments;
import com.mindalliance.channels.core.community.protocols.CommunityCommitments;
import com.mindalliance.channels.core.community.protocols.CommunityEmployment;
import com.mindalliance.channels.core.dao.user.ChannelsUser;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Web Service data element for the procedures of an agent or user according to a plan.
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 12/5/11
 * Time: 12:25 PM
 */
@XmlRootElement( name = "procedures", namespace = "http://mind-alliance.com/api/isp/v1/" )
@XmlType( propOrder = {"date", "planIdentifier", "userEmail", "userFullName", "dateVersioned", "actorIds",
        "employments", "procedures", "environment"} )
public class ProceduresData implements Serializable {

    private List<Agent> agents;
    private ChannelsUser user;
    private List<ProcedureData> procedures;
    private List<EmploymentData> employments;
    private List<Agent> participatingAgents;
    private EnvironmentData environmentData;
    private PlanIdentifierData planIdentifierData;
    private String dateVersioned;

    public ProceduresData() {
        // required
    }

    public ProceduresData(
            String serverUrl,
            CommunityService communityService,
            List<UserParticipation> participations,
            ChannelsUser user ) {
        this.user = user;
        initData( serverUrl, participations, communityService );
    }

    public ProceduresData(
            String serverUrl,
            CommunityService communityService,
            Agent agent ) {
        this.agents = new ArrayList<Agent>();
        agents.add( agent );
        initData( serverUrl, communityService );
    }


    private void initData(
            String serverUrl,
            List<UserParticipation> participations,
            CommunityService communityService ) {
        initParticipatingAgents( participations, communityService );
        this.agents = getAgents( participations );
        initData( serverUrl, communityService );
    }

    private void initData( String serverUrl, CommunityService communityService ) {
        initProcedures( serverUrl, communityService );
        initEmployments( communityService );
        environmentData = new EnvironmentData( serverUrl, this, communityService );
        planIdentifierData = new PlanIdentifierData( communityService );
        dateVersioned = new SimpleDateFormat( "yyyy/MM/dd H:mm:ss z" )
                .format( communityService.getPlan().getWhenVersioned() );
    }

    private void initEmployments( CommunityService communityService ) {
        employments = new ArrayList<EmploymentData>();
        for ( Agent agent : agents )
            for ( CommunityEmployment employment :
                    communityService.getParticipationManager().findAllEmploymentsForAgent( agent, communityService ) ) {
                employments.add( new EmploymentData( employment ) );
            }
    }

    private void initProcedures( String serverUrl, CommunityService communityService ) {
        procedures = new ArrayList<ProcedureData>();
        CommunityCommitments allCommitments = communityService.getAllCommitments( true );   // include commitments to self
        Set<CommunityAssignment> assignments = new HashSet<CommunityAssignment>();
        for ( Agent agent : agents ) {
            for ( CommunityAssignment assignment : getAgentAssignments( agent, communityService ) ) {
                assignments.add( assignment );
            }
        }
        for ( CommunityAssignment assignment : assignments ) {
            procedures.add( new ProcedureData(
                    serverUrl,
                    communityService,
                    assignment,
                    allCommitments.benefiting( assignment ),
                    allCommitments.committing( assignment ),
                    user ) );
        }
    }

    private void initParticipatingAgents(
            List<UserParticipation> participations,
            CommunityService communityService ) {
        participatingAgents = new ArrayList<Agent>();
        for ( UserParticipation participation : participations ) {
            Agent agent = participation.getAgent( communityService );
            if ( agent != null ) participatingAgents.add( agent );
        }

    }


    @XmlElement
    public String getDate() {
        return new SimpleDateFormat( "yyyy/MM/dd H:mm:ss z" ).format( new Date() );
    }

    @XmlElement( name = "plan" )
    public PlanIdentifierData getPlanIdentifier() {
        return planIdentifierData;
    }

    @XmlElement
    public String getDateVersioned() {
        return dateVersioned;
    }

    @XmlElement( name = "agentId" )
    public List<Long> getActorIds() {
        List<Long> agentIds = new ArrayList<Long>();
        for ( Agent agent : agents ) {
            agentIds.add( agent.getId() );
        }
        return agentIds;
    }

    private List<Agent> getAgents( List<UserParticipation> participations ) {
        return participatingAgents;
    }

    @XmlElement( name = "employment" )
    // Get given actor's or user's employments
    public List<EmploymentData> getEmployments() {
        return employments;
    }

    @XmlElement( name = "procedure" )
    public List<ProcedureData> getProcedures() {
        return procedures;
    }

    @XmlElement
    public EnvironmentData getEnvironment() {
        return environmentData;
    }

    @XmlElement
    public String getUserEmail() {
        return user.getEmail();
    }

    @XmlElement
    public String getUserFullName() {
        return user.getFullName();
    }


    private CommunityAssignments getAgentAssignments( Agent agent, CommunityService communityService ) {
        return communityService.getAllAssignments().with( agent );
    }

    public ChannelsUser getUser() {
        return user;
    }

    public List<Agent> getParticipatingAgents() {
        return participatingAgents;
    }

    public Set<ContactData> allContacts() {
        Set<ContactData> allContacts = new HashSet<ContactData>();
        for ( ProcedureData procedureData : getProcedures() ) {
            allContacts.addAll( procedureData.allContacts() );
        }
        return allContacts;
    }

    public Set<AgencyData> allEmployers() {
        Set<AgencyData> allEmployers = new HashSet<AgencyData>();
        for ( ProcedureData procedureData : getProcedures() ) {
            allEmployers.add( procedureData.employer() );
        }
        return allEmployers;
    }

 }
