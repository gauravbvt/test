package com.mindalliance.channels.api.procedures;

import com.mindalliance.channels.api.directory.ContactData;
import com.mindalliance.channels.core.dao.user.ChannelsUser;
import com.mindalliance.channels.core.model.Assignment;
import com.mindalliance.channels.core.model.Commitment;
import com.mindalliance.channels.core.model.Event;
import com.mindalliance.channels.core.model.Flow;
import com.mindalliance.channels.core.model.Place;
import com.mindalliance.channels.core.participation.PlanParticipationService;
import com.mindalliance.channels.core.query.PlanService;

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
@XmlType( propOrder = {"task", "inNotifications", "inRequests", "outNotifications", "outRequests", "discoveries", "research"} )
public class AssignmentData extends AbstractProcedureElementData {

    private ProcedureData procedureData;
    private List<NotificationData> outNotifications;
    private List<RequestData> outRequests;
    private List<NotificationData> inNotifications;
    private List<RequestData> inRequests;
    private TaskData taskData;
    private List<DiscoveryData> discoveries;
    private List<ResearchData> allResearch;

    public AssignmentData() {
        // required
    }

    public AssignmentData(
            String serverUrl,
            Assignment assignment,
            PlanService planService,
            PlanParticipationService planParticipationService,
            ChannelsUser user,
            ProcedureData procedureData ) {
        super( assignment, planService, planParticipationService, user );
        this.procedureData = procedureData;
        initData( serverUrl, planService, planParticipationService );
    }

    private void initData( String serverUrl, PlanService planService, PlanParticipationService planParticipationService ) {
        taskData = new TaskData( serverUrl, getAssignment(), planService, planParticipationService, getUser() );
        initInNotifications(  serverUrl, planService, planParticipationService );
        initOutNotifications(  serverUrl, planService, planParticipationService );
        initInRequests( serverUrl, planService, planParticipationService );
        initOutRequests( serverUrl, planService, planParticipationService );
        initDiscoveries(  serverUrl, planService, planParticipationService );
        initAllResearch(  serverUrl, planService, planParticipationService );
    }

    private void initAllResearch( String serverUrl, PlanService planService, PlanParticipationService planParticipationService ) {
        allResearch = new ArrayList<ResearchData>();
        for ( Flow researchFlow : research() ) {
            allResearch.add( new ResearchData(
                    serverUrl,
                    researchFlow,
                    getAssignment(),
                    planService,
                    planParticipationService,
                    getUser() ) );
        }

    }

    private void initDiscoveries( String serverUrl, PlanService planService, PlanParticipationService planParticipationService ) {
        discoveries = new ArrayList<DiscoveryData>();
        for ( Flow discoveringFlow : discoveries() ) {
            discoveries.add( new DiscoveryData(
                    serverUrl,
                    discoveringFlow,
                    planService,
                    planParticipationService,
                    getUser() ) );
        }

    }

    private void initOutRequests( String serverUrl, PlanService planService, PlanParticipationService planParticipationService ) {
            outRequests = new ArrayList<RequestData>();
            for ( Flow outRequestFlow : outRequests() ) {
                boolean initiating = true;
                outRequests.add( new RequestData(
                        serverUrl,
                        outRequestFlow,
                        initiating,
                        getAssignment(),
                        planService,
                        planParticipationService,
                        getUser() ) );
            }
    }

    private void initInRequests( String serverUrl, PlanService planService, PlanParticipationService planParticipationService ) {
            inRequests = new ArrayList<RequestData>();
            for ( Flow inRequestFlow : inRequests() ) {
                boolean initiating = false;
                inRequests.add( new RequestData(
                        serverUrl,
                        inRequestFlow,
                        initiating,
                        getAssignment(),
                        planService,
                        planParticipationService,
                        getUser() ) );
            }
    }

    private void initOutNotifications( String serverUrl, PlanService planService, PlanParticipationService planParticipationService ) {
            outNotifications = new ArrayList<NotificationData>();
            for ( Flow outNotificationFlow : outNotifications() ) {
                boolean initiating = true;
                outNotifications.add( new NotificationData(
                        serverUrl,
                        outNotificationFlow,
                        initiating,
                        getAssignment(),
                        planService,
                        planParticipationService,
                        getUser() ) );
            }
    }

