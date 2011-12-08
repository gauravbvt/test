package com.mindalliance.channels.api.procedures;

import com.mindalliance.channels.core.model.Assignment;
import com.mindalliance.channels.core.model.Commitment;
import com.mindalliance.channels.core.model.Flow;
import com.mindalliance.channels.core.query.Commitments;
import com.mindalliance.channels.core.query.PlanService;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
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
@XmlRootElement( name = "procedures", namespace = "http://mind-alliance.com/api/isp/v1/" )
@XmlType( propOrder = {"triggers", "situation", "task"} )
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
        List<TriggerData> triggers = new ArrayList<TriggerData>();
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
        for ( Flow triggerNotification : triggeringNotifications( ) ) {
            TriggerData trigger = new TriggerData( assignment, planService );
            trigger.setNotification( triggerNotification );
            triggers.add( trigger );
        }
        // triggering requests (can be from self)
        for ( Flow triggerRequest : triggeringRequests() ) {
            TriggerData trigger = new TriggerData( assignment, planService );
            trigger.setRequest( triggerRequest );
            triggers.add( trigger );
        }
        return triggers;
    }

    private Set<Flow> triggeringNotifications( ) {
        Set<Flow> triggerNotifications = new HashSet<Flow>();
        for ( Commitment commitment : benefitingCommitments ) {
            Flow flow = commitment.getSharing();
            if ( flow.isNotification() && flow.isTriggeringToTarget() && isToSelf( commitment ) ) {
                triggerNotifications.add( flow );
            }
        }
        return triggerNotifications;
    }

    private boolean isToSelf( Commitment commitment ) {
        return commitment.getCommitter().getActor().equals( assignment.getActor() );
    }

    private Set<Flow> triggeringRequests() {
        Set<Flow> triggerRequests = new HashSet<Flow>();
        for ( Commitment commitment : committingCommitments ) {
            Flow flow = commitment.getSharing();
            if ( flow.isAskedFor() && flow.isTriggeringToSource() ) {
                triggerRequests.add( flow );
            }
        }
        return triggerRequests;
    }

    @XmlElement( name = "situation" )
    public SituationData getSituation() {
        return null;
    }

    @XmlElement( name = "task" )
    public TaskData getTask() {    // todo AssignmentData
        return null;
    }


}
