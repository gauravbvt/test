package com.mindalliance.channels.api.procedures;

import com.mindalliance.channels.api.community.AgentData;
import com.mindalliance.channels.api.directory.ContactData;
import com.mindalliance.channels.core.community.Agent;
import com.mindalliance.channels.core.community.CommunityService;
import com.mindalliance.channels.core.community.protocols.CommunityAssignment;
import com.mindalliance.channels.core.community.protocols.CommunityCommitment;
import com.mindalliance.channels.core.community.protocols.CommunityCommitments;
import com.mindalliance.channels.core.dao.user.ChannelsUser;
import com.mindalliance.channels.core.model.Event;
import com.mindalliance.channels.core.model.Flow;
import com.mindalliance.channels.core.model.Place;

import javax.jws.WebMethod;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Web service data element for a task assignment.
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 12/8/11
 * Time: 3:57 PM
 */
@XmlType( propOrder = {"task", "supervisor", "teammates"} )
// todo - ASSIGNMENT location
public class AssignmentData extends AbstractProcedureElementData {

    private AgentData supervisor;
    private List<AgentData> teammates;
    private Set<ContactData> teamContacts;
    private TaskData taskData;
    private CommunityCommitments benefitingCommitments;
    private CommunityCommitments committingCommitments;

    public AssignmentData() {
        // required
    }

    public AssignmentData(
            String serverUrl,
            CommunityAssignment assignment,
            CommunityCommitments benefitingCommitments,
            CommunityCommitments committingCommitments,
            CommunityService communityService,
            ChannelsUser user ) {
        super( communityService, assignment, user );
        this.benefitingCommitments = benefitingCommitments;
        this.committingCommitments = committingCommitments;
        initData( serverUrl, communityService );
    }


    private void initData( String serverUrl, CommunityService communityService ) {
        taskData = new TaskData( serverUrl, getAssignment(), communityService, getUser() );
        initSupervisor( serverUrl, getAssignment(), communityService );
        initTeammates( serverUrl, getAssignment(), communityService );
    }

    private void initTeammates( String serverUrl,
                                CommunityAssignment assignment,
                                CommunityService communityService ) {
        teammates = new ArrayList<AgentData>();
        teamContacts = new HashSet<ContactData>();
        if ( getAssignment().getPart().isAsTeam() ) {
            for ( CommunityAssignment teamAssignment
                    : communityService.getAllAssignments().assignedTo( assignment.getPart() ) ) {
                if ( !teamAssignment.equals( assignment ) ) {
                    teammates.add( new AgentData( serverUrl, teamAssignment.getAgent(), communityService ) );
                    for ( ContactData contactData :
                            ContactData.findContactsFromEmployment(
                                    serverUrl,
                                    teamAssignment.getCommunityEmployment(),
                                    communityService ) ) {
                        teamContacts.add( contactData );
                    }
                }
            }
        }
    }

    private void initSupervisor( String serverUrl, CommunityAssignment assignment, CommunityService communityService ) {
        Agent supervisorAgent = assignment.getSupervisor();
        if ( supervisorAgent != null ) {
            supervisor = new AgentData( serverUrl, supervisorAgent, communityService );
        }
    }

    @XmlElement
    public TaskData getTask() {
        return taskData;
    }

    @WebMethod( exclude = true )
    public Set<Long> allEventIds() { // todo - why never called (others as well)?
        Set<Long> ids = new HashSet<Long>();
        Event initiatedEvent = getAssignment().getPart().getInitiatedEvent();
        if ( initiatedEvent != null )
            ids.add( initiatedEvent.getId() );
        return ids;
    }


    public Set<Long> allActorIds() {
        Set<Long> ids = new HashSet<Long>();
        ids.add( getAssignment().getAgent().getActorId() );
        if ( getAssignment().getSupervisor() != null )
            ids.add( getAssignment().getSupervisor().getActorId() );
        return ids;
    }


