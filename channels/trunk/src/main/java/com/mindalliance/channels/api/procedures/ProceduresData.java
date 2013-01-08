package com.mindalliance.channels.api.procedures;

import com.mindalliance.channels.api.directory.ContactData;
import com.mindalliance.channels.api.entities.EmploymentData;
import com.mindalliance.channels.api.plan.PlanIdentifierData;
import com.mindalliance.channels.core.community.PlanCommunity;
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
 * Web Service data element for the procedures of an actor according to a plan.
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 12/5/11
 * Time: 12:25 PM
 */
@XmlRootElement( name = "procedures", namespace = "http://mind-alliance.com/api/isp/v1/" )
@XmlType( propOrder = {"date", "planIdentifier", "userEmail", "dateVersioned", "actorIds", "employments", "procedures", "environment"} )
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
            PlanCommunity planCommunity,
            List<UserParticipation> participations,
            ChannelsUser user ) {
        this.user = user;
        initData( serverUrl, participations, planCommunity );
    }

    public ProceduresData(
            String serverUrl,
            PlanCommunity planCommunity,
            Agent agent ) {
        this.agents = new ArrayList<Agent>();
        agents.add( agent );
        initData( serverUrl, planCommunity );
    }


    private void initData(
            String serverUrl,
            List<UserParticipation> participations,
            PlanCommunity planCommunity ) {
        initParticipatingAgents( participations, planCommunity );
        this.agents = getAgents( participations );
        initData( serverUrl, planCommunity );
    }

    private void initData( String serverUrl, PlanCommunity planCommunity ) {
        initProcedures( serverUrl, planCommunity );
        initEmployments( planCommunity );
        environmentData = new EnvironmentData( serverUrl, this, planCommunity );
        planIdentifierData = new PlanIdentifierData( planCommunity );
        dateVersioned = new SimpleDateFormat( "yyyy/MM/dd H:mm:ss z" )
                .format( planCommunity.getPlan().getWhenVersioned() );
    }

    private void initEmployments( PlanCommunity planCommunity ) {
        employments = new ArrayList<EmploymentData>();
        for ( Agent agent : agents )
            for ( CommunityEmployment employment :
                    planCommunity.getParticipationManager().findAllEmploymentsForAgent( agent, planCommunity ) ) {
                employments.add( new EmploymentData( employment ) );
            }
    }

    private void initProcedures( String serverUrl, PlanCommunity planCommunity ) {
        procedures = new ArrayList<ProcedureData>();
        CommunityCommitments allCommitments = planCommunity.getAllCommitments( true );   // include commitments to self
        Set<CommunityAssignment> assignments = new HashSet<CommunityAssignment>();
        for ( Agent agent : agents ) {
            for ( CommunityAssignment assignment : getAgentAssignments( agent, planCommunity ) ) {
                assignments.add( assignment );
            }
        }
        for ( CommunityAssignment assignment : assignments ) {
            procedures.add( new ProcedureData(
                    serverUrl,
                    planCommunity,
                    assignment,
                    allCommitments.benefiting( assignment ),
                    allCommitments.committing( assignment ),
                    user ) );
        }
    }

    private void initParticipatingAgents(
            List<UserParticipation> participations,
            PlanCommunity planCommunity ) {
        participatingAgents = new ArrayList<Agent>();
        for ( UserParticipation participation : participations ) {
            Agent agent = participation.getAgent( planCommunity );
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

    private CommunityAssignments getAgentAssignments( Agent agent, PlanCommunity planCommunity ) {
        return planCommunity.getAllAssignments().with( agent );
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
