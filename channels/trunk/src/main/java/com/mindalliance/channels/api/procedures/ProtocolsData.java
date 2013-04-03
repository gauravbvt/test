package com.mindalliance.channels.api.procedures;

import com.mindalliance.channels.api.directory.ContactData;
import com.mindalliance.channels.api.entities.EmploymentData;
import com.mindalliance.channels.api.plan.PlanIdentifierData;
import com.mindalliance.channels.api.procedures.checklist.ChecklistData;
import com.mindalliance.channels.core.community.CommunityService;
import com.mindalliance.channels.core.community.participation.Agent;
import com.mindalliance.channels.core.community.participation.UserParticipation;
import com.mindalliance.channels.core.community.protocols.CommunityAssignment;
import com.mindalliance.channels.core.community.protocols.CommunityAssignments;
import com.mindalliance.channels.core.community.protocols.CommunityCommitment;
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
 * Web Service data element for the protocols of an agent or user according to a plan.
 * Copyright (C) 2008-2013 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 4/1/13
 * Time: 2:45 PM
 */
@XmlRootElement( name = "protocols", namespace = "http://mind-alliance.com/api/isp/v1/" )
@XmlType( propOrder = {"date", "planIdentifier", "userEmail", "userFullName", "dateVersioned", "actorIds",
        "employments", "checklists", "expectedRequests", "environment"} )
public class ProtocolsData implements Serializable {

    private List<Agent> agents;
    private ChannelsUser user;
    private List<ChecklistData> checklists;
    private List<RequestData> expectedRequests;
    private List<EmploymentData> employments;
    private List<Agent> participatingAgents;
    private EnvironmentData environmentData;
    private PlanIdentifierData planIdentifierData;
    private String dateVersioned;

    public ProtocolsData() {
        // required
    }

    public ProtocolsData(
            String serverUrl,
            CommunityService communityService,
            List<UserParticipation> participations,
            ChannelsUser user ) {
        this.user = user;
        initData( serverUrl, participations, communityService, user );
    }

    public ProtocolsData(
            String serverUrl,
            CommunityService communityService,
            Agent agent,
            ChannelsUser user ) {
        this.agents = new ArrayList<Agent>();
        agents.add( agent );
        initData( serverUrl, communityService, user );
    }


    private void initData(
            String serverUrl,
            List<UserParticipation> participations,
            CommunityService communityService,
            ChannelsUser user ) {
        initParticipatingAgents( participations, communityService );
        this.agents = getAgents( participations );
        initData( serverUrl, communityService, user );
    }

