package com.mindalliance.channels.api.procedures;

import com.mindalliance.channels.core.model.Assignment;
import com.mindalliance.channels.core.model.Commitment;
import com.mindalliance.channels.core.query.Commitments;
import com.mindalliance.channels.core.query.PlanService;

import javax.jws.WebMethod;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
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
@XmlRootElement( name = "assignedTask", namespace = "http://mind-alliance.com/api/isp/v1/" )
@XmlType( propOrder = {"task", "inNotifications", "inReplies", "outNotifications", "outReplies", "discoveries", "research"} )
public class AssignmentData extends AbstractProcedureElementData {

    private ProcedureData procedureData;
    private List<NotificationData> outNotifications;
    private List<RequestData> outReplies;
    private List<NotificationData> inNotifications;
    private List<RequestData> inReplies;

    public AssignmentData() {
        // required
    }

    public AssignmentData(
            Assignment assignment,
            PlanService planService,
            ProcedureData procedureData ) {
        super( assignment, planService );
        this.procedureData = procedureData;
    }

    @XmlElement
    public TaskData getTask() {
        return new TaskData( getAssignment(), getPlanService() );
    }

    @XmlElement( name = "inNotication" )
    public List<NotificationData> getInNotifications() {
        if ( inNotifications == null ) {
            inNotifications = new ArrayList<NotificationData>();
            for ( Commitment inNotification : inNotifications() ) {
                boolean benefiting = true;
                inNotifications.add( new NotificationData(
                        inNotification.getSharing(),
                        benefiting,
                        getAssignment(),
                        getPlanService() ) );
            }
        }
        return inNotifications;
    }

    @XmlElement( name = "inReply" )
    public List<RequestData> getInReplies() {
        if ( inReplies == null ) {
            inReplies = new ArrayList<RequestData>();
            for ( Commitment inReply : inReplies() ) {
                boolean benefiting = true;
                inReplies.add( new RequestData(
                        inReply.getSharing(),
                        benefiting,
                        getAssignment(),
                        getPlanService() ) );
            }
        }
        return inReplies;
    }

    @XmlElement( name = "outNotication" )
    public List<NotificationData> getOutNotifications() {
        if ( outNotifications == null ) {
            outNotifications = new ArrayList<NotificationData>();
            for ( Commitment outNotification : outNotifications() ) {
                boolean benefiting = false;
                outNotifications.add( new NotificationData(
                        outNotification.getSharing(),
                        benefiting,
                        getAssignment(),
                        getPlanService() ) );
            }
        }
        return outNotifications;
    }

    @XmlElement( name = "outReply" )
    public List<RequestData> getOutReplies() {
        if ( outReplies == null ) {
            outReplies = new ArrayList<RequestData>();
            for ( Commitment outReply : outReplies() ) {
                boolean benefiting = false;
                outReplies.add( new RequestData(
                        outReply.getSharing(),
                        benefiting,
                        getAssignment(),
                        getPlanService() ) );
            }
        }
        return outReplies;
    }


    @XmlElement( name = "discovery" )
    public List<DiscoveryData> getDiscoveries() {
        List<DiscoveryData> discoveries = new ArrayList<DiscoveryData>();
        for ( Commitment discovery : discoveries() ) {
            discoveries.add( new DiscoveryData( discovery, getPlanService() ) );
        }
        return discoveries;
    }

    @XmlElement( name = "research" )
    public List<ResearchData> getResearch() {
        List<ResearchData> allResearch = new ArrayList<ResearchData>();
        for ( Commitment research : research() ) {
            allResearch.add( new ResearchData( research, getAssignment(), getPlanService() ) );
        }
        return allResearch;
    }

    
    @WebMethod( exclude = true )
    public Set<Long> allEventIds() {
        Set<Long> ids = new HashSet<Long>();
        for ( NotificationData notificationData : getOutNotifications() ) {
            ids.add( notificationData.getConsumingTask().getEventId() );
        }
        for ( RequestData requestData : getOutReplies() ) {
            ids.add( requestData.getConsumingTask().getEventId() );
        }
        return ids;
    }

    @WebMethod( exclude = true )
    public Set<Long> allPhaseIds() {
        Set<Long> ids = new HashSet<Long>();
        for ( NotificationData notificationData : getOutNotifications() ) {
            ids.add( notificationData.getConsumingTask().getPhaseId() );
        }
        for ( RequestData requestData : getOutReplies() ) {
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
        return ids;
    }

    public Set<Long> allMediumIds() {
         Set<Long> ids = new HashSet<Long>();
         for ( AbstractFlowData flowData : getCommunications() ) {
             ids.addAll( flowData.getMediumIds() );
         }
         return ids;
     }

    private List<AbstractFlowData> getCommunications() {
        List<AbstractFlowData> communications = new ArrayList<AbstractFlowData>(  );
        communications.addAll(  getInNotifications() );
        communications.addAll( getInReplies() );
        communications.addAll(  getOutNotifications() );
        communications.addAll( getOutReplies() );
        return communications;
    }


    private Commitments inNotifications() {
        return procedureData.getBenefitingCommitments().notifications().notTo( getAssignment().getActor() );
    }

    private Commitments inReplies() {
        return procedureData.getBenefitingCommitments().requests().notTo( getAssignment().getActor() );
    }

    private Commitments outNotifications() {
        return procedureData.getCommittingCommitments().notifications().notTo( getAssignment().getActor() );
    }

    private Commitments outReplies() {
        return procedureData.getCommittingCommitments().requests().notTo( getAssignment().getActor() );
    }

    private Commitments discoveries() {
        return procedureData.getCommittingCommitments().notifications().to( getAssignment().getActor() );
    }

    private Commitments research() {
        return procedureData.getBenefitingCommitments().requests()
                .to( getAssignment().getActor() )
                .from( getAssignment().getActor() );
    }

    
    
}
