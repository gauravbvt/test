package com.mindalliance.channels.api.procedures;

import com.mindalliance.channels.core.dao.user.ChannelsUser;
import com.mindalliance.channels.core.dao.user.PlanParticipationService;
import com.mindalliance.channels.core.model.Assignment;
import com.mindalliance.channels.core.model.Commitment;
import com.mindalliance.channels.core.model.Event;
import com.mindalliance.channels.core.model.Flow;
import com.mindalliance.channels.core.model.Place;
import com.mindalliance.channels.core.query.PlanService;

import javax.jws.WebMethod;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.util.ArrayList;
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
@XmlType( propOrder = {"task", "inNotifications", "inRequests", "outNotifications", "outRequests", "discoveries", "research"} )
public class AssignmentData extends AbstractProcedureElementData {

    private ProcedureData procedureData;
    private List<NotificationData> outNotifications;
    private List<RequestData> outReplies;
    private List<NotificationData> inNotifications;
    private List<RequestData> inReplies;
    private TaskData taskData;
    private List<DiscoveryData> discoveries;
    private List<ResearchData> allResearch;

    public AssignmentData() {
        // required
    }

    public AssignmentData(
            Assignment assignment,
            PlanService planService,
            PlanParticipationService planParticipationService,
            ChannelsUser user,
            ProcedureData procedureData ) {
        super( assignment, planService, planParticipationService, user );
        this.procedureData = procedureData;
        initData( planService, planParticipationService );
    }

    private void initData( PlanService planService, PlanParticipationService planParticipationService ) {
        // todo
        taskData = new TaskData( getAssignment(), planService, planParticipationService, getUser() );
        initInNotifications( planService, planParticipationService );
        initOutNotifications( planService, planParticipationService );
        initInReplies( planService, planParticipationService );
        initOutReplies( planService, planParticipationService );
        initDiscoveries( planService, planParticipationService );
        initAllResearch( planService, planParticipationService );
    }

    private void initAllResearch( PlanService planService, PlanParticipationService planParticipationService ) {
        allResearch = new ArrayList<ResearchData>();
        for ( Flow research : research() ) {
            allResearch.add( new ResearchData(
                    research,
                    getAssignment(),
                    planService,
                    planParticipationService,
                    getUser() ) );
        }

    }

    private void initDiscoveries( PlanService planService, PlanParticipationService planParticipationService ) {
        discoveries = new ArrayList<DiscoveryData>();
        for ( Flow discovery : discoveries() ) {
            discoveries.add( new DiscoveryData(
                    discovery,
                    planService,
                    planParticipationService,
                    getUser() ) );
        }

    }

    private void initOutReplies( PlanService planService, PlanParticipationService planParticipationService ) {
            outReplies = new ArrayList<RequestData>();
            for ( Flow flow : outRequests() ) {
                boolean replying = false;
                outReplies.add( new RequestData(
                        flow,
                        replying,
                        getAssignment(),
                        planService,
                        planParticipationService,
                        getUser() ) );
            }
    }

    private void initInReplies( PlanService planService, PlanParticipationService planParticipationService ) {
            inReplies = new ArrayList<RequestData>();
            for ( Flow flow : inRequests() ) {
                boolean replying = true;
                inReplies.add( new RequestData(
                        flow,
                        replying,
                        getAssignment(),
                        planService,
                        planParticipationService,
                        getUser() ) );
            }
    }

    private void initOutNotifications( PlanService planService, PlanParticipationService planParticipationService ) {
            outNotifications = new ArrayList<NotificationData>();
            for ( Flow outNotification : outNotifications() ) {
                boolean benefiting = false;
                outNotifications.add( new NotificationData(
                        outNotification,
                        benefiting,
                        getAssignment(),
                        planService,
                        planParticipationService,
                        getUser() ) );
            }
    }

    private void initInNotifications( PlanService planService, PlanParticipationService planParticipationService ) {
        inNotifications = new ArrayList<NotificationData>();
        for ( Flow inNotification : inNotifications() ) {
            boolean benefiting = true;
            inNotifications.add( new NotificationData(
                    inNotification,
                    benefiting,
                    getAssignment(),
                    planService,
                    planParticipationService,
                    getUser() ) );
        }
    }

    @XmlElement
    public TaskData getTask() {
        return taskData;
    }

    @XmlElement( name = "inNotification" )
    public List<NotificationData> getInNotifications() {
        return inNotifications;
    }

    @XmlElement( name = "outNotification" )
    public List<NotificationData> getOutNotifications() {
        return outNotifications;
    }

    @XmlElement( name = "inRequest" )
    public List<RequestData> getInRequests() {
        return inReplies;
    }

    @XmlElement( name = "outRequest" )
    public List<RequestData> getOutRequests() {
        return outReplies;
    }


    @XmlElement( name = "discovery" )
    public List<DiscoveryData> getDiscoveries() {
        return discoveries;
    }

    @XmlElement( name = "research" )
    public List<ResearchData> getResearch() {
        return allResearch;
    }


    @WebMethod( exclude = true )
    public Set<Long> allEventIds() {
        Set<Long> ids = new HashSet<Long>();
        for ( NotificationData notificationData : getOutNotifications() ) {
            ids.add( notificationData.getConsumingTask().getEventId() );
        }
        for ( RequestData requestData : getInRequests() ) {
            ids.add( requestData.getConsumingTask().getEventId() );
        }
        Event initiatedEvent = getAssignment().getPart().getInitiatedEvent();
        if ( initiatedEvent != null )
            ids.add( initiatedEvent.getId() );
        return ids;
    }