    private void initInNotifications( String serverUrl, PlanService planService, PlanParticipationService planParticipationService ) {
        inNotifications = new ArrayList<NotificationData>();
        for ( Flow inNotificationFlow : inNotifications() ) {
            boolean initiating = false;
            inNotifications.add( new NotificationData(
                    serverUrl,
                    inNotificationFlow,
                    initiating,
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
        return inRequests;
    }

    @XmlElement( name = "outRequest" )
    public List<RequestData> getOutRequests() {
        return outRequests;
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
            if ( requestData.getConsumingTask() != null )
                ids.add(  requestData.getConsumingTask().getEventId() );
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
            if (requestData.getConsumingTask() != null )
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


    public Set<Long> allInfoProductIds() {
        Set<Long> ids = new HashSet<Long>();
        for ( AbstractFlowData flowData : getCommunications() ) {
            ids.addAll( flowData.getInfoProductIds() );
        }
        return ids;
    }

    public Set<Long> allInfoFormatIds() {
        Set<Long> ids = new HashSet<Long>();
        for ( AbstractFlowData flowData : getCommunications() ) {
            ids.addAll( flowData.getInfoFormatIds() );
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
        Set<Flow> inNotificationFlows = new HashSet<Flow>();
        for ( Commitment commitment : procedureData
                .getBenefitingCommitments()
                .notifications()
                .notTriggeringToTarget()
                .notFrom( getAssignment()
                        .getActor() )
                ) {
            inNotificationFlows.add( commitment.getSharing() );
        }
        return asSortedFlows( inNotificationFlows );
    }

    private List<Flow> outNotifications() {
        Set<Flow> outNotificationFlows = new HashSet<Flow>();
        for ( Commitment commitment : procedureData
                .getCommittingCommitments()
                .notifications()
                .notTo( getAssignment()
                        .getActor() ) ) {
            outNotificationFlows.add( commitment.getSharing() );
        }
        return asSortedFlows( outNotificationFlows );
    }

    private List<Flow> outRequests() {    // same as inReplies
        Set<Flow> outRequestFlows = new HashSet<Flow>();
        for ( Commitment commitment : procedureData
                .getBenefitingCommitments()
                .requests()
                .notFrom( getAssignment()
                        .getActor() ) ) {
            outRequestFlows.add( commitment.getSharing() );
        }
        return asSortedFlows( outRequestFlows );
    }

    private List<Flow> inRequests() {   // same meaning as out replies
        Set<Flow> inRequestFlows = new HashSet<Flow>();
        for ( Commitment commitment : procedureData
                .getCommittingCommitments()
                .requests()
                .notTriggeringToSource()
                .notTo( getAssignment()
                        .getActor() ) ) {
            inRequestFlows.add( commitment.getSharing() );
        }
        return asSortedFlows( inRequestFlows );
    }

    private List<Flow> discoveries() {
        Set<Flow> discoveringFlows = new HashSet<Flow>();
        for ( Commitment commitment : procedureData
                .getCommittingCommitments()
                .notifications()
                .to( getAssignment()
                        .getActor() ) ) {
            discoveringFlows.add( commitment.getSharing() );
        }
        return asSortedFlows( discoveringFlows );
    }

    private List<Flow> research() {
        Set<Flow> researchFlows = new HashSet<Flow>();
        for ( Commitment commitment : procedureData.getBenefitingCommitments()
                .requests()
                .to( getAssignment().getActor() )
                .from( getAssignment().getActor() ) ) {
            researchFlows.add( commitment.getSharing() );
        }
        return asSortedFlows( researchFlows );
    }

    private List<Flow> asSortedFlows( Set<Flow> flows ) {
        List<Flow> sortedFlows = new ArrayList<Flow>( flows );
        Collections.sort( sortedFlows, new Comparator<Flow>() {
            @Override
            public int compare( Flow f1, Flow f2 ) {
                return f1.getName().compareTo( f2.getName() );
            }
        });
        return sortedFlows;
    }

    public String getLabel() {
        StringBuilder sb = new StringBuilder(  );
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


    public String getTitleOrRole() {
        return getAssignment().getEmployment().getTitleOrRole();
    }

    public String getOrganizationLabel() {
        return getAssignment().getEmployment().getOrganization().getName();
    }


    public Set<ContactData> allContacts() {
        Set<ContactData> allContacts = new HashSet<ContactData>(  );
        for ( NotificationData notificationData : getOutNotifications() ) {
            for (ContactData contact : notificationData.getContacts() ) {
                allContacts.add( contact );
                allContacts.addAll(  contact.getSupervisorContacts() );
            }
        }
        for ( RequestData requestData : getOutRequests() ) {
            for (ContactData contact : requestData.getContacts() ) {
                allContacts.add( contact );
                allContacts.addAll(  contact.getSupervisorContacts() );
            }
        }
        // Add bypass contacts not yet added as direct contacts
        for ( NotificationData notificationData : getOutNotifications() ) {
            for (ContactData contact : notificationData.getContacts() ) {
                allContacts.addAll( contact.getBypassContacts() );
            }
        }
        for ( RequestData requestData : getOutRequests() ) {
            for (ContactData contact : requestData.getContacts() ) {
                allContacts.addAll( contact.getBypassContacts() );
            }
        }
        return allContacts;
    }

}
