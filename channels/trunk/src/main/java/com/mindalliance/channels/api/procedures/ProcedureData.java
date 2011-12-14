package com.mindalliance.channels.api.procedures;

import com.mindalliance.channels.core.model.Assignment;
import com.mindalliance.channels.core.model.Commitment;
import com.mindalliance.channels.core.model.Flow;
import com.mindalliance.channels.core.query.Commitments;
import com.mindalliance.channels.core.query.PlanService;

import javax.jws.WebMethod;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Web Service data element for a procedure of an actor according to a plan.
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 12/5/11
 * Time: 3:05 PM
 */
@XmlType( propOrder = {"triggers", "assignment"} )
public class ProcedureData {

    /**
     * An assignment of the actor for which this procedure is being marshalled.
     */
    private Assignment assignment;
    /**
     * All commitments, including to self, benefiting this assignment.
     */
    private Commitments benefitingCommitments;
    /**
     * All commitments
     */
    private Commitments committingCommitments;
    private PlanService planService;
    private List<TriggerData> triggers;

    public ProcedureData() {
        // required
    }

    public ProcedureData(
            Assignment assignment,
            Commitments benefitingCommitments,
            Commitments committingCommitments,
            PlanService planService ) {
        this.assignment = assignment;
        this.benefitingCommitments = benefitingCommitments;
        this.committingCommitments = committingCommitments;
        this.planService = planService;
    }

    @XmlElement( name = "trigger" )
    public List<TriggerData> getTriggers() {
        if ( triggers == null ) {
            triggers = new ArrayList<TriggerData>();
            // anytime
            if ( assignment.isOngoing() ) {
                triggers.add( new TriggerData( assignment, planService ) );
            }
            // event phase is trigger
            if ( assignment.isInitiatedByEventPhase() ) {
                TriggerData trigger = new TriggerData( assignment, planService );
                trigger.setEventPhase( assignment.getEventPhase() );
                triggers.add( trigger );
            }
            // information discovery (notifications to self)
            for ( Commitment triggerSelfNotification : benefitingCommitments.toSelf() ) {
                TriggerData trigger = new TriggerData( assignment, planService );
                trigger.setNotificationToSelf( triggerSelfNotification );
                triggers.add( trigger );
            }
            // triggering notifications (from others)
            for ( Commitment triggerNotification : triggeringNotifications() ) {
                TriggerData trigger = new TriggerData( assignment, planService );
                trigger.setNotification( triggerNotification );
                triggers.add( trigger );
            }
            // triggering requests
            for ( Commitment triggerRequest : triggeringRequests() ) {
                TriggerData trigger = new TriggerData( assignment, planService );
                trigger.setRequest( triggerRequest );
                triggers.add( trigger );
            }
            // triggering requests to self
            for ( Commitment triggerRequest : triggeringRequestsToSelf() ) {
                TriggerData trigger = new TriggerData( assignment, planService );
                trigger.setRequestToSelf( triggerRequest );
                triggers.add( trigger );
            }
        }
        return triggers;
    }

    private List<Commitment> triggeringNotifications() {
        List<Commitment> triggerNotifications = new ArrayList<Commitment>();
        for ( Commitment commitment : benefitingCommitments ) {
            Flow flow = commitment.getSharing();
            if ( flow.isNotification() && flow.isTriggeringToTarget() && !commitment.isToSelf() ) {
                triggerNotifications.add( commitment );
            }
        }
        return triggerNotifications;
    }

    private List<Commitment> triggeringRequests() {
        List<Commitment> triggerRequests = new ArrayList<Commitment>();
        for ( Commitment commitment : committingCommitments ) {
            Flow flow = commitment.getSharing();
            if ( flow.isAskedFor() && flow.isTriggeringToSource() && !commitment.isToSelf( ) ) {
                triggerRequests.add( commitment );
            }
        }
        return triggerRequests;
    }

    private List<Commitment> triggeringRequestsToSelf() {
         List<Commitment> triggerRequestsToSelf = new ArrayList<Commitment>(  );
         for ( Commitment commitment : committingCommitments ) {
             Flow flow = commitment.getSharing();
             if ( flow.isAskedFor() && flow.isTriggeringToSource() && commitment.isToSelf() ) {
                 triggerRequestsToSelf.add( commitment );
             }
         }
         return triggerRequestsToSelf;
     }


    @XmlElement( name = "assignment" )
    public AssignmentData getAssignment() {
        return new AssignmentData( assignment, planService, this );
    }

    @WebMethod( exclude = true )
    public Commitments getBenefitingCommitments() {
        return benefitingCommitments;
    }

    @WebMethod( exclude = true )
    public Commitments getCommittingCommitments() {
        return committingCommitments;
    }

    @WebMethod( exclude = true )
    public Set<Long> allEventIds() {
        Set<Long> ids = new HashSet<Long>();
        for ( TriggerData trigger : getTriggers() ) {
            Long eventId = trigger.getEventId();
            if ( eventId != null )
                ids.add( eventId );
        }
        ids.addAll( getAssignment().allEventIds() );
        return ids;
    }

    public Set<Long> allPhaseIds() {
        Set<Long> ids = new HashSet<Long>();
        for ( TriggerData trigger : getTriggers() ) {
            Long phaseId = trigger.getPhaseId();
            if ( phaseId != null )
                ids.add( phaseId );
        }
        ids.addAll( getAssignment().allPhaseIds() );
        return ids;
    }

    public Set<Long> allOrganizationIds() {
        Set<Long> ids = new HashSet<Long>();
        for ( TriggerData trigger : getTriggers() ) {
            ids.addAll( trigger.allOrganizationIds() );
        }
        ids.addAll( getAssignment().allOrganizationIds() );
        return ids;
    }

    public Set<Long> allActorIds() {
        Set<Long> ids = new HashSet<Long>();
        for ( TriggerData trigger : getTriggers() ) {
            ids.addAll( trigger.allActorIds() );
        }
        ids.addAll( getAssignment().allActorIds() );
        return ids;
    }

    public Set<Long> allRoleIds() {
        Set<Long> ids = new HashSet<Long>();
        for ( TriggerData trigger : getTriggers() ) {
            ids.addAll( trigger.allRoleIds() );
        }
        ids.addAll( getAssignment().allRoleIds() );
        return ids;
    }

    public Set<Long> allPlaceIds() {
        Set<Long> ids = new HashSet<Long>();
        for ( TriggerData trigger : getTriggers() ) {
            ids.addAll( trigger.allPlaceIds() );
        }
        ids.addAll( getAssignment().allPlaceIds() );
        return ids;
    }

    public Set<Long> allMediumIds() {
        Set<Long> ids = new HashSet<Long>();
        for ( TriggerData trigger : getTriggers() ) {
            ids.addAll( trigger.allMediumIds() );
        }
        ids.addAll( getAssignment().allMediumIds() );
        return ids;
    }




}