    private void initData( String serverUrl, CommunityService communityService, ChannelsUser user ) {
        initChecklistsAndRequests( serverUrl, communityService, user );
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

    private void initChecklistsAndRequests( String serverUrl, CommunityService communityService, ChannelsUser user ) {
        checklists = new ArrayList<ChecklistData>();
        expectedRequests = new ArrayList<RequestData>(  );
        CommunityCommitments allCommitments = communityService.getAllCommitments( true );   // include commitments to self
        Set<CommunityAssignment> assignments = new HashSet<CommunityAssignment>();
        for ( Agent agent : agents ) {
            for ( CommunityAssignment assignment : getAgentAssignments( agent, communityService ) ) {
                assignments.add( assignment );
            }
        }
        for ( CommunityAssignment assignment : assignments ) {
            CommunityCommitments committingCommitments = allCommitments.committing( assignment );
            checklists.add( new ChecklistData(
                    serverUrl,
                    communityService,
                    assignment,
                    allCommitments.benefiting( assignment ),
                    committingCommitments,
                    user ) );
            for ( CommunityCommitment commitment : committingCommitments.requests() ) {
                expectedRequests.add( new RequestData(
                        serverUrl,
                        communityService,
                        commitment.getSharing(),
                        false,
                        assignment,
                        user));
            }
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

    @XmlElement( name = "checklist" )
    public List<ChecklistData> getChecklists() {
        return checklists;
    }

    @XmlElement( name = "expectedRequest" )
    public List<RequestData> getExpectedRequests() {
        return expectedRequests;
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
        for ( ChecklistData checklistData : getChecklists() ) {
            allContacts.addAll( checklistData.allContacts() );
        }
        for ( RequestData expectedRequest : getExpectedRequests()) {
            allContacts.addAll( expectedRequest.getContacts() );
        }
        return allContacts;
    }

    public Set<AgencyData> allEmployers() {
        Set<AgencyData> allEmployers = new HashSet<AgencyData>();
        for ( ChecklistData checklistData : getChecklists() ) {
            allEmployers.add( checklistData.employer() );
        }
        return allEmployers;
    }

    public Set<Long> allEventsIds() {
        Set<Long> ids = new HashSet<Long>(  );
        for ( ChecklistData checklistData : getChecklists() ) {
            ids.addAll( checklistData.allEventIds() );
        }
        return ids;
    }

    public Set<Long> allPhaseIds() {
        Set<Long> ids = new HashSet<Long>(  );
        for ( ChecklistData checklistData : getChecklists() ) {
            ids.addAll( checklistData.allPhaseIds() );
        }
        return ids;
    }

    public Set<Long> allOrganizationIds() {
        Set<Long> ids = new HashSet<Long>(  );
        for ( RequestData requestData : getExpectedRequests() ) {
            ids.addAll( requestData.allOrganizationIds() );
        }
        for ( ChecklistData checklistData : getChecklists() ) {
            ids.addAll( checklistData.allOrganizationIds() );
        }
        return ids;
    }

    public Set<Long> allActorIds() {
        Set<Long> ids = new HashSet<Long>(  );
        for ( RequestData requestData : getExpectedRequests() ) {
            ids.addAll( requestData.allActorIds() ) ;
        }
        for ( ChecklistData checklistData : getChecklists() ) {
            ids.addAll( checklistData.allActorIds() );
        }
        return ids;
    }

    public Set<Long> allRoleIds() {
        Set<Long> ids = new HashSet<Long>(  );
        for ( RequestData requestData : getExpectedRequests() ) {
            ids.addAll( requestData.allRoleIds() );
        }
        for ( ChecklistData checklistData : getChecklists() ) {
            ids.addAll( checklistData.allRoleIds() );
        }
        return ids;
    }

    public Set<Long> allPlaceIds() {
        Set<Long> ids = new HashSet<Long>(  );
        for ( RequestData requestData : getExpectedRequests() ) {
            ids.addAll( requestData.allPlaceIds() );
        }
        for ( ChecklistData checklistData : getChecklists() ) {
            ids.addAll( checklistData.allPlaceIds() );
        }
        return ids;
    }

    public Set<Long> allMediumIds() {
        Set<Long> ids = new HashSet<Long>(  );
        for ( RequestData requestData : getExpectedRequests() ) {
            ids.addAll( requestData.getMediumIds()) ;
        }
        for ( ChecklistData checklistData : getChecklists() ) {
            ids.addAll( checklistData.allMediumIds() );
        }
        return ids;
    }

    public Set<Long> allInfoProductIds() {
        Set<Long> ids = new HashSet<Long>(  );
        for ( RequestData requestData : getExpectedRequests() ) {
            ids.addAll( requestData.getInfoProductIds() ) ;
        }
        for ( ChecklistData checklistData : getChecklists() ) {
            ids.addAll( checklistData.allInfoProductIds() );
        }
        return ids;
    }

    public Set<Long> allInfoFormatIds() {
        Set<Long> ids = new HashSet<Long>(  );
        for ( RequestData requestData : getExpectedRequests() ) {
            ids.addAll( requestData.getInfoFormatIds() ) ;
        }
        for ( ChecklistData checklistData : getChecklists() ) {
            ids.addAll( checklistData.allInfoFormatIds() );
        }
        return ids;
    }
}
