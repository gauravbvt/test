package com.mindalliance.channels.api.procedures;

import com.mindalliance.channels.core.dao.user.ChannelsUser;
import com.mindalliance.channels.core.dao.user.PlanParticipationService;
import com.mindalliance.channels.core.model.Assignment;
import com.mindalliance.channels.core.model.Commitment;
import com.mindalliance.channels.core.model.Employment;
import com.mindalliance.channels.core.model.Flow;
import com.mindalliance.channels.core.query.Commitments;
import com.mindalliance.channels.core.query.PlanService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;

import javax.jws.WebMethod;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;
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
@XmlType( propOrder = {"actorId", "triggers", "assignment"} )
public class ProcedureData implements Serializable {

    private ChannelsUser user;
    /**
     * All triggers.
     */
    private List<TriggerData> triggers;
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

    private AssignmentData assignmentData;

    public ProcedureData() {
        // required
    }

    public ProcedureData(
            Assignment assignment,
            Commitments benefitingCommitments,
            Commitments committingCommitments,
            PlanService planService,
            PlanParticipationService planParticipationService,
            ChannelsUser user ) {
        this.assignment = assignment;
        this.benefitingCommitments = benefitingCommitments;
        this.committingCommitments = committingCommitments;
        this.user = user;
        initData( planService, planParticipationService );
    }

    private void initData( PlanService planService, PlanParticipationService planParticipationService ) {
        assignmentData = new AssignmentData( assignment, planService, planParticipationService, user, this );
        initTriggers( planService, planParticipationService );
    }


    private void initTriggers( PlanService planService, PlanParticipationService planParticipationService ) {
        triggers = new ArrayList<TriggerData>();
        // anytime
        if ( assignment.isOngoing() ) {
            TriggerData triggerData = new TriggerData( assignment, planService, planParticipationService, user );
            triggerData.setOngoing( true );
            triggerData.initTrigger( planService, planParticipationService );
            triggers.add( triggerData );
        } else {
            // event phase is trigger
            if ( assignment.isInitiatedByEventPhase() ) {
                TriggerData triggerData = new TriggerData( assignment, planService, planParticipationService, user );
                triggerData.setEventPhase( assignment.getEventPhase() );
                triggerData.setEventPhaseContext( assignment.getEventPhaseContext() );
                triggerData.initTrigger( planService, planParticipationService );
                triggers.add( triggerData );
            }
            // information discovery (notifications to self)
            for ( Commitment triggerSelfNotification : triggeringNotificationsToSelf() ) {
                TriggerData triggerData = new TriggerData( assignment, planService, planParticipationService, user );
                triggerData.setCommitmentToSelf( triggerSelfNotification );
                triggerData.initTrigger( planService, planParticipationService );
                triggers.add( triggerData );
            }
            // triggering notifications (from others)
            for ( Commitment triggerNotification : triggeringNotificationsFromOthers() ) {
                TriggerData triggerData = new TriggerData( assignment, planService, planParticipationService, user );
                triggerData.setNotificationFromOther( triggerNotification );
                triggerData.initTrigger( planService, planParticipationService );
                triggers.add( triggerData );
            }
            // triggering requests
            for ( Commitment triggerRequest : triggeringRequestsFromOthers() ) {
                TriggerData triggerData = new TriggerData( assignment, planService, planParticipationService, user );
                triggerData.setRequestFromOther( triggerRequest );
                triggerData.initTrigger( planService, planParticipationService );
                triggers.add( triggerData );
            }
            // triggering requests to self
            for ( Commitment triggerRequest : triggeringRequestsToSelf() ) {
                TriggerData triggerData = new TriggerData( assignment, planService, planParticipationService, user );
                triggerData.setRequestToSelf( triggerRequest );
                triggerData.initTrigger( planService, planParticipationService );
                triggers.add( triggerData );
            }
        }
    }

    @XmlElement( name = "agentId" )
    public Long getActorId() {
        return assignment.getActor().getId();
    }