    public Set<Long> allPlaceIds() {
        Set<Long> ids = new HashSet<Long>();
        if ( getAssignment().getJurisdiction() != null )
            ids.add( getAssignment().getJurisdiction().getId() );
        Event initiatedEvent = getAssignment().getPart().getInitiatedEvent();
        if ( initiatedEvent != null ) {
            Place scope = initiatedEvent.getScope();
            if ( scope != null ) {
                ids.add( scope.getId() );
            }
            Place placeBasis = initiatedEvent.getPlaceBasis();
            if ( placeBasis != null ) {
                ids.add( placeBasis.getId() );
            }
        }

        return ids;
    }

    public Set<Long> allFunctionIds() {
        Set<Long> ids = new HashSet<Long>();
        ids.addAll( getTask().allFunctionIds() );
        return ids;
    }

    public Set<Long> allInfoProductIds() {
        Set<Long> ids = new HashSet<Long>();
        ids.addAll( getTask().allInfoProductIds() );
        return ids;
    }


    private List<Flow> asSortedFlows( Set<Flow> flows ) {
        List<Flow> sortedFlows = new ArrayList<Flow>( flows );
        Collections.sort( sortedFlows, new Comparator<Flow>() {
            @Override
            public int compare( Flow f1, Flow f2 ) {
                return f1.getName().compareTo( f2.getName() );
            }
        } );
        return sortedFlows;
    }

    public String getLabel() {
        StringBuilder sb = new StringBuilder();
        sb.append( getAssignment().getPart().getTask() );
        sb.append( "\" as " );
        sb.append( getAssignment().getCommunityEmployment().getLabel() );
        return sb.toString();
    }


    public String getTitle() {
        return getAssignment().getCommunityEmployment().getTitle();
    }

    public String getAgencyLabel() {
        return getAssignment().getCommunityEmployment().getEmployer().getName();
    }

    @XmlElement
    public AgentData getSupervisor() {
        return supervisor;
    }

    @XmlElement( name = "teammate" )
    public List<AgentData> getTeammates() {
        return teammates;
    }

    public Set<ContactData> allContacts() {
        Set<ContactData> allContacts = new HashSet<ContactData>();
        allContacts.addAll( teamContacts );
        return allContacts;
    }

    private CommunityCommitments getBenefitingCommitments() {
        return benefitingCommitments;
    }

    private CommunityCommitments getCommittingCommitments() {
        return committingCommitments;
    }

    private List<Flow> inNotifications() {
        Set<Flow> inNotificationFlows = new HashSet<Flow>();
        for ( CommunityCommitment commitment : getBenefitingCommitments()
                .notifications()
                .notTriggeringToTarget()
                .notFrom( getAssignment()
                        .getAgent() )
                ) {
            inNotificationFlows.add( commitment.getSharing() );
        }
        return asSortedFlows( inNotificationFlows );
    }

    private List<Flow> outNotifications() {
        Set<Flow> outNotificationFlows = new HashSet<Flow>();
        for ( CommunityCommitment commitment : getCommittingCommitments()
                .notifications()
                .notTo( getAssignment()
                        .getAgent() ) ) {
            outNotificationFlows.add( commitment.getSharing() );
        }
        return asSortedFlows( outNotificationFlows );
    }

    private List<Flow> outRequests() {    // same as inReplies
        Set<Flow> outRequestFlows = new HashSet<Flow>();
        for ( CommunityCommitment commitment : getBenefitingCommitments()
                .requests()
                .notFrom( getAssignment()
                        .getAgent() ) ) {
            outRequestFlows.add( commitment.getSharing() );
        }
        return asSortedFlows( outRequestFlows );
    }

    private List<Flow> inRequests() {   // same meaning as out replies
        Set<Flow> inRequestFlows = new HashSet<Flow>();
        for ( CommunityCommitment commitment : getCommittingCommitments()
                .requests()
                .notTriggeringToSource()
                .notTo( getAssignment()
                        .getAgent() ) ) {
            inRequestFlows.add( commitment.getSharing() );
        }
        return asSortedFlows( inRequestFlows );
    }

    public boolean hasReceives() {
        return !inNotifications().isEmpty() || !inRequests().isEmpty();
    }

    public boolean hasSends() {
        return !outNotifications().isEmpty() || !outRequests().isEmpty();
    }


}