    @WebMethod( exclude = true )
    public Set<Long> allPhaseIds() {
        Set<Long> ids = new HashSet<Long>();
        for ( NotificationData notificationData : getOutNotifications() ) {
            ids.add( notificationData.getConsumingTask().getPhaseId() );
        }
        for ( RequestData requestData : getInRequests() ) {
            ids.add( requestData.getConsumingTask().getPhaseId() );
        }
        return ids;
    }

    public Set<Long> allOrganizationIds() {
        Set<Long> ids = new HashSet<Long>();
        ids.add( getAssignment().getOrganization().getId() );
        for ( AbstractFlowData flowData : getCommunications() ) {
            ids.addAll( flowData.allOrganizationIds() );
        }
        return ids;
    }

    public Set<Long> allActorIds() {
        Set<Long> ids = new HashSet<Long>();
        ids.add( getAssignment().getActor().getId() );
        if ( getAssignment().getSupervisor() != null )
            ids.add( getAssignment().getSupervisor().getId() );
        for ( AbstractFlowData flowData : getCommunications() ) {
            ids.addAll( flowData.allActorIds() );
        }
        return ids;
    }

    public Set<Long> allRoleIds() {
        Set<Long> ids = new HashSet<Long>();
        ids.add( getAssignment().getRole().getId() );
        for ( AbstractFlowData flowData : getCommunications() ) {
            ids.addAll( flowData.allRoleIds() );
        }
        return ids;
    }

    public Set<Long> allPlaceIds() {
        Set<Long> ids = new HashSet<Long>();
        if ( getAssignment().getJurisdiction() != null )
            ids.add( getAssignment().getJurisdiction().getId() );
        for ( AbstractFlowData flowData : getCommunications() ) {
            ids.addAll( flowData.allPlaceIds() );
        }
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

    public Set<Long> allMediumIds() {
        Set<Long> ids = new HashSet<Long>();
        for ( AbstractFlowData flowData : getCommunications() ) {
            ids.addAll( flowData.getMediumIds() );
        }
        return ids;
    }

    public List<AbstractFlowData> getCommunications() {
        List<AbstractFlowData> communications = new ArrayList<AbstractFlowData>();
        communications.addAll( getInNotifications() );
        communications.addAll( getInRequests() );
        communications.addAll( getOutNotifications() );
        communications.addAll( getOutRequests() );
        return communications;
    }


    private List<Flow> inNotifications() {
        Set<Flow> flows = new HashSet<Flow>();
        for ( Commitment commitment : procedureData
                .getBenefitingCommitments()
                .notifications()
                .notTo( getAssignment()
                        .getActor() ) ) {
            flows.add( commitment.getSharing() );
        }
        return new ArrayList<Flow>( flows );
    }

    private List<Flow> outNotifications() {
        Set<Flow> flows = new HashSet<Flow>();
        for ( Commitment commitment : procedureData
                .getCommittingCommitments()
                .notifications()
                .notTo( getAssignment()
                        .getActor() ) ) {
            flows.add( commitment.getSharing() );
        }
        return new ArrayList<Flow>( flows );
    }

    private List<Flow> outRequests() {
        Set<Flow> flows = new HashSet<Flow>();
        for ( Commitment commitment : procedureData
                .getBenefitingCommitments()
                .requests()
                .notFrom( getAssignment()
                        .getActor() ) ) {
            flows.add( commitment.getSharing() );
        }
        return new ArrayList<Flow>( flows );
    }

    private List<Flow> inRequests() {
        Set<Flow> flows = new HashSet<Flow>();
        for ( Commitment commitment : procedureData.getCommittingCommitments()
                .requests()
                .notTo( getAssignment()
                        .getActor() ) ) {
            flows.add( commitment.getSharing() );
        }
        return new ArrayList<Flow>( flows );
    }

    private List<Flow> discoveries() {
        Set<Flow> flows = new HashSet<Flow>();
        for ( Commitment commitment : procedureData
                .getCommittingCommitments()
                .notifications()
                .to( getAssignment()
                        .getActor() ) ) {
            flows.add( commitment.getSharing() );
        }
        return new ArrayList<Flow>( flows );
    }

    private List<Flow> research() {
        Set<Flow> flows = new HashSet<Flow>();
        for ( Commitment commitment : procedureData.getBenefitingCommitments()
                .requests()
                .to( getAssignment().getActor() )
                .from( getAssignment().getActor() ) ) {
            flows.add( commitment.getSharing() );
        }
        return new ArrayList<Flow>( flows );
    }


    public String getLabel() {
        StringBuilder sb = new StringBuilder(  );
        if ( getAssignment().isOngoing() ) {
            sb.append( "You are constantly doing task \"" );
        } else {
            sb.append( "Do task \"" );
        }
        sb.append( getAssignment().getPart().getTask() );
        sb.append( "\" as " );
        sb.append( getAssignment().getEmployment().getLabel() );
        return sb.toString();
    }

    public boolean hasReceives() {
        return !inNotifications().isEmpty() || !inRequests().isEmpty();
    }

    public boolean hasSends() {
        return !outNotifications().isEmpty() || !outRequests().isEmpty();
    }

}