    @XmlElement( name = "trigger" )
    public List<TriggerData> getTriggers() {
        return triggers;
    }

    private List<Commitment> triggeringNotificationsFromOthers() {
        Set<Commitment> triggerNotifications = new HashSet<Commitment>();
        for ( Commitment commitment : benefitingCommitments ) {
            Flow flow = commitment.getSharing();
            if ( flow.isNotification() && flow.isTriggeringToTarget() && !commitment.isToSelf() ) {
                triggerNotifications.add( commitment );
            }
        }
        return new ArrayList<Commitment>( triggerNotifications );
    }

    private List<Commitment> triggeringRequestsFromOthers() {
        Set<Commitment> triggerRequests = new HashSet<Commitment>();
        for ( Commitment commitment : committingCommitments ) {
            Flow flow = commitment.getSharing();
            if ( flow.isAskedFor() && flow.isTriggeringToSource() && !commitment.isToSelf() ) {
                triggerRequests.add( commitment );
            }
        }
        return new ArrayList<Commitment>( triggerRequests );
    }


    private List<Commitment> triggeringNotificationsToSelf() {
        Set<Commitment> triggerNotificationsToSelf = new HashSet<Commitment>();
        for ( Commitment commitment : benefitingCommitments.toSelf() ) {
            Flow flow = commitment.getSharing();
            if ( flow.isNotification() && flow.isTriggeringToTarget() && commitment.isToSelf() ) {
                triggerNotificationsToSelf.add( commitment );
            }
        }
        return new ArrayList<Commitment>( triggerNotificationsToSelf );
    }

    private List<Commitment> triggeringRequestsToSelf() {
        Set<Commitment> triggerRequestsToSelf = new HashSet<Commitment>();
        for ( Commitment commitment : committingCommitments ) {
            Flow flow = commitment.getSharing();
            if ( flow.isAskedFor() && flow.isTriggeringToSource() && commitment.isToSelf() ) {
                triggerRequestsToSelf.add( commitment );
            }
        }
        return new ArrayList<Commitment>( triggerRequestsToSelf );
    }


    @XmlElement( name = "assignment" )
    public AssignmentData getAssignment() {
        return assignmentData;
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


    public List<Employment> getContactEmployments() {
        List<Employment> contactEmployments = new ArrayList<Employment>();
        // from trigger(s)
        for ( TriggerData triggerData : triggers ) {
            if ( triggerData.isOnNotificationFromOther() ) {
                NotificationData notificationData = triggerData.getOnNotification();
                contactEmployments.addAll( notificationData.findContactEmployments() );
                contactEmployments.addAll( notificationData.findBypassContactEmployments() );
            } else if ( triggerData.isOnRequestFromOther() ) {
                RequestData requestData = triggerData.getOnRequest();
                contactEmployments.addAll( requestData.findContactEmployments() );
                contactEmployments.addAll( requestData.findBypassContactEmployments() );
            }
        }
        // from assignment requests and notifications
        contactEmployments.addAll( getNonTriggerContactEmployments() );
        // from teammates
        contactEmployments.addAll( getAssignment().getTask().getTeamEmployments() );
        return contactEmployments;
    }

    public boolean isOngoing() {
        return CollectionUtils.exists(
                getTriggers(),
                new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        return ( (TriggerData) object ).getOngoing();
                    }
                } );
    }

    public boolean isTriggeredByDiscovery() {
        return CollectionUtils.exists(
                getTriggers(),
                new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        return ( (TriggerData) object ).getOnDiscovery() != null;
                    }
                } );
    }

    public boolean isTriggeredByResearch() {
        return CollectionUtils.exists(
                getTriggers(),
                new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        return ( (TriggerData) object ).getOnResearch() != null;
                    }
                } );
    }

    public boolean isTriggeredByObservation() {
        return CollectionUtils.exists(
                getTriggers(),
                new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        return ( (TriggerData) object ).getOnObservation() != null;
                    }
                } );
    }


    public boolean isTriggeredByCommunication() {
        return CollectionUtils.exists(
                getTriggers(),
                new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        return ( (TriggerData) object ).getOnNotification() != null
                                || ( (TriggerData) object ).getOnRequest() != null;
                    }
                } );
    }

    @SuppressWarnings( "unchecked" )
    public List<TriggerData> getObservationTriggers() {
        return (List<TriggerData>) CollectionUtils.select(
                getTriggers(),
                new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        return ( (TriggerData) object ).isOnObserving();
                    }
                }
        );

    }

    @SuppressWarnings( "unchecked" )
    public List<TriggerData> getRequestTriggers() {
        return (List<TriggerData>) CollectionUtils.select(
                getTriggers(),
                new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        return ( (TriggerData) object ).isOnRequestFromOther();
                    }
                }
        );
    }

    @SuppressWarnings( "unchecked" )
    public List<TriggerData> getNotificationTriggers() {
        return (List<TriggerData>) CollectionUtils.select(
                getTriggers(),
                new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        return ( (TriggerData) object ).isOnNotificationFromOther();
                    }
                }
        );
    }

    @SuppressWarnings( "unchecked" )
    public List<TriggerData> getDiscoveryTriggers() {
        return (List<TriggerData>) CollectionUtils.select(
                getTriggers(),
                new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        return ( (TriggerData) object ).isOnDiscovering();
                    }
                }
        );
    }

    @SuppressWarnings( "unchecked" )
    public List<TriggerData> getResearchTriggers() {
        return (List<TriggerData>) CollectionUtils.select(
                getTriggers(),
                new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        return ( (TriggerData) object ).isOnResearching();
                    }
                }
        );
    }


    public String getAnchor() {
        return getAssignment().getTask().getAnchor();
    }

    public String getLabel() {
        StringBuilder sb = new StringBuilder();
        if ( isOngoing() ) {
            sb.append( "You constantly do task \"" );
        } else if ( isTriggeredByDiscovery() ) {
            sb.append( "Follow up with task \"" );
        } else if ( isTriggeredByResearch() ) {
            sb.append( "To find what you need, do task \"" );
        } else {
            sb.append( "Do task \"" );
        }
        sb.append( getAssignment().getLabel() );
        return sb.toString();
    }

    public List<Employment> getNonTriggerContactEmployments() {
        AssignmentData assignmentData = getAssignment();
        Set<Employment> contactEmployments = new HashSet<Employment>();
        for ( AbstractFlowData flowWithOther : assignmentData.getCommunications() ) {
            contactEmployments.addAll( flowWithOther.findContactEmployments() );
            contactEmployments.addAll( flowWithOther.findBypassContactEmployments() );
        }
        return new ArrayList<Employment>( contactEmployments );
    }

    public boolean hasReceives() {
        return getAssignment().hasReceives();
    }

    public boolean hasSends() {
        return getAssignment().hasSends();
    }

    public String getTaskLabel() {
        StringBuilder sb = new StringBuilder();
        if ( isOngoing() ) {
            sb.append( "You constantly do task \"" );
        } else if ( isTriggeredByDiscovery() ) {
            sb.append( "Follow up with task \"" );
        } else if ( isTriggeredByResearch() ) {
            sb.append( "To find what you need, do task \"" );
        } else {
            sb.append( "Do task \"" );
        }
        sb.append( getAssignment().getTask().getName() );
        sb.append( "\"" );
        return sb.toString();
    }

    public String getTitleOrRole() {
        return getAssignment().getTitleOrRole();
    }

    public String getOrganizationLabel() {
        return getAssignment().getOrganizationLabel();
    }

    @Override
    public boolean equals( Object object ) {
        return object instanceof ProcedureData
                && getAssignment().equals( ( (ProcedureData) object ).getAssignment() );
    }

    @Override
    public int hashCode() {
        return getAssignment().hashCode();
    }
}
